package org.TraceVerifier.Query.Properties;

import org.TraceVerifier.Model.ModelType;

import java.util.Map;

import static java.util.Map.entry;

public class PropUtil {
    public static MemoryRange calculateMemoryRange(int startAddress, int size)
    {
        return new MemoryRange(startAddress, startAddress + size - 1);
    }

    public static int ceilingAlignment(int value, int sizeMultiple)
    {
        int remainder = value % sizeMultiple;
        return remainder == 0 ? value : value + sizeMultiple - remainder;
    }

    public static String always(String string)
    {
        return "A[] " + string;
    }

    public static String deriveAllocatorString(ModelType modelType) {
        return switch (modelType) {
            case FFAO -> "ffao_allocator";
            case NFAO -> "nfao_allocator";
            case BFAO -> "bfao_allocator";
        };
    }

    public static String deriveAllocator(ModelType type, int allocatorID)
    {
        return deriveAllocatorString(type) + "(" + allocatorID + ")";
    }

    public static String deriveHeapInterface(int allocatorID)
    {
        String heapID = deriveHeapID(allocatorID);
        return "heap_interface("+heapID+")";
    }

    public static String deriveHeapMemory(int allocatorID)
    {
        String heapID = deriveHeapID(allocatorID);
        return "heap_memory["+heapID+"]";
    }

    public static String derivePage(int allocatorID, int pageID)
    {
        String heapID = deriveHeapID(allocatorID);
        return "memory_page("+heapID+", "+pageID+")";
    }

    public static String derivePageMemory(int allocatorID, int pageID)
    {
        String page = derivePage(allocatorID, pageID);
        return page+".page_memory";
    }

    public static String derivePageInfo(int allocatorID, int pageID)
    {
        String page = derivePage(allocatorID, pageID);
        return page+".page_info";
    }

    public static String deriveFreeListSize(int allocatorID, int pageID)
    {
        String pageInfo = derivePageInfo(allocatorID, pageID);
        return pageInfo+".free_list_size";
    }

    public static String deriveFreeListEntry(int allocatorID, int pageID, int entry)
    {
        String pageInfo = derivePageInfo(allocatorID, pageID);
        return pageInfo+".free_list["+entry+"]";
    }

    public static String forAllInRange(int start, int end, String iterator, String property)
    {
        return forAllInRange(start, Integer.toString(end), iterator, property);
    }

    public static String forAllInRange(int start, String end, String iterator, String property)
    {
        return "forall(i : int["+start+","+ end +"] (" + property + ")";
    }

    public static String allocatorCount()
    {
        return "ALLOCATOR_COUNT";
    }

    public static String allocatorCount(int offset)
    {
        return "ALLOCATOR_COUNT - " + Integer.toString(offset);
    }

    public static String deriveProperty(String object, String property)
    {
        return object + "." + property;
    }

    public static String atTime(int time)
    {
        return "trace.y == " + time;
    }

    public static String atTimeImplies(int time, String property)
    {
        return atTime(time) + " imply (" + property + ")";
    }

    public static String atTimeWithCondImplies(int time, String cond, String property)
    {
        return and(atTime(time), cond) + " imply (" + property + ")";
    }

    public static String whenTraceProcessed(ModelType modelType, int allocatorID)
    {
        return "heap_interface(" + deriveHeapID(modelType, allocatorID) + ").processed";
    }

    public static String whenTraceProcessedTimed(int time, ModelType modelType, int allocatorID, String property)
    {
        return atTimeWithCondImplies(time, whenTraceProcessed(modelType, allocatorID), property);
    }

    public static String whenTraceProcessed(int allocatorID)
    {
        return "heap_interface(" + deriveHeapID(allocatorID) + ").processed";
    }

    public static String whenTraceProcessedTimed(int time, int allocatorID, String property)
    {
        return atTimeWithCondImplies(time, whenTraceProcessed(allocatorID), property);
    }

    public static String imply(String cond, String implies) {return cond + " imply " + implies;}
    public static String and(String a, String b)
    {
        return "(" + a + " && " + b + ")";
    }

    public static String deriveHeapRequest(ModelType modelType, int allocatorID)
    {
        return "heap_mem_req[" + PropUtil.deriveHeapID(modelType, allocatorID) + "]";
    }

    public static String deriveHeapRequestRange(ModelType modelType, int allocatorID)
    {
        return "heap_mem_req[" + PropUtil.deriveHeapID(modelType, allocatorID) + "].range";
    }

    public static String deriveHeapID(ModelType modelType, int allocatorID)
    {
        String allocator = deriveAllocator(modelType, allocatorID);
        return "allocator_heap_link[" + allocator + ".allocator_id]";
    }

