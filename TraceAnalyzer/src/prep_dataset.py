import pandas as pd
import os.path
import parsing.directory_parser as dp
import parsing.query_parser as qp
import parsing.model_parser as mp
import filters.filters as fl
import pandas as pd
from datetime import datetime

def setup_query_collection_parsers(register: qp.QueryCollectionParserRegister, tracking_register: qp.QueryParserRegister, validation_register: qp. QueryParserRegister):
    register.addParser('tracking-queries', qp.TraceQueryCollectionParser, tracking_register)
    register.addParser('verification-queries', qp.TraceQueryCollectionParser, validation_register)

def setup_query_parsers_tracking(register: qp.QueryParserRegister):
    register.addDefaultParser(qp.TraceQueryParser)

def setup_query_parsers_validation(register: qp.QueryParserRegister):
    register.addDefaultParser(qp.TraceQueryParser)

def setup_config_parsers(register: mp.ModelConfigRegister):
    register.addDefaultParser(mp.ModelConfigParser)

def setup_variables_parsers(register: mp.ModelVariablesRegister):
    register.addDefaultParser(mp.ModelVariablesParser)
        
def dump_column_names(df: pd.DataFrame, export_path: str):
    cols: list[str] = list(df.columns.values)
    s =  "Overview of columns\n"
    s += "-----------\n"
    for col in cols:
        s += (col + "\n")
    with open(export_path, "w") as f:
        f.write(s)

def dump_given_columns(df: pd.DataFrame, columns, export_path):
    dff = df[columns].drop_duplicates()
    def iter_to_str(iter) -> str:
        s = ""
        for (i, e) in enumerate(iter):
            s +=  e
            if i == len(iter) - 1:
                s += "\n"
            else:
                s += " -- "
        return s

    s =  "Overview of queries: "
    s += iter_to_str(columns)
    s += "-----------\n"
    for t in dff.itertuples(index=False):
        s += iter_to_str(t)
    with open(export_path, "w") as f:
        f.write(s)

