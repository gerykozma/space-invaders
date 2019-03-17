import org.junit.Test;

import static org.junit.Assert.*;

public class SpaceInvadersTests
{
    @Test
    public void ScoreHelperTest()
    {
        ScoreHelper scoreHelper= new ScoreHelper(0, 1);

        scoreHelper.IncreaseScore();
        scoreHelper.IncreaseScore();
        scoreHelper.IncreaseScore();
        scoreHelper.IncreaseLevel();
        scoreHelper.IncreaseScore();
        scoreHelper.IncreaseScore();
        scoreHelper.IncreaseLevel();
        scoreHelper.IncreaseScore();

        assertEquals(scoreHelper.GetScore(), 1000);
    }

    @Test
    public void HighScoreHelperTest()
    {

    }
}