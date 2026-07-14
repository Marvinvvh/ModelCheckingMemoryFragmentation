package org.TraceVerifier.Model.FFAO;

import com.google.gson.annotations.Expose;
import org.TraceVerifier.Model.ModelVariables;
import org.TraceVerifier.Trace.Trace;

import java.text.MessageFormat;

/**
 * FFAO model variables which change depending on the trace.
 */
public class FFAOModelVariables implements ModelVariables {
    @Expose
    private int traceActionCount = 0;
    @Expose
    private String identifier;

    public FFAOModelVariables() {
        this.identifier = "FFAO";
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void processTrace(Trace trace) {
        this.traceActionCount = trace.getTraceActionCount();
    }

    public String deriveGlobalVariables() {
        return "\n"
                + MessageFormat.format("const int32_t ALLOC_ACTION_COUNT = {0,number,#};\n", traceActionCount);
    }
}
