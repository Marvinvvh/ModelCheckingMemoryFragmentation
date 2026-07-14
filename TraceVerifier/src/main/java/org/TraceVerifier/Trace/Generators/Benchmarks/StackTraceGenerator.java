package org.TraceVerifier.Trace.Generators.Benchmarks;

import org.TraceVerifier.Trace.Generators.TraceGenerator;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Trace.TraceAction;
import org.TraceVerifier.Trace.TraceActionAlloc;
import org.TraceVerifier.Trace.TraceActionFree;

import java.io.IOException;
import java.util.Random;
import java.util.Stack;

/**
 * Generator for stack traces.
 */
public class StackTraceGenerator implements TraceGenerator {
    private StackTraceConfiguration config;

    public StackTraceGenerator() throws IOException {

    }

    public StackTraceConfiguration getConfig() {
        return config;
    }

    public void setConfig(StackTraceConfiguration config) {
        this.config = config;
        this.config.setVersion("1.0");
    }

    @Override
    public Trace generate(String identifier) {
        Trace trace = new Trace();
        Stack<TraceAction> stack = new Stack<>();
        Random rand = new Random(config.getSeed());
        int allocCounter = 0;
        int actionCounter = 0;
        int stackSize = 0;
        int numActions = config.getNumAllocations() * 2; // Balanced trace: Frees == Allocs
        while (actionCounter < numActions) {
            // Pull a value, which is either an allocation or a free.
            boolean generatedIncrement = config.getWeightGrowthDirection().getRandomizedValue(rand) == config.getIncrementValue();

            // If there's nothing on the stack we are always able to allocate because the trace is balanced.
            // Else check so we don't go beyond the stack.
            boolean incrementDepth = stackSize == 0
                    || (generatedIncrement && stackSize < config.getMaxDepth() && allocCounter <= config.getNumAllocations());

            if (incrementDepth) {
                // Ensure that we don't exceed the stack.
                int incrSize = config.getWeightsDepthStepIncr().getRandomizedValue(rand);
                incrSize = incrSize + stackSize > config.getMaxDepth() ? config.getMaxDepth() - stackSize : incrSize;

                // Never allocate more than the amount of allocations we have left.
                incrSize = Math.min(incrSize, config.getNumAllocations() - allocCounter);

                for (int i = 0; i < incrSize; i++) {
                    TraceAction action = new TraceActionAlloc(allocCounter, incrSize);
                    stack.add(action);
                    trace.append(action);
                    allocCounter++;
                    stackSize++;
                    actionCounter++;
                }
            } else {
                int decrSize = config.getWeightsDepthStepDecr().getRandomizedValue(rand);
                // Never go beyond the stack root (depth == 0)
                decrSize = stackSize - decrSize < 0 ? stackSize : decrSize;
                for (int i = 0; i < decrSize; i++) {
                    TraceAction poppedAlloc = stack.pop();
                    trace.append(new TraceActionFree(poppedAlloc.getPointer()));
                    stackSize--;
                    actionCounter++;
                }
            }
        }

        trace.setIdentifier(identifier);
        return trace;
    }
}
