package Controller;

import Model.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static Model.GameObjectType.*;

/**
 * Main game engine. Responsible for running the game and managing user inputs.
 */
public class gameController {

    /**
     * General event logger instance.
     */
    private final static Logger EVENT_LOGGER = Logger.getLogger(gameController.class);

    /**
     * Player ship.
     */
    private SpaceShip player;
    /**
     *
     */
    private Pane gamePane;
    private Label scoreLabel;
    private Label levelLabel;
    private ScoreHelper scoreHelper;
    private double playerShootCooldown = 0.0;
    private AnimationTimer timer;
    private boolean isPaused = false;
    private boolean playerMoveLeft = false;
    private boolean playerMoveRight = false;

    private boolean enemyMoveToRight = false;
    private int enemyMoveTimer = 0;
    private Random randomGenerator;

    /**
     * Creates an initialized gameController object.
     *
     * @param primaryStage the main stage(window)
     * @throws IOException              when the fxml file cannot be loaded
     * @throws IllegalArgumentException when primaryStage is null
     */
    public gameController(Stage primaryStage) throws IOException, IllegalArgumentException {
        if (primaryStage == null) {
            EVENT_LOGGER.debug("primarystage was null. ");
            throw new IllegalArgumentException("primaryStage cannot be null. Pass a valid stage to build upon.");
        }

        AnchorPane root;
        try {
            root = FXMLLoader.load(getClass().getResource("..\\SpaceInvadersMainScene.fxml"));
        } catch (IOException ex) {
            EVENT_LOGGER.debug("Cannot load fxml resource file. Make sure it is in the resources folder. " +
                    "Details: ", ex);
            throw ex;
        }

        this.timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                UpdateScene();
            }
        };
        this.scoreHelper = new ScoreHelper(0, 1);
        this.randomGenerator = new Random(Calendar.MILLISECOND);

        primaryStage.setTitle("Space Invaders");
        primaryStage.setResizable(false);

        Scene mainScene = new Scene(root);
        this.gamePane = (Pane) mainScene.lookup("#GamePane");
        this.scoreLabel = (Label) mainScene.lookup("#ScoreLabel");
        this.levelLabel = (Label) mainScene.lookup("#LevelLabel");

        if (this.gamePane == null) {
            throw new UnsupportedOperationException("GamePane was null. " +
                    "Error while loading element from fxml.");
        }

        if (this.scoreLabel == null) {
            throw new UnsupportedOperationException("ScoreLabel was null. " +
                    "Error while loading element from fxml.");
        }

        if (this.levelLabel == null) {
            throw new UnsupportedOperationException("LevelLabel was null. " +
                    "Error while loading element from fxml.");
        }

        this.gamePane.setPrefSize(AppConstants.MaxGamePaneWidth, AppConstants.MaxGamePaneHeight);
        this.gamePane.setFocusTraversable(true);

        this.gamePane.setOnKeyPressed(event ->
        {
            switch (event.getCode()) {
                case LEFT:

                    this.playerMoveLeft = true;
                    this.playerMoveRight = false;
                    break;

                case RIGHT:
                    this.playerMoveRight = true;
                    this.playerMoveLeft = false;
                    break;

                case SPACE:
                    this.TryPlayerShoot();
                    break;

                case P:
                    this.PauseGame();
                    break;
                case R:
                    this.RestartGame();
                    break;
                case ESCAPE:
                    System.exit(0);
                    break;
                case L:
                    this.LoadGame();
                    break;
                case S:
                    this.SaveGame();
                    break;
            }
        });

        primaryStage.setScene((mainScene));
        primaryStage.show();
    }

    /**
     * Method to start a new game using the given level.
     *
     * @param level given GameLevel to start.
     * @throws IllegalArgumentException if the level parameter is null.
     */
    public void StartGame(GameLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("Level cannot be null.");
        }

        this.InitNewLevel(level);
        timer.start();
        EVENT_LOGGER.info("Game started.");
    }

    /**
     * Loads a saved GameLevel from file
     */
    private void LoadGame() {
        EVENT_LOGGER.info("Loading game..");
        this.isPaused = true;
        this.timer.stop();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "SpaceInvaders saved game",
                "save");
        fileChooser.addChoosableFileFilter(filter);

        int response = fileChooser.showOpenDialog(null);
        if (response != JFileChooser.APPROVE_OPTION) {
            EVENT_LOGGER.debug("User cancelled load operation.");
            return;
        }

        GameLevel loadedLevel = SavedGameHelper
                .LoadGame(fileChooser.getSelectedFile().getPath());
        if (loadedLevel != null) {
            EVENT_LOGGER.info("Game level loaded from file.");
            this.InitNewLevel(loadedLevel);
            return;
        }
        EVENT_LOGGER.error("Failed to load game level.");

    }

    /**
     * Saves a GameLevel to file. The resulting file can be loaded
     * again to recreate the GameLevel.
     */
    private void SaveGame() {
        EVENT_LOGGER.info("Saving game..");
        this.isPaused = true;
        this.timer.stop();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int response = fileChooser.showOpenDialog(null);
        if (response != JFileChooser.APPROVE_OPTION) {
            EVENT_LOGGER.debug("User cancelled save operation.");
            return;
        }

        ArrayList<GameObject> gameObjects = this.gamePane
                .getChildren()
                .stream()
                .map(o -> ((ObservableGameObject) o).GetGameObject())
                .collect(Collectors.toCollection(ArrayList::new));

        GameLevel level = new GameLevel(
                gameObjects,
                this.scoreHelper.GetScore(),
                this.scoreHelper.GetLevel());

        String message;
        String folderPathToSave = fileChooser.getSelectedFile().getPath();
        if (SavedGameHelper.SaveGame(folderPathToSave, level)) {
            message = "Game saved successfully.";
        } else {
            message = "Failed to save game.";
        }
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Resets score, level and begins a new game from level one.
     */
    private void RestartGame() {
        EVENT_LOGGER.info("Game Restarted");
        this.isPaused = false;
        this.timer.stop();
        this.scoreHelper = new ScoreHelper(0, 1);
        this.StartGame(GameLevel.GetInitialLevel());
    }

    /**
     * Ends the game and analyzes results. It will raise pop-up windows based
     * on the outcome of the game.
     */
    private void EndGame(boolean playerWon) {
        EVENT_LOGGER.info("Game over.");

        this.timer.stop();
        this.isPaused = true;

        EVENT_LOGGER.debug("Saving player score..");
        try {
            if (HighScoreHelper.SaveScore(this.scoreHelper.GetScore())) {
                EVENT_LOGGER.info("Player achieved new high score.");
                JOptionPane.showMessageDialog(
                        null,
                        String.format("New HIGH SCORE: %s !!", this.scoreHelper.GetScore()));
            }
        } catch (IOException ex) {
            EVENT_LOGGER.warn("Something went wrong while saving player score." +
                    " Score has not been saved. Details: %s", ex);
        }

        String message;
        if (playerWon) {
            message = String.format("Congratulations! You beat the game with a score of: %s",
                    this.scoreHelper.GetScoreAsString());
            EVENT_LOGGER.info("Player won.");
        } else {
            message = String.format("You are Dead! You made it to level: %s with a score of %s",
                    this.scoreHelper.GetLevelAsString(),
                    this.scoreHelper.GetScoreAsString());

            EVENT_LOGGER.info(String.format("Player lost the game. Level %s, Score %s",
                    this.scoreHelper.GetLevelAsString(),
                    this.scoreHelper.GetScoreAsString()));
        }
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Main rendering method. Handles most of the UI logic.
     */
    private void UpdateScene() {
        EVENT_LOGGER.debug("Updating scene..");

        this.UpdatePlayerTorpedoes();
        this.UpDateEnemyTorpedoes();

        if (this.playerMoveLeft) {
            this.TryPlayerMoveLeft();
        }

        if (this.playerMoveRight) {
            this.TryPlayerMoveRight();
        }

        //Remove Dead Objects from the GamePane
        this.RemoveDeadObjects();

        //Check if player is alive
        this.CheckPlayerStatus();

        //Check if there are remaining enemies, else increase level
        if (!this.AnyEnemyShipAlive()) {
            this.IncreaseLevel();
        }

        //Try to shoot torpedoes every 50 nanoseconds (enemies hav 50% chance to shoot)
        if (enemyMoveTimer % 50 == 0) {
            this.EnemyShootTorpedoes();
        }

        this.MoveEnemyShips();

        if (this.playerShootCooldown > 0.0) {
            this.playerShootCooldown -= 0.1;
        }

        this.enemyMoveTimer++;
    }

    /**
     * Moves the enemy ships. Move algorithm: move every ship in one direction,
     * until the first failed attempt to move into that direction.
     * Then change direction.
     */
    private void MoveEnemyShips() {
        EVENT_LOGGER.debug("Moving enemies.");
        List<SpaceShip> enemyShips = GetGameObjects()
                .stream()
                .filter(o -> o.GetGameObject().GetType().equals(EnemyShip))
                .map(o -> (SpaceShip) o)
                .collect(Collectors.toList());

        for (SpaceShip enemyShip : enemyShips) {

            if (this.enemyMoveToRight) {
                if (!enemyShip.TryMoveRight()) {
                    this.enemyMoveToRight = !this.enemyMoveToRight;
                }
            } else {
                if (!enemyShip.TryMoveLeft()) {
                    this.enemyMoveToRight = !this.enemyMoveToRight;
                }
            }
        }

    }

    /**
     * Shot enemy torpedoes. Shooting algorithm: every enemy ship has
     * a chance of 50% to shot a torpedo.
     */
    private void EnemyShootTorpedoes() {
        List<SpaceShip> enemyShips = GetGameObjects()
                .stream()
                .filter(o -> o.GetGameObject().GetType().equals(EnemyShip))
                .map(o -> (SpaceShip) o)
                .collect(Collectors.toList());

        for (SpaceShip enemy : enemyShips) {
            if (randomGenerator.nextDouble() > 0.5) {
                this.Shoot(enemy);
                EVENT_LOGGER.debug("Enemy shoot a torpedo.");
            }
        }
    }

    private boolean AnyEnemyShipAlive() {
        for (ObservableGameObject gameObject : this.GetGameObjects()) {
            if (gameObject.GetGameObject().GetType().equals(EnemyShip)
                    && !gameObject.GetGameObject().GetIsDead()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a new level with +1 enemy ship compared to the beginning of the current level. If
     * the level count reaches it's maximum the game is ended here and the player is victorious.
     */
    private void IncreaseLevel() {
        if (this.scoreHelper.GetLevel() < AppConstants.MaxLevelNumber) {
            EVENT_LOGGER.info("Increasing game level..");
            this.scoreHelper.IncreaseLevel();
            this.InitNewLevel(new GameLevel(
                    null,
                    this.scoreHelper.GetScore(),
                    this.scoreHelper.GetLevel()));
        } else if (this.scoreHelper.GetLevel() == AppConstants.MaxLevelNumber) {
            this.EndGame(true);
        }
    }

    /**
     * Maps the children of GamePane to List.
     */
    private List<ObservableGameObject> GetGameObjects() {
        return this.gamePane
                .getChildren()
                .stream()
                .map(o -> (ObservableGameObject) o)
                .collect(Collectors.toList());
    }

    /**
     * Moves the torpedoes shot by the player and checks if there is a hit.
     */
    private void UpdatePlayerTorpedoes() {
        for (ObservableGameObject playerTorpedo : this.GetGameObjects()) {
            if (playerTorpedo.GetGameObject().GetType().equals(PlayerTorpedo)) {
                playerTorpedo.TryMoveUp();
                this.GetGameObjects()
                        .stream()
                        .filter(enemy -> enemy.GetGameObject().GetType().equals(EnemyShip))
                        .forEach(enemy ->
                        {
                            if (playerTorpedo.GetGameObject().Intersect(enemy.GetGameObject())) {
                                enemy.SetDeath();
                                playerTorpedo.SetDeath();
                                this.scoreHelper.IncreaseScore();
                                this.scoreLabel.setText(this.scoreHelper.GetScoreAsString());
                                EVENT_LOGGER.info("Enemy ship destroyed.");
                            }
                        });
            }
        }
    }

    /**
     * Moves torpedoes shot by enemy ships and checks if there is a hit.
     */
    private void UpDateEnemyTorpedoes() {
        for (ObservableGameObject enemyTorpedo : this.GetGameObjects()) {
            if (enemyTorpedo.GetGameObject().GetType().equals(EnemyTorpedo)) {
                enemyTorpedo.TryMoveDown();
                if (enemyTorpedo.GetGameObject().Intersect(this.player.GetGameObject())) {
                    this.player.SetDeath();
                    enemyTorpedo.SetDeath();
                    EVENT_LOGGER.info("Player ship destroyed.");
                }
            }
        }
    }

    /**
     * Checks whether the player is alive. If not, the game is ended.
     */
    private void CheckPlayerStatus() {
        if (this.player.GetGameObject().GetIsDead()) {
            EVENT_LOGGER.info("Player is Dead.");
            this.EndGame(false);
        }
    }

    /**
     * Removes objects from the UI marked as 'dead'. An object is marked 'dead' when one of the following occurs:
     * torpedo reached GamePane boundary and failed to hit anything, enemy ship was hit by player's torpedo,
     * player was hit by enemy torpedo.
     */
    private void RemoveDeadObjects() {
        EVENT_LOGGER.debug("Removing dead objects.");
        List<ObservableGameObject> deadObjects = this.GetGameObjects()
                .stream()
                .filter(obj -> obj.GetGameObject().GetIsDead())
                .collect(Collectors.toList());

        this.gamePane.getChildren().removeAll(deadObjects);
        deadObjects.clear();

        EVENT_LOGGER.debug("Removed dead objects.");
    }

    /**
     * Initializes a new game level. It can be used to load any game level.
     */
    private void InitNewLevel(GameLevel gameLevel) {
        //Remove left-over objects and data
        EVENT_LOGGER.debug("Removing left-over objects..");
        this.gamePane.getChildren().removeAll(this.GetGameObjects());
        this.enemyMoveTimer = 0;

        this.levelLabel.setText(String.format("%s", gameLevel.GetLevel()));
        this.scoreLabel.setText(String.format("%s", gameLevel.GetScore()));

        if (gameLevel.getGameObjects() == null) {
            this.player = ObservableGameObjectFactory.CreatePlayerShip();
            EVENT_LOGGER.debug("Player created.");

            this.gamePane.getChildren().add(player);
            EVENT_LOGGER.debug("PLayer ship added to GamePane.");

            this.gamePane
                    .getChildren()
                    .addAll(ObservableGameObjectFactory.CreateEnemyShips(gameLevel.GetLevel()));
            EVENT_LOGGER.debug("Enemy ships added to GamePane.");
        } else {
            for (GameObject gameObject : gameLevel.getGameObjects()) {
                ArrayList<ObservableGameObject> objectsLoaded = new ArrayList<>();
                switch (gameObject.GetType()) {
                    case PlayerShip:
                        this.player = new SpaceShip(gameObject);
                        objectsLoaded.add(this.player);
                        break;
                    case EnemyShip:
                        objectsLoaded.add(new SpaceShip(gameObject));
                        break;
                    case PlayerTorpedo:
                    case EnemyTorpedo:
                        objectsLoaded.add(new Torpedo(gameObject));
                        break;
                }

                this.scoreHelper = new ScoreHelper(gameLevel.GetScore(), gameLevel.GetLevel());

                this.gamePane.getChildren().addAll(objectsLoaded);
                EVENT_LOGGER.info("Saved game loaded.");
            }
        }
    }

    /**
     * Creates a torpedo object on the UI linked. The type of the torpedo is defined by it's parent (shooter).
     *
     * @param shooter parent of the torpedo object. Either the player or an enemy ship.
     */
    private void Shoot(SpaceShip shooter) {
        Torpedo torpedo = ObservableGameObjectFactory.CreateTorpedo(shooter);
        this.gamePane.getChildren().add(torpedo);
        EVENT_LOGGER.debug(String.format("Torpedo shot by %s.", shooter.toString()));
    }

    /**
     * If the shooting cool down is 0, will shoot a torpedo (owned by the player).
     * If the game is on pause, the command will be ignored.
     */
    private void TryPlayerShoot() {
        if (!this.isPaused && this.playerShootCooldown <= 0.0) {
            this.Shoot(this.player);
            this.playerShootCooldown = 1.5;
            EVENT_LOGGER.debug("Player shot torpedo.");
        }
    }

    /**
     * If it can move to left, then it will move the player to the left.
     * If the game is on pause, the command will be ignored.
     */
    private void TryPlayerMoveLeft() {
        if (!this.isPaused) {
            this.player.TryMoveLeft();
        }
    }

    /**
     * If it can move to right, then it will move the player to the right.
     * If the game is on pause, the command will be ignored.
     */
    private void TryPlayerMoveRight() {
        if (!this.isPaused) {
            this.player.TryMoveRight();
        }
    }

    /**
     * Pauses the game. Scene update will be interrupted, UI objects will freeze.
     */
    private void PauseGame() {
        if (this.player.GetGameObject().GetIsDead()) {
            return;
        }

        if (this.isPaused) {
            this.timer.start();
            EVENT_LOGGER.debug("Game Continued");
        } else {
            this.timer.stop();
            EVENT_LOGGER.debug("Game Paused");
        }
        this.isPaused = !this.isPaused;
    }
}
