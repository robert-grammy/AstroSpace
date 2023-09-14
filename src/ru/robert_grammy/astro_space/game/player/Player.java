package ru.robert_grammy.astro_space.game.player;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.*;
import ru.robert_grammy.astro_space.game.background.Particle;
import ru.robert_grammy.astro_space.game.background.ParticleGenerator;
import ru.robert_grammy.astro_space.game.shape.PlayerShape;
import ru.robert_grammy.astro_space.game.shape.LineShape;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.GeneralPath;

public class Player implements Updatable, Renderable {

    private static final int SHOOT_DEFAULT_TIME = 25;

    private LineShape shape;
    private int zIndex = 100;

    private final Vector position;
    private Vector movement = new Vector(0, 0);
    private int shootTimer = 0;
    private boolean isDestroyed = false;
    private int destroyTimer = 35;
    private ParticleGenerator explosion;

    public Player(Vector position) {
        this.position = position;
        shape = PlayerShape.PLAYER_DEFAULT.getShape();
    }

    public Player(float x,float y) {
        this(new Vector(x,y));
    }

    @Override
    public void render(Graphics2D graphics) {
        if (!isDestroyed) {
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
        Rectangle lineBound = new Rectangle((int) (position.getX() - 100), (int) (position.getY() - 100), 200, 200);
        if (Main.getGame().getGameDebugger().isDrawPlayerShapeLines()) {
            shape.getShapeRealLines(position).forEach(line -> line.draw(graphics, lineBound, Color.YELLOW, 1));
        }
        if (Main.getGame().getGameDebugger().isDrawPlayerLineBound()) {
            graphics.setColor(Color.GREEN);
            Stroke stroke = graphics.getStroke();
            graphics.setStroke(new BasicStroke(shape.getLineWeight(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphics.drawRect(lineBound.x, lineBound.y, lineBound.width, lineBound.height);
            graphics.setStroke(stroke);
        }
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
        if (!isDestroyed) {
            control(Main.getGame().getWindow().getKeyboard());
            movement();
        }
        afterDie();
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
            Vector particleCenter = position.clone().subtract(getDirection().normalize().multiply(12));
            Rectangle trailBound = new Rectangle((int) (particleCenter.getX() - 7), (int) (particleCenter.getY() - 7), 14, 14);
            Particle particle = new Particle(trailBound, 5, 15, 50, 180, 1, 3, 0xFFAA88);
            particle.setZIndex(95);
            particle.setRecurring(false);
            Main.getGame().register(particle);
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
                Main.getGame().register(bullet);
            }
        }

        if (shootTimer > 0) shootTimer--;
    }

    private void afterDie() {
        if (!isDestroyed) return;
        destroyTimer--;
        if (destroyTimer > 0) return;
        explosion.setRecurring(false);
        Main.getGame().unregister(this);
    }

    private void movement() {
        position.add(movement);
        if (position.getX() < 0 || position.getX() > Main.getGame().getWindow().getCanvasWidth() || position.getY() < 0 || position.getY() > Main.getGame().getWindow().getCanvasHeight()) destroy();
    }

    public Vector getDirection() {
        return shape.getXBasisVector().clone().rotate(90).rotate(shape.getRotation());
    }

    public void destroy() {
        if (isDestroyed) return;
        isDestroyed = true;
        Rectangle explosionBound = new Rectangle((int) (position.getX() - 40), (int) (position.getY() - 40), 80, 80);
        explosion = new ParticleGenerator(50, 100, explosionBound, 15, 40, 30, 200, 2, 5, 0xAA6633);
        Main.getGame().register(explosion);
    }

    public LineShape getShape() {
        return shape;
    }

    public Vector getPosition() {
        return position;
    }

}
