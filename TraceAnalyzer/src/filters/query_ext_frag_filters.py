from typing import Final
import filters.filters as fl
from common_constants import ExtFragConstants, ExtFragUtilConstants
from .filter_types import FILTER_TYPE_EXACT

QUERY_FILTER_FRAG_EXT_WJ_REQ_AVERAGE: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_EXT_WJ_REQ_AVERAGE.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    ExtFragConstants.all_WJ_req_avg()
)

QUERY_FILTER_FRAG_EXT_WJ_REQ_REQUIRED: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_EXT_WJ_REQ_REQUIRED.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    ExtFragConstants.all_WJ_req_required()
)

QUERY_FILTER_FRAG_EXT_WJ_REQ_RESERVED: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_EXT_WJ_REQ_RESERVED.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    ExtFragConstants.all_WJ_req_reserved()
)

QUERY_FILTER_FRAG_EXT_WJ_REQ_MAX: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_EXT_WJ_REQ_MAX.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [ExtFragConstants.WJ_req_max.value]
)

QUERY_FILTER_FRAG_EXT_WJ_ALLOC_AVERAGE: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_EXT_WJ_ALLOC_AVERAGE.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    ExtFragConstants.all_WJ_alloc_avg()
)

QUERY_FILTER_FRAG_EXT_WJ_ALLOC_REQUIRED: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_EXT_WJ_ALLOC_REQUIRED.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    ExtFragConstants.all_WJ_alloc_required()
)

QUERY_FILTER_FRAG_EXT_WJ_ALLOC_RESERVED: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_EXT_WJ_ALLOC_RESERVED.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    ExtFragConstants.all_WJ_alloc_reserved()
)

QUERY_FILTER_FRAG_EXT_WJ_ALLOC_MAX: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_EXT_WJ_ALLOC_MAX.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [ExtFragConstants.WJ_alloc_max.value]
)

QUERY_FILTER_FRAG_LFB_BV: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_LFB_BV.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [ExtFragConstants.LFB_BV.value]
)

QUERY_FILTER_FRAG_LFB_PV: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_LFB_PV.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [ExtFragConstants.LFB_PV.value]
)

QUERY_FILTER_FRAG_LFB_HV: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_LFB_HV.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [ExtFragConstants.LFB_HV.value]
)

QUERY_FILTER_FRAG_EXT_ALL_METRICS: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_EXT_ALL_METRICS.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    ExtFragConstants.all_metrics()
)

QUERY_FILTER_FRAG_EXT_UTIL: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_EXT_UTIL.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    ExtFragUtilConstants.all_util()
)
