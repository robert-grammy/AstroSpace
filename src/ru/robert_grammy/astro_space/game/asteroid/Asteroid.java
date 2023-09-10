package ru.robert_grammy.astro_space.game.asteroid;

import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.engine.Vector;
import ru.robert_grammy.astro_space.game.Game;
import ru.robert_grammy.astro_space.game.shape.AsteroidShape;
import ru.robert_grammy.astro_space.game.shape.LineShape;

import java.awt.*;
import java.awt.geom.GeneralPath;

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
        position.add(inertia);
        shape.rotate(rightRotation ? rotationSpeed : -rotationSpeed);
        //TODO Реализация столкновений с игроком
    }

}
