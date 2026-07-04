package com.spawnsaver.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;

/**
 * A rotating, glowing star that grants a temporary bonus effect when
 * caught. EXTRA_LIFE is intentionally rarer than the others.
 */
public class PowerUp extends FallingObject {

    public enum Type {
        SHIELD("Shield", new Color(90, 200, 255)),
        SLOW_TIME("Slow Time", new Color(150, 230, 160)),
        MAGNET("Magnet", new Color(255, 190, 90)),
        MULTIPLIER("2x Score", new Color(255, 120, 200)),
        EXTRA_LIFE("Extra Life", new Color(255, 90, 110));

        public final String label;
        public final Color color;

        Type(String label, Color color) {
            this.label = label;
            this.color = color;
        }
    }

    private final Type type;
    private double rotation;

    public PowerUp(double x, double y, double vy, Type type) {
        super(x, y, vy, 20);
        this.type = type;
    }

    /** EXTRA_LIFE is rare (8%); the rest are evenly split the remaining 92%. */
    public static Type rollType() {
        double r = Math.random() * 100;
        if (r < 8) return Type.EXTRA_LIFE;
        Type[] regular = {Type.SHIELD, Type.SLOW_TIME, Type.MAGNET, Type.MULTIPLIER};
        int index = (int) (Math.random() * regular.length);
        return regular[index];
    }

    public Type getType() {
        return type;
    }

    @Override
    public void update(double timeScale) {
        super.update(timeScale);
        rotation += 0.06 * timeScale;
    }

    @Override
    public void render(Graphics2D g2) {
        int r = radius;
        int cx = (int) x;
        int cy = (int) y;

        RadialGradientPaint glow = new RadialGradientPaint(
                new Point(cx, cy), r * 2f,
                new float[]{0f, 1f},
                new Color[]{
                        new Color(type.color.getRed(), type.color.getGreen(), type.color.getBlue(), 130),
                        new Color(type.color.getRed(), type.color.getGreen(), type.color.getBlue(), 0)
                });
        g2.setPaint(glow);
        g2.fillOval(cx - r * 2, cy - r * 2, r * 4, r * 4);

        Graphics2D g2d = (Graphics2D) g2.create();
        g2d.translate(cx, cy);
        g2d.rotate(rotation);
        Polygon star = buildStar(r, r / 2, 5);
        g2d.setColor(type.color);
        g2d.fillPolygon(star);
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawPolygon(star);
        g2d.dispose();
    }

    private Polygon buildStar(int outerR, int innerR, int points) {
        Polygon p = new Polygon();
        for (int i = 0; i < points * 2; i++) {
            double angle = Math.PI * i / points - Math.PI / 2;
            int rad = (i % 2 == 0) ? outerR : innerR;
            p.addPoint((int) (Math.cos(angle) * rad), (int) (Math.sin(angle) * rad));
        }
        return p;
    }
}
