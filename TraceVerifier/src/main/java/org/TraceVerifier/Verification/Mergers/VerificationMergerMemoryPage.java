package org.TraceVerifier.Verification.Mergers;

import org.TraceVerifier.Model.ModelConfiguration;
import org.TraceVerifier.Model.ModelMerger;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Verification.Queries.VerificationQueriesMemoryPage;
import org.TraceVerifier.Verification.Traces.VerificationTracesMemoryPage;
import org.TraceVerifier.Verification.Traces.VerificationTracesShared;
import org.TraceVerifier.Verification.VerificationItem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that contains all Memory Page-related verification items.
 * Refer to the thesis for referencing Requirement Req X.NUM to the properties we verify using these verification items.
 */
public class VerificationMergerMemoryPage extends VerificationMerger {

    public VerificationMergerMemoryPage(Path baseModelPath, ModelConfiguration config, boolean clearExistingQueries) {
        super(baseModelPath, config, clearExistingQueries);
    }

    /**
     * Check whether the page has the expected amount of memory after a free/allocation.
     * Req. Memory Page.1
     */
    public List<VerificationItem> ExpectedMemory() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        String expectedMemory = "ExpectedMemory";
        tq = new TraceQueryCollection(expectedMemory);
        int tick = 0;
        int pageDiv = 4;
        int allocations = 1 * pageDiv; // Don't need to do all pages. Same concept.
        int offset = config.getSizeMultiple() / 2;
        int sizeAlloc = config.getPageSize() / pageDiv - offset;

        // Check allocations heap memory
        boolean allocate = true;
        for(int i = 0; i < allocations; i++)
        {
            tick++;
            int pageNum = i / pageDiv;
            int numAllocations = i + 1;
            int requested = sizeAlloc * numAllocations;
            int padded = offset * numAllocations;
            int allocated = requested+padded;
            tq.add(VerificationQueriesMemoryPage.CheckHeapMemoryForPage(allocate, allocatorID, pageNum, tick, requested, padded, allocated));
        }
        // Check frees heap memory
        allocate = false;
        int numFrees = 0;
        for(int i = 0; i < allocations; i+=2)
        {
            tick++;
            numFrees++;
            int pageNum = i / pageDiv;
            int requested = sizeAlloc * (allocations - numFrees);
            int padded = offset * (allocations - numFrees);
            int allocated = requested+padded;
            tq.add(VerificationQueriesMemoryPage.CheckHeapMemoryForPage(allocate, allocatorID, pageNum, tick, requested, padded, allocated));
        }
        // Add some uneven allocation checks
        for(int i = 1; i < allocations; i+=2)
        {
            tick++;
            numFrees++;
            int pageNum = i / pageDiv;
            int requested = sizeAlloc * (allocations - numFrees);
            int padded = offset * (allocations - numFrees);
            int allocated = requested+padded;
            tq.add(VerificationQueriesMemoryPage.CheckHeapMemoryForPage(allocate, allocatorID, pageNum, tick, requested, padded, allocated));
        }

