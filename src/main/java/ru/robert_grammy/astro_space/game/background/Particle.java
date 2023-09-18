package ru.robert_grammy.astro_space.game.background;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.engine.Vector;
import ru.robert_grammy.astro_space.game.powerup.PowerUp;

import java.awt.*;
import java.util.Optional;
import java.util.Random;

public class Particle implements Renderable, Updatable {

    private static final Random rnd = new Random();

    private int sizeOrigin = 3;
    private int sizeBound = 20;
    private double alphaOrigin = 80;
    private double alphaBound = 220;
    private Rectangle bound = new Rectangle(-10, -10, Main.getGame().getWindow().getBufferWidth() + 10, Main.getGame().getWindow().getBufferHeight() + 10);
    private double fadeSpeedOrigin = 0.025;
    private double fadeSpeedBound = 5;
    private int hexColor = 0xAB9AB2;

    private int zIndex = 0;
    private boolean isRecurring = true;

    private int size;
    private double alpha;
    private double fadeSpeed;
    private Vector position;
    private ParticleGenerator generator;

    public Particle(ParticleGenerator generator) {
        this.generator = generator;
        randomize();
    }

    public Particle(Rectangle bound, int sizeOrigin, int sizeBound, double alphaOrigin, double alphaBound, double fadeSpeedOrigin, double fadeSpeedBound, int hexColor) {
        this.bound = bound;
        this.sizeOrigin = sizeOrigin;
        this.sizeBound = sizeBound;
        this.alphaOrigin = alphaOrigin;
        this.alphaBound = alphaBound;
        this.fadeSpeedOrigin = fadeSpeedOrigin;
        this.fadeSpeedBound = fadeSpeedBound;
        this.hexColor = hexColor;
        randomize();
    }

    public Particle(ParticleGenerator generator, Rectangle bound, int sizeOrigin, int sizeBound, double alphaOrigin, double alphaBound, double fadeSpeedOrigin, double fadeSpeedBound, int hexColor) {
        this(bound, sizeOrigin, sizeBound, alphaOrigin, alphaBound, fadeSpeedOrigin, fadeSpeedBound, hexColor);
        this.generator = generator;
        randomize();
    }

    @Override
    public void render(Graphics2D graphics) {
        Color fill = new Color(hexColor + ((int) (alpha < 0 ? 0 : alpha) << 24), true);
        graphics.setColor(fill);
        graphics.fillOval((int) position.getX(), (int) position.getY(), size, size);
        graphics.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    }

    @Override
    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public void setRecurring(boolean value) {
        isRecurring = value;
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void update() {
        if (Main.getGame().getPlayer().onPower(PowerUp.PowerType.FREEZER)) return;
        alpha -= fadeSpeed;
        if (alpha <= 0) {
            if (isRecurring) {
                randomize();
            } else {
                destroy();
            }
        }
    }

    private void randomize() {
        size = rnd.nextInt(sizeOrigin, sizeBound);
        alpha = rnd.nextInt((int) alphaOrigin, (int) alphaBound);
        position = new Vector(rnd.nextInt((int) bound.getX(), (int) (bound.getX() + bound.getWidth())), rnd.nextInt((int) bound.getY(), (int) (bound.getY() + bound.getHeight())));
        fadeSpeed = rnd.nextDouble(fadeSpeedOrigin, fadeSpeedBound);
    }

    private void destroy() {
        if (Optional.ofNullable(generator).isPresent()) {
            generator.remove(this);
        } else {
            Main.getGame().unregister(this);
        }
    }

}
