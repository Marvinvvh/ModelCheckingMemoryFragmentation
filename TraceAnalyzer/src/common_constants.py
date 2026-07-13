from enum import Enum, auto

class MemoryUsageConstants(Enum):
    live_requested_memory = 'live_requested_memory'
    live_allocated_memory = 'live_allocated_memory'
    live_padded_memory = 'live_padded_memory'
    live_page_memory = 'live_page_memory'
    total_requested_memory = 'total_requested_memory'
    total_allocated_memory = 'total_allocated_memory'
    total_padded_memory = 'total_padded_memory'
    free_memory_BV = 'free_memory_BV'
    free_memory_PV = 'free_memory_PV'
    free_memory_HV = 'free_memory_HV'
    LFB_BV = 'LFB_BV'
    LFB_PV = 'LFB_PV'
    LFB_HV = 'LFB_HV'

    @staticmethod
    def live_memory():
        return [
            MemoryUsageConstants.live_requested_memory.value,
            MemoryUsageConstants.live_allocated_memory.value,
            MemoryUsageConstants.live_padded_memory.value,
            MemoryUsageConstants.live_page_memory.value,
        ]
    
    @staticmethod
    def total_memory():
        return [
            MemoryUsageConstants.total_requested_memory.value,
            MemoryUsageConstants.total_allocated_memory.value,
            MemoryUsageConstants.total_padded_memory.value,
        ]

    @staticmethod
    def free_memory():
        return [
            MemoryUsageConstants.free_memory_BV.value,
            MemoryUsageConstants.free_memory_PV.value,
            MemoryUsageConstants.free_memory_HV.value,
        ]

    @staticmethod
    def largest_free_block():
        return [
            MemoryUsageConstants.LFB_BV.value,
            MemoryUsageConstants.LFB_PV.value,
            MemoryUsageConstants.LFB_HV.value,
        ]

class MemoryExtremesConstants(Enum):
    min_allocated_size = "min_allocated_size"
    max_allocated_size = "max_allocated_size"
    min_requested_size = "min_requested_size"
    max_requested_size = "max_requested_size"
    min_padded_size = "min_padded_size"
    max_padded_size = "max_padded_size"
    max_allocated_memory = "max_allocated_memory"
    max_requested_memory = "max_requested_memory"
    max_padded_memory = "max_padded_memory"
    max_page_memory = "max_page_memory"

    @staticmethod
    def max_memory():
        return [
            MemoryExtremesConstants.max_requested_memory.value,
            MemoryExtremesConstants.max_allocated_memory.value,
            MemoryExtremesConstants.max_padded_memory.value,
            MemoryExtremesConstants.max_page_memory.value,
        ]

    @staticmethod
    def max_size():
        return [
            MemoryExtremesConstants.max_requested_size.value,
            MemoryExtremesConstants.max_allocated_size.value,
            MemoryExtremesConstants.max_padded_size.value,
        ]

    @staticmethod
    def min_size():
        return [
            MemoryExtremesConstants.min_requested_size.value,
            MemoryExtremesConstants.min_allocated_size.value,
            MemoryExtremesConstants.min_padded_size.value,
        ]

class ActionConstants(Enum):
    action_count = "action_count"
    alloc_action_count = "alloc_action_count"
    free_action_count = "free_action_count"

class StateConstants(Enum):
    trace_done = "trace_done"
    trace_error = "trace_error"
    allocator_error = "allocator_error"
    heap_error = "heap_error"

class IntFragUtilConstants(Enum):

    WJ_required_max_req_mem = "frag_int_WJ_required_max_req_mem"
    WJ_required_max_res_mem_upper = "frag_int_WJ_required_max_res_mem_upper"
    WJ_required_max_res_mem_lower = "frag_int_WJ_required_max_res_mem_lower"
    WJ_required_peak_count = "frag_int_WJ_required_peak_count"
    WJ_required_action_count_upper = "frag_int_WJ_required_action_count_upper"
    WJ_required_action_count_lower = "frag_int_WJ_required_action_count_lower"
    WJ_reserved_max_res_mem = "frag_int_WJ_reserved_max_res_mem"
    WJ_reserved_max_req_mem_upper = "frag_int_WJ_reserved_max_req_mem_upper"
    WJ_reserved_max_req_mem_lower = "frag_int_WJ_reserved_max_req_mem_lower"
    WJ_reserved_peak_count = "frag_int_WJ_reserved_peak_count"
    WJ_reserved_action_count_upper = "frag_int_WJ_reserved_action_count_upper"
    WJ_reserved_action_count_lower = "frag_int_WJ_reserved_action_count_lower"

    @staticmethod
    def all_util():
        return [
            IntFragUtilConstants.WJ_required_max_req_mem.value,
            IntFragUtilConstants.WJ_required_max_res_mem_upper.value,
            IntFragUtilConstants.WJ_required_max_res_mem_lower.value,
            IntFragUtilConstants.WJ_required_peak_count.value,
            IntFragUtilConstants.WJ_required_action_count_upper.value,
            IntFragUtilConstants.WJ_required_action_count_lower.value,
            IntFragUtilConstants.WJ_reserved_max_res_mem.value,
            IntFragUtilConstants.WJ_reserved_max_req_mem_upper.value,
            IntFragUtilConstants.WJ_reserved_max_req_mem_lower.value,
            IntFragUtilConstants.WJ_reserved_peak_count.value,
            IntFragUtilConstants.WJ_reserved_action_count_upper.value,
            IntFragUtilConstants.WJ_reserved_action_count_lower.value,
        ]


