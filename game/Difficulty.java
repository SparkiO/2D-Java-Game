package game;

import java.io.Serializable;

/**
 * Difficulty class that determines the difficulty of the game - number of Asteroids and Enemies.
 */
public class Difficulty implements Serializable {
    //number of asteroids and enemies in the game to be spawned
    private int numerOfAsteroids, numberOfEnemies, roundTime;

    public Difficulty(int numerOfAsteroids, int numberOfEnemies, int roundTime) {
        this.numerOfAsteroids = numerOfAsteroids;
        this.numberOfEnemies = numberOfEnemies;
        this.roundTime = roundTime;
    }

    public int getNumerOfAsteroids() {
        return numerOfAsteroids;
    }

    public int getNumberOfEnemies() {
        return numberOfEnemies;
    }

    public int getRoundTime() {
        return roundTime;
    }
}
