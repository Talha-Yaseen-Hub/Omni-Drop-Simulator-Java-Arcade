package com.spawnsaver.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RadialGradientPaint;

/**
 * The "good" falling object -- a newly hatched creature that must be
 * caught before it hits the ground. Comes in three rarities, each worth
 * a different amount of points and drawn in a different color.
 */
public class Spawn extends FallingObject {

    public enum Rarity {
        COMMON(10, new Color(120, 220, 150), new Color(70, 170, 110)),
        RARE(25, new Color(90, 180, 250), new Color(50, 120, 210)),
        EPIC(50, new Color(230, 150, 250), new Color(170, 90, 220));

        public final int points;
        public final Color bodyColor;
        public final Color darkColor;

        Rarity(int points, Color bodyColor, Color darkColor) {
            this.points = points;
            this.bodyColor = bodyColor;
            this.darkColor = darkColor;
        }
    }

    private final Rarity rarity;
    private double bobPhase;

    public Spawn(double x, double y, double vy, Rarity rarity) {
        super(x, y, vy, rarity == Rarity.EPIC ? 26 : (rarity == Rarity.RARE ? 24 : 22));
        this.rarity = rarity;
        this.bobPhase = Math.random() * Math.PI * 2;
    }

    /** Weighted random rarity roll: mostly common, epic is rare. */
    public static Rarity rollRarity() {
        double r = Math.random() * 100;
        if (r < 65) return Rarity.COMMON;
        if (r < 92) return Rarity.RARE;
        return Rarity.EPIC;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public int getPoints() {
        return rarity.points;
    }

    @Override
    public void update(double timeScale) {
        super.update(timeScale);
        bobPhase += 0.15 * timeScale;
    }

    @Override
    public void render(Graphics2D g2) {
        int r = radius;
        int cx = (int) x;
        int cy = (int) (y + Math.sin(bobPhase) * 1.5);

        // Soft outer glow
        RadialGradientPaint glow = new RadialGradientPaint(
                new Point(cx, cy), r * 2f,
                new float[]{0f, 1f},
                new Color[]{
                        new Color(rarity.bodyColor.getRed(), rarity.bodyColor.getGreen(), rarity.bodyColor.getBlue(), 90),
                        new Color(rarity.bodyColor.getRed(), rarity.bodyColor.getGreen(), rarity.bodyColor.getBlue(), 0)
                });
        g2.setPaint(glow);
        g2.fillOval(cx - r * 2, cy - r * 2, r * 4, r * 4);

        // Egg-shaped body with a vertical gradient
        GradientPaint bodyPaint = new GradientPaint(
                cx, cy - r, rarity.bodyColor,
                cx, cy + r, rarity.darkColor);
        g2.setPaint(bodyPaint);
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);

        // Shine highlight
        g2.setColor(new Color(255, 255, 255, 140));
        g2.fillOval((int) (cx - r * 0.4), (int) (cy - r * 0.6), (int) (r * 0.5), (int) (r * 0.35));

        // Cute face
        g2.setColor(new Color(40, 30, 40));
        int eyeOffset = r / 3;
        g2.fillOval(cx - eyeOffset - 2, cy - 2, 4, 4);
        g2.fillOval(cx + eyeOffset - 2, cy - 2, 4, 4);

        // Rarity ring for RARE and EPIC
        if (rarity != Rarity.COMMON) {
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(new Color(255, 255, 255, 160));
            g2.drawOval(cx - r - 3, cy - r - 3, r * 2 + 6, r * 2 + 6);
        }
    }
}
