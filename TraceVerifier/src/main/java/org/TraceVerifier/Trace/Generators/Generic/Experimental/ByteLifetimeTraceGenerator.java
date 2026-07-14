package org.TraceVerifier.Trace.Generators.Generic.Experimental;

import org.TraceVerifier.Trace.Generators.TraceGenerator;
import org.TraceVerifier.Trace.TimedTraceAction;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Trace.TraceActionFree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ByteLifetimeTraceGenerator implements TraceGenerator {
    private final List<LifetimeUnit> currentLifetimes;
    private final HashMap<Integer, LifetimeUnit> currentLifetimesLookup;
    private final List<List<TimedTraceAction>> orderedAllocations;
    private final List<List<TimedTraceAction>> orderedFrees;
    private final List<Trace> traces;
    private LifetimeTrace lifetimeTrace;
    private int timeQuantum;
    private int timePassed;
    private int memoryCounter;
    private boolean collapse;
    private int pointerOffset;

    public ByteLifetimeTraceGenerator() {
        this.currentLifetimesLookup = new HashMap<>();
        this.currentLifetimes = new ArrayList<>();
        this.traces = new ArrayList<>();
        this.orderedAllocations = new ArrayList<>();
        this.orderedFrees = new ArrayList<>();
        this.timeQuantum = 0;
        this.timePassed = 0;
        this.memoryCounter = 0;
        this.collapse = true;
        this.pointerOffset = 0;
        refreshLookup();
    }

    public int getPointerOffset() {
        return pointerOffset;
    }

    public void setPointerOffset(int pointerOffset) {
        this.pointerOffset = pointerOffset;
    }

    public void setTimeQuantum(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

    public void setCollapse(boolean collapse) {
        this.collapse = collapse;
    }

    public void setLifetimeTrace(LifetimeTrace lifetimeTrace) {
        this.lifetimeTrace = lifetimeTrace;
        cleanupTimeQuantum();
        refreshLookup();
    }

    @Override
    public Trace generate(String identifier) {
        Trace localTrace = new Trace();
        localTrace.setIdentifier(identifier);
        boolean initialPass = true;
        while (!currentLifetimes.isEmpty() || initialPass) {
            Trace trace = new Trace();
            processTimeQuantum(trace);
            traces.add(trace);
            localTrace.append(trace);
            initialPass = false;
        }

        return localTrace;
    }

    public void reset() {
        timeQuantum = 0;
        timePassed = 0;
        memoryCounter = 0;
    }

    public int getTimePassed() {
        return timePassed;
    }

    public Trace getTraceAtTime(int time) {
        if (time >= traces.size()) {
            return null;
        }

        Trace trace = new Trace();
        for (var action : traces.get(time).getTraceActions()) {
            trace.append(action);
        }
        return trace;
    }

    private void processTimeQuantum(Trace trace) {
        int lifetimeProcess = 0;
        int startMemoryCounter = memoryCounter % timeQuantum;
        var allocs = new ArrayList<TimedTraceAction>();
        var frees = new ArrayList<TimedTraceAction>();

        // As this is a byte-based lifetime, we don't have any passage of time related without allocations. So we have the option to collapse immediately.
        // There is however an alternative choice to pass on the existing lifetimes to some other thread, but this is currently not implemented.
        if (collapse && lifetimeTrace.isEmpty()) {
            collapseLifetimes();
        }

        // See if there are any frees to process.
        if (hasLifetime(lifetimeProcess)) {
            LifetimeUnit lifetime = getLifetime(lifetimeProcess);
            for (var action : lifetime.getTraceActions()) {
                frees.add(action);
                TraceActionFree free = new TraceActionFree(action.traceAction().getPointer() + pointerOffset);
                trace.append(free);
            }
        }
        cleanupTimeQuantum();

        // Keep processing actions until we've passed the time-quantum
        while (startMemoryCounter < timeQuantum && !lifetimeTrace.isEmpty()) {
            var action = lifetimeTrace.popFirst();
            action.traceAction().setPointer(action.traceAction().getPointer() + pointerOffset);
            trace.append(action.traceAction());
            add(action);
            allocs.add(action);

            if (action.traceAction().isFree()) {
                throw new IllegalStateException("Can't have frees in lifetimeTrace");
            }
            startMemoryCounter += action.traceAction().getSize();
        }

        // Without collapse always pass time, else immediately skip to the next collapsing phase.
        if (!collapse || startMemoryCounter > timeQuantum) {
            passTimeQuantum();
        }

        orderedAllocations.add(allocs);
        orderedFrees.add(frees);
    }

    private void cleanupTimeQuantum() {
        int endLifetime = 0;
        if (currentLifetimesLookup.containsKey(endLifetime)) {
            var passedLifetime = currentLifetimesLookup.get(endLifetime);
            currentLifetimesLookup.remove(endLifetime);
            currentLifetimes.remove(passedLifetime);
        }
        refreshLookup();
    }

    private void add(TimedTraceAction timedTraceAction) {
        var lifetime = timedTraceAction.lifetime();
        if (currentLifetimesLookup.containsKey(lifetime)) {
            currentLifetimesLookup.get(lifetime).add(timedTraceAction);
        } else {
            LifetimeUnit newLifetime = new LifetimeUnit(lifetime);
            newLifetime.add(timedTraceAction);
            currentLifetimes.add(newLifetime);
            currentLifetimesLookup.put(newLifetime.getTimeLeft(), newLifetime);
        }
    }

    private boolean hasLifetime(int lifetime) {
        return currentLifetimesLookup.containsKey(lifetime);
    }

    private LifetimeUnit getLifetime(int lifetime) {
        return currentLifetimesLookup.getOrDefault(lifetime, null);
    }

    private void passTimeQuantum() {
        for (var lifetime : currentLifetimes) {
            lifetime.decrement();
        }
        timePassed++;
        refreshLookup();
    }

    private void collapseLifetimes() {
        LifetimeUnit lifetime = new LifetimeUnit(0);

        for (var lf : currentLifetimes) {
            lifetime.add(lf.getTraceActions());
        }
        currentLifetimes.clear();
        currentLifetimes.add(lifetime);
        refreshLookup();
    }

    private void refreshLookup() {
        currentLifetimesLookup.clear();
        for (var lifetime : currentLifetimes) {
            currentLifetimesLookup.put(lifetime.getTimeLeft(), lifetime);
        }
    }
}
