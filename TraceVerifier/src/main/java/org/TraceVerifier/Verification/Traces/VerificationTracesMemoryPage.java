package org.TraceVerifier.Verification.Traces;

import org.TraceVerifier.Model.SharedModelSettings;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Trace.TraceActionAlloc;
import org.TraceVerifier.Trace.TraceActionFree;

/**
 * Class that contains all Traces only used for Memory Page.
 */
public class VerificationTracesMemoryPage {


    public static Trace generateMergePatterns(String identifier, SharedModelSettings settings, int pageCount)
    {
        Trace trace = new Trace();
        trace.setIdentifier(identifier);

        int bigBlockSize = settings.getPageSize() - 4*settings.getSizeMultiple();
        int allocCounter = 0;
        for(int i = 0; i < settings.getAmountOfPages(); i++)
        {
            // Each page should contain 5 blocks: 2 small - 1 big - 2 small. This allows for LL on the left, LR on the right, LRRL on the merged ones.
            for(int j = 0; j < 5; j++)
            {
                int stepSize = j == 2 ? bigBlockSize : settings.getSizeMultiple();
                trace.append(new TraceActionAlloc(allocCounter, stepSize));
                allocCounter++;
            }
        }

        int freeCounter = 0;
        for(int i = 0; i < pageCount; i++)
        {
            // LR at the end of the page
            trace.append(new TraceActionFree(freeCounter + 4));
            trace.append(new TraceActionFree(freeCounter + 3));

            // LL at the beginning of the page
            trace.append(new TraceActionFree(freeCounter));
            trace.append(new TraceActionFree(freeCounter + 1));

            // Free block to the left and of the right to th middle allocation.
            // So LRRL in the middle with the merged blocks.
            trace.append(new TraceActionFree(freeCounter + 2));

            freeCounter+=5;
        }


        return trace;
    }
}
