package com.spawnsaver.entities;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;

/**
 * The "bad" falling object -- a spiky shadow creature. Catching one costs
 * the player a life (unless shielded); letting it fall to the ground is
 * completely safe, since it was never a spawn worth saving.
 */
public class Voidling extends FallingObject {

    private double spikePhase;

    public Voidling(double x, double y, double vy) {
        super(x, y, vy, 24);
    }

    @Override
    public void update(double timeScale) {
        super.update(timeScale);
        spikePhase += 0.2 * timeScale;
    }

    @Override
    public void render(Graphics2D g2) {
        int r = radius;
        int cx = (int) x;
        int cy = (int) y;

        // Dark aura
        RadialGradientPaint glow = new RadialGradientPaint(
                new Point(cx, cy), r * 2.2f,
                new float[]{0f, 1f},
                new Color[]{new Color(200, 40, 60, 110), new Color(200, 40, 60, 0)});
        g2.setPaint(glow);
        int glowSize = (int) (r * 4.4);
        g2.fillOval(cx - glowSize / 2, cy - glowSize / 2, glowSize, glowSize);

        // Spiky silhouette
        Polygon spikes = new Polygon();
        int points = 10;
        for (int i = 0; i < points; i++) {
            double angle = spikePhase + (Math.PI * 2 * i) / points;
            double rad = (i % 2 == 0) ? r * 1.15 : r * 0.75;
            int px = cx + (int) (Math.cos(angle) * rad);
            int py = cy + (int) (Math.sin(angle) * rad);
            spikes.addPoint(px, py);
        }
        g2.setColor(new Color(35, 15, 30));
        g2.fillPolygon(spikes);

        // Core body
        GradientPaint bodyPaint = new GradientPaint(
                cx, cy - r, new Color(120, 20, 50),
                cx, cy + r, new Color(30, 10, 20));
        g2.setPaint(bodyPaint);
        int coreSize = (int) (r * 1.4);
        g2.fillOval(cx - coreSize / 2, cy - coreSize / 2, coreSize, coreSize);

        // Angry glowing eyes
        g2.setColor(new Color(255, 60, 60));
        g2.fillOval(cx - 8, cy - 4, 5, 5);
        g2.fillOval(cx + 3, cy - 4, 5, 5);
    }
}
