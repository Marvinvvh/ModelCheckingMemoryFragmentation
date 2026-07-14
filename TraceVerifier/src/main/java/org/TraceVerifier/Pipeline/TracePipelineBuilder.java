package org.TraceVerifier.Pipeline;

import org.TraceVerifier.Model.ModelConfiguration;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Trace.TraceGroup;
import org.TraceVerifier.Trace.TraceQueryPair;

import java.nio.file.Path;
import java.util.List;

/**
 * Interface for setting up a builder for different pipelines.
 */
public interface TracePipelineBuilder<T extends TracePipelineBuilder<T>> {
    public T setPipelineConfig(TracePipelineConfig tracePipelineConfig);

    public T addTraceQueryPairing(TraceQueryPair traceQueryPair);

    public T addTraceQueryPairings(List<TraceQueryPair> traceQueryPair);

    public T addTraceGroup(TraceGroup traceGroup);

    public T addTraceGroup(TraceGroup traceGroup, TraceQueryCollection traceQueryCollection);

    public T addTraceGroup(TraceGroup traceGroup, List<TraceQueryCollection> traceQueryCollections);

    public T addTraceGroups(List<TraceGroup> traceGroups);

    public T addTraceGroups(List<TraceGroup> traceGroups, TraceQueryCollection traceQueryCollection);

    public T addTraceGroups(List<TraceGroup> traceGroups, List<TraceQueryCollection> traceQueryCollections);

    public T addGlobalQueryCollections(TraceQueryCollection traceQueryCollection);

    public T addGlobalQueryCollections(List<TraceQueryCollection> traceQueryCollection);

    public T addModel(Path baseModel, ModelConfiguration config);

    public TracePipeline build();
}
