package org.TraceVerifier.Trace.Weights;

import com.google.gson.annotations.Expose;

import java.util.Random;

/**
 * ALlows for picking a weight within a set range. Factor multiplies the values.
 */
public record WeightedRange(@Expose int weight, @Expose int minValue, @Expose int maxBound,
                            @Expose int factor) implements WeightedElement {

    @Override
    public boolean isRanged() {
        return true;
    }

    @Override
    public int value() {
        throw new RuntimeException("A weighted constant should be generated using a seed.");
    }

    @Override
    public int getValueRanged(Random rand) {
        return rand.nextInt(minValue, maxBound + 1) * factor;
    }
}
