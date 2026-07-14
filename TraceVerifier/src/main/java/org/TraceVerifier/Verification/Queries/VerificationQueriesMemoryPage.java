package org.TraceVerifier.Verification.Queries;

import org.TraceVerifier.Query.PassFailTraceQuery;
import org.TraceVerifier.Query.Properties.PropUtil;
import org.TraceVerifier.Query.TraceQueryCollection;

/**
 * Class that contains all Memory Page-related queries.
 */
public class VerificationQueriesMemoryPage {

    public enum PageState {
        ePageStateFree,
        ePageStateAlloc,
        ePageStatePartialAlloc,
    }

    public static TraceQueryCollection CheckHeapMemoryForPage(boolean allocate, int allocatorID, int pageID, int tick, int requested, int padded, int allocated)
    {
        String typeAction = allocate ? "allocate" : "free";
        String identifier = "CorrectHeapTrackerArrayAlloc";
        String heapMemory = PropUtil.deriveHeapMemory(allocatorID);
        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);
        String page = PropUtil.derivePage(allocatorID, pageID);
        String comment = "Checks whether, after processing a given page, the requested, padded, and allocated memory have an expected value. at time "+tick+". Note that we check this for a given page.";

        String query = "A[] (trace.timer == "+tick+" && "+heapInterface+".processing_page_req && "+heapInterface+".page_num == "+pageID+" && "+page+"."+typeAction+") imply ("+
                    heapMemory+".live_requested_memory == "+requested+
                    " && "+heapMemory+".live_allocated_memory == "+allocated+
                    " && "+heapMemory+".live_padded_memory == "+padded+")";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CheckFreeListEntry(boolean allocate, int allocatorID, int pageID, int tick, int entry, int address, int size)
    {
        String typeAction = allocate ? "allocate" : "free";
        String identifier = "CheckFreeListEntry";
        String freeListEntry = PropUtil.deriveFreeListEntry(allocatorID, pageID, entry);
        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);
        String page = PropUtil.derivePage(allocatorID, pageID);
        String comment = "Checks whether a free list entry has an express starting address and time at time "+tick+". Note that we check this for a given page.";

        String query = "A[] (trace.timer == "+tick+" && "+heapInterface+".processing_page_req && "+heapInterface+".page_num == "+pageID+" && "+page+"."+typeAction+") imply ("+
                        freeListEntry+".address == "+address+
                        " && "+freeListEntry+".length == "+size+")";
        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CheckFreeListSize(boolean allocate, int allocatorID, int pageID, int tick, int expectedFreeListSize)
    {
        String typeAction = allocate ? "allocate" : "free";
        String identifier = "CheckFreeListSize";
        String freeListSize = PropUtil.deriveFreeListSize(allocatorID, pageID);
        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);
        String page = PropUtil.derivePage(allocatorID, pageID);
        String comment = "Checks whether the free list size is the same size as expected at time "+tick+". Note that we check this for a given page.";

        String query = "A[] (trace.timer == "+tick+" && "+heapInterface+".processing_page_req && "+heapInterface+".page_num == "+pageID+" && "+page+"."+typeAction+") imply "+ freeListSize+" == "+expectedFreeListSize;
        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CheckPageState(boolean allocate, int allocatorID, int pageID, int tick, PageState pageState)
    {
        String typeAction = allocate ? "allocate" : "free";
        String identifier = "CheckPageState";
        String pageInfo = PropUtil.derivePageInfo(allocatorID, pageID);
        String heapInterface = PropUtil.deriveHeapInterface(allocatorID);
        String page = PropUtil.derivePage(allocatorID, pageID);
        String comment = "Checks whether the page is in the "+pageState+" state at time "+tick+". Note that we check this for a given page.\"";

        String query = "A[] (trace.timer == "+tick+" && "+heapInterface+".processing_page_req && "+heapInterface+".page_num == "+pageID+" && "+page+"."+typeAction+") imply "+pageInfo+".page_state == "+pageState.ordinal();
        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }
}
