package org.TraceVerifier.Verification.Traces;

import org.TraceVerifier.Model.SharedModelSettings;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Trace.TraceActionAlloc;
import org.TraceVerifier.Trace.TraceActionFree;

/**
 * Class that contains all Traces that are shared amongst model aspects.
 */
public class VerificationTracesShared {
    public static Trace generateStandardizedTrace(String identifier, SharedModelSettings settings)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);

        // Test initial block placement at start
        // T = 1
        trace.append(new TraceActionAlloc(0, 1));
        // T = 2
        trace.append(new TraceActionFree(0));

        int pageSize = settings.getPageSize();

        // Test reinit same block
        // Test whether allocated memory is contiguous.
        // T = 3-6
        int offset = settings.getSizeMultiple() / 2;
        trace.append(new TraceActionAlloc(1, pageSize * 2 - offset));
        trace.append(new TraceActionAlloc(2, pageSize * 4 - offset));
        trace.append(new TraceActionAlloc(3, pageSize * 4 - offset));
        trace.append(new TraceActionAlloc(4, pageSize * 2 - offset));

        // T = 7-11
        trace.append(new TraceActionFree(1));
        trace.append(new TraceActionFree(2));
        trace.append(new TraceActionFree(3));
        trace.append(new TraceActionFree(4));

        return trace;
    }

    public static Trace generateStandardizedTraceVariant(String identifier, SharedModelSettings settings)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);
        int pageSize = settings.getPageSize();

        // Test initial block placement at start
        // T = 1
        trace.append(new TraceActionAlloc(0, pageSize));
        // T = 2
        trace.append(new TraceActionFree(0));


        // Test reinit same block
        // Test whether allocated memory is contiguous.
        // T = 3-6
        int offset = settings.getSizeMultiple() / 2;
        trace.append(new TraceActionAlloc(1, pageSize * 2 - offset));
        trace.append(new TraceActionAlloc(2, pageSize * 4 - offset));
        trace.append(new TraceActionAlloc(3, pageSize * 4 - offset));
        trace.append(new TraceActionAlloc(4, pageSize * 2 - offset));

        // T = 7-11
        trace.append(new TraceActionFree(1));
        trace.append(new TraceActionFree(2));
        trace.append(new TraceActionFree(3));
        trace.append(new TraceActionFree(4));

        return trace;
    }

    public static Trace generateTriangleAlternate(String identifier, SharedModelSettings settings, int steps, int stepsize)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);

        for(int i = 0; i < steps; i++)
        {
            trace.append(new TraceActionAlloc(i, stepsize));
        }

        // Creates layered frees based on pointer. Allows for splitting free list in A-F-A-F blocks per page.

        // All even
        for(int i = 0; i < steps; i+=2)
        {
            trace.append(new TraceActionFree(i));
        }

        // All uneven. Free the rest.
        for(int i = 1; i < steps; i+=2)
        {
            trace.append(new TraceActionFree(i));
        }

        return trace;
    }

    public static Trace generateTriangleAllocSameSize(String identifier, SharedModelSettings settings, int stepsize)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);
        int numAllocations = settings.getPageSize() * settings.getAmountOfPages() / stepsize;
        for(int i = 0; i < numAllocations; i++)
        {
            trace.append(new TraceActionAlloc(i, stepsize));
        }

        // Free all even.
        for(int i = 0; i < numAllocations; i+=2)
        {
            trace.append(new TraceActionFree(i));
        }
        
        // All uneven. Free the rest.
        for(int i = 0; i < numAllocations / 2; i++)
        {
            int allocNum = i + numAllocations;
            trace.append(new TraceActionAlloc(allocNum, stepsize));
        }

        return trace;
    }

    public static Trace generateTriangleAllocSameSizeExhaustion(String identifier, SharedModelSettings settings, int stepsize)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);
        int numAllocations = settings.getPageSize() * settings.getAmountOfPages() / stepsize;
        for(int i = 0; i < numAllocations; i++)
        {
            trace.append(new TraceActionAlloc(i, stepsize));
        }

        // All even
        for(int i = 0; i < numAllocations; i+=2)
        {
            trace.append(new TraceActionFree(i));
        }

        trace.append(new TraceActionAlloc(numAllocations+1, settings.getHeapSize()));


        return trace;
    }

    public static Trace generateAllocABBAEvenFree(String identifier, SharedModelSettings settings, int numSequences, int stepsizeA, int stepsizeB, int allocSize, int numAllocs)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);
        int numAllocations = numSequences * 4;

        for(int i = 0; i < numSequences; i++)
        {
            for(int allocNumSeq = 0; allocNumSeq < 4; allocNumSeq++)
            {
                boolean stepA = allocNumSeq == 0 || allocNumSeq == 3;
                int size = stepA ? stepsizeA : stepsizeB;
                trace.append(new TraceActionAlloc(i * 4 + allocNumSeq, size));
            }
        }

        // All even
        for(int i = 0; i < numAllocations; i+=2)
        {
            trace.append(new TraceActionFree(i));
        }

        for(int i = 0; i < numAllocs; i++)
        {
            int allocNum = i + numAllocations;
            trace.append(new TraceActionAlloc(allocNum, allocSize));
            trace.append(new TraceActionAlloc(allocNum, allocSize));
        }

        return trace;
    }

    public static Trace generateTriangle(String identifier, SharedModelSettings settings, int allocations, int stepsize)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);

        for(int i = 0; i < allocations; i++)
        {
            trace.append(new TraceActionAlloc(i, stepsize));
        }

        for(int i = 0; i < allocations; i++)
        {
            trace.append(new TraceActionFree(i));
        }

        return trace;
    }

    public static Trace AllocatorErrorOOB(String identifier, SharedModelSettings settings)
    {
        int allocSize = settings.getHeapSize() * 2;
        Trace trace = new Trace();
        trace.setIdentifier(identifier);

        trace.append(new TraceActionAlloc(0, allocSize));
        // We add a free just so it cannot transition to done in some way, needs to process another action.
        trace.append(new TraceActionFree(0));
        return trace;
    }
}
