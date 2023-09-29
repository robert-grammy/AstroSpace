package ru.robert_grammy.astro_space.game.asteroid;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.geometry.StraightLine;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.engine.geometry.Vector;
import ru.robert_grammy.astro_space.engine.sound.GameSound;
import ru.robert_grammy.astro_space.engine.sound.Sound;
import ru.robert_grammy.astro_space.game.background.ParticleGenerator;
import ru.robert_grammy.astro_space.game.player.Player;
import ru.robert_grammy.astro_space.game.powerup.PowerUp;
import ru.robert_grammy.astro_space.game.shape.LineShape;
import ru.robert_grammy.astro_space.game.shape.ShapeManager;
import ru.robert_grammy.astro_space.utils.QMath;
import ru.robert_grammy.astro_space.utils.rnd.RandomIntegerValueRange;
import ru.robert_grammy.astro_space.utils.rnd.RandomValueRange;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.Optional;

public class Asteroid implements Renderable, Updatable {

    private final static RandomIntegerValueRange XY_SPAWN_OFFSET_RANGE = new RandomIntegerValueRange(-100, 100);
    private final static RandomIntegerValueRange NEGATIVE_ZERO_POSITIVE_RANGE = new RandomIntegerValueRange(-1, 1);
    private final static RandomIntegerValueRange DEGREES_OF_ANGLE_OFFSET_RANGE = new RandomIntegerValueRange(-100, 100);
    private final static BasicStroke ASTEROID_LINE_WEIGHT = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private final static int MIN_ASTEROIDS_COUNT = 2;
    private final static int MIN_ASTEROID_SIZE = 3;
    private final static int MIN_ASTEROID_SIZE_TO_SPLIT = 8;
    private final static int ASTEROID_SPLIT_COEFFICIENT = 5;
    private final static int ASTEROID_EXPLOSION_PARTICLE_HEX_COLOR = 0x7C2B2B;
    private final static int ASTEROID_EXPLOSION_SIZE_COEFFICIENT = 6;
    private final static int INSCRIBED_SIZE_COEFFICIENT = 10;
    private final static int OUT_OF_SCREEN_OFFSET = 10;
    private final static int ASTEROIDS_MAX_COUNT = 40;
    private final static int ASTEROID_MAX_SIZE = 47;
    private final static int ASTEROID_MIN_SIZE = 7;
    private final static int DEFAULT_MAX_Z_INDEX = 90;
    private final static int COEFFICIENT_TO_CALCULATE_MOVEMENT_SPEED_INCREASE = 35;
    private final static double ROTATION_SPEED_RND_BOUND = 0.75;
    private final static double DEFAULT_ROTATION_SPEED = 0.95;
    private final static double ASTEROID_SIZE_BOUND_COEFFICIENT = 1.2;
    private final static double ASTEROID_HEALTH_CALCULATE_COEFFICIENT = 3.5;
    private final static double ASTEROID_MIN_MOVEMENT_SPEED = 0.60;
    private final static double COEFFICIENT_TO_CALCULATE_MOVEMENT_SPEED_DECREASE = 0.35;
    private final static Sound BOOM_SOUND = GameSound.BOOM.get();
    private boolean isDestroyed = false;
    private boolean isResetImmune = true;
    private Vector inertia;
    private Vector position;
    private LineShape shape;
    private boolean rightRotation;
    private double rotationSpeed;
    private int zIndex;
    private int size;
    private int health;
    private int destroyTimer;
    private ParticleGenerator explosion;
    private Color asteroidColor;

    public Asteroid(int size, boolean rightRotation, double rotationSpeed, Vector inertia, Vector position) {
        this.size = size;
        this.position = position;
        this.rightRotation = rightRotation;
        this.rotationSpeed = rotationSpeed;
        this.inertia = inertia;
        this.shape = ShapeManager.generate(size);
        this.zIndex = DEFAULT_MAX_Z_INDEX - size;
        this.health = (int) (size / ASTEROID_HEALTH_CALCULATE_COEFFICIENT);
        this.asteroidColor = shape.getFillColor();
        destroyTimer = size * 2;
    }

    public Asteroid() {
        reset();
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
        firstPoint = Optional.ofNullable(firstPoint).orElse(Vector.getZero());
        path.lineTo(firstPoint.getX(), firstPoint.getY());
        path.closePath();
        graphics.setColor(asteroidColor);
        graphics.fill(path);
        graphics.setStroke(ASTEROID_LINE_WEIGHT);
        graphics.setColor(shape.getLineColor());
        graphics.draw(path);
    }

    public void damage() {
        health--;
        colorChange();
    }

    private void colorChange() {
        int hexFillColor = shape.getFillColor().getRGB();
        double thirdOfSize = size / 3.0;
        int colorBrighter = (int) (48 * ((thirdOfSize - health) / thirdOfSize));
        hexFillColor += colorBrighter + (colorBrighter << 8) + (colorBrighter << 16);
        asteroidColor = new Color(hexFillColor);
    }

