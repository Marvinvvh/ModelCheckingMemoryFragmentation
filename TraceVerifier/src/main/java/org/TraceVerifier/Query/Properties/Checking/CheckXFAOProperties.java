package org.TraceVerifier.Query.Properties.Checking;

import org.TraceVerifier.Query.BaseTraceQuery;
import org.TraceVerifier.Query.PassFailTraceQuery;
import org.TraceVerifier.Query.Properties.PropUtil;

public class CheckXFAOProperties {
    public static BaseTraceQuery addressStartGEQ(int value, int allocatorID)
    {
        String heapReqRange = PropUtil.deriveHeapRequestRange(allocatorID);
        String startAddressGEQ = heapReqRange + ".start_address >= " + value;
        String queryString = "A[] " + PropUtil.imply(PropUtil.whenTraceProcessed(allocatorID), startAddressGEQ);
        return new PassFailTraceQuery(queryString, "Verifies whether an address matches.", "Address Start GEQ" + allocatorID);
    }
}
