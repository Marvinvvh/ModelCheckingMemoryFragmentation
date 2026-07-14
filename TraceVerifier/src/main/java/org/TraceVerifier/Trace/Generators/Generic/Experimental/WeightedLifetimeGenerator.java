package org.TraceVerifier.Trace.Generators.Generic.Experimental;

import org.TraceVerifier.Trace.TimedTraceAction;
import org.TraceVerifier.Trace.TraceAction;
import org.TraceVerifier.Trace.TraceActionAlloc;
import org.TraceVerifier.Trace.Weights.WeightedMap;

import java.util.Random;

public class WeightedLifetimeGenerator implements LifetimeGenerator<TraceAction> {
    private WeightedMap weightedLifetimes;
    private WeightedMap weightedSizes;
    private long seed;
    private int numAllocations;
    private int lifetimeOffset;

    public WeightedLifetimeGenerator() {
        weightedLifetimes = new WeightedMap();
        weightedSizes = new WeightedMap();
    }

    public void setNumAllocations(int numAllocations) {
        this.numAllocations = numAllocations;
    }

    public void setLifetimeOffset(int lifetimeOffset) {
        this.lifetimeOffset = lifetimeOffset;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public void setWeightedSizes(WeightedMap weightedSizes) {
        this.weightedSizes = weightedSizes;
    }

    public void setWeightedLifetimes(WeightedMap weightedLifetimes) {
        this.weightedLifetimes = weightedLifetimes;
    }

    @Override
    public LifetimeTrace createLifetimeTrace() {
        LifetimeTrace lifetimeTrace = new LifetimeTrace();
        Random rand = new Random(seed);
        Random randSize = new Random(rand.nextLong(Long.MAX_VALUE));
        Random randLifetime = new Random(rand.nextLong(Long.MAX_VALUE));

        for (int i = 0; i < numAllocations; i++) {
            int randomSize = weightedSizes.getRandomizedValue(randSize);
            int randomLifetime = weightedLifetimes.getRandomizedValue(randLifetime) + lifetimeOffset;

            var lifetime = new TimedTraceAction(randomLifetime, new TraceActionAlloc(i, randomSize));
            lifetimeTrace.add(lifetime);
        }

        return lifetimeTrace;
    }
}
