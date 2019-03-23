import Model.*;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
    public void SaveScore_HighScoreAndSimpleScore_ScoreSaved() throws IOException
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1000\n");
        stringBuilder.append("100\n");
        stringBuilder.append("10\n");
        stringBuilder.append("0\n");

        String fileName = String.format("TestScore_%s.score", LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));

        Files.write(Paths.get(fileName), stringBuilder.toString().getBytes());

        assertFalse(HighScoreHelper.SaveScore(999, fileName));
        assertFalse(HighScoreHelper.SaveScore(99, fileName));
        assertFalse(HighScoreHelper.SaveScore(9, fileName));
        assertFalse(HighScoreHelper.SaveScore(0, fileName));
        assertFalse(HighScoreHelper.SaveScore(-999, fileName));
        assertTrue(HighScoreHelper.SaveScore(9999, fileName));
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
        assertEquals(playerTorpedo.GetX(),  AppConstants.PlayerShipXCoordinate + AppConstants.TorpedoXOffset, ErrorThreshold);
        assertEquals(playerTorpedo.GetY(),  AppConstants.PlayerShipYCoordinate, ErrorThreshold);
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
        assertEquals(enemyTorpedo.GetX(),  AppConstants.EnemyShipXCoordinate + AppConstants.TorpedoXOffset, ErrorThreshold);
        assertEquals(enemyTorpedo.GetY(),  AppConstants.EnemyShipYCoordinate, ErrorThreshold);
        assertEquals(enemyTorpedo.GetWidth(), AppConstants.TorpedoWidth);
        assertEquals(enemyTorpedo.GetHeight(), AppConstants.TorpedoHeight);
        assertEquals(enemyTorpedo.GetType(), GameObjectType.EnemyTorpedo);
    }

    @Test
    public void SaveGameHelper_SaveAndLoadLevel_LevelSuccessfullySavedAndLoaded()
    {
        ArrayList<GameObject> objects = new ArrayList<>();

        GameObject player = GameObjectFactory.CreatePlayerObject();
        GameObject enemy = GameObjectFactory.CreateEnemyObject();
        GameObject playerTorpedo = GameObjectFactory.CreatePlayerTorpedoObject(100,100);
        GameObject enemyTorpedo = GameObjectFactory.CreateEnemyTorpedoObject(200, 200);
        objects.add(player);
        objects.add(enemy);
        objects.add(playerTorpedo);
        objects.add(enemyTorpedo);

        GameLevel level = new GameLevel(objects, 1000, 5);

        SavedGameHelper.SaveGame("", level);

        String fileName =String.format("SavedGame_%s.save", LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));

        GameLevel loaded = SavedGameHelper.LoadGame(fileName);
        assertEquals(4, loaded.getGameObjects().size());
       for(GameObject gameObject : loaded.getGameObjects())
       {
           assertNotNull(gameObject);
           if (gameObject.GetType().equals(GameObjectType.PlayerShip))
           {
               assertEquals(gameObject.GetX(),  AppConstants.PlayerShipXCoordinate, ErrorThreshold);
               assertEquals(gameObject.GetY(),  AppConstants.PlayerShipYCoordinate, ErrorThreshold);
               assertEquals(gameObject.GetWidth(), AppConstants.PlayerShipWidth);
               assertEquals(gameObject.GetHeight(), AppConstants.PlayerShipHeight);
           }
           else if(gameObject.GetType().equals(GameObjectType.EnemyShip))
           {
               assertEquals(gameObject.GetX(),  AppConstants.EnemyShipXCoordinate, ErrorThreshold);
               assertEquals(gameObject.GetY(),  AppConstants.EnemyShipYCoordinate, ErrorThreshold);
               assertEquals(gameObject.GetWidth(), AppConstants.EnemyShipWidth);
               assertEquals(gameObject.GetHeight(), AppConstants.EnemyShipHeight);
           }
           else if(gameObject.GetType().equals(GameObjectType.PlayerTorpedo))
           {
               assertEquals(gameObject.GetX(),  100+AppConstants.TorpedoXOffset, ErrorThreshold);
               assertEquals(gameObject.GetY(),  100, ErrorThreshold);
               assertEquals(gameObject.GetWidth(), AppConstants.TorpedoWidth);
               assertEquals(gameObject.GetHeight(), AppConstants.TorpedoHeight);
           }
           else if(gameObject.GetType().equals(GameObjectType.EnemyTorpedo))
           {
               assertEquals(gameObject.GetX(),  200 + AppConstants.TorpedoXOffset, ErrorThreshold);
               assertEquals(gameObject.GetY(),  200, ErrorThreshold);
               assertEquals(gameObject.GetWidth(), AppConstants.TorpedoWidth);
               assertEquals(gameObject.GetHeight(), AppConstants.TorpedoHeight);
           }
       }

       assertEquals(loaded.getScore(), 1000);
       assertEquals(loaded.getLevel(), 5);
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