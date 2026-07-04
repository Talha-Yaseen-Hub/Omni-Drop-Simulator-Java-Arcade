package com.spawnsaver.fx;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Owns and updates every active Particle. Callers just ask for a burst
 * of particles at a point; this class handles storage, aging and cleanup.
 */
public class ParticleSystem {

    private final List<Particle> particles = new ArrayList<>();
    private final Random random = new Random();

    public void burst(double x, double y, Color color, int count) {
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double speed = 1.5 + random.nextDouble() * 3.5;
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            int life = 24 + random.nextInt(20);
            float size = 4f + random.nextFloat() * 5f;
            particles.add(new Particle(x, y, vx, vy, life, size, color));
        }
    }

    public void update() {
        particles.removeIf(p -> {
            p.update();
            return p.isDead();
        });
    }

    public void render(Graphics2D g2) {
        for (Particle p : particles) {
            p.render(g2);
        }
    }

    public void clear() {
        particles.clear();
    }
}
