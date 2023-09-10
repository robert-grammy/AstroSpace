package ru.robert_grammy.astro_space.game.shape;

import ru.robert_grammy.astro_space.engine.StraightLine;
import ru.robert_grammy.astro_space.engine.Vector;

import java.awt.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LineShape {
    private final List<Vector> points = new ArrayList<>();
    private Vector xBasisVector = new Vector(1, 0);
    private double degree;
    private Color fillColor;
    private Color lineColor;
    private float lineWeight;

    public LineShape(double degree, Color fillColor, Color lineColor, float lineWeight, double scale, List<Vector> points) {
        this.degree = degree;
        this.fillColor = fillColor;
        this.lineColor = lineColor;
        this.lineWeight = lineWeight;
        this.points.addAll(points.stream().map(point -> point.multiply(scale)).toList());
    }

    public LineShape(double degree, Color fillColor, Color lineColor, float lineWeight, double scale, Vector... points) {
        this.degree = degree;
        this.fillColor = fillColor;
        this.lineColor = lineColor;
        this.lineWeight = lineWeight;
        this.points.addAll(Arrays.stream(points).map(point -> point.multiply(scale)).toList());
    }

    public List<Vector> getPoints() {
        return points;
    }

    public List<Vector> getRealPoints(Vector position) {
        return points.stream().map(Vector::clone).map(point -> point.fromBasis(xBasisVector.clone().rotate(getRotation())).add(position)).toList();
    }

    public List<StraightLine> getShapeRealLines(Vector position) {
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

    public int getRotation() {
        return (int) Math.floor(degree);
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

    public Vector getXBasisVector() {
        return xBasisVector;
    }

}
