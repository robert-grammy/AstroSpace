package ru.robert_grammy.astro_space.game.background;

import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.engine.Vector;
import ru.robert_grammy.astro_space.game.Game;

import java.awt.*;
import java.util.Random;

public class Star implements Renderable, Updatable {

    private static final Random rnd = new Random();

    private int size;
    private double alpha;
    private double fadeSpeed;
    private Vector position;

    public Star() {
        randomize();
    }

    @Override
    public void render(Graphics2D graphics) {
        Color fill = new Color(0xAB9AB2 + ((int) alpha << 24), true);
        graphics.setColor(fill);
        graphics.fillOval((int) position.getX(), (int) position.getY(), size, size);
        graphics.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    }

    @Override
    public void setZIndex(int z) {}

    @Override
    public int getZIndex() {
        return 0;
    }

    @Override
    public void update() {
        alpha -= fadeSpeed;
        if (alpha <= 0) {
            randomize();
        }
    }

    private void randomize() {
        size = rnd.nextInt(3, 20);
        alpha = rnd.nextInt(80, 220);
        position = new Vector(rnd.nextInt(-10,1290), rnd.nextInt(-10,730));
        fadeSpeed = rnd.nextDouble(5);
    }

}
