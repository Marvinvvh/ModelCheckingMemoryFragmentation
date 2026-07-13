import os

import xxhash
from . import model_parser as mp
from . import query_parser as qp
from . import trace_parser as tp
from .shared_datatypes import ParsingOptions
import pandas as pd
import pyarrow as pa
import pyarrow.parquet as pq

class TraceDirParser():
    def __init__(self):
        self.path: str = ""
        self.merged_model_dir_parser: MergedModelDirParser
    
    def parse_models(self):
        model_dirs = [os.path.join(self.path, dir) for dir in os.listdir(self.path) if dir.startswith("merged-model_") and os.path.isdir(os.path.join(self.path, dir))]
        models = []
        for model_dir in model_dirs:
            self.merged_model_dir_parser.path = model_dir
            model = self.merged_model_dir_parser.parse_queries()
            if model is None:
                return None
            models.append(model)

        trace_files = [os.path.join(self.path, file) for file in os.listdir(self.path) if file.startswith("trace_") and os.path.isfile(os.path.join(self.path, file))]
        if len(trace_files) != 1:
            return None
        trace_file = trace_files[0]
        trace = tp.parse_trace(trace_file)
        if trace is None:
            return None


        identifier = os.path.basename(self.path)
        return TraceContainer(identifier, self.path, trace, models, [])

    def _parse_to_parquet(self, parquet_dir, hierarchy, df_actions: pd.DataFrame, df_query_search: pd.DataFrame):

        trace = self.parse_models()
        if trace is None:
            return df_actions, df_query_search

        trace_dict = trace.flatten_to_dict(hierarchy, ParsingOptions())
        if trace_dict is None:
            return df_actions, df_query_search

        options = ParsingOptions()
        options.include_trace_actions = True
        trace_actions = trace.trace.to_dict(options)['trace_actions']

        query_search: list[dict] = []
        query_data: list = []

        options.include_trace_actions = False
        trace_dict: list[dict] = trace.flatten_to_dict(hierarchy, options)
        for d in trace_dict:
            qs = {}
            qd = {}
            for k,v in d.items():
                if k == 'query_data':
                    qd[k] = v
                else:
                    qs[k] = v
            
            query_data.append(qd['query_data'])
            query_search.append(qs)

        example_search = query_search[0]
        qs_keys: list = [k for k in example_search.keys()]
        trace_keys: list = [k for k in qs_keys if 'query' not in k and 'model' not in k]
        trace_values = [example_search[k] for k in trace_keys]

        # Generate hasher so we get a unique hash per trace and query
        hasher = xxhash.xxh128()
        def hash_query(values):
            hasher.reset()
            for v in values:
                hasher.update(str(v))
            return hasher.digest().hex()
        
        def hash_trace(values):
            hasher.reset()
            for v in values:
                hasher.update(str(v))
            return hasher.digest().hex()

        hash_query_name = "hash_query"
        hash_trace_name = "hash_trace"

        # Generate hashes for the queries and trace
        df_qs = pd.DataFrame(query_search) 
        df_hashed_query = df_qs[qs_keys].apply(hash_query, axis=1)
        df_hashed_trace = df_qs[trace_keys].apply(hash_trace, axis=1)
        df_qs = df_qs.merge(
            df_hashed_query.copy().rename(hash_query_name), left_index=True, right_index=True).merge(
            df_hashed_trace.copy().rename(hash_trace_name), left_index=True, right_index=True)
        
        trace_hash = hash_trace(trace_values)
        df_act = pd.DataFrame(trace_actions)
        df_act[hash_trace_name] = trace_hash
        df_act.set_index(hash_trace_name)

        data = []
        for ht, hq, d in zip(df_hashed_trace, df_hashed_query, query_data):
            [data.append({hash_query_name:hq,hash_trace_name:ht, **i}) for i in d]
        df_qd = pd.DataFrame(data)

        # Write the hashed traces to file
        table = pa.Table.from_pandas(df_qd)
        hashed_dir_path: str = os.path.join(parquet_dir, "hashed_traces")
        if not os.path.exists(hashed_dir_path):
            os.makedirs(hashed_dir_path)
        pq.write_to_dataset(table, root_path=hashed_dir_path, partition_cols=['hash_trace'])

        # Parse query data to parquet file
        qd_path = os.path.join(hashed_dir_path, "trace_hash_dir.parquet")
        df_qd.to_parquet(qd_path)

        df_actions = pd.concat([df_actions, df_act], ignore_index=True) if not df_actions.empty else df_act
        df_query_search = pd.concat([df_query_search, df_qs], ignore_index=True) if not df_query_search.empty else df_qs
        return df_actions, df_query_search

