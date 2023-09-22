package ru.robert_grammy.astro_space.game;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.Keyboard;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.engine.geometry.Vector;
import ru.robert_grammy.astro_space.engine.sound.GameSound;
import ru.robert_grammy.astro_space.game.asteroid.Asteroid;
import ru.robert_grammy.astro_space.game.background.ParticleGenerator;
import ru.robert_grammy.astro_space.game.player.Player;
import ru.robert_grammy.astro_space.game.powerup.PowerUp;
import ru.robert_grammy.astro_space.graphics.Window;
import ru.robert_grammy.astro_space.utils.GameDebugger;
import ru.robert_grammy.astro_space.utils.TimeManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.font.TextLayout;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class Game {

    private final Window window = new Window();
    private final GameDebugger gameDebugger = new GameDebugger();
    private final List<Renderable> renderables = new ArrayList<>();
    private List<Renderable> renderablesDublicate;
    private final List<Updatable> updatables = new ArrayList<>();
    private List<Updatable> updatablesDublicate;
    private final TimeManager time = new TimeManager(60);
    private boolean running = false;

    private Player player;
    private int asteroidsCount = 0;
    private int score = 0;
    private int bestScore = 0;
    private boolean paused = false;
    private int scoreTimer = (int) time.getUpdateRate();

    private RenderThread render;
    private UpdateThread update;

    public Game() {
        render = new RenderThread(this);
        update = new UpdateThread(this);
    }

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

    public void updateRenderableList() {
        renderablesDublicate = new ArrayList<>(renderables);
    }

    public void updateUpdatablesList() {
        updatablesDublicate = new ArrayList<>(updatables);
    }

    public List<Updatable> getUpdatables() {
        return updatablesDublicate;
    }

    public List<Renderable> getRenderables() {
        return renderablesDublicate;
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
        updatables.clear();
    }

    public void initialize() {
        updateUpdatablesList();
        updateRenderableList();

        ParticleGenerator light = new ParticleGenerator((300 * (window.getBufferWidth()* window.getBufferHeight())/(1280*720)), 0);
        register(light);

        Player player = new Player(window.getBufferWidth()/2.0,window.getBufferHeight()/2.0);
        register(player);
        Rectangle smokeBound = new Rectangle((int) (player.getPosition().getX() - 40), (int) (player.getPosition().getY() - 40), 40, 40);
        ParticleGenerator smoke = new ParticleGenerator(50, 100, smokeBound, 30, 60, 40, 250, 1, 4, 0xEEFFEE);
        Main.getGame().register(smoke);
        smoke.setRecurring(false);

        asteroidsInitialize();
    }

    public void reset() {
        updatablesDublicate.stream().filter(updatable -> updatable instanceof Asteroid).map(asteroid -> (Asteroid) asteroid).forEach(Asteroid::kill);
        renderablesDublicate.stream().filter(renderable -> renderable instanceof PowerUp).map(powerUp -> (PowerUp) powerUp).forEach(PowerUp::kill);
        player.setPosition(new Vector(window.getBufferWidth()/2.0,window.getBufferHeight()/2.0));
        player.resurrect();
        asteroidsInitialize();
        bestScore = Math.max(bestScore, score);
        score = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public void reload() {
        render = new RenderThread(this);
        update = new UpdateThread(this);
    }

    public void play() {
        if (running) return;
        initialize();
        update.start();
        render.start();
        running = true;
    }

    public synchronized void stop() {
        running = false;
        render.stop();
        update.stop();
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
        if (keyboard.pressed(KeyEvent.VK_F5) && keyboard.isMemorized(KeyEvent.VK_F5)) {
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

        if (player.isDestroyed() && !(GameSound.GAME_OVER.get().isPlaying() && !GameSound.GAME_OVER.get().isEnded())) {
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
            IntStream.range(0,3).forEach(i -> asteroids.add(new Asteroid()));
            asteroids.stream().peek(Asteroid::reset).forEach(this::register);
            asteroidsCount = asteroids.size();
        }
    }

    public void scoreIncrement() {
        if (player.isDestroyed()) return;
        scoreTimer--;
        if (scoreTimer <= 0) {
            scoreTimer = (int) time.getUpdateRate();
            addScore(1 + asteroidsCount/5);
        }
    }
    public void addScore(int score) {
        if (player.isDestroyed()) return;
        if (player.onPower(PowerUp.PowerType.DOUBLE_SCORE)) score *= 2;
        this.score += score;
    }

    public int getAsteroidsCount() {
        return asteroidsCount;
    }

    public void drawScoreText(Graphics2D graphics) {
        if (player.isDestroyed()) return;
        Font font = new Font(Window.FONT_NAME, Font.PLAIN, 24);
        graphics.setFont(font);
        graphics.setColor(Color.WHITE);
        graphics.drawString("Best score: " + Math.max(score, bestScore), window.getBufferWidth() - 250, 50);
        graphics.drawString("Score: " + score, window.getBufferWidth() - 250, 75);
    }

    public void drawResultText(Graphics2D graphics) {
        if (!player.isDestroyed()) return;
        graphics.setColor(Color.BLACK);
        graphics.fillRect(window.getBufferWidth()/2 - 360, getWindow().getBufferHeight()/2 - 120, 720, 150);

        graphics.setColor(Color.WHITE);
        graphics.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawRect(window.getBufferWidth()/2 - 360, getWindow().getBufferHeight()/2 - 120, 720, 150);

        Font font = new Font(Window.FONT_NAME, Font.PLAIN, 64);
        graphics.setFont(font);
        String crashedText = "You have crashed!";
        TextLayout textLayout = new TextLayout(crashedText, font, graphics.getFontRenderContext());
        double xTextOffset = textLayout.getBounds().getWidth()/2;
        double yTextOffset = textLayout.getAscent()/2;
        double progress = GameSound.GAME_OVER.get().getClipProgress();
        int hexColor = 0xFFFFFF + ((int) (255 * progress) << 24);
        graphics.setColor(new Color(hexColor, true));
        graphics.drawString(crashedText, (int) (window.getBufferWidth()/2 - xTextOffset), (int) (window.getBufferHeight()/2 - (yTextOffset - 5) * progress));

        if (GameSound.GAME_OVER.get().isPlaying() && !GameSound.GAME_OVER.get().isEnded()) return;

        font = new Font(Window.FONT_NAME, Font.PLAIN, 24);
        graphics.setFont(font);
        graphics.setColor(Color.WHITE);
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
    }

    public void drawPauseText(Graphics2D graphics) {
        if (player.isDestroyed() || !paused) return;
        graphics.setColor(Color.BLACK);
        graphics.fillRect(window.getBufferWidth()/2 - 360, getWindow().getBufferHeight()/2 - 120, 720, 150);

        graphics.setColor(Color.WHITE);
        graphics.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawRect(window.getBufferWidth()/2 - 360, getWindow().getBufferHeight()/2 - 120, 720, 150);
        Font baseFont = graphics.getFont();

        Font font = new Font(Window.FONT_NAME, Font.PLAIN, 64);
        graphics.setFont(font);
        String pauseText = "PAUSED";
        TextLayout textLayout = new TextLayout(pauseText, font, graphics.getFontRenderContext());
        double xTextOffset = textLayout.getBounds().getWidth()/2;
        double yTextOffset = textLayout.getAscent()/2;
        graphics.drawString(pauseText, (int) (window.getBufferWidth()/2 - xTextOffset), (int) (window.getBufferHeight()/2 - yTextOffset - 5));

        font = new Font(Window.FONT_NAME, Font.PLAIN, 32);
        graphics.setFont(font);
        String pauseHint = "Press P to resume!";
        textLayout = new TextLayout(pauseHint, font, graphics.getFontRenderContext());
        xTextOffset = textLayout.getBounds().getWidth()/2;
        yTextOffset = textLayout.getAscent()/2;
        graphics.drawString(pauseHint, (int) (window.getBufferWidth()/2 - xTextOffset), (int) (window.getBufferHeight()/2 - yTextOffset + 5));

        graphics.setFont(baseFont);
        graphics.setColor(Color.BLACK);
    }

    public void update() {
        updateUpdatablesList();
        updateRenderableList();
        control(window.getKeyboard());
        if (paused) return;
        updatablesDublicate.forEach(Updatable::update);
        scoreIncrement();
        asteroidsInitialize();
    }

    public void render() {
        window.clear();
        Graphics2D graphics = window.getGameGraphics();
        renderablesDublicate.stream().sorted(Comparator.comparingInt(Renderable::getZIndex)).forEach(renderable -> renderable.render(graphics));
        drawScoreText(graphics);
        drawResultText(graphics);
        drawPauseText(graphics);
        window.swapCanvasImage();
    }

    public TimeManager getTimeManager() {
        return time;
    }

}
