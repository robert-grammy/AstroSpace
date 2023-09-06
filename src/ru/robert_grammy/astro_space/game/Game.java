package ru.robert_grammy.astro_space.game;

import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.graphics.Window;
import ru.robert_grammy.astro_space.utils.GameLogger;
import ru.robert_grammy.astro_space.utils.TimeManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Game implements Runnable {

    private final Window window = new Window();
    private final GameLogger logger = new GameLogger(this);;

    private final List<Renderable> renderables = new ArrayList<>();
    private final List<Updatable> updatables = new ArrayList<>();

    private Thread thread = new Thread(this, window.getName());
    private final TimeManager time = new TimeManager(60,60);
    private boolean running = false;

    public Game() {}

    public void addRenderable(Renderable renderable) {
        if (!renderables.contains(renderable))
            renderables.add(renderable);
    }

    public void removeRenderable(Renderable renderable) {
        renderables.remove(renderable);
    }

    public void addUpdatable(Updatable updatable) {
        if (!updatables.contains(updatable))
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
                logger.addUps();
                delta--;
                if (!render) render = true;
            }
            if (render) {
                render();
                logger.addFps();
            } else {
                try {
                    Thread.sleep(TimeManager.IDLE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (count >= TimeManager.SECOND) {
                StringBuilder displayTitle = new StringBuilder(Window.TITLE);
                if (logger.isDisplayFps()) {
                    displayTitle.append(" || FPS: ").append(logger.getFps());
                    logger.resetFps();
                }
                if (logger.isDisplayUps()) {
                    displayTitle.append(" || UPS: ").append(logger.getUps());
                    logger.resetUps();
                }
                window.setTitle(displayTitle.toString());
                count = 0;
            }
        }
    }

    public void console(String text) {
        logger.console(text);
    }

}
