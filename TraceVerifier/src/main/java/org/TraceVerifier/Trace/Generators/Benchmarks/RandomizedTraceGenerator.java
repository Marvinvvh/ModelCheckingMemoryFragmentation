package org.TraceVerifier.Trace.Generators.Benchmarks;

import org.TraceVerifier.Trace.Generators.Generic.WeightedTraceGenerator;
import org.TraceVerifier.Trace.Generators.TraceGenerator;
import org.TraceVerifier.Trace.Trace;

/**
 * Generator for randomized traces.
 */
public class RandomizedTraceGenerator implements TraceGenerator {
    RandomizedTraceConfiguration config;


    public RandomizedTraceGenerator() {
    }

    public void setConfig(RandomizedTraceConfiguration config) {
        this.config = config;
        this.config.setVersion("1.0");
    }

    /**
     * Pattern looks like this:
     * Randomized size allocations, and a balanced alloc/free chance. If no allocations, it will always allocate next, as it cannot free anything.
     */
    @Override
    public Trace generate(String identifier) {
        // Note that we use a weighted trace generator as we can set wanted weights for allocs and frees, and the allocation sizes.
        WeightedTraceGenerator generator = new WeightedTraceGenerator();
        generator.setSeed(config.getSeed());
        generator.setFreePolicy(config.getFreePolicy());
        generator.setActionWeights(config.getWeightAlloc(), config.getWeightFree());
        generator.setBalancedBehavior(config.getNumAllocations());
        generator.addSizeWeights(config.getWeightedAllocationSizes().getWeightedElements());
        Trace trace = generator.generate(identifier);
        trace.setIdentifier(identifier);
        return trace;
    }
}
