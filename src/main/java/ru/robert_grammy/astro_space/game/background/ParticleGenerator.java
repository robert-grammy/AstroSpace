package ru.robert_grammy.astro_space.game.background;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;
import ru.robert_grammy.astro_space.engine.geometry.Vector;
import ru.robert_grammy.astro_space.utils.rnd.RandomDoubleValueRange;
import ru.robert_grammy.astro_space.utils.rnd.RandomIntegerValueRange;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParticleGenerator implements Renderable, Updatable {

    public static final int BASE_STARS_COUNT = 375;
    public static final int STARS_Z_INDEX = 0;
    private static final RandomIntegerValueRange PUFF_SIZE_RANGE = new RandomIntegerValueRange(15, 40);
    private static final RandomIntegerValueRange EXPLOSION_SIZE_RANGE = new RandomIntegerValueRange(15, 40);
    private static final RandomIntegerValueRange SMOKE_SIZE_RANGE = new RandomIntegerValueRange(30, 60);
    private static final RandomIntegerValueRange FIRE_TRAIL_COLOR_OFFSET_RANGE = new RandomIntegerValueRange(0x11, 0x55);
    private static final RandomIntegerValueRange FIRE_TRAIL_SIZE_RANGE = new RandomIntegerValueRange(3, 12);
    private static final RandomDoubleValueRange EXPLOSION_ALPHA_RANGE = new RandomDoubleValueRange(30.0, 200.0);
    private static final RandomDoubleValueRange EXPLOSION_FADE_SPEED_RANGE = new RandomDoubleValueRange(2.0, 5.0);
    private static final RandomDoubleValueRange SMOKE_ALPHA_RANGE = new RandomDoubleValueRange(40.0, 250.0);
    private static final RandomDoubleValueRange SMOKE_FADE_SPEED_RANGE = new RandomDoubleValueRange(1.0, 4.0);
    private static final RandomDoubleValueRange FIRE_TRAIL_ALPHA_RANGE = new RandomDoubleValueRange(50.0, 250.0);
    private static final RandomDoubleValueRange FIRE_TRAIL_FADE_SPEED_RANGE = new RandomDoubleValueRange(.75, 2.5);
    private static final RandomDoubleValueRange PUFF_ALPHA_RANGE = new RandomDoubleValueRange(30.0, 150.0);
    private static final RandomDoubleValueRange PUFF_FADE_SPEED_RANGE = new RandomDoubleValueRange(2.0, 3.0);
    private static final int FIRE_TRAIL_BOUND_SIZE = 7;
    private static final int FIRE_TRAIL_BASE_HEX_COLOR = 0x571B06;
    private static final int FIRE_TRAIL_PARTICLE_Z_INDEX = 30;
    private static final int DEFAULT_PARTICLES_COUNT = 50;
    private static final int DEFAULT_Z_INDEX = 100;
    private static final int SMOKE_HEX_COLOR = 0xEEFFEE;
    private static final int SMOKE_PARTICLE_SIZE = 40;
    private static final int PUFF_PARTICLE_COUNT = 50;
    private static final int PUFF_PARTICLE_SIZE = 30;
    private static final int PUFF_PARTICLE_COLOR = 0x117711;
    private static final int PUFF_PARTICLE_BOUND_SIZE = 50;

    private final int zIndex;
    private final List<Particle> particles = new ArrayList<>();
    private List<Particle> duplicate;

    public ParticleGenerator(int count, int zIndex) {
        this.zIndex = zIndex;
        for (int i = 0; i<count; i++) {
            particles.add(new Particle(this));
        }
        updateParticlesList();
    }

    public ParticleGenerator(int count, int zIndex, Rectangle bound, RandomIntegerValueRange sizeRange, RandomDoubleValueRange alphaRange, RandomDoubleValueRange fadeSpeedRange, int hexColor) {
        this.zIndex = zIndex;
        for (int i = 0; i<count; i++) {
            particles.add(new Particle(this, bound, sizeRange, alphaRange, fadeSpeedRange, hexColor));
        }
    }

    @Override
    public void render(Graphics2D graphics) {
        if (Optional.ofNullable(duplicate).isPresent())
            duplicate.forEach(particle -> particle.render(graphics));
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    @Override
    public void update() {
        if (particles.size() == 0) Main.getGame().unregister(this);
        updateParticlesList();
        duplicate.forEach(Particle::update);
    }

    public void remove(Particle particle) {
        particles.remove(particle);
    }

    public void setRecurring(boolean value) {
        if (!value) particles.forEach(particle -> particle.setRecurring(false));
    }

    private void updateParticlesList() {
        duplicate = new ArrayList<>(particles);
    }

    public static ParticleGenerator createExplosion(Vector position, int size, int hexColor) {
        Rectangle explosionBound = new Rectangle((int) position.getX() - (size / 2),  (int) position.getY() - (size / 2), size, size);
        return new ParticleGenerator(
                DEFAULT_PARTICLES_COUNT,
                DEFAULT_Z_INDEX,
                explosionBound,
                EXPLOSION_SIZE_RANGE,
                EXPLOSION_ALPHA_RANGE,
                EXPLOSION_FADE_SPEED_RANGE,
                hexColor
        );
    }

    public static ParticleGenerator createSmoke(Vector position) {
        Rectangle smokeBound = new Rectangle((int) position.getX() - (SMOKE_PARTICLE_SIZE / 2),  (int) position.getY() - (SMOKE_PARTICLE_SIZE / 2), SMOKE_PARTICLE_SIZE, SMOKE_PARTICLE_SIZE);
        return new ParticleGenerator(
                DEFAULT_PARTICLES_COUNT,
                DEFAULT_Z_INDEX,
                smokeBound,
                SMOKE_SIZE_RANGE,
                SMOKE_ALPHA_RANGE,
                SMOKE_FADE_SPEED_RANGE,
                SMOKE_HEX_COLOR
        );
    }

    public static Particle createTrailParticle(Vector position) {
        Rectangle trailBound = new Rectangle((int) (position.getX() - (FIRE_TRAIL_BOUND_SIZE / 2)), (int) (position.getY() - (FIRE_TRAIL_BOUND_SIZE / 2)), FIRE_TRAIL_BOUND_SIZE, FIRE_TRAIL_BOUND_SIZE);
        int hexColor = FIRE_TRAIL_BASE_HEX_COLOR + (FIRE_TRAIL_COLOR_OFFSET_RANGE.randomValue() << 8);
        Particle particle = new Particle(trailBound, FIRE_TRAIL_SIZE_RANGE, FIRE_TRAIL_ALPHA_RANGE, FIRE_TRAIL_FADE_SPEED_RANGE, hexColor);
        particle.setZIndex(FIRE_TRAIL_PARTICLE_Z_INDEX);
        particle.setRecurring(false);
        return particle;
    }

    public static ParticleGenerator createPuff(Vector position) {
        Rectangle puffBound = new Rectangle((int) (position.getX() - (PUFF_PARTICLE_BOUND_SIZE / 2)), (int) (position.getY() - (PUFF_PARTICLE_BOUND_SIZE / 2)), PUFF_PARTICLE_BOUND_SIZE, PUFF_PARTICLE_BOUND_SIZE);
        ParticleGenerator puff = new ParticleGenerator(PUFF_PARTICLE_COUNT, PUFF_PARTICLE_SIZE, puffBound, PUFF_SIZE_RANGE, PUFF_ALPHA_RANGE, PUFF_FADE_SPEED_RANGE, PUFF_PARTICLE_COLOR);
        puff.setRecurring(false);
        return puff;
    }

}