    public static String deriveAllocatorActionReq(int allocatorID)
    {
        return "allocator_action_req["+allocatorID+ "]";
    }

    public static String deriveHeapRequest(int allocatorID)
    {
        return "heap_mem_req[" + PropUtil.deriveHeapID(allocatorID) + "]";
    }

    public static String deriveHeapRequestRange(int allocatorID)
    {
        return "heap_mem_req[" + PropUtil.deriveHeapID(allocatorID) + "].range";
    }

    public static String derivePageRequest(int allocatorID)
    {
        return "page_mem_req[" + PropUtil.deriveHeapID(allocatorID) + "]";
    }

    public static String derivePageRequestRange(int allocatorID)
    {
        return "page_mem_req[" + PropUtil.deriveHeapID(allocatorID) + "].range";
    }

    public static String deriveHeapID(int allocatorID)
    {
        return "allocator_heap_link[" + allocatorID + "]";
    }

    public enum WJVariant
    {
        RequestedAllocated,
        RequestedPage,
        AllocatedPage;

        private static final Map<WJVariant, String> MapWJVariant = Map.of(

                WJVariant.RequestedAllocated,"frag_int_WJ",
                WJVariant.RequestedPage,    "frag_ext_WJ_req",
                WJVariant.AllocatedPage,    "frag_ext_WJ_alloc"
        );

        public static String getValue(WJVariant view)
        {
            return MapWJVariant.get(view);
        }
    }

    public enum WJType
    {
        AverageAny,
        AverageAlloc,
        RequiredAverage,
        RequiredUpper,
        RequiredLower,
        ReservedAverage,
        ReservedUpper,
        ReservedLower,
        Max;

        private static final Map<WJType, String> MapWJType = Map.ofEntries(

                entry(AverageAny,      "frag_method_avg_any"),
                entry(AverageAlloc,          "frag_method_avg_alloc"),
                entry(RequiredAverage,     "frag_method_required_avg"),
                entry(RequiredUpper,     "frag_method_required_upper"),
                entry(RequiredLower,          "frag_method_required_lower"),
                entry(ReservedAverage,   "frag_method_reserved_avg"),
                entry(ReservedUpper,   "frag_method_reserved_upper"),
                entry(ReservedLower,          "frag_method_reserved_lower"),
                entry(Max,     "frag_method_max")
        );

        public static String getValue(WJType view)
        {
            return MapWJType.get(view);
        }
    }

//    public enum WJType
//    {
//        AvgNonZeroActionCount,
//        RequiredMaxReqMem,
//        RequiredMaxResMemUpper,
//        RequiredMaxResMemLower,
//        RequiredPeakCount,
//        RequiredActionCountUpper,
//        RequiredActionCountLower,
//        ReservedMaxResMem,
//        ReservedMaxReqMemUpper,
//        ReservedMaxReqMemLower,
//        ReservedPeakCount,
//        ReservedActionCountUpper,
//        ReservedAcountCountLower;
//
//        private static final Map<WJType, String> MapWJType = Map.ofEntries(
//
//                entry(AvgNonZeroActionCount,      "method_avg_non_zero_action_count"),
//                entry(RequiredMaxReqMem,          "method_required_max_req_mem"),
//                entry(RequiredMaxResMemUpper,     "method_required_max_res_mem_upper"),
//                entry(RequiredMaxResMemLower,     "method_required_max_res_mem_lower"),
//                entry(RequiredPeakCount,          "method_required_peak_count"),
//                entry(RequiredActionCountUpper,   "method_required_action_count_upper"),
//                entry(RequiredActionCountLower,   "method_required_action_count_lower"),
//                entry(ReservedMaxResMem,          "method_reserved_max_res_mem"),
//                entry(ReservedMaxReqMemUpper,     "method_reserved_max_req_mem_upper"),
//                entry(ReservedMaxReqMemLower,     "method_reserved_max_req_mem_lower"),
//                entry(ReservedPeakCount,          "method_reserved_peak_count"),
//                entry(ReservedActionCountUpper,   "method_reserved_action_count_upper"),
//                entry(ReservedAcountCountLower,   "method_reserved_action_count_lower")
//        );
//
//        public static String getValue(WJType view)
//        {
//            return MapWJType.get(view);
//        }
//    }

    public enum MemoryView
    {
        ByteView,
        PageView,
        HeapView;

        private static final Map<MemoryView, String> MapMemoryView = Map.of(

                MemoryView.ByteView, "BV",
                MemoryView.PageView, "PV",
                MemoryView.HeapView, "HV"
        );

        public static String getValue(MemoryView view)
        {
            return MapMemoryView.get(view);
        }
    }
}
