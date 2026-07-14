package org.TraceVerifier.Verification.Queries;

import org.TraceVerifier.Model.ModelType;
import org.TraceVerifier.Query.PassFailTraceQuery;
import org.TraceVerifier.Query.TraceQueryCollection;

/**
 * Class that contains all Constant-related queries.
 */
public class VerificationQueriesConstants {

    public static TraceQueryCollection SizeMultiple(int sizeMultiple)
    {
        String identifier = "SizeMultiple";
        String comment = """
                Checks whether size multiple is the expected value.
                """;
        String query = "A[] SIZE_MULTIPLE == "+sizeMultiple;

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection PageSize(int pageSize)
    {
        String identifier = "PageSize";
        String comment = """
                Checks whether page size is the expected value.
                """;
        String query = "A[] PAGE_SIZE == "+pageSize;

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection AmtOfPages(int amtOfPages)
    {
        String identifier = "AmtOfPages";
        String comment = """
                Checks whether the model holds the expected amount of pages..
                """;
        String query = "A[] AMT_OF_PAGES_HEAP == "+amtOfPages;

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection AllocatorCount()
    {
        String identifier = "AllocatorCount";
        String comment = """
                Checks whether there's the expected among of allocators in total..
                """;
        String query = "A[] ALLOCATOR_COUNT == 1";

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }

    public static TraceQueryCollection SingleAllocator(ModelType modelType)
    {
        String FFAO = "FFAO_ALLOCATOR_COUNT == " + (modelType == ModelType.FFAO ? 1 : 0);
        String BFAO = "BFAO_ALLOCATOR_COUNT == " + (modelType == ModelType.BFAO ? 1 : 0);
        String NFAO = "NFAO_ALLOCATOR_COUNT == " + (modelType == ModelType.NFAO ? 1 : 0);

        String identifier = "SingleAllocator";
        String comment = """
                Checks whether there's only a single allocator.
                """;
        String query = "A[] "+FFAO+" && "+BFAO+" && "+NFAO;

        TraceQueryCollection tqc = new TraceQueryCollection(identifier);
        tqc.add(new PassFailTraceQuery(query, comment, identifier));
        return tqc;
    }
}
