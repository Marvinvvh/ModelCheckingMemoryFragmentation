package org.TraceVerifier.Verification.Queries;

import org.TraceVerifier.Model.ModelType;
import org.TraceVerifier.Query.FailOnCompilationQuery;
import org.TraceVerifier.Query.PassFailTraceQuery;
import org.TraceVerifier.Query.Properties.PropUtil;
import org.TraceVerifier.Query.TraceQueryCollection;

/**
 * Class that contains all XFAO-related queries.
 */
public class VerificationQueriesXFAO {
    public static TraceQueryCollection CorrectActionFree(ModelType modelType, int allocatorID)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "CorrectActionFree";
        String comment = """
                Whenever the allocator is freeing, it must have received a free action.
                """;
        String query = "A[] "+allocator+".free imply allocator_action_req["+allocatorID+"].type == eAllocatorActionFree";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CorrectActionAlloc(ModelType modelType, int allocatorID)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "CorrectActionAlloc";
        String comment = """
                Whenever the allocator is processing an allocator, it must have received an allocation action.
                """;
        String query = "A[] ("+allocator+".allocate || "+allocator+".processing_alloc || "+allocator+".retrieved_page_info) imply allocator_action_req["+allocatorID+"].type == eAllocatorActionAlloc";
        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection MatchingAllocAndHeapRequested(ModelType modelType, int allocatorID)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "MatchingAllocAndHeapRequested";
        String comment = """
                Whenever allocating, we expected the requested memory requested from the heap to match the requested value received from the trace. 
                """;

        String query = "A[] "+allocator+".allocate imply "+PropUtil.deriveHeapRequest(0)+".info.requested_memory == "+PropUtil.deriveAllocatorActionReq(allocatorID)+".allocation.size";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection MatchingHeapAndOrderedRequestAlloc(ModelType modelType, int allocatorID)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "MatchingHeapAndOrderedRequestAlloc";
        String comment = """
                Whenever allocating, 
                """;

        String query = "A[] "+allocator+".allocate imply "+PropUtil.deriveHeapRequest(0)+" == "+allocator+".ordered_heap_requests["+PropUtil.deriveAllocatorActionReq(allocatorID)+".allocation.pointer].req";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection MatchingHeapAndOrderedRequestFree(ModelType modelType, int allocatorID)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "MatchingHeapAndOrderedRequestFree";
        String comment = """

                """;

