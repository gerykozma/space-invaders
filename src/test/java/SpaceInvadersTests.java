import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class SpaceInvadersTests {


    public SpaceInvadersTests()
    {

    }

    @Test
    public void ScoreHelperTest() {
        ScoreHelper scoreHelper = new ScoreHelper(0, 1);

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
    public void HighScoreHelperTest() throws IOException
    {
        int score=9999;
        int highScore=99999;
        assertFalse(HighScoreHelper.SaveScore(score));
        assertTrue(HighScoreHelper.SaveScore(highScore));


    }

    @Test
    public void LoadGameTest()
    {

    }

    @Test
    public void SaveGameTest()
    {

    }

    @Test
    public void MoveUpTest()
    {

    }

    @Test
    public void MoveDownTest()
    {

    }

    @Test
    public void MoveRightTest()
    {

    }
    @Test
    public void MoveLeftTest()
    {

    }
    @Test
    public void IntersectUpperBoundTest()
    {

    }

    @Test
    public void IntersectLowerBoundTest()
    {

    }

}