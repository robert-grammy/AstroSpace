package ru.robert_grammy.astro_space.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedHashSet;
import java.util.Set;

public class Keyboard implements KeyListener {

    private final Set<Integer> pressedKeys = new LinkedHashSet<>();
    private final Set<Integer> memorizedKeys = new LinkedHashSet<>();
    private int lastPressedKey = -1;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        lastPressedKey = e.getKeyCode();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
        memorizedKeys.remove(e.getKeyCode());
    }

    public boolean pressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }

    public boolean isMemorized(int keyCode) {
        return memorizedKeys.contains(keyCode);
    }
    public void memorizePress(int keyCode) {
        if (pressed(keyCode)) memorizedKeys.add(keyCode);
    }

    public int getLastPressedKey() {
        return lastPressedKey;
    }

}
