package org.TraceVerifier.Verification.Queries;

import org.TraceVerifier.Query.PassFailTraceQuery;
import org.TraceVerifier.Query.Properties.PropUtil;
import org.TraceVerifier.Query.TraceQueryCollection;

/**
 * Class that contains all Trace-related queries.
 */
public class VerificationQueriesHeapInterface {

    public static TraceQueryCollection CorrectPageRequest(boolean allocate, int allocatorID, int pageID, int requested, int padded, int allocated, int tick)
    {
        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);
        String pageRequest = PropUtil.derivePageRequest(allocatorID);
        String actionType = allocate ? "eHeapRequestAlloc" : "eHeapRequestFree";
        String identifier = "CorrectMemoryAtTimeFree";
        String comment = "Checks whether the expected amoung of requested, padded, and allocated memory is in a page request at time "+tick+".";
        String query = "A[] (trace.timer == "+tick+" && "+heapInterface+".processing_page_req && "+heapInterface+".page_num == "+pageID+") imply ("+pageRequest+".type == "+actionType+
                " && "+pageRequest+".info.requested_memory == "+requested+
                " && "+pageRequest+".info.padded_memory == "+padded+
                " && "+pageRequest+".info.allocated_memory == "+allocated+")";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CorrectHeapMemoryAtTime(int allocatorID, int requested, int padded, int allocated, int tick)
    {
        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);
        String heapMemory = PropUtil.deriveHeapMemory(allocatorID);
        String identifier = "CorrectMemoryAtTimeFree";
        String comment = "Checks whether the expected amoung of requested, padded, and allocated memory is in a heap request at time "+tick+".";
        String query = "A[] (trace.timer == "+tick+" && "+heapInterface+".processed) imply ("+heapMemory+".live_requested_memory == "+requested+
                " && "+heapMemory+".live_padded_memory == "+padded+
                " && "+heapMemory+".live_allocated_memory == "+allocated+")";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CorrectPageRange(int allocatorID, int tick, int pageStart, int pageSpan)
    {
        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);
        String identifier = "CorrectMemoryAtTimeFree";
        String comment = "Checks whether there's no page handled other than the given span at time "+tick+".";
        String query = "A[] (trace.timer == "+tick+" && "+heapInterface+".processing_page_req) imply ("+heapInterface+".page_num >= "+pageStart+" && "+heapInterface+".page_num <"+(pageStart+pageSpan)+")";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection addressMatchTimed(int allocatorID, int pageID, int tick, int startAddress, int endAddress)
    {
        String identifier = "addressMatchTimed";
        String comment = "Checks whether page request holds the expected address range at time "+tick+".";

        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);
        String pageReqRange = PropUtil.derivePageRequestRange(allocatorID);
        String sameStartAddress = pageReqRange + ".start_address == " + startAddress;
        String sameEndAddress = pageReqRange + ".end_address == " + endAddress;

        String query =  "A[] (trace.timer == "+tick+" && "+heapInterface+".processing_page_req && "+heapInterface+".page_num == "+pageID+") imply ("+sameStartAddress+" && "+sameEndAddress+")";
        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection typeMatchTimed(int allocatorID, int pageID, int tick, boolean allocate)
    {

        String actionType = allocate ? "eHeapRequestAlloc" : "eHeapRequestFree";
        String identifier = "typeMatchTimed";
        String comment = "Checks the whether the page request is of type "+actionType+" at time "+tick+".";

        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);
        String pageReq = PropUtil.derivePageRequest(allocatorID);

        String query =  "A[] (trace.timer == "+tick+" && "+heapInterface+".processing_page_req && "+heapInterface+".page_num == "+pageID+") imply "+pageReq
                +".type == "+actionType;
        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CheckActionLeadsToProcessed(int allocatorID, int tick)
    {

        String identifier = "CheckActionLeadsToProcessed";
        String comment = """
                Whenever there's no error we always expect to land in processed afterwards for a valid allocation trace. 
                """;

        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);

        String query = "trace.timer == "+tick+" && trace.verify_action && !trace.fault_invalid_trace && "+heapInterface+".wait_for_action && !global_allocator_state.error && !global_heap_state.error -->" +
                " trace.timer == "+tick+" && trace.wait_for_action && "+heapInterface+".processed";
        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CheckSingleActionProcessedPerTick(int allocatorID)
    {

        String identifier = "CheckSingleActionProcessedPerTick";
        String comment = """
                The action count increases by exactly one each time tick of the trace. Therefore, once processed, the amount of requests served should equal that value.
                """;
        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);

