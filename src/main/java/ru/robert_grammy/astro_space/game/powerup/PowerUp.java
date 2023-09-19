package ru.robert_grammy.astro_space.game.powerup;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Vector;
import ru.robert_grammy.astro_space.game.background.ParticleGenerator;
import ru.robert_grammy.astro_space.graphics.Window;

import java.awt.*;
import java.awt.font.TextLayout;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.stream.IntStream;

public class PowerUp implements Renderable {

    private static final double FADE_SPEED = 0.15;

    private static final Random rnd = new Random();

    private final Vector position;
    private final PowerType type;
    private double alpha = 0.0;

    public PowerUp() {
        position = new Vector(rnd.nextInt(25, Main.getGame().getWindow().getBufferWidth() - 25), rnd.nextInt(25, Main.getGame().getWindow().getBufferHeight() - 25));
        type = PowerType.getRandom();
    }

    public PowerType getType() {
        return type;
    }

    public Vector getPosition() {
        return position;
    }

    @Override
    public void render(Graphics2D graphics) {
        Color fill = new Color(type.getHexRGB() + ((int) (alpha >= 255 ? 255 : alpha) << 24), true);
        Color outline = new Color(0xFFFFFF + ((int) (alpha >= 255 ? 255 : alpha) << 24), true);
        Color symbolColor = new Color(0xFFFFFF + ((int) (alpha >= 255 ? 255 : alpha) << 24), true);
        alpha += FADE_SPEED;
        graphics.setColor(fill);
        graphics.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.fillOval((int) position.getX() - 20, (int) position.getY() - 20, 40, 40);
        graphics.setColor(outline);
        graphics.drawOval((int) position.getX() - 20, (int) position.getY() - 20, 40, 40);
        Font font = new Font(Window.FONT_NAME, Font.PLAIN, 20);
        graphics.setFont(font);
        graphics.setColor(symbolColor);
        TextLayout textLayout = new TextLayout(type.getSymbol(), font, graphics.getFontRenderContext());
        double xTextOffset = textLayout.getBounds().getWidth()/2;
        double yTextOffset = textLayout.getAscent()/2;
        graphics.drawString(type.getSymbol(), (int) (position.getX() - xTextOffset), (int) (position.getY() + yTextOffset - 4));
    }

    @Override
    public void setZIndex(int z) {}

    @Override
    public int getZIndex() {
        return 20;
    }

    public void kill() {
        Main.getGame().unregister(this);
        Rectangle puffBound = new Rectangle((int) (position.getX() - 25), (int) (position.getY() - 25), 50, 50);
        ParticleGenerator puff = new ParticleGenerator(50, 30, puffBound, 15, 40, 30, 150, 2, 3, 0x117711);
        puff.setRecurring(false);
        Main.getGame().register(puff);
    }

    public enum PowerType {

        DOUBLE_DAMAGE(25, 0x880000, "D"),
        FIRE_RATE(25, 0x888800, "R"),
        BIG_BOOM(75, 0x884422, "B"),
        DOUBLE_SCORE(100, 0x008800, "S"),
        INVINCIBLE(10, 0x440088, "I"),
        ADD_SCORE(250, 0x008844, "+"),
        FREEZER(75, 0x008888, "F");

        private static final int totalWeight;
        static {
            totalWeight = IntStream.of(Arrays.stream(values()).mapToInt(PowerType::getWeight).toArray()).sum();
        }

        private final int weight;
        private final int rgb;
        private final String symbol;

        PowerType(int weight, int rgb, String symbol) {
            this.weight = weight;
            this.rgb = rgb;
            this.symbol = symbol;
        }

        public int getWeight() {
            return weight;
        }

        public int getHexRGB() {
            return rgb;
        }

        public String getSymbol() {
            return symbol;
        }

        public static PowerType getRandom() {
            int value = rnd.nextInt(totalWeight);
            int cumulative = 0;
            for (int i = 0; i<values().length; i++) {
                PowerType type = (PowerType) Arrays.stream(values()).sorted(Comparator.comparingInt(PowerType::getWeight)).toArray()[i];
                cumulative += type.weight;
                if (value < cumulative) {
                    return type;
                }
            }
            return DOUBLE_SCORE;
        }

    }

}
