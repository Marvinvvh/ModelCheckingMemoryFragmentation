package org.TraceVerifier.Query;

/**
 * Something that can either pass or fail somehow.
 */
public interface Passable {
    public boolean hasResult();

    public boolean hasExpectedResult();

    public void setExpectedResult(boolean expectedToPass);

    public boolean isExpectedToPass();
    public boolean isExpectedToCrash();
}
