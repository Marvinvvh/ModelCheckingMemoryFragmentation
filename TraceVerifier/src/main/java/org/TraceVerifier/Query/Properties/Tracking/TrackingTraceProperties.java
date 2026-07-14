package org.TraceVerifier.Query.Properties.Tracking;

import org.TraceVerifier.Query.BaseTraceQuery;
import org.TraceVerifier.Query.SimulationTraceQuery;

public class TrackingTraceProperties {
    public static BaseTraceQuery trackActionCount() {
        String queryString = "action_count";
        String queryComment = "Amount of actions processed.";
        String queryIdentifier = "action_count";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "actions");
    }

    public static BaseTraceQuery trackAllocActionCount() {
        String queryString = "alloc_action_count";
        String queryComment = "Amount of allocations processed.";
        String queryIdentifier = "alloc_action_count";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "actions");
    }

    public static BaseTraceQuery trackFreeActionCount() {
        String queryString = "free_action_count";
        String queryComment = "Amount of frees processed.";
        String queryIdentifier = "free_action_count";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "actions");
    }

    public static BaseTraceQuery trackTraceError() {
        String queryString = "trace.error";
        String queryComment = "Track the error state of the trace.";
        String queryIdentifier = "trace_error";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bool");
    }

    public static BaseTraceQuery trackTraceDone() {
        String queryString = "trace.done";
        String queryComment = "Point in time when trace is done.";
        String queryIdentifier = "trace_done";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bool");
    }

}
