package org.TraceVerifier.Verification.Mergers;

import org.TraceVerifier.Model.ModelConfiguration;
import org.TraceVerifier.Model.ModelMerger;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Verification.Queries.VerificationQueriesTrace;
import org.TraceVerifier.Verification.Traces.VerificationTracesShared;
import org.TraceVerifier.Verification.Traces.VerificationTracesTrace;
import org.TraceVerifier.Verification.VerificationItem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that contains all Trace-related verification items.
 * Refer to the thesis for referencing Requirement Req X.NUM to the properties we verify using these verification items.
 */
public class VerificationMergerTrace extends VerificationMerger {

    public VerificationMergerTrace(Path baseModelPath, ModelConfiguration config, boolean clearExistingQueries) {
        super(baseModelPath, config, clearExistingQueries);
    }

    // Req. Trace.1
    /**
     * Check whether there is only a single action per time tick.
     * Req. Trace.1: Single action processed per tick.
     */
    public List<VerificationItem> SingleActionPerTick() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = VerificationQueriesTrace.SingleActionPerTick();
        trace = VerificationTracesShared.generateStandardizedTrace("SingleActionPerTick", config);
        merger = setupMerger("trace", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }


    /**
     * Check whether the expected allocation action occurs during trace processing.
     * Req. Trace.2: Checks action results for free and alloc
     * Req. Trace.3: Check whether it matches the trace
     * Req. Trace.4: Iterate over trace.
     */
    public List<VerificationItem> ExpectedAllocationAction() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = VerificationQueriesTrace.ExpectedAllocationAction();
        trace = VerificationTracesShared.generateStandardizedTrace("ExpectedAllocationAction", config);
        merger = setupMerger("trace", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }


    /**
     * Check whether expected action count is reachable in done (and error)
     * Req. Trace.5
     */
    public List<VerificationItem> ExpectedActionCountInDone() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = VerificationQueriesTrace.ExpectedActionCountInDone();
        String expectedActionCountInDoneProcessing = "ExpectedActionCountInDoneProcessing";
        trace = VerificationTracesShared.generateStandardizedTrace(expectedActionCountInDoneProcessing, config);
        merger = setupMerger(expectedActionCountInDoneProcessing, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        tq = VerificationQueriesTrace.ExpectedActionCountInDone();
        String expectedActionCountInError = "ExpectedActionCountInError";
        trace = VerificationTracesShared.AllocatorErrorOOB(expectedActionCountInError, config);
        merger = setupMerger(expectedActionCountInError, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }


    /**
     * Check whether trace errors after a fault.
     * Req. Trace.6
     * Req. Trace.8 (PARTIAL): We can't compile the model due to bound limitations for invalid allocation actions.
     */
    public List<VerificationItem> CheckBoundedInvalidAllocationActions() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = VerificationQueriesTrace.CheckBoundedInvalidAllocationActionsError();
        trace = VerificationTracesTrace.CheckBoundedInvalidAllocationActions("CheckBoundedInvalidAllocationActionsError");
        merger = setupMerger("CheckBoundedInvalidAllocationActionsError", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        tq = VerificationQueriesTrace.CheckBoundedInvalidAllocationActionsOK();
        trace = VerificationTracesShared.generateStandardizedTrace("CheckBoundedInvalidAllocationActionsDoneProcessing", config);
        merger = setupMerger("CheckBoundedInvalidAllocationActionsDoneProcessing", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check there are no allocation actions being processed before initiated by the trace after a tick.
     // Req. Trace.7
     // Req. Trace.8 (PARTIAL): We can't induce the 'processing while new tick' error. So can't test that error state.
     */
    public List<VerificationItem> ActionsProcessedBeforeNewAction() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = VerificationQueriesTrace.ActionsProcessedBeforeNewAction(1);
        trace = VerificationTracesShared.generateStandardizedTrace("ActionsProcessedBeforeNewAction", config);
        merger = setupMerger("ActionsProcessedBeforeNewAction", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }



    /**
     * Check whether processing stops after there are no allocators left to process.
     // Req. Trace.9
     */
    public List<VerificationItem> ProcessingStopsAfterNoAllocatorsLeft() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = VerificationQueriesTrace.ProcessingStopsAfterNoAllocatorsLeft();
        trace = VerificationTracesShared.generateStandardizedTrace("ProcessingStopsAfterNoAllocatorsLeftDoneProcessing", config);
        merger = setupMerger("ProcessingStopsAfterNoAllocatorsLeftDoneProcessing", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        tq = VerificationQueriesTrace.ProcessingStopsAfterNoAllocatorsLeft();
        trace = VerificationTracesShared.AllocatorErrorOOB("ProcessingStopsAfterNoAllocatorsLeftError", config);
        merger = setupMerger("ProcessingStopsAfterNoAllocatorsLeftError", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    @Override
    public List<VerificationItem> mergeAll() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        items.addAll(SingleActionPerTick());
        items.addAll(ExpectedAllocationAction());
        items.addAll(ActionsProcessedBeforeNewAction());
        items.addAll(CheckBoundedInvalidAllocationActions());
        items.addAll(ExpectedActionCountInDone());
        items.addAll(ProcessingStopsAfterNoAllocatorsLeft());
        return items;
    }
}
