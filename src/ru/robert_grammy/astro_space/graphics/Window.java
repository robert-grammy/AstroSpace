package ru.robert_grammy.astro_space.graphics;

import ru.robert_grammy.astro_space.engine.KeyBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class Window extends JFrame {

    public final static String TITLE = "Astro Space";

    private final static int CLEAR_COLOR = 0xFF000000;
    private final static int WIDTH = 1280, HEIGHT = 720;
    private final static int BUFFER_STRATEGY_COUNT = 3;

    private final Canvas canvas = new Canvas();
    private final Dimension dimension = new Dimension(WIDTH, HEIGHT);
    private final BufferedImage canvasImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    private final Graphics2D graphics = (Graphics2D) canvasImage.getGraphics();
    private final KeyBoard keyBoard = new KeyBoard();

    private int[] pixels;
    private BufferStrategy bufferStrategy;

    public Window() {
        super(TITLE);
        windowInitialize();
        graphicsInitialize();
    }

    public void clear() {
        Arrays.fill(pixels, CLEAR_COLOR);
    }

    public Graphics2D getGameGraphics() {
        return graphics;
    }

    public void swapCanvasImage() {
        bufferStrategy.getDrawGraphics().drawImage(canvasImage, 0, 0, null);
        bufferStrategy.show();
    }

    public KeyBoard getKeyBoard() {
        return keyBoard;
    }

    private void windowInitialize() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        canvas.setPreferredSize(dimension);
        getContentPane().add(canvas);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        addKeyListener(keyBoard);
    }

    private void graphicsInitialize() {
        pixels = ((DataBufferInt) canvasImage.getRaster().getDataBuffer()).getData();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        canvas.createBufferStrategy(BUFFER_STRATEGY_COUNT);
        bufferStrategy = canvas.getBufferStrategy();
    }

}
