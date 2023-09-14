package ru.robert_grammy.astro_space.game.asteroid;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.StraightLine;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.engine.Vector;
import ru.robert_grammy.astro_space.game.player.Player;
import ru.robert_grammy.astro_space.game.shape.AsteroidShape;
import ru.robert_grammy.astro_space.game.shape.LineShape;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.Optional;

public class Asteroid implements Renderable, Updatable {

    private final boolean rightRotation;
    private final double rotationSpeed;
    private final Vector inertia;
    private final LineShape shape;
    private final int zIndex;

    private Vector position;
    private int health;
    private boolean isDestroyed = false;

    public Asteroid(int size, boolean rightRotation, double rotationSpeed, Vector inertia, Vector position) {
        this.position = position;
        this.rightRotation = rightRotation;
        this.rotationSpeed = rotationSpeed;
        this.inertia = inertia;
        this.shape = AsteroidShape.generate(size);
        this.zIndex = 80 - size;
        this.health = size/3;
    }

    @Override
    public void render(Graphics2D graphics) {
        GeneralPath path = new GeneralPath();
        Vector firstPoint = null;
        for (Vector point : shape.getRealPoints(position)) {
            if (firstPoint == null) {
                firstPoint = point;
                path.moveTo(firstPoint.getX(), firstPoint.getY());
            }
            path.lineTo(point.getX(), point.getY());
        }
        path.lineTo(firstPoint.getX(), firstPoint.getY());
        path.closePath();
        Stroke stroke = graphics.getStroke();
        graphics.setColor(shape.getFillColor());
        graphics.fill(path);
        graphics.setStroke(new BasicStroke(shape.getLineWeight(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.setColor(shape.getLineColor());
        graphics.draw(path);
        graphics.setStroke(stroke);
        graphics.setColor(Color.BLACK);
    }

    public void damage() {
        health--;
    }

    public void destroy() {

    }

    @Override
    public void setZIndex(int z) {}

    @Override
    public int getZIndex() {
        return 80 - health;
    }

    @Override
    public void update() {
        movement();
        playerCollision();
        process();
    }

    private void movement() {
        position.add(inertia);
        shape.rotate(rightRotation ? rotationSpeed : -rotationSpeed);
    }

    private void playerCollision() {
        Optional<Player> optionalPlayer = Optional.ofNullable(Main.game.getPlayer());
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
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
                    player.destroy();
                }
            }
        }
    }

    private void process() {
        if (health <= 0) {
            destroy();
        }
    }

}
