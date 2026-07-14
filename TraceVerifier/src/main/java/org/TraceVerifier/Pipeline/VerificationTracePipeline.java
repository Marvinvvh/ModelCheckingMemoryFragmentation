package org.TraceVerifier.Pipeline;

import com.uppaal.model.core2.Document;
import org.TraceVerifier.IO.IOModelManager;
import org.TraceVerifier.Model.BFAO.BFAOModelConfiguration;
import org.TraceVerifier.Model.DefaultModelMerger;
import org.TraceVerifier.Model.FFAO.FFAOModelConfiguration;
import org.TraceVerifier.Model.ModelConfiguration;
import org.TraceVerifier.Model.ModelType;
import org.TraceVerifier.Model.ModelVariables;
import org.TraceVerifier.Model.NFAO.NFAOModelConfiguration;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Simulator.TraceSimulator;
import org.TraceVerifier.Trace.TraceQueryPair;
import org.TraceVerifier.Util.File.VisitorDirectoryWipe;
import org.TraceVerifier.Verification.Mergers.*;
import org.TraceVerifier.Verification.Mergers.VerificationMergerNFAO;
import org.TraceVerifier.Verification.VerificationItem;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static org.TraceVerifier.Util.Trace.AllocatorID.*;

public class VerificationTracePipeline implements TracePipeline {
    private final TracePipelineConfig pipelineConfig;
    private final List<TraceQueryPair> traceQueryPairs;
    private List<ModelConfigMapping> modelConfigMapping;

    public VerificationTracePipeline(
            TracePipelineConfig pipelineConfig,
            List<TraceQueryPair> traceQueryPairs,
            List<ModelConfigMapping> modelConfigMapping
    ) {
        this.pipelineConfig = pipelineConfig;
        this.traceQueryPairs = traceQueryPairs;
        this.modelConfigMapping = modelConfigMapping;
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

        // Run
        //       |_ Merged Model
        //          |_ Validation grouping
        //              |_ Query Collection
        //                  |_ Query (In simulator)


        // Clean up any previous runs
        Files.createDirectories(Path.of(pipelineConfig.getBasePath()));
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

        // Iterate over each run. Usually only one.
        for (int i = 0; i < pipelineConfig.getRunAmount(); i++) {
            Path runDirPath = Path.of(pipelineConfig.getBasePath(),
                    pipelineConfig.getDefaultDirNameRun() + separator + String.valueOf(i));

            // Validate each model separately.
            int modelCount = 0;
            for (ModelConfigMapping mapping : modelConfigMapping) {
                ModelConfiguration modelConfiguration = mapping.modelConfiguration();
                ModelVariables modelVariables = modelConfiguration.createModelVariables();

                String modelPostfix = separator + modelCount + separator + modelConfiguration.getIdentifier();
                String modelName = pipelineConfig.getDefaultNameMergedModel() + modelPostfix;
                String modelDirName = pipelineConfig.getDefaultDirNameMergedModel() + modelPostfix;
                Path modelPath = Path.of(runDirPath.toAbsolutePath().toString(), modelDirName);

                DefaultModelMerger merger = new DefaultModelMerger();
                merger.setModelConfiguration(modelConfiguration);
                merger.setModelVariables(modelVariables);
                merger.setClearExistingQueries(true);

                validate(separator, simulator, mapping.baseModelPath(), modelConfiguration, modelPath, modelName, modelPostfix);

            }
        }

        simulator.stop();
        ZonedDateTime endTime = ZonedDateTime.now();
        System.out.println("Verification results are available at: file://" + pipelineConfig.getBasePath());
        System.out.println("Start time: " + startTime.format(DateTimeFormatter.RFC_1123_DATE_TIME));
        System.out.println("End time: " + endTime.format(DateTimeFormatter.RFC_1123_DATE_TIME));
    }

    private record GroupedVerifItems(String name, List<VerificationItem> items){}

