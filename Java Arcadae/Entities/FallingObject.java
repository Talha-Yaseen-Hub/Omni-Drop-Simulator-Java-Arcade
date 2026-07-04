package com.spawnsaver.entities;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

/**
 * Base type for anything that falls from the top of the screen:
 * Spawns (catch these), Voidlings (avoid these) and PowerUps (bonus).
 * Handles shared physics (gentle side-to-side wobble + falling) and
 * collision bounds; subclasses only need to implement their own look.
 */
public abstract class FallingObject {

    protected double x;
    protected double y;
    protected double vy;
    protected double vx;
    protected final int radius;
    protected double wobblePhase;

    protected FallingObject(double x, double y, double vy, int radius) {
        this.x = x;
        this.y = y;
        this.vy = vy;
        this.radius = radius;
        this.vx = 0;
        this.wobblePhase = Math.random() * Math.PI * 2;
    }

    /**
     * Advances position and animation. timeScale is 1.0 normally and
     * less than 1.0 while the Slow Time power-up is active, so every
     * falling object (not just new ones) responds to it uniformly.
     */
    public void update(double timeScale) {
        wobblePhase += 0.05 * timeScale;
        x += (Math.sin(wobblePhase) * 0.6 + vx) * timeScale;
        y += vy * timeScale;
    }

    public abstract void render(Graphics2D g2);

    public Shape getBounds() {
        return new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2);
    }

    public void nudgeX(double dx) {
        this.x += dx;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getRadius() {
        return radius;
    }

    public boolean isBelow(int groundY) {
        return y - radius > groundY;
    }
}
