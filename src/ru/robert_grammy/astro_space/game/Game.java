package ru.robert_grammy.astro_space.game;

import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.graphics.Window;
import ru.robert_grammy.astro_space.utils.TimeManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Game implements Runnable {

    private final Window window = new Window();

    private final List<Renderable> renderables = new ArrayList<>();
    private final List<Updatable> updatables = new ArrayList<>();

    private Thread thread = new Thread(this, window.getName());
    private TimeManager time = new TimeManager(60,60);
    private boolean running = false;

    public Game() {}

    public void addRenderable(Renderable renderable) {
        renderables.add(renderable);
    }

    public void removeRenderable(Renderable renderable) {
        renderables.remove(renderable);
    }

    public void addUpdatable(Updatable updatable) {
        updatables.add(updatable);
    }

    public void removeUpdatable(Updatable updatable) {
        updatables.remove(updatable);
    }

    public boolean register(Object object) {
        boolean isRegistered = false;
        if (object instanceof Renderable) {
            addRenderable((Renderable) object);
            isRegistered = true;
        }
        if (object instanceof Updatable) {
            addUpdatable((Updatable) object);
            isRegistered = true;
        }
        return isRegistered;
    }

    public boolean unregister(Object object) {
        boolean isUnregistered = false;
        if (object instanceof Renderable) {
            removeRenderable((Renderable) object);
            isUnregistered = true;
        }
        if (object instanceof Updatable) {
            removeUpdatable((Updatable) object);
            isUnregistered = true;
        }
        return isUnregistered;
    }

    public synchronized void play() {
        if (running) return;
        thread = new Thread(this, window.getName());
        running = true;
        thread.start();
    }

    public synchronized void stop() throws InterruptedException {
        running = false;
        thread.join();
    }

    public Window getWindow() {
        return window;
    }

    public void update() {
        updatables.forEach(updatable -> updatable.update(this));
    }

    public void render() {
        window.clear();
        Graphics2D graphics = window.getGameGraphics();
        renderables.stream().sorted(Comparator.comparingInt(Renderable::getZIndex)).forEach(renderable -> renderable.render(graphics));
        window.swapCanvasImage();
    }

    public void run() {
        int fps = 0;
        int ups = 0;
        long count = 0;
        float delta = 0;
        long lastTime = time.getCurrentTime();
        while (running) {
            long now = time.getCurrentTime();
            long elapsedTime = now - lastTime;
            lastTime = now;
            count += elapsedTime;
            boolean render = false;
            delta += (elapsedTime / time.getUpdateInterval());
            while (delta > 1) {
                update();
                ups++;
                delta--;
                if (!render) render = true;
            }
            if (render) {
                render();
                fps++;
            } else {
                try {
                    Thread.sleep(TimeManager.IDLE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (count >= TimeManager.SECOND) {
                window.setTitle(Window.TITLE + " || FPS: " + fps + " | UPS: " + ups);
                ups = 0;
                fps = 0;
                count = 0;
            }
        }
    }

}
