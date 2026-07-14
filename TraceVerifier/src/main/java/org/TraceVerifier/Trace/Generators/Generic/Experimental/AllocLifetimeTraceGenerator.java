package org.TraceVerifier.Trace.Generators.Generic.Experimental;

import org.TraceVerifier.Trace.Generators.FreePolicy;
import org.TraceVerifier.Trace.Generators.TraceGenerator;
import org.TraceVerifier.Trace.TimedTraceAction;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Trace.TraceActionFree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class AllocLifetimeTraceGenerator implements TraceGenerator {
    private final List<LifetimeUnit> currentLifetimes;
    private final HashMap<Integer, LifetimeUnit> currentLifetimesLookup;
    private final List<List<TimedTraceAction>> orderedAllocations;
    private final List<List<TimedTraceAction>> orderedFrees;
    private final List<Trace> traces;
    private LifetimeTrace lifetimeTrace;
    private FreePolicy freePolicy;
    private int timePassed;
    private int minAllocations;
    private int maxAllocations;
    private int pointerOffset;
    private long seed;
    private Random rand;

    public AllocLifetimeTraceGenerator() {
        this.currentLifetimesLookup = new HashMap<>();
        this.currentLifetimes = new ArrayList<>();
        this.traces = new ArrayList<>();
        this.orderedAllocations = new ArrayList<>();
        this.orderedFrees = new ArrayList<>();
        this.freePolicy = FreePolicy.FIFO;
        this.timePassed = 0;
        this.pointerOffset = 0;
        rand = new Random(getSeed());
        refreshLookup();
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
        rand = new Random(seed);
    }

    public int getPointerOffset() {
        return pointerOffset;
    }

    public void setPointerOffset(int pointerOffset) {
        this.pointerOffset = pointerOffset;
    }

    public int getMaxAllocations() {
        return maxAllocations;
    }

    public void setMaxAllocations(int maxAllocations) {
        this.maxAllocations = maxAllocations;
    }

    public int getMinAllocations() {
        return minAllocations;
    }

    public void setMinAllocations(int minAllocations) {
        this.minAllocations = minAllocations;
    }

    public void setFreePolicy(FreePolicy freePolicy) {
        this.freePolicy = freePolicy;
    }

    public void setLifetimeTrace(LifetimeTrace lifetimeTrace) {
        this.lifetimeTrace = lifetimeTrace;
        cleanupTimeQuantum();
        refreshLookup();
    }

    @Override
    public Trace generate(String identifier) {
        Trace localTrace = new Trace();
        boolean initialPass = true;
        rand = new Random(getSeed());
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
        timePassed = 0;
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
        var allocs = new ArrayList<TimedTraceAction>();
        var frees = new ArrayList<TimedTraceAction>();

        // See if there are any frees to process.
        if (hasLifetime(lifetimeProcess)) {
            LifetimeUnit lifetime = getLifetime(lifetimeProcess);
            for (var action : lifetime.getTraceActions()) {
                frees.add(action);
                TraceActionFree free = new TraceActionFree(action.traceAction().getPointer());
                trace.append(free);
            }
        }
        cleanupTimeQuantum();
        // Keep processing actions until we've passed the time-quantum
        Random actionRand = new Random(rand.nextLong(Long.MAX_VALUE));
        int actions = actionRand.nextInt(minAllocations, maxAllocations + 1);
        int actionsLeft = lifetimeTrace.size();
        actions = Math.min(actionsLeft, actions);

        for (int i = 0; i < actions; i++) {
            var action = lifetimeTrace.popFirst();
            action.traceAction().setPointer(action.traceAction().getPointer() + pointerOffset);
            trace.append(action.traceAction());
            add(action);
            allocs.add(action);
            if (action.traceAction().isFree()) {
                throw new IllegalStateException("Can't have frees in lifetimeTrace");
            }
        }

        // Currently we bundle the allocation and frees for a select
        passTimeQuantum();
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
