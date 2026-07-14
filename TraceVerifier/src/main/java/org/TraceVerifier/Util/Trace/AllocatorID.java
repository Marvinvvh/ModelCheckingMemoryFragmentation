package org.TraceVerifier.Util.Trace;

/**
 * Class for retrieving an allocator id based on the number of allocators.
 */
public class AllocatorID {
    public static int getFFAOAllocatorID(int id) {
        return id;
    }

    public static int getNFAOAllocatorID(int id, int numFFAO) {
        return numFFAO + id;
    }

    public static int getBFAOAllocatorID(int id, int numFFAO, int numNFAO) {
        return numFFAO + numNFAO + id;
    }
}
