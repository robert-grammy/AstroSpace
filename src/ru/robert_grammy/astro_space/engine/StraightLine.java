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

    private Vector getBoundPoint(Vector vector) {
        Vector result;
        if (isHorizontal() || isVertical()) {
            result = isHorizontal() ? new Vector(vector.getX(), yFromX(vector)) : new Vector(xFromY(vector), vector.getY());
        } else {
            double k = -a / b;
            if (Math.abs(k) > 1) {
                result = new Vector(xFromY(vector), vector.getY());
            } else {
                result = new Vector(vector.getX(), yFromX(vector));
            }
        }
        return result;
    }

    public void draw(Graphics2D graphics, Vector start, Vector end, Color color, float weight) {
        Vector a = getBoundPoint(start);
        Vector b = getBoundPoint(end);
        Color currentColor = graphics.getColor();
        Stroke currentStroke = graphics.getStroke();
        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(weight, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine((int) a.getX(), (int) a.getY(), (int) b.getX(), (int) b.getY());

        //TODO Удалить, дебаг окресности
        graphics.setColor(Color.GREEN);
        graphics.drawRect((int) start.getX(), (int) start.getY(), (int) (end.getX()-start.getX()), (int) (end.getY()-start.getY()));

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
