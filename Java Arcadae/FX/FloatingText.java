package com.spawnsaver.fx;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

/**
 * A short-lived piece of text that drifts upward and fades out.
 * Used for "+10", "-1 life" and similar feedback popups.
 */
public class FloatingText {

    private double x, y;
    private int life;
    private final int maxLife;
    private final String text;
    private final Color color;

    public FloatingText(double x, double y, String text, Color color, int lifeFrames) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = color;
        this.maxLife = Math.max(1, lifeFrames);
        this.life = this.maxLife;
    }

    public void update() {
        y -= 0.7;
        life--;
    }

    public boolean isDead() {
        return life <= 0;
    }

    public void render(Graphics2D g2) {
        float alpha = Math.max(0f, life / (float) maxLife);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, (int) (x - fm.stringWidth(text) / 2.0), (int) y);
    }
}
