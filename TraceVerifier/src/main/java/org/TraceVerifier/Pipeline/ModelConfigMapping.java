package org.TraceVerifier.Pipeline;

import org.TraceVerifier.Model.ModelConfiguration;

import java.nio.file.Path;

/**
 * Map base model path to configuration.
 */
public record ModelConfigMapping(Path baseModelPath, ModelConfiguration modelConfiguration) {
}
