package ru.robert_grammy.astro_space.game.background;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.engine.Renderable;
import ru.robert_grammy.astro_space.engine.Updatable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParticleGenerator implements Renderable, Updatable {

    private int zIndex;

    private final List<Particle> particles = new ArrayList<>();
    private List<Particle> duplicate;

    public ParticleGenerator(int count, int zIndex) {
        this.zIndex = zIndex;
        for (int i = 0; i<count; i++) {
            particles.add(new Particle(this));
        }
        updateParticlesList();
    }

    public ParticleGenerator(int count, int zIndex, Rectangle bound, int sizeOrigin, int sizeBound, double alphaOrigin, double alphaBound, double fadeSpeedOrigin, double fadeSpeedBound, int hexColor) {
        this.zIndex = zIndex;
        for (int i = 0; i<count; i++) {
            particles.add(new Particle(this, bound, sizeOrigin, sizeBound, alphaOrigin, alphaBound, fadeSpeedOrigin, fadeSpeedBound, hexColor));
        }
    }

    @Override
    public void render(Graphics2D graphics) {
        if (Optional.ofNullable(duplicate).isPresent())
            duplicate.forEach(particle -> particle.render(graphics));
    }

    @Override
    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
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

}
