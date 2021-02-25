package game.controller;

import game.model.GameObject;
import game.model.HelperPod;
import game.model.PlayerShip;

/**
 * Seek And Shoot type of controller, used by Helper Pod. Will make Actions depending on the Reinforcement Learning controller variables passed.
 */
public class SeekNShootTarget implements Controller {
    private GameObject target; //closest target
    private boolean shoot; //determines if shooting should be turned on or off
    private boolean seek; //determines if the Pod should seek target
    private boolean follow; //determines if the Pod should follow the player
    private PlayerShip player; //player ship
    private Action action; //action with appropriate commands to take
    private HelperPod pod; //Helper Pod object reference, used to get its position and direction

    public SeekNShootTarget(boolean shoot, boolean seek, Action action, GameObject target, PlayerShip ps) {
        this.target = target;
        this.shoot = shoot;
        this.seek = seek;
        this.action = action;
        this.player = ps;
        this.pod = (HelperPod) target;
    }

    /**
     * Setters - called by Reinforcement Learning controller.
     */
    void setTarget(GameObject target) {
        this.target = target;
    }

    void setShoot(boolean shoot) {
        this.shoot = shoot;
    }

    void setSeek(boolean seek) {
        this.seek = seek;
    }


    /**
     * Sets actions to make accordingly to value of the boolean attributes and player's position.
     *
     * @return appropriate Action object with actions to execute.
     */
    @Override
    public Action action() {
        if (shoot)
            action.shoot = true;

        //calculate distance to player
        double distanceX = Math.abs(pod.position.x - player.position.x);
        double distanceY = Math.abs(pod.position.y - player.position.y);
        double distanceTotal = distanceX + distanceY;

        if (distanceTotal > 800) { //if distance is high, try to find the player and follow him
            follow = true;
            shoot = false;
            double xDirectionToPlayer = distanceX / distanceTotal;
            double yDirectionToPlayer = distanceX / distanceTotal;

            if (player.position.x < pod.position.x)
                xDirectionToPlayer *= -1;
            if (player.position.y < pod.position.y)
                yDirectionToPlayer *= -1;

            pod.direction.set(xDirectionToPlayer, yDirectionToPlayer);
        } else //if the distance is low, do not follow the player
            follow = false;

        //if there is a target found, change Pod's direction to face the target
        if (!follow && seek && target != null && target != player) {
            distanceX = Math.abs(target.position.x - pod.position.x);
            distanceY = Math.abs(target.position.y - pod.position.y);
            distanceTotal = distanceX + distanceY;

            double xDirectionToTarget = distanceX / distanceTotal;
            double yDirectionToTarget = distanceY / distanceTotal;

            if (target.position.x < pod.position.x)
                xDirectionToTarget *= -1;
            if (target.position.y < pod.position.y)
                yDirectionToTarget *= -1;

            pod.direction.set(xDirectionToTarget, yDirectionToTarget);
        }

        action.thrust = 1;
        return action;
    }
}
