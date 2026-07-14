package org.TraceVerifier.Verification.Mergers;

import org.TraceVerifier.Model.ModelConfiguration;
import org.TraceVerifier.Model.ModelMerger;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Verification.Queries.VerificationQueriesXFAO;
import org.TraceVerifier.Verification.Traces.VerificationTracesShared;
import org.TraceVerifier.Verification.Traces.VerificationTracesXFAO;
import org.TraceVerifier.Verification.VerificationItem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * Class that contains all XFAO-related verification items.
 * Refer to the thesis for referencing Requirement Req X.NUM to the properties we verify using these verification items.
 */
public class VerificationMergerXFAO extends VerificationMerger {
    public VerificationMergerXFAO(Path baseModelPath, ModelConfiguration config, boolean clearExistingQueries) {
        super(baseModelPath, config, clearExistingQueries);
    }

    /**
     * Check whether expected allocation actions occur.
     * Req. XFAO.1
     */
    public List<VerificationItem> ExpectedAllocationActions() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = VerificationQueriesXFAO.CorrectActionAlloc( config.getModelType(), allocatorID);
        String expectedAllocationActionAlloc = "ExpectedAllocationActionAlloc";
        trace = VerificationTracesShared.generateStandardizedTrace(expectedAllocationActionAlloc, config);
        merger = setupMerger(expectedAllocationActionAlloc, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        tq = VerificationQueriesXFAO.CorrectActionFree( config.getModelType(), allocatorID);
        String expectedAllocationActionFree = "ExpectedAllocationActionFree";
        trace = VerificationTracesShared.generateStandardizedTrace(expectedAllocationActionFree, config);
        merger = setupMerger(expectedAllocationActionFree, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether expected heap requests are created.
     * Req. XFAO.2
     */
    public List<VerificationItem> ExpectedHeapRequestCreated() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        String expectedHeapRequestCreated = "ExpectedHeapRequestCreated";
        TraceQueryCollection tq = new TraceQueryCollection(expectedHeapRequestCreated);

        // Test whether the expected requested/padded/allocated is passed to the heap requests.
        int offset = config.getSizeMultiple() / 2;

        int requested = 1;
        int padded = config.getSizeMultiple() - requested;
        tq.add(VerificationQueriesXFAO.CorrectMemoryAtTimeAlloc(config.getModelType(), allocatorID, requested, padded, requested + padded, 1));
        tq.add(VerificationQueriesXFAO.CorrectMemoryAtTimeFree( config.getModelType(), allocatorID, requested, padded, requested + padded, 2));

        padded = offset;
        requested = config.getPageSize() * 2 - offset;
        tq.add(VerificationQueriesXFAO.CorrectMemoryAtTimeAlloc( config.getModelType(), allocatorID, requested, padded, requested + padded, 3));

        requested = config.getPageSize() * 4 - offset;
        tq.add(VerificationQueriesXFAO.CorrectMemoryAtTimeAlloc(config.getModelType(), allocatorID, requested, padded, requested + padded, 4));

        requested = config.getPageSize() * 4 - offset;
        tq.add(VerificationQueriesXFAO.CorrectMemoryAtTimeAlloc( config.getModelType(), allocatorID, requested, padded, requested + padded, 5));

        requested = config.getPageSize() * 2 - offset;
        tq.add(VerificationQueriesXFAO.CorrectMemoryAtTimeAlloc( config.getModelType(), allocatorID, requested, padded, requested + padded, 6));

        requested = config.getPageSize() * 2 - offset;
        tq.add(VerificationQueriesXFAO.CorrectMemoryAtTimeFree( config.getModelType(), allocatorID, requested, padded, requested + padded, 7));

        requested = config.getPageSize() * 4 - offset;
        tq.add(VerificationQueriesXFAO.CorrectMemoryAtTimeFree( config.getModelType(), allocatorID, requested, padded, requested + padded, 8));

        requested = config.getPageSize() * 4 - offset;
        tq.add(VerificationQueriesXFAO.CorrectMemoryAtTimeFree( config.getModelType(), allocatorID, requested, padded, requested + padded, 9));

        requested = config.getPageSize() * 2 - offset;
        tq.add(VerificationQueriesXFAO.CorrectMemoryAtTimeFree(config.getModelType(), allocatorID, requested, padded, requested + padded, 10));


        // Check whether requested memory matches what's expected from alloc request.
        tq.add(VerificationQueriesXFAO.MatchingAllocAndHeapRequested( config.getModelType(), allocatorID));

        // Check whether the ordered that we associate with an allocation match
        tq.add(VerificationQueriesXFAO.MatchingHeapAndOrderedRequestAlloc( config.getModelType(), allocatorID));
        tq.add(VerificationQueriesXFAO.MatchingHeapAndOrderedRequestFree(config.getModelType(), allocatorID));

        trace = VerificationTracesShared.generateStandardizedTrace(expectedHeapRequestCreated, config);
        merger = setupMerger(expectedHeapRequestCreated, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether unsafe behavior causes errors.
     * Req. XFAO.3
     */
    public List<VerificationItem> UnsafeBehaviorCheck() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        // Standard no errors
        String unsafeBehaviorCheckNoErrors = "UnsafeBehaviorCheckNoErrors";
        tq = new TraceQueryCollection(unsafeBehaviorCheckNoErrors);
        tq.add(VerificationQueriesXFAO.NoAllocatorErrors(config.getModelType(), allocatorID));
        trace = VerificationTracesShared.generateStandardizedTrace(unsafeBehaviorCheckNoErrors, config);
        merger = setupMerger(unsafeBehaviorCheckNoErrors, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        // Duplicate pointers
        String unsafeBehaviorCheckDuplicatePointer = "UnsafeBehaviorCheckDuplicatePointer";
        tq = new TraceQueryCollection(unsafeBehaviorCheckDuplicatePointer);
        tq.add(VerificationQueriesXFAO.ExactDuplicatePointer(config.getModelType(), allocatorID, 2));
        tq.add(VerificationQueriesXFAO.DuplicatePointerCondition( config.getModelType(), allocatorID));
        trace = VerificationTracesXFAO.generateDuplicatePointer(unsafeBehaviorCheckDuplicatePointer);
        merger = setupMerger(unsafeBehaviorCheckDuplicatePointer, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        // Unalloced free
        String unsafeBehaviorCheckUnallocedFree = "UnsafeBehaviorCheckUnallocedFree";
        tq = new TraceQueryCollection(unsafeBehaviorCheckUnallocedFree);
        tq.add(VerificationQueriesXFAO.ExactUnallocatedFree(config.getModelType(), allocatorID, 3));
        tq.add(VerificationQueriesXFAO.UnallocatedFreeCondition(config.getModelType(), allocatorID));
        trace = VerificationTracesXFAO.generateUnallocatedFree(unsafeBehaviorCheckUnallocedFree);
        merger = setupMerger(unsafeBehaviorCheckUnallocedFree, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        // Double free
        String unsafeBehaviorCheckDoubleFree = "UnsafeBehaviorCheckDoubleFree";
        tq = new TraceQueryCollection(unsafeBehaviorCheckDoubleFree);
        tq.add(VerificationQueriesXFAO.DoubleFreeCondition(config.getModelType(), allocatorID));
        tq.add(VerificationQueriesXFAO.ExactDoubleFree(config.getModelType(), allocatorID, 3));
        trace = VerificationTracesXFAO.generateDoubleFree(unsafeBehaviorCheckDoubleFree);
        merger = setupMerger(unsafeBehaviorCheckDoubleFree, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether memory exhaustion by fragmentation causes errors and whether it triggers the fragmentation variables.
     * Req. XFAO.4
     */
    public List<VerificationItem> MemoryExhaustionByFragmentation() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        String memoryExhaustionByFragmentation = "MemoryExhaustionByFragmentation";
        tq = new TraceQueryCollection(memoryExhaustionByFragmentation);
        tq.add(VerificationQueriesXFAO.MemoryExhaustionByFragmentationCondition(config.getModelType(), allocatorID));
        tq.add(VerificationQueriesXFAO.ExactMemoryExhaustionByFragmentation(config.getModelType(), allocatorID, 4));
        trace = VerificationTracesXFAO.generateMemoryExhaustionByFragmentation(memoryExhaustionByFragmentation, config);
        merger = setupMerger(memoryExhaustionByFragmentation, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether only a single allocator is processing.
     * Req. XFAO.5
     * This specifically proves that we do not activate more than one model at a time.
     *   We use this to iterate over all allocators in allocator_controller, and with the offsets between allocators this means that we at max only process on allocator at a time.
     */
    public List<VerificationItem> SingleAllocatorProcessing() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        String singleAllocatorProcessing = "SingleAllocatorProcessing";
        tq = new TraceQueryCollection(singleAllocatorProcessing);
        tq.add(VerificationQueriesXFAO.SingleAllocatorProcessing());
        trace = VerificationTracesShared.generateStandardizedTrace(singleAllocatorProcessing, config);
        merger = setupMerger(singleAllocatorProcessing, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether the controller stops after an error.
     * Req. XFAO.6
     */
    public List<VerificationItem> ControllerStopsAfterError() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        String controllerStopsAfterError = "ControllerStopsAfterError";
        tq = new TraceQueryCollection(controllerStopsAfterError);
        tq.add(VerificationQueriesXFAO.AllocatorInError(config.getModelType(), allocatorID, 3));
        tq.add(VerificationQueriesXFAO.ControllerStopsAfterError(config.getModelType(), allocatorID, 3));
        trace = VerificationTracesXFAO.generateUnallocatedFree(controllerStopsAfterError);
        merger = setupMerger(controllerStopsAfterError, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether the expected size multiple is allocated. This is also repeatedly tested in other queries.
     * Req. XFAO.7
     */
    public List<VerificationItem> ExpectedSizeMultipleAllocated() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        String expectedSizeMultipleAllocated = "ExpectedSizeMultipleAllocated";
        tq = new TraceQueryCollection(expectedSizeMultipleAllocated);
        tq.add(VerificationQueriesXFAO.ExpectedSizeMultipleAllocated(allocatorID));
        trace = VerificationTracesShared.generateStandardizedTrace(expectedSizeMultipleAllocated, config);
        merger = setupMerger(expectedSizeMultipleAllocated, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check for invalid sizes and out-of-bounds values. The invalid size should trigger a compilation error due to bounds.
     * Req. XFAO.8
     */
    public List<VerificationItem> InvalidAndOOBSizes() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        String invalidSizes = "InvalidSizes";
        tq = new TraceQueryCollection(invalidSizes);
        tq.add(VerificationQueriesXFAO.NegativeSizedRequest(config.getModelType(), allocatorID, 1));
        trace = VerificationTracesXFAO.generateInvalidSized(invalidSizes);
        merger = setupMerger(invalidSizes, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        String oobSizes = "OOBSizes";
        tq = new TraceQueryCollection(oobSizes);
        tq.add(VerificationQueriesXFAO.OOBRequest(config.getModelType(), allocatorID, 1));
        trace = VerificationTracesXFAO.generateOOBSize(oobSizes, config);
        merger = setupMerger(oobSizes, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }


    /**
     * Check whether the allocator observer error state is reached when expected.
     * Req. XFAO.9
     */
    public List<VerificationItem> AllocatorObserverAutomataErrorInduction() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        // Standard no errors
        String observerErrorStandardNoErrors = "ObserverErrorStandardNoErrors";
        tq = new TraceQueryCollection(observerErrorStandardNoErrors);
        tq.add(VerificationQueriesXFAO.NoAllocatorErrors(config.getModelType(), allocatorID));
        trace = VerificationTracesShared.generateStandardizedTrace(observerErrorStandardNoErrors, config);
        tq.add(VerificationQueriesXFAO.ObserversNotInUnerroredState(config.getModelType(), allocatorID));
        merger = setupMerger(observerErrorStandardNoErrors, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        // Duplicate pointers
        String observerErrorDuplicatePointer = "ObserverErrorDuplicatePointer";
        tq = new TraceQueryCollection(observerErrorDuplicatePointer);
        tq.add(VerificationQueriesXFAO.ExactDuplicatePointer(config.getModelType(), allocatorID, 2));
        tq.add(VerificationQueriesXFAO.ObserversNotInUnerroredState(config.getModelType(), allocatorID));
        tq.add(VerificationQueriesXFAO.ObserverErrorAtTime(config.getModelType(), allocatorID, 2));
        trace = VerificationTracesXFAO.generateDuplicatePointer(observerErrorDuplicatePointer);
        merger = setupMerger(observerErrorDuplicatePointer, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        // Unalloced free
        String observerErrorUnallocedFree = "ObserverErrorUnallocedFree";
        tq = new TraceQueryCollection(observerErrorUnallocedFree);
        tq.add(VerificationQueriesXFAO.ExactUnallocatedFree(config.getModelType(), allocatorID, 3));
        tq.add(VerificationQueriesXFAO.ObserversNotInUnerroredState(config.getModelType(), allocatorID));
        tq.add(VerificationQueriesXFAO.ObserverErrorAtTime(config.getModelType(), allocatorID, 3));
        trace = VerificationTracesXFAO.generateUnallocatedFree(observerErrorUnallocedFree);
        merger = setupMerger(observerErrorUnallocedFree, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        // Double free
        String observerErrorDoubleFree = "ObserverErrorDoubleFree";
        tq = new TraceQueryCollection(observerErrorDoubleFree);
        tq.add(VerificationQueriesXFAO.ExactDoubleFree(config.getModelType(), allocatorID, 3));
        tq.add(VerificationQueriesXFAO.ObserversNotInUnerroredState(config.getModelType(), allocatorID));
        tq.add(VerificationQueriesXFAO.ObserverErrorAtTime(config.getModelType(), allocatorID, 3));
        trace = VerificationTracesXFAO.generateDoubleFree(observerErrorDoubleFree);
        merger = setupMerger(observerErrorDoubleFree, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        // Memory exhaustion (by fragmentation)
        String observerErrorMemoryExhaustionByFragmentation = "ObserverErrorMemoryExhaustionByFragmentation";
        tq = new TraceQueryCollection(observerErrorMemoryExhaustionByFragmentation);
        tq.add(VerificationQueriesXFAO.ExactMemoryExhaustionByFragmentation(config.getModelType(), allocatorID, 4));
        tq.add(VerificationQueriesXFAO.ObserversNotInUnerroredState(config.getModelType(), allocatorID));
        tq.add(VerificationQueriesXFAO.ObserverErrorAtTime(config.getModelType(), allocatorID, 0));
        trace = VerificationTracesXFAO.generateMemoryExhaustionByFragmentation(observerErrorMemoryExhaustionByFragmentation, config);
        merger = setupMerger(observerErrorMemoryExhaustionByFragmentation, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether only a single allocator action is processed per time tick.
     * Req. XFAO.10
     */
    public List<VerificationItem> SingleAllocatorActionPerTick() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        // Standard no errors
        String singleAllocatorActionPerTick = "SingleAllocatorActionPerTick";
        tq = new TraceQueryCollection(singleAllocatorActionPerTick);
        trace = VerificationTracesShared.generateStandardizedTrace(singleAllocatorActionPerTick, config);

        // Timer == #action + 1
        for(int i = 0; i < trace.getTraceActions().size(); i++)
        {
            tq.add(VerificationQueriesXFAO.NeverReachProcessAfterActionAtTime( config.getModelType(), allocatorID, i + 1));
        }

        merger = setupMerger(singleAllocatorActionPerTick, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether the tracker entry is set correctly on allocation.
     * Req. XFAO.11
     */
    public List<VerificationItem> HeapTrackerAlloc() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        String heapTrackerAlloc = "HeapTrackerAlloc";
        tq = new TraceQueryCollection(heapTrackerAlloc);
        tq.add(VerificationQueriesXFAO.CorrectHeapTrackerArrayAlloc(config.getModelType(), allocatorID));
        trace = VerificationTracesShared.generateStandardizedTrace(heapTrackerAlloc, config);
        merger = setupMerger(heapTrackerAlloc, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether the tracker entry is set correctly on free.
     * Req. XFAO.12
     */
    public List<VerificationItem> HeapTrackerFree() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        String heapTrackerFree = "HeapTrackerFree";
        tq = new TraceQueryCollection(heapTrackerFree);
        tq.add(VerificationQueriesXFAO.CorrectHeapTrackerArrayFree( config.getModelType(), allocatorID));
        trace = VerificationTracesShared.generateStandardizedTrace(heapTrackerFree, config);
        merger = setupMerger(heapTrackerFree, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }


    @Override
    public List<VerificationItem> mergeAll() throws IOException {
        List<VerificationItem> list = new ArrayList<>();
        list.addAll(ExpectedAllocationActions());
        list.addAll(ExpectedHeapRequestCreated());
        list.addAll(UnsafeBehaviorCheck());
        list.addAll(MemoryExhaustionByFragmentation());
        list.addAll(SingleAllocatorProcessing());
        list.addAll(ControllerStopsAfterError());
        list.addAll(ExpectedSizeMultipleAllocated());
        list.addAll(InvalidAndOOBSizes());
        list.addAll(AllocatorObserverAutomataErrorInduction());
        list.addAll(SingleAllocatorActionPerTick());
        list.addAll(HeapTrackerAlloc());
        list.addAll(HeapTrackerFree());

        return list;
    }

}
