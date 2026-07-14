package org.TraceVerifier.Trace.Weights;

import java.util.Random;

/**
 * Element that represent some weighted value.
 */
public interface WeightedElement {
    int weight();

    boolean isRanged();

    int value();

    int getValueRanged(Random rand);
}
