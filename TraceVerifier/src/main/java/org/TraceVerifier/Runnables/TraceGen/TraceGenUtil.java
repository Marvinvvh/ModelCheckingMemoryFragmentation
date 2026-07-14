package org.TraceVerifier.Runnables.TraceGen;

import org.TraceVerifier.IO.IOModelManager;
import org.TraceVerifier.Trace.Generators.Benchmarks.BenchmarkConfiguration;
import org.TraceVerifier.Trace.Generators.TraceGenerator;
import org.TraceVerifier.Trace.Trace;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Reusable static functions for trace generation.
 */
public class TraceGenUtil {
    /**
     * Generates and exports synthetic traces.
     */
    public static void generateSyntheticTrace(TraceGenerator traceGenerator, String traceGroupID, String traceID, SharedSynthGenVars sharedVars) throws IOException {
        Trace trace = traceGenerator.generate(traceID);
        IOModelManager.getInstance().Export(
                Path.of(sharedVars.pathSyntheticTraces().toString(), traceGroupID),
                traceID + sharedVars.fileExtension(),
                trace,
                true
        );
    }

    /**
     * Exports generator config.
     */
    public static <T extends BenchmarkConfiguration> void saveGeneratorConfig(T config, Path exportConfigDir, String configID, String fileExtension) throws IOException {
        IOModelManager.getInstance().Export(
                exportConfigDir,
                configID + fileExtension,
                config,
                true);
    }
}
