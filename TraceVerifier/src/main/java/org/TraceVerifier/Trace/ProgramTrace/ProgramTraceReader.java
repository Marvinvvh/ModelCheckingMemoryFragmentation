package org.TraceVerifier.Trace.ProgramTrace;

import org.TraceVerifier.Trace.Trace;
import org.TraceVerifier.Trace.TraceGroup;
import org.TraceVerifier.Trace.TraceGroupIdentifier;

import java.io.IOException;
import java.nio.file.Path;

public interface ProgramTraceReader {
    Trace fromFile(Path filepath);

    TraceGroup fromDirectory(TraceGroupIdentifier groupIdentifier, Path dirpath, String matchingPattern) throws IOException;
}
