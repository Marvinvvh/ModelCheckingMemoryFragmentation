from typing import Final
import filters.filters as fl
from common_constants import ActionConstants
from .filter_types import FILTER_TYPE_IN_COLUMN, FILTER_TYPE_EXACT

# COMMON ACTIONS
QUERY_FILTER_ACTION_COUNT: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_ACTION_COUNT.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [ActionConstants.action_count.value]
)

QUERY_FILTER_ALLOC_ACTION_COUNT: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_ALLOC_ACTION_COUNT.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [ActionConstants.alloc_action_count.value]
)

QUERY_FILTER_FREE_ACTION_COUNT: Final[fl.Filter] = fl.Filter()
QUERY_FILTER_FREE_ACTION_COUNT.add_rules(
    fl.QueryFilterOptions.id.value,
    [FILTER_TYPE_EXACT],
    [ActionConstants.free_action_count.value]
)