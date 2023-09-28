package ru.robert_grammy.astro_space.engine.sound;

import ru.robert_grammy.astro_space.utils.GameDebugger;

import javax.sound.sampled.*;
import java.io.IOException;

public class Sound implements AutoCloseable {

    private AudioInputStream stream;
    private Clip clip;
    private FloatControl volumeControl;
    private boolean playing = false;

    Sound(AudioInputStream stream) {
        try {
            this.stream = stream;
            clip = AudioSystem.getClip();
            clip.open(stream);
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) playing = false;
            });
            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        } catch (LineUnavailableException | IOException e) {
            GameDebugger.console(e);
        }
    }

    @Override
    public void close() throws Exception {
        clip.close();
        stream.close();
    }

    public boolean isPlaying() {
        return playing;
    }

    public void play(boolean breakOld) {
        if (breakOld) {
            stopAndReset();
        }
        start();
    }

    public void play() {
        play(true);
    }

    private void start() {
        if (!playing) {
            clip.start();
            playing = true;
        }
    }

    public int getClipPosition() {
        return clip.getFramePosition();
    }

    public int getClipLength() {
        return clip.getFrameLength();
    }

    public double getClipProgress() {
        double length = clip.getFrameLength();
        double position = clip.getFramePosition();
        return position / length;
    }

    public boolean isEnded() {
        return getClipPosition() == getClipLength();
    }

    public void loop() {
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void loop(int count) {
        clip.loop(count);
    }

    public void stop() {
        clip.stop();
        playing = false;
    }

    public void stopAndReset() {
        clip.stop();
        clip.setFramePosition(0);
        playing = false;
    }

    public void reset() {
        clip.setFramePosition(0);
    }

    public void setVolume(float x) {
        if (x<0) x = 0;
        if (x>1) x = 1;
        float min = volumeControl.getMinimum();
        float max = volumeControl.getMaximum();
        volumeControl.setValue((max-min)*x+min);
    }

    public float getVolume() {
        float value = volumeControl.getValue();
        float min = volumeControl.getMinimum();
        float max = volumeControl.getMaximum();
        return (value-min)/(max-min);
    }

}