class IntFragConstants(Enum):
    WJ_average_any = "frag_int_WJ_avg_any"
    WJ_average_alloc = "frag_int_WJ_avg_alloc"
    WJ_required_avg = "frag_int_WJ_required_avg"
    WJ_required_upper = "frag_int_WJ_required_upper"
    WJ_required_lower = "frag_int_WJ_required_lower"
    WJ_reserved_avg = "frag_int_WJ_reserved_avg"
    WJ_reserved_upper = "frag_int_WJ_reserved_upper"
    WJ_reserved_lower = "frag_int_WJ_reserved_lower"
    WJ_max = "frag_int_WJ_max"
    relative = "frag_int_relative"

    @staticmethod 
    def all_WJ_avg():
        return [
            IntFragConstants.WJ_average_any.value,
            IntFragConstants.WJ_average_alloc.value,
        ]

    @staticmethod
    def all_WJ_reserved():
        return [
            IntFragConstants.WJ_reserved_avg.value,
            IntFragConstants.WJ_reserved_upper.value,
            IntFragConstants.WJ_reserved_lower.value,
        ]

    @staticmethod
    def all_WJ_required():
        return [
            IntFragConstants.WJ_required_avg.value,
            IntFragConstants.WJ_required_upper.value,
        ]

    @staticmethod
    def all_WJ():
        return [
            *IntFragConstants.all_WJ_avg(),
            *IntFragConstants.all_WJ_required(),
            *IntFragConstants.all_WJ_reserved(),
            IntFragConstants.WJ_max.value
        ]

    @staticmethod
    def all_metrics():
        return [
            *IntFragConstants.all_WJ(),
            IntFragConstants.relative.value,
        ]

