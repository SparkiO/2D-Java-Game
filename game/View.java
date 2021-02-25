package game;

import game.model.GameObject;
import game.model.Particle;
import game.model.PlayerShip;
import utilities.Sprite;
import utilities.Vector2D;

import javax.swing.*;
import java.awt.*;

/**
 * View class that handles rendering of all GameObjects and UI.
 */
public class View extends JComponent {
    private Sprite background; //background of the game
    private Minimap minimap; //minimap object showing the game world and player
    private Game game; //current game
    private Camera camera; //camera object that follows player
    private Player player; //player/user
    private PlayerShip playerShip; //player's ship
    private Vector2D playerShipPosition; //player's ship's position
    private double minimapMidX, minimapMidY, minimapX, minimapY; //important points on the minimap (centre and top-left points)

    /**
     * Constructor. Initializes the background, camera and minimap.
     *
     * @param game reference to the current Game object
     */
    public View(Game game) {
        this.game = game;
        camera = new Camera(Constants.FRAME_WIDTH / 2, Constants.FRAME_HEIGHT / 2);
        minimap = new Minimap(Sprite.MILKYWAY1, new Vector2D(minimapX, minimapY), new Vector2D(0, 0), Constants.FRAME_WIDTH / 4, Constants.FRAME_HEIGHT / 4);
        background = new Sprite(Sprite.MILKYWAY1, new Vector2D(Constants.MID_WORLD_X, Constants.MID_WORLD_Y), new Vector2D(0, 0), Constants.WORLD_WIDTH + 2000, Constants.WORLD_HEIGHT + 1200);
    }

    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Sets the Player, his ship and the ship's position.
     *
     * @param player current Player of the game
     */
    public void setPlayer(Player player) {
        this.player = player;
        this.playerShip = player.getShip();
        this.playerShipPosition = playerShip.getPosition();
    }

    /**
     * Paint method. Draws the UI, background and Game Objects.
     *
     * @param g0 graphic component
     */
    @Override
    public void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0;

        //translate to the camera values
        g.translate(camera.getX(), camera.getY());
        background.draw(g);
        minimap.update(); //update minimap position
        minimap.draw(g);
        g.setColor(Color.WHITE);

        //translate to the minimap values and draw rectangles around it so they make a frame
        g.translate(minimap.position.x, minimap.position.y);
        g.fillOval((int) minimapMidX, (int) minimapMidY, 10, 10);
        g.fillRect(-Constants.FRAME_WIDTH / 8, -Constants.FRAME_HEIGHT / 8, Constants.FRAME_WIDTH / 4, 5);
        g.fillRect(-Constants.FRAME_WIDTH / 8, Constants.FRAME_HEIGHT / 8, Constants.FRAME_WIDTH / 4, 5);
        g.fillRect(-Constants.FRAME_WIDTH / 8, -Constants.FRAME_HEIGHT / 8, 5, Constants.FRAME_HEIGHT / 4);
        g.fillRect(Constants.FRAME_WIDTH / 8, -Constants.FRAME_HEIGHT / 8, 5, Constants.FRAME_HEIGHT / 4 + 5);
        g.translate(-minimap.position.x, -minimap.position.y);

        //calls draw method for every GameObject and Particle
        for (GameObject object : game.objects)
            object.draw(g);
        synchronized (Game.class) {
            for (Particle p : game.particles)
                p.draw(g);
        }
        g.translate(-camera.getX(), -camera.getY());
        camera.update(); //update the camera position

        //draws the UI in the top-left position and top-right position
        g.setColor(Color.WHITE);
        ScoreTracker st = game.getScoreTracker();
        g.drawString("Lives: " + player.getLives(), 5, 15);
        g.drawString("Shield: " + playerShip.getShield(), 5, 35);
        g.drawString("Pods: " + playerShip.maxPods, 5, 75);

        g.drawString("Enemies: " + st.getEnemiesLeft(), Constants.FRAME_WIDTH - 100, 15);
        g.drawString("Time: " + st.getTimeLeft(), Constants.FRAME_WIDTH - 100, 55);
        g.drawString("Score: " + st.getScore(player.getName()), Constants.FRAME_WIDTH - 100, 35);
        if (playerShip.isShielding() && playerShip.getShield() > 0)
            g.drawString("Shield: ENABLED", 5, 55);
        else
            g.drawString("Shield: DISABLED", 5, 55);

        //if player is respawning - show the counting time
        if (playerShip.getRespawning())
            g.drawString("Respawning: " + (200 - playerShip.respawnTime), Constants.FRAME_WIDTH / 2, Constants.FRAME_HEIGHT / 2);
    }

    @Override
    public Dimension getPreferredSize() {
        return Constants.FRAME_SIZE;
    }

    /**
     * Minimap class representing the minimap in the Game which shows the entire Game World and Player's position.
     */
    class Minimap extends Sprite {
        public Minimap(Image image, Vector2D s, Vector2D direction, double width, double height) {
            super(image, s, direction, width, height);
        }

        /**
         * Updates the position of the minimap on screen.
         */
        public void update() {
            minimapX = playerShipPosition.x + Constants.FRAME_WIDTH / 3;
            minimapY = playerShipPosition.y + Constants.FRAME_HEIGHT / 3;
            minimap.position.set(minimapX, minimapY);

            double differenceWorldMapX = playerShipPosition.x - Constants.MID_WORLD_X;
            minimapMidX = differenceWorldMapX / 8;
            double differenceWorldMapY = playerShipPosition.y - Constants.MID_WORLD_Y;
            minimapMidY = differenceWorldMapY / 8;
        }

    }

    /**
     * Camera Class used to specify which area of the Game World should be painted on the Frame.
     */
    class Camera {
        private int x, y;

        public Camera(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Updates the coordinates of the camera depending on the player's position.
         * Player should always be on the middle of the screen.
         */
        public void update() {
            x = -(int) playerShipPosition.x + Constants.FRAME_WIDTH / 2;
            y = -(int) playerShipPosition.y + Constants.FRAME_HEIGHT / 2;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

    }

}
