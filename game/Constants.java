package game;

import java.awt.*;
import java.util.Random;

/**
 * Constant class holding static constants variables used throughout the game.
 */
public class Constants {
    public static final int FRAME_HEIGHT = GameWindow.HEIGHT; //Height of the Frame
    public static final int FRAME_WIDTH = GameWindow.WIDTH; //Width of the Frame
    public static final Dimension FRAME_SIZE = new Dimension(Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT); //Frame dimensions combined together


    public static final int WORLD_HEIGHT = FRAME_HEIGHT * 2; //Height of the Frame
    public static final int WORLD_WIDTH = FRAME_WIDTH * 2; //Width of the Frame

    public static final int MID_WORLD_X = WORLD_WIDTH / 2; //middle point of the world (X value)
    public static final int MID_WORLD_Y = WORLD_HEIGHT / 2; //middle point of the world (Y value)

    //sleep time between two frames
    public static final int DELAY = 10;  //in milliseconds
    public static final double DT = DELAY / 1000.0;  //in seconds
    public static final Random RANDOM = new Random(); //random object used to generate random variables
    public static final String SAVE_FILE = "save.bin"; //name of the save file
    public static final String SCORES_FILE = "scores.txt";


    public static final int MAX_PLAYER_SPEED = 500; //maximum speed of the player

    public static final Difficulty DEFAULT_DIFFICULTY = new Difficulty(20, 10, 60); //default difficulty of the game
    public static final int MAX_PLAYER_NUMBER = 10; //maximum number of players playing simultaneously
    public static final String SERVER_IP = "167.99.82.164"; //server IP for multiplayer functionality
    public static final String REMOTE_SERVER_NAME = "AsteroidsGameServer"; //server IP for multiplayer functionality
    public static final int SERVER_PORT = 1099; //server PORT for multiplayer functionality

}
