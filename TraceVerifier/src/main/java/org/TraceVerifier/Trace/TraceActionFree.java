package org.TraceVerifier.Trace;

/**
 * The allocation trace action.
 */
public class TraceActionFree extends TraceAction {
    public TraceActionFree(int pointer) {
        super(Type.eFree, pointer);
    }
}
