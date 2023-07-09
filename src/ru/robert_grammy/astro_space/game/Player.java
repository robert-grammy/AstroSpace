package ru.robert_grammy.astro_space.game;

import ru.robert_grammy.astro_space.engine.KeyBoard;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.engine.Vector;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import java.util.Arrays;
import java.util.Set;

public class Player implements Updatable, Renderable {

    private LineShape shape;
    private int zIndex = 5;

    private float x, y;
    private Vector movement = new Vector(0, 0);

    public Player(float x,float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void render(Graphics2D graphics) {
        GeneralPath path = new GeneralPath();
        path.moveTo(shape.getCenter().getX(), shape.getCenter().getY());
        final Vector[] firstPoint = {null};
        shape.getRealPoints().forEach(point -> {
            if (firstPoint[0] == null) firstPoint[0] = point;
            path.lineTo(point.getX(), point.getY());
        });
        path.moveTo(firstPoint[0].getX(), firstPoint[0].getY());
        path.closePath();
        Stroke stroke = graphics.getStroke();
        graphics.setColor(shape.getFillColor());
        graphics.fill(path);
        graphics.setStroke(new BasicStroke(shape.getLineWeight()));
        graphics.setColor(shape.getLineColor());
        graphics.draw(path);
        graphics.setStroke(stroke);
        graphics.setColor(Color.BLACK);
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
    public void update(Game game) {
        KeyBoard keyBoard = game.getWindow().getKeyBoard();
        if (keyBoard.pressed(KeyEvent.VK_UP)) {

        }
    }

}
