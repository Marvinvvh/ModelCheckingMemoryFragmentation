
from abc import ABC, abstractmethod
import pandas as pd
import conversions.conversion_util as cu
import common_constants as cc
import filters.query_action_filters as f_action
import filters.query_memory_extremes_filters as f_mem_ext
import filters.query_memory_usage_filters as f_mem_usage
import filters.query_int_frag_filters as f_frag_int
import filters.query_ext_frag_filters as f_frag_ext
import filters.model_filters as f_model
import filters.state_filters as f_state
import filters.filters as fl
import math
from itertools import pairwise
from typing import NamedTuple

# Used when aggregating limits
LIMIT_SEPARATOR = "_LIMIT_"

def create_trace_view(df:pd.DataFrame, trace_name:str, hashes_path:str) -> pd.DataFrame:

    query_filter = fl.Filter()
    query_filter.merge(
        [
            f_model.MODEL_FILTER_TRACE_TRACKER,
            f_mem_usage.QUERY_FILTER_TOTAL_REQUESTED_MEMORY,
            f_mem_ext.QUERY_FILTER_MAX_REQUESTED_MEMORY,
            f_mem_ext.QUERY_FILTER_MIN_REQUESTED_SIZE,
            f_mem_ext.QUERY_FILTER_MAX_REQUESTED_SIZE,
            f_action.QUERY_FILTER_ACTION_COUNT,
            f_action.QUERY_FILTER_ALLOC_ACTION_COUNT,
            f_action.QUERY_FILTER_FREE_ACTION_COUNT,
        ]
    )

    df_queries = query_filter.apply(df)
    df_queries = cu.get_query_data(df_queries, hashes_path)
    df_queries = cu.get_max_time_values(df_queries)

    queries: list[str] = [
        cc.MemoryUsageConstants.total_requested_memory.value,
        cc.MemoryExtremesConstants.max_requested_memory.value,
        cc.MemoryExtremesConstants.min_requested_size.value,
        cc.MemoryExtremesConstants.max_requested_size.value,
        cc.ActionConstants.action_count.value,
        cc.ActionConstants.alloc_action_count.value,
        cc.ActionConstants.free_action_count.value,
    ]
    
    df_trace_overview = pd.DataFrame()
    for q in queries:
        df_filtered = df_queries[(df_queries['query_identifier'] == q) & (df_queries['trace_identifier'] == trace_name)]
        # We just skip non-existent queries
        if df_filtered.empty:
            continue
        df_trace_overview = df_trace_overview.assign(**{q:df_filtered['value'].values})

    return df_trace_overview

def create_model_memory_view(df:pd.DataFrame, hashes_path:str) -> pd.DataFrame:
    query_filter = fl.Filter()
    query_filter.merge(
        [
            f_mem_usage.QUERY_FILTER_ALL_FREE,
            f_mem_usage.QUERY_FILTER_ALL_TOTAL,
            f_mem_usage.QUERY_FILTER_ALL_LFB,
            f_mem_usage.QUERY_FILTER_ALL_LIVE,
            f_mem_ext.QUERY_FILTER_ALL_MAX_MEMORY,
            f_mem_ext.QUERY_FILTER_ALL_MIN_SIZE,
            f_mem_ext.QUERY_FILTER_ALL_MAX_SIZE,
            f_action.QUERY_FILTER_ACTION_COUNT,
            f_action.QUERY_FILTER_FREE_ACTION_COUNT,
            f_action.QUERY_FILTER_ALLOC_ACTION_COUNT,
            f_state.QUERY_FILTER_TRACE_DONE,
            f_state.QUERY_FILTER_TRACE_ERROR,
            f_state.QUERY_FILTER_ALLOCATOR_ERROR,
            f_state.QUERY_FILTER_HEAP_ERROR,
        ]
    )

    df_queries = query_filter.apply(df)
    df_queries = cu.get_query_data(df_queries, hashes_path)
    df_queries = cu.get_max_time_values(df_queries)

    # Get anything related to memory states, and the "basic" extremes.
    queries: list[str] = [
        *cc.MemoryUsageConstants.largest_free_block(),
        *cc.MemoryUsageConstants.free_memory(),
        *cc.MemoryUsageConstants.live_memory(),
        *cc.MemoryUsageConstants.total_memory(),
        *cc.MemoryExtremesConstants.max_memory(),
        *cc.MemoryExtremesConstants.min_size(),
        *cc.MemoryExtremesConstants.max_size(),
        cc.ActionConstants.action_count.value,
        cc.ActionConstants.alloc_action_count.value,
        cc.ActionConstants.free_action_count.value,
        cc.StateConstants.trace_done.value,
        cc.StateConstants.trace_error.value,
        cc.StateConstants.allocator_error.value,
        cc.StateConstants.heap_error.value,
    ]

    df_view = pd.DataFrame()
    for q in queries:
        df_filtered = df_queries[df_queries['query_identifier'] == q]
        # TEMP: We just skip non-existent queries
        if df_filtered.empty:
            continue
        df_view = df_view.assign(**{q:df_filtered['value'].values})

    return df_view

