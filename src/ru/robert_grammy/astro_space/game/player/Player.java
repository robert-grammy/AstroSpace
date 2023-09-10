package ru.robert_grammy.astro_space.game.player;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.*;
import ru.robert_grammy.astro_space.game.shape.PlayerShape;
import ru.robert_grammy.astro_space.game.shape.LineShape;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

public class Player implements Updatable, Renderable {

    private static final int SHOOT_DEFAULT_TIME = 25;

    private LineShape shape;
    private int zIndex = 100;

    private final Vector position;
    private Vector movement = new Vector(0, 0);
    private int shootTimer = 0;

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
        Rectangle lineBound = new Rectangle((int) (position.getX() - 150), (int) (position.getY() - 150), 300, 300);
        if (Main.game.getLogger().isDrawPlayerShapeLines()) {
            shape.getShapeRealLines(position).forEach(line -> line.draw(graphics, lineBound, Color.YELLOW, 1));
        }
        if (Main.game.getLogger().isDrawLineBound()) {
            graphics.setColor(Color.GREEN);
            graphics.drawRect(lineBound.x, lineBound.y, lineBound.width, lineBound.height);
        }
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
    public void update() {
        control(Main.game.getWindow().getKeyboard());
        movement();
    }

    private void control(Keyboard keyboard) {
        if (keyboard.pressed(KeyEvent.VK_RIGHT)) {
            shape.rotate(3);
        }
        if (keyboard.pressed(KeyEvent.VK_LEFT)) {
            shape.rotate(-3);
        }

        if (keyboard.pressed(KeyEvent.VK_DOWN)){
            movement.multiply(.965);
        }
        if (keyboard.pressed(KeyEvent.VK_UP)) {
            movement.multiply(.975).add(getDirection().multiply(.15));
        }

        double length = movement.length();
        if (length <= .135) movement = new Vector(0,0);

        if (keyboard.pressed(KeyEvent.VK_SPACE)) {
            if (!keyboard.isMemorized(KeyEvent.VK_SPACE) && shootTimer <= 0) {
                keyboard.memorizePress(KeyEvent.VK_SPACE);
                shootTimer = SHOOT_DEFAULT_TIME;
                Vector firstRealPoint = shape.getRealPoints(position).get(0);
                Vector bulletMovement = new Vector(firstRealPoint.getX() - position.getX(), firstRealPoint.getY() - position.getY());
                Bullet bullet = new Bullet(firstRealPoint.add(bulletMovement.normalize().multiply(5)), bulletMovement);
                Main.game.register(bullet);
            }
        }

        if (shootTimer > 0) shootTimer--;
    }

    private void movement() {
        position.add(movement);
    }

    private Vector getDirection() {
        return shape.getXBasisVector().clone().rotate(90).rotate(shape.getRotation());
    }

}
