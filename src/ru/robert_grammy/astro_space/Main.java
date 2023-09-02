package ru.robert_grammy.astro_space;

import ru.robert_grammy.astro_space.game.Game;
import ru.robert_grammy.astro_space.game.player.Player;
import ru.robert_grammy.astro_space.game.background.StarsLight;

public class Main {

    public final static Game game = new Game();

    public static void main(String[] args) {
        gameInitialize();
        game.play();
    }

    private static void gameInitialize() {
        Player player = new Player(640,360);
        game.register(player);

        StarsLight light = new StarsLight();
        game.register(light);
    }

}
