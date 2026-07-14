package org.TraceVerifier.Pipeline;

import org.TraceVerifier.Trace.TraceQueryPair;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Interface for retrieving pipeline config data.
 */
public interface TracePipeline {
    public TracePipelineConfig getPipelineConfig();

    public List<TraceQueryPair> getTraceQueryPairs();

    public List<ModelConfigMapping> getModelConfigMapping();

    void execute() throws IOException, ExecutionException, InterruptedException;
}
