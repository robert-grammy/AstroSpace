package ru.robert_grammy.astro_space.engine.geometry;

import ru.robert_grammy.astro_space.utils.QMath;

import java.util.Objects;

public class Vector implements Cloneable {

    private static final Vector ZERO = new Vector(0,0);
    private static final Vector UNDEFINED = new Vector(Double.NaN, Double.NaN);

    private double x, y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector add(Vector vector) {
        x += vector.x;
        y += vector.y;
        return this;
    }

    public Vector add(double x, double y) {
        return add(new Vector(x,y));
    }

    public Vector subtract(Vector vector) {
        x -= vector.x;
        y -= vector.y;
        return this;
    }

    public Vector subtract(double x, double y) {
        return subtract(new Vector(x,y));
    }

    public Vector multiply(double scale) {
        x *= scale;
        y *= scale;
        return this;
    }

    public double getScalar(Vector vector) {
        return this.x * vector.x + this.y * vector.y;
    }

    public double getProduct(Vector vector) {
        return this.x * vector.y - vector.x * this.y;
    }

    public Vector normalize() {
        double length = length();
        x = x / length;
        y = y / length;
        return this;
    }

    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector rotate(int degree) {
        double x = this.x;
        double y = this.y;
        this.x = x * QMath.cos(degree) - y * QMath.sin(degree);
        this.y = x * QMath.sin(degree) + y * QMath.cos(degree);
        return this;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    public Vector fromBasis(Vector xBasisVector) {
        Vector yBasisVector = new Vector(-xBasisVector.y, xBasisVector.x);
        return xBasisVector.multiply(x).add(yBasisVector.multiply(y));
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public static Vector getZero() {
        return ZERO.clone();
    }

    public static Vector getUndefined() {
        return UNDEFINED.clone();
    }

    @Override
    public Vector clone() {
        return new Vector(x,y);
    }
    public void setXY(Vector vector) {
        this.x = vector.x;
        this.y = vector.y;
    }
    public void resetCoordinates() {
        x = 0;
        y = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector vector = (Vector) o;
        return Double.compare(vector.x, x) == 0 && Double.compare(vector.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Vector{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

}
