package org.TraceVerifier.Verification.Traces;

import org.TraceVerifier.Model.SharedModelSettings;
import org.TraceVerifier.Trace.Trace;

/**
 * Class that contains all Traces only used for the Heap Interface.
 */
public class VerificationTracesHeapInterface {

    public static Trace generateFragExample(String identifier, SharedModelSettings settings)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);

        int sm = settings.getSizeMultiple() == 1 ? 1 : settings.getSizeMultiple() / 2;
        Trace LowFrag = VerificationTracesShared.generateTriangleAlternate(identifier, settings, 16, sm);
        Trace HighFrag = VerificationTracesShared.generateTriangleAlternate(identifier, settings, 16, sm);
        HighFrag.continueFromTrace(LowFrag);

        trace.append(LowFrag);
        trace.append(HighFrag);

        return trace;
    }
}
