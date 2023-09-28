package ru.robert_grammy.astro_space.utils.rnd;

public class RandomDoubleValueRange extends RandomValueRange<Double> {

    public RandomDoubleValueRange(Double origin, Double bound) {
        super(origin, bound);
    }

    @Override
    public Double randomValue() {
        return RND.nextDouble(origin, bound);
    }
}
