package model;

/**
 * Represents a score and level count tracking object.
 */
public class ScoreHelper {
    private int score;
    private int level;

    public ScoreHelper(final int score, final int level) {
        this.score = score;
        this.level = level;
    }

    /**
     * Increases the score based on the level.
     */
    public final void increaseScore() {
        this.score = this.score + AppConstants.DESTROYED_ENEMY_SHIP_BASE_SCORE * this.level;
    }

    /**
     * Increases level counter.
     */
    public final void increaseLevel() {
        this.level++;
    }

    public final int getScore() {
        return this.score;
    }

    public final String getScoreAsString() {
        return String.format("%s", this.score);
    }

    public final String getLevelAsString() {
        return String.format("%s", this.level);
    }

    public final int getLevel() {
        return this.level;
    }
}