class ExtFragUtilConstants(Enum):

    WJ_req_required_max_req_mem = "frag_ext_WJ_req_required_max_req_mem"
    WJ_req_required_max_res_mem_upper = "frag_ext_WJ_req_required_max_res_mem_upper"
    WJ_req_required_max_res_mem_lower = "frag_ext_WJ_req_required_max_res_mem_lower"
    WJ_req_required_peak_count = "frag_ext_WJ_req_required_peak_count"
    WJ_req_required_action_count_upper = "frag_ext_WJ_req_required_action_count_upper"
    WJ_req_required_action_count_lower = "frag_ext_WJ_req_required_action_count_lower"
    WJ_req_reserved_max_res_mem = "frag_ext_WJ_req_reserved_max_res_mem"
    WJ_req_reserved_max_req_mem_upper = "frag_ext_WJ_req_reserved_max_req_mem_upper"
    WJ_req_reserved_max_req_mem_lower = "frag_ext_WJ_req_reserved_max_req_mem_lower"
    WJ_req_reserved_peak_count = "frag_ext_WJ_req_reserved_peak_count"
    WJ_req_reserved_action_count_upper = "frag_ext_WJ_req_reserved_action_count_upper"
    WJ_req_reserved_action_count_lower = "frag_ext_WJ_req_reserved_action_count_lower"

    WJ_alloc_required_max_req_mem = "frag_ext_WJ_alloc_required_max_req_mem"
    WJ_alloc_required_max_res_mem_upper = "frag_ext_WJ_alloc_required_max_res_mem_upper"
    WJ_alloc_required_max_res_mem_lower = "frag_ext_WJ_alloc_required_max_res_mem_lower"
    WJ_alloc_required_peak_count = "frag_ext_WJ_alloc_required_peak_count"
    WJ_alloc_required_action_count_upper = "frag_ext_WJ_alloc_required_action_count_upper"
    WJ_alloc_required_action_count_lower = "frag_ext_WJ_alloc_required_action_count_lower"
    WJ_alloc_reserved_max_res_mem = "frag_ext_WJ_alloc_reserved_max_res_mem"
    WJ_alloc_reserved_max_req_mem_upper = "frag_ext_WJ_alloc_reserved_max_req_mem_upper"
    WJ_alloc_reserved_max_req_mem_lower = "frag_ext_WJ_alloc_reserved_max_req_mem_lower"
    WJ_alloc_reserved_peak_count = "frag_ext_WJ_alloc_reserved_peak_count"
    WJ_alloc_reserved_action_count_upper = "frag_ext_WJ_alloc_reserved_action_count_upper"
    WJ_alloc_reserved_action_count_lower = "frag_ext_WJ_alloc_reserved_action_count_lower"

    @staticmethod
    def all_req_util():
        return [
            ExtFragUtilConstants.WJ_req_required_max_req_mem.value,
            ExtFragUtilConstants.WJ_req_required_max_res_mem_upper.value,
            ExtFragUtilConstants.WJ_req_required_max_res_mem_lower.value,
            ExtFragUtilConstants.WJ_req_required_peak_count.value,
            ExtFragUtilConstants.WJ_req_required_action_count_upper.value,
            ExtFragUtilConstants.WJ_req_required_action_count_lower.value,
            ExtFragUtilConstants.WJ_req_reserved_max_res_mem.value,
            ExtFragUtilConstants.WJ_req_reserved_max_req_mem_upper.value,
            ExtFragUtilConstants.WJ_req_reserved_max_req_mem_lower.value,
            ExtFragUtilConstants.WJ_req_reserved_peak_count.value,
            ExtFragUtilConstants.WJ_req_reserved_action_count_upper.value,
            ExtFragUtilConstants.WJ_req_reserved_action_count_lower.value,
        ]

    @staticmethod
    def all_alloc_util():
        return [
            ExtFragUtilConstants.WJ_alloc_required_max_req_mem.value,
            ExtFragUtilConstants.WJ_alloc_required_max_res_mem_upper.value,
            ExtFragUtilConstants.WJ_alloc_required_max_res_mem_lower.value,
            ExtFragUtilConstants.WJ_alloc_required_peak_count.value,
            ExtFragUtilConstants.WJ_alloc_required_action_count_upper.value,
            ExtFragUtilConstants.WJ_alloc_required_action_count_lower.value,
            ExtFragUtilConstants.WJ_alloc_reserved_max_res_mem.value,
            ExtFragUtilConstants.WJ_alloc_reserved_max_req_mem_upper.value,
            ExtFragUtilConstants.WJ_alloc_reserved_max_req_mem_lower.value,
            ExtFragUtilConstants.WJ_alloc_reserved_peak_count.value,
            ExtFragUtilConstants.WJ_alloc_reserved_action_count_upper.value,
            ExtFragUtilConstants.WJ_alloc_reserved_action_count_lower.value,
        ]
    
    @staticmethod
    def all_util():
        return [
            *ExtFragUtilConstants.all_req_util(),
            *ExtFragUtilConstants.all_alloc_util(),
        ]


