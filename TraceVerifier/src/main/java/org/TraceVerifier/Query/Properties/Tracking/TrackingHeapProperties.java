package org.TraceVerifier.Query.Properties.Tracking;

import org.TraceVerifier.Query.BaseTraceQuery;
import org.TraceVerifier.Query.SimulationTraceQuery;

public class TrackingHeapProperties {
    public static BaseTraceQuery trackHeapError(int heapID) {
        String queryString = "heap_state(" + heapID + ").error";
        String queryComment = "Track the error state of a heap.";
        String queryIdentifier = "heap_error";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bool");
    }
}
