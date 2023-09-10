package ru.robert_grammy.astro_space.utils;

import ru.robert_grammy.astro_space.game.Game;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GameLogger {

    private Game game;
    private Logger logger;

    private int ups = 0;
    private int fps = 0;

    boolean displayFps = false;
    boolean displayUps = false;
    boolean drawLineBound = false;
    boolean drawPlayerShapeLines = false;

    public GameLogger(Game game) {
        this.game = game;
        logger = Logger.getLogger(Game.class.getName());
    }

    public boolean isDisplayFps() {
        return displayFps;
    }

    public void setDisplayFps(boolean value) {
        displayFps = value;
    }

    public void addFps() {
        fps++;
    }

    public void resetFps() {
        fps = 0;
    }

    public int getFps() {
        return fps;
    }

    public boolean isDisplayUps() {
        return displayUps;
    }

    public void setDisplayUps(boolean value) {
        displayUps = value;
    }

    public void addUps() {
        ups++;
    }

    public void resetUps() {
        ups = 0;
    }

    public int getUps() {
        return ups;
    }

    public void console(String text) {
        logger.log(Level.INFO, text);
    }

    public void setDrawLineBound(boolean value) {
        drawLineBound = value;
    }

    public boolean isDrawLineBound() {
        return drawLineBound;
    }

    public void setDrawPlayerShapeLines(boolean value) {
        drawPlayerShapeLines = value;
    }

    public boolean isDrawPlayerShapeLines() {
        return drawPlayerShapeLines;
    }

}
