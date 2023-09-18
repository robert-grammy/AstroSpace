package ru.robert_grammy.astro_space.game.shape;

import ru.robert_grammy.astro_space.engine.Vector;
import ru.robert_grammy.astro_space.utils.QMath;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum ShapeManager {

    PLAYER_DEFAULT(new LineShape(180, Color.BLACK, Color.WHITE, 2, 8, new Vector(0,2.5), new Vector(1.5,-1.5), new Vector(-1.5,-1.5))),
    PLAYER_PLANE(new LineShape(180, Color.GRAY, Color.LIGHT_GRAY, 2, 4, new Vector(-.5, 8), new Vector(.5, 8), new Vector(1.5, 7), new Vector(1.5, 1), new Vector(10.5, 1), new Vector(10.5, -2), new Vector(1.5, -1), new Vector(1.5, -6), new Vector(2.5, -6), new Vector(2.5, -8), new Vector(0, -7), new Vector(-2.5, -8), new Vector(-2.5, -6), new Vector(-1.5, -6), new Vector(-1.5, -1), new Vector(-10.5, -2), new Vector(-10.5, 1), new Vector(-1.5, 1), new Vector(-1.5, 7)));

    private static final Random rnd = new Random();

    private final LineShape shape;

    ShapeManager(LineShape shape) {
        this.shape = shape;
    }

    public LineShape getShape() {
        return shape;
    }

    public static LineShape generate(int size) {
        List<Vector> points = new ArrayList<>();
        int extraPoints = (size / 15) + 1;
        size *= 5;
        double offset = size * 0.75;
        for (int i = 0; i < 6 * extraPoints; i++) {
            double multiply = rnd.nextDouble(offset, size);
            int coefficient = 7 * extraPoints;
            double radians = (Math.PI / 3) / extraPoints * i + (rnd.nextDouble(( -Math.PI / coefficient), Math.PI / coefficient));
            int degree = (int) (radians * 180 / Math.PI);
            Vector point = new Vector(QMath.cos(degree), QMath.sin(degree)).multiply(multiply);
            points.add(point);
        }
        LineShape shape = new LineShape(180, Color.BLACK, Color.WHITE, 2, 1, points);
        return shape;
    }

}
