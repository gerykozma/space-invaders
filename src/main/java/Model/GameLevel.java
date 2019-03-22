package Model;

import java.util.ArrayList;

public class GameLevel
{
    private ArrayList<GameObject> _gameObjects;
    private int _score;
    private int _level;

    public GameLevel(ArrayList<GameObject> gameObjects, int score, int level)
    {
        this._gameObjects = gameObjects;
        this._score=score;
        this._level=level;
    }

    public static GameLevel GetInitialLevel()
    {
        return new GameLevel(null, 0, 1);
    }

    public ArrayList<GameObject> getGameObjects()
    {
        return _gameObjects;
    }

    public int getScore()
    {
        return _score;
    }

    public int getLevel()
    {
        return _level;
    }
}
