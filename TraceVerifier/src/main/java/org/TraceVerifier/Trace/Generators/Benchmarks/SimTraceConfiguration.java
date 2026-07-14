package org.TraceVerifier.Trace.Generators.Benchmarks;

import com.google.gson.annotations.Expose;
import org.TraceVerifier.Trace.Generators.FreePolicy;
import org.TraceVerifier.Trace.Weights.WeightedElement;
import org.TraceVerifier.Trace.Weights.WeightedMap;

/**
 * Configuration for the simulation traces.
 */
public class SimTraceConfiguration implements BenchmarkConfiguration {
    // Settings
    @Expose
    private final BenchmarkGeneratorType type;
    @Expose
    private long seed;
    @Expose
    private String version;

    // Init
    @Expose
    WeightedMap weightAllocInit;
    @Expose
    private int numAllocationsInit;

    // Steady state
    @Expose
    WeightedMap weightAllocSteadyState;
    @Expose
    private int numAllocationsRound;
    @Expose
    private int rounds;
    @Expose
    private FreePolicy freePolicySteadyState;

    // Deinit
    @Expose
    private FreePolicy freePolicyDeinit;

    public SimTraceConfiguration() {
        type = BenchmarkGeneratorType.Sim;
        seed = 0;
        numAllocationsInit = 0;
        weightAllocInit = new WeightedMap();
        numAllocationsRound = 0;
        weightAllocSteadyState = new WeightedMap();
        rounds = 1;
        freePolicySteadyState = FreePolicy.FIFO;
        freePolicyDeinit = FreePolicy.FIFO;
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

    public long getSeed() {
        return this.seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    // Init
    public void addWeightSizesInit(WeightedElement weightedElement) {
        weightAllocInit.add(weightedElement);
    }

    public WeightedMap getWeightAllocInit()
    {
        return weightAllocInit;
    }

    public int getNumAllocationsInit() {
        return numAllocationsInit;
    }

    public void setNumAllocationsInit(int numAllocationsInit) {
        this.numAllocationsInit = numAllocationsInit;
    }

    // Steady state
    public void addWeightSizesSteadyState(WeightedElement weightedElement) {
        weightAllocSteadyState.add(weightedElement);
    }

    public WeightedMap getWeightAllocSteadyState()
    {
        return weightAllocSteadyState;
    }

    public int getNumAllocationsRound() {
        return numAllocationsRound;
    }

    public void setNumAllocationsRound(int numAllocationsRound) {
        this.numAllocationsRound = numAllocationsRound;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public FreePolicy getFreePolicySteadyState() {
        return freePolicySteadyState;
    }

    public void setFreePolicySteadyState(FreePolicy freePolicySteadyState) {
        this.freePolicySteadyState = freePolicySteadyState;
    }

    // Deinit
    public FreePolicy getFreePolicyDeinit() {
        return freePolicyDeinit;
    }

    public void setFreePolicyDeinit(FreePolicy freePolicyDeinit) {
        this.freePolicyDeinit = freePolicyDeinit;
    }

}
