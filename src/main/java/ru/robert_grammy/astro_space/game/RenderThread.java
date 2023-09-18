package ru.robert_grammy.astro_space.game;

public class RenderThread implements Runnable {

    private static final String THREAD_NAME = "RenderThread";

    private final Thread thread;
    private final Game game;

    public RenderThread(Game game) {
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
        while (!Thread.currentThread().isInterrupted()) {
            game.render();
            game.getGameDebugger().addFps();
        }
    }

}
