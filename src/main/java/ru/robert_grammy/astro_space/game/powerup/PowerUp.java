package ru.robert_grammy.astro_space.game.powerup;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.geometry.Vector;
import ru.robert_grammy.astro_space.engine.sound.GameSound;
import ru.robert_grammy.astro_space.game.background.ParticleGenerator;
import ru.robert_grammy.astro_space.graphics.Window;
import ru.robert_grammy.astro_space.utils.rnd.RandomIntegerValueRange;
import ru.robert_grammy.astro_space.utils.rnd.RandomValueRange;

import java.awt.*;
import java.awt.font.TextLayout;
import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

public class PowerUp implements Renderable {

    public static final Font DEFAULT_POWER_UP_FONT = new Font(Window.FONT_NAME, Font.PLAIN, 20);
    public static final Stroke DEFAULT_POWER_UP_STROKE = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    public static final RandomIntegerValueRange RND_SCORE_EARN_RANGE = new RandomIntegerValueRange(1, 11);
    public static final int SIZE = 40;
    public static final int BASE_SCORE_EARN_MULTIPLY = 50;
    private static final int BASE_HEX_COLOR = 0xFFFFFF;
    private static final int Z_INDEX = 20;
    private static final RandomIntegerValueRange X_RANGE = new RandomIntegerValueRange(25, Main.getGame().getWindow().getBufferWidth() - 25);
    private static final RandomIntegerValueRange Y_RANGE = new RandomIntegerValueRange(25, Main.getGame().getWindow().getBufferHeight() - 25);
    private static final double FADE_SPEED = .15;
    private final Vector position;
    private final PowerType type;
    private static  Color fill;
    private static  Color outline;
    private static  Color symbolColor;
    private double alpha = .0;

    public PowerUp() {
        this(PowerType.getRandom());
    }

    public PowerUp(PowerType type) {
        position = new Vector(
                X_RANGE.randomValue(),
                Y_RANGE.randomValue()
        );
        this.type = type;
    }

    public PowerType getType() {
        return type;
    }

    public Vector getPosition() {
        return position;
    }

    @Override
    public void render(Graphics2D graphics) {
        if (alpha < 255) {
            alpha += FADE_SPEED;
            fill = new Color(type.getHexRGB() + ((int) (alpha >= 255 ? 255 : alpha) << 24), true);
            outline = new Color(BASE_HEX_COLOR + ((int) (alpha >= 255 ? 255 : alpha) << 24), true);
            symbolColor = new Color(BASE_HEX_COLOR + ((int) (alpha >= 255 ? 255 : alpha) << 24), true);
        }
        graphics.setColor(fill);
        graphics.setStroke(DEFAULT_POWER_UP_STROKE);
        graphics.fillOval((int) position.getX() - (SIZE / 2), (int) position.getY() - (SIZE / 2), SIZE, SIZE);
        graphics.setColor(outline);
        graphics.drawOval((int) position.getX() - (SIZE / 2), (int) position.getY() - (SIZE / 2), SIZE, SIZE);
        graphics.setFont(DEFAULT_POWER_UP_FONT);
        graphics.setColor(symbolColor);
        TextLayout textLayout = new TextLayout(type.getSymbol(), DEFAULT_POWER_UP_FONT, graphics.getFontRenderContext());
        double xTextOffset = textLayout.getBounds().getWidth()/2;
        double yTextOffset = textLayout.getAscent()/2;
        graphics.drawString(type.getSymbol(), (int) (position.getX() - xTextOffset), (int) (position.getY() + yTextOffset - 4));
    }

    @Override
    public int getZIndex() {
        return Z_INDEX;
    }

    public void kill() {
        Main.getGame().unregister(this);
        ParticleGenerator puff = ParticleGenerator.createPuff(position);
        Main.getGame().register(puff);
        GameSound.PUFF.get().play();
    }

    public enum PowerType {

        DOUBLE_DAMAGE(50, 0x880000, "D", 1000),
        FIRE_RATE(25, 0x888800, "R", 850),
        BIG_BOOM(100, 0x884422, "B", 0),
        DOUBLE_SCORE(100, 0x008800, "S", 1500),
        INVINCIBLE(10, 0x440088, "I", 750),
        ADD_SCORE(300, 0x008844, "+", 0),
        FREEZER(75, 0x008888, "F", 1200);

        private static final int totalWeight = IntStream.of(Arrays.stream(values()).mapToInt(PowerType::getWeight).toArray()).sum();
        private final int weight;
        private final int rgb;
        private final int duration;
        private final String symbol;

        PowerType(int weight, int rgb, String symbol, int duration) {
            this.weight = weight;
            this.rgb = rgb;
            this.symbol = symbol;
            this.duration = duration;
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
        public int getDuration() {
            return duration;
        }

        public static PowerType getRandom() {
            int value = RandomValueRange.RND.nextInt(totalWeight);
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
