package ru.robert_grammy.astro_space.engine;

import java.awt.*;
import java.util.Optional;

public class StraightLine {
    private final double a, b, c;

    public StraightLine(Vector a, Vector b) {
        this(a.getY() - b.getY(), b.getX() - a.getX(), a.getX() * b.getY() - b.getX() * a.getY());
    }

    public StraightLine(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Vector getPointIntersectionLines(StraightLine line) {
        double x, y;
        y = (line.a * this.c - this.a * line.c) / (this.a * line.b - line.a * this.b);
        if (line.isHorizontal()) {
            x = (line.b * y + line.c) / -line.a;
        } else {
            x = (this.b * y + this.c) / -this.a;
        }
        return new Vector(x,y);
    }

    public boolean isVertical() {
        return a == 0;
    }

    public boolean isHorizontal() {
        return b == 0;
    }

    public boolean isParallel(StraightLine line) {
        if (this.isVertical()) return line.isVertical();
        if (this.isHorizontal()) return line.isHorizontal();
        if (line.isVertical() || line.isHorizontal()) return false;
        return this.a / line.a == this.b / line.b;
    }

    public boolean isEquals(StraightLine line) {
        return isParallel(line) && this.a / line.a == this.c / line.c;
    }

    private Vector fromY(double y) {
        if (isVertical()) return null;
        return new Vector((b * y + c) / -a, y);
    }

    private Vector fromX(double x) {
        if (isHorizontal()) return null;
        return new Vector(x, (a * x + c) / -b);
    }

    public void draw(Graphics2D graphics, Vector start, Vector end, Color color, float weight) {
        Vector a = isVertical() ? fromX(start.getX()) : fromY(start.getY());
        Vector b = isVertical() ? fromX(end.getX()) : fromY(end.getY());
        Color currentColor = graphics.getColor();
        Stroke currentStroke = graphics.getStroke();
        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(weight, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY());
        graphics.setColor(currentColor);
        graphics.setStroke(currentStroke);
    }

    @Override
    public String toString() {
        return "StraightLine{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                '}';
    }
}