        trace = VerificationTracesShared.generateTriangleAlternate(expectedMemory, config, allocations, sizeAlloc);
        merger = setupMerger(expectedMemory, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether the page has the expected amount of memory after a free/allocation.
     * Req. Memory Page.2, because it manipulates the free blocks as expected.
     * Req. Memory Page.3, because free blocks are resized multiple times, and matched in addressing.
     * Req. Memory Page.4, because the free list was empty when blocks were removed.
     * Req. Memory Page.5 Partial, because we check repeated types of deallocations, which pass.
     */
    public List<VerificationItem> AllocationAddressRangeLinear() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        int tick = 0;
        int pageDiv = 4;
        int allocations = 1 * pageDiv; // Don't need to do all pages, same concept.
        int offset = config.getSizeMultiple() / 2;
        int sizeAlloc = config.getPageSize() / pageDiv - offset;
        int allocatedSize = sizeAlloc + offset;

        String allocationAddressRangeLinear = "AllocationAddressRangeLinear";
        tq = new TraceQueryCollection(allocationAddressRangeLinear);

        // Check the allocations.
        boolean allocate = true;
        for(int i = 0; i < allocations; i++)
        {
            tick++;

            int pageNum = i / pageDiv;
            int numAllocationsPage = (i % pageDiv) + 1;
            int address = pageNum * config.getPageSize() + (numAllocationsPage) * allocatedSize;

            // Check whether the free list ranges are
            if(numAllocationsPage < pageDiv)
            {
                tq.add(VerificationQueriesMemoryPage.CheckFreeListEntry(allocate, allocatorID, pageNum, tick, 0, address, config.getPageSize() - (numAllocationsPage) * allocatedSize));
                tq.add(VerificationQueriesMemoryPage.CheckFreeListSize(allocate, allocatorID, pageNum, tick, 1));
                tq.add(VerificationQueriesMemoryPage.CheckPageState(allocate, allocatorID, pageNum, tick, VerificationQueriesMemoryPage.PageState.ePageStatePartialAlloc));
            }
            // Check whether the free list is empty because we allocated the last bit.
            else
            {
                tq.add(VerificationQueriesMemoryPage.CheckFreeListSize(allocate, allocatorID, pageNum, tick, 0));
                tq.add(VerificationQueriesMemoryPage.CheckPageState(allocate, allocatorID, pageNum, tick, VerificationQueriesMemoryPage.PageState.ePageStateAlloc));
            }
        }

        // Check the frees.
        for(int i = allocations; i < allocations*2; i++)
        {
            tick++;

            int pageNum = i / pageDiv;
            int NumFreesPage = (i % pageDiv) + 1;
            int address = pageNum * config.getPageSize() + (NumFreesPage) * allocatedSize;

            tq.add(VerificationQueriesMemoryPage.CheckFreeListSize(allocate, allocatorID, pageNum, tick, 1));
            tq.add(VerificationQueriesMemoryPage.CheckFreeListEntry(allocate, allocatorID, pageNum, tick, 0, address, (NumFreesPage) * allocatedSize));

            // Check whether the free list ranges are
            if(NumFreesPage < pageDiv)
            {
                tq.add(VerificationQueriesMemoryPage.CheckPageState(allocate, allocatorID, pageNum, tick, VerificationQueriesMemoryPage.PageState.ePageStatePartialAlloc));
            }
            // Check whether the free list is empty because we allocated the last bit.
            else
            {
                tq.add(VerificationQueriesMemoryPage.CheckPageState(allocate, allocatorID, pageNum, tick, VerificationQueriesMemoryPage.PageState.ePageStateFree));
            }
        }

        trace = VerificationTracesShared.generateTriangle(allocationAddressRangeLinear, config, allocations, sizeAlloc);
        merger = setupMerger(allocationAddressRangeLinear, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }
    
    /**
     * Check whether coalescing is possible for LR, RL, and LRRL variants.
     // Req. Memory Page.5 Partial, because we check for valid ranges for allocations.
     // Req. Memory Page.6, part of trace creation.
     // Req. Memory Page.7, because we check all coalescing variants.
     */
    public List<VerificationItem> FreeCoalescingAndEntries() throws IOException {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        String freeCoalescingAndEntries = "FreeCoalescingAndEntries";
        tq = new TraceQueryCollection(freeCoalescingAndEntries);

        boolean allocate = false;

        int pageChecks = 1; // No need to test for all pages.
        int freeCount = 0;
        int tickStart = 5 * config.getAmountOfPages() + 1;
        for(int pageNum = 0; pageNum < pageChecks; pageNum++)
        {
            // Check LR merge
            int pageAddress = config.getPageSize()*pageNum;
            int blockEndAddress =  pageAddress + config.getPageSize() - config.getSizeMultiple();
            int blockEndSize = config.getSizeMultiple();
            int tickPage = tickStart + freeCount;
            tq.add(VerificationQueriesMemoryPage.CheckFreeListSize(allocate, allocatorID, pageNum, tickPage, 1));
            tq.add(VerificationQueriesMemoryPage.CheckFreeListEntry(allocate, allocatorID, pageNum, tickPage, 0, blockEndAddress, blockEndSize));

            blockEndAddress = pageAddress + config.getPageSize() - 2 * config.getSizeMultiple();
            blockEndSize = config.getSizeMultiple() * 2;
            tq.add(VerificationQueriesMemoryPage.CheckFreeListSize(allocate, allocatorID, pageNum, tickPage + 1, 1));
            tq.add(VerificationQueriesMemoryPage.CheckFreeListEntry(allocate, allocatorID, pageNum, tickPage + 1, 0, blockEndAddress, blockEndSize));

            // Check LL merge
            int blockStartAddress = pageAddress;
            int blockStartSize = config.getSizeMultiple();
            tq.add(VerificationQueriesMemoryPage.CheckFreeListSize(allocate, allocatorID, pageNum, tickPage + 2, 2));
            tq.add(VerificationQueriesMemoryPage.CheckFreeListEntry(allocate, allocatorID, pageNum, tickPage + 2, 0, blockStartAddress, blockStartSize));
            tq.add(VerificationQueriesMemoryPage.CheckFreeListEntry(allocate, allocatorID, pageNum, tickPage + 2, 1, blockEndAddress, blockEndSize));

            blockStartSize = 2 * config.getSizeMultiple();
            tq.add(VerificationQueriesMemoryPage.CheckFreeListSize(allocate, allocatorID, pageNum, tickPage + 3, 2));
            tq.add(VerificationQueriesMemoryPage.CheckFreeListEntry(allocate, allocatorID, pageNum, tickPage + 3, 0, blockStartAddress, blockStartSize));
            tq.add(VerificationQueriesMemoryPage.CheckFreeListEntry(allocate, allocatorID, pageNum, tickPage + 3, 1, blockEndAddress, blockEndSize));

            // Check LRRL merge
            int blockFinalAddress = pageAddress;
            int blockFinalSize = config.getPageSize();
            tq.add(VerificationQueriesMemoryPage.CheckFreeListSize(allocate, allocatorID, pageNum, tickPage + 4, 1));
            tq.add(VerificationQueriesMemoryPage.CheckFreeListEntry(allocate, allocatorID, pageNum, tickPage + 4, 0, blockFinalAddress, blockFinalSize));
            tq.add(VerificationQueriesMemoryPage.CheckPageState(allocate, allocatorID, pageNum, tickPage + 4, VerificationQueriesMemoryPage.PageState.ePageStateFree));

            freeCount+=5;
        }

        trace = VerificationTracesMemoryPage.generateMergePatterns(freeCoalescingAndEntries, config, pageChecks);
        merger = setupMerger(freeCoalescingAndEntries, trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    @Override
    public List<VerificationItem> mergeAll() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        items.addAll(ExpectedMemory());
        items.addAll(AllocationAddressRangeLinear());
        items.addAll(FreeCoalescingAndEntries());
        return items;
    }

}
