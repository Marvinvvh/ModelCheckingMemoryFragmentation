package org.TraceVerifier.Runnables.TraceGen;

import java.nio.file.Path;

/**
 * Shared variables among synthetic generators.
 */
public record SharedSynthGenVars(
        Path pathSyntheticTraces,
        Path pathExportConfigs,
        String fileExtension,
        boolean exportTraceConfigs) {
}