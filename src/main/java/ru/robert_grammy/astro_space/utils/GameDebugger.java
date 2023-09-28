package ru.robert_grammy.astro_space.utils;

import ru.robert_grammy.astro_space.Main;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class GameDebugger {

    private GameDebugger() {}

    private static final Logger logger = Logger.getLogger(Main.getGame().getClass().getName());

    public static void console(String text) {
        logger.log(Level.INFO, text);
    }

    public static void console(Exception exception) {
        logger.log(Level.SEVERE, "Error while game loop: ", exception);
    }

}
