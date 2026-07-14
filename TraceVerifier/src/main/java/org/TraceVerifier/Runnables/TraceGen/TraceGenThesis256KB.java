package org.TraceVerifier.Runnables.TraceGen;

import org.TraceVerifier.IO.IOModelManager;
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
import java.util.ArrayList;
import java.util.List;


/**
 * Runnable for creating traces for the benchmark.
 */
public class TraceGenThesis256KB {

    public static void run(Path programTraceDirectory,
                           Path allocationTraceDirectory,
                           boolean cleanAllocDirs,
                           Path generatorConfigDirectory,
                           boolean exportGeneratorConfigs,
                           boolean cleanConfigDirs) throws IOException {
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

            processSyntheticTraces(allocationTraceDirectory, generatorConfigDirectory, exportGeneratorConfigs);
            processProgramTraces(programTraceDirectory, allocationTraceDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void processProgramTraces(Path programTraceDirectory, Path allocationTraceDirectory) throws IOException {
        processProgramTraceGroup(Path.of(programTraceDirectory.toString(), "larson"), allocationTraceDirectory, "program_larson", "glob:**/larson/*.*");
        processProgramTraceGroup(Path.of(programTraceDirectory.toString(), "cfrac"), allocationTraceDirectory, "program_cfrac", "glob:**/cfrac/*[0-2].*");
    }

    private static void processProgramTraceGroup(Path programTraceDirectory, Path allocationTraceDirectory, String traceID, String patternTrace) {
        ProgramTraceReader programTraceReader = new ProgramTraceReaderJsonLines();
        TraceGroup traceGroup;

        // Read program traces from directory into allocation traces.
        try {
            traceGroup = programTraceReader.fromDirectory(
                    new TraceGroupIdentifier(traceID),
                    programTraceDirectory,
                    patternTrace);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Save each program allocation trace.
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

    public static void processSyntheticTraces(Path allocationTraceDirectory, Path exportConfigDirectory, boolean exportGeneratorConfigs) throws IOException {

        if (exportGeneratorConfigs && Files.exists(exportConfigDirectory) && !Files.isDirectory(exportConfigDirectory)) {
            throw new IOException("Invalid generator config output");
        }

        // Shared vars
        SharedSynthGenVars sharedVars = new SharedSynthGenVars
                (
                        Path.of(allocationTraceDirectory.toString(), "synthetic"),
                        exportConfigDirectory,
                        ".json",
                        exportGeneratorConfigs
                );

        // For each trace generator, generate the three seeds used for the benchmark.
        // --- Simulation ---
        List<Long> seedSim = new ArrayList<>();
        seedSim.add(8074257794302175870L); // T0
        seedSim.add(6604381828566711277L); // T1
        seedSim.add(663317486552589301L);  // T2

        SimTraceGenerator simTraceGenerator = new SimTraceGenerator();
        SynthTraceGen<SimTraceConfiguration> simTraceInfo = getSimTraceInfo(seedSim.size());
        Path simConfigPath = Path.of(sharedVars.pathExportConfigs().toString(), simTraceInfo.traceIdentifier());

        for (int i = 0; i < seedSim.size(); i++) {
            var config = simTraceInfo.baseConfig();
            config.setSeed(seedSim.get(i));
            simTraceGenerator.setConfig(config);

            String traceID = simTraceInfo.traceIdentifier() + "_" + i;
            TraceGenUtil.generateSyntheticTrace(simTraceGenerator, simTraceInfo.traceIdentifier(), traceID, sharedVars);
            if (sharedVars.exportTraceConfigs()) {
                TraceGenUtil.saveGeneratorConfig(config, simConfigPath, traceID, sharedVars.fileExtension());
            }
        }

        // --- RANDOMIZED ---
        List<Long> seedRandomized = new ArrayList<>();
        seedRandomized.add(1949870943162229938L); // T0
        seedRandomized.add(5665107177781614002L); // T1
        seedRandomized.add(308594831385502389L);  // T2

        RandomizedTraceGenerator evenRandomizedTraceGenerator = new RandomizedTraceGenerator();
        SynthTraceGen<RandomizedTraceConfiguration> evenRandomizedTraceInfo = getRandomizedTraceInfo_EvenSpread(seedRandomized.size());
        Path evenRandomizedPath = Path.of(sharedVars.pathExportConfigs().toString(), evenRandomizedTraceInfo.traceIdentifier());

        for (int i = 0; i < seedRandomized.size(); i++) {
            var config = evenRandomizedTraceInfo.baseConfig();
            config.setSeed(seedRandomized.get(i));
            evenRandomizedTraceGenerator.setConfig(config);

            String traceID = evenRandomizedTraceInfo.traceIdentifier() + "_" + i;
            TraceGenUtil.generateSyntheticTrace(evenRandomizedTraceGenerator, evenRandomizedTraceInfo.traceIdentifier(), traceID, sharedVars);
            if (sharedVars.exportTraceConfigs()) {
                TraceGenUtil.saveGeneratorConfig(config, evenRandomizedPath, traceID, sharedVars.fileExtension());
            }
        }

        // --- Stack ---
        List<Long> seedStack = new ArrayList<>();
        seedStack.add(694141187955482835L); // T0
        seedStack.add(8732741093663241232L); // T1
        seedStack.add(4029475597569372662L);  // T2

        StackTraceGenerator stackTraceGenerator = new StackTraceGenerator();
        SynthTraceGen<StackTraceConfiguration> stackTraceInfo = getStackTraceInfo(seedStack.size());
        Path stackConfigPath = Path.of(sharedVars.pathExportConfigs().toString(), stackTraceInfo.traceIdentifier());
        for (int i = 0; i < seedStack.size(); i++) {
            var config = stackTraceInfo.baseConfig();
            config.setSeed(seedStack.get(i));
            stackTraceGenerator.setConfig(config);

            String traceID = stackTraceInfo.traceIdentifier() + "_" + i;
            TraceGenUtil.generateSyntheticTrace(stackTraceGenerator, stackTraceInfo.traceIdentifier(), traceID, sharedVars);
            if (sharedVars.exportTraceConfigs()) {
                TraceGenUtil.saveGeneratorConfig(config, stackConfigPath, traceID, sharedVars.fileExtension());
            }
        }
    }

    private static SynthTraceGen<SimTraceConfiguration> getSimTraceInfo(int amtOfSynthTraces) {
        SimTraceConfiguration simTraceConfiguration = new SimTraceConfiguration();
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

    private static SynthTraceGen<RandomizedTraceConfiguration> getRandomizedTraceInfo_EvenSpread(int amtOfSynthTraces) {
        RandomizedTraceConfiguration randomizedTraceConfiguration = new RandomizedTraceConfiguration();
        randomizedTraceConfiguration.setFreePolicy(FreePolicy.FIFO);
        randomizedTraceConfiguration.setNumAllocations(2000);
        randomizedTraceConfiguration.setAllocationWeights(1, 1);
        randomizedTraceConfiguration.addWeightSizes(new WeightedRange(1, 1, 4096, 1));
        return new SynthTraceGen<>(
                randomizedTraceConfiguration,
                amtOfSynthTraces,
                "synth_randomized_even_spread"
        );
    }

    private static SynthTraceGen<StackTraceConfiguration> getStackTraceInfo(int amtOfSynthTraces) {
        StackTraceConfiguration stackTraceConfiguration = new StackTraceConfiguration();
        stackTraceConfiguration.setMaxDepth(200);
        stackTraceConfiguration.setNumAllocations(2000);
        stackTraceConfiguration.setWeightGrowthDirection(1, 1);
        stackTraceConfiguration.addWeightDepthStepIncr(new WeightedRange(1, 1, 20, 1));
        stackTraceConfiguration.addWeightDepthStepDecr(new WeightedRange(1, 1, 20, 1));
        stackTraceConfiguration.addWeightAllocationSize(new WeightedRange(90, 1, 256, 1));
        stackTraceConfiguration.addWeightAllocationSize(new WeightedRange(9, 256, 2048, 1));
        stackTraceConfiguration.addWeightAllocationSize(new WeightedRange(1, 2048, 4096, 1));

        return new SynthTraceGen<>(
                stackTraceConfiguration,
                amtOfSynthTraces,
                "synth_stack"
        );
    }
}
