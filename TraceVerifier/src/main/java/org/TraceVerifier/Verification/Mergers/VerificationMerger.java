package org.TraceVerifier.Verification.Mergers;

import org.TraceVerifier.Model.DefaultModelMerger;
import org.TraceVerifier.Model.ModelConfiguration;
import org.TraceVerifier.Model.ModelMerger;
import org.TraceVerifier.Model.ModelVariables;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Verification.VerificationItem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public abstract class VerificationMerger {
    protected Path baseModelPath;
    protected ModelConfiguration config;
    protected boolean clearExistingQueries;
    protected final int allocatorID = 0; // This is constant amongst all validation.

    public VerificationMerger(Path baseModelPath, ModelConfiguration config, boolean clearExistingQueries) {
        this.baseModelPath = baseModelPath;
        this.config = config;
        this.clearExistingQueries = clearExistingQueries;
    }

    public void setClearExistingQueries(boolean clearExistingQueries) {
        this.clearExistingQueries = clearExistingQueries;
    }

    protected ModelMerger setupMerger(String identifier, Trace trace, TraceQueryCollection traceQueries) throws IOException {
        ModelVariables variables = config.createModelVariables();
        variables.processTrace(trace);

        ModelMerger merger = new DefaultModelMerger();
        merger.setIdentifier(identifier);
        merger.setModelConfiguration(config);
        merger.setModelVariables(variables);
        merger.setClearExistingQueries(clearExistingQueries);
        merger.addTraceQueries(traceQueries);
        merger.setTrace(trace);
        merger.loadModel(baseModelPath);
        merger.merge();

        return merger;
    }

    public abstract List<VerificationItem> mergeAll() throws IOException;
}
