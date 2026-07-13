
import os
import pandas as pd 
import os
from plotting import plot_util as pu

from plotting import trace_plots as p_trace
from conversions import trace_conversions as c_trace

from analysis_util import *

import socket
from common_constants import *
import filters.filters as fl
import pyarrow.parquet as pq
import filters.trace_filters as f_trace
import plotly.io as pio
import datetime
import time

# Used for plotly, whenever dash is used.
def get_free_port():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.bind(('', 0)) # Get random free port
        return sock.getsockname()[1]

def stamp(row, filters = [], separator=" | "):
    s = ""
    for i, name in enumerate(filters):
        value_entry = row[name]
        s += str(value_entry)
        if i < len(filters) - 1:
            s += separator

    return s

def stamp_dataframe(df: pd.DataFrame, stamps):
    dff = df.copy()
    dff['series'] = dff.apply(lambda r: stamp(r, stamps), axis=1)
    return dff


def save_fig_to_pdf(fig, path):

    import plotly.graph_objects as go
    import kaleido
    
    fig.write_image(path, scale=1, width=fig.layout.width, height=fig.layout.height)

def get_query_data(df_query_search: pd.DataFrame, hashes_path) -> pd.DataFrame:
    dataset = pq.ParquetDataset(hashes_path,
                                partitioning='hive',
                                filters=[
                                    ('hash_trace', 'in', df_query_search['hash_trace'].to_list()),
                                    ('hash_query', 'in', df_query_search['hash_query'].to_list())])
    table = dataset.read()
    df_data: pd.DataFrame = table.to_pandas()

    dff = df_data.merge(df_query_search, on="hash_query")
    return dff

def get_stamped_data(df_query_search, hashes_path, stamps):
    return stamp_dataframe(get_query_data(df_query_search, hashes_path), stamps)

class TraceGroupData():
    def __init__(self, tg_identifier, trace_identifiers, df_trace_view, df_model_mem, df_model_frag, df_model_points):
        self.tg_identifier = tg_identifier
        self.df_trace_view = df_trace_view
        self.df_model_mem = df_model_mem
        self.df_model_frag = df_model_frag
        self.df_model_points = df_model_points
        self.trace_identifiers = trace_identifiers
    

