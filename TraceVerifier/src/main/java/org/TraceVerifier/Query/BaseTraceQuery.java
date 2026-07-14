package org.TraceVerifier.Query;

import com.google.gson.annotations.Expose;
import com.uppaal.model.core2.Query;
import com.uppaal.model.core2.QueryResult;

/**
 * A query that's simulatable in UPPAAL.
 */
public abstract class BaseTraceQuery implements TraceQuery, TraceStringify {
    @Expose
    protected String identifier = "";

    @Expose
    protected TraceQueryResult traceQueryResult;

    @Expose
    protected TraceProperty traceProperty;

    @Expose
    protected String unit;

    @Expose
    protected String type = "base";

    protected boolean expectedToPass = true;
    protected boolean expectedToCrash = false;

    public BaseTraceQuery() {
        traceProperty = new TraceProperty("", "default");
    }

    public BaseTraceQuery(String queryString, String queryComment, String queryIdentifier, String unit, String type) {
        this.traceProperty = new TraceProperty(queryString, queryComment);
        this.identifier = queryIdentifier;
        this.unit = unit;
        this.type = type;
    }

    public BaseTraceQuery(TraceProperty queryData) {
        this.traceProperty = queryData;
    }

    @Override
    public String toTraceString() {
        return "";
    }

    @Override
    public boolean hasResult() {
        return traceQueryResult != null;
    }

    @Override
    public boolean hasExpectedResult() {
        if (traceQueryResult != null) {
            boolean queryPassed = traceQueryResult.getQueryValue().getStatus().toString().equals("Success");
            return queryPassed == expectedToPass;
        }

        return false;
    }

    @Override
    public void setExpectedResult(boolean expectedToPass) {
        this.expectedToPass = expectedToPass;
    }

    @Override
    public boolean isExpectedToPass() {
        return expectedToPass;
    }

    @Override
    public boolean isExpectedToCrash() {
        return expectedToCrash;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String getUnit() {
        return unit;
    }

    @Override
    public TraceProperty getProperty() {
        return traceProperty;
    }

    @Override
    public TraceQueryResult getResult() {
        return traceQueryResult;
    }

    @Override
    public Query generateUPPAALQuery() {
        return new Query(traceProperty.queryString(), traceProperty.queryComment());
    }

    /**
     * Converts UPPAAL query into one we have more control over.
     */
    @Override
    public void parseUPPAALQueryResult(QueryResult queryResult) {
        this.traceQueryResult = new TraceQueryResult(
                queryResult.getMessage(),
                queryResult.getQueryInfo(),
                queryResult.getTimestamp(),
                queryResult.getValue(),
                queryResult.getData()
        );
    }


}
