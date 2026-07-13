from typing import Callable
import parsing.directory_parser as dp
import parsing.model_parser as mp
import parsing.trace_parser as tp
import parsing.query_parser as qp
import plotly.express as px

class TraceQueryDataFilter():
    query: list[Callable[..., bool]]
    query_collection: list[Callable[..., bool]]
    merged_model: list[Callable[..., bool]]
    trace: list[Callable[..., bool]]
    trace_group: list[Callable[..., bool]]

class TraceQueryDataPoint:
    def __init__(self, run, trace_group, trace, model, query_collection, query):
        self.run = run
        self.trace_group = trace_group
        self.trace = trace
        self.model = model
        self.query_collection = query_collection
        self.query = query

def get_queries_from_query_collection(query_collection: qp.TraceQueryCollection, query_identifiers: list[Callable[..., bool]]) -> dict[str, list[TraceQueryDataPoint]]:
    queries: dict[str, list[TraceQueryDataPoint]] = {}
    for query in query_collection.queries:
        if query_identifiers and not any(fn(query) for fn in query_identifiers):
            continue

        identifier = query.identifier
        if identifier not in queries:
            queries[identifier] = []
        query_data_point = TraceQueryDataPoint("", "", "", "", query_collection.identifier, query)
        queries[query.identifier].append(query_data_point)

    return queries

def get_queries_from_query_collections(query_collections: list[qp.TraceQueryCollection], query_collection_identifiers: list[Callable[..., bool]], query_identifiers: list[Callable[..., bool]]):
    queries: dict[str, list[TraceQueryDataPoint]] = {}
    for query_collection in query_collections:
        if query_collection_identifiers and not any(fn(query_collection) for fn in query_collection_identifiers): 
            continue
        for k, v in get_queries_from_query_collection(query_collection, query_identifiers).items():
            if k not in queries:
                queries[k] = []
            queries[k].extend(v)
    return queries

def get_queries_from_merged_model(merged_model: dp.MergedModelContainer, query_collection_identifiers: list[Callable[..., bool]], query_identifiers: list[Callable[..., bool]]):
    queries =  get_queries_from_query_collections(merged_model.query_collections, query_collection_identifiers, query_identifiers)
    for datapoint_collection in queries.values():
        for datapoint in datapoint_collection:
            datapoint.model = merged_model.identifier
    return queries


def get_queries_from_merged_models(merged_models: list[dp.MergedModelContainer], merged_model_identifiers: list[Callable[..., bool]], query_collection_identifiers: list[Callable[..., bool]], query_identifiers: list[Callable[..., bool]]):
    queries: dict[str, list[TraceQueryDataPoint]] = {}
    for merged_model in merged_models:
        if merged_model_identifiers and not any(fn(merged_model) for fn in merged_model_identifiers):
            continue
        for k, v in get_queries_from_merged_model(merged_model, query_collection_identifiers, query_identifiers).items():
            if k not in queries:
                queries[k] = []
            queries[k].extend(v)
    return queries

def get_queries_from_trace(trace: dp.TraceContainer, merged_model_identifiers: list[Callable[..., bool]], query_collection_identifiers: list[Callable[..., bool]], query_identifiers: list[Callable[..., bool]]):
    queries =  get_queries_from_merged_models(trace.merged_models, merged_model_identifiers, query_collection_identifiers, query_identifiers)
    for datapoint_collection in queries.values():
        for datapoint in datapoint_collection:
                datapoint.trace = trace.trace.identifier
    return queries


def get_queries_from_traces(traces: list[dp.TraceContainer], trace_identifiers: list[Callable[..., bool]], merged_model_identifiers: list[Callable[..., bool]], query_collection_identifiers: list[Callable[..., bool]], query_identifiers: list[Callable[..., bool]]):
    queries: dict[str, list[TraceQueryDataPoint]] = {}
    for trace in traces:
        if trace_identifiers and not any(fn(trace) for fn in trace_identifiers):
            continue

        for k, v in get_queries_from_trace(trace, merged_model_identifiers, query_collection_identifiers, query_identifiers).items():
            if k not in queries:
                queries[k] = []
            queries[k].extend(v)
    return queries

def get_queries_from_trace_group(trace_group: dp.TraceGroupContainer, trace_identifiers: list[Callable[..., bool]], merged_model_identifiers: list[Callable[..., bool]], query_collection_identifiers: list[Callable[..., bool]], query_identifiers: list[Callable[..., bool]]):
    queries =  get_queries_from_traces(trace_group.traces, trace_identifiers, merged_model_identifiers, query_collection_identifiers, query_identifiers)
    for datapoint_collection in queries.values():
        for datapoint in datapoint_collection:
                datapoint.trace_group = trace_group.identifier
    return queries

def get_queries_from_trace_groups(trace_groups: list[dp.TraceGroupContainer], trace_group_identifiers: list[Callable[..., bool]], trace_identifiers: list[Callable[..., bool]], merged_model_identifiers: list[Callable[..., bool]], query_collection_identifiers: list[Callable[..., bool]], query_identifiers: list[Callable[..., bool]]):
    queries: dict[str, list[TraceQueryDataPoint]] = {}
    for trace_group in trace_groups:
        if trace_group_identifiers and not any(fn(trace_group) for fn in trace_group_identifiers):
            continue

        for k, v in get_queries_from_trace_group(trace_group, trace_identifiers, merged_model_identifiers, query_collection_identifiers, query_identifiers).items():
            if k not in queries:
                queries[k] = []
            queries[k].extend(v)
    return queries

def get_traces_from_trace_group(trace_group: dp.TraceGroupContainer, trace_group_identifiers: list[Callable[..., bool]], trace_identifiers: list[Callable[..., bool]]):
    traces = []
    for trace_container in trace_group.traces:
        if trace_identifiers and not any(fn(trace_container.trace) for fn in trace_identifiers):
            continue

        traces.append(trace_container.trace)
    return traces

def get_traces_from_trace_groups(trace_groups: list[dp.TraceGroupContainer], trace_group_identifiers: list[Callable[..., bool]], trace_identifiers: list[Callable[..., bool]]):
    traces = []
    for trace_group in trace_groups:
        if trace_group_identifiers and not any(fn(trace_group) for fn in trace_group_identifiers):
            continue

        traces += get_traces_from_trace_group(trace_group, trace_group_identifiers, trace_identifiers)
    return traces
