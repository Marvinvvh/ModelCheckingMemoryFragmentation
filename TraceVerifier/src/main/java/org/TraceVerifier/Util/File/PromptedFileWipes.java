package org.TraceVerifier.Util.File;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Class which prompts a user to delete a file or directory/directories.
 */
public class PromptedFileWipes {
    /**
     * Prompt to delete multiple directories with YES, NO, QUIT.
     * If YES, delete all files and directories in it.
     * If NO, skip.
     * If QUIT, exit program.
     */
    public static void YesNoQuitMultiDir(List<Path> directories, String additionalWarning) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("You are about to delete all files/dirs in the following directories: ");
        for (Path path : directories) {
            stringBuilder.append(path).append("\n");
        }

        stringBuilder.append("YES to wipe, NO to skip, QUIT to quit.\n");

        if (additionalWarning != null) {
            stringBuilder.append(additionalWarning);
        }

        // No lines, this should be handled in the sb.
        System.out.print(stringBuilder);

        PromptValues wipePrompt = InputPrompts.YesNoQuit();
        switch (wipePrompt) {
            case PromptValues.Yes -> {
                System.out.println("Deleting.");
                System.out.println("\n");
                for (var directory : directories) {
                    Files.walkFileTree(directory, new VisitorDirectoryWipe());
                }
            }
            case PromptValues.No -> {
                System.out.println("Deletion skipped.");
                System.out.println();
            }
            case PromptValues.Quit -> {
                System.out.println("Quitting program before deleting allocation traces.");
                System.out.println();
                System.exit(0);
            }
        }
    }

    /**
     * Prompt to delete a single file with YES, NO, QUIT.
     * If YES, delete all files and directories in it.
     * If NO, skip.
     * If QUIT, exit program.
     */
    public static void YesNoQuitSingleFile(Path file, String additionalWarning) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("You are about to delete the following file: ")
                .append(file.toString())
                .append("\n")
                .append("YES to wipe, NO to skip, QUIT to quit.\n");

        if (additionalWarning != null) {
            stringBuilder.append(additionalWarning);
        }

        // No lines, this should be handled in the sb.
        System.out.print(stringBuilder);

        PromptValues wipePrompt = InputPrompts.YesNoQuit();
        switch (wipePrompt) {
            case PromptValues.Yes -> {
                System.out.println("Deleting.");
                System.out.println();
                File f = new File(file.toString());

                if(!f.exists())
                {
                    throw new IOException("Attempting to delete a non-existent file. Verify the input.");
                }

                if(!f.delete())
                {
                    throw new IOException("Failed to delete the file.");
                }
            }
            case PromptValues.No -> {
                System.out.println("Deletion skipped.");
                System.out.println();
            }
            case PromptValues.Quit -> {
                System.out.println("Quitting program before deleting allocation traces.");
                System.out.println();
                System.exit(0);
            }
        }
    }
    /**
     * Prompt to delete a single directory with YES, NO, QUIT.
     * If YES, delete all files and directories in it.
     * If NO, skip.
     * If QUIT, exit program.
     */
    public static void YesNoQuitSingleDir(Path directory, String additionalWarning) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("You are about to delete all files/dirs in directory: ")
                .append(directory.toString())
                .append("\n")
                .append("YES to wipe, NO to skip, QUIT to quit.\n");

        if (additionalWarning != null) {
            stringBuilder.append(additionalWarning);
        }

        // No lines, this should be handled in the sb.
        System.out.print(stringBuilder);

        PromptValues wipePrompt = InputPrompts.YesNoQuit();
        switch (wipePrompt) {
            case PromptValues.Yes -> {
                System.out.println("Deleting.");
                System.out.println();
                Files.walkFileTree(directory, new VisitorDirectoryWipe());
            }
            case PromptValues.No -> {
                System.out.println("Deletion skipped.");
                System.out.println();
            }
            case PromptValues.Quit -> {
                System.out.println("Quitting program before deleting allocation traces.");
                System.out.println();
                System.exit(0);
            }
        }
    }
}