def name_limit_query(str, lim):
    return f"{str}_{lim}"

def sum_limit_query_id(df_query_data: pd.DataFrame, limits: list[int], query_identifier: str) -> pd.DataFrame:

    # Remove the first instance of any values which hold two values per time unit
    # If a data point in the automata changes values in a single time unit it will have a snapshot of the start and end state
    df_filter = df_query_data[df_query_data['query_identifier'] == query_identifier]
    df_abs_group = df_filter.groupby('time', as_index=False).last()
    df_abs = df_abs_group.merge(df_abs_group)
    
    # Sum up all values which equal or exceed the lim
    list_frags = df_abs[['time', 'value']].values.tolist()
    df_view = pd.DataFrame()
    # Iterate over all time-value ranges because we might have multiple duplicate values in a row, which UPPAAL does not include.
    lims = {}
    for j, (t, v) in enumerate(list_frags):
        (t_next, _) = list_frags[j + 1] if j < len(list_frags) - 1 else (None, None)
        l = "unknown"
        amt_limit_breaks = 0

        # For each limit, sum up the amount of the we pass that limit. That way we can determine the points per limit.
        for  i, lim in enumerate(limits):
            diff = t_next - t if j < len(list_frags) - 1 else 1
            if i < len(limits) - 1 and v >= lim and v < limits[i+1]:
                # If we "skip" time units that means we have a range with the same value. Sum that up.
                l = lim
                amt_limit_breaks += math.floor(diff)
                break       
            elif i == len(limits) - 1 and v >= lim:
                l = lim
                amt_limit_breaks += math.floor(diff)
                break
            elif i == len(limits) - 1:
                assert(False)

        if l not in lims:
            lims[l] = amt_limit_breaks
        else:
            lims[l] += amt_limit_breaks

    for (i, l) in enumerate(limits):
        if l not in lims:
            lims[l] = 0
        
        # Compensate for fragmentation values at time 0 (no value processed) and time N + 1 where N indicates the last processed value
        if l == 0:
            lims[i] -= 2

    for l in lims:
        df_view = df_view.assign(**{f"{query_identifier}{LIMIT_SEPARATOR}{l}":[lims[l]]})
    

    return df_view

