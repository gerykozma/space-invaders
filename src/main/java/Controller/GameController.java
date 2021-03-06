package controller;

import model.*;
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

import static model.GameObjectType.*;

/**
 * Main game engine. Responsible for running the game and managing user inputs.
 */
public class GameController {

    /**
     * General event logger instance.
     */
    private final static Logger EVENT_LOGGER = Logger.getLogger(GameController.class);

    /**
     * Player ship.
     */
    private SpaceShip player;

    /**
     * Main game canvas.
     */
    private Pane gamePane;

    /**
     * UI label that represent player score.
     */
    private Label scoreLabel;

    /**
     * UI label that represent game level.
     */
    private Label levelLabel;

    /**
     * Object that manages player score calculation based on level and destroyed enemies.
     */
    private ScoreHelper scoreHelper;

    /**
     * Cooldown between player shots.
     */
    private double playerShootCooldown = 0.0;

    /**
     * Field that indicates whether the game is paused.
     */
    private boolean isPaused = false;

    /**
     * Field that indicates whether the player is moving to the left.
     */
    private boolean playerMoveLeft = false;

    /**
     * Field that indicates whether the player is moving to the right.
     */
    private boolean playerMoveRight = false;

    private boolean enemyMoveToRight = false;
    private int enemyMoveTimer = 0;
    private Random randomGenerator;
    private AnimationTimer timer;

    /**
     * Creates an initialized GameController object.
     *
     * @param primaryStage the Main stage(window)
     * @throws IOException              when the fxml file cannot be loaded
     * @throws IllegalArgumentException when primaryStage is null
     */
    public GameController(Stage primaryStage) throws IOException, IllegalArgumentException {
        if (primaryStage == null) {
            EVENT_LOGGER.debug("primarystage was null. ");
            throw new IllegalArgumentException("primaryStage cannot be null. Pass a valid stage to build upon.");
        }

        AnchorPane root;
        try {
            root = FXMLLoader.load(getClass().getClassLoader().getResource("SpaceInvadersMainScene.fxml"));
        } catch (IOException ex) {
            EVENT_LOGGER.debug("Cannot load fxml resource file. Make sure it is in the resources folder. " +
                    "Details: ", ex);
            throw ex;
        }

        this.timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateScene();
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

        this.gamePane.setPrefSize(AppConstants.MAX_GAME_PANE_WIDTH, AppConstants.MAX_GAME_PANE_HEIGHT);
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
                    this.tryPlayerShoot();
                    break;

                case P:
                    this.pauseGame();
                    break;
                case R:
                    this.restartGame();
                    break;
                case ESCAPE:
                    System.exit(0);
                    break;
                case L:
                    this.loadGame();
                    break;
                case S:
                    this.saveGame();
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
    public void startGame(GameLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("Level cannot be null.");
        }

        this.initNewLevel(level);
        timer.start();
        EVENT_LOGGER.info("Game started.");
    }

    /**
     * Loads a saved GameLevel from file
     */
    private void loadGame() {
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
                .loadGame(fileChooser.getSelectedFile().getPath());
        if (loadedLevel != null) {
            EVENT_LOGGER.info("Game level loaded from file.");
            this.initNewLevel(loadedLevel);
            return;
        }
        EVENT_LOGGER.error("Failed to load game level.");

    }

