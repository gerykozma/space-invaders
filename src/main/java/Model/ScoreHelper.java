package Model;

/**
 * Represents a score and level count tracking object.
 * */
public class ScoreHelper {
    private int _score;
    private int _level;

    public ScoreHelper(int score, int level) {
        this._score = score;
        this._level = level;
    }

    /**
     * Increases the score based on the level.
     * */
    public void IncreaseScore() {
        this._score = this._score + AppConstants.DestroyedEnemyShipBaseScore * this._level;
    }

    /**
     * Increases level counter.
     * */
    public void IncreaseLevel()
    {
        this._level++;
    }

    public int GetScore() {
        return this._score;
    }

    public String GetScoreAsString()
    {
        return String.format("%s",this._score);
    }

    public String GetLevelAsString()
    {
        return String.format("%s",this._level);
    }

    public int GetLevel()
    {
        return this._level;
    }
}
