package ru.robert_grammy.astro_space.utils.rnd;

import java.util.Random;

public abstract class RandomValueRange<T extends Number> {

    public static final Random RND = new Random();

    protected final T origin;
    protected final T bound;

    protected RandomValueRange(T origin, T bound) {
        this.origin = origin;
        this.bound = bound;
    }

    public abstract T randomValue();

    public T getBound() {
        return bound;
    }

    public T getOrigin() {
        return origin;
    }

}
