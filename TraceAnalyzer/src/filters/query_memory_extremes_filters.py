from typing import Final
import filters.filters as fl
from common_constants import MemoryExtremesConstants
from .filter_types import FILTER_TYPE_EXACT

QUERY_FILTER_MIN_ALLOCATED_SIZE: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_MIN_ALLOCATED_SIZE.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryExtremesConstants.min_allocated_size.value]
)

QUERY_FILTER_MAX_ALLOCATED_SIZE: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_MAX_ALLOCATED_SIZE.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryExtremesConstants.max_allocated_size.value]
)

QUERY_FILTER_MIN_REQUESTED_SIZE: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_MIN_REQUESTED_SIZE.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryExtremesConstants.min_requested_size.value]
)

QUERY_FILTER_MAX_REQUESTED_SIZE: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_MAX_REQUESTED_SIZE.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryExtremesConstants.max_requested_size.value]
)

QUERY_FILTER_MIN_PADDED_SIZE: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_MIN_PADDED_SIZE.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryExtremesConstants.min_padded_size.value]
)

QUERY_FILTER_MAX_PADDED_SIZE: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_MAX_PADDED_SIZE.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryExtremesConstants.max_padded_size.value]
)

QUERY_FILTER_MAX_ALLOCATED_MEMORY: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_MAX_ALLOCATED_MEMORY.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryExtremesConstants.max_allocated_memory.value]
)

QUERY_FILTER_MAX_REQUESTED_MEMORY: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_MAX_REQUESTED_MEMORY.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryExtremesConstants.max_requested_memory.value]
)

QUERY_FILTER_MAX_PADDED_MEMORY: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_MAX_PADDED_MEMORY.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryExtremesConstants.max_padded_memory.value]
)

QUERY_FILTER_MAX_PAGE_MEMORY: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_MAX_PAGE_MEMORY.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryExtremesConstants.max_page_memory.value]
)

QUERY_FILTER_ALL_MAX_MEMORY: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_ALL_MAX_MEMORY.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    MemoryExtremesConstants.max_memory()
)

QUERY_FILTER_ALL_MAX_SIZE: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_ALL_MAX_SIZE.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    MemoryExtremesConstants.max_size()
)

QUERY_FILTER_ALL_MIN_SIZE: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_ALL_MIN_SIZE.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    MemoryExtremesConstants.min_size()
)