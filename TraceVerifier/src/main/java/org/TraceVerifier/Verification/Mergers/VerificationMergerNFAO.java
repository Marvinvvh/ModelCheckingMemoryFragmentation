package org.TraceVerifier.Verification.Mergers;

import org.TraceVerifier.Model.ModelConfiguration;
import org.TraceVerifier.Model.ModelMerger;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Verification.Queries.*;
import org.TraceVerifier.Verification.Queries.VerificationQueriesNFAO;
import org.TraceVerifier.Verification.Traces.VerificationTracesNFAO;
import org.TraceVerifier.Verification.VerificationItem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * Class that contains all NFAO-related verification items.
 * Refer to the thesis for referencing Requirement Req X.NUM to the properties we verify using these verification items.
 */
public class VerificationMergerNFAO extends VerificationMerger {

    public VerificationMergerNFAO(Path baseModelPath, ModelConfiguration config, boolean clearExistingQueries) {
        super(baseModelPath, config, clearExistingQueries);
    }


    /**
     * Check whether the model can allocate/free single blocks in phase 1.
     * Req. NFAO.1 Partial, as we check against multiple blocks available.
     * Req. NFAO.2 Partial, as we check whether it continues from where it left off.
     * Req. NFAO.3 Partial, as we check whether searching stops.
     */
    public List<VerificationItem> SearchFirstPhaseSingleBlock(boolean differentSizes) throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        String searchFirstPhaseSingleBlock = "SearchFirstPhaseSingleBlock";
        TraceQueryCollection tq = new TraceQueryCollection(searchFirstPhaseSingleBlock);

