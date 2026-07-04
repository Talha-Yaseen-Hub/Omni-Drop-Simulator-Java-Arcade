package com.spawnsaver;

/**
 * The finite set of states the game can be in.
 * The GamePanel reads this each frame to decide what to update and draw.
 */
public enum GameState {
    MENU,
    PLAYING,
    PAUSED,
    GAME_OVER
}
