package org.TraceVerifier.Trace;

/**
 * The allocation trace action.
 */
public class TraceActionAlloc extends TraceAction {
    public TraceActionAlloc(int pointer, int size) {
        super(Type.eAlloc, pointer, size);
    }
}
