package org.TraceVerifier.Trace.Generators.Benchmarks;

import com.google.gson.annotations.Expose;
import org.TraceVerifier.Trace.Generators.FreePolicy;
import org.TraceVerifier.Trace.Weights.WeightedElement;
import org.TraceVerifier.Trace.Weights.WeightedMap;

/**
 * Configuration for the randomized trace generator.
 */
public class RandomizedTraceConfiguration implements BenchmarkConfiguration {
    @Expose
    private BenchmarkGeneratorType type;
    @Expose
    private int numAllocations;
    @Expose
    private int weightAlloc;
    @Expose
    private int weightFree;
    @Expose
    private FreePolicy freePolicy;
    @Expose
    private WeightedMap weightedAllocationSizes;
    @Expose
    private long seed;
    @Expose
    private String version;

    public RandomizedTraceConfiguration() {
        seed = 0;
        version = "1.0";
        type = BenchmarkGeneratorType.Randomized;
        numAllocations = 0;
        weightAlloc = 1;
        weightFree = 1;
        freePolicy = FreePolicy.FIFO;
        weightedAllocationSizes = new WeightedMap();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    /**
     * Weights for allocation sizes.
     */
    public void addWeightSizes(WeightedElement weightedElement) {
        weightedAllocationSizes.add(weightedElement);
    }

    public void setAllocationWeights(int weightAlloc, int weightFree) {
        this.weightAlloc = weightAlloc;
        this.weightFree = weightFree;
    }

    public BenchmarkGeneratorType getType() {
        return type;
    }

    public void setType(BenchmarkGeneratorType type) {
        this.type = type;
    }

    public int getNumAllocations() {
        return numAllocations;
    }

    public void setNumAllocations(int numAllocations) {
        this.numAllocations = numAllocations;
    }

    /**
     * Weight to get an allocation.
     */
    public int getWeightAlloc() {
        return weightAlloc;
    }
    /**
     * Weight to get a free, if an allocation is still unfreed.
     */
    public int getWeightFree() {
        return weightFree;
    }

    public FreePolicy getFreePolicy() {
        return freePolicy;
    }

    public void setFreePolicy(FreePolicy freePolicy) {
        this.freePolicy = freePolicy;
    }

    public WeightedMap getWeightedAllocationSizes() {
        return weightedAllocationSizes;
    }
}
