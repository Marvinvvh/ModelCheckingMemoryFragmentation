package org.TraceVerifier.Query;

/**
 * Query that's expected to crash when ran.
 */
public class FailOnCompilationQuery extends BaseTraceQuery {
    public FailOnCompilationQuery(String queryString, String queryComment, String queryIdentifier) {
        super(queryString, queryComment, queryIdentifier, "", "CompileFailure");
        expectedToCrash = true;
    }
}
