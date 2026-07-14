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
 * Class that contains all FFAO-related verification items.
 * Refer to the thesis for referencing Requirement Req X.NUM to the properties we verify using these verification items.
 */
public class VerificationMergerFFAO extends VerificationMerger {
    public VerificationMergerFFAO(Path baseModelPath, ModelConfiguration config, boolean clearExistingQueries) {
        super(baseModelPath, config, clearExistingQueries);
    }

    /**
     * Check whether the model can allocate/free single blocks.
     * Req. FFAO.1 Partial, as we check whether the search starts on the first block.
     * Req. FFAO.2 Partial, as we check whether the lowest-addressed block is allocated. Same sizes.
     */
    public List<VerificationItem> StartSearchFirstBlock() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = new TraceQueryCollection("StartSearchFirstBlock");

        // Check for first four pages only, due to simulation length.
        int numPages = 1;
        int stepSize = config.getPageSize()/4;

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

        trace = VerificationTracesShared.generateTriangleAllocSameSize("StartSearchFirstBlock", config, stepSize);
        merger = setupMerger("StartSearchFirstBlock", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether the model can allocate/free multi blocks.
     * Req. FFAO.1 Partial, as we check whether the search starts on the first block.
     * Req. FFAO.2 Partial, as we check whether the lowest-addressed block is allocated. Same sizes.
     */
    public List<VerificationItem> StartSearchFirstBlockMultiblock() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = new TraceQueryCollection("StartSearchFirstBlockMultiblock");

        // Check for first four pages only, due to simulation length.
        int numPages = 1;
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
     * Check whether the model can allocate/free single blocks.
     * Req. FFAO.2 Partial, as we check whether the lowest-addressed block is allocated. Different sizes.
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
        int allocSize = stepSizeA;

        int numSequences = config.getAmountOfPages();
        int numAllocations = numSequences * 4;
        int numTestedSequences = 4;

        for(int i = 0; i < numTestedSequences; i+=4)
        {
            int pageID = i / 4;
            int startAddressPage = pageID * config.getPageSize();
            int tickStart = numAllocations + numAllocations / 2 + i + 1;

            int address = startAddressPage;
            tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, tickStart, address, address + allocSize - 1));
            address += allocSize + stepSizeB;
            tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, tickStart + 1, address, address + allocSize - 1));
            address += allocSize;
            tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, tickStart + 2, address, address + allocSize - 1));
            address += allocSize;
            tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, tickStart + 3, address, address + allocSize - 1));
        }

        trace = VerificationTracesShared.generateAllocABBAEvenFree("LowerAddressDifferentSized", config, numSequences, stepSizeA, stepSizeB, allocSize, numTestedSequences);
        merger = setupMerger("LowerAddressDifferentSized", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether the model can allocate/free multi blocks.
     * Req. FFAO.2 Partial, as we check whether the lowest-addressed block is allocated. Different sizes.
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
        int allocSize = stepSizeA;

        int numSequences = config.getAmountOfPages() / 8; // (3+1)*2 pages per sequence
        int numAllocations = numSequences * 4;
        int numTestedSequences = 4;

        for(int i = 0; i < numTestedSequences; i+=4)
        {
            int pageID = i / 4;
            int startAddressPage = pageID * config.getPageSize();
            int tickStart = numAllocations + numAllocations / 2 + i + 1;
            int address = startAddressPage;
            tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, tickStart, address, address + allocSize - 1));
            address += allocSize + stepSizeB;
            tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, tickStart + 1, address, address + allocSize - 1));
            address += allocSize;
            tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, tickStart + 2, address, address + allocSize - 1));
            address += allocSize;
            tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, tickStart + 3, address, address + allocSize - 1));
        }

        trace = VerificationTracesShared.generateAllocABBAEvenFree("LowerAddressDifferentSizedMultiblock", config, numSequences, stepSizeA, stepSizeB, allocSize, numTestedSequences);
        merger = setupMerger("LowerAddressDifferentSizedMultiblock", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }
    
    /**
     * Check whether the model stops searching when it reaches the end of the list, triggers exhaustion.
     * Req. FFAO.3
     */
    public List<VerificationItem> StopSearchOnExhaustion() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = new TraceQueryCollection("StopSearchOnExhaustion");

        // Check for first four pages only, due to simulation length.
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
