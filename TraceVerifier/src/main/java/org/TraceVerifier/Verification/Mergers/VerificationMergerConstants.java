package org.TraceVerifier.Verification.Mergers;

import org.TraceVerifier.Model.ModelConfiguration;
import org.TraceVerifier.Model.ModelMerger;
import org.TraceVerifier.Query.TraceQueryCollection;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Verification.Queries.VerificationQueriesConstants;
import org.TraceVerifier.Verification.Traces.VerificationTracesShared;
import org.TraceVerifier.Verification.VerificationItem;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that contains constant-related verification items.
 */
public class VerificationMergerConstants extends VerificationMerger {
    public VerificationMergerConstants(Path baseModelPath, ModelConfiguration config, boolean clearExistingQueries) {
        super(baseModelPath, config, clearExistingQueries);
    }

    /**
     * Check whether the size multiple has the expected value.
     */
    public List<VerificationItem> SizeMultiple() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = VerificationQueriesConstants.SizeMultiple(config.getSizeMultiple());
        trace = VerificationTracesShared.generateStandardizedTrace("SizeMultiple", config);
        merger = setupMerger("SizeMultiple", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether the page size has the expected value.
     */
    public List<VerificationItem> PageSize() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = VerificationQueriesConstants.PageSize(config.getPageSize());
        trace = VerificationTracesShared.generateStandardizedTrace("PageSize", config);
        merger = setupMerger("PageSize", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether the number of pages has the expected value.
     */
    public List<VerificationItem> AmtOfPages() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = VerificationQueriesConstants.AmtOfPages(config.getAmountOfPages());
        trace = VerificationTracesShared.generateStandardizedTrace("AmtOfPages", config);
        merger = setupMerger("AmtOfPages", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether the allocator count has the expected value.
     */
    public List<VerificationItem> AllocatorCount() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = VerificationQueriesConstants.AllocatorCount();
        trace = VerificationTracesShared.generateStandardizedTrace("AllocatorCount", config);
        merger = setupMerger("AllocatorCount", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    /**
     * Check whether there is only a single allocator.
     */
    public List<VerificationItem> SingleAllocator() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        Trace trace;
        ModelMerger merger;
        TraceQueryCollection tq;

        tq = VerificationQueriesConstants.SingleAllocator(config.getModelType());
        trace = VerificationTracesShared.generateStandardizedTrace("SingleAllocator", config);
        merger = setupMerger("SingleAllocator", trace, tq);
        items.add(new VerificationItem(merger, trace, tq));

        return items;
    }

    @Override
    public List<VerificationItem> mergeAll() throws IOException
    {
        List<VerificationItem> items = new ArrayList<>();
        items.addAll(SizeMultiple());
        items.addAll(AmtOfPages());
        items.addAll(AllocatorCount());
        items.addAll(SingleAllocator());
        items.addAll(PageSize());
        return items;
    }
}
