package org.TraceVerifier.Query.Properties.Tracking;

import org.TraceVerifier.Query.BaseTraceQuery;
import org.TraceVerifier.Query.SimulationTraceQuery;

public class TrackingAllocatorProperties {
    public static BaseTraceQuery trackAllocatorError(int allocatorID) {
        String queryString = "allocator_state(" + allocatorID + ").error";
        String queryComment = "Track the error state of an allocator.";
        String queryIdentifier = "allocator_error";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bool");
    }

}
