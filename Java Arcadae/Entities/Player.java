package com.spawnsaver.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * The player-controlled "Guardian" basket that catches falling Spawns.
 * Owns its own small animation state (catch bounce, flash) plus the
 * two power-up visuals (shield ring, magnet aura) that read as on/off
 * flags set by GamePanel.
 */
public class Player {

    private double x;
    private final double y;
    private final int width;
    private final int height;

    private double bounce;
    private double catchFlash;
    private boolean shieldActive;
    private boolean magnetActive;

    public Player(double startX, double y, int width, int height) {
        this.x = startX;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void moveBy(double dx, int minX, int maxX) {
        x += dx;
        clampToBounds(minX, maxX);
    }

    public void setCenterX(double centerX, int minX, int maxX) {
        x = centerX - width / 2.0;
        clampToBounds(minX, maxX);
    }

    private void clampToBounds(int minX, int maxX) {
        if (x < minX) x = minX;
        if (x > maxX - width) x = maxX - width;
    }

    public void update() {
        bounce *= 0.9;
        if (catchFlash > 0) {
            catchFlash -= 0.05;
        }
    }

    public void triggerCatchBounce() {
        bounce = 6;
        catchFlash = 1.0;
    }

    public void setShieldActive(boolean v) {
        shieldActive = v;
    }

    public boolean isShieldActive() {
        return shieldActive;
    }

    public void setMagnetActive(boolean v) {
        magnetActive = v;
    }

    public boolean isMagnetActive() {
        return magnetActive;
    }

    public double getCenterX() {
        return x + width / 2.0;
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double(x + 6, y + bounce, width - 12, height - 10);
    }

    public void render(Graphics2D g2) {
        int drawY = (int) (y - bounce);
        int drawX = (int) x;

        if (shieldActive) {
            g2.setStroke(new BasicStroke(3f));
            g2.setColor(new Color(90, 200, 255, 160));
            g2.drawOval(drawX - 10, drawY - 10, width + 20, height + 20);
            g2.setColor(new Color(90, 200, 255, 40));
            g2.fillOval(drawX - 10, drawY - 10, width + 20, height + 20);
        }

        if (magnetActive) {
            g2.setColor(new Color(255, 190, 90, 80));
            g2.fillOval(drawX - 30, drawY - 30, width + 60, height + 60);
        }

        // Ground shadow
        g2.setColor(new Color(0, 0, 0, 70));
        g2.fillOval(drawX + 6, drawY + height - 6, width - 12, 14);

        // Basket body (trapezoid) with a vertical gradient
        GradientPaint bodyPaint = new GradientPaint(
                drawX, drawY, new Color(255, 210, 110),
                drawX, drawY + height, new Color(200, 130, 40));
        g2.setPaint(bodyPaint);
        int[] bx = {drawX + 8, drawX + width - 8, drawX + width - 18, drawX + 18};
        int[] by = {drawY + 18, drawY + 18, drawY + height - 8, drawY + height - 8};
        g2.fillPolygon(bx, by, 4);

        // Basket weave lines
        g2.setColor(new Color(150, 90, 30, 160));
        g2.setStroke(new BasicStroke(2f));
        for (int i = 1; i < 4; i++) {
            int lx = drawX + 8 + i * (width - 16) / 4;
            g2.drawLine(lx, drawY + 20, lx - 4, drawY + height - 10);
        }

        // Rim
        g2.setColor(new Color(255, 235, 180));
        g2.fillRoundRect(drawX, drawY, width, 20, 16, 16);
        g2.setColor(new Color(180, 110, 40));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(drawX, drawY, width, 20, 16, 16);

        // Cute guardian face on the rim
        g2.setColor(new Color(60, 40, 20));
        int faceCX = drawX + width / 2;
        g2.fillOval(faceCX - 10, drawY + 4, 5, 5);
        g2.fillOval(faceCX + 5, drawY + 4, 5, 5);
        g2.setStroke(new BasicStroke(2f));
        g2.drawArc(faceCX - 7, drawY + 6, 14, 8, 200, 140);

        // Flash when a catch just happened
        if (catchFlash > 0) {
            g2.setColor(new Color(255, 255, 255, (int) (catchFlash * 120)));
            g2.fillRoundRect(drawX - 4, drawY - 4, width + 8, height + 8, 20, 20);
        }
    }
}
