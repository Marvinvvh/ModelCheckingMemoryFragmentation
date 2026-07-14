package org.TraceVerifier.Trace.Generators.Benchmarks;

import com.google.gson.annotations.Expose;
import org.TraceVerifier.Trace.Weights.WeightedElement;
import org.TraceVerifier.Trace.Weights.WeightedMap;
import org.TraceVerifier.Trace.Weights.WeightedRange;

public class StackTraceConfiguration implements BenchmarkConfiguration {
    @Expose
    private BenchmarkGeneratorType type;
    @Expose
    private WeightedMap weightsDepthStepIncr;
    @Expose
    private WeightedMap weightsDepthStepDecr;
    @Expose
    private WeightedMap weightGrowthDirection;
    @Expose
    private WeightedMap weightsAllocationSize;
    @Expose
    private int maxDepth;
    @Expose
    private int numAllocations;
    @Expose
    private int incrementValue;
    @Expose
    private int decrementValue;
    @Expose
    private long seed;
    @Expose
    private String version;

    public StackTraceConfiguration() {
        type = BenchmarkGeneratorType.Stack;
        weightsAllocationSize = new WeightedMap();
        weightsDepthStepDecr = new WeightedMap();
        weightsDepthStepIncr = new WeightedMap();
        weightGrowthDirection = new WeightedMap();
        maxDepth = 1;
        numAllocations = 0;
        incrementValue = 1;
        decrementValue = 0;
        seed = 0;
        version = "1.0";
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public BenchmarkGeneratorType getType() {
        return type;
    }

    public int getIncrementValue() {return incrementValue; }
    public int getDecrementValue() {return decrementValue; }


    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public WeightedMap getWeightGrowthDirection() {
        return weightGrowthDirection;
    }

    /**
     * Set how much depth is increased or decreased when an increment/decrement is chosen.
     */
    public void setWeightGrowthDirection(int weightIncr, int weightDecr) {
        this.weightGrowthDirection.clear();
        this.weightGrowthDirection.add(new WeightedRange(weightDecr, decrementValue, decrementValue, 1));
        this.weightGrowthDirection.add(new WeightedRange(weightIncr, incrementValue, incrementValue, 1));
    }

    public WeightedMap getWeightsDepthStepIncr() {
        return weightsDepthStepIncr;
    }

    public void addWeightDepthStepIncr(WeightedElement weightsDepthStepIncr) {
        this.weightsDepthStepIncr.add(weightsDepthStepIncr);
    }

    public WeightedMap getWeightsDepthStepDecr() {
        return weightsDepthStepDecr;
    }

    /**
     * Set chan
     */
    public void addWeightDepthStepDecr(WeightedElement weightsDepthStepDecr) {
        this.weightsDepthStepDecr.add(weightsDepthStepDecr);
    }

    public WeightedMap getWeightsAllocationSize() {
        return weightsAllocationSize;
    }


    public void addWeightAllocationSize(WeightedElement weightsAllocationSize) {
        this.weightsAllocationSize.add(weightsAllocationSize);
    }

    public int getNumAllocations() {
        return numAllocations;
    }

    public void setNumAllocations(int numAllocations) {
        this.numAllocations = numAllocations;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }
}
