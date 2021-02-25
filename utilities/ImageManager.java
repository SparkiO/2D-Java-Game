package utilities;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Image Manager class used to handling images used in the game.
 */
public class ImageManager {

    public final static String path = "images/"; //path to the folder with images used in the game
    public final static String ext = ".png"; //extension of the image
    public static Map<String, Image> images = new HashMap<String, Image>(); //map with the Image object and its name

    /**
     * Loads image from given file name. Adds it to map of images
     * @param fname name of the file with image
     * @return Image object with image loaded from the file
     * @throws IOException input/output exception
     */
    public static Image loadImage(String fname) throws IOException {
        BufferedImage img;
        img = ImageIO.read(new File(path + fname + ext));
        images.put(fname, img);
        return img;
    }

}