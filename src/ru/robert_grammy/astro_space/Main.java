package ru.robert_grammy.astro_space;

import ru.robert_grammy.astro_space.game.Game;
import ru.robert_grammy.astro_space.game.Player;

public class Main {

    public final static Game game = new Game();

    public static void main(String[] args) {
        Player player = new Player(640,360);
        if (game.register(player)) game.play();
    }

}
