package org.TraceVerifier.Model;

import com.uppaal.model.core2.*;
import com.uppaal.model.io2.Problem;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Trace.Trace;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class DefaultModelMerger implements ModelMerger {
    private Document model;
    private Trace trace;
    private ModelConfiguration modelConfiguration;
    private ModelVariables modelVariables;
    private String identifier;
    private final List<TraceQueryCollection> queryCollections;
    private boolean clearExistingQueries;

    public DefaultModelMerger() {
        identifier = "default_model_merger";
        queryCollections = new ArrayList<>();
        clearExistingQueries = false;
    }

    /**
     * Load a UPPAAL model from an import path.
     */
    @Override
    public void loadModel(Path importPath) throws IOException {
        List<Problem> problems = new ArrayList<>();
        model = new DocumentPrototype().load(URI.create("file://" + importPath), problems);
        // For some reason this is always null in the lib
        PlotConfiguration plot = new PlotConfiguration("", new ArrayList<>());
        model.setConcretePlots(new ArrayList<>());
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setTrace(Trace trace) {
        this.trace = trace;
    }

    @Override
    public void setModelVariables(ModelVariables modelVariables) {
        this.modelVariables = modelVariables;
    }

    public void setModelConfiguration(ModelConfiguration modelConfiguration) {
        this.modelConfiguration = modelConfiguration;
    }

    public void setClearExistingQueries(boolean clearExistingQueries) {
        this.clearExistingQueries = clearExistingQueries;
    }

    /**
     * Add trace queries to merge along with the model.
     */
    @Override
    public void addTraceQueries(TraceQueryCollection queryCollection) {
        queryCollections.add(queryCollection);
    }

    private String replaceModelTag(String propertyString, String tagName, String insertionString) {
        String startTag = MessageFormat.format("// <{0}>\n", tagName);
        String endTag = MessageFormat.format("// <END_{0}>\n", tagName);
        int indexInsert = propertyString.indexOf(startTag) + startTag.length();
        int indexEnd = propertyString.indexOf(endTag);
        if (indexInsert < 0) {
            throw new Error("Unavailable property.");
        }

        StringBuilder builder = new StringBuilder(propertyString);
        builder.replace(indexInsert, indexEnd, insertionString);
        return builder.toString();
    }

    private void mergeGlobalConstants() {
        Property declProperty = model.getProperty("declaration");
        String declString = replaceModelTag(declProperty.getValue(), "GLOBAL_CONFIG_CONSTANTS", modelConfiguration.generateGlobalConstants());
        model.setProperty("declaration", declString);
    }

    private void mergeAllocatorConstants() {
        Property declProperty = model.getProperty("declaration");
        String declString = replaceModelTag(declProperty.getValue(), modelConfiguration.getAllocatorTag(), modelConfiguration.generateAllocatorConstants());
        model.setProperty("declaration", declString);
    }

    private void mergeVariables() {
        Property declProperty = model.getProperty("declaration");
        String declString = replaceModelTag(declProperty.getValue(), "CONFIG_VARIABLES", modelVariables.deriveGlobalVariables());
        model.setProperty("declaration", declString);
    }

    private void mergeTrace() {
        Optional<AbstractTemplate> searchTraceArray = model.getTemplateList().stream()
                .filter(t -> t.getPropertyValue("name").equals("trace"))
                .findFirst();

        if (searchTraceArray.isEmpty()) {
            throw new Error("No existing trace array");
        }

        AbstractTemplate traceArray = searchTraceArray.get();
        Property declProperty = traceArray.getProperty("declaration");
        String declString = replaceModelTag(declProperty.getValue(), "TRACE_INSERT", trace.toTraceString());
        traceArray.setProperty("declaration", declString);
    }

    private void mergeSystemDeclarations() {
        Property declProperty = model.getProperty("system");
        String declString = replaceModelTag(declProperty.getValue(), "ALLOCATOR_TEMPLATES", modelConfiguration.getAllocatorTemplateName() + ",\n");
        model.setProperty("system", declString);
    }

    private void mergeQueries() {
        if (queryCollections.isEmpty()) {
            return;
        }

        QueryList queries = model.getQueryList();
        if (clearExistingQueries) {
            queries.removeAll();
        }

        for (var qc : queryCollections) {
            for (var q : qc.getAllQueries()) {
                queries.addLast(q.generateUPPAALQuery());
            }
        }
    }
    /**
     * Merge all constants, the trace, system configuration, and queries into a model.
     */
    public void merge() {
        mergeGlobalConstants();
        mergeAllocatorConstants();
        mergeVariables();
        mergeTrace();
        mergeSystemDeclarations();
        mergeQueries();
    }

    @Override
    public Document getMergedModel() {
        return model;
    }

    @Override
    public void saveMergedModel(Path path, String filename, boolean overwrite) throws IOException {
        Path file = Path.of(path.toAbsolutePath().toString(), filename);

        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        if (!Files.exists(path) || (!overwrite && Files.exists(file))) {
            throw new Error("Location doesn't exist or file already exists.");
        }

        model.save(file.toAbsolutePath().toString());
    }
}
