package org.TraceVerifier.Runnables.TraceGen;

import org.TraceVerifier.IO.IOModelManager;
import org.TraceVerifier.Trace.Generators.Experimental.BatchBufferTraceConfiguration;
import org.TraceVerifier.Trace.Generators.Benchmarks.*;
import org.TraceVerifier.Trace.Generators.FreePolicy;
import org.TraceVerifier.Trace.ProgramTrace.ProgramTraceReader;
import org.TraceVerifier.Trace.ProgramTrace.ProgramTraceReaderJsonLines;
import org.TraceVerifier.Trace.TraceGroup;
import org.TraceVerifier.Trace.TraceGroupIdentifier;
import org.TraceVerifier.Trace.Weights.WeightedRange;
import org.TraceVerifier.Util.File.PromptedFileWipes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;


/**
 * Runnable for creating traces from a single base seed. We used this originally to experiment with different seeds.
 * But this is less clear in displaying how the configurations of the benchmark map to actual trace generation.
 */
public class TraceGenSingleOrigin {

    public static void run(Path programTraceDirectory,
                           Path allocationTraceDirectory,
                           boolean cleanAllocDirs,
                           Path generatorConfigDirectory,
                           boolean exportGeneratorConfigs,
                           boolean cleanConfigDirs,
                           long seedGenerators) throws IOException {
        try {
            if (cleanAllocDirs && Files.exists(allocationTraceDirectory)) {
                if (!Files.isDirectory(allocationTraceDirectory)) {
                    throw new IOException("Invalid allocation trace directory");
                }
                PromptedFileWipes.YesNoQuitSingleDir(allocationTraceDirectory, null);
            }

            if (cleanConfigDirs && Files.exists(generatorConfigDirectory)) {
                if (!Files.isDirectory(generatorConfigDirectory)) {
                    throw new IOException("Invalid generator config directory");
                }
                PromptedFileWipes.YesNoQuitSingleDir(generatorConfigDirectory, null);
            }

            processSyntheticTraces(allocationTraceDirectory, generatorConfigDirectory, exportGeneratorConfigs, seedGenerators);
            processProgramTraces(programTraceDirectory, allocationTraceDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate program trace groups.
     */
    private static void processProgramTraces(Path programTraceDirectory, Path allocationTraceDirectory) throws IOException {
        processProgramTraceGroup(Path.of(programTraceDirectory.toString(), "larson"), allocationTraceDirectory, "program_larson", "glob:**/larson/*.*");
        processProgramTraceGroup(Path.of(programTraceDirectory.toString(), "cfrac"), allocationTraceDirectory, "program_cfrac", "glob:**/cfrac/*.*");
    }

    private static void processProgramTraceGroup(Path programTraceDirectory, Path allocationTraceDirectory, String traceID, String patternTrace) {
        ProgramTraceReader programTraceReader = new ProgramTraceReaderJsonLines();
        TraceGroup traceGroup;
        try {
            traceGroup = programTraceReader.fromDirectory(
                    new TraceGroupIdentifier(traceID),
                    programTraceDirectory,
                    patternTrace);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (var trace : traceGroup.getTraces()) {
            try {
                IOModelManager.getInstance().Export(
                        Path.of(allocationTraceDirectory.toString(), "program", traceID),
                        trace.getIdentifier() + ".json",
                        trace,
                        true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void processSyntheticTraces(Path allocationTraceDirectory, Path exportConfigDirectory, boolean exportGeneratorConfigs, long seedGenerators) throws IOException {

        if (exportGeneratorConfigs && Files.exists(exportConfigDirectory) && !Files.isDirectory(exportConfigDirectory)) {
            throw new IOException("Invalid generator config output");
        }

        // Note that while we set the seed when retrieving the base config, the seeds of the generators is actually generated with this RNGen.
        Random rand = new Random(seedGenerators);
        int startTraceNum = 0;
        int amtOfSynthTraces = 5;

        // Shared vars
        SharedSynthGenVars sharedVars = new SharedSynthGenVars
                (
                        Path.of(allocationTraceDirectory.toString(), "synthetic"),
                        exportConfigDirectory,
                        ".json",
                        exportGeneratorConfigs
                );

        // --- Simulation ---
        SimTraceGenerator simTraceGenerator = new SimTraceGenerator();
        SynthTraceGen<SimTraceConfiguration> simTraceInfo = getSimTraceInfo(seedGenerators, amtOfSynthTraces);
        Path simConfigPath = Path.of(sharedVars.pathExportConfigs().toString(), simTraceInfo.traceIdentifier());
        for (int i = startTraceNum; i < simTraceInfo.amtOfTraces() + startTraceNum; i++) {
            var config = simTraceInfo.baseConfig();
            config.setSeed(rand.nextLong(Long.MAX_VALUE));
            simTraceGenerator.setConfig(config);

            String traceID = simTraceInfo.traceIdentifier() + "_" + i;
            TraceGenUtil.generateSyntheticTrace(simTraceGenerator, simTraceInfo.traceIdentifier(), traceID, sharedVars);
            if (sharedVars.exportTraceConfigs()) {
                TraceGenUtil.saveGeneratorConfig(config, simConfigPath, traceID, sharedVars.fileExtension());
            }
        }

        // --- RANDOMIZED ---
        RandomizedTraceGenerator evenRandomizedTraceGenerator = new RandomizedTraceGenerator();
        SynthTraceGen<RandomizedTraceConfiguration> evenRandomizedTraceInfo = getRandomizedTraceInfo_EvenSpread(seedGenerators, amtOfSynthTraces);
        Path evenRandomizedPath = Path.of(sharedVars.pathExportConfigs().toString(), evenRandomizedTraceInfo.traceIdentifier());
        for (int i = startTraceNum; i < evenRandomizedTraceInfo.amtOfTraces() + startTraceNum; i++) {
            var config = evenRandomizedTraceInfo.baseConfig();
            config.setSeed(rand.nextLong(Long.MAX_VALUE));
            evenRandomizedTraceGenerator.setConfig(config);

            String traceID = evenRandomizedTraceInfo.traceIdentifier() + "_" + i;
            TraceGenUtil.generateSyntheticTrace(evenRandomizedTraceGenerator, evenRandomizedTraceInfo.traceIdentifier(), traceID, sharedVars);
            if (sharedVars.exportTraceConfigs()) {
                TraceGenUtil.saveGeneratorConfig(config, evenRandomizedPath, traceID, sharedVars.fileExtension());
            }
        }

        // --- Stack ---
        StackTraceGenerator stackTraceGenerator = new StackTraceGenerator();
        SynthTraceGen<StackTraceConfiguration> stackTraceInfo = getStackTraceInfo(seedGenerators, amtOfSynthTraces);
        Path stackConfigPath = Path.of(sharedVars.pathExportConfigs().toString(), stackTraceInfo.traceIdentifier());
        for (int i = startTraceNum; i < stackTraceInfo.amtOfTraces() + startTraceNum; i++) {
            var config = stackTraceInfo.baseConfig();
            config.setSeed(rand.nextLong(Long.MAX_VALUE));
            stackTraceGenerator.setConfig(config);

            String traceID = stackTraceInfo.traceIdentifier() + "_" + i;
            TraceGenUtil.generateSyntheticTrace(stackTraceGenerator, stackTraceInfo.traceIdentifier(), traceID, sharedVars);
            if (sharedVars.exportTraceConfigs()) {
                TraceGenUtil.saveGeneratorConfig(config, stackConfigPath, traceID, sharedVars.fileExtension());
            }
        }
    }

    private static SynthTraceGen<BatchBufferTraceConfiguration> getBatchBufferTraceInfo(long seed, int amtOfSynthTraces) {
        BatchBufferTraceConfiguration batchBufferTraceConfiguration = new BatchBufferTraceConfiguration();
        batchBufferTraceConfiguration.setSeed(seed);
        batchBufferTraceConfiguration.addWeightPeaks(new WeightedRange(1, 5, 10, 1));
        batchBufferTraceConfiguration.addWeightAllocsPerPeak(new WeightedRange(1, 1, 5, 50));
        batchBufferTraceConfiguration.addWeightSizeAllocs(new WeightedRange(1, 1, 1024, 1));
        return new SynthTraceGen<>(
                batchBufferTraceConfiguration,
                amtOfSynthTraces,
                "synth_batch_buffer"
        );
    }

    private static SynthTraceGen<SimTraceConfiguration> getSimTraceInfo(long seed, int amtOfSynthTraces) {
        SimTraceConfiguration simTraceConfiguration = new SimTraceConfiguration();
        simTraceConfiguration.setSeed(seed);
        simTraceConfiguration.setRounds(10);
        // Init
        simTraceConfiguration.addWeightSizesInit(new WeightedRange(1, 512, 2048, 1));
        simTraceConfiguration.setNumAllocationsInit(50);
        // Steady State
        simTraceConfiguration.addWeightSizesSteadyState(new WeightedRange(1, 1, 512, 1));
        simTraceConfiguration.setNumAllocationsRound(200);
        simTraceConfiguration.setFreePolicySteadyState( FreePolicy.FIFO);
        // De-init
        simTraceConfiguration.setFreePolicyDeinit(FreePolicy.FIFO);

        return new SynthTraceGen<>(
                simTraceConfiguration,
                amtOfSynthTraces,
                "synth_sim"
        );
    }

    private static SynthTraceGen<RandomizedTraceConfiguration> getRandomizedTraceInfo_EvenSpread(long seed, int amtOfSynthTraces) {
        RandomizedTraceConfiguration randomizedTraceConfiguration = new RandomizedTraceConfiguration();
        randomizedTraceConfiguration.setFreePolicy(FreePolicy.FIFO);
        randomizedTraceConfiguration.setNumAllocations(2000);
        randomizedTraceConfiguration.setAllocationWeights(1, 1);
        randomizedTraceConfiguration.addWeightSizes(new WeightedRange(1, 1, 4096, 1));
        randomizedTraceConfiguration.setSeed(seed);
        return new SynthTraceGen<>(
                randomizedTraceConfiguration,
                amtOfSynthTraces,
                "synth_randomized_even_spread"
        );
    }

    private static SynthTraceGen<StackTraceConfiguration> getStackTraceInfo(long seed, int amtOfSynthTraces) {
        StackTraceConfiguration stackTraceConfiguration = new StackTraceConfiguration();
        stackTraceConfiguration.setMaxDepth(200);
        stackTraceConfiguration.setNumAllocations(2000);
        stackTraceConfiguration.setWeightGrowthDirection(1, 1);
        stackTraceConfiguration.addWeightDepthStepIncr(new WeightedRange(1, 1, 20, 1));
        stackTraceConfiguration.addWeightDepthStepDecr(new WeightedRange(1, 1, 20, 1));
        stackTraceConfiguration.addWeightAllocationSize(new WeightedRange(90, 1, 256, 1));
        stackTraceConfiguration.addWeightAllocationSize(new WeightedRange(9, 256, 2048, 1));
        stackTraceConfiguration.addWeightAllocationSize(new WeightedRange(1, 2048, 4096, 1));
        stackTraceConfiguration.setSeed(seed);

        return new SynthTraceGen<>(
                stackTraceConfiguration,
                amtOfSynthTraces,
                "synth_stack"
        );
    }
}