        String query = "A[] "+heapInterface+".processed imply "+heapInterface+".requests_served == (action_count - 1)";
        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CheckStayInProcessedOrWaitForAction(int allocatorID, int tick)
    {

        String identifier = "CheckStayInProcessedOrWaitForAction";
        String comment = """
                If we're in the processed state, we should NEVER, in the same time tick, land in any state except where it waits for the next action.
                As such, we expect this query to fail.
                """;
        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);

        // Expected to fail.
        String query = "trace.timer == "+tick+" && "+heapInterface+".processed --> trace.timer=="+tick+" && !"+heapInterface+".wait_for_action && !"+heapInterface+".processed";
        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier, false));
        return tqc;
    }

    public static TraceQueryCollection CheckRelativeInternalFragmentation(int allocatorID, int tick, int value)
    {
        String identifier = "CheckRelativeInternalFragmentation";
        String comment = "Checks the relative internal fragmentation value at time"+tick+".";
        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);
        String heapID = PropUtil.deriveHeapID(allocatorID);

        // Expected to fail.
        String query = "A[] (trace.timer == "+tick+" && "+heapInterface+".processed) imply frag_int_relative["+heapID+"] == "+value;
        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CheckRelativeExternalFragmentation(int allocatorID, int tick, PropUtil.MemoryView memoryView, int value)
    {
        String identifier = "CheckRelativeExternalFragmentation";
        String comment = "Checks the relative external fragmentation value for the "+memoryView+" view at time"+tick+".";
        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);
        String heapID = PropUtil.deriveHeapID(allocatorID);

        // Expected to fail.
        String frag = "frag_ext_relative_"+PropUtil.MemoryView.getValue(memoryView);
        String query = "A[] (trace.timer == "+tick+" && "+heapInterface+".processed) imply "+frag+"["+heapID+"] == "+value;
        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CheckLFBFragmentation(int allocatorID, int tick, PropUtil.MemoryView memoryView, int value)
    {
        String identifier = "CheckLFBFragmentation";
        String comment = "Checks whether there's any LFB fragmentation for the "+memoryView+" view at time +"+tick+".";
        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);
        String heapID = PropUtil.deriveHeapID(allocatorID);

        // Expected to fail.
        String frag = "frag_LFB_"+PropUtil.MemoryView.getValue(memoryView);
        String query = "A[] (trace.timer == "+tick+" && "+heapInterface+".processed) imply "+frag+"["+heapID+"] == "+value;
        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CheckIntFragPresence(int allocatorID, int tick, boolean value)
    {
        String identifier = "CheckIntFragPresence";
        String comment = "Checks whether there's any internal fragmentation at time +"+tick+".";
        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);
        String heapID = PropUtil.deriveHeapID(allocatorID);

        // Expected to fail.
        String query = "A[] (trace.timer == "+tick+" && "+heapInterface+".processed) imply frag_presence_int["+heapID+"] == "+value;
        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CheckExtFragPresence(int allocatorID, int tick, PropUtil.MemoryView memoryView, boolean value)
    {
        String identifier = "CheckExtFragPresence";
        String comment = "Checks whether there's any external fragmentation for the "+memoryView+" view at time +"+tick+".";
        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);
        String heapID = PropUtil.deriveHeapID(allocatorID);

        // Expected to fail.
        String frag = "frag_presence_ext_"+PropUtil.MemoryView.getValue(memoryView);
        String query = "A[] (trace.timer == "+tick+" && "+heapInterface+".processed) imply "+frag+"["+heapID+"] == "+value;
        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CheckFragPresence(int allocatorID, int tick, PropUtil.MemoryView memoryView, boolean value)
    {
        String identifier = "CheckFragPresence";
        String comment = "Checks whether there's any fragmentation for the "+memoryView+" view at time +"+tick+".";
        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);
        String heapID = PropUtil.deriveHeapID(allocatorID);

        // Expected to fail.
        String frag = "frag_presence_"+PropUtil.MemoryView.getValue(memoryView);
        String query = "A[] (trace.timer == "+tick+" && "+heapInterface+".processed) imply "+frag+"["+heapID+"] == "+value;
        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CheckWJFragmentation(int allocatorID, int tick, PropUtil.WJVariant wjVariant, PropUtil.WJType wjType, int value)
    {
        String identifier = "CheckWJFragmentation";
        String comment = "Checks the WJ Variant ("+wjVariant+"), type("+wjType+") expected value. at time"+tick+".";
        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);
        String heapID = PropUtil.deriveHeapID(allocatorID);
        // Expected to fail.
        String frag = PropUtil.WJVariant.getValue(wjVariant)+"["+heapID+"]."+ PropUtil.WJType.getValue(wjType);
        String query = "A[] (trace.timer == "+tick+" && "+heapInterface+".processed) imply "+frag+" == "+value;
        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }



}
