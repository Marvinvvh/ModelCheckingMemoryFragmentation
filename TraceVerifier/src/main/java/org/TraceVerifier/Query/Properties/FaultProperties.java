package org.TraceVerifier.Query.Properties;

import org.TraceVerifier.Model.ModelType;
import org.TraceVerifier.Query.BaseTraceQuery;
import org.TraceVerifier.Query.PassFailTraceQuery;

public class FaultProperties {
    public static BaseTraceQuery checkHasDoubleFree(ModelType modelType, int allocIdentifier) {
        String allocString = PropUtil.deriveAllocatorString(modelType);
        return new PassFailTraceQuery("E<> " + allocString + "(" + allocIdentifier + ").fault_double_free", "Trace has double free.", "fault-double-free");
    }

    public static BaseTraceQuery checkHasUnallocFree(ModelType modelType, int allocIdentifier) {
        String allocString = PropUtil.deriveAllocatorString(modelType);
        return new PassFailTraceQuery("E<> " + allocString + "(" + allocIdentifier + ").fault_unalloc_free", "Trace has double free", "fault-unalloced-free");
    }

    public static BaseTraceQuery checkHasMemoryExhaustion(ModelType modelType, int allocIdentifier) {
        String allocString = PropUtil.deriveAllocatorString(modelType);
        return new PassFailTraceQuery("E<> " + allocString + "(" + allocIdentifier + ").fault_memory_exhaustion", "Allocator has memory exhaustion.", "fault-memory-exhaustion");
    }

    public static BaseTraceQuery checkHasInvalidSize(ModelType modelType, int allocIdentifier) {
        String allocString = PropUtil.deriveAllocatorString(modelType);
        return new PassFailTraceQuery("E<> " + allocString + "(" + allocIdentifier + ").fault_invalid_size", "Trace has invalid sized allocation.", "fault-invalid-size");
    }

    public static BaseTraceQuery checkHasDuplicatePointers(ModelType modelType, int allocIdentifier) {
        String allocString = PropUtil.deriveAllocatorString(modelType);
        return new PassFailTraceQuery("E<> " + allocString + "(" + allocIdentifier + ").fault_duplicate_pointers", "Trace has duplicate pointers.", "fault-duplicate-pointers");
    }

    public static BaseTraceQuery checkHasMemoryFragmentation(ModelType modelType, int allocIdentifier) {
        String allocString = PropUtil.deriveAllocatorString(modelType);
        return new PassFailTraceQuery("E<> " + allocString + "(" + allocIdentifier + ").fault_memory_fragmentation", "Trace has memory fragmentation.", "fault-memory-fragmentation");
    }
}
