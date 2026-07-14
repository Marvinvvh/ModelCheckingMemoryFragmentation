package org.TraceVerifier.Verification.Traces;

import org.TraceVerifier.Model.SharedModelSettings;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Trace.TraceActionAlloc;
import org.TraceVerifier.Trace.TraceActionFree;

/**
 * Class that contains all Traces only used for NFAO.
 */
public class VerificationTracesNFAO
{
    public static Trace generateEnterSecondPhase(boolean includeFirst, String identifier, SharedModelSettings settings)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);

        for(int i = 0; i < 10; i++)
        {
            trace.append(new TraceActionAlloc(i, settings.getSizeMultiple()));
        }

        trace.append(new TraceActionAlloc(trace.getLastPointer() + 1, settings.getPageSize() - 10*settings.getSizeMultiple()));

        // Stop right before last page.
        int startingPointer = trace.getLastPointer() + 1;
        for(int i = 0; i < settings.getAmountOfPages() - 2; i++)
        {
            trace.append(new TraceActionAlloc(startingPointer + i, settings.getPageSize()));
        }

        // Single size multiple block at the end.
        trace.append(new TraceActionAlloc(trace.getLastPointer() + 1, settings.getPageSize() - settings.getSizeMultiple()));

        // Free up spot of 4 blocks
        if(includeFirst)
        {
            trace.append(new TraceActionFree(0));
        }
        trace.append(new TraceActionFree(1));
        trace.append(new TraceActionFree(2));
        trace.append(new TraceActionFree(3));
        trace.append(new TraceActionFree(4));

        // One block gap so we can check whether it chooses the first open block.

        // Free up spot of 4 blocks
        trace.append(new TraceActionFree(6));
        trace.append(new TraceActionFree(7));
        trace.append(new TraceActionFree(8));
        trace.append(new TraceActionFree(9));

        // Attempt to allocate the four block gap. There's only one page left, so it skips to next page.
        trace.append(new TraceActionAlloc(trace.getLastPointer() + 1, settings.getSizeMultiple() * 4));
        // Free up the previous allocation.
        trace.append(new TraceActionFree( trace.getLastPointer()));
        // Check if it starts at the next block.
        trace.append(new TraceActionAlloc(trace.getLastPointer() + 1, settings.getSizeMultiple() * 4));

        return trace;
    }

    public static Trace generateEnterSecondPhaseMultiblock(boolean differentSizes, String identifier, SharedModelSettings settings)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);

        for(int i = 0; i < settings.getAmountOfPages() - 1; i++)
        {
            trace.append(new TraceActionAlloc(i, settings.getSizeMultiple()));
        }

        // Free up spot of 4 blocks
        if(differentSizes)
        {
            trace.append(new TraceActionFree(0));
        }
        trace.append(new TraceActionFree(1));
        trace.append(new TraceActionFree(2));
        trace.append(new TraceActionFree(3));
        trace.append(new TraceActionFree(4));

        // One block gap so we can check whether it chooses the first open block.

        // Free up spot of 4 blocks
        trace.append(new TraceActionFree(6));
        trace.append(new TraceActionFree(7));
        trace.append(new TraceActionFree(8));
        trace.append(new TraceActionFree(9));

        // Attempt to allocate the four block gap. There's only one page left, so it skips to next page.
        trace.append(new TraceActionAlloc(trace.getLastPointer() + 1, settings.getPageSize() * 4));

        return trace;
    }

    public static Trace generateEnterFirstPhase(boolean includeFirst, String identifier, SharedModelSettings settings)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);

        for(int i = 0; i < 10; i++)
        {
            trace.append(new TraceActionAlloc(i, settings.getSizeMultiple()));
        }

        trace.append(new TraceActionAlloc(trace.getLastPointer() + 1, settings.getPageSize() - 10*settings.getSizeMultiple()));


        // Stop at last page
        int startingPointer = trace.getLastPointer() + 1;
        for(int i = 0; i < settings.getAmountOfPages() - 1; i++)
        {
            trace.append(new TraceActionAlloc(startingPointer + i, settings.getPageSize()));
        }

        // Free up spot of 4 blocks
        if(includeFirst)
        {
            trace.append(new TraceActionFree(0));
        }
        trace.append(new TraceActionFree(1));
        trace.append(new TraceActionFree(2));
        trace.append(new TraceActionFree(3));
        trace.append(new TraceActionFree(4));

        // One block gap so we can check whether it chooses the first open block.

        // Free up spot of 4 blocks
        trace.append(new TraceActionFree(6));
        trace.append(new TraceActionFree(7));
        trace.append(new TraceActionFree(8));
        trace.append(new TraceActionFree(9));

        // Allocate the first block
        trace.append(new TraceActionAlloc(trace.getLastPointer() + 1, settings.getSizeMultiple() * 4));
        // Free up the previous allocation.
        trace.append(new TraceActionFree( trace.getLastPointer()));
        // Check if it starts at the next block.
        trace.append(new TraceActionAlloc(trace.getLastPointer() + 1, settings.getSizeMultiple() * 4));

        return trace;
    }


    public static Trace generateEnterFirstPhaseMultiBlock(boolean includeFirst, String identifier, SharedModelSettings settings)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);

        // ALlocate entire heap without gaps
        for(int i = 0; i < settings.getAmountOfPages(); i++)
        {
            trace.append(new TraceActionAlloc(i, settings.getPageSize()));
        }

        // Free up spot of 4 blocks
        if(includeFirst)
        {
            trace.append(new TraceActionFree(0));
        }
        trace.append(new TraceActionFree(1));
        trace.append(new TraceActionFree(2));
        trace.append(new TraceActionFree(3));
        trace.append(new TraceActionFree(4));

        // One page gap so we can check whether it chooses the first open block.

        // Free up spot of 4 pages
        trace.append(new TraceActionFree(6));
        trace.append(new TraceActionFree(7));
        trace.append(new TraceActionFree(8));
        trace.append(new TraceActionFree(9));

        // Attempt to allocate the four block gap. There's only one page left, so it skips to next page.
        trace.append(new TraceActionAlloc(trace.getLastPointer() + 1, settings.getPageSize() * 4));
        // Free up the previous allocation.
        trace.append(new TraceActionFree( trace.getLastPointer()));
        // Check if it starts at the next block.
        trace.append(new TraceActionAlloc(trace.getLastPointer() + 1, settings.getPageSize() * 4));

        return trace;
    }

    public static Trace generateFirstPhaseError(String identifier, SharedModelSettings settings)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);

        // ALlocate entire heap without gaps
        for(int i = 0; i < settings.getAmountOfPages(); i++)
        {
            trace.append(new TraceActionAlloc(i, settings.getPageSize()));
        }

        // Free up spot of 4 pages
        trace.append(new TraceActionFree(1));
        trace.append(new TraceActionFree(2));
        trace.append(new TraceActionFree(3));
        trace.append(new TraceActionFree(4));

        // One page gap so we can check whether it chooses the first open block.

        // Free up spot of 4 pages
        trace.append(new TraceActionFree(6));
        trace.append(new TraceActionFree(7));
        trace.append(new TraceActionFree(8));
        trace.append(new TraceActionFree(9));

        // Forces memory exhaustion and fragmentation because we have 4 free pages, but they're not contiguous.
        trace.append(new TraceActionAlloc(trace.getLastPointer() + 1, settings.getPageSize() * 8));

        return trace;
    }

    public static Trace generateSecondPhaseError(String identifier, SharedModelSettings settings)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);

        // Stop right before last page.
        for(int i = 0; i < settings.getAmountOfPages() - 1; i++)
        {
            trace.append(new TraceActionAlloc(i, settings.getPageSize()));
        }

        // Free up spot of 4 pages
        trace.append(new TraceActionFree(1));
        trace.append(new TraceActionFree(2));
        trace.append(new TraceActionFree(3));
        trace.append(new TraceActionFree(4));

        // One page gap so we can check whether it chooses the first open block.

        // Free up spot of 4 pages
        trace.append(new TraceActionFree(6));
        trace.append(new TraceActionFree(7));
        trace.append(new TraceActionFree(8));
        trace.append(new TraceActionFree(9));


        // Attempt to allocate the four block gap. There's only one page left, so it skips to next page.
        trace.append(new TraceActionAlloc(trace.getLastPointer() + 1, settings.getPageSize() * 4));

        return trace;
    }

}
