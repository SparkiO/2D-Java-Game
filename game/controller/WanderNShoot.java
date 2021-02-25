package game.controller;

import game.model.PlayerShip;
import utilities.Vector2D;

/**
 * Wander And Shoot type of controller, used by EnemyShips. Will try to find the player ship, if it is close - starts shooting.
 */
public class WanderNShoot implements Controller {
    Action action = new Action(); //action made by the enemy

    private Vector2D thisPosition; //position of object that implements this controller
    private Vector2D thisDirection; //direction of the object
    private PlayerShip player; //player ship

    public WanderNShoot(Vector2D thisPosition, PlayerShip player, Vector2D thisDirection) {
        this.thisPosition = thisPosition;
        this.player = player;
        this.thisDirection = thisDirection;
    }

    /**
     * Sets actions to make accordingly to player's distance.
     *
     * @return appropriate Action object with actions to execute.
     */
    @Override
    public Action action() {
        //calculate distance x and y from this object to player's object
        double distanceX = Math.abs(thisPosition.x - player.position.x);
        double distanceY = Math.abs(thisPosition.y - player.position.y);
        double distanceTotal = distanceX + distanceY;

        //if player is not dead, try to find him
        if (!player.dead) {
            if (distanceTotal < 1000) { //if player is near change direction to face player and start shooting
                double xDirection = distanceX / distanceTotal;
                double yDirection = distanceY / distanceTotal;

                if (player.position.x < thisPosition.x)
                    xDirection *= -1;
                if (player.position.y < thisPosition.y)
                    yDirection *= -1;

                thisDirection.set(xDirection, yDirection);
                action.shoot = true;
            }
        } else //if player is not near, do not shoot
            action.shoot = false;

        action.thrust = 1;
        return action;
    }
}
