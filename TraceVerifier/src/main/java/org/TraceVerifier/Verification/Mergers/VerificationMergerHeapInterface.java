package org.TraceVerifier.Verification.Mergers;

import org.TraceVerifier.Model.ModelConfiguration;
import org.TraceVerifier.Model.ModelMerger;
import org.TraceVerifier.Model.ModelType;
import org.TraceVerifier.Query.Properties.PropUtil;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Verification.Queries.VerificationQueriesHeapInterface;
import org.TraceVerifier.Verification.Traces.VerificationTracesHeapInterface;
import org.TraceVerifier.Verification.Traces.VerificationTracesShared;
import org.TraceVerifier.Verification.VerificationItem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

/**
 * Class that contains all Heap Interface-related verification items.
 * Refer to the thesis for referencing Requirement Req X.NUM to the properties we verify using these verification items.
 */
public class VerificationMergerHeapInterface extends VerificationMerger {
    public VerificationMergerHeapInterface(Path baseModelPath, ModelConfiguration config, boolean clearExistingQueries) {
        super(baseModelPath, config, clearExistingQueries);
    }

    /**
     * Check whether allocation and freeing occurs as expected.
     * Req. HeapInterface.2, because we check different spans of frees.
     * Req. HeapInterface.3, because we check different spans of allocations.
     * Req. HeapInterface.4, because we check the page ranges for single and multiple pages.
     * Req. HeapInterface.5, because we check the page memory after the allocations and frees.
     * Req. HeapInterface.6, because we check the heap memory after the entire allocation and frees have been finished.
     */
    public List<VerificationItem> AllocationFreeingChecks() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq = new TraceQueryCollection("AllocationFreeingChecks");

        // Single block check alloc/free
        int tick = 1;
        int pageID = 0;
        int padded = 0;

        // Check first alloc
        tq.add(VerificationQueriesHeapInterface.addressMatchTimed(allocatorID, pageID, tick, 0, config.getPageSize() - 1));
        tq.add(VerificationQueriesHeapInterface.typeMatchTimed(allocatorID, pageID, tick, true));
        tq.add(VerificationQueriesHeapInterface.CorrectPageRequest(true, allocatorID, pageID, config.getPageSize(), padded, config.getPageSize(), tick));
        tq.add(VerificationQueriesHeapInterface.CorrectHeapMemoryAtTime(allocatorID, config.getPageSize(), padded, config.getPageSize(), tick));
        tq.add(VerificationQueriesHeapInterface.CorrectPageRange(allocatorID, tick, 0, 1));

        // Check its free
        tick = 2;
        tq.add(VerificationQueriesHeapInterface.addressMatchTimed(allocatorID, pageID, tick, 0, config.getPageSize() - 1));
        tq.add(VerificationQueriesHeapInterface.typeMatchTimed(allocatorID, pageID, tick, false));
        tq.add(VerificationQueriesHeapInterface.CorrectPageRequest(false, allocatorID, pageID, config.getPageSize(), padded, config.getPageSize(), tick));
        tq.add(VerificationQueriesHeapInterface.CorrectHeapMemoryAtTime(allocatorID, 0, 0, 0, tick));
        tq.add(VerificationQueriesHeapInterface.CorrectPageRange(allocatorID, tick, 0, 1));


        // !! NFAO is handled differently, as it continues.
        boolean isNFAO = config.getModelType() == ModelType.NFAO;
        int NFAOOffset = config.getPageSize();
        int offset = config.getSizeMultiple() / 2; // Trace always halves the requested and allocated memory

        // Multiblock check alloc
        // Check first page (or offset by one page for NFAO)
        tick = 3;
        if(isNFAO) {
            pageID = 1;
            tq.add(VerificationQueriesHeapInterface.addressMatchTimed(allocatorID, pageID, tick, NFAOOffset, config.getPageSize() - 1 + NFAOOffset));
        } else {
            pageID = 0;
            tq.add(VerificationQueriesHeapInterface.addressMatchTimed(allocatorID, pageID, tick, 0, config.getPageSize() - 1));
        }
        tq.add(VerificationQueriesHeapInterface.typeMatchTimed(allocatorID, pageID, tick, true));
        tq.add(VerificationQueriesHeapInterface.CorrectPageRequest(true, allocatorID, pageID, config.getPageSize(), 0, config.getPageSize(), tick));

