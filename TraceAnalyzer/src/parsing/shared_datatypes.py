from enum import Enum

class Sample:
    def __init__(self, time, value):
        self.time = time
        self.value = value
    
    def to_dict(self) -> dict:
        return {
            "time":self.time,
            "value":self.value,
        }

class ParsingOptions:
    def __init__(self):
        self.include_trace_actions = False
        self.stop_at_traces = False

class DatasetPathInfo(Enum):
    trace_hash_dirname = "trace_hashes"
    trace_action_dataset_filename = "trace_actions_dataset"
    query_search_dataset_filename = "query_search_dataset"
    