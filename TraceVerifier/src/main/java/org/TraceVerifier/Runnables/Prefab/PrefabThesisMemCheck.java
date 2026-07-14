package org.TraceVerifier.Runnables.Prefab;

import org.TraceVerifier.Model.BFAO.BFAOModelConfiguration;
import org.TraceVerifier.Model.FFAO.FFAOModelConfiguration;
import org.TraceVerifier.Model.NFAO.NFAOModelConfiguration;
import org.TraceVerifier.Pipeline.StandardTracePipelineBuilder;
import org.TraceVerifier.Pipeline.TracePipeline;
import org.TraceVerifier.Pipeline.TracePipelineConfig;
import org.TraceVerifier.Query.Properties.Checking.CheckXFAOProperties;
import org.TraceVerifier.Query.Properties.CommentUtil;
import org.TraceVerifier.Query.Properties.Tracking.*;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Trace.TraceGroup;
import org.TraceVerifier.Util.File.PromptedFileWipes;
import org.TraceVerifier.Util.MemoryTracker;
import org.TraceVerifier.Util.Trace.TraceImportUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Runnable used for creating the mem_track.txt files. It tracks the VmRSS and VmHWM to determine memory usage (see https://docs.kernel.org/filesystems/proc.html)
 */
public class PrefabThesisMemCheck {
    public static void run(TracePipelineConfig pipelineConfig, Path modelLocation, Path traceLocation) throws IOException {
        // Shared model settings
        int freeListReductionFactor = 1;

        String memCheckPath = Path.of(pipelineConfig.getBasePath(), "mem_track.txt").toString();

        // Don't want 'file' variable to dirty up the scope.
        {
            File file = new File(memCheckPath);
            Path path = Path.of(memCheckPath);
            if(file.exists())
            {
                PromptedFileWipes.YesNoQuitSingleFile(path, null);
            }
            else {
                Files.createDirectories(Path.of(pipelineConfig.getBasePath()));
                Files.createFile(path);
            }
        }

        // Setup memory tracker with additional columns.
        MemoryTracker.getSingleton().setup(memCheckPath, "trace_group", "trace_name", "allocator", "size_multiple", "query_collection", "page_size", "amt_pages", "identifier");

        record MemoryConfig(int pageSize, int amountOfPages){}
        List<MemoryConfig> configs = new ArrayList<>();
        // 256 KB
        configs.add(new MemoryConfig(1024, 256));
        configs.add(new MemoryConfig(4096, 64));
        configs.add(new MemoryConfig( 16384, 16));

        // 320 KB
        configs.add(new MemoryConfig(1024, 320));
        configs.add(new MemoryConfig(4096, 80));
        configs.add(new MemoryConfig( 16384, 20));

        // 384 KB
        configs.add(new MemoryConfig(1024, 384));
        configs.add(new MemoryConfig(4096, 96));
        configs.add(new MemoryConfig( 16384, 24));

        // Note that we iterate over each config. This also creates an execution log per config.
        for(var config : configs)
        {
            int pageSize = config.pageSize;
            int amountOfPages = config.amountOfPages;

            // FFAO
            FFAOModelConfiguration FFAOConfig1ByteAlignment = new FFAOModelConfiguration();
            FFAOConfig1ByteAlignment.setAllocatorCount(1);
            FFAOConfig1ByteAlignment.setPageSize(pageSize);
            FFAOConfig1ByteAlignment.setAmountOfPages(amountOfPages);
            FFAOConfig1ByteAlignment.setAddressAlignmentAllocator(1);
            FFAOConfig1ByteAlignment.setSizeMultipleAllocator(1);
            FFAOConfig1ByteAlignment.setFreeListReductionFactor(freeListReductionFactor);

            FFAOModelConfiguration FFAOConfig16ByteAlignment = new FFAOModelConfiguration();
            FFAOConfig16ByteAlignment.setAllocatorCount(1);
            FFAOConfig16ByteAlignment.setPageSize(pageSize);
            FFAOConfig16ByteAlignment.setAmountOfPages(amountOfPages);
            FFAOConfig16ByteAlignment.setAddressAlignmentAllocator(1);
            FFAOConfig16ByteAlignment.setSizeMultipleAllocator(16);
            FFAOConfig16ByteAlignment.setFreeListReductionFactor(freeListReductionFactor);

            FFAOModelConfiguration FFAOConfig64ByteAlignment = new FFAOModelConfiguration();
            FFAOConfig64ByteAlignment.setAllocatorCount(1);
            FFAOConfig64ByteAlignment.setPageSize(pageSize);
            FFAOConfig64ByteAlignment.setAmountOfPages(amountOfPages);
            FFAOConfig64ByteAlignment.setAddressAlignmentAllocator(1);
            FFAOConfig64ByteAlignment.setSizeMultipleAllocator(64);
            FFAOConfig64ByteAlignment.setFreeListReductionFactor(freeListReductionFactor);

            // NFAO
            NFAOModelConfiguration NFAOConfig1ByteAlignment = new NFAOModelConfiguration();
            NFAOConfig1ByteAlignment.setAllocatorCount(1);
            NFAOConfig1ByteAlignment.setPageSize(pageSize);
            NFAOConfig1ByteAlignment.setAmountOfPages(amountOfPages);
            NFAOConfig1ByteAlignment.setAddressAlignmentAllocator(1);
            NFAOConfig1ByteAlignment.setSizeMultipleAllocator(1);
            NFAOConfig1ByteAlignment.setFreeListReductionFactor(freeListReductionFactor);

            NFAOModelConfiguration NFAOConfig16ByteAlignment = new NFAOModelConfiguration();
            NFAOConfig16ByteAlignment.setAllocatorCount(1);
            NFAOConfig16ByteAlignment.setPageSize(pageSize);
            NFAOConfig16ByteAlignment.setAmountOfPages(amountOfPages);
            NFAOConfig16ByteAlignment.setAddressAlignmentAllocator(1);
            NFAOConfig16ByteAlignment.setSizeMultipleAllocator(16);
            NFAOConfig16ByteAlignment.setFreeListReductionFactor(freeListReductionFactor);

            NFAOModelConfiguration NFAOConfig64ByteAlignment = new NFAOModelConfiguration();
            NFAOConfig64ByteAlignment.setAllocatorCount(1);
            NFAOConfig64ByteAlignment.setPageSize(pageSize);
            NFAOConfig64ByteAlignment.setAmountOfPages(amountOfPages);
            NFAOConfig64ByteAlignment.setAddressAlignmentAllocator(1);
            NFAOConfig64ByteAlignment.setSizeMultipleAllocator(64);
            NFAOConfig64ByteAlignment.setFreeListReductionFactor(freeListReductionFactor);

            // BFAO
            BFAOModelConfiguration BFAOConfig1ByteAlignment = new BFAOModelConfiguration();
            BFAOConfig1ByteAlignment.setAllocatorCount(1);
            BFAOConfig1ByteAlignment.setPageSize(pageSize);
            BFAOConfig1ByteAlignment.setAmountOfPages(amountOfPages);
            BFAOConfig1ByteAlignment.setAddressAlignmentAllocator(1);
            BFAOConfig1ByteAlignment.setSizeMultipleAllocator(1);
            BFAOConfig1ByteAlignment.setFreeListReductionFactor(freeListReductionFactor);

            BFAOModelConfiguration BFAOConfig16ByteAlignment = new BFAOModelConfiguration();
            BFAOConfig16ByteAlignment.setAllocatorCount(1);
            BFAOConfig16ByteAlignment.setPageSize(pageSize);
            BFAOConfig16ByteAlignment.setAmountOfPages(amountOfPages);
            BFAOConfig16ByteAlignment.setAddressAlignmentAllocator(1);
            BFAOConfig16ByteAlignment.setSizeMultipleAllocator(16);
            BFAOConfig16ByteAlignment.setFreeListReductionFactor(freeListReductionFactor);

            BFAOModelConfiguration BFAOConfig64ByteAlignment = new BFAOModelConfiguration();
            BFAOConfig64ByteAlignment.setAllocatorCount(1);
            BFAOConfig64ByteAlignment.setPageSize(pageSize);
            BFAOConfig64ByteAlignment.setAmountOfPages(amountOfPages);
            BFAOConfig64ByteAlignment.setAddressAlignmentAllocator(1);
            BFAOConfig64ByteAlignment.setSizeMultipleAllocator(64);
            BFAOConfig64ByteAlignment.setFreeListReductionFactor(freeListReductionFactor);

            // Queries
            int allocID = 0; // We only use a single allocator
            int heapID = 0; // Only single heap models used

            TraceQueryCollection queries = new TraceQueryCollection("mem_tracking");
            queries.add(CommentUtil.createHeader(0, "Tracking Memory Usage", ""));
            queries.add(TrackingMemUsageProperties.trackLiveRequestedMemory(heapID));
            queries.add(CheckXFAOProperties.addressStartGEQ(0, 0));

            // Shared vars traces
            Path programTracePath = Path.of(traceLocation.toString(), "program");
            Path syntheticTracePath = Path.of(traceLocation.toString(), "synthetic");
            String traceSearchPattern = "glob:**/*[0].*"; // All trace-0's in that dir, recursively.

            // Synth traces

            Path simTracePath = Path.of(syntheticTracePath.toString(), "synth_sim");
            TraceGroup synthSimTraces = TraceImportUtil.getTraceGroupFromDir(simTracePath, traceSearchPattern);

            Path randomizedEvenTracePath = Path.of(syntheticTracePath.toString(), "synth_randomized_even_spread");
            TraceGroup synthRandomizedEvenTraces = TraceImportUtil.getTraceGroupFromDir(randomizedEvenTracePath, traceSearchPattern);

            Path stackTracePath = Path.of(syntheticTracePath.toString(), "synth_stack");
            TraceGroup synthStackTraces = TraceImportUtil.getTraceGroupFromDir(stackTracePath, traceSearchPattern);

            // Program Traces
            Path programLarsonTracePath = Path.of(programTracePath.toString(), "program_larson");
            TraceGroup programLarsonTraces = TraceImportUtil.getTraceGroupFromDir(programLarsonTracePath, traceSearchPattern);

            Path programCfracTracePath = Path.of(programTracePath.toString(), "program_cfrac");
            TraceGroup programCfracTraces = TraceImportUtil.getTraceGroupFromDir(programCfracTracePath, traceSearchPattern);

            // Partial generation (for now) as runs take quite a bit of time.
            pipelineConfig.setDeleteRunsBeforeSimulation(false);

            // Pipeline
            StandardTracePipelineBuilder pipelineBuilder = new StandardTracePipelineBuilder()
                    .setPipelineConfig(pipelineConfig)
                    .setMemCheck(true)
                    .setMemCheckPath(memCheckPath)
                    .addTraceGroup(synthSimTraces)
                    .addTraceGroup(synthRandomizedEvenTraces)
                    .addTraceGroup(synthStackTraces)
                    .addTraceGroup(programLarsonTraces)
                    .addTraceGroup(programCfracTraces)
                    .addModel(modelLocation, FFAOConfig1ByteAlignment)
                    .addModel(modelLocation, FFAOConfig16ByteAlignment)
                    .addModel(modelLocation, FFAOConfig64ByteAlignment)
                    .addModel(modelLocation, NFAOConfig1ByteAlignment)
                    .addModel(modelLocation, NFAOConfig16ByteAlignment)
                    .addModel(modelLocation, NFAOConfig64ByteAlignment)
                    .addModel(modelLocation, BFAOConfig1ByteAlignment)
                    .addModel(modelLocation, BFAOConfig16ByteAlignment)
                    .addModel(modelLocation, BFAOConfig64ByteAlignment)
                    .addGlobalQueryCollections(queries)
                    ;

            TracePipeline pipeline = pipelineBuilder.build();
            try {
                pipeline.execute();
            } catch (Exception e) {
                System.exit(1);
            }
        }

        // UPPAAL finalizer thread hangs even when disconnecting. Exit is required.
        System.exit(0);
    }
}

