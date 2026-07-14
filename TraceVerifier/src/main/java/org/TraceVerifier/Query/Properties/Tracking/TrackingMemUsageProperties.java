package org.TraceVerifier.Query.Properties.Tracking;

import org.TraceVerifier.Query.BaseTraceQuery;
import org.TraceVerifier.Query.SimulationTraceQuery;

public class TrackingMemUsageProperties {


    public static BaseTraceQuery trackLiveRequestedMemory(int heapID) {
        String queryString = "heap_memory[" + heapID + "].live_requested_memory";
        String queryComment = "Live requested memory.";
        String queryIdentifier = "live_requested_memory";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackLiveAllocatedMemory(int heapID) {
        String queryString = "heap_memory[" + heapID + "].live_allocated_memory";
        String queryComment = "Live allocated memory.";
        String queryIdentifier = "live_allocated_memory";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackLivePaddedMemory(int heapID) {
        String queryString = "heap_memory[" + heapID + "].live_padded_memory";
        String queryComment = "Live padded memory.";
        String queryIdentifier = "live_padded_memory";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackLivePageMemory(int heapID) {
        String queryString = "heap_memory[" + heapID + "].live_page_memory";
        String queryComment = "Live page memory.";
        String queryIdentifier = "live_page_memory";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackTotalRequestedMemory(int heapID) {
        String queryString = "heap_memory[" + heapID + "].total_requested_memory";
        String queryComment = "Total requested memory.";
        String queryIdentifier = "total_requested_memory";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackTotalAllocatedMemory(int heapID) {
        String queryString = "heap_memory[" + heapID + "].total_allocated_memory";
        String queryComment = "Total allocated memory.";
        String queryIdentifier = "total_allocated_memory";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackTotalPaddedMemory(int heapID) {
        String queryString = "heap_memory[" + heapID + "].total_padded_memory";
        String queryComment = "Total padded memory.";
        String queryIdentifier = "total_padded_memory";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackTotalPageMemory(int heapID) {
        String queryString = "heap_memory[" + heapID + "].total_page_memory";
        String queryComment = "Total page memory.";
        String queryIdentifier = "total_page_memory";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackFreeMemoryBV(int heapID) {
        String queryString = "heap_free_memory_BV[" + heapID + "]";
        String queryComment = "Free heap memory for byte-view.";
        String queryIdentifier = "free_memory_BV";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackFreeMemoryPV(int heapID) {
        String queryString = "heap_free_memory_PV[" + heapID + "]";
        String queryComment = "Free heap memory for page-view.";
        String queryIdentifier = "free_memory_PV";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackFreeMemoryHV(int heapID) {
        String queryString = "heap_free_memory_HV[" + heapID + "]";
        String queryComment = "Free heap memory for heap-view.";
        String queryIdentifier = "free_memory_HV";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackLargestFreeBlockBV(int heapID) {
        String queryString = "heap_pages[" + heapID + "].largest_free_block_BV.size";
        String queryComment = "Largest free block for byte-view.";
        String queryIdentifier = "LFB_BV";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackLargestFreeBlockPV(int heapID) {
        String queryString = "heap_pages[" + heapID + "].largest_free_block_PV.size";
        String queryComment = "Largest free block for page-view.";
        String queryIdentifier = "LFB_PV";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackLargestFreeBlockHV(int heapID) {
        String queryString = "heap_pages[" + heapID + "].largest_free_block_HV.size";
        String queryComment = "Largest free block for heap-view.";
        String queryIdentifier = "LFB_HV";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackAmountNonFreePages(int heapID) {
        String queryString = "heap_pages[" + heapID + "].amt_non_free_pages";
        String queryComment = "Amount of (partially) allocated pages.";
        String queryIdentifier = "amt_non_free_pages";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }
}