class TraceContainer():
    def __init__(self, identifier, path, trace, merged_models, queries):
        self.identifier = identifier
        self.path: str = path
        self.trace: tp.Trace = trace
        self.merged_models: list[MergedModelContainer] = merged_models
    
    def flatten_to_dict(self, hierarchy, dataset_filter: ParsingOptions):
        hierarchy = {**hierarchy, "trace_container_identifier":self.identifier, "trace_container_path":self.path, **self.trace.to_dict(dataset_filter)}

        if dataset_filter.stop_at_traces:
            return [hierarchy]

        entries = []
        for merged_model in self.merged_models:
            entries.extend(merged_model.flatten_to_dict(hierarchy))
        return entries


class MergedModelDirParser():
    def __init__(self):
        self.path: str = ""
        self.model_config_parser: mp.ModelConfigRegister 
        self.model_variables_parser: mp.ModelVariablesRegister
        # Trace higher than merged model
        self.query_parser: qp.QueryCollectionParserRegister
    
    def parse_queries(self):
        query_files = []
        for dir, _, files in os.walk(self.path):
            query_files += [(file, os.path.join(dir, file)) for file in files if file.startswith("query_")]
        
        query_collections: list[qp.TraceQueryCollection] = []
        for filename, query_file in query_files:
            query_collection = qp.parse_query_file(query_file, self.query_parser)
            if query_collection is None:
                return None
            query_collections.append(query_collection)
        
        model_configs_files = [os.path.join(self.path, file) for file in os.listdir(self.path) if file.startswith("config_")]
        if len(model_configs_files) != 1:
            return None
        model_config_file = model_configs_files[0]

        model_variables_files = [os.path.join(self.path, file) for file in os.listdir(self.path) if file.startswith("variables_")]
        if len(model_variables_files) != 1:
            return None
        model_variable_file = model_variables_files[0]

        model = mp.parse_model(model_config_file, model_variable_file, self.model_config_parser, self.model_variables_parser)
        identifier = os.path.basename(self.path)
        return MergedModelContainer(identifier, self.path, model, query_collections, [])
    
class MergedModelContainer:
    def __init__(self, identifier, path, model, query_collections, traces):
        self.identifier = identifier
        self.path: str = path
        self.model: mp.Model = model
        self.query_collections: list[qp.TraceQueryCollection] = query_collections

    def flatten_to_dict(self, hierarchy):
        hierarchy = {**hierarchy, "model_identifier":self.identifier, "model_path":self.path, **self.model.to_dict()}
        entries = []
        for query_collection in self.query_collections:
            entries.extend(query_collection.flatten_to_dict(hierarchy, None))
        return entries

class TraceGroupDirParser():
    def __init__(self):
        self.path: str = ""
        self.trace_dir_parser: TraceDirParser
        return
    
    def get_trace_dirs(self) -> list[str]:
        return [os.path.join(self.path, dir) for dir in os.listdir(self.path) if dir.startswith("trace_") and os.path.isdir(os.path.join(self.path, dir))]

    
    def parse(self):
        traces = []
        for trace_dir in self.get_trace_dirs():
            self.trace_dir_parser.path = trace_dir
            trace = self.trace_dir_parser.parse_models()

            if trace is None:
                return None
            traces.append(trace)
        
        identifier = os.path.basename(self.path)

        return TraceGroupContainer(identifier, self.path, traces)
    
    def _parse_to_parquet(self, parquet_dir, hierarchy, df_actions, df_query_search):
        identifier = os.path.basename(self.path)
        hierarchy = {**hierarchy, "trace_group_path":self.path, "trace_group_identifier": identifier}
        print(identifier)
        for trace_dir in self.get_trace_dirs():
            self.trace_dir_parser.path = trace_dir
            df_actions, df_query_search = self.trace_dir_parser._parse_to_parquet(parquet_dir, hierarchy, df_actions, df_query_search)
        return df_actions, df_query_search


