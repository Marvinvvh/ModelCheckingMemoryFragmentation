package org.TraceVerifier.Model;

import org.TraceVerifier.Trace.Trace;

/**
 * Variables that change depending on the trace that's given to a model.
 */
public interface ModelVariables {
    public void processTrace(Trace trace);

    public String getIdentifier();

    public String deriveGlobalVariables();
}
