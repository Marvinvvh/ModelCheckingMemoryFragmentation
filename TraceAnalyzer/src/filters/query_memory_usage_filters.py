from typing import Final
import filters.filters as fl
from common_constants import MemoryUsageConstants
from .filter_types import FILTER_TYPE_EXACT

QUERY_FILTER_LIVE_REQUESTED_MEMORY: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_LIVE_REQUESTED_MEMORY.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryUsageConstants.live_requested_memory.value]
)

QUERY_FILTER_LIVE_ALLOCATED_MEMORY: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_LIVE_ALLOCATED_MEMORY.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryUsageConstants.live_allocated_memory.value]
)

QUERY_FILTER_LIVE_PADDED_MEMORY: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_LIVE_PADDED_MEMORY.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryUsageConstants.live_padded_memory.value]
)

QUERY_FILTER_LIVE_PAGE_MEMORY: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_LIVE_PAGE_MEMORY.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryUsageConstants.live_page_memory.value]
)

QUERY_FILTER_TOTAL_REQUESTED_MEMORY: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_TOTAL_REQUESTED_MEMORY.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryUsageConstants.total_requested_memory.value]
)

QUERY_FILTER_TOTAL_ALLOCATED_MEMORY: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_TOTAL_ALLOCATED_MEMORY.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryUsageConstants.total_allocated_memory.value]
)

QUERY_FILTER_TOTAL_PADDED_MEMORY: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_TOTAL_PADDED_MEMORY.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryUsageConstants.total_padded_memory.value]
)

QUERY_FILTER_FREE_MEMORY_BV: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FREE_MEMORY_BV.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryUsageConstants.free_memory_BV.value]
)

QUERY_FILTER_FREE_MEMORY_PV: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FREE_MEMORY_PV.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryUsageConstants.free_memory_PV.value]
)

QUERY_FILTER_FREE_MEMORY_HV: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FREE_MEMORY_HV.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryUsageConstants.free_memory_HV.value]
)

QUERY_FILTER_LFB_BV: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_LFB_BV.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryUsageConstants.LFB_BV.value]
)

QUERY_FILTER_LFB_PV: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_LFB_PV.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryUsageConstants.LFB_PV.value]
)

QUERY_FILTER_LFB_HV: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_LFB_HV.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [MemoryUsageConstants.LFB_HV.value]
)

QUERY_FILTER_ALL_LIVE: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_ALL_LIVE.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    MemoryUsageConstants.live_memory()
)

QUERY_FILTER_ALL_TOTAL: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_ALL_TOTAL.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    MemoryUsageConstants.total_memory()
)

QUERY_FILTER_ALL_FREE: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_ALL_FREE.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    MemoryUsageConstants.free_memory()
)

QUERY_FILTER_ALL_LFB: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_ALL_LFB.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    MemoryUsageConstants.largest_free_block()
)




