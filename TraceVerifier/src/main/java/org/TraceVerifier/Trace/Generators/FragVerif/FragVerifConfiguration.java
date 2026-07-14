package org.TraceVerifier.Trace.Generators.FragVerif;

import com.google.gson.annotations.Expose;
import org.TraceVerifier.Trace.Generators.Benchmarks.BenchmarkConfiguration;
import org.TraceVerifier.Trace.Generators.Benchmarks.BenchmarkGeneratorType;
import org.TraceVerifier.Trace.Generators.FreePolicy;

public class FragVerifConfiguration implements BenchmarkConfiguration {
    // Settings
    @Expose
    private final BenchmarkGeneratorType type;
    @Expose
    private long seed;
    @Expose
    private String version;
    @Expose
    private int sizeBase;
    @Expose
    private int numAllocationsPerPeak;

    // Deinit
    @Expose
    private FreePolicy freePolicy;

    public FragVerifConfiguration() {
        seed = 0;
        sizeBase = 256;
        freePolicy = FreePolicy.FIFO;
        version = "1.0";
        type = BenchmarkGeneratorType.FragVerif;
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

    public long getSeed() {
        return this.seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public int getSizeBase() {
        return sizeBase;
    }

    public void setSizeBase(int sizeBase) {
        this.sizeBase = sizeBase;
    }

    public int getNumAllocationsPerPeak() {
        return numAllocationsPerPeak;
    }

    public void setNumAllocationPerPeak(int numAllocationsPerPeak) {
        this.numAllocationsPerPeak = numAllocationsPerPeak;
    }

    public FreePolicy getFreePolicy() {
        return freePolicy;
    }

    public void setFreePolicy(FreePolicy freePolicy) {
        this.freePolicy = freePolicy;
    }

}
