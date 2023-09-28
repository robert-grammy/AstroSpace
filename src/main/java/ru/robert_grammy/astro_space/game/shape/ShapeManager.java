package ru.robert_grammy.astro_space.game.shape;

import ru.robert_grammy.astro_space.engine.geometry.Vector;
import ru.robert_grammy.astro_space.utils.QMath;
import ru.robert_grammy.astro_space.utils.rnd.RandomValueRange;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public enum ShapeManager {

    PLAYER_DEFAULT(new LineShape(LineShape.DEFAULT_ROTATION, Color.BLACK, Color.WHITE, LineShape.DEFAULT_LINE_WEIGHT, 8,
            new Vector(0,2.5),
            new Vector(1.5,-1.5),
            new Vector(-1.5,-1.5))
    ),
    PLAYER_STARSHIP(new LineShape(LineShape.DEFAULT_ROTATION, Color.BLACK, Color.WHITE, LineShape.DEFAULT_LINE_WEIGHT, 2.5,
            new Vector(0,8),
            new Vector(1, 7),
            new Vector(2, 1),
            new Vector(5,-3),
            new Vector(1, -2),
            new Vector(2, -5),
            new Vector(0,-4),
            new Vector(-2, -5),
            new Vector(-1, -2),
            new Vector(-5, -3),
            new Vector(-2, 1),
            new Vector(-1, 7))
    );

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
            double multiply = RandomValueRange.RND.nextDouble(offset, size);
            int coefficient = 7 * extraPoints;
            double radians = (Math.PI / 3) / extraPoints * i + (RandomValueRange.RND.nextDouble(( -Math.PI / coefficient), Math.PI / coefficient));
            int degree = (int) Math.toDegrees(radians);
            Vector point = new Vector(QMath.cos(degree), QMath.sin(degree)).multiply(multiply);
            points.add(point);
        }
        return new LineShape(LineShape.DEFAULT_ROTATION, Color.BLACK, Color.WHITE, LineShape.DEFAULT_LINE_WEIGHT, 1, points);
    }

}
