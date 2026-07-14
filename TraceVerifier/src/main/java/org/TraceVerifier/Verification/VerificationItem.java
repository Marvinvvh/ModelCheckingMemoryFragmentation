package org.TraceVerifier.Verification;

import org.TraceVerifier.Model.ModelMerger;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Trace.Trace;

import java.util.Objects;

public final class VerificationItem {
    private final ModelMerger merger;
    private final Trace trace;
    private final TraceQueryCollection queryCollection;

    public VerificationItem(ModelMerger merger, final Trace trace, TraceQueryCollection queryCollection) {
        this.merger = merger;
        this.trace = trace;
        this.queryCollection = queryCollection;
    }

    public ModelMerger merger() {
        return merger;
    }

    public Trace trace() {
        return trace;
    }

    public TraceQueryCollection queryCollection() {
        return queryCollection;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (VerificationItem) obj;
        return Objects.equals(this.merger, that.merger) &&
                Objects.equals(this.trace, that.trace) &&
                Objects.equals(this.queryCollection, that.queryCollection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(merger, trace, queryCollection);
    }

    @Override
    public String toString() {
        return "ValidationItem[" +
                "merger=" + merger + ", " +
                "trace=" + trace + ", " +
                "queryCollection=" + queryCollection + ']';
    }
}
