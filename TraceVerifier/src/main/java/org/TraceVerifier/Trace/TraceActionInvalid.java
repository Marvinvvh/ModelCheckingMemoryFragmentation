package org.TraceVerifier.Trace;

/**
 * Invalid action, used only for testing purposes for validation.
 */
public class TraceActionInvalid extends TraceAction {
    public TraceActionInvalid(int pointer, int size) {
        super(Type.eInvalidAction, pointer, size);
    }
}
