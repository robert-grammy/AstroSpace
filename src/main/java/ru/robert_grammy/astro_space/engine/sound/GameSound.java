package ru.robert_grammy.astro_space.engine.sound;

import ru.robert_grammy.astro_space.Main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Objects;

public enum GameSound {

    BOOM(1F),
    DAMAGE(0.8F),
    GAME_OVER(1F),
    START_GAME(1F),
    GAS_ON(0.9F),
    FLY(0.8F),
    GAS_OFF(0.9F),
    SHOOT(0.9F),
    PUFF(1F);


    private static final String WAV_EXTENSION = ".wav";
    private static final String DIR = "assets/sound/";

    private final String path = DIR + name().toLowerCase() + WAV_EXTENSION;
    private Sound sound;

    GameSound(float baseVolume) {
        try {
            sound = new Sound(getStream());
            sound.setVolume(baseVolume);
        } catch (UnsupportedAudioFileException | IOException e) {
            Main.getGame().getGameDebugger().console(e);
        }
    }

    public AudioInputStream getStream() throws UnsupportedAudioFileException, IOException {
        return AudioSystem.getAudioInputStream(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream(path)));
    }

    public Sound get() {
        return Objects.requireNonNull(sound);
    }
    
    public Sound getNewInstance() {
        Sound sound = null;
        try {
            sound = new Sound(getStream());
        }  catch (UnsupportedAudioFileException | IOException e) {
            Main.getGame().getGameDebugger().console(e);
        }
        return Objects.requireNonNull(sound);
    }

}
