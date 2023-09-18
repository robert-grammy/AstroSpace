package ru.robert_grammy.astro_space.game.player;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.*;
import ru.robert_grammy.astro_space.game.asteroid.Asteroid;
import ru.robert_grammy.astro_space.game.background.Particle;
import ru.robert_grammy.astro_space.game.background.ParticleGenerator;
import ru.robert_grammy.astro_space.game.powerup.PowerUp;
import ru.robert_grammy.astro_space.game.shape.ShapeManager;
import ru.robert_grammy.astro_space.game.shape.LineShape;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Player implements Updatable, Renderable {

    private static final Random rnd = new Random();

    private static final int DEFAULT_SHOOT_TIME = 25;
    private static final int DEFAULT_DESTROY_TIME = 35;
    private static final int DEFAULT_TIME_TO_POWER_UP_CALL = 1000;
    private static final int BOUND_TIME_TO_POWER_UP_CALL = 2500;
    private static final int DEFAULT_POWER_UP_DURATION = 750;

    private final LineShape shape;
    private int zIndex = 100;

    private Vector position;
    private Vector movement = new Vector(0, 0);
    private int shootTimer = 0;
    private boolean isDestroyed = false;
    private int destroyTimer = DEFAULT_DESTROY_TIME;
    private ParticleGenerator explosion;

    private int timeToNextPowerUpCall = DEFAULT_TIME_TO_POWER_UP_CALL * 2;
    private PowerUp.PowerType powerUpType;
    private int powerUpDuration = 0;

    public Player(Vector position) {
        this.position = position;
        shape = ShapeManager.PLAYER_DEFAULT.getShape();
    }

    public Player(double x,double y) {
        this(new Vector(x,y));
    }

    @Override
    public void render(Graphics2D graphics) {
        if (!isDestroyed) {
            GeneralPath path = new GeneralPath();
            Vector firstPoint = null;
            for (Vector point : shape.getRealPoints(position)) {
                if (firstPoint == null) {
                    firstPoint = point;
                    path.moveTo(firstPoint.getX(), firstPoint.getY());
                }
                path.lineTo(point.getX(), point.getY());
            }
            firstPoint = Optional.ofNullable(firstPoint).orElse(new Vector(-1, -1));
            path.lineTo(firstPoint.getX(), firstPoint.getY());
            path.closePath();
            graphics.setColor(shape.getFillColor());
            graphics.fill(path);
            graphics.setStroke(new BasicStroke(shape.getLineWeight(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphics.setColor(shape.getLineColor());
            if (onPower(PowerUp.PowerType.FIRE_RATE)) graphics.setColor(Color.YELLOW);
            if (onPower(PowerUp.PowerType.DOUBLE_SCORE)) graphics.setColor(Color.GREEN);
            graphics.draw(path);
            if (onPower(PowerUp.PowerType.INVINCIBLE)) {
                graphics.setColor(new Color(0x4400FFFF, true));
                graphics.fillOval((int) position.getX() - 25, (int) position.getY() - 25, 50, 50);
                graphics.setColor(Color.WHITE);
                graphics.drawOval((int) position.getX() - 25, (int) position.getY() - 25, 50, 50);
            }
        }
        Rectangle lineBound = new Rectangle((int) (position.getX() - 100), (int) (position.getY() - 100), 200, 200);
        if (Main.getGame().getGameDebugger().isDrawPlayerShapeLines()) {
            shape.getShapeRealLines(position).forEach(line -> line.draw(graphics, lineBound, Color.YELLOW, 1));
        }
        if (Main.getGame().getGameDebugger().isDrawPlayerLineBound()) {
            graphics.setColor(Color.GREEN);
            Stroke stroke = graphics.getStroke();
            graphics.setStroke(new BasicStroke(shape.getLineWeight(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphics.drawRect(lineBound.x, lineBound.y, lineBound.width, lineBound.height);
            graphics.setStroke(stroke);
        }

    }

    @Override
    public void setZIndex(int z) {
        zIndex = z;
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void update() {
        if (!isDestroyed) {
            control(Main.getGame().getWindow().getKeyboard());
            movement();
            handlePowerUpUp();
            stockCall();
        }
        afterDie();
    }

    private void control(Keyboard keyboard) {
        if (keyboard.pressed(KeyEvent.VK_RIGHT)) {
            shape.rotate(3);
        }
        if (keyboard.pressed(KeyEvent.VK_LEFT)) {
            shape.rotate(-3);
        }

        if (keyboard.pressed(KeyEvent.VK_DOWN)){
            movement.multiply(.965);
        }
        if (keyboard.pressed(KeyEvent.VK_UP)) {
            movement.multiply(.975).add(getDirection().multiply(.15));
            Vector particleCenter = position.clone().subtract(getDirection().normalize().multiply(12));
            Rectangle trailBound = new Rectangle((int) (particleCenter.getX() - 7), (int) (particleCenter.getY() - 7), 7, 7);
            int colorOffset = rnd.nextInt(0x11, 0x55);
            Particle particle = new Particle(trailBound, 5, 15, 50, 180, 0.5, 2, 0xCC2222 + (colorOffset << 8));
            particle.setZIndex(30);
            particle.setRecurring(false);
            Main.getGame().register(particle);
        }

        double length = movement.length();
        if (length <= .135) movement = new Vector(0,0);

        if (keyboard.pressed(KeyEvent.VK_SPACE)) {
            if (!keyboard.isMemorized(KeyEvent.VK_SPACE) && shootTimer <= 0) {
                keyboard.memorizePress(KeyEvent.VK_SPACE);
                shootTimer = DEFAULT_SHOOT_TIME;
                if (onPower(PowerUp.PowerType.FIRE_RATE)) shootTimer /= 5;
                Vector firstRealPoint = shape.getRealPoints(position).get(0);
                Vector bulletMovement = new Vector(firstRealPoint.getX() - position.getX(), firstRealPoint.getY() - position.getY());
                Bullet bullet = new Bullet(firstRealPoint.add(bulletMovement.normalize().multiply(5)), bulletMovement);
                Main.getGame().register(bullet);
            }
        }

        if (shootTimer > 0) shootTimer--;
    }

    private void afterDie() {
        if (!isDestroyed || destroyTimer <= 0) return;
        destroyTimer--;
        if (destroyTimer > 0) return;
        explosion.setRecurring(false);
    }

    private void movement() {
        position.add(movement);
        if (position.getX() < 0 || position.getX() > Main.getGame().getWindow().getBufferWidth() || position.getY() < 0 || position.getY() > Main.getGame().getWindow().getBufferHeight()) destroy();
    }

    public void handlePowerUpUp() {
        Main.getGame().getRenderables().stream().filter(renderable -> renderable instanceof PowerUp).map(powerUp -> (PowerUp) powerUp).forEach(powerUp -> {
            List<Vector> realPoints = shape.getRealPoints(position);
            for (int i = 0; i<realPoints.size(); i++) {
                Vector a = realPoints.get(i);
                if (powerUp.getPosition().clone().subtract(a).length() < 20) {
                    if (powerUp.getType() == PowerUp.PowerType.ADD_SCORE) {
                        Main.getGame().addScore(rnd.nextInt(1,11) * 50);
                        powerUp.kill();
                        return;
                    }
                    powerUpType = powerUp.getType();
                    powerUp.kill();
                    if (powerUpType == PowerUp.PowerType.BIG_BOOM) {
                        Main.getGame().getUpdatables().stream().filter(updatable -> updatable instanceof Asteroid).map(asteroid -> (Asteroid) asteroid).forEach(Asteroid::kill);
                        return;
                    }
                    powerUpDuration = DEFAULT_POWER_UP_DURATION;
                    return;
                }
                Vector b = realPoints.get(i + 1 == realPoints.size() ? 0 : i + 1);
                StraightLine shapeLine = new StraightLine(a, b);
                StraightLine normalLine = shapeLine.getNormalLineFromPoint(powerUp.getPosition());
                Vector cross = shapeLine.getPointIntersectionLines(normalLine);
                boolean outYRange = cross.getY() < Math.min(a.getY(), b.getY()) || cross.getY() > Math.max(a.getY(), b.getY());
                boolean outXRange = cross.getX() < Math.min(a.getX(), b.getX()) || cross.getX() > Math.max(a.getX(), b.getX());
                if (outYRange || outXRange) continue;
                double length = powerUp.getPosition().clone().subtract(cross).length();
                if (length > 20) continue;
                if (powerUp.getType() == PowerUp.PowerType.ADD_SCORE) {
                    Main.getGame().addScore(rnd.nextInt(1,11) * 50);
                    powerUp.kill();
                    return;
                }
                powerUpType = powerUp.getType();
                powerUp.kill();
                if (powerUpType == PowerUp.PowerType.BIG_BOOM) {
                    Main.getGame().getUpdatables().stream().filter(updatable -> updatable instanceof Asteroid).map(asteroid -> (Asteroid) asteroid).forEach(Asteroid::kill);
                    return;
                }
                powerUpDuration = DEFAULT_POWER_UP_DURATION;
                return;
            }
        });
    }

    public Vector getDirection() {
        return shape.getXBasisVector().clone().rotate(90).rotate(shape.getRotation());
    }

    public void destroy() {
        if (isDestroyed) return;
        isDestroyed = true;
        Rectangle explosionBound = new Rectangle((int) (position.getX() - 40), (int) (position.getY() - 40), 40, 40);
        explosion = new ParticleGenerator(50, 100, explosionBound, 15, 40, 30, 200, 2, 5, 0x551111);
        Main.getGame().register(explosion);
    }

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public LineShape getShape() {
        return shape;
    }

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public void resurrect() {
        isDestroyed = false;
        movement = new Vector(0, 0);
        destroyTimer = DEFAULT_DESTROY_TIME;
        shootTimer = DEFAULT_SHOOT_TIME;
        powerUpDuration = 0;
        timeToNextPowerUpCall = DEFAULT_TIME_TO_POWER_UP_CALL + rnd.nextInt(BOUND_TIME_TO_POWER_UP_CALL);
        powerUpType = null;
        shape.setRotation(180);
        explosion.setRecurring(false);
        Rectangle smokeBound = new Rectangle((int) (position.getX() - 40), (int) (position.getY() - 40), 40, 40);
        ParticleGenerator smoke = new ParticleGenerator(50, 100, smokeBound, 30, 60, 40, 250, 1, 4, 0xEEFFEE);
        Main.getGame().register(smoke);
        smoke.setRecurring(false);
    }

    public void stockCall() {
        if (timeToNextPowerUpCall > 0) {
            timeToNextPowerUpCall--;
        } else {
            timeToNextPowerUpCall = DEFAULT_TIME_TO_POWER_UP_CALL + rnd.nextInt(BOUND_TIME_TO_POWER_UP_CALL);
            PowerUp powerUp = new PowerUp();
            Main.getGame().register(powerUp);
        }
        if (powerUpDuration > 0) {
            powerUpDuration--;
        }
    }

    public boolean onPower(PowerUp.PowerType type) {
        return powerUpDuration > 0 && powerUpType == type;
    }

}
