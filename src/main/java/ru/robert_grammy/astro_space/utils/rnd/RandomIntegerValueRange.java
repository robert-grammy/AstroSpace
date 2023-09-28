package ru.robert_grammy.astro_space.utils.rnd;

public class RandomIntegerValueRange extends RandomValueRange<Integer> {

    public RandomIntegerValueRange(Integer origin, Integer bound) {
        super(origin, bound);
    }

    @Override
    public Integer randomValue() {
        return RND.nextInt(origin, bound);
    }

}
