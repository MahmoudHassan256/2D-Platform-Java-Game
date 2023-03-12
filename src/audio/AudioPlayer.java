package audio;

import utilz.LoadSave;

import javax.sound.sampled.*;
import java.io.File;
import java.net.URL;
import java.util.Random;

public class AudioPlayer {
    public static int MENU_1 = 0, LEVEL_1 = 1, LEVEL_2 = 2;
    public static int DIE = 0, JUMP = 1, GAMEOVER = 2, LVL_COMPLETED = 3, ATTACK_ONE = 4, ATTACK_TWO = 5, ATTACK_THREE = 6;
    private Clip[] songs, effects;
    private int currentSongID;
    private float volume = 1f;
    private boolean songMute, effectMute;
    private Random rand = new Random();

    public AudioPlayer() {
        loadSongs();
        loadEffects();
        playSong(MENU_1);
    }

    private void loadSongs() {
        String[] names = {"menu", "level1", "level2"};
        songs = new Clip[names.length];
        for (int i = 0; i < songs.length; i++) {
            songs[i] = getClip(names[i]);
        }
    }

    private void loadEffects() {
        String[] names = {"die", "jump", "gameover", "lvlcompleted", "attack1", "attack2", "attack3"};
        effects = new Clip[names.length];
        for (int i = 0; i < effects.length; i++) {
            effects[i] = getClip(names[i]);
        }
        updateEffectsVolume();
    }

    private Clip getClip(String name) {
        URL url = getClass().getResource("/audio/" + name + ".wav");
        AudioInputStream audio;
        try {
            audio = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void toggleSongMute() {
        this.songMute = !songMute;
        for (Clip c : songs) {
            BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
            booleanControl.setValue(songMute);
        }
        if (!effectMute)
            playEffect(JUMP);
    }

    public void setVolume(float volume) {
        this.volume = volume;
        updateSongVolume();
        updateEffectsVolume();
    }

    public void stopSong() {
        if (songs[currentSongID].isActive())
            songs[currentSongID].stop();
    }

    public void setLevelSong(int lvlIndex) {
        if (lvlIndex % 2 == 0) {
            playSong(LEVEL_1);
        } else
            playSong(LEVEL_2);
    }

    public void lvlCompleted() {
        stopSong();
        playEffect(LVL_COMPLETED);
    }

    public void playAttackSound() {
        int start = 4;
        start += rand.nextInt(3);
        playEffect(start);
    }

    public void playEffect(int effect) {
        effects[effect].setMicrosecondPosition(0);
        effects[effect].start();
    }

    public void playSong(int song) {
        stopSong();
        currentSongID = song;
        updateSongVolume();
        songs[currentSongID].setMicrosecondPosition(0);
        songs[currentSongID].loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void toggleEffectMute() {
        this.effectMute = !effectMute;
        for (Clip c : effects) {
            BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
            booleanControl.setValue(effectMute);
        }
    }

    private void updateSongVolume() {
        FloatControl gainControl = (FloatControl) songs[currentSongID].getControl(FloatControl.Type.MASTER_GAIN);
        float range = gainControl.getMaximum() - gainControl.getMinimum();
        float gain = (range * volume) + gainControl.getMinimum();
        gainControl.setValue(gain);
    }

    private void updateEffectsVolume() {
        for (Clip clip : effects) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * volume) + gainControl.getMinimum();
            gainControl.setValue(gain);
        }

    }
}
