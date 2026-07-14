package org.TraceVerifier.Query;

/**
 * Property that's being tested. Has a string which contains the test (queryString), and comment it is annotated with (queryComment).
 */
public record TraceProperty(String queryString, String queryComment) {
}
