package ru.robert_grammy.astro_space.game.shape;

import ru.robert_grammy.astro_space.engine.geometry.StraightLine;
import ru.robert_grammy.astro_space.engine.geometry.Vector;

import java.awt.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class LineShape {

    public static final int DEFAULT_ROTATION = 180;
    public static final int DEFAULT_LINE_WEIGHT = 2;
    private static final Vector X_BASIS_VECTOR = new Vector(1, 0);
    private final List<Vector> points = new ArrayList<>();
    private final Color fillColor;
    private final Color lineColor;
    private final float lineWeight;
    private double scale;
    private double degree;

    public LineShape(double degree, Color fillColor, Color lineColor, float lineWeight, double scale, List<Vector> points) {
        this.degree = degree;
        this.fillColor = fillColor;
        this.lineColor = lineColor;
        this.lineWeight = lineWeight;
        this.scale = scale;
        this.points.addAll(points);
    }

    public LineShape(double degree, Color fillColor, Color lineColor, float lineWeight, double scale, Vector... points) {
        this.degree = degree;
        this.fillColor = fillColor;
        this.lineColor = lineColor;
        this.lineWeight = lineWeight;
        this.scale = scale;
        this.points.addAll(List.of(points));
    }

    public List<Vector> getRealPoints(Vector position) {
        return points.stream()
                .map(Vector::clone)
                .map(point -> point.multiply(scale))
                .map(point -> point.fromBasis(X_BASIS_VECTOR.clone().rotate(getRotation())).add(position))
                .toList();
    }

    public List<StraightLine> getRealLines(Vector position) {
        List<Vector> points = getRealPoints(position);
        List<StraightLine> lines = new ArrayList<>();
        for (int i = 0; i<points.size(); i++) {
            Vector a = points.get(i);
            Vector b = points.get(i + 1 == points.size() ? 0 : i + 1);
            lines.add(new StraightLine(a,b));
        }
        return lines;
    }

    public void rotate(double degree) {
        degree += this.degree;
        setRotation((int) Math.floor(degree));
    }

    public void setRotation(int degree) {
        if (degree >= 360) degree -= 360;
        if (degree < 0) degree += 360;
        this.degree = degree;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getScale() {
        return scale;
    }

    public void scale(double scale) {
        this.scale *= scale;
    }

    public int getRotation() {
        return (int) Math.floor(degree);
    }

    public Color getFillColor() {
        return fillColor;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public float getLineWeight() {
        return lineWeight;
    }

    public Vector getXBasisVector() {
        return X_BASIS_VECTOR;
    }

}
