package org.TraceVerifier.Verification.Queries;

import org.TraceVerifier.Model.ModelType;
import org.TraceVerifier.Query.PassFailTraceQuery;
import org.TraceVerifier.Query.Properties.PropUtil;
import org.TraceVerifier.Query.TraceQueryCollection;

/**
 * Class that contains all NFAO-related queries.
 */
public class VerificationQueriesNFAO {

    public static TraceQueryCollection CheckStopsInFirstPhase(ModelType modelType, int allocatorID, int time)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "CheckStopsInFirstPhase";
        String comment = "Check whether the search stops in the first phase at time "+time+". If in allocate, its done processing.";
        String query = "A[] (trace.timer == "+time+" && "+allocator+".allocate) imply ("+allocator+".phase_1_started && !"+allocator+".phase_2_started)";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CheckStopsInSecondPhase(ModelType modelType, int allocatorID, int time)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "CheckStopsInSecondPhase";
        String comment = "Check whether the search stops in the second phase at time "+time+". If in allocate, its done processing.";
        String query = "A[] (trace.timer == "+time+" && "+allocator+".allocate) imply ("+allocator+".phase_1_searched && "+allocator+".phase_2_started)";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CheckErrorFirstPhase(ModelType modelType, int allocatorID, int time)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "CheckErrorFirstPhase";
        String comment = "Check whether the first search is done (no second phase), i.e., searched, without match at time "+time+". NOTE: expects that we start on the starting address of the first free block.";
        String query = "A[] (trace.timer == "+time+" && "+allocator+".error) imply ("+allocator+".fault_memory_exhaustion && "+allocator+".phase_1_started && !"+allocator+".phase_2_started)";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection CheckErrorSecondPhase(ModelType modelType, int allocatorID, int time)
    {
        String allocator = PropUtil.deriveAllocator(modelType, allocatorID);
        String identifier = "CheckErrorSecondPhase";
        String comment = "Check whether the both searches are done, i.e., searched, without match at time "+time+".";
        String query = "A[] (trace.timer == "+time+" && "+allocator+".error) imply ("+allocator+".fault_memory_exhaustion && "+allocator+".phase_1_searched && "+allocator+".phase_2_searched)";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }
}
