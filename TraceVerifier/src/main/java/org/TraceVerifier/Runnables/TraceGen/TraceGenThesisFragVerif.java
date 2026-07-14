package org.TraceVerifier.Runnables.TraceGen;

import org.TraceVerifier.Trace.Generators.FragVerif.FragVerifConfiguration;
import org.TraceVerifier.Trace.Generators.FragVerif.FragVerifTraceGenerator;
import org.TraceVerifier.Util.File.PromptedFileWipes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Runnable for generating the fragverif traces.
 */
public class TraceGenThesisFragVerif {

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

            generateTraces(allocationTraceDirectory, generatorConfigDirectory, exportGeneratorConfigs, seedGenerators);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void generateTraces(Path allocationTraceDirectory, Path exportConfigDirectory, boolean exportGeneratorConfigs, long seedGenerators) throws IOException {

        if (exportGeneratorConfigs && Files.exists(exportConfigDirectory) && !Files.isDirectory(exportConfigDirectory)) {
            throw new IOException("Invalid generator config output");
        }

        // Shared vars
        SharedSynthGenVars sharedVars = new SharedSynthGenVars
                (
                        allocationTraceDirectory,
                        exportConfigDirectory,
                        ".json",
                        exportGeneratorConfigs
                );

        // Note that while we set the seed when retrieving the base config, the seeds of the generators is actually generated with this RNGen.
        Random rand = new Random(seedGenerators);

        // Shared vars
        int numAllocationsPerPeak = 200;
        List<Integer> sizes = new ArrayList<>();
        sizes.add(100);
        sizes.add(200);

        // For each size base we create a fragverif trace.
        for(var sizeBase : sizes)
        {
            FragVerifTraceGenerator generator = new FragVerifTraceGenerator();
            SynthTraceGen<FragVerifConfiguration> traceInfo = getTraceInfo(seedGenerators, sizeBase, numAllocationsPerPeak);
            Path simConfigPath = Path.of(sharedVars.pathExportConfigs().toString(), traceInfo.traceIdentifier());
            var config = traceInfo.baseConfig();
            config.setSeed(rand.nextLong(Long.MAX_VALUE));
            generator.setConfig(config);

            String traceID = traceInfo.traceIdentifier() + "_" + sizeBase;
            TraceGenUtil.generateSyntheticTrace(generator, traceInfo.traceIdentifier(), traceID, sharedVars);
            if (sharedVars.exportTraceConfigs()) {
                TraceGenUtil.saveGeneratorConfig(config, simConfigPath, traceID, sharedVars.fileExtension());
            }
        }
    }

    private static SynthTraceGen<FragVerifConfiguration> getTraceInfo(long seed, int sizeBase, int numAllocationsPerPeak) {
        FragVerifConfiguration traceConfiguration = new FragVerifConfiguration();
        traceConfiguration.setSeed(seed);
        traceConfiguration.setSizeBase(sizeBase);
        traceConfiguration.setNumAllocationPerPeak(numAllocationsPerPeak);

        return new SynthTraceGen<>(
                traceConfiguration,
                1,
                "fragverif_trace"
        );
    }
}
