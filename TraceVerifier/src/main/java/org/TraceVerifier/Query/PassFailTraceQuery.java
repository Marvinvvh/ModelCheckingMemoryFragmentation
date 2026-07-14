package org.TraceVerifier.Query;

/**
 * A check whether some does or does not hold.
 */
public class PassFailTraceQuery extends BaseTraceQuery {

    public PassFailTraceQuery(String queryString, String queryComment, String queryIdentifier) {
        super(queryString, queryComment, queryIdentifier, "", "boolean");
    }

    public PassFailTraceQuery(String queryString, String queryComment, String queryIdentifier, boolean expectedToPass) {
        super(queryString, queryComment, queryIdentifier, "", "boolean");
        this.setExpectedResult(expectedToPass);
    }
}
