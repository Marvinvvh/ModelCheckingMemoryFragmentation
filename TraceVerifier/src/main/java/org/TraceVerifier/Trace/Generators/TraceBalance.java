package org.TraceVerifier.Trace.Generators;

public enum TraceBalance {
    Balanced, // #alloc = #free
    Unbalanced, // not guaranteed that alloc = free
}
