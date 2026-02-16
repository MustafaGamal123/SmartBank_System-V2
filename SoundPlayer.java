/**
 * SoundPlayer: Audio playback utility class
 * 
 * Responsibilities:
 * - Load and play audio files from disk
 * - Handle audio playback errors gracefully
 * 
 * Supported Formats:
 * - WAV (WAVE)
 * - AIFF (Audio Interchange File Format)
 * - AU (Sun Audio)
 * - Other formats supported by Java Sound API
 * 
 * Note:
 * - Non-blocking playback (uses system audio thread)
 * - Single-file playback (can be extended for playlists)
 * - May be redundant with SoundManager in Main.java (consider consolidation)
 * 
 * @author Mostafa Gamal
 * @version 1.0.0
 */

import javax.sound.sampled.*;
import java.io.File;

public class SoundPlayer {
    /**
     * Plays an audio file from the given path
     * Creates a clip, loads audio, and starts playback
     * Playback runs on system audio thread
     * 
     * @param soundFilePath Path to the audio file to play
     */
    public static void playSound(String soundFilePath) {
        try {
            // Create audio input stream from file
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(new File(soundFilePath));
            
            // Create and open clip
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            
            // Start playback
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

