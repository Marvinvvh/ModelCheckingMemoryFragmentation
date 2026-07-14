package org.TraceVerifier.Verification.Queries;

import org.TraceVerifier.Query.PassFailTraceQuery;
import org.TraceVerifier.Query.TraceQueryCollection;

/**
 * Class that contains all shared queries.
 */
public class VerificationQueriesShared {
    public static TraceQueryCollection NoErrors()
    {
        String identifier = "NoErrors";
        String comment = """
                Checks whether there are no errors.
                """;

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        String query = "A[] !global_allocator_state.error && !global_heap_state.error && !trace.error";
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }
}
