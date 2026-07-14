package org.TraceVerifier.Pipeline;

import org.TraceVerifier.Model.ModelConfiguration;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Trace.TraceGroup;
import org.TraceVerifier.Trace.TraceQueryPair;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Interface for setting up a builder for different pipelines.
 */
public class VerificationTracePipelineBuilder implements TracePipelineBuilder<VerificationTracePipelineBuilder> {
    private TracePipelineConfig tracePipelineConfig;
    private List<TraceQueryCollection> globalTraceQueryCollections = new ArrayList<>();
    private List<ModelConfigMapping> modelConfigurationMap = new ArrayList<>();
    private List<TraceQueryPair> traceQueryPairs = new ArrayList<>();
    private boolean validate;
    private boolean simulate;

    @Override
    public VerificationTracePipelineBuilder setPipelineConfig(TracePipelineConfig tracePipelineConfig) {
        this.tracePipelineConfig = tracePipelineConfig;
        return this;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public void setSimulate(boolean simulate) {
        this.simulate = simulate;
    }

    @Override
    public VerificationTracePipelineBuilder addTraceQueryPairing(TraceQueryPair traceQueryPair) {
        traceQueryPairs.add(traceQueryPair);
        return null;
    }

    @Override
    public VerificationTracePipelineBuilder addTraceQueryPairings(List<TraceQueryPair> traceQueryPairs) {
        this.traceQueryPairs.addAll(traceQueryPairs);
        return null;
    }

    @Override
    public VerificationTracePipelineBuilder addTraceGroup(TraceGroup traceGroup) {
        traceQueryPairs.add(new TraceQueryPair(traceGroup, new ArrayList<>()));
        return this;
    }

    @Override
    public VerificationTracePipelineBuilder addTraceGroup(TraceGroup traceGroup, TraceQueryCollection traceQueryCollection) {
        List<TraceQueryCollection> traceQueryCollections = new ArrayList<>();
        traceQueryCollections.add(traceQueryCollection);
        traceQueryPairs.add(new TraceQueryPair(traceGroup, traceQueryCollections));
        return this;
    }

    @Override
    public VerificationTracePipelineBuilder addTraceGroup(TraceGroup traceGroup, List<TraceQueryCollection> traceQueryCollections) {
        traceQueryPairs.add(new TraceQueryPair(traceGroup, traceQueryCollections));
        return this;
    }

    @Override
    public VerificationTracePipelineBuilder addTraceGroups(List<TraceGroup> traceGroups) {
        for (var group : traceGroups) {
            addTraceGroup(group);
        }
        return this;
    }

    @Override
    public VerificationTracePipelineBuilder addTraceGroups(List<TraceGroup> traceGroups, TraceQueryCollection traceQueryCollection) {
        for (var group : traceGroups) {
            addTraceGroup(group);
        }
        return this;
    }

    @Override
    public VerificationTracePipelineBuilder addTraceGroups(List<TraceGroup> traceGroups, List<TraceQueryCollection> traceQueryCollections) {
        for (var group : traceGroups) {
            addTraceGroup(group);
        }
        return this;
    }

    @Override
    public VerificationTracePipelineBuilder addGlobalQueryCollections(TraceQueryCollection traceQueryCollection) {
        this.globalTraceQueryCollections.add(traceQueryCollection);
        return this;
    }

    @Override
    public VerificationTracePipelineBuilder addGlobalQueryCollections(List<TraceQueryCollection> traceQueryCollection) {
        this.globalTraceQueryCollections.addAll(traceQueryCollection);
        return this;
    }

    @Override
    public VerificationTracePipelineBuilder addModel(Path baseModel, ModelConfiguration config) {
        this.modelConfigurationMap.add(new ModelConfigMapping(baseModel, config));
        return this;
    }

    /**
     * Used to generate a pipeline that starts a simulation.
     */
    @Override
    public VerificationTracePipeline build() {

        // We always want the global queries to come first, as this makes the ordering of properties for all trace groups easier.
        List<TraceQueryPair> orderedTraceQueryPairs = new ArrayList<>();
        for (var traceQueryPair : traceQueryPairs) {
            TraceQueryPair newTraceQueryPair = new TraceQueryPair(traceQueryPair.traceGroup(), globalTraceQueryCollections);
            newTraceQueryPair.traceQueryCollections().addAll(traceQueryPair.traceQueryCollections());
            orderedTraceQueryPairs.add(newTraceQueryPair);
        }

        return new VerificationTracePipeline(tracePipelineConfig, orderedTraceQueryPairs, modelConfigurationMap);
    }
}
