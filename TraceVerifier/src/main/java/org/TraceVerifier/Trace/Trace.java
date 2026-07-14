package org.TraceVerifier.Trace;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Trace that holds the allocation actions. The order of the actions in traceActions determines the order.
 */
public class Trace {
    @Expose
    private List<TraceAction> traceActions;
    @Expose
    private int lastPointer;
    @Expose
    private int sizeAllocationActions;
    @Expose
    private String identifier = "";

    public Trace() {
        traceActions = new ArrayList<>();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Prepares the trace so that it can be injected into the model.
     */
    public String toTraceString() {
        StringBuilder stringBuilder = new StringBuilder();
        int size = traceActions.size();
        for (int i = 0; i < size; i++) {
            stringBuilder.append(traceActions.get(i).toTraceActionString(i));
            if (i < size - 1) {
                stringBuilder.append(",");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public int getTraceActionCount() {
        return traceActions.size();
    }

    public int getAllocCount() {
        return this.sizeAllocationActions;
    }

    public List<TraceAction> getTraceActions() {
        return new ArrayList<>(traceActions);
    }

    public Trace append(Trace trace) {
        if (trace.getLastPointer() > lastPointer) {
            lastPointer = trace.getLastPointer();
        }
        this.sizeAllocationActions += trace.getAllocCount();
        append(trace.getTraceActions());
        return this;
    }
    /**
     * Add actions to the trace.
     */
    public Trace append(TraceAction traceAction) {
        if (traceAction.getPointer() > lastPointer) {
            lastPointer = traceAction.getPointer();
        }

        if (traceAction.isAlloc()) {
            this.sizeAllocationActions++;
        }
        this.traceActions.add(traceAction);
        return this;
    }

    public int getLastPointer() {
        return lastPointer;
    }

    public Trace copy() {
        Trace trace = new Trace();
        trace.append(this);
        return trace;
    }
    /**
     * Sets the pointers of the traceActions so that it continues from where the other trace left off.
     */
    public void continueFromTrace(Trace trace) {
        // Say pointer == 0 and last_pointer is 10, then we should stay at 11, so pointer + last_pointer + 1
        for (TraceAction traceAction : traceActions) {
            traceAction.setPointer(traceAction.getPointer() + trace.getLastPointer() + 1);
        }
    }

    public void continueFromPointer(int pointerOffset) {
        for (TraceAction traceAction : traceActions) {
            traceAction.setPointer(traceAction.getPointer() + pointerOffset);
        }
    }

    private Trace append(List<TraceAction> traceActions) {
        for (TraceAction traceAction : traceActions) {
            append(traceAction);
        }
        return this;
    }

}