# Not an ideal implementation, but we're iterating over existing fragmentation types in the first place. This will not extend in the POC.
def create_model_frag_view(df:pd.DataFrame, hashes_path:str, limits_frag_int, limits_frag_LFB, limits_frag_ext) -> pd.DataFrame:
    query_filter = fl.Filter()
    query_filter.merge(
        [
            f_frag_ext.QUERY_FILTER_FRAG_EXT_ALL_METRICS,
            f_frag_int.QUERY_FILTER_FRAG_INT_ALL_METRICS
        ]
    )

    df_queries = query_filter.apply(df)
    df_query_data = cu.get_query_data(df_queries, hashes_path)
    df_query_data_max = cu.get_max_time_values(df_query_data)


    ALL_WJ: list[str] = [
        *cc.IntFragConstants.all_WJ(),
        *cc.ExtFragConstants.all_WJ_req(),
        *cc.ExtFragConstants.all_WJ_alloc(),
    ]

    # Get all WJ fragmentation types, and determine their value at the end of the trace.
    df_view = pd.DataFrame()
    for wj in ALL_WJ:
        df_filtered = df_query_data_max[df_query_data_max['query_identifier'] == wj]
        # We just skip non-existent queries
        if df_filtered.empty:
            continue
        df_view = df_view.assign(**{wj:df_filtered['value'].values})
    
    # Checking limits for all relative internal fragmentation
    df_view = pd.concat([df_view, sum_limit_query_id(df_query_data, limits_frag_int, cc.IntFragConstants.relative.value)], axis=1)

    # Checking limits for all LFB frag
    for lfb in cc.ExtFragConstants.all_LFB():
        # We just skip non-existent queries
        if df_query_data_max[df_query_data_max['query_identifier'] == lfb].empty:
            continue

        df_view = pd.concat([df_view, sum_limit_query_id(df_query_data, limits_frag_LFB, lfb)], axis=1)

    # Checking limits for all relative external frag
    for ext_rel in cc.ExtFragConstants.all_relative():
        # We just skip non-existent queries
        if df_query_data_max[df_query_data_max['query_identifier'] == ext_rel].empty:
            continue

        df_view = pd.concat([df_view, sum_limit_query_id(df_query_data, limits_frag_ext, ext_rel)], axis=1)


    return df_view

class PointsFunction(ABC):
    @abstractmethod
    def get_points(self, val: int) -> float:
        pass

class PointThreshold(NamedTuple):
    threshold: float
    valuation: float

# Point scaling function based on steps. Each step is associated with some threshold and valuation. Depending on the value, it returns a certain amount of points.
class PointsSteps(PointsFunction):
    def __init__(self, steps: list[PointThreshold]):
        for i, (threshold, valuation) in enumerate(steps):
            assert(threshold >= 0)
            assert(valuation >= 0 and valuation <= 1)
            if i + 1 < len(steps):
                assert steps[i].threshold < steps[i + 1].threshold

        self.steps = steps
    
    def get_points(self, val: int) -> float:
        assert(val >= 0)
        for i, (threshold, point) in enumerate(self.steps):
            if i < len(self.steps) - 1 and val >= threshold and val < self.steps[i+1].threshold:
                return point
            elif i == len(self.steps) - 1:
                return point
        
        # Unreachable. 
        assert False
    
# Linear point scaling function, that scaling between two points. Must be positive values.
class PointsLinearIdeal(PointsFunction):
    def __init__(self, min, max):
        assert(min >= 0)
        assert(max > min)
        self.min = min
        self.max = max
    
    def get_points(self, val: int) -> float:
        assert(val >= 0)
        return 1 - (val - self.min) / (self.max - self.min) if val <= self.max else 0
    
class FragScaling(NamedTuple):
    frag_type: str
    points_fn: PointsFunction
    uses_limits: bool

    
def _collect_points_for_limits(row, cols: list[str], fn: PointsFunction) -> float:
    points = 0.0
    action_count = 0.0
    for col in cols:
        _, lim = col.split(LIMIT_SEPARATOR)
        point_factor = fn.get_points(int(lim))
        action_count += row[col]
        points += point_factor * row[col]
    
    return points / action_count

def frag_view_to_point_view(df: pd.DataFrame, pointScaling: list[FragScaling]) -> pd.DataFrame:
    df_points = pd.DataFrame()

    for ps in pointScaling:
        # If there are no limits we just apply the function immediately as long as we actually retrieved it from data
        if not ps.uses_limits and ps.frag_type in df.columns.values.tolist():
            df_points[ps.frag_type]= df[ps.frag_type].transform(lambda x: ps.points_fn.get_points(x))
        # If we're using limits we have fragmentation columns like SOME<LIMIT_SEPARATOR>LIMIT. So we collect all those columns and summarize their points
        elif ps.uses_limits and any(ps.frag_type in x for x in df.columns.values):
            cols = [col for col in df.columns.values if ps.frag_type in col]
            if cols:
                df_points[ps.frag_type] = df.apply(lambda row: _collect_points_for_limits(row, cols, ps.points_fn), axis=1)
        
    return df_points