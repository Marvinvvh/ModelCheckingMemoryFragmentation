package org.TraceVerifier.Trace.Generators.Experimental;

import org.TraceVerifier.Trace.Generators.Generic.Experimental.ByteLifetimeTraceGenerator;
import org.TraceVerifier.Trace.Generators.Generic.Experimental.WeightedLifetimeGenerator;
import org.TraceVerifier.Trace.Generators.TraceGenerator;
import org.TraceVerifier.Trace.Trace;

import java.util.Random;

public class TTLCacheTraceGenerator implements TraceGenerator {
    private TTLCacheTraceConfiguration config;

    public TTLCacheTraceGenerator() {
        setConfig(config = new TTLCacheTraceConfiguration());
    }

    public void setConfig(TTLCacheTraceConfiguration config) {
        this.config = config;
        this.config.setVersion("1.0");
    }

    @Override
    public Trace generate(String identifier) {
        Random rand = new Random(config.getSeed());
        WeightedLifetimeGenerator lfGenerator = new WeightedLifetimeGenerator();
        lfGenerator.setLifetimeOffset(0);
        lfGenerator.setNumAllocations(config.getNumAllocations());
        lfGenerator.setWeightedLifetimes(config.getWeightedLifetimes());
        lfGenerator.setWeightedSizes(config.getWeightedSizes());
        lfGenerator.setSeed(rand.nextLong(Long.MAX_VALUE));

        var lfTraceGenerator = new ByteLifetimeTraceGenerator();
        lfTraceGenerator.setTimeQuantum(config.getTimeQuantum());
        lfTraceGenerator.setLifetimeTrace(lfGenerator.createLifetimeTrace());
        lfTraceGenerator.setCollapse(true);

        return lfTraceGenerator.generate(identifier);
    }
}
