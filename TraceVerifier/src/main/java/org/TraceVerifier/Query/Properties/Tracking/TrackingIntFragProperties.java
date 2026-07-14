package org.TraceVerifier.Query.Properties.Tracking;

import org.TraceVerifier.Query.BaseTraceQuery;
import org.TraceVerifier.Query.SimulationTraceQuery;

public class TrackingIntFragProperties {
    public static BaseTraceQuery trackRelativeInternal(int heapID) {
        String queryString = "frag_int_relative[" + heapID + "]";
        String queryComment = "Relative internal fragmentation.";
        String queryIdentifier = "frag_int_relative";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodAvgAny(int heapID) {
        String queryString = "frag_int_WJ[" + heapID + "].frag_method_avg_any";
        String queryComment = "Fragmentation method for allocated memory: average fragmentation for all non-zero actions.";
        String queryIdentifier = "frag_int_WJ_avg_any";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodAvgAlloc(int heapID) {
        String queryString = "frag_int_WJ[" + heapID + "].frag_method_avg_alloc";
        String queryComment = "Fragmentation method for allocated memory: average fragmentation for only allocations.";
        String queryIdentifier = "frag_int_WJ_avg_alloc";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodRequiredAvg(int heapID) {
        String queryString = "frag_int_WJ[" + heapID + "].frag_method_required_avg";
        String queryComment = "Fragmentation method: required peak fragmentation averaged over peaks.";
        String queryIdentifier = "frag_int_WJ_required_avg";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodReservedAvg(int heapID) {
        String queryString = "frag_int_WJ[" + heapID + "].frag_method_reserved_avg";
        String queryComment = "Fragmentation method: reserved peak fragmentation averaged over peaks.";
        String queryIdentifier = "frag_int_WJ_reserved_avg";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodRequiredUpper(int heapID) {
        String queryString = "frag_int_WJ[" + heapID + "].frag_method_required_upper";
        String queryComment = "Fragmentation method: required peak fragmentation at best res peak.";
        String queryIdentifier = "frag_int_WJ_required_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodReservedUpper(int heapID) {
        String queryString = "frag_int_WJ[" + heapID + "].frag_method_reserved_upper";
        String queryComment = "Fragmentation method: reserved peak fragmentation at best req peak.";
        String queryIdentifier = "frag_int_WJ_reserved_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodRequiredLower(int heapID) {
        String queryString = "frag_int_WJ[" + heapID + "].frag_method_required_lower";
        String queryComment = "Fragmentation method: required peak fragmentation at worst res peak.";
        String queryIdentifier = "frag_int_WJ_required_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodReservedLower(int heapID) {
        String queryString = "frag_int_WJ[" + heapID + "].frag_method_reserved_lower";
        String queryComment = "Fragmentation method: reserved peak fragmentation at worst req peak.";
        String queryIdentifier = "frag_int_WJ_reserved_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodMax(int heapID) {
        String queryString = "frag_int_WJ[" + heapID + "].frag_method_max";
        String queryComment = "Fragmentation method: max peak fragmentation";
        String queryIdentifier = "frag_int_WJ_max";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodRequiredMaxReqMem(int heapID) {
        String queryString = "frag_int_WJ_util[" + heapID + "].method_required_max_req_mem";
        String queryComment = "Fragmentation util: max requested memory for the required method.";
        String queryIdentifier = "frag_int_WJ_required_max_req_mem";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodRequiredMaxResMemUpper(int heapID) {
        String queryString = "frag_int_WJ_util[" + heapID + "].method_required_max_res_mem_upper";
        String queryComment = "Fragmentation util: reserved memory upper bound for the required method.";
        String queryIdentifier = "frag_int_WJ_required_max_req_mem_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodRequiredMaxResMemLower(int heapID) {
        String queryString = "frag_int_WJ_util[" + heapID + "].method_required_max_res_mem_lower";
        String queryComment = "Fragmentation util: reserved memory lower bound for the required method.";
        String queryIdentifier = "frag_int_WJ_required_max_req_mem_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodRequiredPeakCount(int heapID) {
        String queryString = "frag_int_WJ_util[" + heapID + "].method_required_peak_count";
        String queryComment = "Fragmentation util: Peak count for the max requested memory for the required method.";
        String queryIdentifier = "frag_int_WJ_required_peak_count";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodRequiredActionCountUpper(int heapID) {
        String queryString = "frag_int_WJ_util[" + heapID + "].method_required_action_count_upper";
        String queryComment = "Fragmentation util: Allocation count of the upper bound reserved memory for the required method.";
        String queryIdentifier = "frag_int_WJ_required_action_count_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodRequiredActionCountLower(int heapID) {
        String queryString = "frag_int_WJ_util[" + heapID + "].method_required_action_count_lower";
        String queryComment = "Fragmentation util: Allocation count of the lower bound reserved memory for the required method.";
        String queryIdentifier = "frag_int_WJ_required_action_count_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodReservedMaxResMem(int heapID) {
        String queryString = "frag_int_WJ_util[" + heapID + "].method_reserved_max_res_mem";
        String queryComment = "Fragmentation util: max reserved memory for the reserved method.";
        String queryIdentifier = "frag_int_WJ_reserved_max_res_mem";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }
    public static BaseTraceQuery trackWJMethodReservedMaxReqMemUpper(int heapID) {
        String queryString = "frag_int_WJ_util[" + heapID + "].method_reserved_max_req_mem_upper";
        String queryComment = "Fragmentation util: required memory upper bound for the reserved method.";
        String queryIdentifier = "frag_int_WJ_reserved_max_req_mem_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }
    public static BaseTraceQuery trackWJMethodReservedMaxReqMemLower(int heapID) {
        String queryString = "frag_int_WJ_util[" + heapID + "].method_reserved_max_req_mem_lower";
        String queryComment = "Fragmentation Util: required memory lower bound for the reserved method.";
        String queryIdentifier = "frag_int_WJ_reserved_max_req_mem_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodReservedPeakCount(int heapID) {
        String queryString = "frag_int_WJ_util[" + heapID + "].method_reserved_peak_count";
        String queryComment = "Fragmentation util: Peak count for the max requested memory for the reserved method.";
        String queryIdentifier = "frag_int_WJ_reserved_peak_count";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodReservedActionCountUpper(int heapID) {
        String queryString = "frag_int_WJ_util[" + heapID + "].method_reserved_action_count_upper";
        String queryComment = "Fragmentation util: Allocation count of the upper bound reserved memory for the reserved method.";
        String queryIdentifier = "frag_int_WJ_reserved_action_count_upper";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }

    public static BaseTraceQuery trackWJMethodReservedActionCountLower(int heapID) {
        String queryString = "frag_int_WJ_util[" + heapID + "].method_reserved_action_count_lower";
        String queryComment = "Fragmentation util: Allocation count of the lower bound reserved memory for the reserved method.";
        String queryIdentifier = "frag_int_WJ_reserved_action_count_lower";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "percentage");
    }
}
