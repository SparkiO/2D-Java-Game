package utilities;

import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import java.io.File;

/**
 * SoundManager class that deals with the sounds in the game.
 */
public class SoundManager {
    private static boolean thrusting = false; //determines weather the player is moving the ship or not
    private final static String path = "sounds/"; //path to sounds folder

    public static boolean on = false; //determines if the game is in state of "play" or "pause"

    // note: having too many clips open may cause
    // "LineUnavailableException: No Free Voices"
    public static Clip[] bullets;

    public static Clip bangLarge;
    public static Clip bangMedium;
    public static Clip bangSmall;
    public static Clip beat1;
    public static Clip beat2;
    public static Clip extraShip;
    public static Clip fire;
    public static Clip saucerBig;
    public static Clip saucerSmall;
    public static Clip thrust;

    /**
     * Turns on the sound manager and assigns the proper sounds from files to the Clip objects.
     */
    public static void init() {
        on = true;
        bangLarge = getClip("bangLarge");
        bangMedium = getClip("bangMedium");
        bangSmall = getClip("bangSmall");
        extraShip = getClip("extraShip");
        saucerBig = getClip("saucerBig");
        beat2 = getClip("beat2");
        beat1 = getClip("beat1");
        thrust = getClip("thrust");
        fire = getClip("fire");
        saucerSmall = getClip("saucerSmall");

        bullets = new Clip[15];
        for (int i = 0; i < bullets.length; i++)
            bullets[i] = getClip("fire");
    }

    /**
     * Plays the Clip if the game is not paused.
     */
    public static void play(Clip clip) {
        if (on) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    /**
     * Gets the Clip from a file.
     * @param filename name of the file
     * @return Clip object containing sounds from the file
     */
    private static Clip getClip(String filename) {
        Clip clip = null;
        try {
            clip = AudioSystem.getClip();
            AudioInputStream sample = AudioSystem.getAudioInputStream(new File(path
                    + filename + ".wav"));
            clip.open(sample);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clip;
    }

    /**
     * Plays "thrust" Clip in a loop.
     */
    public static void startThrust() {
        if (!thrusting) {
            thrust.loop(-1);
            thrusting = true;
        }
    }

    /**
     * Stops playing the "thrust" Clip.
     */
    public static void stopThrust() {
        thrust.loop(0);
        thrusting = false;
    }

}
