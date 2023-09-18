package ru.robert_grammy.astro_space.game;

import ru.robert_grammy.astro_space.graphics.Window;
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

    public void stop() {
        thread.interrupt();
    }

    @Override
    public void run() {
        long count = 0;
        float delta = 0;
        long lastTime = TimeManager.getCurrentTime();
        while (!Thread.currentThread().isInterrupted()) {
            long now = TimeManager.getCurrentTime();
            long elapsedTime = now - lastTime;
            lastTime = now;
            count += elapsedTime;
            delta += (elapsedTime / game.getTimeManager().getUpdateInterval());
            while (delta > 1) {
                game.update();
                game.getGameDebugger().addUps();
                delta--;
            }
            if (count >= TimeManager.SECOND) {
                StringBuilder displayTitle = new StringBuilder(Window.TITLE);
                if (game.getGameDebugger().isDisplayFps()) {
                    displayTitle.append(" || FPS: ").append(game.getGameDebugger().getFps());
                }
                if (game.getGameDebugger().isDisplayUps()) {
                    displayTitle.append(" || UPS: ").append(game.getGameDebugger().getUps());
                }
                game.getWindow().setTitle(displayTitle.toString());
                game.getGameDebugger().resetFps();
                game.getGameDebugger().resetUps();
                count = 0;
            }
        }
    }

}
