package ru.robert_grammy.astro_space.game.player;

import ru.robert_grammy.astro_space.engine.*;
import ru.robert_grammy.astro_space.game.Game;
import ru.robert_grammy.astro_space.game.shape.PlayerShape;
import ru.robert_grammy.astro_space.game.shape.LineShape;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Player implements Updatable, Renderable {

    private LineShape shape;
    private int zIndex = 100;

    private final Vector position;
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

        //TODO Удалить, дебаг
        List<Vector> points = shape.getRealPoints(position);
        List<StraightLine> lines = new ArrayList<>();
        for (int i = 0; i<points.size(); i++) {
            Vector a = points.get(i);
            Vector b = points.get(i + 1 == points.size() ? 0 : i + 1);
            lines.add(new StraightLine(a,b));
        }
        for (int i = 0; i<lines.size(); i++) {
            StraightLine a = lines.get(i);
            StraightLine b = lines.get(i + 1 == lines.size() ? 0 : i + 1);
            a.draw(graphics, position.clone().subtract(150,150), position.clone().add(150,150), Color.YELLOW, 1);
            Vector cross = a.getPointIntersectionLines(b);
            Color c = graphics.getColor();
            graphics.setColor(Color.RED);
            graphics.fillOval((int) cross.getX() - 3, (int) cross.getY() - 3, 6, 6);
            graphics.setColor(c);
        }
        AtomicInteger offset = new AtomicInteger(1);
        graphics.setColor(Color.WHITE);
        graphics.setFont(graphics.getFont().deriveFont(20F));
        lines.forEach(line -> graphics.drawString(line.toString(), 100, 100 + 20 * (offset.getAndIncrement())));
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
            movement.multiply(.975).add(getDirection().multiply(.15));
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
