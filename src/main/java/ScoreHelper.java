public class ScoreHelper {
    private int _score;
    private int _level;

    protected ScoreHelper(int score, int level) {
        this._score = score;
        this._level = level;
    }

    protected void IncreaseScore() {
        this._score = this._score + AppConstants.DestroyedEnemyShipBaseScore * this._level;
    }

    protected void IncreaseLevel()
    {
        this._level++;
    }

    protected int GetScore() {
        return this._score;
    }

    protected String GetScoreAsString()
    {
        return String.format("%s",this._score);
    }

    protected String GetLevelAsString()
    {
        return String.format("%s",this._level);
    }

    protected int GetLevel()
    {
        return this._level;
    }
}
