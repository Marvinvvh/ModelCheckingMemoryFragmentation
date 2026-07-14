package org.TraceVerifier.Query;


/**
 * Query that does not contain a condition, only a comment.
 */
public class CommentTraceQuery extends BaseTraceQuery {
    public CommentTraceQuery(String queryComment, String queryIdentifier) {
        super("", queryComment, queryIdentifier, "", "comment");
    }
}
