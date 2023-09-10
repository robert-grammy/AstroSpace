package ru.robert_grammy.astro_space.utils;

public class TimeManager {

    public static final long SECOND	= 1000000000L;
    public static final long IDLE = 1L;

    private int updateRate;
    private int renderRate;

    public TimeManager(int updateRate, int renderRate) {
        this.updateRate = updateRate;
        this.renderRate = renderRate;
    }

    public double getUpdateInterval() {
        return SECOND / updateRate;
    }

    public double getRenderInterval() {
        return SECOND / renderRate;
    }

    public static long getCurrentTime() {
        return System.nanoTime();
    }

    public int getUpdateRate() {
        return updateRate;
    }

    public void setUpdateRate(int updateRate) {
        this.updateRate = updateRate;
    }

    public int getRenderRate() {
        return renderRate;
    }

    public void setRenderRate(int renderRate) {
        this.renderRate = renderRate;
    }
}
