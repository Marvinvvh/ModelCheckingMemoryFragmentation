package org.TraceVerifier.Trace;

import org.TraceVerifier.Query.TraceQueryCollection;

import java.util.List;

public record TraceQueryPair(TraceGroup traceGroup, List<TraceQueryCollection> traceQueryCollections) {
}
