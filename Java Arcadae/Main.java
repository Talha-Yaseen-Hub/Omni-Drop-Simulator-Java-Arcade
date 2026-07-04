package com.spawnsaver;

import javax.swing.SwingUtilities;

/**
 * Entry point. Launches the game window on Swing's event dispatch
 * thread, which is the correct/safe way to start any Swing UI.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameWindow::new);
    }
}
