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
        Color outline = new Color(0x796279 + ((int) alpha << 24), true);
        Color fill = new Color(0x5D5A67 + ((int) alpha << 24), true);
        graphics.setColor(outline);
        graphics.drawOval((int) position.getX(), (int) position.getY(), size, size);
        graphics.setColor(fill);
        graphics.fillOval((int) position.getX(), (int) position.getY(), size, size);
    }

    @Override
    public void setZIndex(int z) {}

    @Override
    public int getZIndex() {
        return 0;
    }

    @Override
    public void update(Game game) {
        alpha -= fadeSpeed;
        if (alpha <= 0) {
            randomize();
        }
    }

    private void randomize() {
        size = rnd.nextInt(5, 40);
        alpha = rnd.nextInt(128, 225);
        position = new Vector(rnd.nextInt(-10,1290), rnd.nextInt(-10,730));
        fadeSpeed = rnd.nextDouble(5);
    }

}
