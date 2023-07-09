package ru.robert_grammy.astro_space.game;

import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.graphics.Window;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Game implements Runnable {

    private final static float UPDATE_RATE = 60.0F;

    private final Window window = new Window();

    private final List<Renderable> renderables = new ArrayList<>();
    private final List<Updatable> updatables = new ArrayList<>();

    private Thread thread = new Thread(this, window.getName());
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
        Graphics2D graphics = window.getGraphics();
        renderables.stream().sorted(Comparator.comparingInt(Renderable::getZIndex)).forEach(renderable -> renderable.render(graphics));
        window.swapCanvasImage();
    }

    @Override
    public void run() {
        float delta = 0;
        long last = System.nanoTime();
        boolean render = false;
        while (running) {
            long now = System.nanoTime();
            long elapsed = now - last;
            delta += (elapsed / UPDATE_RATE);
            while(delta > 1) {
                update();
                delta--;
                render = true;
            }
            if (render) {
                render();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
