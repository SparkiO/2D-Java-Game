package game.controller;

import utilities.SoundManager;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Keys class that is used to get keyboard input from the user and respond it appropriately.
 */
public class Keys extends KeyAdapter implements Controller {
    public Action action; //action made by the user

    public Keys() {
        action = new Action();
    }

    public Action action() {
        return action;
    }

    /**
     * Method that gets keyboard input when a key is pressed and responds appropriately.
     *
     * @param e - indicates that a keystroke occurred
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP: //thrusting (moving in a direction of the ship)
                action.thrust = 1;
                SoundManager.startThrust();
                break;
            case KeyEvent.VK_LEFT: //turning left
                action.turn = -1;
                break;
            case KeyEvent.VK_RIGHT: //turning right
                action.turn = +1;
                break;
            case KeyEvent.VK_SPACE: //shooting
                action.shoot = true;
                break;
        }
    }

    /**
     * Method that gets keyboard input when a key is released and responds appropriately.
     *
     * @param e - indicates that a keystroke occurred
     */
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP: //stops moving forward
                action.thrust = 0;
                break;
            case KeyEvent.VK_LEFT: //stops turning left
                action.turn = 0;
                break;
            case KeyEvent.VK_RIGHT: //stops turning right
                action.turn = 0;
                break;
            case KeyEvent.VK_SPACE: //stops shooting
                action.shoot = false;
                break;
            case KeyEvent.VK_S: //activates or deactivates the shield
                action.shieldOn = !action.shieldOn;
                break;
            case KeyEvent.VK_P: //summons Helper Pod
                action.pod = !action.pod;
                break;


            case KeyEvent.VK_ESCAPE: //shows pause menu
                action.pause = !action.pause;
                break;
            case KeyEvent.VK_F5: //quick save
                action.save = true;
                break;
            case KeyEvent.VK_F6: //quick load
                action.load = true;
                break;
            case KeyEvent.VK_F12: //force exit
                action.exit = true;
                break;
        }
    }
}
