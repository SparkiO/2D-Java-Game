package game.model;

import game.Constants;
import game.controller.Controller;
import utilities.Vector2D;

import java.awt.*;
import java.awt.geom.AffineTransform;

import static game.Constants.*;

/**
 * Abstract Ship class representing ships on the game. Inherits from GameObject abstract class.
 */
public abstract class Ship extends GameObject {

    private static final double DRAG = 2; //constant speed loss factor
    private static final double STEER_RATE = 2 * Math.PI; //rotation velocity in radians per second

    protected static double mag_acc = 500; //acceleration when thrust is applied
    private Color colour; //colour of the ship
    protected Bullet bullet; //bullet object that is created when the ship shoots

    private int[] XP; //array holding X coordinates of edges of the polygon representing the ship
    private int[] YP; //array holding Y coordinates of edges of the polygon representing the ship
    protected int[] XPTHRUST; //array holding X coordinates of edges of the polygon representing the "flame" when ship is thrusting
    protected int[] YPTHRUST; //array holding Y coordinates of edges of the polygon representing the "flame" when ship is thrusting

    private long previousTime; //long that controls the speed of firing the bullets
    private String controllerName; //name of the controller used by the ship
    protected Vector2D[] bulletPosVel; //position and velocity of bullets shot by this ship

    protected transient Controller ctrl; //controller of the ship

    protected String name;

    protected int shield = 0; //default shield value
    protected boolean thrusting, shielding, hit; //determines whether those action takes place or not
    protected int timeToShoot; //determines the time between continues shooting

    /**
     * Constructor of the Ship object. Calls the constructor of superclass (GameObject).
     *
     * @param color     - colour of the ship
     * @param position  - position of the ship when spawned
     * @param velocity  - velocity of the ship when spawned
     * @param direction - direction of the ship when spawned
     * @param radius    - radius of the ship (used by CollisionHandling)
     * @param ctrl      - controller of the ship
     */
    public Ship(Color color, Vector2D position, Vector2D velocity, Vector2D direction, double radius, Controller ctrl) {
        super(position, velocity, direction, radius);
        this.ctrl = ctrl;
        controllerName = ctrl.getClass().getName();
        colour = color;
        bullet = null;
        XP = new int[]{-10, 0, 10, 0};
        YP = new int[]{10, -20, 10, 0};
    }

    /**
     * Updates the velocity and direction. Creates a Bullet if the ship is shooting. Updates the bullet if it exists.
     */
    @Override
    public void update() {
        //updates the direction
        direction.rotate(ctrl.action().turn * STEER_RATE * DT);

        //updates the velocity by direction and scales it by the factor if the ship is not at maximum speed already
        if (velocity.x + velocity.y < Constants.MAX_PLAYER_SPEED && velocity.x + velocity.y > -Constants.MAX_PLAYER_SPEED)
            velocity.addScaled(direction, mag_acc * DT * ctrl.action().thrust);

        //updates the velocity by DRAG amount
        if (velocity.x > 0)
            velocity.subtract(DRAG, 0);
        else if (velocity.x < 0)
            velocity.add(DRAG, 0);

        if (velocity.y > 0)
            velocity.subtract(0, DRAG);
        else if (velocity.y < 0)
            velocity.add(0, DRAG);

        //updates the position accordingly
        position.addScaled(velocity, DT);
        position.wrap(WORLD_WIDTH, WORLD_HEIGHT);

        //creates stop of 1 second between creating the bullet when the ship is constantly shooting
        if (ctrl.action().shoot && System.currentTimeMillis() - previousTime > timeToShoot) {
            mkBullet();
            ctrl.action().shoot = false;
        }

        if (bullet != null)
            bullet.update();
    }

    /**
     * Creates a position and velocity vectors of Bullet, sets the time of creation.
     */
    public void mkBullet() {
        bulletPosVel = new Vector2D[2];
        previousTime = System.currentTimeMillis();
        Vector2D vel = new Vector2D();
        vel.addScaled(direction, 800);
        Vector2D pos = new Vector2D(position);
        pos.addScaled(direction, 35);
        bulletPosVel[0] = vel;
        bulletPosVel[1] = pos;
    }

    /**
     * Draws the ship objects.
     *
     * @param g - graphic component
     */
    @Override
    public void draw(Graphics2D g) {
        AffineTransform at = g.getTransform(); //"save" the current Affine Transform
        g.translate(position.x, position.y); //translate to position of the ship

        //rotates, scales and draws the ship accordingly
        double rot = direction.angle() + Math.PI / 2;
        g.rotate(rot);
        g.scale(1, 1);
        g.setColor(colour);
        g.fillPolygon(XP, YP, XP.length);
        //if thrusting - draws the flames
        if (thrusting) {
            g.setColor(Color.red);
            g.fillPolygon(XPTHRUST, YPTHRUST, XPTHRUST.length);
        }
        //if shield is on - draws it
        if (shielding) {
            g.setColor(new Color(255, 55, 155));
            g.drawOval(-20, -25, 40, 40);
        }

        //draws the name of the player near the ship
        if (this instanceof PlayerShip) {
            g.drawString(name, -12, 20);
        }
        g.setTransform(at);
    }

    /**
     * Calls super method to load the ship.
     */
    @Override
    public void load() {
        super.load();

    }

    public String getControllerName() {
        return controllerName;
    }

    public void setCtrl(Controller ctrl) {
        this.ctrl = ctrl;
    }

    public Bullet getBullet() {
        Bullet b = bullet;
        this.bullet = null;
        return b;
    }

    public boolean isShielding() {
        return shielding;
    }

    public boolean isHit() {
        return hit;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }
}
