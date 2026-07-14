package org.TraceVerifier.Trace.Generators.Experimental;

import com.google.gson.annotations.Expose;
import org.TraceVerifier.Trace.Generators.Benchmarks.BenchmarkConfiguration;
import org.TraceVerifier.Trace.Generators.Benchmarks.BenchmarkGeneratorType;
import org.TraceVerifier.Trace.Weights.WeightedElement;
import org.TraceVerifier.Trace.Weights.WeightedMap;

import java.util.List;

public class CooperatingThreadsConfiguration implements BenchmarkConfiguration {
    @Expose
    private final BenchmarkGeneratorType type;
    @Expose
    private int numAllocations;
    @Expose
    private WeightedMap weightedLifetimes;
    @Expose
    private WeightedMap weightedSizes;
    @Expose
    private int minAllocationsTimeQuantum;
    @Expose
    private int maxAllocationsTimeQuantum;
    @Expose
    private int amtThreads;
    @Expose
    private String version;
    @Expose
    private long seed;

    public CooperatingThreadsConfiguration() {
        type = BenchmarkGeneratorType.CooperatingThreads;
        numAllocations = 0;
        weightedLifetimes = new WeightedMap();
        weightedSizes = new WeightedMap();
        minAllocationsTimeQuantum = 0;
        maxAllocationsTimeQuantum = 0;
        amtThreads = 0;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getAmtThreads() {
        return amtThreads;
    }

    public void setAmtThreads(int amtThreads) {
        this.amtThreads = amtThreads;
    }

    public int getMinAllocationsTimeQuantum() {
        return minAllocationsTimeQuantum;
    }

    public void setMinAllocationsTimeQuantum(int minAllocationsTimeQuantum) {
        this.minAllocationsTimeQuantum = minAllocationsTimeQuantum;
    }

    public int getMaxAllocationsTimeQuantum() {
        return maxAllocationsTimeQuantum;
    }

    public void setMaxAllocationsTimeQuantum(int maxAllocationsTimeQuantum) {
        this.maxAllocationsTimeQuantum = maxAllocationsTimeQuantum;
    }

    public void addWeightSizes(WeightedElement weightedSize) {
        weightedSizes.add(weightedSize);
    }

    public void addWeightSizes(List<WeightedElement> weightedSize) {
        for (var alloc : weightedSize) {
            weightedSizes.add(alloc);
        }
    }

    public void addWeightLifetimes(WeightedElement weightedLifetime) {
        weightedLifetimes.add(weightedLifetime);
    }

    public void addWeightLifetimes(List<WeightedElement> weightedLifetime) {
        for (var alloc : weightedLifetime) {
            weightedLifetimes.add(alloc);
        }
    }

    public int getNumAllocations() {
        return numAllocations;
    }

    public void setNumAllocations(int numAllocations) {
        this.numAllocations = numAllocations;
    }

    public WeightedMap getWeightedLifetimes() {
        return weightedLifetimes;
    }

    public void setWeightedLifetimes(WeightedMap weightedLifetimes) {
        this.weightedLifetimes = weightedLifetimes;
    }

    public WeightedMap getWeightedSizes() {
        return weightedSizes;
    }

    public void setWeightedSizes(WeightedMap weightedSizes) {
        this.weightedSizes = weightedSizes;
    }

    @Override
    public BenchmarkGeneratorType getType() {
        return null;
    }
}
