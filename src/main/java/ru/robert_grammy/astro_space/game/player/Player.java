package ru.robert_grammy.astro_space.game.player;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.*;
import ru.robert_grammy.astro_space.engine.geometry.StraightLine;
import ru.robert_grammy.astro_space.engine.geometry.Vector;
import ru.robert_grammy.astro_space.engine.sound.GameSound;
import ru.robert_grammy.astro_space.engine.sound.Sound;
import ru.robert_grammy.astro_space.game.asteroid.Asteroid;
import ru.robert_grammy.astro_space.game.background.Particle;
import ru.robert_grammy.astro_space.game.background.ParticleGenerator;
import ru.robert_grammy.astro_space.game.powerup.PowerUp;
import ru.robert_grammy.astro_space.game.shape.ShapeManager;
import ru.robert_grammy.astro_space.game.shape.LineShape;
import ru.robert_grammy.astro_space.utils.QMath;
import ru.robert_grammy.astro_space.utils.rnd.RandomValueRange;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.Optional;

import static ru.robert_grammy.astro_space.game.powerup.PowerUp.*;

public class Player implements Updatable, Renderable {

    private static final Sound FLY_SOUND = GameSound.FLY.get();
    private static final Sound GAS_ON_SOUND = GameSound.GAS_ON.get();
    private static final Sound GAS_OFF_SOUND = GameSound.GAS_OFF.get();
    private static final Sound SHOOT_SOUND = GameSound.SHOOT.get();
    private static final Sound BOOM_SOUND = GameSound.BOOM.get();
    private static final Sound START_GAME_SOUND = GameSound.START_GAME.get();
    private static final Sound GAME_OVER_SOUND = GameSound.GAME_OVER.get();
    private static final Sound BACKGROUND_SOUND = GameSound.BACKGROUND.get();
    private static final Color INVINCIBLE_SHIELD_FILL_COLOR = new Color(0x4400FFFF, true);
    private static final Color INVINCIBLE_SHIELD_OUTLINE_COLOR = Color.WHITE;
    private static final int DEFAULT_SHOOT_TIME = 25;
    private static final int DEFAULT_DESTROY_TIME = 35;
    private static final int DEFAULT_TIME_TO_POWER_UP_CALL = 2000;
    private static final int BOUND_TIME_TO_POWER_UP_CALL = 4000;
    private static final int Z_INDEX = 100;
    private static final int INVINCIBLE_SHIELD_SIZE = 50;
    private static final int INVINCIBLE_SHIELD_BLINK_TIME = 100;
    private static final int INVINCIBLE_SHIELD_BLINK_RATE_COEFFICIENT = 5;
    private static final int ROTATE_SPEED = 3;
    private static final int EXPLOSION_PARTICLE_SIZE = 40;
    private static final int EXPLOSION_PARTICLE_HEX_COLOR = 0x551111;
    private static final int SHOOT_TIME_FASTER_COEFFICIENT = 5;
    private static final int DEFAULT_BULlET_SPEED = 5;
    private static final double DECREASE_MOVEMENT_SPEED_COEFFICIENT = .975;
    private static final double INCREASE_MOVEMENT_SPEED_COEFFICIENT = .15;
    private static final double FIRE_TRAIL_POINT_OFFSET_OF_PLAYER_POSITION = 12;
    private static final double STOP_MOVEMENT_VECTOR_LENGTH = .135;
    private static final double SHAPE_SMALLER_POWER_UP_SCALE = 0.75;
    private int destroyTimer = DEFAULT_DESTROY_TIME;
    private int timeToNextPowerUpCall = DEFAULT_TIME_TO_POWER_UP_CALL * 2;
    private int shootTimer = 0;
    private int powerUpDuration = 0;
    private boolean isDestroyed = false;
    private final double defaultScale;
    private final double scaleIncrementCoefficient;
    private final Stroke shapeStroke;
    private final LineShape shape;
    private final Vector position;
    private final Vector movement;
    private ParticleGenerator explosion;
    private PowerType powerUpType;

