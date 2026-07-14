package org.TraceVerifier.Query;

/**
 * Trace Query which tracks the value of a variable over the duration of the trace processing.
 */
public class SimulationTraceQuery extends BaseTraceQuery {
    public SimulationTraceQuery(String simulationString, String queryComment, String queryIdentifier, String unit) {
        super("simulate[<=ALLOC_ACTION_COUNT + 1] {" + simulationString + "}", queryComment, queryIdentifier, unit, "simulation");
    }

    public SimulationTraceQuery(String simulationString, String queryComment, String queryIdentifier, String unit, Integer customActionCount) {
        super("simulate[<=" + customActionCount.toString() + "] {" + simulationString + "}", queryComment, queryIdentifier, unit, "simulation");
        if (customActionCount <= 0) {
            throw new Error("It is not possible for a custom action count to be below 0. Invalid trace query.");
        }
    }
}
