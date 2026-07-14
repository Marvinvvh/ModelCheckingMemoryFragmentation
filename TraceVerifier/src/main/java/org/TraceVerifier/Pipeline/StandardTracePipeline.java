package org.TraceVerifier.Pipeline;

import com.uppaal.model.core2.Document;
import org.TraceVerifier.IO.IOModelManager;
import org.TraceVerifier.Model.DefaultModelMerger;
import org.TraceVerifier.Model.ModelConfiguration;
import org.TraceVerifier.Model.ModelVariables;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Simulator.TraceSimulator;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Trace.TraceQueryPair;
import org.TraceVerifier.Util.File.VisitorDirectoryWipe;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class StandardTracePipeline implements TracePipeline {
    private final TracePipelineConfig pipelineConfig;
    private final List<TraceQueryPair> traceQueryPairs;
    private List<ModelConfigMapping> modelConfigMapping;
    private final boolean memCheck;

    public StandardTracePipeline(
            TracePipelineConfig pipelineConfig,
            List<TraceQueryPair> traceQueryPairs,
            List<ModelConfigMapping> modelConfigMapping,
            boolean memCheck
    ) {
        this.pipelineConfig = pipelineConfig;
        this.traceQueryPairs = traceQueryPairs;
        this.modelConfigMapping = modelConfigMapping;
        this.memCheck = memCheck;
    }

    @Override
    public List<TraceQueryPair> getTraceQueryPairs() {
        return traceQueryPairs;
    }

    @Override
    public TracePipelineConfig getPipelineConfig() {
        return pipelineConfig;
    }

    @Override
    public List<ModelConfigMapping> getModelConfigMapping() {
        return modelConfigMapping;
    }

    public void setModelConfigMapping(List<ModelConfigMapping> modelConfigMapping) {
        this.modelConfigMapping = modelConfigMapping;
    }

    @Override
    public void execute() throws IOException, ExecutionException, InterruptedException {
        TraceSimulator simulator = new TraceSimulator();
        simulator.setupEngine(pipelineConfig.getServerPath());
        ZonedDateTime startTime = ZonedDateTime.now();

        // Remove any previous runs if wanted.
        String separator = pipelineConfig.getDefaultNameSeparator();
        if (pipelineConfig.deleteRunsBeforeSimulation()) {
            try (DirectoryStream<Path> directories = Files.newDirectoryStream(Path.of(pipelineConfig.getBasePath()))) {
                for (Path directory : directories) {
                    // Only want to delete exact run matches, nothing else. Note that if different run notations have been used in that folder this doesn't work.
                    String runDeletionRegex = "^" + pipelineConfig.getDefaultDirNameRun() + separator + "[0-9]+$";
                    if (directory.getFileName().toString().matches(runDeletionRegex)) {
                        Files.walkFileTree(directory, new VisitorDirectoryWipe());
                    }
                }
            } catch (Exception e) {
                System.out.println("Unable to delete all run directories");
            }
        }

        // Setup a shared logger for the execution log (execution.log)
        Files.createDirectories(Path.of(pipelineConfig.getBasePath()));
        Path logPath = Path.of(pipelineConfig.getBasePath(), "execution.log");
        FileHandler fh = new FileHandler(logPath.toString(), false);
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT [%4$-7s] %5$s%n"); // TIME DATE [LEVEL] LOG
        fh.setFormatter(new SimpleFormatter());
        Logger queryLogger = Logger.getLogger("QueryLogger");
        queryLogger.addHandler(fh);

        // The simulation structure is something like:
        // Run
        // |_ Trace Group
        //    |_ Trace
        //       |_ Merged Model
        //          |_ Query Collection
        //             |_ Query (In simulator)
        for (int i = 0; i < pipelineConfig.getRunAmount(); i++) {
            Path runDirPath = Path.of(pipelineConfig.getBasePath(),
                    pipelineConfig.getDefaultDirNameRun() + separator + i);

            int traceGroupCount = 0;
            // For each trace group...
            for (TraceQueryPair traceQueryPair : traceQueryPairs) {
                String traceGroupPostfix = separator + traceGroupCount + separator + traceQueryPair.traceGroup().getGroupInformation().identifier();
                String traceGroupName = pipelineConfig.getDefaultNameTraceGroup() + traceGroupPostfix;
                String traceGroupDirName = pipelineConfig.getDefaultDirNameTraceGroup() + traceGroupPostfix;
                Path traceGroupPath = Path.of(runDirPath.toAbsolutePath().toString(), traceGroupDirName);
                try {
                    IOModelManager.getInstance().Export(traceGroupPath, traceGroupName + ".json", traceQueryPair.traceGroup().getGroupInformation(), true);
                } catch (Exception _) {
                    System.exit(1);
                }

                // For each trace...
                int traceCount = 0;
                for (Trace trace : traceQueryPair.traceGroup().getTraces()) {
                    String tracePostfix = separator + traceCount + separator + trace.getIdentifier();
                    String traceName = pipelineConfig.getDefaultNameTrace() + tracePostfix;
                    String traceDirName = pipelineConfig.getDefaultDirNameTrace() + tracePostfix;
                    Path tracePath = Path.of(traceGroupPath.toAbsolutePath().toString(), traceDirName);

                    // Export the trace we're simulating.
                    try {
                        IOModelManager.getInstance().Export(tracePath, traceName + ".json", trace, true);
                    } catch (Exception _) {
                        System.exit(1);
                    }

                    // For each model...
                    int modelCount = 0;
                    for (ModelConfigMapping mapping : modelConfigMapping) {
                        ModelConfiguration modelConfiguration = mapping.modelConfiguration();
                        ModelVariables modelVariables = modelConfiguration.createModelVariables();
                        modelVariables.processTrace(trace);

                        String modelPostfix = separator + modelCount + separator + modelConfiguration.getIdentifier();
                        String variablesName = pipelineConfig.getDefaultNameModelVariables() + modelPostfix;
                        String configName = pipelineConfig.getDefaultNameModelConfig() + modelPostfix;
                        String modelName = pipelineConfig.getDefaultNameMergedModel() + modelPostfix;
                        String modelDirName = pipelineConfig.getDefaultDirNameMergedModel() + modelPostfix;
                        Path modelPath = Path.of(tracePath.toAbsolutePath().toString(), modelDirName);

                        // Prepare the items that are to be merged into the model, settings etc.
                        DefaultModelMerger merger = new DefaultModelMerger();
                        merger.setTrace(trace);
                        merger.setModelConfiguration(modelConfiguration);
                        merger.setModelVariables(modelVariables);
                        merger.setClearExistingQueries(true);

                        // Add queries to the model
                        for (TraceQueryCollection queryCollection : traceQueryPair.traceQueryCollections()) {
                            merger.addTraceQueries(queryCollection);
                        }

                        // Attempt to merge the model, and save itself, its configuration and its variables.
                        try {
                            merger.loadModel(mapping.baseModelPath());
                            merger.merge();
                            merger.saveMergedModel(modelPath, modelName + ".xml", true);
                            IOModelManager.getInstance().Export(modelPath, configName + ".json", modelConfiguration, true);
                            IOModelManager.getInstance().Export(modelPath, variablesName + ".json", modelVariables, true);
                        } catch (Exception e) {
                            System.exit(1);
                        }

                        // For each query collection, simulate.
                        int queryCount = 0;
                        for (TraceQueryCollection queryCollection : traceQueryPair.traceQueryCollections()) {
                            String queryPostfix = separator + queryCount + separator + queryCollection.getIdentifier();
                            String queryName = pipelineConfig.getDefaultNameQuery() + queryPostfix;
                            String queryDirName = pipelineConfig.getDefaultDirNameQuery() + queryPostfix;
                            Path queryPath = Path.of(modelPath.toAbsolutePath().toString(), queryDirName);

                            String logString = MessageFormat.format("{0} | {1} | {2} | {3,number,#} | {4} | {5,number,#} | {6,number,#}",
                                    traceGroupPostfix,
                                    tracePostfix,
                                    modelConfiguration.getModelType().name(),
                                    modelConfiguration.getSizeMultiple(),
                                    queryPostfix,
                                    modelConfiguration.getPageSize(),
                                    modelConfiguration.getAmountOfPages());

                            String startLog = "--- Verifying " + logString + " ---";
                            queryLogger.info(startLog);
                            System.out.println(startLog);

                            simulate(simulator, merger.getMergedModel(), queryCollection, queryPath, queryName, logString, queryLogger);

                            String outLog = "Result path: file://" + queryPath + "\n";
                            queryLogger.info(outLog);
                            System.out.println(outLog);

                            queryCount++;
                        }
                        modelCount++;
                    }
                    traceCount++;
                }
                traceGroupCount++;
            }
        }

        simulator.stop();
        ZonedDateTime endTime = ZonedDateTime.now();
        System.out.println("Verification results are available at: file://" + pipelineConfig.getBasePath());
        System.out.println("Start time: " + startTime.format(DateTimeFormatter.RFC_1123_DATE_TIME));
        System.out.println("End time: " + endTime.format(DateTimeFormatter.RFC_1123_DATE_TIME));
    }

    /**
     * Starts the trace simulation with the UPPAAL server.
     */
    private void simulate(TraceSimulator simulator, Document mergedModel, TraceQueryCollection queryCollection, Path queryPath, String queryName, String logString, Logger queryLogger) {
        queryCollection.removeComments();
        simulator.setProperties(queryCollection);
        simulator.setModel(mergedModel);
        try {
            simulator.simulate(memCheck, logString, queryLogger);
            IOModelManager.getInstance().Export(queryPath, queryName + ".json", queryCollection, true);
        } catch (Exception e) {
            System.exit(1);
        }
    }
}
