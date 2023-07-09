package ru.robert_grammy.astro_space.engine;

import java.awt.*;

public interface Renderable {

    void render(Graphics2D graphics);

    void setZIndex(int z);

    int getZIndex();

}