        String heapRequest = PropUtil.deriveHeapRequest(0);
        String query = "A[] "+allocator+".free imply "+heapRequest+".type == eHeapRequestFree"
                        +" && "+heapRequest+".info == "+allocator+".ordered_heap_requests["+PropUtil.deriveAllocatorActionReq(allocatorID)+".free.pointer].req.info"
                        +" && "+heapRequest+".range == "+allocator+".ordered_heap_requests["+PropUtil.deriveAllocatorActionReq(allocatorID)+".free.pointer].req.range" ;

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }


    public static TraceQueryCollection CorrectMemoryAtTimeAlloc(ModelType modelType, int allocatorID, int requested, int padded, int allocated, int time)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String heapRequest = PropUtil.deriveHeapRequest(modelType, allocatorID);
        String identifier = "CorrectMemoryAtTimeAlloc";
        String comment = """

                """;
        String query = "A[] (trace.timer == "+time+" && "+allocator+".allocate) imply ("+heapRequest+".type == eHeapRequestAlloc" +
                " && "+heapRequest+".info.requested_memory == "+requested+
                " && "+heapRequest+".info.padded_memory == "+padded+
                " && "+heapRequest+".info.allocated_memory == "+allocated+")";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CorrectMemoryAtTimeFree(ModelType modelType, int allocatorID, int requested, int padded, int allocated, int time)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String heapRequest = PropUtil.deriveHeapRequest(modelType, allocatorID);
        String identifier = "CorrectMemoryAtTimeFree";
        String comment = """

                """;
        String query = "A[] (trace.timer == "+time+" && "+allocator+".free) imply ("+heapRequest+".type == eHeapRequestFree" +
                                                                                    " && "+heapRequest+".info.requested_memory == "+requested+
                                                                                    " && "+heapRequest+".info.padded_memory == "+padded+
                                                                                    " && "+heapRequest+".info.allocated_memory == "+allocated+")";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection NoAllocatorErrors(ModelType modelType, int allocatorID)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "NoAllocatorErrors";
        String comment = """

                """;
        String query = "A[] not "+allocator+".alloc_error && not "+allocator+".free_error";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }


    public static TraceQueryCollection ExactDoubleFree(ModelType modelType, int allocatorID, int time)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "ExactDoubleFree";
        String comment = "Check whether a double free happens at time"+time+".";
        String query = "E<> trace.timer == "+time+" && "+allocator+".error && "+allocator+".fault_double_free";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection DoubleFreeCondition(ModelType modelType, int allocatorID)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "DoubleFreeCondition";
        String comment = """
                Check whether a double free happens when an allocation has already been freed.
                """;
        String allocatorReq = PropUtil.deriveAllocatorActionReq(allocatorID);
        String query = "A[] "+allocator+".fault_double_free imply ("+allocatorReq+".type == eAllocatorActionFree && "+allocator+".ordered_heap_requests[+"+allocatorReq+".free.pointer].freed)";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection ExactUnallocatedFree(ModelType modelType, int allocatorID, int time)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "ExactUnallocatedFree";
        String comment = "Check whether an unallocated free happens at time "+time+".";
        String query = "E<> trace.timer == "+time+" && "+allocator+".error && "+allocator+".fault_unalloc_free";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection UnallocatedFreeCondition(ModelType modelType, int allocatorID)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "UnallocatedFreeCondition";
        String comment = """
                Check whether an unallocated free occurs when an entry has not yet been allocated.
                """;
        String allocatorReq = PropUtil.deriveAllocatorActionReq(allocatorID);
        String query = "A[] "+allocator+".fault_unalloc_free imply ("+allocatorReq+".type == eAllocatorActionFree && not "+allocator+".ordered_heap_requests[+"+allocatorReq+".free.pointer].alloced)";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection ExactDuplicatePointer(ModelType modelType, int allocatorID, int time)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "ExactDuplicatePointer";
        String comment = "Check whether a duplicate pointer error happens at time "+time+".";
        String query = "E<> trace.timer == "+time+" && "+allocator+".fault_duplicate_pointers && " + allocator +".error";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection DuplicatePointerCondition(ModelType modelType, int allocatorID)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "DuplicatePointerCondition";
        String comment = """
                Checker whether a duplicate pointer error occurs when an entry has already been allocated.
                """;
        String allocatorReq = PropUtil.deriveAllocatorActionReq(allocatorID);
        String query = "A[] "+allocator+".fault_duplicate_pointers imply "+allocatorReq+".type == eAllocatorActionAlloc && "+allocator+".ordered_heap_requests[+"+allocatorReq+".free.pointer].alloced";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection ExactMemoryExhaustion(ModelType modelType, int allocatorID, int time)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "ExactMemoryExhaustion";
        String comment = "Check whether memory exhaustion occurs at time "+time+".";
        String query = "trace.timer == "+time+" && "+allocator+".action_signaled --> trace.timer == "+time+" && "+allocator+".fault_memory_exhaustion && " + allocator +".error";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection ExactMemoryExhaustionByFragmentation(ModelType modelType, int allocatorID, int time)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "ExactMemoryExhaustionByFragmentation";
        String comment = "Check whether memory exhaustion due to fragmentation occurs at time "+time+".";
        String query = "E<> trace.timer == "+time+" && "+allocator+".fault_memory_exhaustion && "+allocator+".fault_memory_fragmentation && " + allocator +".error";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection MemoryExhaustionByFragmentationCondition(ModelType modelType, int allocatorID)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "MemoryExhaustionByFragmentationCondition";
        String comment = """

                """;
        String allocatorReq = PropUtil.deriveAllocatorActionReq(allocatorID);
        String heapID = PropUtil.deriveHeapID(0);
        String query = "A[] ("+allocator+".fault_memory_fragmentation && "+allocator+".fault_memory_exhaustion) imply (" +
                "heap_pages["+heapID+"].largest_free_block_HV.size < "+allocatorReq+".allocation.size"
                + "&& heap_free_memory_HV["+heapID+"] == "+allocatorReq+".allocation.size)"

                ;

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection SingleAllocatorProcessing()
    {
        String identifier = "SingleAllocatorProcessing";
        String comment = """
                    Checker whether there's only a single allocator present. This immediately implies there's only a single allocator processing due to the way templating works.
                """;
        String query = "A[] ALLOCATOR_COUNT == 1";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection AllocatorInError(ModelType modelType, int allocatorID, int time)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "AllocatorInError";
        String comment = "Check whether an allocator is ever in error at time "+time+".";
        String query = "E<> " + allocator + ".error && trace.timer == "+time;

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection ControllerStopsAfterError(ModelType modelType, int allocatorID, int time)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "ControllerStopsAfterError";
        String comment = "Checker whether the allocator controller halts when encountering an error at time"+time+".";
        String query = allocator + ".error && trace.timer == "+time+" --> trace.timer == "+time+" && (allocator_controller.has_error_signal || allocator_controller.error_detected || allocator_controller.no_available_allocators)";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection ExpectedSizeMultipleAllocated(int allocatorID)
    {
        String identifier = "ExpectedSizeMultipleAllocated";
        String comment = """
                Check whether the allocation is entirely divisible by the size multiple. If so, it matches the size multiple. 
                """;
        String heapReq = PropUtil.deriveHeapRequest(allocatorID);
        String query = "A[] " + heapReq + ".info.allocated_memory % SIZE_MULTIPLE == 0";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection NegativeSizedRequest(ModelType modelType, int allocatorID, int time)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "NegativeSizedRequest";
        String comment = "Check whether an allocator has a negative sized request at time "+time+". This situation should cause a fail on compilation error.";
        String allocReq = PropUtil.deriveAllocatorActionReq(allocatorID);
        // query doesn't matter here. It fails as we have bounded the requested sizes to >= integers.
        String query = allocReq+ ".allocation.size < 0 && trace.timer == "+time+" --> trace.timer == "+time+" && "+allocator+".error && "+allocator+".fault_out_of_bounds && "+allocator+".fault_memory_exhaustion";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new FailOnCompilationQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection OOBRequest(ModelType modelType, int allocatorID, int time)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "OOBRequest";
        String comment = "Check whether an allocator has an OOB sized request at time "+time+".";
        String allocReq = PropUtil.deriveAllocatorActionReq(allocatorID);
        String query = allocReq+ ".allocation.size > HEAP_SIZE && trace.timer == "+time+" --> trace.timer == "+time+" && "+allocator+".error && "+allocator+".fault_out_of_bounds && "+allocator+".fault_memory_exhaustion";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection ObserversNotInUnerroredState(ModelType modelType, int allocatorID)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "ObserversNotInUnerroredState";
        String comment = """
                Check whenever an allocator observer is never in error as long as the allocator is unerrored.
                """;
        String query = "A[] !"+allocator+".error imply !allocator_state("+allocatorID+").error && !global_allocator_state.error";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection ObserverErrorAtTime(ModelType modelType, int allocatorID, int time)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "ObserverErrorAtTime";
        String comment = "Check whether an allocator has an observer error at time "+time+".";
        String query = allocator+".error && trace.timer == "+time+" --> trace.timer == "+time+" && allocator_state("+allocatorID+").error && global_allocator_state.error";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection NeverReachProcessAfterActionAtTime(ModelType modelType, int allocatorID, int time)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "NeverReachProcessAfterActionAtTime";
        String comment = "Check whether an allocator never processes an action at time "+time+".";
        // We can only trigger the heap using process_heap_req, which happens after an action is processed. So if we check this for each time unit, we're guaranteed we never reach that state.
        // trace.timer == 1 && (ffao_allocator(0).free || ffao_allocator(0).allocate) --> trace.timer == 1 && ffao_allocator(0).action_signaled
        String query = "trace.timer == "+time+" && ("+allocator+".free || "+allocator+".allocate) --> trace.timer == "+time+" && "+allocator+".action_signaled";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier, false));
        return tqc;
    }

    public static TraceQueryCollection CorrectHeapTrackerArrayAlloc(ModelType modelType, int allocatorID)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "CorrectHeapTrackerArrayAlloc";
        String comment = """
                Check whether the heap tracker has the expected values for the requests it stores on allocation.
                """;
        String allocReq = PropUtil.deriveAllocatorActionReq(allocatorID);
        String heapReq = PropUtil.deriveHeapRequest(allocatorID);
        String query = "A[] "+allocator+".allocate imply " +
                            " "+allocator+".ordered_heap_requests["+allocReq+".allocation.pointer].req == "+heapReq+
                            " && "+allocator+".ordered_heap_requests["+allocReq+".allocation.pointer].alloced"+
                            " && "+allocator+".ordered_heap_requests["+allocReq+".allocation.pointer].order ==  "+allocReq+".order";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }


    public static TraceQueryCollection CorrectHeapTrackerArrayFree(ModelType modelType, int allocatorID)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "CorrectHeapTrackerArrayFree";
        String comment = """
                Check whether the heap tracker has the expected values for the requests it stores on free.
                """;
        String allocReq = PropUtil.deriveAllocatorActionReq(allocatorID);
        String heapReq = PropUtil.deriveHeapRequest(allocatorID);
        String query = "A[] "+allocator+".free imply " +
                " "+allocator+".ordered_heap_requests["+allocReq+".free.pointer].req.info == "+heapReq+".info"+
                " && "+allocator+".ordered_heap_requests["+allocReq+".free.pointer].req.range == "+heapReq+".range"+
                " && "+allocator+".ordered_heap_requests["+allocReq+".free.pointer].freed";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection addressMatchTimed(ModelType modelType, int allocatorID, int time, int startAddress, int endAddress)
    {

        String identifier = "addressMatchTimed";
        String comment = """
                Check whether the heap request range has the expected addresses.
                """;

        String heapReqRange = PropUtil.deriveHeapRequestRange(allocatorID);
        String sameStartAddress = heapReqRange + ".start_address == " + startAddress;
        String sameEndAddress = heapReqRange + ".end_address == " + endAddress;
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);

        String query = "A[] (trace.timer == "+time+" && ("+allocator+".allocate || "+allocator+".free)) imply ("+sameStartAddress+" && "+sameEndAddress+")";
        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }
}
