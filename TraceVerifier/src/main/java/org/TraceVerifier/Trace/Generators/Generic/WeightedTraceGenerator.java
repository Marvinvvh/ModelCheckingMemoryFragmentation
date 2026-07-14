package org.TraceVerifier.Trace.Generators.Generic;

import org.TraceVerifier.Trace.Generators.FreePolicy;
import org.TraceVerifier.Trace.Generators.TraceBalance;
import org.TraceVerifier.Trace.Generators.TraceGenerator;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Trace.TraceAction;
import org.TraceVerifier.Trace.TraceActionAlloc;
import org.TraceVerifier.Trace.TraceActionFree;
import org.TraceVerifier.Trace.Weights.WeightedElement;
import org.TraceVerifier.Trace.Weights.WeightedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generator for weighted traces. It uses weights to determine the chance of an allocation/free happening, and the allocation sizes.
 */
public class WeightedTraceGenerator implements TraceGenerator {
    private int amountOfActions = 1;
    private int amountOfAllocations = 0;
    private int startPointerIndex = 0;
    private FreePolicy freePolicy;
    private long seed = 0;
    private int weightAlloc;
    private int weightFree;
    private WeightedMap weightedAllocations;
    private TraceBalance type;

    public WeightedTraceGenerator() {
        this.weightedAllocations = new WeightedMap();
        this.weightAlloc = 1;
        this.weightFree = 1;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public TraceBalance getType() {
        return type;
    }

    /**
     * Allows for more allocations than frees.
     */
    public void setUnbalancedBehavior(int amountOfActions) {
        this.type = TraceBalance.Unbalanced;
        this.amountOfActions = amountOfActions;
    }
    /**
     * Forces the same number of allocations and frees.
     */
    public void setBalancedBehavior(int amountOfAllocations) {
        this.type = TraceBalance.Balanced;
        this.amountOfAllocations = amountOfAllocations;
    }

    public void resetWeightedElements() {
        weightedAllocations.clear();
    }
    /**
     * Set chance for allocation or free to happen. Allocations always occur when no allocations are unallocated.
     */
    public void setActionWeights(int weightAlloc, int weightFree) {
        this.weightAlloc = weightAlloc;
        this.weightFree = weightFree;
    }

    public void addSizeWeights(WeightedElement weightedAllocation) {
        this.weightedAllocations.add(weightedAllocation);
    }
    /**
     * Set weight for certain allocation sizes.
     */
    public void addSizeWeights(List<WeightedElement> weightedAllocation) {
        for (var alloc : weightedAllocation) {
            weightedAllocations.add(alloc);
        }
    }

    public void setSizeWeights(WeightedMap weightedAllocations)
    {
        this.weightedAllocations = weightedAllocations;
    }

    public List<WeightedElement> getWeightedAllocations() {
        return weightedAllocations.toList();
    }

    public void setStartPointerIndex(int startPointerIndex) {
        this.startPointerIndex = startPointerIndex;
    }

    public void setStartPointerIndex(Trace trace) {
        this.startPointerIndex = trace.getLastPointer() + 1;
    }

    public FreePolicy getFreePolicy() {
        return freePolicy;
    }

    public void setFreePolicy(FreePolicy freePolicy) {
        this.freePolicy = freePolicy;
    }

    private TraceAction.Type getRandomAction(Random rand) {
        int value = rand.nextInt(0, weightAlloc + weightFree);
        return value < weightAlloc ? TraceAction.Type.eAlloc : TraceAction.Type.eFree;
    }

    private int getWeightedValue(Random rand) {
        return weightedAllocations.getRandomizedValue(rand);
    }

    /**
     * Generates a (un)balanced allocation trace with allocation/free chances based on the action weights, and sizes based on sizeWeights.
     */
    @Override
    public Trace generate(String identifier) {
        Trace trace = new Trace();

        Random rand = new Random(getSeed());
        Random randAction = new Random(rand.nextLong(Long.MAX_VALUE));
        Random randFreePointer = new Random(rand.nextLong(Long.MAX_VALUE));
        Random randAllocSize = new Random(rand.nextLong(Long.MAX_VALUE));

        // Determine number of actions in the trace
        ArrayList<Integer> availableAllocPointers = new ArrayList<Integer>();
        int numOfActions = this.type == TraceBalance.Balanced ? amountOfAllocations * 2 : amountOfActions;
        int numOfAllocations = 0;

        for (int i = 0; i < numOfActions; i++) {
            TraceAction traceAction;

            // Get an action
            TraceAction.Type actionType = getRandomAction(randAction);

            // If we have no allocations, we force an allocation. If no allocations left, we always free.
            if (actionType == TraceAction.Type.eFree && availableAllocPointers.isEmpty()) {
                actionType = TraceAction.Type.eAlloc;
            } else if (type == TraceBalance.Balanced && actionType == TraceAction.Type.eAlloc && numOfAllocations == amountOfAllocations) {
                actionType = TraceAction.Type.eFree;
            }

            switch (actionType) {
                case eFree -> {
                    int pointerToFreeIndex = switch (freePolicy) {
                        case FIFO -> 0;
                        case LIFO -> availableAllocPointers.size() - 1;
                        case Randomized -> randFreePointer.nextInt(availableAllocPointers.size());
                        case Custom -> throw new Error("Not supported"); // There used to be more complex behavior here, but it has been removed.
                    };
                    // Remove allocation pointer.
                    int pointer = availableAllocPointers.get(pointerToFreeIndex);
                    availableAllocPointers.remove(pointerToFreeIndex);
                    traceAction = new TraceActionFree(pointer);
                }
                case eAlloc -> {
                    int pointer = numOfAllocations + startPointerIndex;
                    int allocationSize = getWeightedValue(randAllocSize);
                    traceAction = new TraceActionAlloc(pointer, allocationSize);
                    // Put allocation pointer in list.
                    availableAllocPointers.add(pointer);
                    numOfAllocations++;
                }
                default -> {
                    throw new Error("Didn't handle all allocation actions.");
                }
            }
            ;
            // Add to the trace.
            trace.append(traceAction);
        }
        trace.setIdentifier(identifier);
        return trace;
    }
}
