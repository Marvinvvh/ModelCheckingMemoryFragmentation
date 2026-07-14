package org.TraceVerifier.Runnables.Prefab;

import org.TraceVerifier.Model.BFAO.BFAOModelConfiguration;
import org.TraceVerifier.Model.FFAO.FFAOModelConfiguration;
import org.TraceVerifier.Model.NFAO.NFAOModelConfiguration;
import org.TraceVerifier.Pipeline.StandardTracePipelineBuilder;
import org.TraceVerifier.Pipeline.TracePipeline;
import org.TraceVerifier.Pipeline.TracePipelineConfig;
import org.TraceVerifier.Query.Properties.CommentUtil;
import org.TraceVerifier.Query.Properties.Tracking.*;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Trace.TraceGroup;
import org.TraceVerifier.Util.Trace.TraceImportUtil;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Runnable used for creating the benchmark results.
 */
public class PrefabThesis256KBStripped {
    public static void run(TracePipelineConfig pipelineConfig, Path modelLocation, Path traceLocation) throws IOException {
        // Shared model settings
        int pageSize = 4096;
        int amountOfPages = 64;
        int freeListReductionFactor = 1;

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

        TraceQueryCollection trackingQueries = new TraceQueryCollection("tracking-queries");
        TraceQueryCollection queriesMemUsage = new TraceQueryCollection("memory_usage");
        queriesMemUsage.add(CommentUtil.createHeader(0, "Tracking Memory Usage", ""));
        queriesMemUsage.add(TrackingMemUsageProperties.trackLiveRequestedMemory(heapID));
        queriesMemUsage.add(TrackingMemUsageProperties.trackLiveAllocatedMemory(heapID));
        queriesMemUsage.add(TrackingMemUsageProperties.trackLivePaddedMemory(heapID));
        queriesMemUsage.add(TrackingMemUsageProperties.trackLivePageMemory(heapID));
        queriesMemUsage.add(TrackingMemUsageProperties.trackTotalRequestedMemory(heapID));
        queriesMemUsage.add(TrackingMemUsageProperties.trackTotalAllocatedMemory(heapID));
        queriesMemUsage.add(TrackingMemUsageProperties.trackTotalPaddedMemory(heapID));
        queriesMemUsage.add(TrackingMemUsageProperties.trackTotalPageMemory(heapID));
        queriesMemUsage.add(TrackingMemUsageProperties.trackFreeMemoryPV(heapID));
        queriesMemUsage.add(TrackingMemUsageProperties.trackLargestFreeBlockPV(heapID));
        queriesMemUsage.add(TrackingMemUsageProperties.trackAmountNonFreePages(heapID));

        TraceQueryCollection queriesMemExtremes = new TraceQueryCollection("mem-extremes");
        queriesMemExtremes.add(CommentUtil.createHeader(0, "Tracking Memory Extremes", ""));
        queriesMemExtremes.add(TrackMemExtremesProperties.trackMinAllocatedSize(heapID));
        queriesMemExtremes.add(TrackMemExtremesProperties.trackMaxAllocatedSize(heapID));
        queriesMemExtremes.add(TrackMemExtremesProperties.trackMinRequestedSize(heapID));
        queriesMemExtremes.add(TrackMemExtremesProperties.trackMaxRequestedSize(heapID));
        queriesMemExtremes.add(TrackMemExtremesProperties.trackMinPaddedSize(heapID));
        queriesMemExtremes.add(TrackMemExtremesProperties.trackMaxPaddedSize(heapID));
        queriesMemExtremes.add(TrackMemExtremesProperties.trackMaxAllocatedMemory(heapID));
        queriesMemExtremes.add(TrackMemExtremesProperties.trackMaxRequestedMemory(heapID));
        queriesMemExtremes.add(TrackMemExtremesProperties.trackMaxPaddedMemory(heapID));
        queriesMemExtremes.add(TrackMemExtremesProperties.trackMaxPageMemory(heapID));

        TraceQueryCollection queriesActions = new TraceQueryCollection("actions");
        queriesActions.add(CommentUtil.createHeader(0, "Tracking States", ""));
        queriesActions.add(TrackingTraceProperties.trackActionCount());
        queriesActions.add(TrackingTraceProperties.trackAllocActionCount());
        queriesActions.add(TrackingTraceProperties.trackFreeActionCount());

        TraceQueryCollection queriesStatesAndErrors = new TraceQueryCollection("states");
        queriesStatesAndErrors.add(CommentUtil.createHeader(0, "Tracking Actions", ""));
        queriesStatesAndErrors.add(TrackingTraceProperties.trackTraceDone());
        queriesStatesAndErrors.add(TrackingTraceProperties.trackTraceError());
        queriesStatesAndErrors.add(TrackingAllocatorProperties.trackAllocatorError(allocID));
        queriesStatesAndErrors.add(TrackingHeapProperties.trackHeapError(heapID));

        TraceQueryCollection queriesIntFrag = new TraceQueryCollection("internal_fragmentation");
        queriesIntFrag.add(CommentUtil.createHeader(0, "Tracking Internal Fragmentation", ""));
        queriesIntFrag.add(TrackingIntFragProperties.trackRelativeInternal(heapID));

        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodRequiredAvg(heapID));
        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodReservedAvg(heapID));
        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodRequiredUpper(heapID));
        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodReservedUpper(heapID));
        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodRequiredLower(heapID));
        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodReservedLower(heapID));
        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodMax(heapID));

        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodRequiredMaxReqMem(heapID));
        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodRequiredMaxResMemUpper(heapID));
        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodRequiredMaxResMemLower(heapID));
        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodRequiredPeakCount(heapID));
        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodRequiredActionCountUpper(heapID));
        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodRequiredActionCountLower(heapID));

        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodReservedMaxResMem(heapID));
        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodReservedMaxReqMemUpper(heapID));
        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodReservedMaxReqMemLower(heapID));
        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodReservedPeakCount(heapID));
        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodReservedActionCountUpper(heapID));
        queriesIntFrag.add(TrackingIntFragProperties.trackWJMethodReservedActionCountLower(heapID));

        TraceQueryCollection queriesExtFrag = new TraceQueryCollection("external_fragmentation");
        queriesExtFrag.add(CommentUtil.createHeader(0, "Tracking External Fragmentation", ""));
        queriesExtFrag.add(TrackingExtFragProperties.trackWJReqMethodReservedAvg(heapID));
        queriesExtFrag.add(TrackingExtFragProperties.trackWJReqMethodReservedUpper(heapID));
        queriesExtFrag.add(TrackingExtFragProperties.trackWJReqMethodReservedLower(heapID));
        queriesExtFrag.add(TrackingExtFragProperties.trackWJReqMethodMax(heapID));


        queriesExtFrag.add(TrackingExtFragProperties.trackWJReqMethodReservedMaxResMem(heapID));
        queriesExtFrag.add(TrackingExtFragProperties.trackWJReqMethodReservedMaxReqMemUpper(heapID));
        queriesExtFrag.add(TrackingExtFragProperties.trackWJReqMethodReservedMaxReqMemLower(heapID));
        queriesExtFrag.add(TrackingExtFragProperties.trackWJReqMethodReservedPeakCount(heapID));
        queriesExtFrag.add(TrackingExtFragProperties.trackWJReqMethodReservedActionCountUpper(heapID));
        queriesExtFrag.add(TrackingExtFragProperties.trackWJReqMethodReservedActionCountLower(heapID));

        queriesExtFrag.add(TrackingExtFragProperties.trackWJAllocMethodReservedAvg(heapID));
        queriesExtFrag.add(TrackingExtFragProperties.trackWJAllocMethodReservedUpper(heapID));
        queriesExtFrag.add(TrackingExtFragProperties.trackWJAllocMethodReservedLower(heapID));
        queriesExtFrag.add(TrackingExtFragProperties.trackWJAllocMethodMax(heapID));

        queriesExtFrag.add(TrackingExtFragProperties.trackWJAllocMethodReservedMaxResMem(heapID));
        queriesExtFrag.add(TrackingExtFragProperties.trackWJAllocMethodReservedMaxReqMemUpper(heapID));
        queriesExtFrag.add(TrackingExtFragProperties.trackWJAllocMethodReservedMaxReqMemLower(heapID));
        queriesExtFrag.add(TrackingExtFragProperties.trackWJAllocMethodReservedPeakCount(heapID));
        queriesExtFrag.add(TrackingExtFragProperties.trackWJAllocMethodReservedActionCountUpper(heapID));
        queriesExtFrag.add(TrackingExtFragProperties.trackWJAllocMethodReservedActionCountLower(heapID));

        queriesExtFrag.add(TrackingExtFragProperties.trackRelativeExternalPV(heapID));

        queriesExtFrag.add(TrackingExtFragProperties.trackLFBPV(heapID));

        // Easy way to enable/disable tracking queries
        trackingQueries.add(queriesMemUsage);
        trackingQueries.add(queriesMemExtremes);
        trackingQueries.add(queriesActions);
        trackingQueries.add(queriesStatesAndErrors);
        trackingQueries.add(queriesIntFrag);
        trackingQueries.add(queriesExtFrag);

        // Shared vars traces. We only want those annotated with 0-2.
        Path programTracePath = Path.of(traceLocation.toString(), "program");
        Path syntheticTracePath = Path.of(traceLocation.toString(), "synthetic");
        String traceSearchPattern = "glob:**/*[0-2].*"; // All files in that dir, recursively.

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
                .addGlobalQueryCollections(trackingQueries);

        TracePipeline pipeline = pipelineBuilder.build();
        try {
            pipeline.execute();
        } catch (Exception e) {
            System.exit(1);
        }

        // UPPAAL finalizer thread hangs even when disconnecting. Exit is required.
        System.exit(0);
    }
}

