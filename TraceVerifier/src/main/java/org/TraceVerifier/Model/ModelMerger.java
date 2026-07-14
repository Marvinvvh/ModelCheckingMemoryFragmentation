package org.TraceVerifier.Model;

import com.uppaal.model.core2.Document;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Trace.Trace;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface for merging a model. There's really only one instance.
 */
public interface ModelMerger {
    public String getIdentifier();

    public void setIdentifier(String identifier);

    public void setTrace(Trace trace);

    public void setModelConfiguration(ModelConfiguration modelConfig);

    public void setModelVariables(ModelVariables modelVariables);

    public void setClearExistingQueries(boolean clearExistingQueries);

    public void addTraceQueries(TraceQueryCollection queryCollection);

    public void loadModel(Path importPath) throws IOException;

    public void merge();

    public Document getMergedModel();

    public void saveMergedModel(Path path, String filename, boolean overwrite) throws IOException;
}
