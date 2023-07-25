package ru.robert_grammy.astro_space.game;

import ru.robert_grammy.astro_space.engine.Vector;
import ru.robert_grammy.astro_space.utils.QMath;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class LineShape {
    private final Set<Vector> points = new HashSet<>();
    private Vector xBasisVector = new Vector(1, 0);
    private int degree;
    private Color fillColor;
    private Color lineColor;
    private float lineWeight;

    public LineShape(int degree, Color fillColor, Color lineColor, float lineWeight, Vector... points) {
        this.degree = degree;
        this.fillColor = fillColor;
        this.lineColor = lineColor;
        this.lineWeight = lineWeight;
        this.points.addAll(Arrays.asList(points));
    }

    public Set<Vector> getPoints() {
        return points;
    }

    public Set<Vector> getRealPoints(Vector position) {
        return points.stream().map(Vector::clone).map(point -> point.fromBasis(xBasisVector.clone().rotate(degree)).add(position)).collect(Collectors.toSet());
    }

    public void rotate(int degree) {
        degree += this.degree;
        setRotation(degree);
    }

    public void setRotation(int degree) {
        if (degree >= 360) degree -= 360;
        if (degree < 0) degree += 360;
        this.degree = degree;
    }

    public int getRotation() {
        return degree;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineWeight(int lineWeight) {
        this.lineWeight = lineWeight;
    }

    public float getLineWeight() {
        return lineWeight;
    }
}
