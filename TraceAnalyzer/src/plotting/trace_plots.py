import plotting.plot_util as p_util

import filters.trace_filters as t_f
import filters.filters as fl

def plot_trace_action_filtered(df_qs, df_actions, labels: dict[str, str] = {}, title: str="Live memory comparison", y_col: str = "value", wanted_trace_groups: list[str] = [], wanted_traces: list[str] = [], include_allocations=True, include_frees=False, show_legend=True, port=5000):
    filter = t_f.get_tg(wanted_trace_groups).merge([t_f.get_trace(wanted_traces)])
    df_trace = filter.apply(df_qs)
    hash_checks = df_actions[fl.HashesFilterOptions.trace.value].isin(df_trace[fl.HashesFilterOptions.trace.value])
    dataset = df_actions[hash_checks]
    return p_util.show_index_chart_query_data(title, dataset, labels=labels, y_name=y_col, show_legend=show_legend)