    private void validate(String separator, TraceSimulator simulator, Path baseModelPath, ModelConfiguration config, Path modelSaveLocation, String modelName, String modelPostfix) throws IOException {

        // Setting mapping between validation mergers, and whatever grouping we associate with that merger.
        List<GroupedVerifItems> vdAll = new ArrayList<>();
        GroupedVerifItems constants = new GroupedVerifItems("Constants", new VerificationMergerConstants(baseModelPath, config, false).mergeAll());
        GroupedVerifItems trace = new GroupedVerifItems("Trace", new VerificationMergerTrace(baseModelPath, config, false).mergeAll());
        GroupedVerifItems XFAO = new GroupedVerifItems("XFAO", new VerificationMergerXFAO(baseModelPath, config, false).mergeAll());
        GroupedVerifItems memoryPage = new GroupedVerifItems("MemoryPage", new VerificationMergerMemoryPage(baseModelPath, config, false).mergeAll());
        GroupedVerifItems heapInterface = new GroupedVerifItems("HeapInterface", new VerificationMergerHeapInterface(baseModelPath, config, false).mergeAll());
        GroupedVerifItems FFAO = new GroupedVerifItems("FFAO", new VerificationMergerFFAO(baseModelPath, config, false).mergeAll());
        GroupedVerifItems BFAO = new GroupedVerifItems("BFAO", new VerificationMergerBFAO(baseModelPath, config, false).mergeAll());
        GroupedVerifItems NFAO = new GroupedVerifItems("NFAO", new VerificationMergerNFAO(baseModelPath, config, false).mergeAll());

        // Gather all verification items into a single iterable.
        vdAll.add(constants);
        vdAll.add(trace);
        vdAll.add(XFAO);
        switch(config.getModelType())
        {
            case ModelType.FFAO -> vdAll.add(FFAO);
            case ModelType.BFAO -> vdAll.add(BFAO);
            case ModelType.NFAO -> vdAll.add(NFAO);
        }
        vdAll.add(heapInterface);
        vdAll.add(memoryPage);

        try {
            // iterate over each grouping separately
            for (var vdGroup : vdAll) {
                // Create an execution log. Don't want to add time due to alot of reruns.
                Path vdGroupPath = Path.of(modelSaveLocation.toString(), vdGroup.name);
                Files.createDirectories(vdGroupPath);

                Path logPath = Path.of(vdGroupPath.toString(), "execution.log");
                Files.deleteIfExists(logPath);

                // Setup query logger for this group.
                FileHandler fh = new FileHandler(logPath.toString(), pipelineConfig.deleteRunsBeforeSimulation());
                fh.setFormatter(new SimpleFormatter());
                Logger queryLogger = Logger.getLogger(logPath.toString());
                queryLogger.addHandler(fh);

                for (var vd : vdGroup.items)
                {
                    Path vdPath = Path.of(vdGroupPath.toString(), vd.merger().getIdentifier());
                    int queryCount = 0;

                    // Simulate each query
                    for (var qc : vd.queryCollection().getAllQueryCollections())
                    {
                        String queryPostfix = separator + queryCount + separator + qc.getIdentifier();
                        String queryName = pipelineConfig.getDefaultNameQuery() + queryPostfix;
                        String queryDirName = pipelineConfig.getDefaultDirNameQuery() + queryPostfix;
                        Path queryPath = Path.of(vdPath.toAbsolutePath().toString(), queryDirName);

                        String startLog = MessageFormat.format("--- Verifying model{0} | query_collection{1} ---", modelPostfix, queryPostfix);
                        System.out.println(startLog);
                        queryLogger.info(startLog);

                        simulate(simulator, vd.merger().getMergedModel(), qc, queryPath, queryName, queryLogger);

                        String outLog = "Result path: file://" + queryPath + "\n";
                        queryLogger.info(outLog);
                        System.out.println(outLog);
                        queryCount++;
                    }
                    vd.merger().saveMergedModel(vdPath, modelName + "_validation_" + vd.merger().getIdentifier() + ".xml", true);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static int getAllocatorIdentifier(ModelConfiguration config) {
        int allocatorIdentifier;
        if (FFAOModelConfiguration.class.isAssignableFrom(config.getClass())) {
            allocatorIdentifier = getFFAOAllocatorID(0);
        } else if (NFAOModelConfiguration.class.isAssignableFrom(config.getClass())) {
            allocatorIdentifier = getNFAOAllocatorID(0, 0);
        } else if (BFAOModelConfiguration.class.isAssignableFrom(config.getClass())) {
            allocatorIdentifier = getBFAOAllocatorID(0, 0, 0);
        } else {
            throw new Error("Unsupported configuration.");
        }
        return allocatorIdentifier;
    }

    private void simulate(TraceSimulator simulator, Document mergedModel, TraceQueryCollection queryCollection, Path queryPath, String queryName, Logger queryLogger) {
        queryCollection.removeComments();
        simulator.setProperties(queryCollection);
        simulator.setModel(mergedModel);
        try {
            simulator.simulate(false, "", queryLogger);
            IOModelManager.getInstance().Export(queryPath, queryName + ".json", queryCollection, true);
        } catch (Exception e) {
            System.exit(1);
        }
    }
}
