package ru.robert_grammy.astro_space.engine.geometry;

import java.awt.*;
import java.util.Objects;

public class StraightLine {

    private static final double AROUND_ZERO = 0.00000000001;
    private final double a, b, c;
    private final Vector pointA, pointB;

    public StraightLine(Vector pointA, Vector pointB) {
        this((pointA.getY() - pointB.getY()), (pointB.getX() - pointA.getX()), (pointA.getX() * pointB.getY() - pointB.getX() * pointA.getY()), pointA, pointB);
    }

    private StraightLine(double a, double b, double c, Vector pointA, Vector pointB) {
        this.a = Math.abs(a) <= AROUND_ZERO ? 0 : (Math.abs(Math.ceil(a) - a) <= AROUND_ZERO ? Math.ceil(a) : a);
        this.b = Math.abs(b) <= AROUND_ZERO ? 0 : (Math.abs(Math.ceil(b) - b) <= AROUND_ZERO ? Math.ceil(b) : b);
        this.c = Math.abs(c) <= AROUND_ZERO ? 0 : (Math.abs(Math.ceil(c) - c) <= AROUND_ZERO ? Math.ceil(c) : c);
        this.pointA = pointA;
        this.pointB = pointB;
    }

    public double distanceToPoint(Vector point) {
        StraightLine normalLine = getNormalLineFromPoint(point);
        Vector cross = getPointIntersectionLines(normalLine);
        return cross.subtract(point).length();
    }

    public StraightLine getNormalLineFromPoint(Vector point) {
        Vector pointOnLine = new Vector(xFromY(Vector.getZero()), yFromX(Vector.getZero()));
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

    private double xFromY(Vector point) {
        if (isHorizontal()) return point.getX();
        return (b * point.getY() + c) / -a;
    }

    private double yFromX(Vector point) {
        if (isVertical()) return point.getY();
        return (a * point.getX() + c) / -b;
    }

    public Vector getGuideVector() {
        return new Vector(-b, a).normalize();
    }

    public Vector getNormalVector() {
        return new Vector(-a, -b).normalize();
    }

    public Vector getPointProjectionOnLine(Vector point) {
        Vector extra = getNormalVector().add(point);
        StraightLine normalLine = new StraightLine(point, extra);
        return getPointIntersectionLines(normalLine);
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
        graphics.setColor(color);
        graphics.setStroke(new BasicStroke(weight, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine((int) startLine.getX(), (int) startLine.getY(), (int) endLine.getX(), (int) endLine.getY());
    }

    public double distanceFromSegmentToPoint(Vector point) {
        Vector ab = new Vector((pointB.getX() - pointA.getX()), (pointB.getY() - pointA.getY()));
        Vector ah = new Vector((point.getX() - pointA.getX()), (point.getY() - pointA.getY()));
        double product = ab.getProduct(ah);
        double leftX = Math.min(pointA.getX(), pointB.getX());
        double rightX = Math.max(pointA.getX(), pointB.getX());
        double upY = Math.min(pointA.getY(), pointB.getY());
        double downY = Math.max(pointA.getY(), pointB.getY());
        Vector projection = getPointProjectionOnLine(point);
        if (product == 0 || (projection.getX() < leftX || projection.getX() > rightX) || (projection.getY() < upY || projection.getY() > downY)) {
            Vector bh = new Vector((point.getX() - pointB.getX()), (point.getY() - pointB.getY()));
            return Math.min(ah.length(), bh.length());
        } else {
            return Math.abs(product) / ab.length();
        }
    }

    public double distanceFromLineToPoint(Vector point) {
        Vector ab = new Vector((pointB.getX() - pointA.getX()), (pointB.getY() - pointA.getY()));
        Vector ah = new Vector((point.getX() - pointA.getX()), (point.getY() - pointA.getY()));
        double product = ab.getProduct(ah);
        return Math.abs(product) / ab.length();
    }

    @Override
    public String toString() {
        return "StraightLine" +
                "{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StraightLine that = (StraightLine) o;
        return Double.compare(that.a, a) == 0 && Double.compare(that.b, b) == 0 && Double.compare(that.c, c) == 0 && Objects.equals(pointA, that.pointA) && Objects.equals(pointB, that.pointB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c, pointA, pointB);
    }
}
