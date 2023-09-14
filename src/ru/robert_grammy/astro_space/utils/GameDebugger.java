package ru.robert_grammy.astro_space.utils;

import ru.robert_grammy.astro_space.game.Game;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GameDebugger {

    private Game game;
    private Logger logger;

    private int ups = 0;
    private int fps = 0;

    boolean displayFps = false;
    boolean displayUps = false;
    boolean drawPlayerLineBound = false;
    boolean drawPlayerShapeLines = false;
    boolean drawAsteroidCircleBound = false;
    boolean drawAsteroidLineBound = false;
    boolean drawAsteroidShapeLines = false;
    boolean drawAsteroidParams = false;

    public GameDebugger(Game game) {
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

    public void setDrawPlayerLineBound(boolean value) {
        drawPlayerLineBound = value;
    }

    public boolean isDrawPlayerLineBound() {
        return drawPlayerLineBound;
    }

    public void setDrawPlayerShapeLines(boolean value) {
        drawPlayerShapeLines = value;
    }

    public boolean isDrawPlayerShapeLines() {
        return drawPlayerShapeLines;
    }

    public void setDrawAsteroidCircleBound(boolean value) {
        drawAsteroidCircleBound = value;
    }

    public boolean isDrawAsteroidCircleBound() {
        return drawAsteroidCircleBound;
    }

    public void setDrawAsteroidLineBound(boolean value) {
        drawAsteroidLineBound = value;
    }

    public boolean isDrawAsteroidLineBound() {
        return drawAsteroidLineBound;
    }

    public void setDrawAsteroidShapeLines(boolean value) {
        drawAsteroidShapeLines = value;
    }

    public boolean isDrawAsteroidShapeLines() {
        return drawAsteroidShapeLines;
    }

    public void setDrawAsteroidParams(boolean value) {
        drawAsteroidParams = value;
    }

    public boolean isDrawAsteroidParams() {
        return drawAsteroidParams;
    }
}
