package org.TraceVerifier.Query.Properties.Tracking;

import org.TraceVerifier.Query.BaseTraceQuery;
import org.TraceVerifier.Query.SimulationTraceQuery;

public class TrackMemExtremesProperties {

    public static BaseTraceQuery trackMinAllocatedSize(int heapID) {
        String queryString = "heap_memory_extremes[" + heapID + "].min_allocated_size";
        String queryComment = "Smallest allocated size.";
        String queryIdentifier = "min_allocated_size";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackMaxAllocatedSize(int heapID) {
        String queryString = "heap_memory_extremes[" + heapID + "].max_allocated_size";
        String queryComment = "Largest allocated size.";
        String queryIdentifier = "max_allocated_size";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackMinRequestedSize(int heapID) {
        String queryString = "heap_memory_extremes[" + heapID + "].min_requested_size";
        String queryComment = "Smallest requested size.";
        String queryIdentifier = "min_requested_size";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackMaxRequestedSize(int heapID) {
        String queryString = "heap_memory_extremes[" + heapID + "].max_requested_size";
        String queryComment = "Largest requested size.";
        String queryIdentifier = "max_requested_size";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackMinPaddedSize(int heapID) {
        String queryString = "heap_memory_extremes[" + heapID + "].min_padded_size";
        String queryComment = "Smallest padding of an allocation request.";
        String queryIdentifier = "min_padded_size";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackMaxPaddedSize(int heapID) {
        String queryString = "heap_memory_extremes[" + heapID + "].max_padded_size";
        String queryComment = "Largest padding of an allocation request.";
        String queryIdentifier = "max_padded_size";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackMaxAllocatedMemory(int heapID) {
        String queryString = "heap_memory_extremes[" + heapID + "].max_allocated_memory";
        String queryComment = "Largest amount of live allocated memory.";
        String queryIdentifier = "max_allocated_memory";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackMaxRequestedMemory(int heapID) {
        String queryString = "heap_memory_extremes[" + heapID + "].max_requested_memory";
        String queryComment = "Largest amount of live requested memory.";
        String queryIdentifier = "max_requested_memory";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackMaxPaddedMemory(int heapID) {
        String queryString = "heap_memory_extremes[" + heapID + "].max_padded_memory";
        String queryComment = "Largest amount of live padded memory.";
        String queryIdentifier = "max_padded_memory";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }

    public static BaseTraceQuery trackMaxPageMemory(int heapID) {
        String queryString = "heap_memory_extremes[" + heapID + "].max_page_memory";
        String queryComment = "Largest amount of live page memory.";
        String queryIdentifier = "max_page_memory";
        return new SimulationTraceQuery(queryString, queryComment, queryIdentifier, "bytes");
    }


}
