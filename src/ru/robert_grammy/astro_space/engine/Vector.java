package ru.robert_grammy.astro_space.engine;

import ru.robert_grammy.astro_space.utils.QMath;

public class Vector {

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

    public Vector subtract(Vector vector) {
        x -= vector.x;
        y -= vector.y;
        return this;
    }

    public Vector multiply(double scale) {
        x *= scale;
        y *= scale;
        return this;
    }

    public double multiply(Vector vector) {
        double x = this.x * vector.x;
        double y = this.y * vector.y;
        return x+y;
    }

    public Vector normalize() {
        double x = this.x, y = this.y;
        this.x = x/length();
        this.y = y/length();
        return this;
    }

    public double length() {
        return Math.sqrt(x*x + y*y);
    }

    public void rotate(int degree) {
        double x = this.x, y = this.y;
        this.x = x * QMath.cos(degree) - y * QMath.sin(degree);
        this.y = x * QMath.sin(degree) + y * QMath.cos(degree);
    }

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

    public Vector clone() {
        return new Vector(x,y);
    }

}