# The purpose of this file is to convert raw datasets into parsed datasets, so that we can compare the allocator policies.
# An issue we encountered was that the raw datasets are too large when querying alot of data.
# In short we instead create a lookup table for traces and query data using hashes. The query data and trace actions are stored as separate parquet datasets, 
#   which are filtered using the hash from the lookup table. There's a unique hash for each trace and query combination.
# 
# The datasets are:
# - A parquet data set for query search, containing the query, trace group, trace, model, and query collection identifiers, and hash.
#       This dataset should be used to filter all the query data to get the exact data you want for analysis.  
# - A parquet data set for query data, queryable using a hash. Typically you would join the datasets on a hash.
# - A parquet data set for trace actions, queryable using a hash. This is queried using the trace hash.
def main(path_results: str, path_parsed_results: str, comparison_run: bool):
    path_parsed_parquet_parse= os.path.join(path_parsed_results, "conversion")

    print(datetime.now())

        # --- Test paths ---
    query_parser_register_tracking = qp.QueryParserRegister()
    setup_query_parsers_tracking(query_parser_register_tracking)

    query_parser_register_validation = qp.QueryParserRegister()
    setup_query_parsers_validation(query_parser_register_validation)

    query_collection_parser_register = qp.QueryCollectionParserRegister()
    setup_query_collection_parsers(query_collection_parser_register, query_parser_register_tracking, query_parser_register_validation)

    config_parser_register = mp.ModelConfigRegister()
    setup_config_parsers(config_parser_register)

    variables_parser_register = mp.ModelVariablesRegister()
    setup_variables_parsers(variables_parser_register)

    # Setup parsers for the dataset
    mm_dir_parser = dp.MergedModelDirParser()
    mm_dir_parser.model_config_parser = config_parser_register
    mm_dir_parser.model_variables_parser = variables_parser_register
    mm_dir_parser.query_parser = query_collection_parser_register

    tr_dir_parser = dp.TraceDirParser()
    tr_dir_parser.merged_model_dir_parser = mm_dir_parser

    tr_group_dir_parser = dp.TraceGroupDirParser()
    tr_group_dir_parser.trace_dir_parser = tr_dir_parser

    multi_run_dir_parser = dp.MultiRunDirParser()
    multi_run_dir_parser.get_runs_from_dir(path_results, tr_group_dir_parser)

    # Start converting the raw benchmark to parquet
    multi_run_dir_parser.parse_to_parquet(path_parsed_parquet_parse)

    # Generate all paths used to parse and dump info
    info_dump_path = os.path.join(path_parsed_results, "dataset_info")
    if not os.path.exists(info_dump_path):
        os.makedirs(info_dump_path)
    query_search_dump_path = os.path.join(info_dump_path, "query_search_columns.txt")
    query_data_dump_path = os.path.join(info_dump_path, "query_data_columns.txt")
    trace_actions_dump_path = os.path.join(info_dump_path, "trace_actions_columns.txt")
    conv_trace_actions_dump_path = os.path.join(info_dump_path, "conv_trace_actions_columns.txt")
    
    query_dump_path = os.path.join(info_dump_path, "queries.txt")
    trace_group_dump_path = os.path.join(info_dump_path, "trace_groups.txt")
    trace_dump_path = os.path.join(info_dump_path, "traces.txt")

    # Create the query search dataset to lookup the query data
    df_base_path = os.path.join(path_parsed_parquet_parse, "run_0")
    df_query_search_name = f"dataset_query_search.parquet"
    df_query_search_path = os.path.join(df_base_path, df_query_search_name)
    df_query_search = pd.read_parquet(df_query_search_path)

    df_trace_actions_name = f"dataset_trace_actions.parquet"
    df_trace_actions_path = os.path.join(df_base_path, df_trace_actions_name)
    df_trace_actions = pd.read_parquet(df_trace_actions_path)

    df_conv_trace_actions_name = f"dataset_conv_trace_actions.parquet"
    df_conv_trace_actions_path = os.path.join(df_base_path, df_conv_trace_actions_name)

    # First convert to groups that each represent a trace
    grouped_df_conv = df_trace_actions.copy()
    grouped_df_conv['time'] = df_trace_actions.groupby([fl.HashesFilterOptions.trace.value]).cumcount()

    # Get unique lookups
    lookup_allocs = grouped_df_conv[grouped_df_conv['trace_action_type'] == 'eAlloc'].drop_duplicates(subset=['hash_trace', 'trace_action_pointer'], keep='first').set_index(['hash_trace', 'trace_action_pointer'])

    def resolve_value(row):
        if row['trace_action_type'] == 'eFree':
            lookup_val = (row['hash_trace'], row['trace_action_pointer'])
            match = lookup_allocs.loc[lookup_val]
            return row['hash_trace'], row['time'], row['trace_action_type'], match['trace_action_size'] * -1.0
        return row['hash_trace'], row['time'], row['trace_action_type'], row['trace_action_size'] * 1.0
    
    # Create hash trace lookup
    df_conv_trace_actions = pd.DataFrame()
    df_conv_trace_actions[['hash_trace', 'time', 'type', 'size']] = grouped_df_conv.apply(resolve_value, axis=1, result_type='expand')
    df_conv_trace_actions.to_parquet(df_conv_trace_actions_path)

    # Create the distributed data structure
    import pyarrow.parquet as pq
    df_query_data_name = f"hashed_traces"
    df_query_data_path = os.path.join(df_base_path, df_query_data_name)
    df_query_data_table =     pq.ParquetDataset(df_query_data_path,
                                partitioning='hive',
                                filters=[
                                    ('hash_trace', 'in', df_query_search['hash_trace'].to_list()),
                                    ('hash_query', 'in', df_query_search['hash_query'].to_list())]).read()
    df_query_data: pd.DataFrame = df_query_data_table.to_pandas()

    # Dump all the data to file
    dump_column_names(df_query_search, query_search_dump_path)
    dump_column_names(df_query_data, query_data_dump_path)
    dump_column_names(df_trace_actions, trace_actions_dump_path)
    dump_column_names(df_conv_trace_actions, conv_trace_actions_dump_path)
    dump_given_columns(df_query_search, ['query_collection_identifier', 'query_identifier'], query_dump_path)
    dump_given_columns(df_query_search, ['trace_group_identifier'], trace_group_dump_path)
    dump_given_columns(df_query_search, ['trace_group_identifier', 'trace_identifier'], trace_dump_path)

    print(datetime.now())

import sys
import argparse
if __name__ == '__main__': 
    parser = argparse.ArgumentParser()

    parser.add_argument('results_input', type=str, help="The input results from TraceVerifier.")
    parser.add_argument('parsed_results_output', type=str, help="The output parsed results usable for analysis.")
    parser.add_argument('--comparison', action='store_true', help="Whether it is a comparison run or not.")

    args=parser.parse_args()

    main(args.results_input, args.parsed_results_output, args.comparison)

    print("path_results", args.results_input)
    print("path_parsed_results", args.parsed_results_output)
    print("comparison_run", args.comparison)