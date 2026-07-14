package org.TraceVerifier.Pipeline;

import org.TraceVerifier.Model.ModelConfiguration;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Trace.TraceGroup;
import org.TraceVerifier.Trace.TraceQueryPair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to generate a pipeline that starts a simulation without additional verification.
 */
public class StandardTracePipelineBuilder implements TracePipelineBuilder<StandardTracePipelineBuilder> {
    private TracePipelineConfig tracePipelineConfig;
    private List<TraceQueryCollection> globalTraceQueryCollections = new ArrayList<>();
    private List<ModelConfigMapping> modelConfigurationMap = new ArrayList<>();
    private List<TraceQueryPair> traceQueryPairs = new ArrayList<>();
    private boolean memCheck = false;

    @Override
    public StandardTracePipelineBuilder setPipelineConfig(TracePipelineConfig tracePipelineConfig) {
        this.tracePipelineConfig = tracePipelineConfig;
        return this;
    }

    /**
     * Enable mem check function, which will create the mem_track.txt file.
     */
    public StandardTracePipelineBuilder setMemCheck(boolean memCheck) {
        this.memCheck = memCheck;
        return this;
    }

    public StandardTracePipelineBuilder setMemCheckPath(String memCheckPath) throws IOException {
        File file = new File(memCheckPath);
        if(memCheck && !file.exists())
        {
            throw new IOException("Mem. Check path does not exist.");
        }
        return this;
    }

    @Override
    public StandardTracePipelineBuilder addTraceQueryPairing(TraceQueryPair traceQueryPair) {
        traceQueryPairs.add(traceQueryPair);
        return null;
    }

    @Override
    public StandardTracePipelineBuilder addTraceQueryPairings(List<TraceQueryPair> traceQueryPairs) {
        this.traceQueryPairs.addAll(traceQueryPairs);
        return null;
    }

    @Override
    public StandardTracePipelineBuilder addTraceGroup(TraceGroup traceGroup) {
        traceQueryPairs.add(new TraceQueryPair(traceGroup, new ArrayList<>()));
        return this;
    }

    @Override
    public StandardTracePipelineBuilder addTraceGroup(TraceGroup traceGroup, TraceQueryCollection traceQueryCollection) {
        List<TraceQueryCollection> traceQueryCollections = new ArrayList<>();
        traceQueryCollections.add(traceQueryCollection);
        traceQueryPairs.add(new TraceQueryPair(traceGroup, traceQueryCollections));
        return this;
    }

    @Override
    public StandardTracePipelineBuilder addTraceGroup(TraceGroup traceGroup, List<TraceQueryCollection> traceQueryCollections) {
        traceQueryPairs.add(new TraceQueryPair(traceGroup, traceQueryCollections));
        return this;
    }

    @Override
    public StandardTracePipelineBuilder addTraceGroups(List<TraceGroup> traceGroups) {
        for (var group : traceGroups) {
            addTraceGroup(group);
        }
        return this;
    }

    @Override
    public StandardTracePipelineBuilder addTraceGroups(List<TraceGroup> traceGroups, TraceQueryCollection traceQueryCollection) {
        for (var group : traceGroups) {
            addTraceGroup(group);
        }
        return this;
    }

    @Override
    public StandardTracePipelineBuilder addTraceGroups(List<TraceGroup> traceGroups, List<TraceQueryCollection> traceQueryCollections) {
        for (var group : traceGroups) {
            addTraceGroup(group);
        }
        return this;
    }

    @Override
    public StandardTracePipelineBuilder addGlobalQueryCollections(TraceQueryCollection traceQueryCollection) {
        this.globalTraceQueryCollections.add(traceQueryCollection);
        return this;
    }

    @Override
    public StandardTracePipelineBuilder addGlobalQueryCollections(List<TraceQueryCollection> traceQueryCollection) {
        this.globalTraceQueryCollections.addAll(traceQueryCollection);
        return this;
    }

    @Override
    public StandardTracePipelineBuilder addModel(Path baseModel, ModelConfiguration config) {
        this.modelConfigurationMap.add(new ModelConfigMapping(baseModel, config));
        return this;
    }


    /**
     * Join together the traces and queries to iterate over, with the different model configurations into a runnable pipeline.
     */
    @Override
    public StandardTracePipeline build() {

        // We always want the global queries to come first, as this makes the ordering of properties for all trace groups easier.
        List<TraceQueryPair> orderedTraceQueryPairs = new ArrayList<>();
        for (var traceQueryPair : traceQueryPairs) {
            TraceQueryPair newTraceQueryPair = new TraceQueryPair(traceQueryPair.traceGroup(), globalTraceQueryCollections);
            newTraceQueryPair.traceQueryCollections().addAll(traceQueryPair.traceQueryCollections());
            orderedTraceQueryPairs.add(newTraceQueryPair);
        }

        return new StandardTracePipeline(tracePipelineConfig, orderedTraceQueryPairs, modelConfigurationMap, memCheck);
    }
}
