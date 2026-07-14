package org.TraceVerifier.Trace.Generators.Generic.Experimental;

import org.TraceVerifier.Trace.TimedTraceAction;

import java.util.ArrayList;
import java.util.List;

public class LifetimeUnit {
    private final int lifetime;
    private final List<TimedTraceAction> traceActions;
    private int timeLeft;

    public LifetimeUnit(int lifetime) {
        this.lifetime = lifetime;
        this.timeLeft = this.lifetime;
        this.traceActions = new ArrayList<>();
    }

    public int getLifetime() {
        return lifetime;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public List<TimedTraceAction> getTraceActions() {
        return traceActions;
    }

    public void add(TimedTraceAction traceAction) {
        traceActions.add(traceAction);
    }

    public void add(List<TimedTraceAction> traceAction) {
        traceActions.addAll(traceAction);
    }

    public void decrement() {
        if (timeLeft > 0) {
            timeLeft--;
        }
    }

    public void expire() {
        timeLeft = 0;
    }
}
