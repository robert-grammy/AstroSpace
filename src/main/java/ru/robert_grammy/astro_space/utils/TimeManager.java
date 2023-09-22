package ru.robert_grammy.astro_space.utils;

public class TimeManager {

    public static final long SECOND	= 1000000000L;

    private final double updateRate;

    public TimeManager(double updateRate) {
        this.updateRate = updateRate;
    }

    public double getUpdateInterval() {
        return SECOND / updateRate;
    }


    public static long getCurrentTime() {
        return System.nanoTime();
    }

    public double getUpdateRate() {
        return updateRate;
    }

}
