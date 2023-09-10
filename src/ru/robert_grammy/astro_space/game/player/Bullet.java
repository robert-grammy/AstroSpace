package ru.robert_grammy.astro_space.game.player;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.engine.Vector;

import java.awt.*;

public class Bullet implements Renderable, Updatable {

    private int zIndex = 150;
    private Color fillColor = Color.BLACK;
    private Color outlineColor = Color.WHITE;
    private float lineWeight = 1;
    private int size = 7;
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
        position.add(movement);
        if (position.getX() < 0 || position.getX() > Main.game.getWindow().getWidth() || position.getY() < 0 || position.getY() > Main.game.getWindow().getHeight()) destroy();
        //TODO Реализация столкновений с астероидами
    }
}
