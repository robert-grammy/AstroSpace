package ru.robert_grammy.astro_space.game.player;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.StraightLine;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.engine.Vector;
import ru.robert_grammy.astro_space.game.asteroid.Asteroid;
import ru.robert_grammy.astro_space.game.powerup.PowerUp;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Bullet implements Renderable, Updatable {

    private int zIndex = 10;
    private final Color fillColor = Color.BLACK;
    private final Color outlineColor = Color.WHITE;
    private float lineWeight = 1;
    private int size = 7;
    private final boolean doubleDamage;

    private Vector lastPosition;
    private Vector position;

    private Vector movement;

    public Bullet(Vector position, Vector movement) {
        this.position = position;
        this.movement = movement;
        doubleDamage = Main.getGame().getPlayer().onPower(PowerUp.PowerType.DOUBLE_DAMAGE);
        size = doubleDamage ? size*2 : size;
        lineWeight = doubleDamage ? lineWeight*2 : lineWeight;
    }

    @Override
    public void render(Graphics2D graphics) {
        Stroke stroke = graphics.getStroke();
        graphics.setColor(fillColor);
        graphics.fillOval((int) position.getX(), (int) position.getY(), size, size);
        graphics.setStroke(new BasicStroke(lineWeight, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.setColor(outlineColor);
        graphics.drawOval((int) position.getX() - size/2, (int) position.getY() - size/2, size, size);
        graphics.setStroke(stroke);
        graphics.setColor(Color.BLACK);
    }

    public void destroy() {
        Main.getGame().unregister(this);
    }

    @Override
    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void update() {
        lastPosition = position.clone();
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
                    if (centerOffsetVector.length() > asteroid.getSize()*3) continue;
                }
                destroy();
                double score = (50.0 - asteroid.getSize())/4 + size/15.0 + asteroid.getInertia().length()/0.75;
                Main.getGame().addScore((int) Math.floor(score));
                asteroid.damage();
                if (doubleDamage) asteroid.damage();
            }
        });
    }

}
