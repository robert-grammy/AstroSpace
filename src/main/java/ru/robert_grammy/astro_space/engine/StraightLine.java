package ru.robert_grammy.astro_space.engine;

import java.awt.*;

public class StraightLine {

    private static final double aroundZero = 0.00000000001;
    private final double a, b, c;

    public StraightLine(Vector a, Vector b) {
        this((a.getY() - b.getY()), (b.getX() - a.getX()), (a.getX() * b.getY() - b.getX() * a.getY()));
    }

    public StraightLine(double a, double b, double c) {
        this.a = Math.abs(a) <= aroundZero ? 0 : (Math.abs(Math.ceil(a) - a) <= aroundZero ? Math.ceil(a) : a);
        this.b = Math.abs(b) <= aroundZero ? 0 : (Math.abs(Math.ceil(b) - b) <= aroundZero ? Math.ceil(b) : b);
        this.c = Math.abs(c) <= aroundZero ? 0 : (Math.abs(Math.ceil(c) - c) <= aroundZero ? Math.ceil(c) : c);
    }

    public double lengthToPoint(Vector point) {
        StraightLine normalLine = getNormalLineFromPoint(point);
        Vector cross = getPointIntersectionLines(normalLine);
        return cross.subtract(point).length();
    }

    public StraightLine getNormalLineFromPoint(Vector point) {
        Vector zero = new Vector(1,1);
        Vector pointOnLine = new Vector(xFromY(zero), yFromX(zero));
        Vector normal = new Vector(-pointOnLine.getY(), pointOnLine.getX());
        Vector extra = point.clone().add(normal);
        return new StraightLine(point, extra);
    }

    public Vector getPointIntersectionLines(StraightLine line) {
        double x, y;
        y = (line.a * this.c - this.a * line.c) / (this.a * line.b - line.a * this.b);
        if (this.isHorizontal()) {
            x = (line.b * y + line.c) / -line.a;
        } else {
            x = (this.b * y + this.c) / -this.a;
        }
        return new Vector(x,y);
    }

    public boolean isHorizontal() {
        return a == 0;
    }
    public boolean isVertical() {
        return b == 0;
    }

    public boolean isParallel(StraightLine line) {
        if (this.isHorizontal()) return line.isHorizontal();
        if (this.isVertical()) return line.isVertical();
        if (line.isVertical() || line.isHorizontal()) return false;
        return this.a / line.a == this.b / line.b;
    }

    public boolean isEquals(StraightLine line) {
        return isParallel(line) && this.a / line.a == this.c / line.c;
    }

    private double xFromY(Vector vector) {
        if (isHorizontal()) return vector.getX();
        return (b * vector.getY() + c) / -a;
    }

    private double yFromX(Vector vector) {
        if (isVertical()) return vector.getY();
        return (a * vector.getX() + c) / -b;
    }

    private Vector getBoundPoint(Vector primary, Vector alternate) {
        Vector result;
        if (isHorizontal() || isVertical()) {
            result = isHorizontal() ? new Vector(primary.getX(), yFromX(primary)) : new Vector(xFromY(primary), primary.getY());
        } else {
            double k = a / b;
            boolean isStartPoint = primary.getX() < alternate.getX();
            if (Math.abs(k) >= 1) {
                result = new Vector(xFromY(primary), primary.getY());
                if (k >= 0) {
                    double offset = result.getX() - alternate.getX();
                    if (!isStartPoint && offset < 0 || isStartPoint && offset > 0) result = new Vector(alternate.getX(), yFromX(alternate));
                } else {
                    double offset = result.getX() - primary.getX();
                    if (isStartPoint && offset < 0 || !isStartPoint && offset > 0) result = new Vector(primary.getX(), yFromX(primary));
                }
            } else {
                result = new Vector(primary.getX(), yFromX(primary));
                if (k >= 0) {
                    double offset = result.getY() - alternate.getY();
                    if (!isStartPoint && offset < 0 || isStartPoint && offset > 0) result = new Vector(xFromY(alternate), alternate.getY());
                } else {
                    double offset = result.getY() - primary.getY();
                    if (isStartPoint && offset < 0 || !isStartPoint && offset > 0) result = new Vector(xFromY(primary), primary.getY());
                }
            }
        }
        return result;
    }

    public void draw(Graphics2D graphics, Rectangle bound, Color color, float weight) {
        Vector start = new Vector(bound.getX(), bound.getY());
        Vector end = start.clone().add(bound.getWidth(), bound.getHeight());
        Vector startLine = getBoundPoint(start, end);
        Vector endLine = getBoundPoint(end, start);
        Color currentColor = graphics.getColor();
        Stroke currentStroke = graphics.getStroke();
        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(weight, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine((int) startLine.getX(), (int) startLine.getY(), (int) endLine.getX(), (int) endLine.getY());
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
