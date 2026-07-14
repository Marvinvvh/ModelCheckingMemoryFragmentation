package org.TraceVerifier.Util.Trace;

import org.TraceVerifier.IO.IOModelManager;
import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Trace.TraceGroup;
import org.TraceVerifier.Trace.TraceGroupIdentifier;
import org.TraceVerifier.Util.File.DirectoryPatternSearch;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Class for retrieving all traces from a location.
 */
public class TraceImportUtil {
    /**
     * Retrieve all traces matching the pattern from a directory as a trace group.
     */
    public static TraceGroup getTraceGroupFromDir(Path traceGroupDir, String traceMatchPattern) throws IOException {
        if (!Files.isDirectory(traceGroupDir)) {
            throw new IOException("Invalid allocation trace directory");
        }

        String traceGroupName = traceGroupDir.toFile().getName();
        List<Path> matchingPaths = DirectoryPatternSearch.searchFiles(traceGroupDir, traceMatchPattern);
        TraceGroup traceGroup = new TraceGroup(new TraceGroupIdentifier(traceGroupName));
        for (Path path : matchingPaths) {
            Trace t = IOModelManager.getInstance().Import(path, Trace.class);
            traceGroup.addTrace(t);
        }

        return traceGroup;
    }
}
