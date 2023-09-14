package ru.robert_grammy.astro_space.game.player;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.StraightLine;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.engine.Vector;
import ru.robert_grammy.astro_space.game.asteroid.Asteroid;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Bullet implements Renderable, Updatable {

    private int zIndex = 10;
    private Color fillColor = Color.BLACK;
    private Color outlineColor = Color.WHITE;
    private float lineWeight = 1;
    private int size = 7;

    private Vector lastPosition;
    private Vector position;

    private Vector movement;

    public Bullet(Vector position, Vector movement) {
        this.position = position;
        this.movement = movement;
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
        Main.game.unregister(this);
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
        if (position.getX() < 0 || position.getX() > Main.game.getWindow().getCanvasWidth() || position.getY() < 0 || position.getY() > Main.game.getWindow().getCanvasHeight()) destroy();
        Vector futurePosition = position.clone().add(movement);
        if (Optional.ofNullable(futurePosition).isEmpty()) return;
        StraightLine bulletLine = new StraightLine(futurePosition, lastPosition);
        Stream<Updatable> stream = Main.game.getUpdatables().stream();
        stream.filter(updatable -> updatable instanceof Asteroid).map(updatable -> (Asteroid) updatable).forEach(asteroid -> {
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
                if (outPlayerYRange || outPlayerXRange || outAsteroidYRange || outAsteroidXRange) continue;
                destroy();
                asteroid.damage();
            }
        });
    }
}
