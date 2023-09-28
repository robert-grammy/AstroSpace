package ru.robert_grammy.astro_space.utils;

public record TimeManager(double updateRate) {

    public static final long SECOND = 1000000000L;

    public double getUpdateInterval() {
        return SECOND / updateRate;
    }

    public static long getCurrentTime() {
        return System.nanoTime();
    }

}
