package org.TraceVerifier.Trace.Generators.FragVerif;

import org.TraceVerifier.Trace.Generators.Generic.WeightedTraceGenerator;
import org.TraceVerifier.Trace.Generators.TraceGenerator;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Trace.Weights.WeightedMap;
import org.TraceVerifier.Trace.Weights.WeightedRange;

import java.util.Random;

/**
 * Generates traces for the fragverif comparison process, which compares these traces with expected values in TraceAnalyzer.
 */
public class FragVerifTraceGenerator implements TraceGenerator {
    private FragVerifConfiguration config;

    public void setConfig(FragVerifConfiguration config) {
        this.config = config;
        this.config.setVersion("1.0");
    }

    /**
     * Pattern looks like this:
     * Generates balanced traces, which are a multiple of some value. The multiple increases/decreases after each trace.
     * Pattern is 1 - 2 - 4 - 2 - 4 - 1
     */
    @Override
    public Trace generate(String identifier) {
        // This could just be done immediately with trace, and not split it up (except init).
        // But the performance impact is rather low and this makes debugging easier.
        Trace trace = new Trace();
        Trace tempTrace;

        Random rand = new Random(config.getSeed());

        WeightedTraceGenerator gen = new WeightedTraceGenerator();
        gen.setSeed(rand.nextLong(Long.MAX_VALUE));
        gen.setBalancedBehavior(config.getNumAllocationsPerPeak());
        gen.setActionWeights(1, 0);
        gen.setFreePolicy(config.getFreePolicy());
        gen.setStartPointerIndex(0);

        WeightedMap weightedMap = new WeightedMap();

        weightedMap.add(new WeightedRange(1, config.getSizeBase(), config.getSizeBase(), 1 ));
        gen.setSizeWeights(weightedMap);
        tempTrace = gen.generate("p");
        trace.append(tempTrace);

        weightedMap.clear();
        weightedMap.add(new WeightedRange(1, config.getSizeBase(), config.getSizeBase(), 2));
        gen.setSizeWeights(weightedMap);
        tempTrace = gen.generate("p");
        tempTrace.continueFromTrace(trace);
        trace.append(tempTrace);

        weightedMap.clear();
        weightedMap.add(new WeightedRange(1, config.getSizeBase(), config.getSizeBase(), 4));
        gen.setSizeWeights(weightedMap);
        tempTrace = gen.generate("p");
        tempTrace.continueFromTrace(trace);
        trace.append(tempTrace);

        weightedMap.clear();
        weightedMap.add(new WeightedRange(1, config.getSizeBase(), config.getSizeBase(), 2));
        gen.setSizeWeights(weightedMap);
        tempTrace = gen.generate("p");
        tempTrace.continueFromTrace(trace);
        trace.append(tempTrace);

        weightedMap.clear();
        weightedMap.add(new WeightedRange(1, config.getSizeBase(), config.getSizeBase(), 4));
        gen.setSizeWeights(weightedMap);
        tempTrace = gen.generate("p");
        tempTrace.continueFromTrace(trace);
        trace.append(tempTrace);

        weightedMap.clear();
        weightedMap.add(new WeightedRange(1, config.getSizeBase(), config.getSizeBase(), 2));
        gen.setSizeWeights(weightedMap);
        tempTrace = gen.generate("p");
        tempTrace.continueFromTrace(trace);
        trace.append(tempTrace);

        weightedMap.clear();
        weightedMap.add(new WeightedRange(1, config.getSizeBase(), config.getSizeBase(), 1));
        gen.setSizeWeights(weightedMap);
        tempTrace = gen.generate("p");
        tempTrace.continueFromTrace(trace);
        trace.append(tempTrace);

        trace.setIdentifier(identifier);
        return trace;
    }
}
