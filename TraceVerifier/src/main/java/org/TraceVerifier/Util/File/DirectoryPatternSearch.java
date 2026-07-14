package org.TraceVerifier.Util.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.stream.Stream;

/**
 * Class with utility for searching through a directory using patterns.
 */
public class DirectoryPatternSearch {
    /**
     * Searches through a directory for all files that match a pattern
     */
    public static List<Path> searchFiles(Path directory, String pattern) throws IOException {
        if (!Files.exists(directory)) {
            throw new FileNotFoundException("Directory does not exist.");
        }

        FileSystem fs = directory.getFileSystem();
        PathMatcher matcher = fs.getPathMatcher(pattern);
        try (Stream<Path> fileStream = Files.list(directory).filter(matcher::matches)) {
            return fileStream.toList();
        } catch (Exception e) {
            throw new RuntimeException("Invalid search pattern for matching files in directory.");
        }
    }
}