class ExtFragConstants(Enum):
    WJ_req_average_any = "frag_ext_WJ_req_avg_any"
    WJ_req_average_alloc = "frag_ext_WJ_req_avg_alloc"
    WJ_req_required_avg = "frag_ext_WJ_req_required_avg"
    WJ_req_required_upper = "frag_ext_WJ_req_required_upper"
    WJ_req_required_lower = "frag_ext_WJ_req_required_lower"
    WJ_req_reserved_avg = "frag_ext_WJ_req_reserved_avg"
    WJ_req_reserved_upper = "frag_ext_WJ_req_reserved_upper"
    WJ_req_reserved_lower = "frag_ext_WJ_req_reserved_lower"
    WJ_req_max = "frag_ext_WJ_req_max"

    WJ_alloc_average_any = "frag_ext_WJ_alloc_avg_any"
    WJ_alloc_average_alloc = "frag_ext_WJ_alloc_avg_alloc"
    WJ_alloc_required_avg = "frag_ext_WJ_alloc_required_avg"
    WJ_alloc_required_upper = "frag_ext_WJ_alloc_required_upper"
    WJ_alloc_required_lower = "frag_ext_WJ_alloc_required_lower"
    WJ_alloc_reserved_avg = "frag_ext_WJ_alloc_reserved_avg"
    WJ_alloc_reserved_upper = "frag_ext_WJ_alloc_reserved_upper"
    WJ_alloc_reserved_lower = "frag_ext_WJ_alloc_reserved_lower"
    WJ_alloc_max = "frag_ext_WJ_alloc_max"
    # TODO: Change this to frag_ext_LFB instead of frag_LFB
    LFB_BV = "frag_ext_LFB_BV"
    LFB_PV = "frag_ext_LFB_PV"
    LFB_HV = "frag_ext_LFB_HV"
    Relative_BV = "frag_ext_rel_BV"
    Relative_PV = "frag_ext_rel_PV"
    Relative_HV = "frag_ext_rel_HV"
    

    @staticmethod 
    def all_WJ_req_avg():
        return [
            ExtFragConstants.WJ_req_average_any.value,
            ExtFragConstants.WJ_req_average_alloc.value,
        ]

    @staticmethod
    def all_WJ_req_reserved():
        return [
            ExtFragConstants.WJ_req_reserved_avg.value,
            ExtFragConstants.WJ_req_reserved_upper.value,
            ExtFragConstants.WJ_req_reserved_lower.value,
        ]

    @staticmethod
    def all_WJ_req_required():
        return [
            ExtFragConstants.WJ_req_required_avg.value,
            ExtFragConstants.WJ_req_required_upper.value,
            ExtFragConstants.WJ_req_required_lower.value,
        ]

    @staticmethod
    def all_WJ_req():
        return [
            *ExtFragConstants.all_WJ_req_avg(),
            *ExtFragConstants.all_WJ_req_required(),
            *ExtFragConstants.all_WJ_req_reserved(),
            ExtFragConstants.WJ_req_max.value
        ]

    @staticmethod 
    def all_WJ_alloc_avg():
        return [
            ExtFragConstants.WJ_alloc_average_any.value,
            ExtFragConstants.WJ_alloc_average_alloc.value,
        ]

    @staticmethod
    def all_WJ_alloc_reserved():
        return [
            ExtFragConstants.WJ_alloc_reserved_avg.value,
            ExtFragConstants.WJ_alloc_reserved_upper.value,
            ExtFragConstants.WJ_alloc_reserved_lower.value,
        ]

    @staticmethod
    def all_WJ_alloc_required():
        return [
            ExtFragConstants.WJ_alloc_required_avg.value,
            ExtFragConstants.WJ_alloc_required_upper.value,
            ExtFragConstants.WJ_alloc_required_lower.value,
        ]

    @staticmethod
    def all_WJ_alloc():
        return [
            *ExtFragConstants.all_WJ_alloc_avg(),
            *ExtFragConstants.all_WJ_alloc_required(),
            *ExtFragConstants.all_WJ_alloc_reserved(),
            ExtFragConstants.WJ_alloc_max.value
        ]
    

    @staticmethod
    def all_relative():
        return [
            ExtFragConstants.Relative_BV.value,
            ExtFragConstants.Relative_PV.value,
            ExtFragConstants.Relative_HV.value,
        ]

    @staticmethod
    def all_LFB():
        return [
            ExtFragConstants.LFB_BV.value,
            ExtFragConstants.LFB_PV.value,
            ExtFragConstants.LFB_HV.value,
        ]

    @staticmethod
    def all_metrics():
        return [
            *ExtFragConstants.all_WJ_req(),
            *ExtFragConstants.all_WJ_alloc(),
            *ExtFragConstants.all_LFB(),
            *ExtFragConstants.all_relative()
        ]

class ModelConfigConstants(Enum):
    config_id_FFAO = 'FFAO'
    config_id_NFAO = 'NFAO'
    config_id_BFAO = 'BFAO'

    page_size = 'model_config_page_size'
    total_memory_size = 'model_config_total_memory_size'
    amount_of_pages = 'model_config_amount_of_pages'
    size_multiple = 'model_config_size_multiple'
    address_alignment = 'model_config_address_alignment'
    allocator_count = 'model_config_allocator_count'


    @staticmethod
    def all_model_config_ids():
        return [
            ModelConfigConstants.config_id_FFAO.value,
            ModelConfigConstants.config_id_NFAO.value,
            ModelConfigConstants.config_id_BFAO.value,
        ]
    
class ModelVariablesConstants(Enum):
    allocator_identifier = 'model_variables_allocator_identifier'
    trace_action_count = 'model_variables_trace_action_count'

class TraceConstants(Enum):
    tg_s_ttl_cache = 'synth_ttl_cache'
    tg_s_cooperating_threads = 'synth_cooperating_threads'
    tg_s_batch_buffer = 'synth_batch_buffer'
    tg_s_sim = 'synth_sim'
    tg_s_random_small = 'synth_randomized_smaller_spread'
    tg_s_random_even = 'synth_randomized_even_spread'
    tg_s_stack = 'synth_stack'
    tg_p_larson = 'program_larson'
    tg_p_sh6bench = 'program_sh6bench'
    tg_p_cfrac = 'program_cfrac'
    tg_s_fragverif = 'fragverif_trace'

    @staticmethod
    def all_trace_groups():
        return [
            TraceConstants.tg_s_ttl_cache.value,
            TraceConstants.tg_s_cooperating_threads.value,
            TraceConstants.tg_s_batch_buffer.value,
            TraceConstants.tg_s_sim.value,
            TraceConstants.tg_s_random_small.value,
            TraceConstants.tg_s_random_even.value,
            TraceConstants.tg_s_stack.value,
            TraceConstants.tg_p_larson.value,
            TraceConstants.tg_p_sh6bench.value,
            TraceConstants.tg_p_cfrac.value,
        ]