    public Player(Vector position) {
        this.position = position;
        movement = Vector.getZero();
        shape = ShapeManager.PLAYER_STARSHIP.getShape();
        START_GAME_SOUND.play();
        GameSound.playAfter(START_GAME_SOUND, BACKGROUND_SOUND, Clip.LOOP_CONTINUOUSLY, false, 500).start();
        shapeStroke = new BasicStroke(shape.getLineWeight(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        defaultScale = shape.getScale();
        scaleIncrementCoefficient = ((defaultScale / SHAPE_SMALLER_POWER_UP_SCALE) / PowerType.SMALLER.getDuration());
    }

    @Override
    public void render(Graphics2D graphics) {
        if (isDestroyed) return;
        GeneralPath path = new GeneralPath();
        Vector firstPoint = null;
        for (Vector point : shape.getRealPoints(position)) {
            if (firstPoint == null) {
                firstPoint = point;
                path.moveTo(firstPoint.getX(), firstPoint.getY());
            }
            path.lineTo(point.getX(), point.getY());
        }
        firstPoint = Optional.ofNullable(firstPoint).orElse(Vector.getUndefined());
        path.lineTo(firstPoint.getX(), firstPoint.getY());
        path.closePath();
        graphics.setColor(shape.getFillColor());
        graphics.fill(path);
        graphics.setStroke(shapeStroke);
        graphics.setColor(shape.getLineColor());
        if (onPower(PowerType.FIRE_RATE)) graphics.setColor(Color.YELLOW);
        if (onPower(PowerType.DOUBLE_SCORE)) graphics.setColor(Color.GREEN);
        graphics.draw(path);
        if (onPower(PowerType.INVINCIBLE)) {
            if (powerUpDuration > INVINCIBLE_SHIELD_BLINK_TIME || powerUpDuration % INVINCIBLE_SHIELD_BLINK_RATE_COEFFICIENT == 0) {
                graphics.setColor(INVINCIBLE_SHIELD_FILL_COLOR);
                graphics.fillOval((int) position.getX() - (INVINCIBLE_SHIELD_SIZE / 2), (int) position.getY() - (INVINCIBLE_SHIELD_SIZE / 2), INVINCIBLE_SHIELD_SIZE, INVINCIBLE_SHIELD_SIZE);
                graphics.setColor(INVINCIBLE_SHIELD_OUTLINE_COLOR);
                graphics.drawOval((int) position.getX() - (INVINCIBLE_SHIELD_SIZE / 2), (int) position.getY() - (INVINCIBLE_SHIELD_SIZE / 2), INVINCIBLE_SHIELD_SIZE, INVINCIBLE_SHIELD_SIZE);
            }
        }
    }

    @Override
    public int getZIndex() {
        return Z_INDEX;
    }

    @Override
    public void update() {
        if (!isDestroyed) {
            control();
            movement();
            handlePowerUpUp();
            powerUpProcess();
            stockCall();
        }
        afterDie();
    }

    private void control() {
        Keyboard keyboard = Main.getGame().getWindow().getKeyboard();

        if (keyboard.pressed(KeyEvent.VK_RIGHT)) {
            shape.rotate(ROTATE_SPEED);
        }
        if (keyboard.pressed(KeyEvent.VK_LEFT)) {
            shape.rotate(-ROTATE_SPEED);
        }

        if (keyboard.pressed(KeyEvent.VK_DOWN)) {
            movement.multiply(DECREASE_MOVEMENT_SPEED_COEFFICIENT);
        }

        if (keyboard.pressed(KeyEvent.VK_UP)) {
            movement.multiply(DECREASE_MOVEMENT_SPEED_COEFFICIENT).add(getDirection().multiply(INCREASE_MOVEMENT_SPEED_COEFFICIENT));
            Vector particleCenter = position.clone().subtract(getDirection().normalize().multiply(FIRE_TRAIL_POINT_OFFSET_OF_PLAYER_POSITION));
            Particle particle = ParticleGenerator.createTrailParticle(particleCenter);
            Main.getGame().register(particle);
            if (!FLY_SOUND.isPlaying() && !GAS_ON_SOUND.isPlaying()) {
                GAS_ON_SOUND.play(false);
            } else if (!FLY_SOUND.isPlaying() && GAS_ON_SOUND.isEnded()) {
                FLY_SOUND.loop();
                FLY_SOUND.play(false);
                GAS_ON_SOUND.stopAndReset();
            }
        } else {
            if ((GAS_ON_SOUND.isPlaying() || !FLY_SOUND.isPlaying()) && GAS_ON_SOUND.isEnded()) GAS_ON_SOUND.stopAndReset();
            if (FLY_SOUND.isPlaying()) {
                FLY_SOUND.stop();
                GAS_OFF_SOUND.play();
            }
        }

        double length = movement.length();
        if (length <= STOP_MOVEMENT_VECTOR_LENGTH) movement.resetCoordinates();

        if (keyboard.pressed(KeyEvent.VK_SPACE)) {
            if (!keyboard.isMemorized(KeyEvent.VK_SPACE) && shootTimer <= 0) {
                keyboard.memorizePress(KeyEvent.VK_SPACE);
                SHOOT_SOUND.play();
                shootTimer = DEFAULT_SHOOT_TIME;
                if (onPower(PowerType.FIRE_RATE)) shootTimer /= SHOOT_TIME_FASTER_COEFFICIENT;
                Vector firstRealPoint = shape.getRealPoints(position).get(0);
                Vector bulletMovement = new Vector(firstRealPoint.getX() - position.getX(), firstRealPoint.getY() - position.getY());
                Bullet bullet = new Bullet(firstRealPoint.add(bulletMovement.normalize().multiply(DEFAULT_BULlET_SPEED)), bulletMovement);
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
        boolean outOfX = position.getX() < 0 || position.getX() > Main.getGame().getWindow().getBufferWidth();
        boolean outOfY = position.getY() < 0 || position.getY() > Main.getGame().getWindow().getBufferHeight();
        if (outOfX || outOfY) {
            if (onPower(PowerType.INVINCIBLE)) {
                if (outOfX) {
                    movement.setX(-movement.getX());
                } else {
                    movement.setY(-movement.getY());
                }
            } else {
                destroy();
            }
        }
    }

    private void stopSound() {
        GAS_ON_SOUND.stopAndReset();
        FLY_SOUND.stopAndReset();
        GAS_OFF_SOUND.stopAndReset();
        BACKGROUND_SOUND.stopAndReset();
    }

    public void handlePowerUpUp() {
        Main.getGame().getRenderables().stream().filter(renderable -> renderable instanceof PowerUp).map(powerUp -> (PowerUp) powerUp).forEach(powerUp -> {
            List<Vector> realPoints = shape.getRealPoints(position);
            for (int i = 0; i < realPoints.size(); i++) {
                Vector a = realPoints.get(i);
                Vector b = realPoints.get(i + 1 == realPoints.size() ? 0 : i + 1);
                StraightLine line = new StraightLine(a, b);
                double distanceFromLine = line.distanceFromSegmentToPoint(powerUp.getPosition());
                if (distanceFromLine <= (double) (PowerUp.SIZE / 2) + 1) {
                    powerUpUp(powerUp);
                }
            }
        });
    }

    private void powerUpProcess() {
        if (shape.getScale() < defaultScale) {
            shape.setScale(shape.getScale() + scaleIncrementCoefficient);
            if (shape.getScale() >= defaultScale) shape.setScale(defaultScale);
        }
    }

    private void powerUpUp(PowerUp powerUp) {
        PowerType powerUpType = powerUp.getType();
        powerUp.kill();
        if (powerUpType == PowerType.SMALLER) {
            shape.scale(SHAPE_SMALLER_POWER_UP_SCALE);
            return;
        }
        if (powerUpType == PowerType.ADD_SCORE) {
            Main.getGame().addScore(RND_SCORE_EARN_RANGE.randomValue() * BASE_SCORE_EARN_MULTIPLY);
            return;
        }
        this.powerUpType = powerUpType;
        if (powerUpType == PowerType.BIG_BOOM) {
            Main.getGame().getUpdatables().stream().filter(updatable -> updatable instanceof Asteroid).map(asteroid -> (Asteroid) asteroid).forEach(Asteroid::kill);
            return;
        }
        powerUpDuration = powerUpType.getDuration();
    }

    public Vector getDirection() {
        return shape.getXBasisVector().clone().rotate(QMath.DEGREES_OF_RIGHT_ANGLE).rotate(shape.getRotation());
    }

    public void destroy() {
        if (isDestroyed) return;
        stopSound();
        isDestroyed = true;
        explosion = ParticleGenerator.createExplosion(position, EXPLOSION_PARTICLE_SIZE, EXPLOSION_PARTICLE_HEX_COLOR);
        Main.getGame().register(explosion);
        GAME_OVER_SOUND.play();
        BOOM_SOUND.play();
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
        this.position.setXY(position);
    }

    public void resurrect() {
        isDestroyed = false;
        movement.resetCoordinates();
        destroyTimer = DEFAULT_DESTROY_TIME;
        shootTimer = DEFAULT_SHOOT_TIME;
        powerUpDuration = 0;
        timeToNextPowerUpCall = DEFAULT_TIME_TO_POWER_UP_CALL + RandomValueRange.RND.nextInt(BOUND_TIME_TO_POWER_UP_CALL);
        powerUpType = null;
        shape.setRotation(180);
        explosion.setRecurring(false);
        ParticleGenerator smoke = ParticleGenerator.createSmoke(position);
        Main.getGame().register(smoke);
        smoke.setRecurring(false);
        START_GAME_SOUND.play();
        GameSound.playAfter(START_GAME_SOUND, BACKGROUND_SOUND, Clip.LOOP_CONTINUOUSLY, false, 500).start();
    }

    public void stockCall() {
        if (timeToNextPowerUpCall > 0) {
            timeToNextPowerUpCall--;
        } else {
            timeToNextPowerUpCall = DEFAULT_TIME_TO_POWER_UP_CALL + RandomValueRange.RND.nextInt(BOUND_TIME_TO_POWER_UP_CALL);
            PowerUp powerUp = new PowerUp();
            Main.getGame().register(powerUp);
        }
        if (powerUpDuration > 0) {
            powerUpDuration--;
        }
    }

    public boolean onPower(PowerType type) {
        return powerUpDuration > 0 && powerUpType == type;
    }

}