class TraceGroupContainer:
    def __init__(self, identifier, path, trace_groups:list[TraceContainer]):
        self.path: str = path
        self.identifier: str = identifier
        self.traces: list[TraceContainer] = trace_groups
    
    def flatten_to_dict(self, hierarchy, dataset_filter):
        hierarchy = {**hierarchy, "trace_group_path":self.path, "trace_group_identifier":self.identifier}
        entries = []
        for trace in self.traces:
            entries.extend(trace.flatten_to_dict(hierarchy, dataset_filter))
        return entries


class RunDirParser():
    def __init__(self):
        self.path: str = ""
        self.trace_group_dir_parser: TraceGroupDirParser
    
    def get_trace_group_dirs(self) -> list[str]:
        return [os.path.join(self.path, dir) for dir in os.listdir(self.path) if dir.startswith("trace-group") and os.path.isdir(os.path.join(self.path, dir))]

    
    def parse(self):
        trace_groups = []
        for trace_group_dir in self.get_trace_group_dirs():
            self.trace_group_dir_parser.path = trace_group_dir
            trace = self.trace_group_dir_parser.parse()
            if trace is None:
                return None
            trace_groups.append(trace)
        identifier = os.path.basename(self.path)
        return RunContainer(identifier, self.path, trace_groups)
    
    def parse_to_parquet(self, parquet_dir):
        trace_groups = []
        identifier = os.path.basename(self.path)
        hierarchy = {"run_identifier":identifier, "run_path":self.path}
        df_query_search = pd.DataFrame()
        df_actions = pd.DataFrame()
        for trace_group_dir in self.get_trace_group_dirs():
            self.trace_group_dir_parser.path = trace_group_dir
            df_actions, df_query_search = self.trace_group_dir_parser._parse_to_parquet(parquet_dir, hierarchy, df_actions, df_query_search)
        return df_actions, df_query_search

    
    
class RunContainer():
    def __init__(self, identifier, path, trace_groups):
        self.path: str = path
        self.identifier: str = identifier
        self.trace_groups: list[TraceGroupContainer] = trace_groups
    
    def to_dict(self, dataset_filter, hierarchy = {}) -> list[dict]:
        hierarchy = {**hierarchy, "run_identifier":self.identifier, "run_path":self.path}
        entries = []
        for trace_group in self.trace_groups:
            entries.extend(trace_group.flatten_to_dict(hierarchy, dataset_filter))
        return entries


class MultiRunDirParser():
    def __init__(self):
        # Trace lower than merged model
        self.run_parsers: list[RunDirParser] = []
    
    def clear_runs(self):
        self.run_parsers.clear()
    
    def get_runs_from_dir(self, multi_dir: str, trace_group_dir_parser: TraceGroupDirParser):
        run_dirs = [os.path.join(multi_dir, dir) for dir in os.listdir(multi_dir) if dir.startswith("run_") and os.path.isdir(os.path.join(multi_dir, dir))]
        for run_dir in run_dirs:
            run_dir_parser = RunDirParser()
            run_dir_parser.path = run_dir
            run_dir_parser.trace_group_dir_parser = trace_group_dir_parser
            self.run_parsers.append(run_dir_parser)
        
    
    def add_run(self, run_dir_parser: RunDirParser):
        self.run_parsers.append(run_dir_parser)

    def parse(self):
        run_containers = []
        for run_parser in self.run_parsers:
            run_container = run_parser.parse()
            if run_container is None:
                return None
            run_containers.append(run_container)

        return MultiRunContainer(run_containers)
    
    def parse_to_parquet(self, parquet_dir):
        for i, run_parser in enumerate(self.run_parsers):
            run_parquet_dir =  os.path.join(parquet_dir, "run_" + str(i))
            df_act, df_qs = run_parser.parse_to_parquet(run_parquet_dir)
            df_qs.to_parquet(os.path.join(run_parquet_dir, "dataset_query_search.parquet"))
            df_act.to_parquet(os.path.join(run_parquet_dir, "dataset_trace_actions.parquet"))

class MultiRunContainer():
    def __init__(self, runs):
        self.runs: list[RunContainer] = runs

    def to_dict(self, dataset_filter, hierarchy = {}) -> list[dict]:
        l = []
        for r in self.runs:
            l += r.to_dict(dataset_filter, hierarchy)
        return l