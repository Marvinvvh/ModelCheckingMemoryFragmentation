package org.TraceVerifier.Query;

import com.google.gson.annotations.Expose;
import com.uppaal.model.core2.QueryData;
import com.uppaal.model.core2.QueryValue;

/**
 * Results retrieved from a UPPAAL simulation
 */
public class TraceQueryResult {
    @Expose
    private String message;
    @Expose
    private String queryInfo;
    @Expose
    private String timestamp;
    @Expose
    private QueryValue queryValue;
    @Expose
    private QueryData queryData;

    public TraceQueryResult(String message, String queryInfo, String timestamp, QueryValue queryValue, QueryData queryData) {
        this.message = message;
        this.queryInfo = queryInfo;
        this.timestamp = timestamp;
        this.queryValue = queryValue;
        this.queryData = queryData;
    }

    public String getMessage() {
        return message;
    }

    public String getQueryInfo() {
        return queryInfo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public QueryValue getQueryValue() {
        return queryValue;
    }

    public QueryData getQueryData() {
        return queryData;
    }
}
