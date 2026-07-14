package org.TraceVerifier.Trace.Generators;

import org.TraceVerifier.Trace.Trace;

public interface TraceGenerator {
    Trace generate(String identifier);
}
