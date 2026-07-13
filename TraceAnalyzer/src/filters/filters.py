from enum import Enum, auto
import pandas as pd
from typing_extensions import Self
from collections.abc import Callable
import numpy as np

# Values based on the 
class QueryFilterOptions(Enum):
    id = 'query_identifier'
    collection_id = 'query_collection_identifier'
    status = 'query_status'
    time = 'time'
    data_value = 'value' # value is reserved for enum

class ModelFilterOptions(Enum):
    id = 'model_identifier'
    config_id = 'model_config_identifier'
    page_size = 'model_config_page_size'
    total_memory_size = auto
    address_alignment = 'model_config_address_alignment'
    size_multiple = 'model_config_size_multiple'
    action_count = 'model_variables_trace_action_count'
    allocator_identifier = 'model_variables_allocator_identifier'

class TraceFilterOptions(Enum):
    id = 'trace_identifier'
    group_id = 'trace_group_identifier'
    size = 'trace_size_allocation_actions'

class HashesFilterOptions(Enum):
    trace = 'hash_trace'
    query = 'hash_query'

filter_type = Callable[[str, list[str]], bool]

# Filter class used to filter pandas dataframes.
class Filter:
    _valid_key_reference: set[str] = set()
    # Need to check whether we don't have duplicate filters for some reason, else {**d, ...} would've been fine.
    for enum in [QueryFilterOptions, ModelFilterOptions, TraceFilterOptions]:
        for e in enum:
            _valid_key_reference.add(e.value)

    def __init__(self):
        self._keys: dict[str, list[str]] = {}
        self._rules: dict[str, list[Callable[[str, list[str]], bool]]] = {}
        self._default_key_rule: Callable[..., bool] = self._default_filter_fn
    
    def add_rules(self, key:str, rules: list[filter_type], values: list[str]):
        rules = [self._default_key_rule] if not rules else rules.copy()
        values = list() if not values else values.copy()

        if key not in self._valid_key_reference:
            return False

        if key not in self._keys:
            self._keys[key] = values
            self._rules[key] = rules
        else:
            self._rules[key] += rules
            self._keys[key] += values
        return True

    def merge(self, filters: list[Self]):
        for f in filters:
            for k in f._keys.keys():
                if k in self._keys:
                    self._keys[k] += f._keys[k]
                    self._rules[k] += f._rules[k]
                else:
                    self._keys[k] = f._keys[k].copy()
                    self._rules[k] = f._rules[k].copy()
        return self
    
    def apply(self, df: pd.DataFrame) -> pd.DataFrame:
        condition = np.ones(len(df), dtype=bool)

        for k, v in self._keys.items():
            condition &= df[k].apply(lambda x: all([f(x, v) for f in self._rules[k]]))
        
        return df[condition]
    
    def clone(self):
        filter = Filter()
        filter.merge([self])
        return filter

    @staticmethod    
    def _default_filter_fn(x, values: list[str]) -> bool:
        return any([v in x for v in values])
    
def get_query_data(df_query_search: pd.DataFrame, df_query_data: pd.DataFrame) -> pd.DataFrame:
    hashes = df_query_search[HashesFilterOptions.query]
    return df_query_data[df_query_data[HashesFilterOptions.query].loc[hashes]]
        
        
