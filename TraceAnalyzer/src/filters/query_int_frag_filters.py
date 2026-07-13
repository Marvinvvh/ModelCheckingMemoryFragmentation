from typing import Final
import filters.filters as fl
from common_constants import IntFragConstants, IntFragUtilConstants
from .filter_types import FILTER_TYPE_EXACT

QUERY_FILTER_FRAG_INT_WJ_AVERAGE: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_INT_WJ_AVERAGE.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    IntFragConstants.all_WJ_avg()
)

QUERY_FILTER_FRAG_INT_WJ_REQUIRED: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_INT_WJ_REQUIRED.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    IntFragConstants.all_WJ_required()
)

QUERY_FILTER_FRAG_INT_WJ_RESERVED: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_INT_WJ_RESERVED.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    IntFragConstants.all_WJ_reserved()
)

QUERY_FILTER_FRAG_INT_WJ_MAX: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_INT_WJ_MAX.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [IntFragConstants.WJ_max.value]
)

QUERY_FILTER_FRAG_INT_RELATIVE: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_INT_RELATIVE.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [IntFragConstants.relative.value]
)

QUERY_FILTER_FRAG_INT_ALL_METRICS: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_INT_ALL_METRICS.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    IntFragConstants.all_metrics()
)

QUERY_FILTER_FRAG_INT_UTIL: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FRAG_INT_UTIL.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    IntFragUtilConstants.all_util()
)
