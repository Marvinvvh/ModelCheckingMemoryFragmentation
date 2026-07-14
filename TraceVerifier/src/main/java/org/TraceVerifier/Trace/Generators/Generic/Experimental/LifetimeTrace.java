package org.TraceVerifier.Trace.Generators.Generic.Experimental;

import org.TraceVerifier.Trace.TimedTraceAction;

import java.util.ArrayList;
import java.util.List;

public class LifetimeTrace {
    private final List<TimedTraceAction> timedTraceActions;

    public LifetimeTrace() {
        timedTraceActions = new ArrayList<>();
    }

    public void add(TimedTraceAction timedTraceAction) {
        timedTraceActions.add(timedTraceAction);
    }

    public void clear() {
        timedTraceActions.clear();
    }

    public List<TimedTraceAction> getOrders() {
        return timedTraceActions;
    }

    public int size() {
        return timedTraceActions.size();
    }

    public TimedTraceAction getOrder(int order) {
        if (order < timedTraceActions.size()) {
            return timedTraceActions.get(order);
        }

        return null;
    }

    public TimedTraceAction popFirst() {
        if (!timedTraceActions.isEmpty()) {
            var action = timedTraceActions.getFirst();
            timedTraceActions.remove(action);
            return action;
        }
        return null;
    }

    public boolean isEmpty() {
        return timedTraceActions.isEmpty();
    }
}
