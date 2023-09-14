package ru.robert_grammy.astro_space.game;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.Keyboard;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.game.asteroid.Asteroid;
import ru.robert_grammy.astro_space.game.background.ParticleGenerator;
import ru.robert_grammy.astro_space.game.player.Player;
import ru.robert_grammy.astro_space.graphics.Window;
import ru.robert_grammy.astro_space.utils.GameDebugger;
import ru.robert_grammy.astro_space.utils.TimeManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.font.TextLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Game implements Runnable {

    private final Window window = new Window();
    private final GameDebugger gameDebugger = new GameDebugger(this);;

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
    private int score = 0;
    private int scoreTimer = time.getUpdateRate();

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

    public void unregisterAll() {
        renderables.clear();
        renderablesToAdd.clear();
        renderablesToRemove.clear();

        updatables.clear();
        updatablesToAdd.clear();
        updatablesToRemove.clear();
    }

    public void initialize() {
        ParticleGenerator light = new ParticleGenerator(300, 0);
        register(light);

        Player player = new Player(640,360);
        register(player);

        List<Asteroid> asteroids = new ArrayList<>();
        for (int i = 0; i<3; i++) asteroids.add(new Asteroid());
        asteroids.stream().peek(Asteroid::reset).forEach(this::register);
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

    public GameDebugger getGameDebugger() {
        return gameDebugger;
    }

    public void control(Keyboard keyboard) {
        if (keyboard.pressed(KeyEvent.VK_F1) && !keyboard.isMemorized(KeyEvent.VK_F1)) {
            keyboard.memorizePress(KeyEvent.VK_F1);
            gameDebugger.setDisplayFps(!gameDebugger.isDisplayFps());
        }
        if (keyboard.pressed(KeyEvent.VK_F2) && !keyboard.isMemorized(KeyEvent.VK_F2)) {
            keyboard.memorizePress(KeyEvent.VK_F2);
            gameDebugger.setDisplayUps(!gameDebugger.isDisplayUps());
        }
        if (keyboard.pressed(KeyEvent.VK_F3) && !keyboard.isMemorized(KeyEvent.VK_F3)) {
            keyboard.memorizePress(KeyEvent.VK_F3);
            gameDebugger.setDrawPlayerShapeLines(!gameDebugger.isDrawPlayerShapeLines());
        }
        if (keyboard.pressed(KeyEvent.VK_F4) && !keyboard.isMemorized(KeyEvent.VK_F4)) {
            keyboard.memorizePress(KeyEvent.VK_F4);
            gameDebugger.setDrawPlayerLineBound(!gameDebugger.isDrawPlayerLineBound());
        }
        if (keyboard.pressed(KeyEvent.VK_F5) && !keyboard.isMemorized(KeyEvent.VK_F5)) {
            keyboard.memorizePress(KeyEvent.VK_F5);
            gameDebugger.setDrawAsteroidCircleBound(!gameDebugger.isDrawAsteroidCircleBound());
        }
        if (keyboard.pressed(KeyEvent.VK_F6) && !keyboard.isMemorized(KeyEvent.VK_F6)) {
            keyboard.memorizePress(KeyEvent.VK_F6);
            gameDebugger.setDrawAsteroidLineBound(!gameDebugger.isDrawAsteroidLineBound());
        }
        if (keyboard.pressed(KeyEvent.VK_F7) && !keyboard.isMemorized(KeyEvent.VK_F7)) {
            keyboard.memorizePress(KeyEvent.VK_F7);
            gameDebugger.setDrawAsteroidShapeLines(!gameDebugger.isDrawAsteroidShapeLines());
        }
        if (keyboard.pressed(KeyEvent.VK_F8) && !keyboard.isMemorized(KeyEvent.VK_F8)) {
            keyboard.memorizePress(KeyEvent.VK_F8);
            gameDebugger.setDrawAsteroidParams(!gameDebugger.isDrawAsteroidParams());
        }
    }

    public void addScore(int score) {
        this.score += score;
    }

    public void scoreIncrement() {
        scoreTimer--;
        if (scoreTimer <= 0) {
            int asteroidsCount = (int) Main.getGame().getUpdatables().stream().filter(object -> object instanceof Asteroid).count();
            scoreTimer = time.getUpdateRate();
            addScore(1 + asteroidsCount/5);
        }
    }

    public void drawScoreText(Graphics2D graphics) {
        Font baseFont = graphics.getFont();
        Font font = new Font("Comic Sans MS", Font.PLAIN, 24);
        graphics.setFont(font);
        graphics.setColor(Color.WHITE);
        StringBuilder builder = new StringBuilder();
        builder.append("Score: ").append(score);
        graphics.drawString(builder.toString(), window.getCanvasWidth() - 200, 50);
        graphics.setFont(baseFont);
        graphics.setColor(Color.BLACK);
    }

    public void update() {
        updatables.forEach(Updatable::update);
        updateUpdatablesList();
        control(window.getKeyboard());
        scoreIncrement();
    }

    public void render() {
        window.clear();
        Graphics2D graphics = window.getGameGraphics();
        renderables.stream().sorted(Comparator.comparingInt(Renderable::getZIndex)).forEach(renderable -> renderable.render(graphics));
        drawScoreText(graphics);
        window.swapCanvasImage();
        updateRenderableList();
    }

    public void run() {
        long count = 0;
        float delta = 0;
        long lastTime = TimeManager.getCurrentTime();
        while (running) {
            long now = TimeManager.getCurrentTime();
            long elapsedTime = now - lastTime;
            lastTime = now;
            count += elapsedTime;
            boolean render = false;
            delta += (elapsedTime / time.getUpdateInterval());
            while (delta > 1) {
                update();
                gameDebugger.addUps();
                delta--;
                if (!render) render = true;
            }
            if (render) {
                render();
                gameDebugger.addFps();
            } else {
                try {
                    Thread.sleep(TimeManager.IDLE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (count >= TimeManager.SECOND) {
                StringBuilder displayTitle = new StringBuilder(Window.TITLE);
                if (gameDebugger.isDisplayFps()) {
                    displayTitle.append(" || FPS: ").append(gameDebugger.getFps());
                }
                if (gameDebugger.isDisplayUps()) {
                    displayTitle.append(" || UPS: ").append(gameDebugger.getUps());
                }
                window.setTitle(displayTitle.toString());
                gameDebugger.resetFps();
                gameDebugger.resetUps();
                count = 0;
            }
        }
    }

}
