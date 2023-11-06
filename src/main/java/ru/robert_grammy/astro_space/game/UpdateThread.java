package ru.robert_grammy.astro_space.game;

import ru.robert_grammy.astro_space.utils.TimeManager;

public class UpdateThread implements Runnable {

    private static final String THREAD_NAME = "UpdateThread";

    private final Thread thread;
    private final Game game;

    public UpdateThread(Game game) {
        this.game = game;
        thread = new Thread(this, THREAD_NAME);
    }

    public void start() {
        thread.start();
    }

    @Override
    public void run() {
        float delta = 0;
        long lastTime = TimeManager.getCurrentTime();
        while (!Thread.currentThread().isInterrupted()) {
            long now = TimeManager.getCurrentTime();
            long elapsedTime = now - lastTime;
            lastTime = now;
            delta += (float) (elapsedTime / game.getTimeManager().getUpdateInterval());
            while (delta > 1) {
                game.update();
                delta--;
            }
        }
    }

}
