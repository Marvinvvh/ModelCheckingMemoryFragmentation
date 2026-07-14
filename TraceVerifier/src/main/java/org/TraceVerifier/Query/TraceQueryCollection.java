package org.TraceVerifier.Query;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of queries to test for a trace.
 */
public class TraceQueryCollection implements Passable, TraceStringify {
    @Expose
    private final List<TraceQuery> queries;
    @Expose
    private final List<TraceQueryCollection> collections;
    @Expose
    private String queryIdentifier = "";

    public TraceQueryCollection(String queryIdentifier) {
        queries = new ArrayList<>();
        collections = new ArrayList<>();
        this.queryIdentifier = queryIdentifier;
    }

    public void add(TraceQuery query) {
        queries.add(query);
    }

    public String getIdentifier() {
        return queryIdentifier;
    }

    public void add(TraceQueryCollection collection) {
        collections.add(collection);
    }

    public List<TraceQuery> getStandaloneQueries() {
        return queries;
    }

    public List<TraceQueryCollection> getQueryCollections() {
        return collections;
    }

    public TraceQueryCollection getStandaloneQueryCollection()
    {
        TraceQueryCollection qc = new TraceQueryCollection(getIdentifier());
        for(var q : getStandaloneQueries())
        {
            qc.add(q);
        }
        return qc;
    }

    public List<TraceQueryCollection> getAllQueryCollections() {
        List<TraceQueryCollection> qc = new ArrayList<>(collections);
        qc.add(getStandaloneQueryCollection());
        return qc;
    }

    public List<TraceQuery> getAllQueries() {
        List<TraceQuery> traceQueries = new ArrayList<>(getStandaloneQueries());
        for (TraceQueryCollection collection : collections) {
            traceQueries.addAll(collection.getAllQueries());
        }

        return traceQueries;
    }

    public void removeComments() {
        queries.removeIf(query -> query.getProperty().queryString().isEmpty());
        for (var qc : collections) {
            qc.removeComments();
        }
    }

    @Override
    public boolean hasExpectedResult() {
        for (var q : queries) {
            if (!q.hasExpectedResult()) {
                return false;
            }
        }

        for (var qc : getQueryCollections()) {
            if (!qc.hasExpectedResult()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void setExpectedResult(boolean expectedToPass) {
        // A trace query collection always expects all its contained queries to have their expected results
    }

    @Override
    public boolean isExpectedToPass() {
        return true;
    }

    @Override
    public boolean isExpectedToCrash() {
        return false;
    }

    @Override
    public boolean hasResult() {
        for (var q : queries) {
            if (!q.hasResult()) {
                return false;
            }
        }

        for (var qc : getQueryCollections()) {
            if (!qc.hasResult()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toTraceString() {
        return "";
    }
}
