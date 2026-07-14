package org.TraceVerifier.Trace.Generators.Experimental;

import com.google.gson.annotations.Expose;
import org.TraceVerifier.Trace.Generators.Benchmarks.BenchmarkConfiguration;
import org.TraceVerifier.Trace.Generators.Benchmarks.BenchmarkGeneratorType;
import org.TraceVerifier.Trace.Weights.WeightedElement;
import org.TraceVerifier.Trace.Weights.WeightedMap;

public class TTLCacheTraceConfiguration implements BenchmarkConfiguration {
    @Expose
    private final BenchmarkGeneratorType type;
    @Expose
    private int numAllocations;
    @Expose
    private WeightedMap weightedLifetimes;
    @Expose
    private WeightedMap weightedSizes;
    @Expose
    private int timeQuantum;
    @Expose
    private long seed;
    @Expose
    private String version;

    public TTLCacheTraceConfiguration() {
        type = BenchmarkGeneratorType.TTLCache;
        numAllocations = 0;
        weightedLifetimes = new WeightedMap();
        weightedSizes = new WeightedMap();
        timeQuantum = 1;
        seed = 0;
        version = "0";
    }

    public int getTimeQuantum() {
        return timeQuantum;
    }

    public void setTimeQuantum(int timeQuantum) {
        this.timeQuantum = timeQuantum;
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

    public WeightedMap getWeightedSizes() {
        return weightedSizes;
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

    public void addWeightSizes(WeightedElement weightedSize) {
        weightedSizes.add(weightedSize);
    }

    public void addWeightLifetimes(WeightedElement weightedLifetime) {
        weightedLifetimes.add(weightedLifetime);
    }

    public BenchmarkGeneratorType getType() {
        return type;
    }
}
