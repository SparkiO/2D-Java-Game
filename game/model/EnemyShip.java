package game.model;

import game.controller.Controller;
import game.controller.WanderNShoot;
import utilities.Vector2D;

import java.awt.*;

import static game.Constants.WORLD_HEIGHT;
import static game.Constants.WORLD_WIDTH;

/**
 * Class representing enemies in the game.
 */
public class EnemyShip extends Ship {
    public EnemyShip(Vector2D pos, Vector2D vel, Vector2D dir, int radius, Controller ctrl) {
        super(Color.RED, pos, vel, dir, radius, ctrl);
        timeToShoot = 3000;
    }

    /**
     * Method that makes sure the Enemy Ship do not collide with Asteroids or other Enemy Ship.
     *
     * @param other - reference to the other GameObject that collides
     * @return boolean whether or not the two objects can hit each other
     */
    @Override
    public boolean canHit(GameObject other) {
        if (other instanceof Asteroid || other instanceof EnemyShip)
            return false;
        if (other instanceof Bullet)
            return other.canHit(this);

        return super.canHit(other);
    }

    /**
     * Makes Enemy Ship in a random position and direction around the Game World.
     *
     * @param player - reference to the player object, needed to pass to WanderNShoot controller
     * @return - EnemyShip object
     */
    public static EnemyShip makeRandomEnemyShip(PlayerShip player) {

        int x, y;
        x = (int) (Math.random() * WORLD_WIDTH);
        if (Math.random() < 0.5) {
            y = (int) (Math.random() * WORLD_HEIGHT / 4);
        } else {
            y = (int) (WORLD_HEIGHT - (Math.random() * WORLD_HEIGHT / 4));
        }

        Vector2D position = new Vector2D(x, y);
        Vector2D velocity = new Vector2D(0, 0);
        Vector2D direction = new Vector2D(Math.random(), Math.random());
        WanderNShoot wn = new WanderNShoot(position, player, direction);
        EnemyShip es = new EnemyShip(position, velocity, direction, 30, wn);
        return es;
    }

    /**
     * Creates a bullet when the ship shots.
     */
    @Override
    public void mkBullet() {
        super.mkBullet();
        bullet = new Bullet(bulletPosVel[1], bulletPosVel[0], 7, "Enemy");
    }
}
