package ru.robert_grammy.astro_space.game;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.Keyboard;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.engine.Vector;
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
    private int asteroidsCount = 0;
    private int score = 0;
    private int bestScore = 0;
    private boolean paused = false;
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

    public void register(Object object) {
        if (object instanceof Player) {
            player = (Player) object;
        }
        if (object instanceof Renderable) {
            addRenderable((Renderable) object);
        }
        if (object instanceof Updatable) {
            addUpdatable((Updatable) object);
        }
    }

    public void unregister(Object object) {
        if (object instanceof Player) {
            player = null;
        }
        if (object instanceof Renderable) {
            removeRenderable((Renderable) object);
        }
        if (object instanceof Updatable) {
            removeUpdatable((Updatable) object);
        }
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
        ParticleGenerator light = new ParticleGenerator((300 * (window.getBufferWidth()* window.getBufferHeight())/(1280*720)), 0);
        register(light);

        Player player = new Player(window.getBufferWidth()/2,window.getBufferHeight()/2);
        register(player);
        Rectangle smokeBound = new Rectangle((int) (player.getPosition().getX() - 40), (int) (player.getPosition().getY() - 40), 40, 40);
        ParticleGenerator smoke = new ParticleGenerator(50, 100, smokeBound, 30, 60, 40, 250, 1, 4, 0xEEFFEE);
        Main.getGame().register(smoke);
        smoke.setRecurring(false);

        asteroidsInitialize();
    }

    public void reset() {
        updatables.stream().filter(updatable -> updatable instanceof Asteroid).map(asteroid -> (Asteroid) asteroid).forEach(Asteroid::kill);
        player.setPosition(new Vector(window.getBufferWidth()/2,window.getBufferHeight()/2));
        player.resurrect();
        asteroidsInitialize();
        bestScore = Math.max(bestScore, score);
        score = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public synchronized void play() {
        if (running) return;
        initialize();
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

    public void control(Keyboard keyboard) throws InterruptedException {
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

        if (keyboard.pressed(KeyEvent.VK_F12) && !keyboard.isMemorized(KeyEvent.VK_F12)) {
            keyboard.memorizePress(KeyEvent.VK_F12);
            window.setFullscreen(!window.isFullscreen());
        }

        if (keyboard.pressed(KeyEvent.VK_P) && !keyboard.isMemorized(KeyEvent.VK_P)) {
            keyboard.memorizePress(KeyEvent.VK_P);
            paused = !paused;
        }

        if (player.isDestroyed()) {
            if (keyboard.pressed(KeyEvent.VK_R) && !keyboard.isMemorized(KeyEvent.VK_R)) {
                keyboard.memorizePress(KeyEvent.VK_R);
                reset();
            }
        }

        if (keyboard.pressed(KeyEvent.VK_ESCAPE) && !keyboard.isMemorized(KeyEvent.VK_ESCAPE)) {
            keyboard.memorizePress(KeyEvent.VK_ESCAPE);
            System.exit(0);
        }

    }

    public void asteroidsInitialize() {
        asteroidsCount = (int) Main.getGame().getUpdatables().stream().filter(object -> object instanceof Asteroid).count();
        if (asteroidsCount == 0) {
            List<Asteroid> asteroids = new ArrayList<>();
            for (int i = 0; i<3; i++) asteroids.add(new Asteroid());
            asteroids.stream().peek(Asteroid::reset).forEach(this::register);
            asteroidsCount = 3;
        }
    }

    public void scoreIncrement() {
        if (player.isDestroyed()) return;
        scoreTimer--;
        if (scoreTimer <= 0) {
            scoreTimer = time.getUpdateRate();
            addScore(1 + asteroidsCount/5);
        }
    }
    public void addScore(int score) {
        if (player.isDestroyed()) return;
        this.score += score;
    }

    public int getAsteroidsCount() {
        return asteroidsCount;
    }

    public void drawScoreText(Graphics2D graphics) {
        if (player.isDestroyed()) return;
        Font baseFont = graphics.getFont();
        Font font = new Font("Comic Sans MS", Font.PLAIN, 24);
        graphics.setFont(font);
        graphics.setColor(Color.WHITE);

        StringBuilder bestScoreLabel = new StringBuilder();
        bestScoreLabel.append("Best score: ").append(Math.max(score, bestScore));
        graphics.drawString(bestScoreLabel.toString(), window.getBufferWidth() - 250, 50);

        StringBuilder scoreLabel = new StringBuilder();
        scoreLabel.append("Score: ").append(score);
        graphics.drawString(scoreLabel.toString(), window.getBufferWidth() - 250, 75);

        graphics.setFont(baseFont);
        graphics.setColor(Color.BLACK);
    }

    public void drawResultText(Graphics2D graphics) {
        if (!player.isDestroyed()) return;
        graphics.setColor(Color.BLACK);
        graphics.fillRect(window.getBufferWidth()/2 - 360, getWindow().getBufferHeight()/2 - 120, 720, 150);

        graphics.setColor(Color.WHITE);
        Font baseFont = graphics.getFont();

        Font font = new Font("Comic Sans MS", Font.PLAIN, 64);
        graphics.setFont(font);
        String crashedText = "You have crashed!";
        TextLayout textLayout = new TextLayout(crashedText, font, graphics.getFontRenderContext());
        double xTextOffset = textLayout.getBounds().getWidth()/2;
        double yTextOffset = textLayout.getAscent()/2;
        graphics.drawString(crashedText, (int) (window.getBufferWidth()/2 - xTextOffset), (int) (window.getBufferHeight()/2 - yTextOffset - 5));

        font = new Font("Comic Sans MS", Font.PLAIN, 24);
        graphics.setFont(font);
        StringBuilder scoreLabel = new StringBuilder();
        if (score > bestScore) {
            scoreLabel.append("New record! ");
        } else {
            scoreLabel.append("Best score: ").append(bestScore).append(". ");
        }
        scoreLabel.append("Your score: ").append(score).append(". Press R to restart!");
        String scoreText = scoreLabel.toString();
        textLayout = new TextLayout(scoreText, font, graphics.getFontRenderContext());
        xTextOffset = textLayout.getBounds().getWidth()/2;
        yTextOffset = textLayout.getAscent()/2;
        graphics.drawString(scoreLabel.toString(), (int) (window.getBufferWidth()/2 - xTextOffset), (int) (window.getBufferHeight()/2 - yTextOffset + 5));

        graphics.setFont(baseFont);
        graphics.setColor(Color.BLACK);
    }

    public void drawPauseText(Graphics2D graphics) {
        if (player.isDestroyed() || !paused) return;
        graphics.setColor(Color.BLACK);
        graphics.fillRect(window.getBufferWidth()/2 - 360, getWindow().getBufferHeight()/2 - 120, 720, 150);

        graphics.setColor(Color.WHITE);
        Font baseFont = graphics.getFont();

        Font font = new Font("Comic Sans MS", Font.PLAIN, 64);
        graphics.setFont(font);
        String pauseText = "PAUSED";
        TextLayout textLayout = new TextLayout(pauseText, font, graphics.getFontRenderContext());
        double xTextOffset = textLayout.getBounds().getWidth()/2;
        double yTextOffset = textLayout.getAscent()/2;
        graphics.drawString(pauseText, (int) (window.getBufferWidth()/2 - xTextOffset), (int) (window.getBufferHeight()/2 - yTextOffset - 5));

        font = new Font("Comic Sans MS", Font.PLAIN, 32);
        graphics.setFont(font);
        String pauseHint = "Press P to resume!";
        textLayout = new TextLayout(pauseHint, font, graphics.getFontRenderContext());
        xTextOffset = textLayout.getBounds().getWidth()/2;
        yTextOffset = textLayout.getAscent()/2;
        graphics.drawString(pauseHint.toString(), (int) (window.getBufferWidth()/2 - xTextOffset), (int) (window.getBufferHeight()/2 - yTextOffset + 5));

        graphics.setFont(baseFont);
        graphics.setColor(Color.BLACK);
    }

    public void update() throws InterruptedException {
        control(window.getKeyboard());
        if (paused) return;
        updatables.forEach(Updatable::update);
        updateUpdatablesList();
        scoreIncrement();
        asteroidsInitialize();
    }

    public void render() {
        window.clear();
        Graphics2D graphics = window.getGameGraphics();
        renderables.stream().sorted(Comparator.comparingInt(Renderable::getZIndex)).forEach(renderable -> renderable.render(graphics));
        drawScoreText(graphics);
        drawResultText(graphics);
        drawPauseText(graphics);
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
                try {
                    update();
                } catch (InterruptedException e) {
                    gameDebugger.console(e);
                }
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
                    gameDebugger.console(e);
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

    public boolean isRunning() {
        return running;
    }

}
