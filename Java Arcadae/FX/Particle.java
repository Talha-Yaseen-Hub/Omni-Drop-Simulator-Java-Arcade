package com.spawnsaver.fx;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * A single physics-driven particle used for catch bursts, hit sparks and
 * power-up sparkles. Lifetime is measured in frames (not milliseconds) so
 * it stays perfectly in sync with the game's fixed-timestep loop.
 */
public class Particle {

    private double x, y;
    private double vx, vy;
    private int life;
    private final int maxLife;
    private final float size;
    private final Color color;

    public Particle(double x, double y, double vx, double vy, int lifeFrames, float size, Color color) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.maxLife = Math.max(1, lifeFrames);
        this.life = this.maxLife;
        this.size = size;
        this.color = color;
    }

    public void update() {
        x += vx;
        y += vy;
        vy += 0.12;   // gentle gravity
        vx *= 0.98;   // gentle drag
        life--;
    }

    public boolean isDead() {
        return life <= 0;
    }

    public void render(Graphics2D g2) {
        float alpha = Math.max(0f, Math.min(1f, life / (float) maxLife));
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)));
        float s = size * (0.4f + 0.6f * alpha);
        g2.fillOval((int) (x - s / 2), (int) (y - s / 2), (int) s, (int) s);
    }
}
