package org.TraceVerifier.Verification.Traces;

import org.TraceVerifier.Model.SharedModelSettings;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Trace.TraceActionAlloc;
import org.TraceVerifier.Trace.TraceActionFree;


/**
 * Class that contains all Traces only used for the XFAO.
 */
public class VerificationTracesXFAO {


    public static Trace generateDoubleFree(String identifier)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);
        trace.append(new TraceActionAlloc(0, 1));
        trace.append(new TraceActionFree(0));
        trace.append(new TraceActionFree(0)); // <-- Point of double free
        trace.append(new TraceActionAlloc(1, 1));
        trace.append(new TraceActionFree(1));
        return trace;
    }

    public static Trace generateUnallocatedFree(String identifier)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);
        trace.append(new TraceActionAlloc(0, 1));
        trace.append(new TraceActionFree(0));
        trace.append(new TraceActionFree(1)); // <-- Point of unalloc free
        trace.append(new TraceActionAlloc(1, 1));
        trace.append(new TraceActionFree(1));
        return trace;
    }

    public static Trace generateDuplicatePointer(String identifier)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);
        trace.append(new TraceActionAlloc(0, 1));
        trace.append(new TraceActionAlloc(0, 1)); // <-- Point of duplicate pointer
        trace.append(new TraceActionFree(0));
        return trace;
    }

    public static Trace generateMemoryExhaustionByFragmentation(String identifier, SharedModelSettings settings)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);

        // Create heap like:
        // [0, SM)                      FREE
        // [SM, HEAP_SIZE - SM)         ALLOC
        // (HEAP_SIZE - SM, HEAP_SIZE)  FREE
        // All possible with XFAO, and allows to test 2*SM alloc, which fails due to fragmentation.

        trace.append(new TraceActionAlloc(0, settings.getSizeMultiple()));
        trace.append(new TraceActionAlloc(1, settings.getHeapSize() - 2*settings.getSizeMultiple()));
        trace.append(new TraceActionFree(0));
        trace.append(new TraceActionAlloc(2, 2 * settings.getSizeMultiple())); // <-- Point of memory fragmentation
        trace.append(new TraceActionFree(1));
        trace.append(new TraceActionFree(2));

        return trace;
    }

    public static Trace generateInvalidSized(String identifier)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);
        trace.append(new TraceActionAlloc(0, -1));
        trace.append(new TraceActionFree(0));
        return trace;
    }

    public static Trace generateOOBSize(String identifier, SharedModelSettings config)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);
        trace.append(new TraceActionAlloc(0, config.getHeapSize() + 1));
        trace.append(new TraceActionFree(0));
        return trace;
    }
}
