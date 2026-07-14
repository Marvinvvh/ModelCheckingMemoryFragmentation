package org.TraceVerifier.Trace.Generators.Experimental;

import org.TraceVerifier.Trace.Generators.FreePolicy;
import org.TraceVerifier.Trace.Generators.Generic.BalancingTraceGenerator;
import org.TraceVerifier.Trace.Generators.Generic.WeightedTraceGenerator;
import org.TraceVerifier.Trace.Generators.TraceGenerator;
import org.TraceVerifier.Trace.Trace;

import java.util.Random;

public class BatchBufferTraceGenerator implements TraceGenerator {
    private BatchBufferTraceConfiguration config;

    public BatchBufferTraceGenerator() {
    }

    public BatchBufferTraceConfiguration getConfig() {
        return config;
    }

    public void setConfig(BatchBufferTraceConfiguration config) {
        this.config = config;
        this.config.setVersion("1.0");
    }

    @Override
    public Trace generate(String identifier) {
        Trace trace = new Trace();
        Random rand = new Random(config.getSeed());

        int amtOfPeaks = config.getWeightedAllocationPeaks().getRandomizedValue(rand);
        for (int i = 0; i < amtOfPeaks; i++) {
            int peakAllocs = config.getWeightedAllocationAllocsPerPeak().getRandomizedValue(rand);
            WeightedTraceGenerator traceGenerator = new WeightedTraceGenerator();
            traceGenerator.setSeed(rand.nextLong(Long.MAX_VALUE));
            traceGenerator.setUnbalancedBehavior(peakAllocs);
            traceGenerator.addSizeWeights(config.getWeightedAllocationSizeAllocs().getWeightedElements());
            traceGenerator.setActionWeights(1, 0);
            traceGenerator.setStartPointerIndex(trace);
            Trace alloc_trace = traceGenerator.generate("alloc_part");

            BalancingTraceGenerator balancingTraceGenerator = new BalancingTraceGenerator();
            balancingTraceGenerator.setSeed(rand.nextLong(Long.MAX_VALUE));
            balancingTraceGenerator.setTrace(alloc_trace);
            balancingTraceGenerator.balanceFully();
            balancingTraceGenerator.setFreePolicy(FreePolicy.FIFO);
            Trace balance_trace = balancingTraceGenerator.generate("balance_part");

            trace.append(alloc_trace);
            trace.append(balance_trace);
        }
        trace.setIdentifier(identifier);
        return trace;
    }


}
