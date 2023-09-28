package ru.robert_grammy.astro_space.engine.sound;

import ru.robert_grammy.astro_space.Main;
import ru.robert_grammy.astro_space.utils.GameDebugger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;

public enum GameSound {

    BOOM(1F),
    DAMAGE(0.8F),
    GAME_OVER(1F),
    START_GAME(1F),
    GAS_ON(0.9F),
    FLY(0.85F),
    GAS_OFF(0.9F),
    SHOOT(0.9F),
    PUFF(1F),
    BACKGROUND(0.85F);


    private static final String WAV_EXTENSION = ".wav";
    private static final String DIR = "assets/sound/";

    private final String path = DIR + name().toLowerCase() + WAV_EXTENSION;
    private final Sound sound;

    GameSound(float baseVolume) {
        sound = getNewInstance();
        sound.setVolume(baseVolume);
    }

    public AudioInputStream getStream() throws UnsupportedAudioFileException, IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream(path)));
        return AudioSystem.getAudioInputStream(bufferedInputStream);
    }

    public Sound get() {
        return Objects.requireNonNull(sound);
    }
    
    public Sound getNewInstance() {
        Sound sound = null;
        try {
            sound = new Sound(getStream());
        }  catch (UnsupportedAudioFileException | IOException e) {
            GameDebugger.console(e);
        }
        return Objects.requireNonNull(sound);
    }

    public static Thread playAfter(Sound current, Sound next, int loop, boolean breakOld, long waitTime) {
        return new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted() && !current.isEnded()) {
                    Thread.onSpinWait();
                }
                Thread.sleep(waitTime);
                next.loop(loop);
                next.play(breakOld);
            } catch (InterruptedException e) {
                GameDebugger.console(e);
            }
        });
    }

}
