package Model;

import java.util.ArrayList;

/**
 * Represents a game level with GameObjects, score and level counter.
 */
public class GameLevel {
    private ArrayList<GameObject> _gameObjects;
    private int _score;
    private int _level;

    public GameLevel(ArrayList<GameObject> gameObjects, int score, int level) {
        this._gameObjects = gameObjects;
        this._score = score;
        this._level = level;
    }

    /**
     * Returns level one.
     */
    public static GameLevel GetInitialLevel() {
        return new GameLevel(null, 0, 1);
    }

    public ArrayList<GameObject> getGameObjects() {
        return _gameObjects;
    }

    public int GetScore() {
        return _score;
    }

    public int GetLevel() {
        return _level;
    }
}