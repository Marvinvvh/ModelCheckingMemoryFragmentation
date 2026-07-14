package org.TraceVerifier;

import org.TraceVerifier.IO.IOModelManager;
import org.TraceVerifier.Pipeline.TracePipelineConfig;
import org.TraceVerifier.Runnables.Prefab.*;
import org.TraceVerifier.Runnables.TraceGen.TraceGenSingleOrigin;
import org.TraceVerifier.Runnables.TraceGen.TraceGenThesis256KB;
import org.TraceVerifier.Runnables.TraceGen.TraceGenThesisFragVerif;
import org.TraceVerifier.Runnables.TraceGen.TraceGenTypes;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Command(name = "TraceVerifier", version = "TraceVerifier 1.0", mixinStandardHelpOptions = true,
        subcommands = {PipelinePrefab.class, TraceGen.class})
public class TraceVerifier implements Runnable {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new TraceVerifier()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        System.out.println("Select trace-gen or a prefabricated pipeline.");
    }
}

@Command(name = "trace-gen")
class TraceGen implements Runnable {
    @Parameters(description = "Type of trace generator to run.")
    private TraceGenTypes tracegenType;

    @Parameters(description = "Directory containing the program traces.")
    private String programTraceDirectory = "";

    @Parameters(description = "Directory for saving the allocation traces.")
    private String allocationTraceDirectory = "";

    @Option(names = {"--seed"}, description = "Seed used for generating traces. Note that different trace-gen configs may use this as a base seed from which other seeds are generated.")
    private long seed = 0;

    @Option(names = {"--export-gen-config"}, description = "Indicate whether generator configs should be exported.")
    private boolean exportGeneratorConfig = false;

    @Option(names = {"--gen-config-output-dir"}, description = "Save the generator configurations in the given directory.")
    private String generatorConfigDirectory = "";

    @Option(names = {"--timed-subdir"}, description = "Save allocations in a timed subdirectory of the given allocation trace directory.")
    private boolean timestamped = false;

    @Option(names = {"--clean-config-dirs"}, description = "Delete all json files within the config directories before writing (non-recursive).")
    private boolean cleanConfigDirs = false;

    @Option(names = {"--clean-alloc-dirs"}, description = "Delete all json files within the config directories before writing (non-recursive).")
    private boolean cleanAllocDirs = false;

    @Override
    public void run() {
        try {
            Path allocDir = timestamped ? Path.of(allocationTraceDirectory, LocalDateTime.now().toString()) : Path.of(allocationTraceDirectory);
            switch (tracegenType) {
                case Thesis ->
                        TraceGenThesis256KB.run(Path.of(programTraceDirectory), allocDir, cleanAllocDirs, Path.of(generatorConfigDirectory), exportGeneratorConfig, cleanConfigDirs);
                case SingleOrigin ->
                        TraceGenSingleOrigin.run(Path.of(programTraceDirectory), allocDir, cleanAllocDirs, Path.of(generatorConfigDirectory), exportGeneratorConfig, cleanConfigDirs, seed);
                case FragVerif ->
                        TraceGenThesisFragVerif.run(Path.of(programTraceDirectory), allocDir, cleanAllocDirs, Path.of(generatorConfigDirectory), exportGeneratorConfig, cleanConfigDirs, seed);
                default -> System.out.println("Invalid trace generator.");
            }
        } catch (Exception e) {
            System.out.println("Verify whether the given directories are valid. Check whether the type of trace-gen is usable with the given directories.");
        }
    }
}

@Command(name = "pipeline-prefab")
class PipelinePrefab implements Runnable {
    @Parameters(description = "Type of pipeline to run.")
    private PrefabTypes prefabType;

    @Option(names = {"--pipeline-config", "--config", "-c"}, description = "Custom pipeline configuration")
    private String configPath = "";

    @Parameters(description = "Model location for the base model.")
    private String modelLocation = "";

    @Option(names = {"-t"}, description = "Location which holds all the trace(group)s.")
    private String traceLocation = "";

    public static void main(String[] args) {
        int exitCode = new CommandLine(new TraceVerifier()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        try {
            Path modelPath = Path.of(modelLocation);
            Path tracePath = Path.of(traceLocation);
            TracePipelineConfig config = IOModelManager.getInstance().Import(Path.of(configPath), TracePipelineConfig.class);
            switch (prefabType) {
                case MemTrack -> PrefabThesisMemCheck.run(config, modelPath.toAbsolutePath(), tracePath.toAbsolutePath());
                case FragVerif -> PrefabThesisFragVerif.run(config, modelPath.toAbsolutePath(), tracePath.toAbsolutePath());
                case Thesis -> PrefabThesis256KBStripped.run(config, modelPath.toAbsolutePath(), tracePath.toAbsolutePath());
                case ThesisVerif ->
                        PrefabThesis256KBVerif.run(config, modelPath.toAbsolutePath());
                default -> System.out.println("Invalid pipeline.");
            }
        } catch (IOException e) {
            System.out.println("Verify whether the pipeline configuration and base model path exist and are valid.");
        }
    }
}
