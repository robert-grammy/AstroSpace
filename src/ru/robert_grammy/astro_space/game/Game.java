package ru.robert_grammy.astro_space.game;

import ru.robert_grammy.astro_space.engine.Keyboard;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.game.player.Player;
import ru.robert_grammy.astro_space.graphics.Window;
import ru.robert_grammy.astro_space.utils.GameLogger;
import ru.robert_grammy.astro_space.utils.TimeManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Game implements Runnable {

    private final Window window = new Window();
    private final GameLogger logger = new GameLogger(this);;

    private final List<Renderable> renderables = new ArrayList<>();
    private final List<Renderable> renderablesToRemove = new ArrayList<>();
    private final List<Renderable> renderablesToAdd = new ArrayList<>();
    private final List<Updatable> updatables = new ArrayList<>();
    private final List<Updatable> updatablesToRemove = new ArrayList<>();
    private final List<Updatable> updatablesToAdd = new ArrayList<>();

    private Thread thread = new Thread(this, window.getName());
    private final TimeManager time = new TimeManager(60,60);
    private boolean running = false;

    private Player player;

    public Game() {}

    public void addRenderable(Renderable renderable) {
        if (!renderablesToAdd.contains(renderable))
            renderablesToAdd.add(renderable);
    }

    public void removeRenderable(Renderable renderable) {
        if (!renderablesToRemove.contains(renderable))
            renderablesToRemove.add(renderable);
    }

    public void addUpdatable(Updatable updatable) {
        if (!updatablesToAdd.contains(updatable))
            updatablesToAdd.add(updatable);
    }

    public void removeUpdatable(Updatable updatable) {
        if (!updatablesToRemove.contains(updatable))
            updatablesToRemove.add(updatable);
    }

    public void updateRenderableList() {
        renderables.removeAll(renderablesToRemove);
        renderablesToRemove.clear();
        renderables.addAll(renderablesToAdd);
        renderablesToAdd.clear();
    }

    public void updateUpdatablesList() {
        updatables.removeAll(updatablesToRemove);
        updatablesToRemove.clear();
        updatables.addAll(updatablesToAdd);
        updatablesToAdd.clear();
    }

    public List<Updatable> getUpdatables() {
        return Collections.unmodifiableList(updatables);
    }

    public List<Renderable> getRenderables() {
        return Collections.unmodifiableList(renderables);
    }

    public boolean register(Object object) {
        boolean isRegistered = false;
        if (object instanceof Player) {
            player = (Player) object;
        }
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
        if (object instanceof Player) {
            player = null;
        }
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

    public Player getPlayer() {
        return player;
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

    public GameLogger getLogger() {
        return logger;
    }

    public void control(Keyboard keyboard) {
        if (keyboard.pressed(KeyEvent.VK_F1) && !keyboard.isMemorized(KeyEvent.VK_F1)) {
            keyboard.memorizePress(KeyEvent.VK_F1);
            logger.setDisplayFps(!logger.isDisplayFps());
        }
        if (keyboard.pressed(KeyEvent.VK_F2) && !keyboard.isMemorized(KeyEvent.VK_F2)) {
            keyboard.memorizePress(KeyEvent.VK_F2);
            logger.setDisplayUps(!logger.isDisplayUps());
        }
        if (keyboard.pressed(KeyEvent.VK_F3) && !keyboard.isMemorized(KeyEvent.VK_F3)) {
            keyboard.memorizePress(KeyEvent.VK_F3);
            logger.setDrawPlayerShapeLines(!logger.isDrawPlayerShapeLines());
        }
        if (keyboard.pressed(KeyEvent.VK_F4) && !keyboard.isMemorized(KeyEvent.VK_F4)) {
            keyboard.memorizePress(KeyEvent.VK_F4);
            logger.setDrawLineBound(!logger.isDrawLineBound());
        }
    }

    public void update() {
        updatables.forEach(Updatable::update);
        updateUpdatablesList();
        control(window.getKeyboard());
    }

    public void render() {
        window.clear();
        Graphics2D graphics = window.getGameGraphics();
        renderables.stream().sorted(Comparator.comparingInt(Renderable::getZIndex)).forEach(renderable -> renderable.render(graphics));
        window.swapCanvasImage();
        updateRenderableList();
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

}