# Parses a trace group into different datasets, used in policy comparisons.
def process_tg(df_qs: pd.DataFrame, tg_name, group_filter: fl.Filter, common_limits, limit_thresholds, hashes_path) -> TraceGroupData:
    points_limits = c_trace.PointsSteps(limit_thresholds)
    points_WJ = c_trace.PointsLinearIdeal(100, 300)

    # Apply scaling to all fragmentation types (even ones we don't include)
    point_fns = [c_trace.FragScaling(wj, points_WJ, False) for wj in [*ExtFragConstants.all_WJ_alloc(), *ExtFragConstants.all_WJ_req(), *IntFragConstants.all_WJ()]]
    point_fns += [c_trace.FragScaling(f, points_limits, True) for f in [*ExtFragConstants.all_relative(), *ExtFragConstants.all_LFB(), IntFragConstants.relative.value]]

    df_traces = pd.DataFrame()
    df_model_mem = pd.DataFrame()
    df_model_frag = pd.DataFrame()
    df_model_points = pd.DataFrame()

    df_tg = group_filter.apply(df_qs)
    trace_names = df_tg['trace_identifier'].unique().tolist()

    def get_timestamp():
        return datetime.datetime.fromtimestamp(time.time()).strftime('%Y-%m-%d %H:%M:%S')
        
    print_times = False
    for name in trace_names:
        if print_times:
            print(name + get_timestamp())
        df_trace = f_trace.get_trace_num([name]).apply(df_tg)
        trace_overview = c_trace.create_trace_view(df_trace, name, hashes_path)
        trace_overview.insert(0, 'trace_identifier', name)
        trace_overview.insert(0, 'trace_group_identifier', tg_name)
        df_traces = pd.concat([df_traces, trace_overview])

        for model_id, df_model in df_trace.groupby('model_identifier'):
            model_type = df_model['model_variables_allocator_identifier'].unique().tolist()
            size_mult = df_model['model_config_size_multiple'].unique().tolist()
            
            # View on memory usage
            df_mem = c_trace.create_model_memory_view(df_model, hashes_path)
            df_mem.insert(0, 'model_identifier', model_id)
            df_mem.insert(0, 'trace_identifier', name)
            df_mem.insert(0, 'trace_group_identifier', tg_name)
            df_model_mem = pd.concat([df_model_mem, df_mem])
            if print_times:
                print(f"after model({model_id}) memory: " + get_timestamp())
            
            # View on fragmentation
            df_frag = c_trace.create_model_frag_view(df_model, hashes_path, common_limits, common_limits, common_limits)
            df_frag.insert(0, 'model_identifier', model_id)
            df_frag.insert(0, 'trace_identifier', name)
            df_frag.insert(0, 'trace_group_identifier', tg_name)

            # Add additional columns for easier filtering later.
            df_frag_copy = df_frag.copy()
            df_frag_copy.insert(0, 'model_config_size_multiple', size_mult)
            df_frag_copy.insert(0, 'model_variables_allocator_identifier', model_type)
            df_model_frag = pd.concat([df_model_frag, df_frag_copy])
            if print_times:
                print(f"after model({model_id}) frag: " + get_timestamp())
            
            # View on points scored
            df_points = c_trace.frag_view_to_point_view(df_frag, point_fns)
            df_points.insert(0, 'model_identifier', model_id)
            df_points.insert(0, 'trace_identifier', name)
            df_points.insert(0, 'trace_group_identifier', tg_name)
            df_points.insert(0, 'model_variables_allocator_identifier', model_type)
            df_points.insert(0, 'model_config_size_multiple', size_mult)
            df_model_points = pd.concat([df_model_points, df_points])
            if print_times:
                print(f"after model({model_id}) point: " + get_timestamp())
        if print_times:
            print()
        
    return TraceGroupData(tg_name, trace_names, df_traces, df_model_mem, df_model_frag, df_model_points)

PLOT_RENAME_MAPPING = {
    "points_total":"Total Points",
    "points_int":"Int. Points",
    "points_ext":"Ext. Points",
    "point_value": "Num. Points",
    "model_config_size_multiple":"Size Multiple",
    "model_variables_allocator_identifier":"Alloc. Type",
    "model_identifier": "Model ID",
    "frag_int_relative":"Rel. Int.",
    "frag_int_WJ_max":"WJ Int. Max",
    "frag_int_WJ_reserved_upper":"WJ Int. Res.",
    "frag_ext_rel_PV":"Rel. Ext. PV",
    "frag_ext_LFB_PV":"LFB PV",
    "frag_ext_WJ_alloc_reserved_upper":"WJ Ext. Alloc. Res",
    "frag_ext_WJ_req_reserved_upper":"WJ Ext. Req. Res",
    "frag_ext_WJ_alloc_max":"WJ Ext. Alloc. Max",
    "frag_ext_WJ_req_max":"WJ Ext. Req. Max",
    "ideal_total":"Ideal Total Points",
    "ideal_int":"Ideal Int. Points",
    "ideal_ext":"Ideal Ext. Points",
    "ideal_total_avg":"Ideal Total Avg.",
    "ideal_int_avg":"Ideal Int. Avg.",
    "ideal_ext_avg":"Ideal Ext. Avg.",    
    "ideal_total_perc":"Ideal Total Perc.",
    "ideal_int_perc":"Ideal Int. Perc.",
    "ideal_ext_perc":"Ideal Ext. Perc.",   
    "program_cfrac":"CFrac",
    "program_larson":"Larson",
    "synth_randomized_even_spread":"Randomized",
    "synth_sim":"Simulation",
    "synth_stack":"Stack"
}  