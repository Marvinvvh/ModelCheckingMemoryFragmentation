package org.TraceVerifier.Model;

import com.google.gson.annotations.Expose;

import java.text.MessageFormat;

/**
 * Retrieve the allocator configuration for a derived model.
 */
public abstract class AllocatorConfiguration implements ModelConfiguration {
    @Expose
    private String identifier;
    @Expose
    private int pageSize = 0;
    @Expose
    private int amountOfPages = 0;
    @Expose
    private int addressAlignmentAllocator = 0;
    @Expose
    private int sizeMultipleAllocator = 0;
    @Expose
    private int allocatorCount = 1;
    @Expose
    private int freeListReductionFactor = 1;

    public AllocatorConfiguration(String identifier) {
        this.identifier = identifier;
    }

    protected void refreshIdentifier() {
        this.identifier = generateIdentifier();
    }

    protected abstract String generateIdentifier();

    public int getHeapSize() {
        return getPageSize() * getAmountOfPages();
    }

    public void setSizeMultipleAllocator(int sizeMultipleAllocator) {
        this.sizeMultipleAllocator = sizeMultipleAllocator;
        refreshIdentifier();
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getAmountOfPages() {
        return amountOfPages;
    }

    public void setAmountOfPages(int amountOfPages) {
        this.amountOfPages = amountOfPages;
    }

    public int getAddressAlignmentAllocator() {
        return addressAlignmentAllocator;
    }

    public void setAddressAlignmentAllocator(int addressAlignmentAllocator) {
        this.addressAlignmentAllocator = addressAlignmentAllocator;
    }

    public int getSizeMultiple() {
        return sizeMultipleAllocator;
    }

    public int getAllocatorCount() {
        return allocatorCount;
    }

    public void setAllocatorCount(int allocatorCount) {
        this.allocatorCount = allocatorCount;
    }

    public int getFreeListReductionFactor() {
        return freeListReductionFactor;
    }

    public void setFreeListReductionFactor(int freeListReductionFactor) {
        this.freeListReductionFactor = freeListReductionFactor;
    }

    @Override
    public String generateGlobalConstants() {
        return "\n"
                + MessageFormat.format("const int32_t PAGE_SIZE = {0,number,#};\n", getPageSize())
                + MessageFormat.format("const int32_t AMT_OF_PAGES_HEAP = {0,number,#};\n", getAmountOfPages())
                + MessageFormat.format("const int32_t SIZE_MULTIPLE = {0,number,#};\n", getSizeMultiple())
                + MessageFormat.format("const int32_t ADDRESS_ALIGNMENT = {0,number,#};\n", getAddressAlignmentAllocator())
                + MessageFormat.format("const int32_t FREE_LIST_REDUCTION_FACTOR = {0,number,#};\n", getFreeListReductionFactor())
                ;
    }
}
