package org.TraceVerifier.Trace.Generators.Experimental;

import com.google.gson.annotations.Expose;
import org.TraceVerifier.Trace.Generators.Benchmarks.BenchmarkConfiguration;
import org.TraceVerifier.Trace.Generators.Benchmarks.BenchmarkGeneratorType;
import org.TraceVerifier.Trace.Weights.WeightedElement;
import org.TraceVerifier.Trace.Weights.WeightedMap;

public class BatchBufferTraceConfiguration implements BenchmarkConfiguration {

    @Expose
    private BenchmarkGeneratorType type;
    @Expose
    private WeightedMap weightedAllocationAllocsPerPeak;
    @Expose
    private WeightedMap weightedAllocationSizeAllocs;
    @Expose
    private WeightedMap weightedAllocationPeaks;
    @Expose
    private long seed;
    @Expose
    private String version;

    public BatchBufferTraceConfiguration() {
        version = "0";
        type = BenchmarkGeneratorType.BatchBuffer;
        seed = 0;
        weightedAllocationSizeAllocs = new WeightedMap();
        weightedAllocationAllocsPerPeak = new WeightedMap();
        weightedAllocationPeaks = new WeightedMap();
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

    public BenchmarkGeneratorType getType() {
        return type;
    }

    public void addWeightAllocsPerPeak(WeightedElement weightedElement) {
        weightedAllocationAllocsPerPeak.add(weightedElement);
    }

    public void addWeightSizeAllocs(WeightedElement weightedElement) {
        weightedAllocationSizeAllocs.add(weightedElement);
    }

    public void addWeightPeaks(WeightedElement weightedElement) {
        weightedAllocationPeaks.add(weightedElement);
    }

    public WeightedMap getWeightedAllocationAllocsPerPeak() {
        return weightedAllocationAllocsPerPeak;
    }

    public void setWeightedAllocationAllocsPerPeak(WeightedMap weightedAllocationAllocsPerPeak) {
        this.weightedAllocationAllocsPerPeak = weightedAllocationAllocsPerPeak;
    }

    public WeightedMap getWeightedAllocationSizeAllocs() {
        return weightedAllocationSizeAllocs;
    }

    public void setWeightedAllocationSizeAllocs(WeightedMap weightedAllocationSizeAllocs) {
        this.weightedAllocationSizeAllocs = weightedAllocationSizeAllocs;
    }

    public WeightedMap getWeightedAllocationPeaks() {
        return weightedAllocationPeaks;
    }

    public void setWeightedAllocationPeaks(WeightedMap weightedAllocationPeaks) {
        this.weightedAllocationPeaks = weightedAllocationPeaks;
    }
}
