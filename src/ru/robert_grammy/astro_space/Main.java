package ru.robert_grammy.astro_space;

import ru.robert_grammy.astro_space.game.Game;

public class Main {

    private final static Game game = new Game();

    public static void main(String[] args) {
        while (true) {
            if (game.isRunning()) continue;
            game.play();
        }
    }

    public static Game getGame() {
        return game;
    }

}
