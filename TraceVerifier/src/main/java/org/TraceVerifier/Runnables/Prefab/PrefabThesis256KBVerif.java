package org.TraceVerifier.Runnables.Prefab;

import org.TraceVerifier.Model.BFAO.BFAOModelConfiguration;
import org.TraceVerifier.Model.FFAO.FFAOModelConfiguration;
import org.TraceVerifier.Model.NFAO.NFAOModelConfiguration;
import org.TraceVerifier.Pipeline.*;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Runnable used for verifying the model. Note that this uses the verification pipeline.
 */
public class PrefabThesis256KBVerif {
    public static void run(TracePipelineConfig pipelineConfig, Path modelLocation) throws IOException {
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

        // Partial generation (for now) as runs take quite a bit of time.
        pipelineConfig.setDeleteRunsBeforeSimulation(false);

        // Pipeline
        VerificationTracePipelineBuilder pipelineBuilder = new VerificationTracePipelineBuilder()
                .setPipelineConfig(pipelineConfig)
//                .addModel(modelLocation, FFAOConfig1ByteAlignment)
//                .addModel(modelLocation, FFAOConfig16ByteAlignment)
//                .addModel(modelLocation, FFAOConfig64ByteAlignment)
//                .addModel(modelLocation, NFAOConfig1ByteAlignment)
//                .addModel(modelLocation, NFAOConfig16ByteAlignment)
//                .addModel(modelLocation, NFAOConfig64ByteAlignment)
                .addModel(modelLocation, BFAOConfig1ByteAlignment)
                .addModel(modelLocation, BFAOConfig16ByteAlignment)
                .addModel(modelLocation, BFAOConfig64ByteAlignment)
                ;

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

