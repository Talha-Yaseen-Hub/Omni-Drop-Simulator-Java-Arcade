package com.spawnsaver;

import com.spawnsaver.entities.FallingObject;
import com.spawnsaver.entities.Player;
import com.spawnsaver.entities.PowerUp;
import com.spawnsaver.entities.Spawn;
import com.spawnsaver.entities.Voidling;
import com.spawnsaver.fx.FloatingText;
import com.spawnsaver.fx.ParticleSystem;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * The heart of the game: owns the fixed-timestep loop (a 60 FPS Swing
 * Timer), reads input, updates every entity, resolves collisions and
 * renders every frame. Everything is driven from a single GameState so
 * there is exactly one code path active at a time -- no overlapping
 * update logic between menu / playing / paused / game-over.
 */
public class GamePanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 1L;

    private final transient Timer timer;
    private final transient Random random = new Random();

    private GameState state = GameState.MENU;

    private final transient Player player;
    private final transient List<FallingObject> fallingObjects = new ArrayList<>();
    private final transient ParticleSystem particles = new ParticleSystem();
    private final transient List<FloatingText> floatingTexts = new ArrayList<>();

    private final transient Set<Integer> pressedKeys = new HashSet<>();

    // ---- Run stats ----
    private int score;
    private int lives;
    private int level;
    private int comboCount;
    private int comboMultiplier;
    private int highScore;

    // ---- Spawning / difficulty ----
    private long lastSpawnTime;
    private long spawnIntervalMs = Constants.BASE_SPAWN_INTERVAL_MS;
    private double fallSpeed = Constants.BASE_FALL_SPEED;

    // ---- Power-up timers (0 == inactive) ----
    private long shieldEndTime;
    private long slowTimeEndTime;
    private long magnetEndTime;
    private long multiplierEndTime;

    // ---- Screen shake ----
    private double screenShake;

    // ---- Background stars: {x, y, phase} ----
    private final double[][] stars = new double[60][3];

    public GamePanel() {
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setFocusable(true);
        setDoubleBuffered(true);

        player = new Player(
                (Constants.WINDOW_WIDTH - Constants.PLAYER_WIDTH) / 2.0,
                Constants.PLAYER_Y,
                Constants.PLAYER_WIDTH,
                Constants.PLAYER_HEIGHT);

        highScore = HighScoreManager.loadHighScore();

        initStars();
        setupInput();

        timer = new Timer(Constants.FRAME_DELAY_MS, this);
        timer.start();
    }

    // =========================================================
    //  INPUT
    // =========================================================

    private void setupInput() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
                handleActionKeys(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (state == GameState.PLAYING) {
                    player.setCenterX(e.getX(), 0, Constants.WINDOW_WIDTH);
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
                if (state == GameState.MENU || state == GameState.GAME_OVER) {
                    startNewGame();
                }
            }
        });
    }

    private void handleActionKeys(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_ENTER:
                if (state == GameState.MENU || state == GameState.GAME_OVER) {
                    startNewGame();
                }
                break;
            case KeyEvent.VK_P:
            case KeyEvent.VK_ESCAPE:
                if (state == GameState.PLAYING) {
                    state = GameState.PAUSED;
                } else if (state == GameState.PAUSED) {
                    state = GameState.PLAYING;
                }
                break;
            default:
                break;
        }
    }

    private void handleMovement() {
        double dx = 0;
        if (pressedKeys.contains(KeyEvent.VK_LEFT) || pressedKeys.contains(KeyEvent.VK_A)) {
            dx -= Constants.PLAYER_SPEED;
        }
        if (pressedKeys.contains(KeyEvent.VK_RIGHT) || pressedKeys.contains(KeyEvent.VK_D)) {
            dx += Constants.PLAYER_SPEED;
        }
        if (dx != 0) {
            player.moveBy(dx, 0, Constants.WINDOW_WIDTH);
        }
    }

    // =========================================================
    //  GAME LOOP
    // =========================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        if (state == GameState.PLAYING) {
            update();
        }
        repaint();
    }

    private void update() {
        long now = System.currentTimeMillis();

        handleMovement();

        if (now - lastSpawnTime >= spawnIntervalMs) {
            spawnFallingObject(now);
            lastSpawnTime = now;
        }

        double timeScale = (now < slowTimeEndTime) ? 0.45 : 1.0;
        updateFallingObjects(now, timeScale);
        updateFloatingTexts();
        particles.update();
        player.update();
        updatePowerUpFlags(now);
        updateDifficulty();

        if (screenShake > 0) {
            screenShake *= 0.85;
            if (screenShake < 0.05) {
                screenShake = 0;
            }
        }
    }

    private void spawnFallingObject(long now) {
        double x = 40 + random.nextDouble() * (Constants.WINDOW_WIDTH - 80);
        double speed = fallSpeed * (0.85 + random.nextDouble() * 0.3);

        int roll = random.nextInt(100);
        if (roll < Constants.VOIDLING_CHANCE) {
            fallingObjects.add(new Voidling(x, -30, speed * 1.05));
        } else if (roll < Constants.VOIDLING_CHANCE + Constants.POWERUP_CHANCE) {
            fallingObjects.add(new PowerUp(x, -30, speed * 0.9, PowerUp.rollType()));
        } else {
            fallingObjects.add(new Spawn(x, -30, speed, Spawn.rollRarity()));
        }
    }

    private void updateFallingObjects(long now, double timeScale) {
        Rectangle2D playerBounds = player.getBounds();
        Iterator<FallingObject> it = fallingObjects.iterator();

        while (it.hasNext()) {
            FallingObject obj = it.next();

            if (now < magnetEndTime && obj instanceof Spawn) {
                double diff = player.getCenterX() - obj.getX();
                obj.nudgeX(diff * 0.03);
            }

            obj.update(timeScale);

            if (obj.getBounds().intersects(playerBounds)) {
                handleCollision(obj, now);
                it.remove();
                continue;
            }

            if (obj.isBelow(Constants.GROUND_Y)) {
                handleGroundHit(obj);
                it.remove();
            }
        }
    }

    private void updateFloatingTexts() {
        Iterator<FloatingText> it = floatingTexts.iterator();
        while (it.hasNext()) {
            FloatingText ft = it.next();
            ft.update();
            if (ft.isDead()) {
                it.remove();
            }
        }
    }

    private void updatePowerUpFlags(long now) {
        if (player.isShieldActive() && now >= shieldEndTime) {
            player.setShieldActive(false);
        }
        if (player.isMagnetActive() && now >= magnetEndTime) {
            player.setMagnetActive(false);
        }
    }

    private void updateDifficulty() {
        level = 1 + score / Constants.POINTS_PER_LEVEL;
        fallSpeed = Math.min(Constants.MAX_FALL_SPEED, Constants.BASE_FALL_SPEED + (level - 1) * 0.35);
        spawnIntervalMs = Math.max(Constants.MIN_SPAWN_INTERVAL_MS, Constants.BASE_SPAWN_INTERVAL_MS - (level - 1) * 60L);
    }

    // =========================================================
    //  COLLISIONS / SCORING
    // =========================================================

    private void handleCollision(FallingObject obj, long now) {
        if (obj instanceof Spawn) {
            Spawn spawn = (Spawn) obj;
            int effectiveMultiplier = comboMultiplier * (now < multiplierEndTime ? 2 : 1);
            int gained = spawn.getPoints() * effectiveMultiplier;
            score += gained;

            comboCount++;
            if (comboCount % Constants.COMBO_STEP == 0 && comboMultiplier < Constants.MAX_COMBO_MULTIPLIER) {
                comboMultiplier++;
            }

            particles.burst(obj.getX(), obj.getY(), spawn.getRarity().bodyColor, 18);
            floatingTexts.add(new FloatingText(obj.getX(), obj.getY(), "+" + gained, Constants.COLOR_ACCENT, 40));
            player.triggerCatchBounce();

        } else if (obj instanceof Voidling) {
            if (now < shieldEndTime) {
                particles.burst(obj.getX(), obj.getY(), new Color(90, 200, 255), 20);
                floatingTexts.add(new FloatingText(obj.getX(), obj.getY(), "Blocked!", new Color(90, 200, 255), 40));
            } else {
                loseLife();
                particles.burst(obj.getX(), obj.getY(), Constants.COLOR_DANGER, 24);
                floatingTexts.add(new FloatingText(obj.getX(), obj.getY(), "-1 Life", Constants.COLOR_DANGER, 45));
                screenShake = 8;
                comboCount = 0;
                comboMultiplier = 1;
            }

        } else if (obj instanceof PowerUp) {
            PowerUp powerUp = (PowerUp) obj;
            activatePowerUp(powerUp, now);
            particles.burst(obj.getX(), obj.getY(), powerUp.getType().color, 20);
            floatingTexts.add(new FloatingText(obj.getX(), obj.getY(), powerUp.getType().label, powerUp.getType().color, 45));
        }
    }

    private void handleGroundHit(FallingObject obj) {
        if (obj instanceof Spawn) {
            loseLife();
            comboCount = 0;
            comboMultiplier = 1;
            particles.burst(obj.getX(), Constants.GROUND_Y, new Color(200, 200, 200), 10);
            floatingTexts.add(new FloatingText(obj.getX(), Constants.GROUND_Y, "Missed!", Constants.COLOR_DANGER, 40));
        }
        // Voidlings and PowerUps hitting the ground are harmless -- they just disappear.
    }

    private void activatePowerUp(PowerUp p, long now) {
        switch (p.getType()) {
            case SHIELD:
                shieldEndTime = now + Constants.POWERUP_DURATION_MS;
                player.setShieldActive(true);
                break;
            case SLOW_TIME:
                slowTimeEndTime = now + Constants.POWERUP_DURATION_MS;
                break;
            case MAGNET:
                magnetEndTime = now + Constants.POWERUP_DURATION_MS;
                player.setMagnetActive(true);
                break;
            case MULTIPLIER:
                multiplierEndTime = now + Constants.POWERUP_DURATION_MS;
                break;
            case EXTRA_LIFE:
                if (lives < Constants.MAX_LIVES) {
                    lives++;
                }
                break;
            default:
                break;
        }
    }

    private void loseLife() {
        lives--;
        if (lives <= 0) {
            endGame();
        }
    }

    private void endGame() {
        state = GameState.GAME_OVER;
        if (score > highScore) {
            highScore = score;
            HighScoreManager.saveHighScore(highScore);
        }
    }

    private void startNewGame() {
        score = 0;
        lives = Constants.STARTING_LIVES;
        level = 1;
        comboCount = 0;
        comboMultiplier = 1;
        fallSpeed = Constants.BASE_FALL_SPEED;
        spawnIntervalMs = Constants.BASE_SPAWN_INTERVAL_MS;
        shieldEndTime = 0;
        slowTimeEndTime = 0;
        magnetEndTime = 0;
        multiplierEndTime = 0;

        fallingObjects.clear();
        particles.clear();
        floatingTexts.clear();

        player.setShieldActive(false);
        player.setMagnetActive(false);
        player.setCenterX(Constants.WINDOW_WIDTH / 2.0, 0, Constants.WINDOW_WIDTH);

        lastSpawnTime = System.currentTimeMillis();
        state = GameState.PLAYING;
        requestFocusInWindow();
    }

    // =========================================================
    //  RENDERING
    // =========================================================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (screenShake > 0) {
            double dx = (random.nextDouble() - 0.5) * screenShake;
            double dy = (random.nextDouble() - 0.5) * screenShake;
            g2.translate(dx, dy);
        }

        drawBackground(g2);
        drawGround(g2);

        if (state == GameState.MENU) {
            drawMenu(g2);
        } else {
            for (FallingObject obj : fallingObjects) {
                obj.render(g2);
            }
            particles.render(g2);
            for (FloatingText ft : floatingTexts) {
                ft.render(g2);
            }
            player.render(g2);
            drawHud(g2);

            if (state == GameState.PAUSED) {
                drawPauseOverlay(g2);
            } else if (state == GameState.GAME_OVER) {
                drawGameOverOverlay(g2);
            }
        }

        g2.dispose();
    }

    private void drawBackground(Graphics2D g2) {
        float t = Math.min(1f, (level - 1) / 8f);
        Color top = lerpColor(Constants.COLOR_BG_TOP_EARLY, Constants.COLOR_BG_TOP_LATE, t);
        Color bottom = lerpColor(Constants.COLOR_BG_BOTTOM_EARLY, Constants.COLOR_BG_BOTTOM_LATE, t);

        GradientPaint sky = new GradientPaint(0, 0, top, 0, Constants.GROUND_Y, bottom);
        g2.setPaint(sky);
        g2.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.GROUND_Y);

        long now = System.currentTimeMillis();
        for (double[] s : stars) {
            double phase = s[2] + now * 0.001;
            float alpha = (float) (0.3 + 0.5 * (0.5 + 0.5 * Math.sin(phase)));
            g2.setColor(new Color(255, 255, 255, (int) (alpha * 255)));
            g2.fillOval((int) s[0], (int) s[1], 2, 2);
        }

        // Spawn portal glow at the top
        g2.setColor(new Color(255, 255, 255, 30));
        g2.fillOval(Constants.WINDOW_WIDTH / 2 - 60, -40, 120, 60);
    }

    private void drawGround(Graphics2D g2) {
        g2.setColor(Constants.COLOR_GROUND);
        g2.fillRect(0, Constants.GROUND_Y, Constants.WINDOW_WIDTH, Constants.GROUND_HEIGHT);
        g2.setColor(Constants.COLOR_GROUND_LINE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(0, Constants.GROUND_Y, Constants.WINDOW_WIDTH, Constants.GROUND_Y);
    }

    private void drawHud(Graphics2D g2) {
        g2.setFont(new Font(Constants.FONT_NAME, Font.BOLD, 22));
        g2.setColor(Constants.COLOR_TEXT);
        g2.drawString("Score: " + score, 20, 34);

        g2.setFont(new Font(Constants.FONT_NAME, Font.PLAIN, 15));
        g2.setColor(new Color(255, 255, 255, 200));
        g2.drawString("Best: " + Math.max(highScore, score), 20, 56);
        g2.drawString("Level " + level, 20, 76);

        if (comboMultiplier > 1) {
            g2.setFont(new Font(Constants.FONT_NAME, Font.BOLD, 16));
            g2.setColor(Constants.COLOR_ACCENT);
            g2.drawString("Combo x" + comboMultiplier, 20, 98);
        }

        int heartX = Constants.WINDOW_WIDTH - 40;
        for (int i = 0; i < lives; i++) {
            drawHeart(g2, heartX - i * 32, 22, 14);
        }

        long now = System.currentTimeMillis();
        int iconX = Constants.WINDOW_WIDTH - 40;
        int iconY = 46;
        if (now < shieldEndTime) {
            drawPowerIcon(g2, iconX, iconY, new Color(90, 200, 255), "S");
            iconX -= 30;
        }
        if (now < magnetEndTime) {
            drawPowerIcon(g2, iconX, iconY, new Color(255, 190, 90), "M");
            iconX -= 30;
        }
        if (now < multiplierEndTime) {
            drawPowerIcon(g2, iconX, iconY, new Color(255, 120, 200), "2x");
            iconX -= 30;
        }
        if (now < slowTimeEndTime) {
            drawPowerIcon(g2, iconX, iconY, new Color(150, 230, 160), "T");
        }
    }

    private void drawHeart(Graphics2D g2, int cx, int cy, int size) {
        GeneralPath path = new GeneralPath();
        path.moveTo(cx, cy + size * 0.3);
        path.curveTo(cx, cy, cx - size, cy, cx - size, cy + size * 0.35);
        path.curveTo(cx - size, cy + size * 0.75, cx, cy + size, cx, cy + size * 1.2);
        path.curveTo(cx, cy + size, cx + size, cy + size * 0.75, cx + size, cy + size * 0.35);
        path.curveTo(cx + size, cy, cx, cy, cx, cy + size * 0.3);
        path.closePath();
        g2.setColor(Constants.COLOR_DANGER);
        g2.fill(path);
    }

    private void drawPowerIcon(Graphics2D g2, int x, int y, Color color, String label) {
        g2.setColor(color);
        g2.fillOval(x - 12, y - 12, 24, 24);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font(Constants.FONT_NAME, Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(label);
        g2.drawString(label, x - tw / 2, y + 4);
    }

    private void drawMenu(Graphics2D g2) {
        drawCenteredTitle(g2, "SPAWN SAVER SIMULATOR", Constants.WINDOW_HEIGHT / 2 - 100, 40, Constants.COLOR_ACCENT);

        g2.setFont(new Font(Constants.FONT_NAME, Font.PLAIN, 18));
        g2.setColor(Constants.COLOR_TEXT);
        drawCenteredString(g2, "Catch the falling Spawns. Avoid the Voidlings.", Constants.WINDOW_HEIGHT / 2 - 50);
        drawCenteredString(g2, "Arrow Keys / A-D / Mouse to move", Constants.WINDOW_HEIGHT / 2 - 22);

        g2.setFont(new Font(Constants.FONT_NAME, Font.BOLD, 22));
        g2.setColor(Constants.COLOR_SUCCESS);
        drawCenteredString(g2, "Press SPACE, ENTER or Click to Start", Constants.WINDOW_HEIGHT / 2 + 40);

        g2.setFont(new Font(Constants.FONT_NAME, Font.PLAIN, 16));
        g2.setColor(new Color(255, 255, 255, 180));
        drawCenteredString(g2, "High Score: " + highScore, Constants.WINDOW_HEIGHT / 2 + 80);
    }

    private void drawPauseOverlay(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        drawCenteredTitle(g2, "PAUSED", Constants.WINDOW_HEIGHT / 2 - 20, 36, Constants.COLOR_TEXT);
        g2.setFont(new Font(Constants.FONT_NAME, Font.PLAIN, 16));
        g2.setColor(Constants.COLOR_TEXT);
        drawCenteredString(g2, "Press P or ESC to resume", Constants.WINDOW_HEIGHT / 2 + 20);
    }

    private void drawGameOverOverlay(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        drawCenteredTitle(g2, "GAME OVER", Constants.WINDOW_HEIGHT / 2 - 60, 40, Constants.COLOR_DANGER);

        g2.setFont(new Font(Constants.FONT_NAME, Font.BOLD, 22));
        g2.setColor(Constants.COLOR_TEXT);
        drawCenteredString(g2, "Final Score: " + score, Constants.WINDOW_HEIGHT / 2 - 10);

        g2.setFont(new Font(Constants.FONT_NAME, Font.PLAIN, 17));
        g2.setColor(Constants.COLOR_ACCENT);
        String msg = score >= highScore ? "New High Score!" : "High Score: " + highScore;
        drawCenteredString(g2, msg, Constants.WINDOW_HEIGHT / 2 + 20);

        g2.setFont(new Font(Constants.FONT_NAME, Font.BOLD, 18));
        g2.setColor(Constants.COLOR_SUCCESS);
        drawCenteredString(g2, "Press SPACE, ENTER or Click to Play Again", Constants.WINDOW_HEIGHT / 2 + 60);
    }

    private void drawCenteredString(Graphics2D g2, String text, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int x = (Constants.WINDOW_WIDTH - fm.stringWidth(text)) / 2;
        g2.drawString(text, x, y);
    }

    private void drawCenteredTitle(Graphics2D g2, String text, int y, int size, Color color) {
        g2.setFont(new Font(Constants.FONT_NAME, Font.BOLD, size));
        g2.setColor(new Color(0, 0, 0, 140));
        drawCenteredString(g2, text, y + 3);
        g2.setColor(color);
        drawCenteredString(g2, text, y);
    }

    private void initStars() {
        for (int i = 0; i < stars.length; i++) {
            stars[i][0] = random.nextDouble() * Constants.WINDOW_WIDTH;
            stars[i][1] = random.nextDouble() * (Constants.GROUND_Y - 20);
            stars[i][2] = random.nextDouble() * Math.PI * 2;
        }
    }

    private Color lerpColor(Color a, Color b, float t) {
        t = Math.max(0, Math.min(1, t));
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        return new Color(r, g, bl);
    }
}
