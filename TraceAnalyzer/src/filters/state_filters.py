from typing import Final
import filters.filters as fl
from common_constants import StateConstants
from .filter_types import FILTER_TYPE_EXACT

QUERY_FILTER_TRACE_DONE: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_TRACE_DONE.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [StateConstants.trace_done.value]
)

QUERY_FILTER_TRACE_ERROR: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_TRACE_ERROR.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [StateConstants.trace_error.value]
)

QUERY_FILTER_ALLOCATOR_ERROR: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_ALLOCATOR_ERROR.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [StateConstants.allocator_error.value]
)

QUERY_FILTER_HEAP_ERROR: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_HEAP_ERROR.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [StateConstants.heap_error.value]
)