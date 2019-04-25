package model;

import java.util.ArrayList;

/**
 * Represents a game level with game objects, score and level counter.
 */
public class GameLevel {
    private ArrayList<GameObject> gameObjects;
    private int score;
    private int level;

    public GameLevel(final ArrayList<GameObject> gameObjects, final int score, final int level) {
        this.gameObjects = gameObjects;
        this.score = score;
        this.level = level;
    }

    /**
     * Returns level one - the initial level.
     * @return the initial level.
     */
    public static GameLevel getInitialLevel() {
        return new GameLevel(null, 0, 1);
    }

    /**
     * Returns the game objects in the level.
     * @return game objects in the level.
     * */
    public final ArrayList<GameObject> getGameObjects() {
        return gameObjects;
    }

    /**
     * Returns the player score of the level.
     * @return the player score of the level.
     * */
    public final int getScore() {
        return score;
    }

    /**
     * Returns the player level as number.
     * @return the player level as number.
     * */
    public final int getLevel() {
        return level;
    }
}