    /**
     * Saves a GameLevel to file. The resulting file can be loaded
     * again to recreate the GameLevel.
     */
    private void saveGame() {
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
                .map(o -> ((ObservableGameObject) o).getGameObject())
                .collect(Collectors.toCollection(ArrayList::new));

        GameLevel level = new GameLevel(
                gameObjects,
                this.scoreHelper.getScore(),
                this.scoreHelper.getLevel());

        String message;
        String folderPathToSave = fileChooser.getSelectedFile().getPath();
        if (SavedGameHelper.saveGame(folderPathToSave, level)) {
            message = "Game saved successfully.";
        } else {
            message = "Failed to save game.";
        }
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Resets score, level and begins a new game from level one.
     */
    private void restartGame() {
        EVENT_LOGGER.info("Game Restarted");
        this.isPaused = false;
        this.timer.stop();
        this.scoreHelper = new ScoreHelper(0, 1);
        this.startGame(GameLevel.getInitialLevel());
    }

    /**
     * Ends the game and analyzes results. It will raise pop-up windows based
     * on the outcome of the game.
     */
    private void endGame(boolean playerWon) {
        EVENT_LOGGER.info("Game over.");

        this.timer.stop();
        this.isPaused = true;

        EVENT_LOGGER.debug("Saving player score..");
        try {
            if (HighScoreHelper.saveScore(this.scoreHelper.getScore())) {
                EVENT_LOGGER.info("Player achieved new high score.");
                JOptionPane.showMessageDialog(
                        null,
                        String.format("New HIGH SCORE: %s !!", this.scoreHelper.getScore()));
            }
        } catch (IOException ex) {
            EVENT_LOGGER.warn("Something went wrong while saving player score." +
                    " Score has not been saved. Details: %s", ex);
        }

        String message;
        if (playerWon) {
            message = String.format("Congratulations! You beat the game with a score of: %s",
                    this.scoreHelper.getScoreAsString());
            EVENT_LOGGER.info("Player won.");
        } else {
            message = String.format("You are Dead! You made it to level: %s with a score of %s",
                    this.scoreHelper.getLevelAsString(),
                    this.scoreHelper.getScoreAsString());

            EVENT_LOGGER.info(String.format("Player lost the game. Level %s, Score %s",
                    this.scoreHelper.getLevelAsString(),
                    this.scoreHelper.getScoreAsString()));
        }
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Main rendering method. Handles most of the UI logic.
     */
    private void updateScene() {
        EVENT_LOGGER.debug("Updating scene..");

        this.updatePlayerTorpedoes();
        this.upDateEnemyTorpedoes();

        if (this.playerMoveLeft) {
            this.tryPlayerMoveLeft();
        }

        if (this.playerMoveRight) {
            this.tryPlayerMoveRight();
        }

        //Remove Dead Objects from the GamePane
        this.removeDeadObjects();

        //Check if player is alive
        this.checkPlayerStatus();

        //Check if there are remaining enemies, else increase level
        if (!this.anyEnemyShipAlive()) {
            this.increaseLevel();
        }

        //Try to shoot torpedoes every 50 nanoseconds (enemies hav 50% chance to shoot)
        if (enemyMoveTimer % 50 == 0) {
            this.enemyShootTorpedoes();
        }

        this.moveEnemyShips();

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
    private void moveEnemyShips() {
        EVENT_LOGGER.debug("Moving enemies.");
        List<SpaceShip> enemyShips = getGameObjects()
                .stream()
                .filter(o -> o.getGameObject().getType().equals(EnemyShip))
                .map(o -> (SpaceShip) o)
                .collect(Collectors.toList());

        for (SpaceShip enemyShip : enemyShips) {

            if (this.enemyMoveToRight) {
                if (!enemyShip.tryMoveRight()) {
                    this.enemyMoveToRight = !this.enemyMoveToRight;
                }
            } else {
                if (!enemyShip.tryMoveLeft()) {
                    this.enemyMoveToRight = !this.enemyMoveToRight;
                }
            }
        }

    }

    /**
     * Shot enemy torpedoes. Shooting algorithm: every enemy ship has
     * a chance of 50% to shot a torpedo.
     */
    private void enemyShootTorpedoes() {
        List<SpaceShip> enemyShips = getGameObjects()
                .stream()
                .filter(o -> o.getGameObject().getType().equals(EnemyShip))
                .map(o -> (SpaceShip) o)
                .collect(Collectors.toList());

        for (SpaceShip enemy : enemyShips) {
            if (randomGenerator.nextDouble() > 0.5) {
                this.shoot(enemy);
                EVENT_LOGGER.debug("Enemy shoot a torpedo.");
            }
        }
    }

    private boolean anyEnemyShipAlive() {
        for (ObservableGameObject gameObject : this.getGameObjects()) {
            if (gameObject.getGameObject().getType().equals(EnemyShip)
                    && !gameObject.getGameObject().getIsDead()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a new level with +1 enemy ship compared to the beginning of the current level. If
     * the level count reaches it's maximum the game is ended here and the player is victorious.
     */
    private void increaseLevel() {
        if (this.scoreHelper.getLevel() < AppConstants.MAX_LEVEL_NUMBER) {
            EVENT_LOGGER.info("Increasing game level..");
            this.scoreHelper.increaseLevel();
            this.initNewLevel(new GameLevel(
                    null,
                    this.scoreHelper.getScore(),
                    this.scoreHelper.getLevel()));
        } else if (this.scoreHelper.getLevel() == AppConstants.MAX_LEVEL_NUMBER) {
            this.endGame(true);
        }
    }

    /**
     * Maps the children of GamePane to List.
     */
    private List<ObservableGameObject> getGameObjects() {
        return this.gamePane
                .getChildren()
                .stream()
                .map(o -> (ObservableGameObject) o)
                .collect(Collectors.toList());
    }

    /**
     * Moves the torpedoes shot by the player and checks if there is a hit.
     */
    private void updatePlayerTorpedoes() {
        for (ObservableGameObject playerTorpedo : this.getGameObjects()) {
            if (playerTorpedo.getGameObject().getType().equals(PlayerTorpedo)) {
                playerTorpedo.tryMoveUp();
                this.getGameObjects()
                        .stream()
                        .filter(enemy -> enemy.getGameObject().getType().equals(EnemyShip))
                        .forEach(enemy ->
                        {
                            if (playerTorpedo.getGameObject().intersect(enemy.getGameObject())) {
                                enemy.setDeath();
                                playerTorpedo.setDeath();
                                this.scoreHelper.increaseScore();
                                this.scoreLabel.setText(this.scoreHelper.getScoreAsString());
                                EVENT_LOGGER.info("Enemy ship destroyed.");
                            }
                        });
            }
        }
    }

    /**
     * Moves torpedoes shot by enemy ships and checks if there is a hit.
     */
    private void upDateEnemyTorpedoes() {
        for (ObservableGameObject enemyTorpedo : this.getGameObjects()) {
            if (enemyTorpedo.getGameObject().getType().equals(EnemyTorpedo)) {
                enemyTorpedo.tryMoveDown();
                if (enemyTorpedo.getGameObject().intersect(this.player.getGameObject())) {
                    this.player.setDeath();
                    enemyTorpedo.setDeath();
                    EVENT_LOGGER.info("Player ship destroyed.");
                }
            }
        }
    }

    /**
     * Checks whether the player is alive. If not, the game is ended.
     */
    private void checkPlayerStatus() {
        if (this.player.getGameObject().getIsDead()) {
            EVENT_LOGGER.info("Player is Dead.");
            this.endGame(false);
        }
    }

    /**
     * Removes objects from the UI marked as 'dead'. An object is marked 'dead' when one of the following occurs:
     * torpedo reached GamePane boundary and failed to hit anything, enemy ship was hit by player's torpedo,
     * player was hit by enemy torpedo.
     */
    private void removeDeadObjects() {
        EVENT_LOGGER.debug("Removing dead objects.");
        List<ObservableGameObject> deadObjects = this.getGameObjects()
                .stream()
                .filter(obj -> obj.getGameObject().getIsDead())
                .collect(Collectors.toList());

        this.gamePane.getChildren().removeAll(deadObjects);
        deadObjects.clear();

        EVENT_LOGGER.debug("Removed dead objects.");
    }

    /**
     * Initializes a new game level. It can be used to load any game level.
     */
    private void initNewLevel(GameLevel gameLevel) {
        //Remove left-over objects and data
        EVENT_LOGGER.debug("Removing left-over objects..");
        this.gamePane.getChildren().removeAll(this.getGameObjects());
        this.enemyMoveTimer = 0;

        this.levelLabel.setText(String.format("%s", gameLevel.getLevel()));
        this.scoreLabel.setText(String.format("%s", gameLevel.getScore()));

        if (gameLevel.getGameObjects() == null) {
            this.player = ObservableGameObjectFactory.createPlayerShip();
            EVENT_LOGGER.debug("Player created.");

            this.gamePane.getChildren().add(player);
            EVENT_LOGGER.debug("PLayer ship added to GamePane.");

            this.gamePane
                    .getChildren()
                    .addAll(ObservableGameObjectFactory.createEnemyShips(gameLevel.getLevel()));
            EVENT_LOGGER.debug("Enemy ships added to GamePane.");
        } else {
            for (GameObject gameObject : gameLevel.getGameObjects()) {
                ArrayList<ObservableGameObject> objectsLoaded = new ArrayList<>();
                switch (gameObject.getType()) {
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

                this.scoreHelper = new ScoreHelper(gameLevel.getScore(), gameLevel.getLevel());

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
    private void shoot(SpaceShip shooter) {
        Torpedo torpedo = ObservableGameObjectFactory.createTorpedo(shooter);
        this.gamePane.getChildren().add(torpedo);
        EVENT_LOGGER.debug(String.format("Torpedo shot by %s.", shooter.toString()));
    }

    /**
     * If the shooting cool down is 0, will shoot a torpedo (owned by the player).
     * If the game is on pause, the command will be ignored.
     */
    private void tryPlayerShoot() {
        if (!this.isPaused && this.playerShootCooldown <= 0.0) {
            this.shoot(this.player);
            this.playerShootCooldown = 1.5;
            EVENT_LOGGER.debug("Player shot torpedo.");
        }
    }

    /**
     * If it can move to left, then it will move the player to the left.
     * If the game is on pause, the command will be ignored.
     */
    private void tryPlayerMoveLeft() {
        if (!this.isPaused) {
            this.player.tryMoveLeft();
        }
    }

    /**
     * If it can move to right, then it will move the player to the right.
     * If the game is on pause, the command will be ignored.
     */
    private void tryPlayerMoveRight() {
        if (!this.isPaused) {
            this.player.tryMoveRight();
        }
    }

    /**
     * Pauses the game. Scene update will be interrupted, UI objects will freeze.
     */
    private void pauseGame() {
        if (this.player.getGameObject().getIsDead()) {
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
