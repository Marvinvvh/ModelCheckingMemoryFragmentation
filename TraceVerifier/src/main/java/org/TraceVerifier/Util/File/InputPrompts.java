package org.TraceVerifier.Util.File;

import java.util.Scanner;

/**
 * Class for creating prompts for user interaction.
 */
public class InputPrompts {

    /**
     * Provides a prompt of YES, NO, QUIT. Waits until input.
     * Returning a matching prompt value.
     */
    public static PromptValues YesNoQuit() {
        Scanner userInput = new Scanner(System.in);
        PromptValues wipePrompt = PromptValues.None;
        boolean waitingForInput = true;
        while (waitingForInput) {
            String line = userInput.nextLine();
            switch (line) {
                case "YES" -> {
                    wipePrompt = PromptValues.Yes;
                    waitingForInput = false;
                }
                case "NO" -> {
                    wipePrompt = PromptValues.No;
                    waitingForInput = false;
                }
                case "QUIT" -> {
                    wipePrompt = PromptValues.Quit;
                }
                default -> {
                    System.out.println("YES, NO, or QUIT.");
                }
            }
        }
        return wipePrompt;
    }

}
