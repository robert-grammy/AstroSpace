package ru.robert_grammy.astro_space.graphics;

import ru.robert_grammy.astro_space.engine.Keyboard;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class Window extends JFrame {

    public final static String TITLE = "Astro Space";

    private final static int CLEAR_COLOR = 0xFF110022;
    private final static double BUFFER_WIDTH = 1600, BUFFER_HEIGHT = 900;
    private final static double FRAME_WIDTH = 1600, FRAME_HEIGHT = 900;
    private final static double SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getWidth(), SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    private final static int BUFFER_STRATEGY_COUNT = 3;

    private final Canvas canvas = new Canvas();
    private final Dimension dimension = new Dimension((int) FRAME_WIDTH, (int) FRAME_HEIGHT);
    private final BufferedImage canvasImage = new BufferedImage((int) BUFFER_WIDTH, (int) BUFFER_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    private final Graphics2D graphics = (Graphics2D) canvasImage.getGraphics();
    private final Keyboard keyboard = new Keyboard();

    private boolean fullscreen = false;

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
        double scale = fullscreen ? Math.max(SCREEN_WIDTH/FRAME_WIDTH, SCREEN_HEIGHT/FRAME_HEIGHT) : 1;
        int width = (int) (FRAME_WIDTH * scale), height = (int) (FRAME_HEIGHT * scale);
        Image image = scale == 1 ? canvasImage : getScaledImage(canvasImage, scale);
        bufferStrategy.getDrawGraphics().drawImage(image, 0, 0, null);
        bufferStrategy.show();
    }

    private BufferedImage getScaledImage(BufferedImage before, double scale) {
        int width = before.getWidth();
        int height = before.getHeight();
        BufferedImage after = new BufferedImage((int) (width * scale), (int) (height * scale), BufferedImage.TYPE_INT_ARGB);
        AffineTransform transform = AffineTransform.getScaleInstance(scale, scale);
        AffineTransformOp scaleTransform = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        after = scaleTransform.filter(before, after);
        return after;
    }

    public Keyboard getKeyboard() {
        return keyboard;
    }

    private void windowInitialize() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        canvas.setPreferredSize(dimension);
        canvas.setFocusable(false);
        getContentPane().add(canvas);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
        setFocusable(true);
        addKeyListener(keyboard);
    }

    private void graphicsInitialize() {
        pixels = ((DataBufferInt) canvasImage.getRaster().getDataBuffer()).getData();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        canvas.createBufferStrategy(BUFFER_STRATEGY_COUNT);
        bufferStrategy = canvas.getBufferStrategy();
    }

    public int getBufferWidth() {
        return canvasImage.getWidth();
    }

    public int getBufferHeight() {
        return canvasImage.getHeight();
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        if (fullscreen) {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            canvas.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        } else {
            setExtendedState(JFrame.NORMAL);
            canvas.setPreferredSize(dimension);
        }
    }

    public boolean isFullscreen() {
        return fullscreen;
    }
}
