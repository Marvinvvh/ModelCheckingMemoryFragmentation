package org.TraceVerifier.Verification.Mergers;

import org.TraceVerifier.Model.ModelConfiguration;
import org.TraceVerifier.Model.ModelMerger;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Verification.Queries.VerificationQueriesXFAO;
import org.TraceVerifier.Verification.Traces.VerificationTracesShared;
import org.TraceVerifier.Verification.VerificationItem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that contains all BFAO-related verification items.
 * Refer to the thesis for referencing Requirement Req X.NUM to the properties we verify using these verification items.
 */
public class VerificationMergerBFAO extends VerificationMerger {
    public VerificationMergerBFAO(Path baseModelPath, ModelConfiguration config, boolean clearExistingQueries) {
        super(baseModelPath, config, clearExistingQueries);
    }

    /**
     * Check whether the model can allocate/free single blocks.
     * Req. BFAO.1 Partial, as we check whether the search starts on the first block.
     * Req. BFAO.2 Partial, as we check whether the perfect fits also match.
     */
    public List<VerificationItem> StartSearchFirstBlock() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = new TraceQueryCollection("StartSearchFirstBlock");

        int numPages = 1; // Single page check is fine as we separate it from the concept of the heap
        int stepSize = config.getPageSize()/4;

        // Check whether first few allocations match
        int address = 0;
        int numAllocations = config.getAmountOfPages() * 4;
        for(int i = 0; i < numPages*4; i++)
        {
            address = i*stepSize;
            tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, i + 1, address, address + stepSize - 1));
        }

        // Check whether frees function as expected.
        for(int i = 0; i < numPages*4; i++)
        {
            address = i*2*stepSize;
            // Because we first free even, then uneven, we know this is alloc 1,3,...
            tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, i + 1 + (numAllocations + numAllocations / 2), address, address + stepSize - 1));
        }

        trace = VerificationTracesShared.generateTriangleAllocSameSize("StartSearchFirstBlock", config, stepSize);
        merger = setupMerger("StartSearchFirstBlock", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether the model can allocate/free multi blocks.
     * Req. BFAO.1 Partial, as we check whether the search starts on the first block.
     * Req. BFAO.2 Partial, as we check whether the perfect fits also match.
     */
    public List<VerificationItem> StartSearchFirstBlockMultiblock() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = new TraceQueryCollection("StartSearchFirstBlockMultiblock");

        int numPages = 1; // Single page check is fine as we separate it from the concept of the heap
        int stepSize = config.getPageSize()*2;

        int address = 0;
        int numAllocations = config.getAmountOfPages() * 4;
        for(int i = 0; i < numPages*4; i++)
        {
            address = i*stepSize;
            tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, i + 1, address, address + stepSize - 1));
        }

        for(int i = 0; i < numPages*4; i++)
        {
            address = i*2*stepSize;
            tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, i + 1 + (numAllocations + numAllocations / 2), address, address + stepSize - 1));
        }

        trace = VerificationTracesShared.generateTriangleAllocSameSize("StartSearchFirstBlockMultiblock", config, stepSize);
        merger = setupMerger("StartSearchFirstBlockMultiblock", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }


    /**
     * Check whether the best fitting single block is prioritized for differing sizes.
     * Req. BFAO.3 Partial, as we check whether the best fit is chosen for single block.
     */
    public List<VerificationItem> LowerAddressDifferentSized() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = new TraceQueryCollection("LowerAddressDifferentSized");


        int stepSizeA = config.getPageSize() / 8;
        int stepSizeB = config.getPageSize() / 8 * 3;
        int allocSize = stepSizeA - 2*config.getSizeMultiple();

        int numSequences = config.getAmountOfPages();
        int numAllocations = numSequences * 4;
        int numTestedSequences = 4;

        for(int i = 0; i < numTestedSequences; i++)
        {
            int startAddressPage = i * config.getPageSize();
            int tickStart = numAllocations + numAllocations/2 + i + 1;

            int address = startAddressPage;
            tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, tickStart, address, address + allocSize - 1));
        }

        trace = VerificationTracesShared.generateAllocABBAEvenFree("LowerAddressDifferentSized", config, numSequences, stepSizeA, stepSizeB, allocSize, numTestedSequences);
        merger = setupMerger("LowerAddressDifferentSized", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether the best fitting multi block is prioritized for differing sizes.
     * Req. BFAO.3 Partial, as we check whether the best fit is chosen for multi block.
     */
    public List<VerificationItem> LowerAddressDifferentSizedMultiblock() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = new TraceQueryCollection("LowerAddressDifferentSizedMultiblock");


        int stepSizeA = config.getPageSize();
        int stepSizeB = config.getPageSize()*3;
        int allocSize = stepSizeA - 2*config.getSizeMultiple();

        int numSequences = config.getAmountOfPages() / 8;
        int numAllocations = numSequences * 4;
        int numTestedSequences = 4;

        for(int i = 0; i < numTestedSequences; i++)
        {
            int startAddressPage = i * config.getPageSize();
            int tickStart = numAllocations + numAllocations/2 + i + 1;

            int address = startAddressPage;
            tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, tickStart, address, address + allocSize - 1));
        }

        trace = VerificationTracesShared.generateAllocABBAEvenFree("LowerAddressDifferentSizedMultiblock", config, numSequences, stepSizeA, stepSizeB, allocSize, numTestedSequences);
        merger = setupMerger("LowerAddressDifferentSizedMultiblock", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether allocator stops whenever it reaches the end of the free list. Memory exhaustion.
     * Req. BFAO.4
     */
    public List<VerificationItem> StopSearchOnExhaustion() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        int stepSize = config.getPageSize()/4;
        int numAllocations = 6*config.getAmountOfPages();
        int exhaustionTime = numAllocations + 1;
        tq = VerificationQueriesXFAO.ExactMemoryExhaustion(config.getModelType(), allocatorID, exhaustionTime);
        trace = VerificationTracesShared.generateTriangleAllocSameSizeExhaustion("StopSearchOnExhaustion", config, stepSize);
        merger = setupMerger("StopSearchOnExhaustion", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    @Override
    public List<VerificationItem> mergeAll() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        items.addAll(StartSearchFirstBlock());
        items.addAll(StartSearchFirstBlockMultiblock());
        items.addAll(LowerAddressDifferentSized());
        items.addAll(LowerAddressDifferentSizedMultiblock());
        items.addAll(StopSearchOnExhaustion());
        return items;
    }
}
