package org.TraceVerifier.Trace;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a collection of traces in a group.
 */
public class TraceGroup {
    @Expose
    private TraceGroupIdentifier groupInformation;
    private List<Trace> traces = new ArrayList<>();

    public TraceGroup(TraceGroupIdentifier groupInformation) {
        this.groupInformation = groupInformation;
    }

    public TraceGroup(TraceGroupIdentifier groupInformation, Trace trace) {
        this.groupInformation = groupInformation;
        this.traces.add(trace);
    }

    public TraceGroup(TraceGroupIdentifier groupInformation, List<Trace> traces) {
        this.groupInformation = groupInformation;
        this.traces = traces;
    }

    public List<Trace> getTraces() {
        return traces;
    }

    public void addTrace(Trace trace) {
        this.traces.add(trace);
    }

    public TraceGroupIdentifier getGroupInformation() {
        return groupInformation;
    }
}
