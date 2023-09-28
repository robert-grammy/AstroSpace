package ru.robert_grammy.astro_space.game.background;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.engine.geometry.Vector;
import ru.robert_grammy.astro_space.game.powerup.PowerUp;
import ru.robert_grammy.astro_space.utils.rnd.RandomDoubleValueRange;
import ru.robert_grammy.astro_space.utils.rnd.RandomIntegerValueRange;

import java.awt.*;
import java.util.Optional;

public class Particle implements Renderable, Updatable {

    private RandomIntegerValueRange sizeRange = new RandomIntegerValueRange(3, 20);
    private RandomDoubleValueRange alphaRange = new RandomDoubleValueRange(80.0, 220.0);
    private RandomDoubleValueRange fadeSpeedRange = new RandomDoubleValueRange(.025, 5.0);
    private RandomDoubleValueRange xRange = new RandomDoubleValueRange(25.0, Main.getGame().getWindow().getBufferWidth() - 25.0);
    private RandomDoubleValueRange yRange = new RandomDoubleValueRange(25.0, Main.getGame().getWindow().getBufferHeight() - 25.0);
    private int hexColor = 0xAB9AB2;
    private int zIndex = 0;
    private boolean isRecurring = true;
    private Stroke stroke;
    private int size;
    private double alpha;
    private double fadeSpeed;
    private Vector position;
    private ParticleGenerator generator;

    public Particle(ParticleGenerator generator) {
        this.generator = generator;
        randomize();
    }

    public Particle(Rectangle bound, RandomIntegerValueRange sizeRange, RandomDoubleValueRange alphaRange, RandomDoubleValueRange fadeSpeedRange, int hexColor) {
        xRange = new RandomDoubleValueRange(bound.getX(), bound.getX() + bound.getWidth());
        yRange = new RandomDoubleValueRange(bound.getY(), bound.getY() + bound.getHeight());
        this.sizeRange = sizeRange;
        this.alphaRange = alphaRange;
        this.fadeSpeedRange = fadeSpeedRange;
        this.hexColor = hexColor;
        randomize();
    }

    public Particle(ParticleGenerator generator, Rectangle bound, RandomIntegerValueRange sizeRange, RandomDoubleValueRange alphaRange, RandomDoubleValueRange fadeSpeedRange, int hexColor) {
        this(bound, sizeRange, alphaRange, fadeSpeedRange, hexColor);
        this.generator = generator;
        randomize();
    }

    @Override
    public void render(Graphics2D graphics) {
        Color fill = new Color(hexColor + ((int) (alpha < 0 ? 0 : alpha) << 24), true);
        graphics.setColor(fill);
        graphics.fillOval((int) position.getX() - (size / 2), (int) position.getY() - (size / 2), size, size);
        graphics.setStroke(stroke);
    }

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
        size = sizeRange.randomValue();
        alpha = alphaRange.randomValue();
        position = new Vector(
                xRange.randomValue(),
                yRange.randomValue()
        );
        fadeSpeed = fadeSpeedRange.randomValue();
        stroke = new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    private void destroy() {
        if (Optional.ofNullable(generator).isPresent()) {
            generator.remove(this);
        } else {
            Main.getGame().unregister(this);
        }
    }

}
