package com.spawnsaver;

import java.awt.Color;

/**
 * Central place for every tunable number and color in the game.
 * Change values here to re-balance difficulty or re-theme the visuals
 * without touching any game logic.
 */
public final class Constants {

    private Constants() {
        // Utility class, no instances.
    }

    // ---- Window ----
    public static final int WINDOW_WIDTH = 900;
    public static final int WINDOW_HEIGHT = 700;
    public static final String GAME_TITLE = "Spawn Saver Simulator";

    // ---- Game loop ----
    public static final int FPS = 60;
    public static final int FRAME_DELAY_MS = 1000 / FPS;

    // ---- Ground ----
    public static final int GROUND_HEIGHT = 60;
    public static final int GROUND_Y = WINDOW_HEIGHT - GROUND_HEIGHT;

    // ---- Player ----
    public static final int PLAYER_WIDTH = 90;
    public static final int PLAYER_HEIGHT = 70;
    public static final double PLAYER_SPEED = 9.0;
    public static final int PLAYER_Y = GROUND_Y - PLAYER_HEIGHT + 15;

    // ---- Lives ----
    public static final int STARTING_LIVES = 3;
    public static final int MAX_LIVES = 5;

    // ---- Difficulty scaling ----
    public static final long BASE_SPAWN_INTERVAL_MS = 1100;
    public static final long MIN_SPAWN_INTERVAL_MS = 380;
    public static final double BASE_FALL_SPEED = 2.6;
    public static final double MAX_FALL_SPEED = 7.5;
    public static final int POINTS_PER_LEVEL = 150;

    // ---- Falling object radii ----
    public static final int VOIDLING_RADIUS = 24;
    public static final int POWERUP_RADIUS = 20;

    // ---- Spawn odds out of 100 for each new falling object ----
    public static final int VOIDLING_CHANCE = 22;   // 0-21   -> Voidling (avoid)
    public static final int POWERUP_CHANCE = 6;     // 22-27  -> PowerUp
    // everything else (28-99) -> Spawn (catch these)

    // ---- Combo system ----
    public static final int COMBO_STEP = 5;
    public static final int MAX_COMBO_MULTIPLIER = 5;

    // ---- Power-up duration ----
    public static final long POWERUP_DURATION_MS = 6000;

    // ---- Colors: mystical night-sky theme ----
    public static final Color COLOR_BG_TOP_EARLY = new Color(48, 58, 128);
    public static final Color COLOR_BG_BOTTOM_EARLY = new Color(96, 74, 168);
    public static final Color COLOR_BG_TOP_LATE = new Color(10, 12, 40);
    public static final Color COLOR_BG_BOTTOM_LATE = new Color(40, 18, 70);

    public static final Color COLOR_GROUND = new Color(34, 24, 48);
    public static final Color COLOR_GROUND_LINE = new Color(120, 90, 200);

    public static final Color COLOR_TEXT = new Color(245, 245, 255);
    public static final Color COLOR_ACCENT = new Color(255, 205, 90);
    public static final Color COLOR_DANGER = new Color(235, 70, 90);
    public static final Color COLOR_SUCCESS = new Color(110, 220, 140);

    public static final String FONT_NAME = "Segoe UI";
}
