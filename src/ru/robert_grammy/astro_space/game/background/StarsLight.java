package ru.robert_grammy.astro_space.game.background;

import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.game.Game;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StarsLight implements Renderable, Updatable {

    private final List<Star> stars = new ArrayList<>();

    public StarsLight() {
        for (int i = 0; i<100; i++) {
            stars.add(new Star());
        }
    }

    @Override
    public void render(Graphics2D graphics) {
        stars.forEach(star -> star.render(graphics));
    }

    @Override
    public void setZIndex(int z) {}

    @Override
    public int getZIndex() {
        return 0;
    }

    @Override
    public void update(Game game) {
        stars.forEach(star -> star.update(game));
    }
}
