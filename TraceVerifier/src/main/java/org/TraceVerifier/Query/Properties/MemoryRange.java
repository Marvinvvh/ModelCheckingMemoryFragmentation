package org.TraceVerifier.Query.Properties;

public record MemoryRange(int startAddress, int endAddress) {
    public int nextAddress(){return endAddress + 1;}
}
