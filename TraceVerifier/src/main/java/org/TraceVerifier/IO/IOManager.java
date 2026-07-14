package org.TraceVerifier.IO;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface for a manager that handles importing/exporting certain types out outputs/inputs.
 */
public interface IOManager {
    public <T> T Import(Path filepath, Class<T> objectType) throws IOException;

    public <T> void Export(Path filepath, String filename, T object, boolean overwrite) throws IOException;
}
