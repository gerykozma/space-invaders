import model.*;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static model.GameObjectFactory.*;
import static org.junit.Assert.*;

public class SpaceInvadersTests {

    private static Double ErrorThreshold = 0.01;

    @Test
    public void CalculateScore_MultipleLevels_CorrectScore() {
        ScoreHelper scoreHelper = new ScoreHelper(0, 1);

        scoreHelper.increaseScore();
        scoreHelper.increaseScore();
        scoreHelper.increaseScore();
        scoreHelper.increaseLevel();
        scoreHelper.increaseScore();
        scoreHelper.increaseScore();
        scoreHelper.increaseLevel();
        scoreHelper.increaseScore();

        assertEquals(scoreHelper.getScore(), 1000);
        assertEquals(scoreHelper.getLevel(), 3);
        assertEquals(scoreHelper.getScoreAsString(), "1000");
        assertEquals(scoreHelper.getLevelAsString(), "3");
    }

    @Test
    public void SaveScore_HighScoreAndSimpleScore_ScoreSaved() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1000\n");
        stringBuilder.append("100\n");
        stringBuilder.append("10\n");
        stringBuilder.append("0\n");

        String fileName = String.format("TestScore_%s.score", LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));

        Files.write(Paths.get(fileName), stringBuilder.toString().getBytes());

        assertFalse(HighScoreHelper.saveScore(999, fileName));
        assertFalse(HighScoreHelper.saveScore(99, fileName));
        assertFalse(HighScoreHelper.saveScore(9, fileName));
        assertFalse(HighScoreHelper.saveScore(0, fileName));
        assertFalse(HighScoreHelper.saveScore(-999, fileName));
        assertTrue(HighScoreHelper.saveScore(9999, fileName));
    }

    @Test
    public void CreateGameObject_CreatePlayerObject_ObjectCreated() {
        GameObject player = createPlayerObject();

        assertNotNull(player);
        assertEquals(player.getX(), AppConstants.PLAYER_SHIP_X_COORDINATE, ErrorThreshold);
        assertEquals(player.getY(), AppConstants.PLAYER_SHIP_Y_COORDINATE, ErrorThreshold);
        assertEquals(player.getWidth(), AppConstants.PLAYER_SHIP_WIDTH);
        assertEquals(player.getHeight(), AppConstants.PLAYER_SHIP_HEIGHT);
        assertEquals(player.getType(), GameObjectType.PlayerShip);
    }

    @Test
    public void SetGameObjectToDead_OnObjectDeath_ObjectIsKilled() {
        GameObject player = createPlayerObject();

        player.setToDead();

        assertTrue(player.getIsDead());
    }

    @Test
    public void CreateGameObject_CreateEnemyObject_ObjectCreated() {
        GameObject enemy = createEnemyObject();

        assertNotNull(enemy);
        assertEquals(enemy.getX(), AppConstants.ENEMY_SHIP_X_COORDINATE, ErrorThreshold);
        assertEquals(enemy.getY(), AppConstants.ENEMY_SHIP_Y_COORDINATE, ErrorThreshold);
        assertEquals(enemy.getWidth(), AppConstants.ENEMY_SHIP_WIDTH);
        assertEquals(enemy.getHeight(), AppConstants.ENEMY_SHIP_HEIGHT);
        assertEquals(enemy.getType(), GameObjectType.EnemyShip);
    }

    @Test
    public void CreateGameObject_CreatePlayerTorpedoObject_ObjectCreated() {
        GameObject playerTorpedo = createPlayerTorpedoObject(
                AppConstants.PLAYER_SHIP_X_COORDINATE,
                AppConstants.PLAYER_SHIP_Y_COORDINATE);

        assertNotNull(playerTorpedo);
        assertEquals(playerTorpedo.getX(), AppConstants.PLAYER_SHIP_X_COORDINATE + AppConstants.TORPEDO_X_OFFSET, ErrorThreshold);
        assertEquals(playerTorpedo.getY(), AppConstants.PLAYER_SHIP_Y_COORDINATE, ErrorThreshold);
        assertEquals(playerTorpedo.getWidth(), AppConstants.TORPEDO_WIDTH);
        assertEquals(playerTorpedo.getHeight(), AppConstants.TORPEDO_HEIGHT);
        assertEquals(playerTorpedo.getType(), GameObjectType.PlayerTorpedo);
    }

    @Test
    public void CreateGameObject_CreateEnemyTorpedoObject_ObjectCreated() {
        GameObject enemyTorpedo = createEnemyTorpedoObject(
                AppConstants.ENEMY_SHIP_X_COORDINATE,
                AppConstants.ENEMY_SHIP_Y_COORDINATE);

        assertNotNull(enemyTorpedo);
        assertEquals(enemyTorpedo.getX(), AppConstants.ENEMY_SHIP_X_COORDINATE + AppConstants.TORPEDO_X_OFFSET, ErrorThreshold);
        assertEquals(enemyTorpedo.getY(), AppConstants.ENEMY_SHIP_Y_COORDINATE, ErrorThreshold);
        assertEquals(enemyTorpedo.getWidth(), AppConstants.TORPEDO_WIDTH);
        assertEquals(enemyTorpedo.getHeight(), AppConstants.TORPEDO_HEIGHT);
        assertEquals(enemyTorpedo.getType(), GameObjectType.EnemyTorpedo);
    }

    @Test
    public void SaveGameHelper_SaveAndLoadLevel_LevelSuccessfullySavedAndLoaded() {
        ArrayList<GameObject> objects = new ArrayList<>();

        GameObject player = createPlayerObject();
        GameObject enemy = createEnemyObject();
        GameObject playerTorpedo = createPlayerTorpedoObject(100, 100);
        GameObject enemyTorpedo = createEnemyTorpedoObject(200, 200);
        objects.add(player);
        objects.add(enemy);
        objects.add(playerTorpedo);
        objects.add(enemyTorpedo);

        GameLevel level = new GameLevel(objects, 1000, 5);

        SavedGameHelper.saveGame("", level);

        String fileName = String.format("SavedGame_%s.save", LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));

        GameLevel loaded = SavedGameHelper.loadGame(fileName);
        assertEquals(4, loaded.getGameObjects().size());
        for (GameObject gameObject : loaded.getGameObjects()) {
            assertNotNull(gameObject);
            if (gameObject.getType().equals(GameObjectType.PlayerShip)) {
                assertEquals(gameObject.getX(), AppConstants.PLAYER_SHIP_X_COORDINATE, ErrorThreshold);
                assertEquals(gameObject.getY(), AppConstants.PLAYER_SHIP_Y_COORDINATE, ErrorThreshold);
                assertEquals(gameObject.getWidth(), AppConstants.PLAYER_SHIP_WIDTH);
                assertEquals(gameObject.getHeight(), AppConstants.PLAYER_SHIP_HEIGHT);
            } else if (gameObject.getType().equals(GameObjectType.EnemyShip)) {
                assertEquals(gameObject.getX(), AppConstants.ENEMY_SHIP_X_COORDINATE, ErrorThreshold);
                assertEquals(gameObject.getY(), AppConstants.ENEMY_SHIP_Y_COORDINATE, ErrorThreshold);
                assertEquals(gameObject.getWidth(), AppConstants.ENEMY_SHIP_WIDTH);
                assertEquals(gameObject.getHeight(), AppConstants.ENEMY_SHIP_HEIGHT);
            } else if (gameObject.getType().equals(GameObjectType.PlayerTorpedo)) {
                assertEquals(gameObject.getX(), 100 + AppConstants.TORPEDO_X_OFFSET, ErrorThreshold);
                assertEquals(gameObject.getY(), 100, ErrorThreshold);
                assertEquals(gameObject.getWidth(), AppConstants.TORPEDO_WIDTH);
                assertEquals(gameObject.getHeight(), AppConstants.TORPEDO_HEIGHT);
            } else if (gameObject.getType().equals(GameObjectType.EnemyTorpedo)) {
                assertEquals(gameObject.getX(), 200 + AppConstants.TORPEDO_X_OFFSET, ErrorThreshold);
                assertEquals(gameObject.getY(), 200, ErrorThreshold);
                assertEquals(gameObject.getWidth(), AppConstants.TORPEDO_WIDTH);
                assertEquals(gameObject.getHeight(), AppConstants.TORPEDO_HEIGHT);
            }
        }

        assertEquals(loaded.getScore(), 1000);
        assertEquals(loaded.getLevel(), 5);
    }

    @Test
    public void SetX_ValidX_ReturnsTrue() {
        GameObject dummy = createPlayerObject();

        assertTrue(dummy.trySetX(100));
        assertTrue(dummy.trySetX(10));
        assertTrue(dummy.trySetX(500));
        assertTrue(dummy.trySetX(AppConstants.MAX_GAME_PANE_WIDTH - dummy.getWidth()));
    }

    @Test
    public void SetX_InvalidX_ReturnsFalse() {
        GameObject dummy = createPlayerObject();

        assertFalse(dummy.trySetX(-10));
        assertFalse(dummy.trySetX(0));
        assertFalse(dummy.trySetX(700));
        assertFalse(dummy.trySetX(AppConstants.MAX_GAME_PANE_WIDTH));
    }

    @Test
    public void SetY_ValidY_ReturnsTrue() {
        GameObject dummy = createPlayerObject();

        assertTrue(dummy.trySetY(100));
        assertTrue(dummy.trySetY(10));
        assertTrue(dummy.trySetY(600));
        assertTrue(dummy.trySetY(AppConstants.MAX_GAME_PANE_HEIGHT - dummy.getHeight()));
    }


    @Test
    public void SetY_InvalidY_ReturnsFalse() {
        GameObject dummy = createPlayerObject();

        assertFalse(dummy.trySetX(-10));
        assertFalse(dummy.trySetX(0));
        assertFalse(dummy.trySetX(800));
        assertFalse(dummy.trySetX(AppConstants.MAX_GAME_PANE_HEIGHT));
    }

    @Test
    public void Intersect_NoIntersection_ReturnsFalse() {
        GameObject dummy1 = createPlayerObject();
        GameObject dummy2 = createPlayerObject();

        assertTrue(dummy1.trySetX(dummy1.getX() + 40));
        assertTrue(dummy1.trySetY(dummy1.getY() + 50));

        assertFalse(dummy1.intersect(dummy2));
    }

    @Test
    public void Intersect_Intersection_ReturnsTrue() {
        GameObject dummy1 = createPlayerObject();
        GameObject dummy2 = createPlayerObject();

        assertTrue(dummy1.trySetX(dummy1.getX() + 15));
        assertTrue(dummy1.trySetY(dummy1.getY() + 15));

        assertTrue(dummy1.intersect(dummy2));
    }
}