        // 10 Allocations
        // + 1 big allocation for the rest of the page
        // + rest of pages
        // + 8 frees
        // + tick for new action
        int tick = 11 + (config.getAmountOfPages() - 1) + 8 + 1 + (differentSizes ? 1 : 0);
        int allocSize = 4 * config.getSizeMultiple();
        int startAddress = differentSizes ? 0 : config.getSizeMultiple();
        tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, tick, startAddress, startAddress + allocSize - 1));
        tq.add(VerificationQueriesNFAO.CheckStopsInFirstPhase(config.getModelType(), allocatorID, tick));
        tick+=2;
        startAddress += allocSize + config.getSizeMultiple() * (differentSizes ? 2 : 1);
        tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, tick, startAddress, startAddress + allocSize - 1));
        tq.add(VerificationQueriesNFAO.CheckStopsInFirstPhase(config.getModelType(), allocatorID, tick));


        trace = VerificationTracesNFAO.generateEnterFirstPhase(differentSizes, searchFirstPhaseSingleBlock, config);
        merger = setupMerger(searchFirstPhaseSingleBlock, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }


    /**
     * Check whether the model can allocate/free multi blocks in phase 1.
     * Req. NFAO.1 Partial, as we check against multiple blocks available.
     * Req. NFAO.2 Partial, as we check whether it continues from where it left off.
     * Req. NFAO.3 Partial, as we check whether searching stops.
     */
    public List<VerificationItem> SearchFirstPhaseMultiBlock(boolean differentSizes) throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        String searchFirstPhaseMultiBlock = "SearchFirstPhaseMultiBlock";
        TraceQueryCollection tq = new TraceQueryCollection(searchFirstPhaseMultiBlock);

        // amount of pages
        // + 8 frees
        // + tick for new action
        int tick = config.getAmountOfPages()  + 8 + 1 + (differentSizes ? 1 : 0);

        int allocSize = 4 * config.getPageSize();
        int startAddress = differentSizes ? 0 : config.getPageSize();
        tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, tick, startAddress, startAddress + allocSize - 1));
        tq.add(VerificationQueriesNFAO.CheckStopsInFirstPhase(config.getModelType(), allocatorID, tick));
        tick+=2;
        startAddress += allocSize + config.getPageSize() + (differentSizes ? config.getPageSize() : 0);
        tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, tick, startAddress, startAddress + allocSize - 1));
        tq.add(VerificationQueriesNFAO.CheckStopsInFirstPhase(config.getModelType(), allocatorID, tick));

        trace = VerificationTracesNFAO.generateEnterFirstPhaseMultiBlock(differentSizes, searchFirstPhaseMultiBlock, config);
        merger = setupMerger(searchFirstPhaseMultiBlock, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether the model can allocate/free single blocks in phase 2.
     * Req. NFAO.4 Partial, as we check multi blocks triggering phase 2.
     * Req. NFAO.6 Partial, as we have both common and different sized blocks.
     * Req. NFAO.7 Partial, as we check if it continues after reaching the last block.
     */
    public List<VerificationItem> SearchSecondPhaseSingleBlock(boolean differentSizes) throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        String searchSecondPhaseSingleBlock = "SearchSecondPhaseSingleBlock";
        TraceQueryCollection tq = new TraceQueryCollection(searchSecondPhaseSingleBlock);

        // 10 Allocations
        // + 1 big allocation for the rest of the page
        // + rest of pages
        // + 8 frees
        // + tick for new action
        int tick = 11 + (config.getAmountOfPages() - 1) + 8 + 1 + (differentSizes ? 1 : 0);

        int allocSize = 4 * config.getSizeMultiple();
        int startAddress = differentSizes ? 0 : config.getSizeMultiple();
        tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, tick, startAddress, startAddress + allocSize - 1));
        tq.add(VerificationQueriesNFAO.CheckStopsInSecondPhase(config.getModelType(), allocatorID, tick));

        trace = VerificationTracesNFAO.generateEnterSecondPhase(differentSizes, searchSecondPhaseSingleBlock, config);
        merger = setupMerger(searchSecondPhaseSingleBlock, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether the model can allocate/free multi blocks in phase 2.
     * Req. NFAO.4 Partial, as we check multi blocks triggering phase 2.
     * Req. NFAO.6 Partial, as we have both common and different sized blocks.
     * Req. NFAO.7 Partial, as we check if it continues after reaching the last block.
     */
    public List<VerificationItem> SearchSecondPhaseMultiBlock(boolean differentSizes) throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        String searchSecondPhaseMultiBlock = "SearchSecondPhaseMultiBlock";
        TraceQueryCollection tq = new TraceQueryCollection(searchSecondPhaseMultiBlock);

        // amount of pages
        // + 8 frees
        // + tick for new action
        int tick = config.getAmountOfPages()  + 8 + 1 + (differentSizes ? 1 : 0);

        int allocSize = 4 * config.getPageSize();
        int startAddress = differentSizes ? 0 : config.getPageSize();
        tq.add(VerificationQueriesXFAO.addressMatchTimed(config.getModelType(), allocatorID, tick, startAddress, startAddress + allocSize - 1));
        tq.add(VerificationQueriesNFAO.CheckStopsInSecondPhase(config.getModelType(), allocatorID, tick));

        trace = VerificationTracesNFAO.generateEnterSecondPhaseMultiblock(differentSizes, searchSecondPhaseMultiBlock, config);
        merger = setupMerger(searchSecondPhaseMultiBlock, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether allocator stops after not being able to find a block in first phase.
     * Requires starting on first block.
     // Req. NFAO.5
     */
    public List<VerificationItem> FirstPhaseErrorExhaustion() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        String firstPhaseErrorExhaustion = "FirstPhaseErrorExhaustion";
        TraceQueryCollection tq = new TraceQueryCollection(firstPhaseErrorExhaustion);

        // amount of pages
        // + 8 frees
        // + tick for new action
        int tick = config.getAmountOfPages()  + 8 + 1;
        tq.add(VerificationQueriesXFAO.ExactMemoryExhaustion(config.getModelType(), allocatorID, tick));
        tq.add(VerificationQueriesNFAO.CheckErrorFirstPhase(config.getModelType(), allocatorID, tick));


        trace = VerificationTracesNFAO.generateFirstPhaseError(firstPhaseErrorExhaustion, config);
        merger = setupMerger(firstPhaseErrorExhaustion, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether allocator stops after not being able to find a block in second phase.
     // Req. NFAO.8
     */
    public List<VerificationItem> SecondPhaseErrorExhaustion() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        String secondPhaseErrorExhaustion = "SecondPhaseErrorExhaustion";
        TraceQueryCollection tq = new TraceQueryCollection(secondPhaseErrorExhaustion);

        // amount of pages
        // + 8 frees
        // + tick for new action
        int tick = config.getAmountOfPages() + 8 + 1;
        tq.add(VerificationQueriesXFAO.ExactMemoryExhaustion(config.getModelType(), allocatorID, tick));
        tq.add(VerificationQueriesNFAO.CheckErrorSecondPhase(config.getModelType(), allocatorID, tick));

        trace = VerificationTracesNFAO.generateSecondPhaseError(secondPhaseErrorExhaustion, config);
        merger = setupMerger(secondPhaseErrorExhaustion, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }


    @Override
    public List<VerificationItem> mergeAll() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        items.addAll(SearchFirstPhaseSingleBlock(true));
        items.addAll(SearchFirstPhaseSingleBlock(false));
        items.addAll(SearchFirstPhaseMultiBlock(true));
        items.addAll(SearchFirstPhaseMultiBlock(false));
        items.addAll(SearchSecondPhaseSingleBlock(true));
        items.addAll(SearchSecondPhaseSingleBlock(false));
        items.addAll(SearchSecondPhaseMultiBlock(true));
        items.addAll(SearchSecondPhaseMultiBlock(false));
        items.addAll(FirstPhaseErrorExhaustion());
        items.addAll(SecondPhaseErrorExhaustion());

        return items;
    }

}
