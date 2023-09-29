package ru.robert_grammy.astro_space.graphics;

import ru.robert_grammy.astro_space.engine.Keyboard;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;

public class Window {

    public static final String TITLE = "Astro Space";
    public static final String FONT_NAME = "Comic Sans MS";
    private static final int BUFFER_STRATEGY_COUNT = 3;
    private static final double FRAME_WIDTH = 1600, FRAME_HEIGHT = 900;
    private static final double SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getWidth(), SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private final Keyboard keyboard = new Keyboard();
    private final Canvas canvas = new Canvas();
    private final Dimension dimension = new Dimension((int) FRAME_WIDTH, (int) FRAME_HEIGHT);
    private final CanvasImage canvasImage = new CanvasImage((int) FRAME_WIDTH, (int) FRAME_HEIGHT);
    private JFrame windowFrame = new JFrame();
    private BufferStrategy bufferStrategy;
    private boolean fullscreen = false;
    private boolean visible = true;

    public Window() {
        windowInitialize();
        graphicsInitialize();
    }

    public void clear() {
        canvasImage.clear();
    }

    public Graphics2D getGameGraphics() {
        return canvasImage.getGraphics();
    }

    public void swapCanvasImage() {
        if (!visible) return;
        double scale = fullscreen ? Math.max(SCREEN_WIDTH/FRAME_WIDTH, SCREEN_HEIGHT/FRAME_HEIGHT) : 1;
        bufferStrategy.getDrawGraphics().drawImage(canvasImage.get(scale), 0, 0, null);
        bufferStrategy.show();
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    private void windowInitialize() {
        windowFrame.setTitle(TITLE);
        windowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setPreferredSize(dimension);
        canvas.setFocusable(false);
        windowFrame.getContentPane().add(canvas);
        windowFrame.pack();
        windowFrame.setResizable(false);
        windowFrame.setLocationRelativeTo(null);
        windowFrame.setVisible(true);
        windowFrame.setFocusable(true);
        windowFrame.addKeyListener(keyboard);
    }

    private void graphicsInitialize() {
        canvas.createBufferStrategy(BUFFER_STRATEGY_COUNT);
        bufferStrategy = canvas.getBufferStrategy();
    }

    public int getBufferWidth() {
        return (int) FRAME_WIDTH;
    }

    public int getBufferHeight() {
        return (int) FRAME_HEIGHT;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        if (fullscreen) {
            toFullscreen();
        } else {
            toWindow();
        }
    }

    private void toFullscreen() {
        visible = false;
        windowFrame.dispose();
        windowFrame = new JFrame();
        windowFrame.setUndecorated(true);
        windowInitialize();
        canvas.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        windowFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        visible = true;
    }

    private void toWindow() {
        visible = false;
        windowFrame.dispose();
        windowFrame = new JFrame();
        windowFrame.setUndecorated(false);
        windowInitialize();
        windowFrame.setExtendedState(JFrame.NORMAL);
        visible = true;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

}
