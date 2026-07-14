package org.TraceVerifier.Trace.Generators.Generic;

import org.TraceVerifier.Trace.Generators.FreePolicy;
import org.TraceVerifier.Trace.Generators.TraceGenerator;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Trace.TraceAction;
import org.TraceVerifier.Trace.TraceActionFree;

import java.util.*;

/**
 * Pattern looks like this:
 * Large up-front allocations between size A and B
 * Balanced trace which does not contain any of the up-front allocations, these are usually smaller than the initial allocations, but do not have to be.
 * Freeing the initial allocations
 */
public class BalancingTraceGenerator implements TraceGenerator {

    private Trace trace = new Trace();
    private int balanceAmount = 0;
    private BalanceType balanceType = BalanceType.eUninitialized;
    private FreePolicy freePolicy = FreePolicy.FIFO;
    private long seed = 0;

    public BalancingTraceGenerator() {
    }

    public BalanceType getBalanceType() {
        return balanceType;
    }

    public int getBalanceAmount() {
        return balanceAmount;
    }

    public FreePolicy getFreePolicy() {
        return freePolicy;
    }

    public void setFreePolicy(FreePolicy freePolicy) {
        this.freePolicy = freePolicy;
    }

    public void setTrace(Trace trace) {
        this.trace = trace;
    }

    public void balanceFully() {
        this.balanceAmount = getAllocationCount();
        this.balanceType = BalanceType.eFull;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public void balanceCounted(int balanceAmount) {
        this.balanceAmount = Math.min(balanceAmount, getAllocationCount());
        this.balanceType = BalanceType.ePartial;
    }

    public void balanceRandomly(Random rng) {
        // Generate value from 1 to max + 1 with -1 that corresponds to 0 to max (non-inclusive)
        int allocationCount = getAllocationCount();
        int randomBalanceAmount = rng.nextInt(getAllocationCount() + 1) - 1;
        this.balanceAmount = allocationCount == 0 ? 0 : randomBalanceAmount;
        this.balanceType = BalanceType.eRandom;
    }

    public int getAllocationCount() {
        return (int) this.trace.getTraceActions()
                .stream()
                .filter(TraceAction::isAlloc)
                .count(); // NOTE: This might be something that has to be changed for very large lists, as this might take a while.
    }

    private Trace generateFIFOTrace(String identifier) {
        Trace trace = new Trace();
        // We want to use a set as each pointer is unique, and we want it hashed for fast lookup
        // As we traverse the pointers linearly, we want to retain the appended order of the pointers.
        LinkedHashSet<Integer> availablePointers = new LinkedHashSet<>();
        for (TraceAction action : this.trace.getTraceActions()) {
            if (action.isAlloc()) {
                availablePointers.add(action.getPointer());
            } else if (action.isFree()) {
                availablePointers.remove(action.getPointer());
            }
        }

        int balanceCount = 0;
        for (int ptr : availablePointers.stream().toList()) {
            if (balanceCount >= balanceAmount) {
                break;
            }
            trace.append(new TraceActionFree(ptr));
            balanceCount++;
        }

        trace.setIdentifier(identifier);
        return trace;
    }

    private Trace generateLIFOTrace(String identifier) {
        Trace trace = new Trace();
        // We want to use a set as each pointer is unique, and we want it hashed for fast lookup
        // As we traverse the pointers linearly, we want to retain the appended order of the pointers.
        LinkedHashSet<Integer> availablePointers = new LinkedHashSet<>();
        for (TraceAction action : this.trace.getTraceActions()) {
            if (action.isAlloc()) {
                availablePointers.add(action.getPointer());
            } else if (action.isFree()) {
                availablePointers.remove(action.getPointer());
            }
        }

        int balanceCount = 0;
        for (int ptr : availablePointers.stream().toList().reversed()) {
            balanceCount++;
            if (balanceCount >= balanceAmount) {
                break;
            }
            trace.append(new TraceActionFree(ptr));
        }

        trace.setIdentifier(identifier);
        return trace;
    }

    private Trace generateRandomizedTrace(String identifier) {
        Trace trace = new Trace();
        HashSet<Integer> availablePointers = new HashSet<>();
        for (TraceAction action : this.trace.getTraceActions()) {
            if (action.isAlloc()) {
                availablePointers.add(action.getPointer());
            } else if (action.isFree()) {
                availablePointers.remove(action.getPointer());
            }
        }

        List<Integer> randomizedPointers = new ArrayList<>(availablePointers.stream().toList());
        Collections.shuffle(randomizedPointers);
        int balanceCount = 0;
        for (int ptr : randomizedPointers) {
            balanceCount++;
            if (balanceCount >= balanceAmount) {
                break;
            }
            trace.append(new TraceActionFree(ptr));
        }
        trace.setIdentifier(identifier);
        return trace;
    }

    @Override
    public Trace generate(String identifier) {
        Trace trace = new Trace();
        switch (freePolicy) {
            case FreePolicy.FIFO -> {
                trace = generateFIFOTrace(identifier);
            }
            case FreePolicy.LIFO -> {
                trace = generateLIFOTrace(identifier);
            }
            case FreePolicy.Randomized -> {
                trace = generateRandomizedTrace(identifier);
            }
        }
        return trace;
    }

    public enum BalanceType {
        eUninitialized,
        eRandom,
        ePartial,
        eFull,
    }
}
