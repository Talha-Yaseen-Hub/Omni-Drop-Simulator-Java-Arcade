package com.spawnsaver;

import javax.swing.JFrame;

/**
 * The application window. Kept intentionally tiny -- all game logic
 * lives in GamePanel, this class only wires it into a JFrame.
 */
public class GameWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    public GameWindow() {
        super(Constants.GAME_TITLE);

        GamePanel gamePanel = new GamePanel();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        gamePanel.requestFocusInWindow();
    }
}
