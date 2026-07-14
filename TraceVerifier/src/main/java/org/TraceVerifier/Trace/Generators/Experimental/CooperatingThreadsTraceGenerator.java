package org.TraceVerifier.Trace.Generators.Experimental;

import org.TraceVerifier.Trace.Generators.FreePolicy;
import org.TraceVerifier.Trace.Generators.Generic.Experimental.AllocLifetimeTraceGenerator;
import org.TraceVerifier.Trace.Generators.Generic.Experimental.WeightedLifetimeGenerator;
import org.TraceVerifier.Trace.Generators.TraceGenerator;
import org.TraceVerifier.Trace.Trace;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CooperatingThreadsTraceGenerator implements TraceGenerator {

    private CooperatingThreadsConfiguration config;

    public CooperatingThreadsTraceGenerator() {
    }

    public void setConfig(CooperatingThreadsConfiguration config) {
        this.config = config;
        this.config.setVersion("1.0");
    }

    @Override
    public Trace generate(String identifier) {
        Trace trace = new Trace();
        Random rand = new Random(config.getSeed());

        WeightedLifetimeGenerator lfGenerator = new WeightedLifetimeGenerator();
        lfGenerator.setLifetimeOffset(0);
        lfGenerator.setNumAllocations(config.getNumAllocations());
        lfGenerator.setWeightedLifetimes(config.getWeightedLifetimes());
        lfGenerator.setWeightedSizes(config.getWeightedSizes());
        lfGenerator.setSeed(rand.nextLong(Long.MAX_VALUE));

        List<AllocLifetimeTraceGenerator> lifetimeTraceGenerators = new ArrayList<>();
        List<Integer> lifetimeTimeTracking = new ArrayList<>();
        for (int i = 0; i < config.getAmtThreads(); i++) {
            var lfTraceGenerator = new AllocLifetimeTraceGenerator();
            lfTraceGenerator.setSeed(rand.nextLong(Long.MAX_VALUE));
            lfTraceGenerator.setFreePolicy(FreePolicy.FIFO);
            lfTraceGenerator.setLifetimeTrace(lfGenerator.createLifetimeTrace());
            lfTraceGenerator.setMinAllocations(config.getMinAllocationsTimeQuantum());
            lfTraceGenerator.setMaxAllocations(config.getMaxAllocationsTimeQuantum());
            lfTraceGenerator.setPointerOffset(config.getNumAllocations() * i); // Amt of allocs + frees per thread, so thread has a pointer offset by this amount.
            lfTraceGenerator.generate("part_" + i);
            lifetimeTraceGenerators.add(lfTraceGenerator);
            lifetimeTimeTracking.add(0);
        }

        while (!lifetimeTraceGenerators.isEmpty()) {
            int generatorIndex = rand.nextInt(lifetimeTraceGenerators.size());
            var generator = lifetimeTraceGenerators.get(generatorIndex);
            int generatorTime = lifetimeTimeTracking.get(generatorIndex);
            trace.append(generator.getTraceAtTime(generatorTime));
            lifetimeTimeTracking.set(generatorIndex, generatorTime + 1);
            if (lifetimeTimeTracking.get(generatorIndex) == generator.getTimePassed()) {
                lifetimeTraceGenerators.remove(generatorIndex);
                lifetimeTimeTracking.remove(generatorIndex);
            }
        }

        trace.setIdentifier(identifier);
        return trace;
    }
}
