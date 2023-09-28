package ru.robert_grammy.astro_space;

import ru.robert_grammy.astro_space.game.Game;

public class Main {

    private static final Game game = new Game();

    public static void main(String... args) {
        game.play();
    }

    public static Game getGame() {
        return game;
    }

}
