package ru.robert_grammy.astro_space.game;

import ru.robert_grammy.astro_space.engine.KeyBoard;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.engine.Vector;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;

public class Player implements Updatable, Renderable {

    private LineShape shape;
    private int zIndex = 5;

    private Vector position;
    private Vector movement = new Vector(0, 0);

    public Player(Vector position) {
        this.position = position;
        Vector[] points = new Vector[]{new Vector(0,2).multiply(30), new Vector(1,-1).multiply(30), new Vector(-1,-1).multiply(30)};
        shape = new LineShape(180, Color.GRAY, Color.LIGHT_GRAY, 4, points);
    }

    public Player(float x,float y) {
        this(new Vector(x,y));
    }

    @Override
    public void render(Graphics2D graphics) {
        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        final Vector[] firstPoint = {null};
        shape.getRealPoints(position).forEach(point -> {
            if (firstPoint[0] == null) {
                firstPoint[0] = point;
                path.moveTo(firstPoint[0].getX(), firstPoint[0].getY());
            }
            path.lineTo(point.getX(), point.getY());
        });
        path.lineTo(firstPoint[0].getX(), firstPoint[0].getY());
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
            position.subtract(Vector.UP_VECTOR);
        }
        if (keyBoard.pressed(KeyEvent.VK_RIGHT)) {
            shape.rotate(3);
        }
        if (keyBoard.pressed(KeyEvent.VK_DOWN)){
            position.add(Vector.UP_VECTOR);
        }
        if (keyBoard.pressed(KeyEvent.VK_LEFT)) {
            shape.rotate(-3);
        }
    }

}
