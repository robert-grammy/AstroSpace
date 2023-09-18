package ru.robert_grammy.astro_space.graphics;

import ru.robert_grammy.astro_space.Main;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CanvasImage {

    private static final int CLEAR_COLOR = 0xFF110022;

    private final BufferedImage image;
    private final Graphics2D graphics;
    private final int[] pixels;

    public CanvasImage(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        graphics = (Graphics2D) image.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    public void clear() {
        Arrays.fill(pixels, CLEAR_COLOR);
    }

    public Graphics2D getGraphics() {
        return graphics;
    }

    public BufferedImage get(double scale) {
        return get(scale, 5);
    }

    public BufferedImage get(double scale, int threadsCount) {
        return scale == 1 ? image : getScaledImage(image, scale, threadsCount);
    }

    private BufferedImage getScaledImage(BufferedImage image, double scale, int threadsCount) {
        int height = image.getHeight() / threadsCount, width = image.getWidth() / threadsCount;
        int newHeight = (int) (height * scale), newWidth = (int) (width * scale);
        List<Thread> threadList = new ArrayList<>();
        BufferedImage result = new BufferedImage((int) (image.getWidth() * scale), (int) (image.getWidth() * scale), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) result.getGraphics();
        for (int v = 0; v<threadsCount; v++) {
            for (int h = 0; h<threadsCount; h++) {
                BufferedImage part = image.getSubimage(h * width, v * height, width, height);
                int finalH = h;
                int finalV = v;
                Thread thread = new Thread(() -> {
                    BufferedImage partOfResult = getScaledImage(part, scale);
                    graphics.drawImage(partOfResult,  finalH * newWidth, finalV * newHeight, null);
                });
                threadList.add(thread);
                thread.start();
            }
        }
        threadList.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Main.getGame().getGameDebugger().console(e);
            }
        });
        return result;
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

}
