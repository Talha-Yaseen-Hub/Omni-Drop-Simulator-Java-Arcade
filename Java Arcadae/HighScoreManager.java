package com.spawnsaver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Persists the player's best score to a small text file in the user's
 * home directory so it survives between play sessions. All failures are
 * caught and handled gracefully -- a missing or unreadable file simply
 * means the high score starts at 0, it never crashes the game.
 */
public final class HighScoreManager {

    private static final String FILE_NAME = "spawn_saver_highscore.dat";
    private static final Path FILE_PATH = Paths.get(System.getProperty("user.home"), FILE_NAME);

    private HighScoreManager() {
        // Utility class, no instances.
    }

    public static int loadHighScore() {
        try {
            if (Files.exists(FILE_PATH)) {
                String content = new String(Files.readAllBytes(FILE_PATH)).trim();
                if (!content.isEmpty()) {
                    return Integer.parseInt(content);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Could not load high score, starting at 0: " + e.getMessage());
        }
        return 0;
    }

    public static void saveHighScore(int score) {
        try {
            Files.write(FILE_PATH, String.valueOf(score).getBytes());
        } catch (IOException e) {
            System.err.println("Could not save high score: " + e.getMessage());
        }
    }
}