        // Check second page (or offset by two pages for NFAO)
        if(isNFAO) {
            pageID = 2;
            tq.add(VerificationQueriesHeapInterface.addressMatchTimed(allocatorID, pageID, tick, config.getPageSize()+NFAOOffset, 2*config.getPageSize()-1+NFAOOffset));
        } else {
            pageID = 1;
            tq.add(VerificationQueriesHeapInterface.addressMatchTimed(allocatorID, pageID, tick, config.getPageSize(), 2*config.getPageSize()-1));
        }
        tq.add(VerificationQueriesHeapInterface.typeMatchTimed(allocatorID, pageID, tick, true));
        tq.add(VerificationQueriesHeapInterface.CorrectPageRequest(true, allocatorID, pageID, config.getPageSize() - offset, offset, config.getPageSize(), tick));
        tq.add(VerificationQueriesHeapInterface.CorrectHeapMemoryAtTime(allocatorID, 2*config.getPageSize() - offset, offset, 2*config.getPageSize(), tick));
        tq.add(VerificationQueriesHeapInterface.CorrectPageRange(allocatorID, tick, isNFAO ? 1 : 0, 2));

        // Multiblock check free
        // Free the first half
        tick = 7;
        if(isNFAO) {
            pageID = 1;
            tq.add(VerificationQueriesHeapInterface.addressMatchTimed(allocatorID, pageID, tick, NFAOOffset, config.getPageSize() - 1 + NFAOOffset));
        } else {
            pageID = 0;
            tq.add(VerificationQueriesHeapInterface.addressMatchTimed(allocatorID, pageID, tick, 0, config.getPageSize() - 1));
        }
        tq.add(VerificationQueriesHeapInterface.typeMatchTimed(allocatorID, pageID, tick, false));
        tq.add(VerificationQueriesHeapInterface.CorrectPageRequest(false, allocatorID, pageID, config.getPageSize(), 0, config.getPageSize(), tick));

        // Free the second half
        if(isNFAO) {
            pageID = 2;
            tq.add(VerificationQueriesHeapInterface.addressMatchTimed(allocatorID, pageID, tick, config.getPageSize()+NFAOOffset, 2*config.getPageSize() - 1 + NFAOOffset));
        } else {
            pageID = 1;
            tq.add(VerificationQueriesHeapInterface.addressMatchTimed(allocatorID, pageID, tick, config.getPageSize(), 2*config.getPageSize()-1));
        }
        tq.add(VerificationQueriesHeapInterface.typeMatchTimed(allocatorID, pageID, tick, false));
        tq.add(VerificationQueriesHeapInterface.CorrectPageRequest(false, allocatorID, pageID, config.getPageSize() - offset, offset, config.getPageSize(), tick));
        tq.add(VerificationQueriesHeapInterface.CorrectHeapMemoryAtTime(allocatorID, 10*config.getPageSize()-3*offset, 3*offset, 10*config.getPageSize(), tick));
        tq.add(VerificationQueriesHeapInterface.CorrectPageRange(allocatorID, tick, isNFAO ? 1 : 0, 2));

