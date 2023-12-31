package ru.robert_grammy.astro_space.utils;

import java.util.HashMap;
import java.util.Map;

public final class QMath {

    public static final int DEGREES_OF_RIGHT_ANGLE = 90;
    private static final Map<Integer, Double> sinTable = new HashMap<>();
    private static final Map<Integer, Double> cosTable = new HashMap<>();

    private QMath() {}

    public static double sin(int degree) {
        if (degree >= 360) degree -= 360;
        if (degree < 0) degree += 360;
        double radian = Math.toRadians(degree);
        if (!sinTable.containsKey(degree)) {
            sinTable.put(degree, Math.sin(radian));
        }
        return sinTable.get(degree);
    }

    public static double cos(int degree) {
        degree = degree >= 360 ? degree - 360 : degree;
        double radian = Math.toRadians(degree);
        if (!cosTable.containsKey(degree)) {
            cosTable.put(degree, Math.cos(radian));
        }
        return cosTable.get(degree);
    }

    static {
        for (int degree = 0; degree<360; degree++) {
            sin(degree);
            cos(degree);
        }
    }

}
