package ru.robert_grammy.astro_space.game.player;

import ru.robert_grammy.astro_space.engine.KeyBoard;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.engine.Vector;
import ru.robert_grammy.astro_space.game.Game;
import ru.robert_grammy.astro_space.game.shape.PlayerShape;
import ru.robert_grammy.astro_space.game.shape.LineShape;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;

public class Player implements Updatable, Renderable {

    private LineShape shape;
    private int zIndex = 100;

    private Vector position;
    private Vector movement = new Vector(0, 0);

    public Player(Vector position) {
        this.position = position;
        shape = PlayerShape.PLAYER_DEFAULT.getShape();
    }

    public Player(float x,float y) {
        this(new Vector(x,y));
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
        control(game.getWindow().getKeyBoard());
        movement();
    }

    private void control(KeyBoard keyBoard) {
        if (keyBoard.pressed(KeyEvent.VK_RIGHT)) {
            shape.rotate(3);
        }
        if (keyBoard.pressed(KeyEvent.VK_LEFT)) {
            shape.rotate(-3);
        }

        if (keyBoard.pressed(KeyEvent.VK_DOWN)){
            movement.multiply(.965);
        }
        if (keyBoard.pressed(KeyEvent.VK_UP)) {
            movement.multiply(.965).add(getDirection().multiply(.15));
        }

        double length = movement.length();
        if (length <= .135) movement = new Vector(0,0);
    }

    private void movement() {
        position.add(movement);
    }

    private Vector getDirection() {
        return shape.getXBasisVector().clone().rotate(90).rotate(shape.getRotation());
    }

}
