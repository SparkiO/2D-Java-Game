package game.model;

import utilities.Vector2D;

import java.util.List;
import java.awt.*;

/**
 * Black Hole class, an object which teleports other Game Objects to another Black Hole chosen randomly.
 */
public class BlackHole extends GameObject {
    private List<BlackHole> otherHoles; //list with other black holes in the game

    public BlackHole(Vector2D position, int radius, List<BlackHole> otherHoles) {
        super(position, new Vector2D(1, 1), radius);
        this.otherHoles = otherHoles;
    }

    /**
     * Method that teleports other Game Objects colliding with this Black Hole to another Black Hole.
     *
     * @param other model colliding with this.
     */
    @Override
    public void collisionHandling(GameObject other) {
        if (canHit(other) && this.overlap(other)) {
            //gets another Black Hole object randomly
            BlackHole otherHole = otherHoles.get((int) (Math.random() * otherHoles.size()));
            while (otherHole == this) //makes sure it doesn't chose itself
                otherHole = otherHoles.get((int) (Math.random() * otherHoles.size()));

            other.position.set(otherHole.position.x + radius, otherHole.position.y + radius);
            other.setInvincible(true, 3000); //sets the model do not collide with the other Black Hole for 3 seconds so they can escape it
        }
    }

    /**
     * Draws the Black Hole.
     *
     * @param g - graphics component
     */
    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillOval((int) position.x, (int) position.y, 200, 200);
        g.setColor(Color.WHITE);
        g.drawOval((int) position.x + 22, (int) position.y + 22, 150, 150);
        g.setColor(Color.ORANGE);
        g.drawOval((int) position.x - 12, (int) position.y - 12, 230, 230);
        g.drawOval((int) position.x - 24, (int) position.y - 24, 250, 250);
    }

}
