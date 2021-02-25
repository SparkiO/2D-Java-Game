package game.controller;

import java.io.Serializable;

/**
 * Action class representing the possible actions made by user.
 */
public class Action implements Serializable {
    public int thrust; // 0 = off, 1 = on; thrusting ship
    public int turn; // -1 = left turn, 0 = no turn, 1 = right turn; turning the ship around
    public boolean shoot; //shooting bullets
    public boolean save; //saving the game
    public boolean load; //loading the game
    public boolean newgame; //starting a new game
    public boolean exit; //exiting the game
    public boolean pause; //pausing the game and showing the pauseMenu
    public boolean resume; //resuming the game and hiding the pauseMenu
    public boolean shieldOn; //turning on/off shield
    public boolean pod = false; //determines if HelperPod is being spawned
}
