package org.TraceVerifier.Verification.Traces;

import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Trace.TraceActionAlloc;
import org.TraceVerifier.Trace.TraceActionFree;
import org.TraceVerifier.Trace.TraceActionInvalid;

/**
 * Class that contains all Traces only used for the Trace.
 */
public class VerificationTracesTrace {

    // Fault-induction trace, somehow.
    public static Trace CheckBoundedInvalidAllocationActions(String identifier) {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);

        trace.append(new TraceActionAlloc(0, 1));
        trace.append(new TraceActionInvalid(1, 1)); // This action should never be parsed within the model.
        trace.append(new TraceActionFree(0));
        return trace;
    }

}
