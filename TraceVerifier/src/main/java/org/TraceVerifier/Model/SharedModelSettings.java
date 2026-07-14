package org.TraceVerifier.Model;

/**
 * Settings that are shared among all models.
 */
public interface SharedModelSettings {
    int getAmountOfPages();

    int getAddressAlignmentAllocator();

    int getSizeMultiple();

    int getAllocatorCount();

    int getFreeListReductionFactor();

    int getHeapSize();

    int getPageSize();
}
