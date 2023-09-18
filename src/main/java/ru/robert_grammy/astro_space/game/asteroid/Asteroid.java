package ru.robert_grammy.astro_space.game.asteroid;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.StraightLine;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.engine.Vector;
import ru.robert_grammy.astro_space.game.background.ParticleGenerator;
import ru.robert_grammy.astro_space.game.player.Player;
import ru.robert_grammy.astro_space.game.powerup.PowerUp;
import ru.robert_grammy.astro_space.game.shape.LineShape;
import ru.robert_grammy.astro_space.game.shape.ShapeManager;
import ru.robert_grammy.astro_space.graphics.Window;
import ru.robert_grammy.astro_space.utils.QMath;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class Asteroid implements Renderable, Updatable {

    private static final Random rnd = new Random();

    private boolean rightRotation;
    private double rotationSpeed;
    private Vector inertia;
    private LineShape shape;
    private int zIndex;
    private int size;
    private Vector position;
    private int health;
    private boolean isDestroyed = false;
    private int destroyTimer;
    private boolean isResetImmune = true;
    private ParticleGenerator explosion;

    public Asteroid(int size, boolean rightRotation, double rotationSpeed, Vector inertia, Vector position) {
        this.size = size;
        this.position = position;
        this.rightRotation = rightRotation;
        this.rotationSpeed = rotationSpeed;
        this.inertia = inertia;
        this.shape = ShapeManager.generate(size);
        this.zIndex = 90 - size;
        this.health = (int) (size/3.5);
        destroyTimer = (int) (size*1.5);
    }

    public Asteroid() {
        this(10, true, 0.5, new Vector(1,0), new Vector(0,0));
    }

    @Override
    public void render(Graphics2D graphics) {
        if (isDestroyed) return;
        GeneralPath path = new GeneralPath();
        Vector firstPoint = null;
        for (Vector point : shape.getRealPoints(position)) {
            if (firstPoint == null) {
                firstPoint = point;
                path.moveTo(firstPoint.getX(), firstPoint.getY());
            }
            path.lineTo(point.getX(), point.getY());
        }
        firstPoint = Optional.ofNullable(firstPoint).orElse(new Vector(-1, -1));
        path.lineTo(firstPoint.getX(), firstPoint.getY());
        path.closePath();
        Stroke stroke = graphics.getStroke();
        int hexFillColor = shape.getFillColor().getRGB();
        int colorBrighter = (int) (48 * (((size/3.0) - health)/(size/3)));
        hexFillColor = hexFillColor + colorBrighter + (colorBrighter << 8) + (colorBrighter << 16) ;
        graphics.setColor(new Color(hexFillColor));
        graphics.fill(path);
        graphics.setStroke(new BasicStroke(shape.getLineWeight(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.setColor(shape.getLineColor());
        graphics.draw(path);
        graphics.setStroke(stroke);
        graphics.setColor(Color.BLACK);

        Rectangle lineBound = new Rectangle((int) (position.getX() - 5*size), (int) (position.getY() - 5*size), 10*size, 10*size);
        if (Main.getGame().getGameDebugger().isDrawAsteroidShapeLines()) {
            shape.getShapeRealLines(position).forEach(line -> line.draw(graphics, lineBound, Color.YELLOW, 1));
        }
        if (Main.getGame().getGameDebugger().isDrawAsteroidLineBound()) {
            graphics.setColor(Color.GREEN);
            graphics.setStroke(new BasicStroke(shape.getLineWeight(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphics.drawRect(lineBound.x, lineBound.y, lineBound.width, lineBound.height);
            graphics.setStroke(stroke);
        }
        if (Main.getGame().getGameDebugger().isDrawAsteroidCircleBound()) {
            graphics.setStroke(new BasicStroke(shape.getLineWeight(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            graphics.setColor(Color.RED);
            graphics.drawOval((int) (position.getX()-size*3), (int) (position.getY()-size*3), size*6, size*6);
            graphics.setStroke(stroke);
            graphics.setColor(Color.BLACK);
        }
        if (Main.getGame().getGameDebugger().isDrawAsteroidParams()) {
            Font baseFont = graphics.getFont();
            Font font = new Font(Window.FONT_NAME, Font.PLAIN, (int) (size*1.75));
            graphics.setFont(font);
            graphics.setColor(Color.BLUE);
            TextLayout textLayout = new TextLayout(String.valueOf(size), font, graphics.getFontRenderContext());
            double xTextOffset = textLayout.getBounds().getWidth()/2;
            graphics.drawString(String.valueOf(size), (int) (position.getX() - xTextOffset), (int) (position.getY()));
            graphics.setColor(Color.PINK);
            textLayout = new TextLayout(String.valueOf(health), font, graphics.getFontRenderContext());
            xTextOffset = textLayout.getBounds().getWidth()/2;
            double yTextOffset = textLayout.getBounds().getHeight()/2;
            graphics.drawString(String.valueOf(health), (int) (position.getX() - xTextOffset), (int) (position.getY() + 2*yTextOffset));
            graphics.setColor(Color.BLACK);
            graphics.setFont(baseFont);
        }
    }

    public void damage() {
        health--;
    }

    public void destroy() {
        kill();
        if (size > 8) {
            int asteroidCountBound = size/5;
            int asteroidCount = asteroidCountBound <= 2 ? 2 : rnd.nextInt(2, size/5);
            for (int i = asteroidCount; i>0; i--) {
                Vector position = new Vector(rnd.nextInt((int) this.position.getX(), (int) (this.position.getX() + size)), rnd.nextInt((int) this.position.getY(), (int) (this.position.getY() + size)));
                int size = this.size / asteroidCount;
                size = Math.max(size, 3);
                double rotationSpeed = 1 + rnd.nextDouble(0.5);
                boolean rightRotation = rnd.nextDouble() < 0.5;
                int degree = (int) (Math.pow(-1, Math.round(1 + rnd.nextDouble())) * 45 + rnd.nextDouble(-150, 150));
                Vector inertia = new Vector(this.inertia.getX() * QMath.cos(degree) - this.inertia.getY() * QMath.sin(degree), this.inertia.getX() * QMath.sin(degree) + this.inertia.getY() * QMath.cos(degree));
                Asteroid asteroid = new Asteroid(size, rightRotation, rotationSpeed, inertia, position);
                Main.getGame().register(asteroid);
            }
        }
    }

    public void kill() {
        if (isDestroyed) return;
        isDestroyed = true;
        Rectangle explosionBound = new Rectangle((int) (position.getX() - size*3), (int) (position.getY() - size*3), size*6, size*6);
        explosion = new ParticleGenerator(50, 100, explosionBound, 15, 40, 30, 200, 2, 5, 0x7C2B2B);
        Main.getGame().register(explosion);
    }

    @Override
    public void setZIndex(int z) {}

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void update() {
        if (!isDestroyed) {
            movement();
            playerCollision();
            process();
        }
        afterDie();
    }

    private void movement() {
        if (Main.getGame().getPlayer().onPower(PowerUp.PowerType.FREEZER)) return;
        position.add(inertia);
        shape.rotate(rightRotation ? rotationSpeed : -rotationSpeed);
    }

    private void playerCollision() {
        Player player = Main.getGame().getPlayer();
        if (!player.isDestroyed()) {
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
                    if (player.onPower(PowerUp.PowerType.INVINCIBLE)) {
                        kill();
                        return;
                    }
                    player.destroy();
                    return;
                }
            }
        }
    }

    private void process() {
        if (health <= 0) {
            destroy();
        }
        if (isResetImmune) {
            if (position.getX() >= 0 || position.getX() <= Main.getGame().getWindow().getBufferWidth() || position.getY() >= 0 || position.getY() <= Main.getGame().getWindow().getBufferHeight()) {
                isResetImmune = false;
            }
        } else {
            if (position.getX() < -size*10 - 10 || position.getX() > Main.getGame().getWindow().getBufferWidth() + size*10 + 10 || position.getY() < -size*10 - 10 || position.getY() > Main.getGame().getWindow().getBufferHeight() + size*10 + 10) {
                reset();
            }
        }
    }

    private void afterDie() {
        if (!isDestroyed) return;
        destroyTimer--;
        if (destroyTimer > 0) return;
        explosion.setRecurring(false);
        Main.getGame().unregister(this);
    }

    public void reset() {
        int asteroidsCount = Main.getGame().getAsteroidsCount();

        if (asteroidsCount > 40) {
            Main.getGame().unregister(this);
            return;
        }

        int size = rnd.nextInt(7, (int) (47 - asteroidsCount/1.20));
        int boundLineSize = size * 10;
        double xOffset = rnd.nextDouble(-100, 100);
        double yOffset = rnd.nextDouble(-100, 100);
        xOffset = xOffset < 0 ? xOffset - boundLineSize : Main.getGame().getWindow().getBufferWidth() + xOffset + boundLineSize;
        yOffset = yOffset < 0 ? yOffset - boundLineSize : Main.getGame().getWindow().getBufferHeight() + yOffset + boundLineSize;
        double rotationSpeed = 0.95 + rnd.nextDouble(0.75);
        boolean rightRotation = rnd.nextDouble() < 0.5;
        Vector inertia = new Vector(rnd.nextInt(Main.getGame().getWindow().getBufferWidth()) - xOffset, rnd.nextInt(Main.getGame().getWindow().getBufferHeight()) - yOffset).normalize().multiply(rnd.nextDouble(35.0/size) + 0.60 - (0.35 * (asteroidsCount/40.0)));
        Vector position = new Vector(xOffset, yOffset);

        this.size = size;
        this.position = position;
        this.rightRotation = rightRotation;
        this.rotationSpeed = rotationSpeed;
        this.inertia = inertia;
        this.shape = ShapeManager.generate(size);
        this.zIndex = 90 - size;
        this.health = (int) (size/3.5);
        destroyTimer = size*2;
    }

    public LineShape getShape() {
        return shape;
    }

    public Vector getPosition() {
        return position;
    }

    public boolean isNotDestroyed() {
        return !isDestroyed;
    }

    public int getSize() {
        return size;
    }

    public Vector getInertia() {
        return inertia;
    }

}
