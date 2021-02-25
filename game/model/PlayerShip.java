package game.model;

import game.controller.Action;
import game.Constants;
import game.controller.Controller;
import game.Game;
import utilities.SoundManager;
import utilities.Vector2D;

import java.awt.*;

/**
 * Class representing player ship controlled by a user.
 */
public class PlayerShip extends Ship {
    private transient Game game; //reference to the current game object
    private boolean respawning = false; //dictates if the ship is in state of respawning
    public long respawnTime = 0; //current respawn time
    public int maxPods = 3; //maximum number of pods to be summoned
    public HelperPod pod = null; //helper pod object

    /**
     * Constructor, calls constructor of the superclass and
     * assigns values to Thrust arrays ("flames" when a ship is thrusting). Turns the shield on at the beginning.
     *
     * @param ctrl - Controller assigned to the ship
     * @param g    - game object reference
     */
    public PlayerShip(Controller ctrl, String playerName, Game g) {
        super(Color.BLUE, new Vector2D(Constants.MID_WORLD_X, Constants.MID_WORLD_Y), new Vector2D(0, 0), new Vector2D(0, -1), 5, ctrl);
        this.name = playerName;
        this.game = g;
        XPTHRUST = new int[]{-8, 0, 8, 0};
        YPTHRUST = new int[]{10, 0, 10, 20};
        shield = 3;
        game = g;
        timeToShoot = 1000;
    }

    public String getName() {
        return name;
    }

    /**
     * Creates the Helper Pod object.
     */
    public void mkPod(Game game) {
        pod = HelperPod.createPod(this, game);
    }


    public HelperPod getPod() {
        HelperPod p = pod;
        pod = null;
        return p;
    }

    /**
     * @return the number of shield lives left
     */
    public int getShield() {
        return shield;
    }

    /**
     * Getter and setter of respawning state.
     */
    public boolean getRespawning() {
        return respawning;
    }

    public void setRespawning(boolean res) {
        respawning = res;
    }

    /**
     * Makes sure it doesn't collide with its own bullets
     *
     * @param other - second Game Object to check
     * @return true if can collide with the other GameObject, else false
     */
    @Override
    public boolean canHit(GameObject other) {
        if (other instanceof Bullet)
            return other.canHit(this);

        return super.canHit(other);
    }

    /**
     * Depletes shield if it is on. If the shield is 0 - turns it off.
     * Doesn't take a hit if the ship is in invincible state.
     */
    @Override
    public void hit() {
        if (shield > 0 && shielding) {
            shield--;
            if (shield == 0) {
                shielding = false;
                ctrl.action().shieldOn = false;
            }
        } else if (!invincible) {
            hit = true;
        }
    }

    /**
     * Checks if there is action.pod active (P key), if yes - create a new Helper Pod.
     */
    @Override
    public void update() {
        if (ctrl.action().shieldOn)
            shielding = true;
        else
            shielding = false;

        super.update();
        Action action = ctrl.action();
        if (action.pod && maxPods > 0) {
            mkPod(game);
            action.pod = false;
            maxPods--;
        }
    }

    /**
     * Creates a bullet.
     */
    @Override
    public void mkBullet() {
        super.mkBullet();
        bullet = new Bullet(bulletPosVel[1], bulletPosVel[0], 7, "Player");
        SoundManager.play(SoundManager.fire);
    }
}