        trace = VerificationTracesShared.generateStandardizedTraceVariant("AllocationFreeingChecks", config);
        merger = setupMerger("AllocationFreeingChecks", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether a valid action always leads to the processed state.
     * Req. Heap Interface.7
     */
    public List<VerificationItem> CheckActionLeadsToProcessed() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq = new TraceQueryCollection("CheckActionLeadsToProcessed");

        tq.add(VerificationQueriesHeapInterface.CheckActionLeadsToProcessed(allocatorID, 1));
        tq.add(VerificationQueriesHeapInterface.CheckActionLeadsToProcessed(allocatorID, 2));
        tq.add(VerificationQueriesHeapInterface.CheckActionLeadsToProcessed(allocatorID, 3));
        tq.add(VerificationQueriesHeapInterface.CheckActionLeadsToProcessed(allocatorID, 7));

        trace = VerificationTracesShared.generateStandardizedTrace("CheckActionLeadsToProcessed", config);
        merger = setupMerger("CheckActionLeadsToProcessed", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether only a single action is processed per tick.
     * Req. Heap Interface.8
     */
    public List<VerificationItem> CheckSingleActionProcessedPerTick() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq = new TraceQueryCollection("CheckSingleActionProcessedPerTick");

        tq.add(VerificationQueriesHeapInterface.CheckSingleActionProcessedPerTick(allocatorID));

        trace = VerificationTracesShared.generateStandardizedTrace("CheckSingleActionProcessedPerTick", config);
        merger = setupMerger("CheckSingleActionProcessedPerTick", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether we stay in processed or wait for action after an action already has been processed.
     * Req. Heap Interface.9
     */
    public List<VerificationItem> CheckStayInProcessedOrWaitForAction() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq = new TraceQueryCollection("CheckStayInProcessedOrWaitForAction");

        tq.add(VerificationQueriesHeapInterface.CheckStayInProcessedOrWaitForAction(allocatorID, 1));
        tq.add(VerificationQueriesHeapInterface.CheckStayInProcessedOrWaitForAction(allocatorID, 2));
        tq.add(VerificationQueriesHeapInterface.CheckStayInProcessedOrWaitForAction(allocatorID, 3));
        tq.add(VerificationQueriesHeapInterface.CheckStayInProcessedOrWaitForAction(allocatorID, 7));


        trace = VerificationTracesShared.generateStandardizedTrace("CheckStayInProcessedOrWaitForAction", config);
        merger = setupMerger("CheckStayInProcessedOrWaitForAction", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    // Alternating allocs and frees. We iterate over them and calculate the averages.
    private int CalculatePagePeak(int peak_PageMemory, int step, int allocsPerPeak)
    {
        int frag = 0;
        int peaks = 0;
        for(int i = 0; i < allocsPerPeak; i++)
        {
            peaks++;
            frag = ((frag * (peaks - 1)) + peak_PageMemory * 100 / ((i+1)*step))/peaks;
        }

        for(int i = 0; i < allocsPerPeak - 1; i++)
        {
            peaks++;
            frag = (   (frag * (peaks - 1)) + peak_PageMemory * 100 / ((allocsPerPeak-i - 1)*step))/peaks;
        }

        for(int i = 0; i < allocsPerPeak; i++)
        {
            peaks++;
            frag = ((frag * (peaks - 1)) + peak_PageMemory * 100 / ((i+1)*step))/peaks;
        }

        for(int i = 0; i < allocsPerPeak - 1; i++)
        {
            peaks++;
            frag = ((frag * (peaks - 1)) + peak_PageMemory * 100 / ((allocsPerPeak-i-1)*step))/peaks;
        }

        return frag;
    }

    // We iterate over the allocs only and calculate the averages.
    private int CalculatePagePeakAlloc(int peak_PageMemory, int step, int allocsPerPeak)
    {
        int frag = 0;
        int peaks = 0;

        for(int i = 0; i < allocsPerPeak; i++)
        {
            peaks++;
            frag = ((frag * (peaks - 1)) + peak_PageMemory * 100 / ((i+1)*step))/peaks;
        }

        for(int i = 0; i < allocsPerPeak; i++)
        {
            peaks++;
            frag = ((frag * (peaks - 1)) + peak_PageMemory * 100 / ((i+1)*step))/peaks;
        }

        return frag;
    }

    /**
     * Check memory fragmentation values for the WJ variants, LFB, Rel. Int/Ext fragmentation, and the presence.
     * Req. Heap Interface.1, check memory fragmentation values.
     * Note that this property requires more extensive formal proof to fully cover.
     */
    public List<VerificationItem> CheckMemoryFragmentation() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq = new TraceQueryCollection("CheckMemoryFragmentation");

        int allocsPerPeak = 16;
        int tickEnd = allocsPerPeak * 4;
        int peakA_AllocatedMemory = allocsPerPeak * config.getSizeMultiple();
        int peakA_RequestedMemory = allocsPerPeak * config.getSizeMultiple() / 2;
        int peakA_Pages = peakA_AllocatedMemory / config.getPageSize() + peakA_AllocatedMemory % config.getPageSize() == 0 ? 0 : 1;
        int peakA_PageMemory = peakA_Pages * config.getPageSize();

        int peakB_AllocatedMemory = allocsPerPeak * config.getSizeMultiple();
        int peakB_RequestedMemory = config.getSizeMultiple() > 1 ? allocsPerPeak * config.getSizeMultiple() / 2 : peakB_AllocatedMemory;
        int peakB_Pages = peakA_AllocatedMemory / config.getPageSize() + peakB_AllocatedMemory % config.getPageSize() == 0 ? 0 : 1;
        int peakB_PageMemory = peakB_Pages * config.getPageSize();

        int peak_AllocatedMemory = max(peakA_AllocatedMemory, peakB_AllocatedMemory);
        int peak_RequestedMemory = max(peakA_RequestedMemory, peakB_RequestedMemory);
        int peak_PageMemory = max(peakA_PageMemory, peakB_PageMemory);

        // There's a single size multiple used, as such, the difference between required and allocated is always 50%.
        int sharedIntFrag = config.getSizeMultiple() > 1 ? 200 : 100;
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedAllocated, PropUtil.WJType.AverageAny,          sharedIntFrag));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedAllocated, PropUtil.WJType.AverageAlloc,        sharedIntFrag));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedAllocated, PropUtil.WJType.RequiredAverage,     sharedIntFrag));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedAllocated, PropUtil.WJType.RequiredUpper,       sharedIntFrag));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedAllocated, PropUtil.WJType.RequiredLower,       sharedIntFrag));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedAllocated, PropUtil.WJType.ReservedAverage,     sharedIntFrag));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedAllocated, PropUtil.WJType.ReservedUpper,       sharedIntFrag));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedAllocated, PropUtil.WJType.ReservedLower,       sharedIntFrag));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedAllocated, PropUtil.WJType.Max,                 sharedIntFrag));


        int step = config.getSizeMultiple() == 1 ? 1 : (config.getSizeMultiple() / 2);
        int sharedFragReq = CalculatePagePeak(peak_PageMemory, step, allocsPerPeak);
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedPage, PropUtil.WJType.AverageAny,          sharedFragReq));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedPage, PropUtil.WJType.AverageAlloc,        CalculatePagePeakAlloc(peak_PageMemory, step, allocsPerPeak)));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedPage, PropUtil.WJType.RequiredAverage,     peak_PageMemory*100/peak_RequestedMemory));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedPage, PropUtil.WJType.RequiredUpper,       peak_PageMemory*100/peak_RequestedMemory));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedPage, PropUtil.WJType.RequiredLower,       peak_PageMemory*100/peak_RequestedMemory));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedPage, PropUtil.WJType.ReservedAverage,     sharedFragReq));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedPage, PropUtil.WJType.ReservedUpper,       peak_PageMemory*100/peak_RequestedMemory));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedPage, PropUtil.WJType.ReservedLower,       peak_PageMemory*100/step));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.RequestedPage, PropUtil.WJType.Max,                 peak_PageMemory*100/peak_RequestedMemory));

        step = config.getSizeMultiple();
        int sharedFragAlloc = CalculatePagePeak(peak_PageMemory, step, allocsPerPeak);
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.AllocatedPage, PropUtil.WJType.AverageAny,        sharedFragAlloc));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.AllocatedPage, PropUtil.WJType.AverageAlloc,      CalculatePagePeakAlloc(peak_PageMemory, step, allocsPerPeak)));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.AllocatedPage, PropUtil.WJType.RequiredAverage,     peak_PageMemory*100/peak_AllocatedMemory));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.AllocatedPage, PropUtil.WJType.RequiredUpper,       peak_PageMemory*100/peak_AllocatedMemory));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.AllocatedPage, PropUtil.WJType.RequiredLower,       peak_PageMemory*100/peak_AllocatedMemory));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.AllocatedPage, PropUtil.WJType.ReservedAverage,     sharedFragAlloc));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.AllocatedPage, PropUtil.WJType.ReservedUpper,       peak_PageMemory*100/peak_AllocatedMemory));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.AllocatedPage, PropUtil.WJType.ReservedLower,       peak_PageMemory*100/step));
        tq.add(VerificationQueriesHeapInterface.CheckWJFragmentation(allocatorID, tickEnd, PropUtil.WJVariant.AllocatedPage, PropUtil.WJType.Max,                 peak_PageMemory*100/peak_AllocatedMemory));


        int tickA = allocsPerPeak;
        int tickB = allocsPerPeak*6;
        int ratioAllocReq = 50;
        tq.add(VerificationQueriesHeapInterface.CheckRelativeInternalFragmentation(allocatorID, tickA, config.getSizeMultiple() > 1 ? ratioAllocReq : 0));
        tq.add(VerificationQueriesHeapInterface.CheckRelativeInternalFragmentation(allocatorID, tickB, config.getSizeMultiple() > 1 ? ratioAllocReq : 0));

        tickB = allocsPerPeak + allocsPerPeak/2;
        int pageMemTotal = config.getPageSize() * config.getAmountOfPages();

        int tickA_totalAllocPeak= config.getSizeMultiple() * allocsPerPeak;
        int tickA_amtOfPagesAllocated = tickA_totalAllocPeak / config.getPageSize() + tickA_totalAllocPeak % config.getPageSize() == 0 ? 0 : 1;
        int tickA_pageMemAllocated = tickA_amtOfPagesAllocated * config.getPageSize();

        tq.add(VerificationQueriesHeapInterface.CheckRelativeExternalFragmentation(allocatorID, tickA, PropUtil.MemoryView.ByteView, 0));
        tq.add(VerificationQueriesHeapInterface.CheckRelativeExternalFragmentation(allocatorID, tickA, PropUtil.MemoryView.PageView, 100 - tickA_totalAllocPeak * 100 / tickA_pageMemAllocated));
        tq.add(VerificationQueriesHeapInterface.CheckRelativeExternalFragmentation(allocatorID, tickA, PropUtil.MemoryView.HeapView, 100 - tickA_totalAllocPeak / pageMemTotal));

        int tickB_totalAllocPeak= config.getSizeMultiple() * allocsPerPeak / 2;
        int tickB_amtOfPagesAllocated = tickB_totalAllocPeak / config.getPageSize() + tickB_totalAllocPeak % config.getPageSize() == 0 ? 0 : 1;
        int tickB_pageMemAllocated = tickB_amtOfPagesAllocated * config.getPageSize();
        int tickB_freeMem = tickB_totalAllocPeak;

        tq.add(VerificationQueriesHeapInterface.CheckRelativeExternalFragmentation(allocatorID, tickB, PropUtil.MemoryView.ByteView, 100 - tickB_totalAllocPeak * 100 / (tickB_freeMem + tickB_totalAllocPeak)));
        tq.add(VerificationQueriesHeapInterface.CheckRelativeExternalFragmentation(allocatorID, tickB, PropUtil.MemoryView.PageView, 100 - tickB_totalAllocPeak * 100 / tickB_pageMemAllocated));
        tq.add(VerificationQueriesHeapInterface.CheckRelativeExternalFragmentation(allocatorID, tickB, PropUtil.MemoryView.HeapView, 100 - tickB_totalAllocPeak * 100 / pageMemTotal));

        // Check the LFB fragmentation. Should be none at the exact peak of fragmentation, because we only have a single (or no for BV) blocks.
        tq.add(VerificationQueriesHeapInterface.CheckLFBFragmentation(allocatorID, tickA, PropUtil.MemoryView.ByteView, 0));
        tq.add(VerificationQueriesHeapInterface.CheckLFBFragmentation(allocatorID, tickA, PropUtil.MemoryView.PageView, 0));
        tq.add(VerificationQueriesHeapInterface.CheckLFBFragmentation(allocatorID, tickA, PropUtil.MemoryView.HeapView, 0));

        tq.add(VerificationQueriesHeapInterface.CheckLFBFragmentation(allocatorID, tickA, PropUtil.MemoryView.ByteView, 0));
        tq.add(VerificationQueriesHeapInterface.CheckLFBFragmentation(allocatorID, tickA, PropUtil.MemoryView.PageView, 0));
        tq.add(VerificationQueriesHeapInterface.CheckLFBFragmentation(allocatorID, tickA, PropUtil.MemoryView.HeapView, 0));

        // Should be fragmentation present during freeing however.
        int tickB_LFB_BV = config.getSizeMultiple();
        int tickB_BlockAfterLast = tickB_pageMemAllocated - tickA_totalAllocPeak; // tickA is correct here.
        int tickB_LFB_PV = max(tickB_BlockAfterLast, config.getSizeMultiple());
        int tickB_LFB_HV = tickB_BlockAfterLast + (config.getAmountOfPages() - tickB_amtOfPagesAllocated) * config.getPageSize();

        int tickB_FreeMem_InBlocks = tickB_totalAllocPeak;
        int tickB_FreeMem_InPages = tickB_FreeMem_InBlocks + tickB_BlockAfterLast;
        int tickB_FreeMem_Heap = tickB_FreeMem_InPages + config.getHeapSize() - tickB_pageMemAllocated;

        tq.add(VerificationQueriesHeapInterface.CheckLFBFragmentation(allocatorID, tickB, PropUtil.MemoryView.ByteView, 100 - tickB_LFB_BV*100/tickB_FreeMem_InBlocks));
        tq.add(VerificationQueriesHeapInterface.CheckLFBFragmentation(allocatorID, tickB, PropUtil.MemoryView.PageView, 100 - tickB_LFB_PV*100/tickB_FreeMem_InPages));
        tq.add(VerificationQueriesHeapInterface.CheckLFBFragmentation(allocatorID, tickB, PropUtil.MemoryView.HeapView, 100 - tickB_LFB_HV*100/tickB_FreeMem_Heap));

        // Check for presence at the end (no fragmentation), and during a free cycle, which has fragmentation for all except SM=1.
        int tickAnyFrag = allocsPerPeak+2;
        tq.add(VerificationQueriesHeapInterface.CheckIntFragPresence(allocatorID, tickAnyFrag, config.getSizeMultiple() > 1));
        tq.add(VerificationQueriesHeapInterface.CheckIntFragPresence(allocatorID, tickEnd, false));

        tq.add(VerificationQueriesHeapInterface.CheckExtFragPresence(allocatorID, tickAnyFrag, PropUtil.MemoryView.ByteView, true));
        tq.add(VerificationQueriesHeapInterface.CheckExtFragPresence(allocatorID, tickAnyFrag, PropUtil.MemoryView.PageView, true));
        tq.add(VerificationQueriesHeapInterface.CheckExtFragPresence(allocatorID, tickAnyFrag, PropUtil.MemoryView.HeapView, true));

        tq.add(VerificationQueriesHeapInterface.CheckExtFragPresence(allocatorID, tickEnd, PropUtil.MemoryView.ByteView, false));
        tq.add(VerificationQueriesHeapInterface.CheckExtFragPresence(allocatorID, tickEnd, PropUtil.MemoryView.PageView, false));
        tq.add(VerificationQueriesHeapInterface.CheckExtFragPresence(allocatorID, tickEnd, PropUtil.MemoryView.HeapView, false));

        tq.add(VerificationQueriesHeapInterface.CheckFragPresence(allocatorID, tickAnyFrag, PropUtil.MemoryView.ByteView, true));
        tq.add(VerificationQueriesHeapInterface.CheckFragPresence(allocatorID, tickAnyFrag, PropUtil.MemoryView.PageView, true));
        tq.add(VerificationQueriesHeapInterface.CheckFragPresence(allocatorID, tickAnyFrag, PropUtil.MemoryView.HeapView, true));

        tq.add(VerificationQueriesHeapInterface.CheckFragPresence(allocatorID, tickEnd, PropUtil.MemoryView.ByteView, false));
        tq.add(VerificationQueriesHeapInterface.CheckFragPresence(allocatorID, tickEnd, PropUtil.MemoryView.PageView, false));
        tq.add(VerificationQueriesHeapInterface.CheckFragPresence(allocatorID, tickEnd, PropUtil.MemoryView.HeapView, false));

        trace = VerificationTracesHeapInterface.generateFragExample("CheckMemoryFragmentation", config);
        merger = setupMerger("CheckMemoryFragmentation", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    @Override
    public List<VerificationItem> mergeAll() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        items.addAll(AllocationFreeingChecks());
        items.addAll(CheckSingleActionProcessedPerTick());
        items.addAll(CheckStayInProcessedOrWaitForAction());
        items.addAll(CheckActionLeadsToProcessed());
        items.addAll(CheckMemoryFragmentation());

        return items;
    }
}
