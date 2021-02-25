package utilities;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;

/**
 * Class representing Sprite in the game. Used for asteroids and background images.
 */
public class Sprite {
    public static Image ASTEROID1, MILKYWAY1; //images representing the objects

    /**
     * Loads images with given names and assigns them to Image objects.
     */
    static {
        try {
            ASTEROID1 = ImageManager.loadImage("asteroid1");
            MILKYWAY1 = ImageManager.loadImage("milkyway1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Image image; //image object used in Sprite
    public Vector2D position; //position of the Sprite
    public Vector2D direction; //direction of the Sprite

    //width and height of the Sprite
    public double width;
    public double height;

    /**
     * Constructor. Assigns passed values to the attributes.
     */
    public Sprite(Image image, Vector2D s, Vector2D direction, double width,
                  double height) {
        this.image = image;
        this.position = s;
        this.direction = direction;
        this.width = width;
        this.height = height;
    }

    /**
     * Draws the Sprite on the Frame.
     * @param g - graphics component.
     */
    public void draw(Graphics2D g) {
        //gets image width and height
        double imW = image.getWidth(null);
        double imH = image.getHeight(null);
        //creates new AffineTransform, manipulate it according to the direction, scale and translation
        AffineTransform t = new AffineTransform();
        t.rotate(direction.angle(), 0, 0);
        t.scale(width / imW, height / imH);
        t.translate(-imW / 2.0, -imH / 2.0);
        AffineTransform t0 = g.getTransform(); //gets currently used AffineTransformation from graphics component
        g.translate(position.x, position.y);
        g.drawImage(image, t, null); //draws the image according to the previous AffineTransformation
        g.setTransform(t0); //sets the transformation to the one used before
    }

}