    public void destroy() {
        kill();
        split();
    }

    public void kill() {
        if (isDestroyed) return;
        isDestroyed = true;
        int explosionSize = size * ASTEROID_EXPLOSION_SIZE_COEFFICIENT;
        explosion = ParticleGenerator.createExplosion(position, explosionSize, ASTEROID_EXPLOSION_PARTICLE_HEX_COLOR);
        Main.getGame().register(explosion);
        BOOM_SOUND.play();
    }

    private void split() {
        if (size > MIN_ASTEROID_SIZE_TO_SPLIT) {
            int asteroidCountBound = size / ASTEROID_SPLIT_COEFFICIENT;
            RandomIntegerValueRange countRange = new RandomIntegerValueRange(MIN_ASTEROIDS_COUNT, asteroidCountBound);
            RandomIntegerValueRange xRange = new RandomIntegerValueRange((int) this.position.getX() - (size / 2), (int) (this.position.getX() + size));
            RandomIntegerValueRange yRange = new RandomIntegerValueRange((int) this.position.getY() - (size / 2), (int) (this.position.getY() + size));
            int asteroidCount = asteroidCountBound <= MIN_ASTEROIDS_COUNT ? MIN_ASTEROIDS_COUNT : countRange.randomValue();
            for (int i = asteroidCount; i>0; i--) {
                Vector position = new Vector(
                        xRange.randomValue(),
                        yRange.randomValue()
                );
                int size = this.size / asteroidCount;
                size = Math.max(size, MIN_ASTEROID_SIZE);
                double rotationSpeed = DEFAULT_ROTATION_SPEED + RandomValueRange.RND.nextDouble(ROTATION_SPEED_RND_BOUND);
                boolean rightRotation = RandomValueRange.RND.nextBoolean();
                int degree = NEGATIVE_ZERO_POSITIVE_RANGE.randomValue() * (QMath.DEGREES_OF_RIGHT_ANGLE / 2) + DEGREES_OF_ANGLE_OFFSET_RANGE.randomValue();
                Vector inertia = new Vector(
                        this.inertia.getX() * QMath.cos(degree) - this.inertia.getY() * QMath.sin(degree),
                        this.inertia.getX() * QMath.sin(degree) + this.inertia.getY() * QMath.cos(degree)
                );
                Asteroid asteroid = new Asteroid(size, rightRotation, rotationSpeed, inertia, position);
                Main.getGame().register(asteroid);
            }
        }
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void update() {
        if (!isDestroyed) {
            movement();
            playerCollision();
            process();
        }
        afterDie();
    }

    private void movement() {
        if (Main.getGame().getPlayer().onPower(PowerUp.PowerType.FREEZER)) return;
        position.add(inertia);
        shape.rotate(rightRotation ? rotationSpeed : -rotationSpeed);
    }

    private void playerCollision() {
        Player player = Main.getGame().getPlayer();
        if (player.isDestroyed()) return;
        List<Vector> asteroidPoints = shape.getRealPoints(position);
        for (int i = 0; i<asteroidPoints.size(); i++) {
            Vector asteroidA = asteroidPoints.get(i);
            Vector asteroidB = asteroidPoints.get(i + 1 == asteroidPoints.size() ? 0 : i + 1);
            StraightLine asteroidLine = new StraightLine(asteroidA, asteroidB);
            List<Vector> playerPoints = player.getShape().getRealPoints(player.getPosition());
            for (int j = 0; j<playerPoints.size(); j++) {
                Vector playerA = playerPoints.get(j);
                Vector playerB = playerPoints.get(j + 1 == playerPoints.size() ? 0 : j + 1);
                StraightLine playerLine = new StraightLine(playerA, playerB);
                if (asteroidLine.isParallel(playerLine)) continue;
                Vector cross = asteroidLine.getPointIntersectionLines(playerLine);
                boolean outPlayerYRange = cross.getY() < Math.min(playerA.getY(), playerB.getY()) || cross.getY() > Math.max(playerA.getY(), playerB.getY());
                boolean outPlayerXRange = cross.getX() < Math.min(playerA.getX(), playerB.getX()) || cross.getX() > Math.max(playerA.getX(), playerB.getX());
                boolean outAsteroidYRange = cross.getY() < Math.min(asteroidA.getY(), asteroidB.getY()) || cross.getY() > Math.max(asteroidA.getY(), asteroidB.getY());
                boolean outAsteroidXRange = cross.getX() < Math.min(asteroidA.getX(), asteroidB.getX()) || cross.getX() > Math.max(asteroidA.getX(), asteroidB.getX());
                if (outPlayerYRange || outPlayerXRange || outAsteroidYRange || outAsteroidXRange) continue;
                if (player.onPower(PowerUp.PowerType.INVINCIBLE)) {
                    kill();
                    return;
                }
                player.destroy();
                return;
            }
        }
    }

    private void process() {
        if (health <= 0) {
            destroy();
        }
        int inscribedSize = size * INSCRIBED_SIZE_COEFFICIENT;
        if (
                position.getX() < (-inscribedSize - OUT_OF_SCREEN_OFFSET) * 2 ||
                position.getX() > (Main.getGame().getWindow().getBufferWidth() + inscribedSize + OUT_OF_SCREEN_OFFSET) * 2 ||
                position.getY() < (-inscribedSize - OUT_OF_SCREEN_OFFSET) * 2 ||
                position.getY() > (Main.getGame().getWindow().getBufferHeight() + inscribedSize + OUT_OF_SCREEN_OFFSET) * 2
        ) {
            reset();
        }
        if (isResetImmune) {
            if (
                    position.getX() >= 0 ||
                    position.getX() <= Main.getGame().getWindow().getBufferWidth() ||
                    position.getY() >= 0 ||
                    position.getY() <= Main.getGame().getWindow().getBufferHeight()
            ) {
                isResetImmune = false;
            }
        } else {
            if (
                    position.getX() < -inscribedSize - OUT_OF_SCREEN_OFFSET ||
                    position.getX() > Main.getGame().getWindow().getBufferWidth() + inscribedSize + OUT_OF_SCREEN_OFFSET ||
                    position.getY() < -inscribedSize - OUT_OF_SCREEN_OFFSET ||
                    position.getY() > Main.getGame().getWindow().getBufferHeight() + inscribedSize + OUT_OF_SCREEN_OFFSET
            ) {
                reset();
            }
        }
    }

    private void afterDie() {
        if (!isDestroyed) return;
        destroyTimer--;
        if (destroyTimer > 0) return;
        explosion.setRecurring(false);
        Main.getGame().unregister(this);
    }

    public void reset() {
        int asteroidsCount = Main.getGame().getAsteroidsCount();
        if (asteroidsCount > ASTEROIDS_MAX_COUNT) {
            Main.getGame().unregister(this);
            return;
        }

        RandomIntegerValueRange sizeRange = new RandomIntegerValueRange(ASTEROID_MIN_SIZE, (int) (ASTEROID_MAX_SIZE - asteroidsCount / ASTEROID_SIZE_BOUND_COEFFICIENT));
        int size = sizeRange.randomValue();
        int inscribedSize = size * INSCRIBED_SIZE_COEFFICIENT;
        double xOffset = XY_SPAWN_OFFSET_RANGE.randomValue();
        double yOffset = XY_SPAWN_OFFSET_RANGE.randomValue();
        xOffset = xOffset < 0 ? xOffset - inscribedSize : Main.getGame().getWindow().getBufferWidth() + xOffset + inscribedSize;
        yOffset = yOffset < 0 ? yOffset - inscribedSize : Main.getGame().getWindow().getBufferHeight() + yOffset + inscribedSize;
        double rotationSpeed = DEFAULT_ROTATION_SPEED + RandomValueRange.RND.nextDouble(ROTATION_SPEED_RND_BOUND);
        boolean rightRotation = RandomValueRange.RND.nextBoolean();
        double speedIncrease = RandomValueRange.RND.nextDouble((double) COEFFICIENT_TO_CALCULATE_MOVEMENT_SPEED_INCREASE/size);
        double speedDecrease = COEFFICIENT_TO_CALCULATE_MOVEMENT_SPEED_DECREASE * ((double) asteroidsCount/ASTEROIDS_MAX_COUNT);
        double inertiaVectorScale = ASTEROID_MIN_MOVEMENT_SPEED + speedIncrease - speedDecrease;
        Vector inertia = new Vector(
                RandomValueRange.RND.nextInt(Main.getGame().getWindow().getBufferWidth()) - xOffset,
                RandomValueRange.RND.nextInt(Main.getGame().getWindow().getBufferHeight()) - yOffset
        )
                .normalize()
                .multiply(inertiaVectorScale);
        Vector position = new Vector(xOffset, yOffset);

        this.size = size;
        this.position = position;
        this.rightRotation = rightRotation;
        this.rotationSpeed = rotationSpeed;
        this.inertia = inertia;
        this.shape = ShapeManager.generate(size);
        this.zIndex = DEFAULT_MAX_Z_INDEX - size;
        this.health = (int) (size / ASTEROID_HEALTH_CALCULATE_COEFFICIENT);
        this.asteroidColor = shape.getFillColor();
        destroyTimer = size * 2;
    }

    public LineShape getShape() {
        return shape;
    }

    public Vector getPosition() {
        return position;
    }

    public boolean isNotDestroyed() {
        return !isDestroyed;
    }

    public int getSize() {
        return size;
    }

    public Vector getInertia() {
        return inertia;
    }

}
