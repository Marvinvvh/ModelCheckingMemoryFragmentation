package org.TraceVerifier.IO;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Interface for importing/exporting objects from/to a location.
 */
public interface FileIO<T> {
    public T Import(Path filepath, Class<T> object) throws IOException;

    public void Export(Path filepath, String filename, T object, boolean overwrite) throws IOException;
}
