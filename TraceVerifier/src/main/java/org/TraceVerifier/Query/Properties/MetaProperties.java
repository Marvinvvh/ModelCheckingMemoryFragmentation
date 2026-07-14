package org.TraceVerifier.Query.Properties;

import org.TraceVerifier.Query.BaseTraceQuery;
import org.TraceVerifier.Query.PassFailTraceQuery;

public class MetaProperties {
    public static BaseTraceQuery checkHasDeadlock() {
        return new PassFailTraceQuery("E<> deadlock", "Check any deadlock.", "any_deadlock");
    }
}
