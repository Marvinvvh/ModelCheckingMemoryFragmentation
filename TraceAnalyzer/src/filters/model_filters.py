from typing import Final
import filters.filters as fl
from common_constants import ModelConfigConstants
from .filter_types import FILTER_TYPE_IN_COLUMN, FILTER_TYPE_EXACT

# COMMON MODEL RULES
MODEL_FILTER_POLICY_FFAO: Final[fl.Filter] = fl.Filter()
MODEL_FILTER_POLICY_FFAO.add_rules(
    fl.ModelFilterOptions.allocator_identifier.value,
    [FILTER_TYPE_IN_COLUMN],
    [ModelConfigConstants.config_id_FFAO.value])

MODEL_FILTER_POLICY_NFAO: Final[fl.Filter] = fl.Filter()
MODEL_FILTER_POLICY_NFAO.add_rules(
    fl.ModelFilterOptions.allocator_identifier.value,
    [FILTER_TYPE_IN_COLUMN],
    [ModelConfigConstants.config_id_NFAO.value])

MODEL_FILTER_POLICY_BFAO: Final[fl.Filter] = fl.Filter()
MODEL_FILTER_POLICY_BFAO.add_rules(
    fl.ModelFilterOptions.allocator_identifier.value,
    [FILTER_TYPE_IN_COLUMN],
    [ModelConfigConstants.config_id_BFAO.value])

MODEL_FILTER_SIZE_MULT_1: Final[fl.Filter] = fl.Filter()
MODEL_FILTER_SIZE_MULT_1.add_rules(
    fl.ModelFilterOptions.size_multiple.value,
    [FILTER_TYPE_EXACT],
    [str(1)]
)

MODEL_FILTER_SIZE_MULT_16: Final[fl.Filter] = fl.Filter()
MODEL_FILTER_SIZE_MULT_16.add_rules(
    fl.ModelFilterOptions.size_multiple.value,
    [FILTER_TYPE_EXACT],
    [str(16)]
)

MODEL_FILTER_SIZE_MULT_64: Final[fl.Filter] = fl.Filter()
MODEL_FILTER_SIZE_MULT_64.add_rules(
    fl.ModelFilterOptions.size_multiple.value,
    [FILTER_TYPE_EXACT],
    [str(64)]
)

MODEL_FILTER_ALL_POLICIES: Final[fl.Filter] = fl.Filter()
MODEL_FILTER_ALL_POLICIES.merge(
    [
        MODEL_FILTER_POLICY_FFAO,
        MODEL_FILTER_POLICY_NFAO,
        MODEL_FILTER_POLICY_BFAO,
    ]
)

MODEL_FILTER_ALL_SIZE_MULTS: Final[fl.Filter] = fl.Filter()
MODEL_FILTER_ALL_SIZE_MULTS.merge(
    [
        MODEL_FILTER_SIZE_MULT_1,
        MODEL_FILTER_SIZE_MULT_16,
        MODEL_FILTER_SIZE_MULT_64,
    ]
)

MODEL_FILTER_TRACE_TRACKER: Final[fl.Filter] = fl.Filter()
MODEL_FILTER_TRACE_TRACKER.merge(
    [
        MODEL_FILTER_POLICY_FFAO,
        MODEL_FILTER_SIZE_MULT_1
    ]
)

def get_size_multiple(size_multiple: int):
    filter: Final[fl.Filter] = fl.Filter()
    filter.add_rules(
        fl.ModelFilterOptions.size_multiple.value,
        [FILTER_TYPE_EXACT],
        [str(size_multiple)]
    )
    return filter

