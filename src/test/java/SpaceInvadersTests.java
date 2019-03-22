import Model.*;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class SpaceInvadersTests {

    private static Double ErrorThreshold = 0.01;

    @Test
    public void CalculateScore_MultipleLevels_CorrectScore() {
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
//        int score=9999;
//        int highScore=99999;
//        assertFalse(HighScoreHelper.SaveScore(score));
//        assertTrue(HighScoreHelper.SaveScore(highScore));
        //TODO
    }

    @Test
    public void CreateGameObject_CreatePlayerObject_ObjectCreated()
    {
        GameObject player = GameObjectFactory.CreatePlayerObject();

        assertNotNull(player);
        assertEquals(player.GetX(),  AppConstants.PlayerShipXCoordinate, ErrorThreshold);
        assertEquals(player.GetY(),  AppConstants.PlayerShipYCoordinate, ErrorThreshold);
        assertEquals(player.GetWidth(), AppConstants.PlayerShipWidth);
        assertEquals(player.GetHeight(), AppConstants.PlayerShipHeight);
        assertEquals(player.GetType(), GameObjectType.PlayerShip);
    }

    @Test
    public void CreateGameObject_CreateEnemyObject_ObjectCreated()
    {
        GameObject enemy = GameObjectFactory.CreateEnemyObject();

        assertNotNull(enemy);
        assertEquals(enemy.GetX(),  AppConstants.EnemyShipXCoordinate, ErrorThreshold);
        assertEquals(enemy.GetY(),  AppConstants.EnemyShipYCoordinate, ErrorThreshold);
        assertEquals(enemy.GetWidth(), AppConstants.EnemyShipWidth);
        assertEquals(enemy.GetHeight(), AppConstants.EnemyShipHeight);
        assertEquals(enemy.GetType(), GameObjectType.EnemyShip);
    }

    @Test
    public void CreateGameObject_CreatePlayerTorpedoObject_ObjectCreated()
    {
        GameObject playerTorpedo = GameObjectFactory.CreatePlayerTorpedoObject(
                AppConstants.PlayerShipXCoordinate,
                AppConstants.PlayerShipYCoordinate);

        assertNotNull(playerTorpedo);
        assertEquals(playerTorpedo.GetX(),  AppConstants.PlayerShipXCoordinate+20, ErrorThreshold);
        assertEquals(playerTorpedo.GetY(),  AppConstants.PlayerShipYCoordinate-10, ErrorThreshold);
        assertEquals(playerTorpedo.GetWidth(), AppConstants.TorpedoWidth);
        assertEquals(playerTorpedo.GetHeight(), AppConstants.TorpedoHeight);
        assertEquals(playerTorpedo.GetType(), GameObjectType.PlayerTorpedo);
    }

    @Test
    public void CreateGameObject_CreateEnemyTorpedoObject_ObjectCreated()
    {
        GameObject enemyTorpedo = GameObjectFactory.CreateEnemyTorpedoObject(
                AppConstants.EnemyShipXCoordinate,
                AppConstants.EnemyShipYCoordinate);

        assertNotNull(enemyTorpedo);
        assertEquals(enemyTorpedo.GetX(),  AppConstants.EnemyShipXCoordinate+20, ErrorThreshold);
        assertEquals(enemyTorpedo.GetY(),  AppConstants.EnemyShipYCoordinate-10, ErrorThreshold);
        assertEquals(enemyTorpedo.GetWidth(), AppConstants.TorpedoWidth);
        assertEquals(enemyTorpedo.GetHeight(), AppConstants.TorpedoHeight);
        assertEquals(enemyTorpedo.GetType(), GameObjectType.EnemyTorpedo);
    }

    @Test
    public void LoadGameTest()
    {
        String path = String.format("TestSave.save");

        File file = new File(path);
        file.getAbsolutePath();
        if(file.exists())
        System.out.println(file.getAbsolutePath());
        //E:\JavaDev\space-invaders\src\test\java\TestFiles\TestSave.save
    }

    @Test
    public void SaveGameTest()
    {

    }

    @Test
    public void SetX_ValidX_ReturnsTrue()
    {
        GameObject dummy = GameObjectFactory.CreatePlayerObject();

        assertTrue(dummy.TrySetX(100));
        assertTrue(dummy.TrySetX(10));
        assertTrue(dummy.TrySetX(500));
        assertTrue(dummy.TrySetX(AppConstants.MaxGamePaneWidth - dummy.GetWidth()));
    }

    @Test
    public void SetX_InvalidX_ReturnsFalse()
    {
        GameObject dummy = GameObjectFactory.CreatePlayerObject();

        assertFalse(dummy.TrySetX(-10));
        assertFalse(dummy.TrySetX(0));
        assertFalse(dummy.TrySetX(700));
        assertFalse(dummy.TrySetX(AppConstants.MaxGamePaneWidth));
    }

    @Test
    public void SetY_ValidY_ReturnsTrue()
    {
        GameObject dummy = GameObjectFactory.CreatePlayerObject();

        assertTrue(dummy.TrySetY(100));
        assertTrue(dummy.TrySetY(10));
        assertTrue(dummy.TrySetY(600));
        assertTrue(dummy.TrySetY(AppConstants.MaxGamePaneHeight - dummy.GetHeight()));
    }


    @Test
    public void SetY_InvalidY_ReturnsFalse()
    {
        GameObject dummy = GameObjectFactory.CreatePlayerObject();

        assertFalse(dummy.TrySetX(-10));
        assertFalse(dummy.TrySetX(0));
        assertFalse(dummy.TrySetX(800));
        assertFalse(dummy.TrySetX(AppConstants.MaxGamePaneHeight));
    }

    @Test
    public void Intersect_NoIntersection_ReturnsFalse()
    {
        GameObject dummy1 = GameObjectFactory.CreatePlayerObject();
        GameObject dummy2 = GameObjectFactory.CreatePlayerObject();

        assertTrue(dummy1.TrySetX(dummy1.GetX() + 40));
        assertTrue(dummy1.TrySetY(dummy1.GetY() + 50));

        assertFalse(dummy1.Intersect(dummy2));
    }

    @Test
    public void Intersect_Intersection_ReturnsTrue()
    {
        GameObject dummy1 = GameObjectFactory.CreatePlayerObject();
        GameObject dummy2 = GameObjectFactory.CreatePlayerObject();

        assertTrue(dummy1.TrySetX(dummy1.GetX() + 15));
        assertTrue(dummy1.TrySetY(dummy1.GetY() + 15));

        assertTrue(dummy1.Intersect(dummy2));
    }
}