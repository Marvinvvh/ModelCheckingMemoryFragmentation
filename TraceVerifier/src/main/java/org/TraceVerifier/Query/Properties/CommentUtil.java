package org.TraceVerifier.Query.Properties;

import org.TraceVerifier.Query.CommentTraceQuery;

public class CommentUtil {
    public static CommentTraceQuery createHeader(int level, String headerName, String identifier) {
        StringBuilder s = new StringBuilder();
        if (level == 0) {
            s = new StringBuilder("===");
        } else if (level == 1) {
            s = new StringBuilder("---");
        } else {
            s.append(".".repeat(Math.max(3, level)));
        }

        return new CommentTraceQuery(s + " " + headerName + " " + s, identifier);
    }

}
