package ru.robert_grammy.astro_space.game.player;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.geometry.StraightLine;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.engine.geometry.Vector;
import ru.robert_grammy.astro_space.engine.sound.GameSound;
import ru.robert_grammy.astro_space.engine.sound.Sound;
import ru.robert_grammy.astro_space.game.asteroid.Asteroid;
import ru.robert_grammy.astro_space.game.powerup.PowerUp;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Bullet implements Renderable, Updatable {

    private static final Color FILL_COLOR = Color.BLACK;
    private static final Color OUTLINE_COLOR = Color.WHITE;
    private static final Sound DAMAGE_SOUND = GameSound.DAMAGE.get();
    private static final int Z_INDEX = 10;
    private static final int DEFAULT_BULLET_SIZE = 7;
    private static final int SCORE_DEPENDENT_ON_ASTEROID_COEFFICIENT = 4;
    private static final double MAX_SCORE_DEPENDENT_ON_ASTEROID_SIZE = 50.0;
    private static final double MAX_SCORE_DEPENDENT_ON_ASTEROID_MOVEMENT_SPEED = .75;
    private int size = 7;
    private final Stroke stroke;
    private final Vector position;
    private final Vector movement;
    private final Vector lastPosition;
    private final boolean doubleDamage;

    public Bullet(Vector position, Vector movement) {
        this.position = position;
        this.movement = movement;
        lastPosition = position.clone();
        doubleDamage = Main.getGame().getPlayer().onPower(PowerUp.PowerType.DOUBLE_DAMAGE);
        size = doubleDamage ? size * 2 : size;
        float lineWeight = doubleDamage ? 1 : 2;
        stroke = new BasicStroke(lineWeight, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    @Override
    public void render(Graphics2D graphics) {
        graphics.setStroke(stroke);
        graphics.setColor(FILL_COLOR);
        graphics.fillOval((int) position.getX() - (size / 2), (int) position.getY() - (size / 2), size, size);
        graphics.setColor(OUTLINE_COLOR);
        graphics.drawOval((int) position.getX() - (size / 2), (int) position.getY() - (size / 2), size, size);
    }

    public void destroy() {
        Main.getGame().unregister(this);
    }

    @Override
    public int getZIndex() {
        return Z_INDEX;
    }

    @Override
    public void update() {
        lastPosition.setXY(position);
        position.add(movement);
        if (position.getX() < 0 || position.getX() > Main.getGame().getWindow().getBufferWidth() || position.getY() < 0 || position.getY() > Main.getGame().getWindow().getBufferHeight()) destroy();
        Vector futurePosition = position.clone().add(movement);
        if (Optional.ofNullable(futurePosition).isEmpty()) return;
        StraightLine bulletLine = new StraightLine(futurePosition, lastPosition);
        Stream<Updatable> stream = Main.getGame().getUpdatables().stream();
        stream.filter(updatable -> updatable instanceof Asteroid).map(updatable -> (Asteroid) updatable).filter(Asteroid::isNotDestroyed).forEach(asteroid -> {
            List<Vector> asteroidPoints = asteroid.getShape().getRealPoints(asteroid.getPosition());
            for (int i = 0; i<asteroidPoints.size(); i++) {
                Vector asteroidA = asteroidPoints.get(i);
                Vector asteroidB = asteroidPoints.get(i + 1 == asteroidPoints.size() ? 0 : i + 1);
                StraightLine asteroidLine = new StraightLine(asteroidA, asteroidB);
                if (asteroidLine.isParallel(bulletLine)) continue;
                Vector cross = asteroidLine.getPointIntersectionLines(bulletLine);
                boolean outPlayerYRange = cross.getY() < Math.min(futurePosition.getY(), lastPosition.getY()) || cross.getY() > Math.max(futurePosition.getY(), lastPosition.getY());
                boolean outPlayerXRange = cross.getX() < Math.min(futurePosition.getX(), lastPosition.getX()) || cross.getX() > Math.max(futurePosition.getX(), lastPosition.getX());
                boolean outAsteroidYRange = cross.getY() < Math.min(asteroidA.getY(), asteroidB.getY()) || cross.getY() > Math.max(asteroidA.getY(), asteroidB.getY());
                boolean outAsteroidXRange = cross.getX() < Math.min(asteroidA.getX(), asteroidB.getX()) || cross.getX() > Math.max(asteroidA.getX(), asteroidB.getX());
                if (outPlayerYRange || outPlayerXRange || outAsteroidYRange || outAsteroidXRange) {
                    Vector centerOffsetVector = position.clone().subtract(asteroid.getPosition());
                    if (centerOffsetVector.length() > asteroid.getSize() * 3) continue;
                }
                destroy();
                double score = (MAX_SCORE_DEPENDENT_ON_ASTEROID_SIZE - asteroid.getSize()) / SCORE_DEPENDENT_ON_ASTEROID_COEFFICIENT + (double) (size / DEFAULT_BULLET_SIZE) + asteroid.getInertia().length() / MAX_SCORE_DEPENDENT_ON_ASTEROID_MOVEMENT_SPEED;
                Main.getGame().addScore((int) Math.floor(score));
                DAMAGE_SOUND.play();
                asteroid.damage();
                if (doubleDamage) asteroid.damage();
            }
        });
    }

}
