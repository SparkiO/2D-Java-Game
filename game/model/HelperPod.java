package game.model;

import game.controller.Controller;
import game.controller.RLController;
import game.Game;
import utilities.SoundManager;
import utilities.Vector2D;

import java.awt.*;

/**
 * Helper Pod that helps player by attacking enemies. Acts and learns using Reinforcement Learning Controller.
 */
public class HelperPod extends Ship {
    private int lives = 3; //lives of the pod

    /**
     * Constructor of the Ship object. Calls the constructor of superclass (GameObject).
     *
     * @param color     - colour of the ship
     * @param position  - position of the ship when spawned
     * @param velocity  - velocity of the ship when spawned
     * @param direction - direction of the ship when spawned
     * @param radius    - radius of the ship (used in Collision Handling)
     * @param ctrl      - controller of the ship
     */
    public HelperPod(Color color, Vector2D position, Vector2D velocity, Vector2D direction, double radius, Controller ctrl) {
        super(color, position, velocity, direction, radius, ctrl);
        mag_acc = 300; //sets acceleration to 300
        timeToShoot = 4000;

    }


    /**
     * Creates the HelperPod object.
     *
     * @param ship - playership reference
     * @param game - game reference
     * @return created HelperPod object
     */
    public static HelperPod createPod(Ship ship, Game game) {
        RLController rlController = new RLController(game, ship);
        rlController.init();

        //puts the pod behind the ship
        Vector2D position = new Vector2D(ship.position.x + 40, ship.position.y + 40);
        HelperPod pod = new HelperPod(Color.BLACK, position, new Vector2D(ship.velocity), new Vector2D(ship.direction), ship.radius / 4, rlController);
        rlController.setPod(pod);
        return pod;
    }

    /**
     * Makes sure it doesn't collide with its own bullets.
     *
     * @param other - second Game Object to check
     * @return boolean if the object can collide or not with the other GameObject
     */
    @Override
    public boolean canHit(GameObject other) {
        if (other instanceof Bullet)
            return other.canHit(this);

        return super.canHit(other);
    }

    /**
     * If this gets hit, subtract from lives, if there is no life left - die.
     */
    @Override
    public void hit() {
        if (lives <= 1) this.dead = true;
        else lives--;
    }

    /**
     * Creates a bullet when the ship shots.
     */
    @Override
    public void mkBullet() {
        super.mkBullet();
        bullet = new Bullet(bulletPosVel[1], bulletPosVel[0], 7, "Pod");
        SoundManager.play(SoundManager.fire);
    }
}
