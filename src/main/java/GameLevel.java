import java.util.ArrayList;

public class GameLevel
{
    private ArrayList<GameObject> _gameObjects;
    private int _score;
    private int _level;

    protected GameLevel(ArrayList<GameObject> gameObjects, int score, int level)
    {
        this._gameObjects=gameObjects;
        this._score=score;
        this._level=level;
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
