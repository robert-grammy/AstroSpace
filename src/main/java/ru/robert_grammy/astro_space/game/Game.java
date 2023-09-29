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
import ru.robert_grammy.astro_space.utils.TimeManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.font.TextLayout;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class Game {

    private static final String CRASHED_TEXT = "You have crashed!";
    private static final String NEW_RECORD = "New record! ";
    private static final String BEST_SCORE = "Best score: %d. ";
    private static final String YOUR_SCORE = "Your score: %d. Press R to restart!";
    private static final String PAUSED = "PAUSED!";
    private static final String RESUME = "Press P to resume!";
    private static final Font SMALL_FONT = new Font(Window.FONT_NAME, Font.PLAIN, 24);
    private static final Font MEDIUM_FONT = new Font(Window.FONT_NAME, Font.PLAIN, 32);
    private static final Font LARGE_FONT = new Font(Window.FONT_NAME, Font.PLAIN, 64);
    private static final Stroke DEFAULT_STROKE = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    private static final Color TEXT_AND_OUTLINE_COLOR = Color.WHITE;
    private static final Color TEXT_BACKGROUND_COLOR = Color.BLACK;
    private static final int MIN_SCORE_INCREMENT_VALUE = 1;
    private static final int SCORE_INCREMENT_COEFFICIENT_DEPENDENT_ON_ASTEROIDS_COUNT = 5;
    private static final int START_ASTEROIDS_COUNT = 3;
    private static final int RESULT_BOARD_WIDTH = 720;
    private static final int RESULT_BOARD_HEIGHT = 150;
    private static final int RESULT_BOARD_Y_OFFSET = 45;
    private static final int TEXT_INTERVAL = 5;
    private final RenderThread render;
    private final UpdateThread update;
    private final Window window = new Window();
    private final Vector spawnPoint = new Vector((double) window.getBufferWidth() / 2,(double) window.getBufferHeight() / 2);
    private final List<Renderable> renderables = new ArrayList<>();
    private final List<Updatable> updatables = new ArrayList<>();
    private final TimeManager time = new TimeManager(60);
    private boolean running = false;
    private boolean paused = false;
    private int asteroidsCount = 0;
    private int score = 0;
    private int bestScore = 0;
    private int scoreTimer = (int) time.updateRate();
    private List<Renderable> renderablesDublicate;
    private List<Updatable> updatablesDublicate;
    private Player player;

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

    public synchronized List<Updatable> getUpdatables() {
        return updatablesDublicate;
    }

    public synchronized List<Renderable> getRenderables() {
        return renderablesDublicate;
    }

    public void updateRenderablesAndUpdatablesLists() {
        renderablesDublicate = new ArrayList<>(renderables);
        updatablesDublicate = new ArrayList<>(updatables);
    }

    public synchronized void register(Object object) {
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

    public synchronized void unregister(Object object) {
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

    public void asteroidsInitialize() {
        asteroidsCount = (int) Main.getGame().getUpdatables().stream().filter(object -> object instanceof Asteroid).count();
        if (asteroidsCount == 0) {
            List<Asteroid> asteroids = new ArrayList<>();
            IntStream.range(0,START_ASTEROIDS_COUNT).forEach(i -> asteroids.add(new Asteroid()));
            asteroids.forEach(this::register);
            asteroidsCount = asteroids.size();
        }
    }

    public void initialize() {
        updateRenderablesAndUpdatablesLists();
        ParticleGenerator light = new ParticleGenerator(ParticleGenerator.BASE_STARS_COUNT, ParticleGenerator.STARS_Z_INDEX);
        register(light);

        Vector spawnPosition = this.spawnPoint.clone();

        Player player = new Player(spawnPosition);
        register(player);

        ParticleGenerator smoke = ParticleGenerator.createSmoke(spawnPosition);
        register(smoke);
        smoke.setRecurring(false);

        asteroidsInitialize();
    }

    public void reset() {
        getUpdatables().stream().filter(updatable -> updatable instanceof Asteroid).map(asteroid -> (Asteroid) asteroid).forEach(Asteroid::kill);
        getRenderables().stream().filter(renderable -> renderable instanceof PowerUp).map(powerUp -> (PowerUp) powerUp).forEach(PowerUp::kill);
        player.setPosition(spawnPoint.clone());
        player.resurrect();
        asteroidsInitialize();
        bestScore = Math.max(bestScore, score);
        score = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public void play() {
        if (running) return;
        initialize();
        update.start();
        render.start();
        running = true;
    }

    public Window getWindow() {
        return window;
    }

    public void control(Keyboard keyboard) {
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

    public void scoreIncrement() {
        if (player.isDestroyed()) return;
        scoreTimer--;
        if (scoreTimer <= 0) {
            scoreTimer = (int) time.updateRate();
            addScore(MIN_SCORE_INCREMENT_VALUE + asteroidsCount / SCORE_INCREMENT_COEFFICIENT_DEPENDENT_ON_ASTEROIDS_COUNT);
        }
    }
    public void addScore(int score) {
        if (player.isDestroyed()) return;
        if (player.onPower(PowerUp.PowerType.DOUBLE_SCORE)) score *= 2;
        this.score += score;
    }

    public void drawScoreText(Graphics2D graphics) {
        if (player.isDestroyed()) return;
        graphics.setFont(SMALL_FONT);
        graphics.setColor(TEXT_AND_OUTLINE_COLOR);
        graphics.drawString("Best score: " + Math.max(score, bestScore), window.getBufferWidth() - 250, 50);
        graphics.drawString("Score: " + score, window.getBufferWidth() - 250, 75);
    }

    public void drawResultText(Graphics2D graphics) {
        if (!player.isDestroyed()) return;
        graphics.setColor(TEXT_BACKGROUND_COLOR);
        graphics.fillRect(
                window.getBufferWidth()/2 - (RESULT_BOARD_WIDTH / 2),
                getWindow().getBufferHeight()/2 - (RESULT_BOARD_HEIGHT / 2) - RESULT_BOARD_Y_OFFSET,
                RESULT_BOARD_WIDTH,
                RESULT_BOARD_HEIGHT
        );

        graphics.setColor(TEXT_AND_OUTLINE_COLOR);
        graphics.setStroke(DEFAULT_STROKE);
        graphics.drawRect(
                window.getBufferWidth()/2 - (RESULT_BOARD_WIDTH / 2),
                getWindow().getBufferHeight()/2 - (RESULT_BOARD_HEIGHT / 2) - RESULT_BOARD_Y_OFFSET,
                RESULT_BOARD_WIDTH,
                RESULT_BOARD_HEIGHT
        );

        graphics.setFont(LARGE_FONT);
        TextLayout textLayout = new TextLayout(CRASHED_TEXT, LARGE_FONT, graphics.getFontRenderContext());
        double xTextOffset = textLayout.getBounds().getWidth()/2;
        double yTextOffset = textLayout.getAscent()/2;
        double progress = GameSound.GAME_OVER.get().getClipProgress();
        int hexColor = 0xFFFFFF + ((int) (255 * progress) << 24);
        graphics.setColor(new Color(hexColor, true));
        graphics.drawString(CRASHED_TEXT, (int) (window.getBufferWidth()/2 - xTextOffset), (int) (window.getBufferHeight()/2 - (yTextOffset - TEXT_INTERVAL) * progress));

        if (GameSound.GAME_OVER.get().isPlaying() && !GameSound.GAME_OVER.get().isEnded()) return;

        graphics.setFont(SMALL_FONT);
        graphics.setColor(Color.WHITE);
        StringBuilder scoreLabel = new StringBuilder();
        if (score > bestScore) {
            scoreLabel.append(NEW_RECORD);
        } else {
            scoreLabel.append(BEST_SCORE.formatted(bestScore));
        }
        scoreLabel.append(YOUR_SCORE.formatted(score));
        String scoreText = scoreLabel.toString();
        textLayout = new TextLayout(scoreText, SMALL_FONT, graphics.getFontRenderContext());
        xTextOffset = textLayout.getBounds().getWidth()/2;
        yTextOffset = textLayout.getAscent()/2;
        graphics.drawString(scoreLabel.toString(), (int) (window.getBufferWidth()/2 - xTextOffset), (int) (window.getBufferHeight()/2 - yTextOffset + TEXT_INTERVAL));
    }

    public void drawPauseText(Graphics2D graphics) {
        if (player.isDestroyed() || !paused) return;
        graphics.setColor(TEXT_BACKGROUND_COLOR);
        graphics.fillRect(
                window.getBufferWidth()/2 - (RESULT_BOARD_WIDTH / 2),
                getWindow().getBufferHeight()/2 - (RESULT_BOARD_HEIGHT / 2) - RESULT_BOARD_Y_OFFSET,
                RESULT_BOARD_WIDTH,
                RESULT_BOARD_HEIGHT
        );

        graphics.setColor(TEXT_AND_OUTLINE_COLOR);
        graphics.setStroke(DEFAULT_STROKE);
        graphics.drawRect(
                window.getBufferWidth()/2 - (RESULT_BOARD_WIDTH / 2),
                getWindow().getBufferHeight()/2 - (RESULT_BOARD_HEIGHT / 2) - RESULT_BOARD_Y_OFFSET,
                RESULT_BOARD_WIDTH,
                RESULT_BOARD_HEIGHT
        );

        graphics.setFont(LARGE_FONT);
        TextLayout textLayout = new TextLayout(PAUSED, LARGE_FONT, graphics.getFontRenderContext());
        double xTextOffset = textLayout.getBounds().getWidth()/2;
        double yTextOffset = textLayout.getAscent()/2;
        graphics.drawString(PAUSED, (int) (window.getBufferWidth()/2 - xTextOffset), (int) (window.getBufferHeight()/2 - yTextOffset - TEXT_INTERVAL));

        graphics.setFont(MEDIUM_FONT);
        textLayout = new TextLayout(RESUME, MEDIUM_FONT, graphics.getFontRenderContext());
        xTextOffset = textLayout.getBounds().getWidth()/2;
        yTextOffset = textLayout.getAscent()/2;
        graphics.drawString(RESUME, (int) (window.getBufferWidth()/2 - xTextOffset), (int) (window.getBufferHeight()/2 - yTextOffset + TEXT_INTERVAL));
    }

    public void update() {
        updateRenderablesAndUpdatablesLists();
        control(window.getKeyboard());
        if (paused) return;
        getUpdatables().forEach(Updatable::update);
        scoreIncrement();
        asteroidsInitialize();
    }

    public void render() {
        window.clear();
        Graphics2D graphics = window.getGameGraphics();
        getRenderables().stream().sorted(Comparator.comparingInt(Renderable::getZIndex)).forEach(renderable -> renderable.render(graphics));
        drawScoreText(graphics);
        drawResultText(graphics);
        drawPauseText(graphics);
        window.swapCanvasImage();
    }

    public TimeManager getTimeManager() {
        return time;
    }

    public int getAsteroidsCount() {
        return asteroidsCount;
    }

}