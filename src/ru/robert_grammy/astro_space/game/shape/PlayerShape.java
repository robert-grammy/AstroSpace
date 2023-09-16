package ru.robert_grammy.astro_space.game.shape;

import ru.robert_grammy.astro_space.engine.Vector;

import java.awt.*;

public enum PlayerShape {

    PLAYER_DEFAULT(new LineShape(180, Color.BLACK, Color.WHITE, 2, 8, new Vector(0,2.5), new Vector(1.5,-1.5), new Vector(-1.5,-1.5))),
    PLAYER_PLANE(new LineShape(180, Color.GRAY, Color.LIGHT_GRAY, 2, 5, new Vector(-.5, 8), new Vector(.5, 8), new Vector(1.5, 7), new Vector(1.5, 1), new Vector(10.5, 1), new Vector(10.5, -2), new Vector(1.5, -1), new Vector(1.5, -6), new Vector(2.5, -6), new Vector(2.5, -8), new Vector(0, -7), new Vector(-2.5, -8), new Vector(-2.5, -6), new Vector(-1.5, -6), new Vector(-1.5, -1), new Vector(-10.5, -2), new Vector(-10.5, 1), new Vector(-1.5, 1), new Vector(-1.5, 7)));

    private final LineShape shape;

    PlayerShape(LineShape shape) {
        this.shape = shape;
    }

    public LineShape getShape() {
        return shape;
    }

}
