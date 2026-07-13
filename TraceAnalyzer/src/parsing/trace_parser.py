from enum import Enum
from .shared_datatypes import Sample, ParsingOptions
import json

class TraceActionType(Enum):
    eAlloc = "eAlloc"
    eFree = "eFree"
        
class TraceAction:
    def __init__(self, type, pointer, size):
        self.type: TraceActionType = type
        self.pointer: int = pointer
        self.size: int = size
    
    def to_dict(self) -> dict:
        return {
            "trace_action_type":self.type,
            "trace_action_pointer":self.pointer,
            "trace_action_size":self.size,
        }

class TraceParser:
    def __init__(self, trace):
        self.trace = trace
    
    def parse(self):
        trace_actions = self.trace['traceActions']
        last_pointer = self.trace['lastPointer']
        size_allocation_actions = self.trace['sizeAllocationActions']
        identifier = self.trace['identifier']

        if None in [trace_actions, last_pointer, size_allocation_actions, identifier]:
            return None
        
        parsed_trace_actions = []
        for action in trace_actions:
            type = action['type']
            pointer = action['pointer']
            size = action['size']

            if None in [type, pointer, size] or type not in TraceActionType.__members__ :
                return None
            else:
                parsed_trace_actions.append(TraceAction(type, pointer, size))
        
        return Trace(parsed_trace_actions, last_pointer, size_allocation_actions, identifier)
        

class Trace:
    def __init__(self, trace_actions, last_pointer, size_allocation_actions, identifier):
        self.trace_actions: list[TraceAction] = trace_actions
        self.last_pointer: int = last_pointer
        self.size_allocation_actions: int = size_allocation_actions
        self.identifier: str = identifier
    
    def to_dict(self, dataset_filter: ParsingOptions) -> dict:
        d = {
            "trace_last_pointer":self.last_pointer,
            "trace_size_allocation_actions":self.size_allocation_actions,
            "trace_identifier":self.identifier,
        }

        if dataset_filter.include_trace_actions:
            d['trace_actions'] = [ta.to_dict() for ta in self.trace_actions]

        return d

def parse_trace(trace_path) -> Trace | None:
    json_trace: dict
    with open(trace_path, "r") as json_raw_data:
        json_trace = json.load(json_raw_data)

    return TraceParser(json_trace).parse()