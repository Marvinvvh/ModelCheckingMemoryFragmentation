package org.TraceVerifier.Query.Properties.Tracking;

import org.TraceVerifier.Query.BaseTraceQuery;
import org.TraceVerifier.Query.SimulationTraceQuery;

public class TrackingExtFragProperties {
    public static BaseTraceQuery trackWJReqMethodAvgAny(int heapID) {
        String queryString = "frag_ext_WJ_req[" + heapID + "].frag_method_avg_any";
        String queryComment = "Fragmentation method for allocated memory: average fragmentation for all non-zero actions.";
        String queryIdentifier = "frag_ext_WJ_req_avg_any";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodAvgAlloc(int heapID) {
        String queryString = "frag_ext_WJ_req[" + heapID + "].frag_method_avg_alloc";
        String queryComment = "Fragmentation method for allocated memory: average fragmentation for only allocations.";
        String queryIdentifier = "frag_ext_WJ_req_avg_alloc";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodRequiredAvg(int heapID) {
        String queryString = "frag_ext_WJ_req[" + heapID + "].frag_method_required_avg";
        String queryComment = "Fragmentation method: required peak fragmentation averaged over peaks.";
        String queryIdentifier = "frag_ext_WJ_req_required_avg";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodReservedAvg(int heapID) {
        String queryString = "frag_ext_WJ_req[" + heapID + "].frag_method_reserved_avg";
        String queryComment = "Fragmentation method: reserved peak fragmentation averaged over peaks.";
        String queryIdentifier = "frag_ext_WJ_req_reserved_avg";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodRequiredUpper(int heapID) {
        String queryString = "frag_ext_WJ_req[" + heapID + "].frag_method_required_upper";
        String queryComment = "Fragmentation method: required peak fragmentation at best res peak.";
        String queryIdentifier = "frag_ext_WJ_req_required_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodReservedUpper(int heapID) {
        String queryString = "frag_ext_WJ_req[" + heapID + "].frag_method_reserved_upper";
        String queryComment = "Fragmentation method: reserved peak fragmentation at best req peak.";
        String queryIdentifier = "frag_ext_WJ_req_reserved_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodRequiredLower(int heapID) {
        String queryString = "frag_ext_WJ_req[" + heapID + "].frag_method_required_lower";
        String queryComment = "Fragmentation method: required peak fragmentation at worst res peak.";
        String queryIdentifier = "frag_ext_WJ_req_required_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodReservedLower(int heapID) {
        String queryString = "frag_ext_WJ_req[" + heapID + "].frag_method_reserved_lower";
        String queryComment = "Fragmentation method: reserved peak fragmentation at worst req peak.";
        String queryIdentifier = "frag_ext_WJ_req_reserved_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodMax(int heapID) {
        String queryString = "frag_ext_WJ_req[" + heapID + "].frag_method_max";
        String queryComment = "Fragmentation method for requested memory: max peak fragmentation";
        String queryIdentifier = "frag_ext_WJ_req_max";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodRequiredMaxReqMem(int heapID) {
        String queryString = "frag_ext_WJ_req_util[" + heapID + "].method_required_max_req_mem";
        String queryComment = "Fragmentation util: max requested memory for the required method.";
        String queryIdentifier = "frag_ext_WJ_req_required_max_req_mem";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodRequiredMaxResMemUpper(int heapID) {
        String queryString = "frag_ext_WJ_req_util[" + heapID + "].method_required_max_res_mem_upper";
        String queryComment = "Fragmentation util: reserved memory upper bound for the required method.";
        String queryIdentifier = "frag_ext_WJ_req_required_max_req_mem_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodRequiredMaxResMemLower(int heapID) {
        String queryString = "frag_ext_WJ_req_util[" + heapID + "].method_required_max_res_mem_lower";
        String queryComment = "Fragmentation util: reserved memory lower bound for the required method.";
        String queryIdentifier = "frag_ext_WJ_req_required_max_req_mem_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodRequiredPeakCount(int heapID) {
        String queryString = "frag_ext_WJ_req_util[" + heapID + "].method_required_peak_count";
        String queryComment = "Fragmentation util: Peak count for the max requested memory for the required method.";
        String queryIdentifier = "frag_ext_WJ_req_required_peak_count";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodRequiredActionCountUpper(int heapID) {
        String queryString = "frag_ext_WJ_req_util[" + heapID + "].method_required_action_count_upper";
        String queryComment = "Fragmentation util: Allocation count of the upper bound reserved memory for the required method.";
        String queryIdentifier = "frag_ext_WJ_req_required_action_count_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodRequiredActionCountLower(int heapID) {
        String queryString = "frag_ext_WJ_req_util[" + heapID + "].method_required_action_count_lower";
        String queryComment = "Fragmentation util: Allocation count of the lower bound reserved memory for the required method.";
        String queryIdentifier = "frag_ext_WJ_req_required_action_count_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodReservedMaxResMem(int heapID) {
        String queryString = "frag_ext_WJ_req_util[" + heapID + "].method_reserved_max_res_mem";
        String queryComment = "Fragmentation util: max reserved memory for the reserved method.";
        String queryIdentifier = "frag_ext_WJ_req_reserved_max_res_mem";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }
    public static BaseTraceQuery trackWJReqMethodReservedMaxReqMemUpper(int heapID) {
        String queryString = "frag_ext_WJ_req_util[" + heapID + "].method_reserved_max_req_mem_upper";
        String queryComment = "Fragmentation util: required memory upper bound for the reserved method.";
        String queryIdentifier = "frag_ext_WJ_req_reserved_max_req_mem_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }
    public static BaseTraceQuery trackWJReqMethodReservedMaxReqMemLower(int heapID) {
        String queryString = "frag_ext_WJ_req_util[" + heapID + "].method_reserved_max_req_mem_lower";
        String queryComment = "Fragmentation Util: required memory lower bound for the reserved method.";
        String queryIdentifier = "frag_ext_WJ_req_reserved_max_req_mem_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodReservedPeakCount(int heapID) {
        String queryString = "frag_ext_WJ_req_util[" + heapID + "].method_reserved_peak_count";
        String queryComment = "Fragmentation util: Peak count for the max requested memory for the reserved method.";
        String queryIdentifier = "frag_ext_WJ_req_reserved_peak_count";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodReservedActionCountUpper(int heapID) {
        String queryString = "frag_ext_WJ_req_util[" + heapID + "].method_reserved_action_count_upper";
        String queryComment = "Fragmentation util: Allocation count of the upper bound reserved memory for the reserved method.";
        String queryIdentifier = "frag_ext_WJ_req_reserved_action_count_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJReqMethodReservedActionCountLower(int heapID) {
        String queryString = "frag_ext_WJ_req_util[" + heapID + "].method_reserved_action_count_lower";
        String queryComment = "Fragmentation util: Allocation count of the lower bound reserved memory for the reserved method.";
        String queryIdentifier = "frag_ext_WJ_req_reserved_action_count_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodAvgAny(int heapID) {
        String queryString = "frag_ext_WJ_alloc[" + heapID + "].frag_method_avg_any";
        String queryComment = "Fragmentation method for allocated memory: average fragmentation for all non-zero actions.";
        String queryIdentifier = "frag_ext_WJ_alloc_avg_any";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodAvgAlloc(int heapID) {
        String queryString = "frag_ext_WJ_alloc[" + heapID + "].frag_method_avg_alloc";
        String queryComment = "Fragmentation method for allocated memory: average fragmentation for only allocations.";
        String queryIdentifier = "frag_ext_WJ_alloc_avg_alloc";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodRequiredAvg(int heapID) {
        String queryString = "frag_ext_WJ_alloc[" + heapID + "].frag_method_required_avg";
        String queryComment = "Fragmentation method: required peak fragmentation averaged over peaks.";
        String queryIdentifier = "frag_ext_WJ_alloc_required_avg";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodReservedAvg(int heapID) {
        String queryString = "frag_ext_WJ_alloc[" + heapID + "].frag_method_reserved_avg";
        String queryComment = "Fragmentation method: reserved peak fragmentation averaged over peaks.";
        String queryIdentifier = "frag_ext_WJ_alloc_reserved_avg";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodRequiredUpper(int heapID) {
        String queryString = "frag_ext_WJ_alloc[" + heapID + "].frag_method_required_upper";
        String queryComment = "Fragmentation method: required peak fragmentation at best res peak.";
        String queryIdentifier = "frag_ext_WJ_alloc_required_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodReservedUpper(int heapID) {
        String queryString = "frag_ext_WJ_alloc[" + heapID + "].frag_method_reserved_upper";
        String queryComment = "Fragmentation method: reserved peak fragmentation at best req peak.";
        String queryIdentifier = "frag_ext_WJ_alloc_reserved_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodRequiredLower(int heapID) {
        String queryString = "frag_ext_WJ_alloc[" + heapID + "].frag_method_required_lower";
        String queryComment = "Fragmentation method: required peak fragmentation at worst res peak.";
        String queryIdentifier = "frag_ext_WJ_alloc_required_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodReservedLower(int heapID) {
        String queryString = "frag_ext_WJ_alloc[" + heapID + "].frag_method_reserved_lower";
        String queryComment = "Fragmentation method: reserved peak fragmentation at worst req peak.";
        String queryIdentifier = "frag_ext_WJ_alloc_reserved_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodMax(int heapID) {
        String queryString = "frag_ext_WJ_alloc[" + heapID + "].frag_method_max";
        String queryComment = "Fragmentation method for allocated memory: max peak fragmentation";
        String queryIdentifier = "frag_ext_WJ_alloc_max";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodRequiredMaxReqMem(int heapID) {
        String queryString = "frag_ext_WJ_alloc_util[" + heapID + "].method_required_max_req_mem";
        String queryComment = "Fragmentation util: max requested memory for the required method.";
        String queryIdentifier = "frag_ext_WJ_alloc_required_max_req_mem";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }
    
    public static BaseTraceQuery trackWJAllocMethodRequiredMaxResMemUpper(int heapID) {
        String queryString = "frag_ext_WJ_alloc_util[" + heapID + "].method_required_max_res_mem_upper";
        String queryComment = "Fragmentation util: reserved memory upper bound for the required method.";
        String queryIdentifier = "frag_ext_WJ_alloc_required_max_req_mem_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodRequiredMaxResMemLower(int heapID) {
        String queryString = "frag_ext_WJ_alloc_util[" + heapID + "].method_required_max_res_mem_lower";
        String queryComment = "Fragmentation util: reserved memory lower bound for the required method.";
        String queryIdentifier = "frag_ext_WJ_alloc_required_max_req_mem_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodRequiredPeakCount(int heapID) {
        String queryString = "frag_ext_WJ_alloc_util[" + heapID + "].method_required_peak_count";
        String queryComment = "Fragmentation util: Peak count for the max requested memory for the required method.";
        String queryIdentifier = "frag_ext_WJ_alloc_required_peak_count";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodRequiredActionCountUpper(int heapID) {
        String queryString = "frag_ext_WJ_alloc_util[" + heapID + "].method_required_action_count_upper";
        String queryComment = "Fragmentation util: Allocation count of the upper bound reserved memory for the required method.";
        String queryIdentifier = "frag_ext_WJ_alloc_required_action_count_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodRequiredActionCountLower(int heapID) {
        String queryString = "frag_ext_WJ_alloc_util[" + heapID + "].method_required_action_count_lower";
        String queryComment = "Fragmentation util: Allocation count of the lower bound reserved memory for the required method.";
        String queryIdentifier = "frag_ext_WJ_alloc_required_action_count_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodReservedMaxResMem(int heapID) {
        String queryString = "frag_ext_WJ_alloc_util[" + heapID + "].method_reserved_max_res_mem";
        String queryComment = "Fragmentation util: max reserved memory for the reserved method.";
        String queryIdentifier = "frag_ext_WJ_alloc_reserved_max_res_mem";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodReservedMaxReqMemUpper(int heapID) {
        String queryString = "frag_ext_WJ_alloc_util[" + heapID + "].method_reserved_max_req_mem_upper";
        String queryComment = "Fragmentation util: required memory upper bound for the reserved method.";
        String queryIdentifier = "frag_ext_WJ_alloc_reserved_max_req_mem_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodReservedMaxReqMemLower(int heapID) {
        String queryString = "frag_ext_WJ_alloc_util[" + heapID + "].method_reserved_max_req_mem_lower";
        String queryComment = "Fragmentation Util: required memory lower bound for the reserved method.";
        String queryIdentifier = "frag_ext_WJ_alloc_reserved_max_req_mem_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodReservedPeakCount(int heapID) {
        String queryString = "frag_ext_WJ_alloc_util[" + heapID + "].method_reserved_peak_count";
        String queryComment = "Fragmentation util: Peak count for the max requested memory for the reserved method.";
        String queryIdentifier = "frag_ext_WJ_alloc_reserved_peak_count";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodReservedActionCountUpper(int heapID) {
        String queryString = "frag_ext_WJ_alloc_util[" + heapID + "].method_reserved_action_count_upper";
        String queryComment = "Fragmentation util: Allocation count of the upper bound reserved memory for the reserved method.";
        String queryIdentifier = "frag_ext_WJ_alloc_reserved_action_count_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJAllocMethodReservedActionCountLower(int heapID) {
        String queryString = "frag_ext_WJ_alloc_util[" + heapID + "].method_reserved_action_count_lower";
        String queryComment = "Fragmentation util: Allocation count of the lower bound reserved memory for the reserved method.";
        String queryIdentifier = "frag_ext_WJ_alloc_reserved_action_count_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }
    
    public static BaseTraceQuery trackLFBBV(int heapID) {
        String queryString = "frag_LFB_BV[" + heapID + "]";
        String queryComment = "Fragmentation of LFB compared to byte-view free memory.";
        String queryIdentifier = "frag_ext_LFB_BV";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackLFBPV(int heapID) {
        String queryString = "frag_LFB_PV[" + heapID + "]";
        String queryComment = "Fragmentation of LFB compared to page-view free memory.";
        String queryIdentifier = "frag_ext_LFB_PV";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackLFBHV(int heapID) {
        String queryString = "frag_LFB_HV[" + heapID + "]";
        String queryComment = "Fragmentation of LFB compared to heap-view free memory.";
        String queryIdentifier = "frag_ext_LFB_HV";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackRelativeExternalHV(int heapID) {
        String queryString = "frag_ext_relative_HV[" + heapID + "]";
        String queryComment = "External relative fragmentation for heap-view free memory.";
        String queryIdentifier = "frag_ext_rel_HV";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackRelativeExternalPV(int heapID) {
        String queryString = "frag_ext_relative_PV[" + heapID + "]";
        String queryComment = "External relative fragmentation for page-view free memory.";
        String queryIdentifier = "frag_ext_rel_PV";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackRelativeExternalBV(int heapID) {
        String queryString = "frag_ext_relative_BV[" + heapID + "]";
        String queryComment = "External relative fragmentation for byte-view free memory.";
        String queryIdentifier = "frag_ext_rel_BV";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }


}
