from typing import Final
import filters.filters as fl
from .filter_types import FILTER_TYPE_IN_COLUMN, FILTER_TYPE_EXACT
from common_constants import TraceConstants

TRACE_FILTER_SYNTH_TTL_CACHE: Final[fl.Filter] = fl.Filter()
TRACE_FILTER_SYNTH_TTL_CACHE.add_rules(
    fl.TraceFilterOptions.group_id.value,
    [FILTER_TYPE_IN_COLUMN],
    [TraceConstants.tg_s_ttl_cache.value]
)

TRACE_FILTER_SYNTH_COOP_THREADS: Final[fl.Filter] = fl.Filter()
TRACE_FILTER_SYNTH_COOP_THREADS.add_rules(
    fl.TraceFilterOptions.group_id.value,
    [FILTER_TYPE_IN_COLUMN],
    [TraceConstants.tg_s_cooperating_threads.value]
)

TRACE_FILTER_SYNTH_BATCH_BUFFER: Final[fl.Filter] = fl.Filter()
TRACE_FILTER_SYNTH_BATCH_BUFFER.add_rules(
    fl.TraceFilterOptions.group_id.value,
    [FILTER_TYPE_IN_COLUMN],
    [TraceConstants.tg_s_batch_buffer.value]
)

TRACE_FILTER_SYNTH_SIM: Final[fl.Filter] = fl.Filter()
TRACE_FILTER_SYNTH_SIM.add_rules(
    fl.TraceFilterOptions.group_id.value,
    [FILTER_TYPE_IN_COLUMN],
    [TraceConstants.tg_s_sim.value]
)


TRACE_FILTER_FRAGVERIF: Final[fl.Filter] = fl.Filter()
TRACE_FILTER_FRAGVERIF.add_rules(
    fl.TraceFilterOptions.group_id.value,
    [FILTER_TYPE_IN_COLUMN],
    [TraceConstants.tg_s_fragverif.value]
)

TRACE_FILTER_SYNTH_RANDOM_SMALL: Final[fl.Filter] = fl.Filter()
TRACE_FILTER_SYNTH_RANDOM_SMALL.add_rules(
    fl.TraceFilterOptions.group_id.value,
    [FILTER_TYPE_IN_COLUMN],
    [TraceConstants.tg_s_random_small.value]
)

TRACE_FILTER_SYNTH_RANDOM_EVEN: Final[fl.Filter] = fl.Filter()
TRACE_FILTER_SYNTH_RANDOM_EVEN.add_rules(
    fl.TraceFilterOptions.group_id.value,
    [FILTER_TYPE_IN_COLUMN],
    [TraceConstants.tg_s_random_even.value]
)

TRACE_FILTER_SYNTH_STACK: Final[fl.Filter] = fl.Filter()
TRACE_FILTER_SYNTH_STACK.add_rules(
    fl.TraceFilterOptions.group_id.value,
    [FILTER_TYPE_IN_COLUMN],
    [TraceConstants.tg_s_stack.value]
)

TRACE_FILTER_PROGRAM_LARSON: Final[fl.Filter] = fl.Filter()
TRACE_FILTER_PROGRAM_LARSON.add_rules(
    fl.TraceFilterOptions.group_id.value,
    [FILTER_TYPE_IN_COLUMN],
    [TraceConstants.tg_p_larson.value]
)

TRACE_FILTER_PROGRAM_SH6BENCH: Final[fl.Filter] = fl.Filter()
TRACE_FILTER_PROGRAM_SH6BENCH.add_rules(
    fl.TraceFilterOptions.group_id.value,
    [FILTER_TYPE_IN_COLUMN],
    [TraceConstants.tg_p_sh6bench.value]
)

TRACE_FILTER_PROGRAM_CFRAC: Final[fl.Filter] = fl.Filter()
TRACE_FILTER_PROGRAM_CFRAC.add_rules(
    fl.TraceFilterOptions.group_id.value,
    [FILTER_TYPE_IN_COLUMN],
    [TraceConstants.tg_p_cfrac.value]
)

TRACE_FILTER_ALL_SYNTH: Final[fl.Filter] = fl.Filter()
TRACE_FILTER_ALL_SYNTH.merge([
    TRACE_FILTER_SYNTH_TTL_CACHE,
    TRACE_FILTER_SYNTH_COOP_THREADS,
    TRACE_FILTER_SYNTH_BATCH_BUFFER,
    TRACE_FILTER_SYNTH_SIM,
    TRACE_FILTER_SYNTH_RANDOM_SMALL,
    TRACE_FILTER_SYNTH_RANDOM_EVEN,
    TRACE_FILTER_SYNTH_STACK
])

TRACE_FILTER_ALL_PROGRAM: Final[fl.Filter] = fl.Filter()
TRACE_FILTER_ALL_PROGRAM.merge([
    TRACE_FILTER_PROGRAM_LARSON,
    TRACE_FILTER_PROGRAM_SH6BENCH,
    TRACE_FILTER_PROGRAM_CFRAC
])

TRACE_FILTER_ALL: Final[fl.Filter] = fl.Filter()
TRACE_FILTER_ALL.merge([
    TRACE_FILTER_ALL_SYNTH,
    TRACE_FILTER_ALL_PROGRAM,
])

def get_tg(names: list[str]):
    filter = fl.Filter()
    for name in names:
        name_filter = fl.Filter()
        name_filter.add_rules(
            fl.TraceFilterOptions.group_id.value,
            [FILTER_TYPE_IN_COLUMN],
            [name]
        )
        filter.merge([name_filter])
    return filter

def get_trace(names: list[str]):
    filter = fl.Filter()
    for name in names:
        name_filter = fl.Filter()
        name_filter.add_rules(
            fl.TraceFilterOptions.id.value,
            [FILTER_TYPE_IN_COLUMN],
            [name]
        )
        filter.merge([name_filter])
    return filter

def get_trace_num(nums: list[str]):
    filter = fl.Filter()
    for num in nums:
        num_filter = fl.Filter()
        num_filter.add_rules(
            fl.TraceFilterOptions.id.value,
            [FILTER_TYPE_IN_COLUMN],
            [num]
        )
        filter.merge([num_filter])
    return filter

def get_time(time: int):
    filter = fl.Filter()
    filter.add_rules(
        fl.QueryFilterOptions.time.value,
        [FILTER_TYPE_EXACT],
        [str(time)]
    )

    return filter
