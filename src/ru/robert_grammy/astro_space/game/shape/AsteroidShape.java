package ru.robert_grammy.astro_space.game.shape;

import ru.robert_grammy.astro_space.engine.Vector;
import ru.robert_grammy.astro_space.utils.QMath;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class AsteroidShape {

    private final static Random rnd = new Random();
    private final static Vector base = new Vector(1,0);

    private AsteroidShape() {}

    public static LineShape generate(int size) {
        List<Vector> points = new ArrayList<>();
        int extraPoints = size / 15;
        size *= 5;
        int offset = (int) (size * 0.75);
        for (int i = 0; i < 6 * extraPoints; i++) {
            int multiply = rnd.nextInt(offset, size);
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
