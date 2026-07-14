package org.TraceVerifier.Runnables.TraceGen;

import org.TraceVerifier.Trace.Generators.Benchmarks.BenchmarkConfiguration;

/**
 * Generic for different benchmark configurations.
 */
record SynthTraceGen<T extends BenchmarkConfiguration>(T baseConfig,
                                                       int amtOfTraces,
                                                       String traceIdentifier) {
}