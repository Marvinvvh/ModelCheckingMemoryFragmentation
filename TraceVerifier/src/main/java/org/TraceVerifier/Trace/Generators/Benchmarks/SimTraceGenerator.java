package org.TraceVerifier.Trace.Generators.Benchmarks;

import org.TraceVerifier.Trace.Generators.Generic.BalancingTraceGenerator;
import org.TraceVerifier.Trace.Generators.Generic.WeightedTraceGenerator;
import org.TraceVerifier.Trace.Generators.TraceGenerator;
import org.TraceVerifier.Trace.Trace;

import java.util.Random;

/**
 * Generator for simulation traces.
 */
public class SimTraceGenerator implements TraceGenerator {
    private SimTraceConfiguration config;

    public SimTraceGenerator() {

    }

    public void setConfig(SimTraceConfiguration config) {
        this.config = config;
        this.config.setVersion("1.0");
    }


    /**
     * Pattern looks like this:
     * Large up-front allocations between size A and B
     * Balanced trace which does not contain any of the up-front allocations, these are usually smaller than the initial allocations, but do not have to be.
     * Freeing the initial allocations
     */
    @Override
    public Trace generate(String identifier) {
        // This could just be done immediately with trace, and not split it up (except init).
        // But the performance impact is rather low and this makes debugging easier.

        // Large up-front allocations between size A and B
        Random rand = new Random(config.getSeed());
        WeightedTraceGenerator genInit = new WeightedTraceGenerator();
        genInit.setSeed(rand.nextLong(Long.MAX_VALUE));
        genInit.setUnbalancedBehavior(config.getNumAllocationsInit());
        genInit.setSizeWeights(config.getWeightAllocInit());
        genInit.setActionWeights(1, 0);
        Trace traceInit = genInit.generate("init");

        // Balanced traces in the middle, which we generate by joining together small balanced traces.
        Trace traceSteadyState = new Trace();
        for (int i = 0; i < config.getRounds(); i++) {
            WeightedTraceGenerator traceGenerator = new WeightedTraceGenerator();
            traceGenerator.setSeed(rand.nextLong(Long.MAX_VALUE));
            traceGenerator.setUnbalancedBehavior(config.getNumAllocationsRound());
            traceGenerator.setSizeWeights(config.getWeightAllocSteadyState());
            traceGenerator.setActionWeights(1, 0);
            if (i == 0) {
                traceGenerator.setStartPointerIndex(traceInit);
            } else {
                traceGenerator.setStartPointerIndex(traceSteadyState);
            }
            Trace alloc_trace = traceGenerator.generate("alloc_part_" + i);

            BalancingTraceGenerator balancingTraceGenerator = new BalancingTraceGenerator();
            balancingTraceGenerator.setSeed(rand.nextLong(Long.MAX_VALUE));
            balancingTraceGenerator.setTrace(alloc_trace);
            balancingTraceGenerator.setFreePolicy(config.getFreePolicySteadyState());
            balancingTraceGenerator.balanceFully();
            Trace balance_trace = balancingTraceGenerator.generate("balance_part");

            traceSteadyState.append(alloc_trace);
            traceSteadyState.append(balance_trace);
        }

        // Freeing the initial allocations
        BalancingTraceGenerator deinitTraceGenerator = new BalancingTraceGenerator();
        deinitTraceGenerator.setSeed(rand.nextLong(Long.MAX_VALUE));
        deinitTraceGenerator.setTrace(traceInit);
        deinitTraceGenerator.setFreePolicy(config.getFreePolicyDeinit());
        deinitTraceGenerator.balanceFully();
        Trace traceDeinit = deinitTraceGenerator.generate("deinit");

        Trace trace = new Trace();
        trace.append(traceInit);
        trace.append(traceSteadyState);
        trace.append(traceDeinit);
        trace.setIdentifier(identifier);

        return trace;
    }
}
