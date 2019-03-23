package Controller;

import Model.*;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
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
public class GameController {

    private final static Logger EventLogger = Logger.getLogger(GameController.class);

    private SpaceShip _player;
    private Pane _gamePane;
    private Label _scoreLabel;
    private Label _levelLabel;
    private ScoreHelper _scoreHelper;
    private double _playerShootCooldown = 0.0;
    private AnimationTimer _timer;
    private boolean _isPaused = false;
    private boolean _playerMoveLeft = false;
    private boolean _playerMoveRight = false;

    private boolean _enemyMoveToRight = false;
    private int _enemyMoveTimer = 0;
    private Random _randomGenerator;

    /** Creates an initialized GameController object.
     * @param primaryStage the main stage(window)
     * @throws IOException when the fxml file cannot be loaded
     * @throws IllegalArgumentException when primaryStage is null
     */
    public GameController(Stage primaryStage) throws IOException, IllegalArgumentException
    {
        if (primaryStage == null) {
            EventLogger.debug("primarystage was null. ");
            throw new IllegalArgumentException("primaryStage cannot be null. Pass a valid stage to build upon.");
        }

        AnchorPane root;
        try {
            root = FXMLLoader.load(getClass().getResource("..\\SpaceInvadersMainScene.fxml"));
        } catch (IOException ex) {
            EventLogger.debug("Cannot load fxml resource file. Make sure it is in the resources folder. " +
                    "Details: ", ex);
            throw ex;
        }

        this._timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                UpdateScene();
            }
        };
        this._scoreHelper = new ScoreHelper(0, 1);
        this._randomGenerator = new Random(Calendar.MILLISECOND);

        primaryStage.setTitle("Space Invaders");
        primaryStage.setResizable(false);

        Scene mainScene = new Scene(root);
        this._gamePane = (Pane) mainScene.lookup("#GamePane");
        this._scoreLabel = (Label) mainScene.lookup("#ScoreLabel");
        this._levelLabel = (Label) mainScene.lookup("#LevelLabel");

        if (this._gamePane == null) {
            throw new UnsupportedOperationException("GamePane was null. Error while loading element from fxml.");
        }

        if (this._scoreLabel == null) {
            throw new UnsupportedOperationException("ScoreLabel was null. Error while loading element from fxml.");
        }

        if (this._levelLabel == null) {
            throw new UnsupportedOperationException("LevelLabel was null. Error while loading element from fxml.");
        }

        this._gamePane.setPrefSize(AppConstants.MaxGamePaneWidth, AppConstants.MaxGamePaneHeight);
        this._gamePane.setFocusTraversable(true);

        this._gamePane.setOnKeyPressed(event ->
        {
            switch (event.getCode()) {
                case LEFT:

                    this._playerMoveLeft = true;
                    this._playerMoveRight = false;
                    break;

                case RIGHT:
                    this._playerMoveRight = true;
                    this._playerMoveLeft = false;
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
     * @param level given GameLevel to start.
     * @throws IllegalArgumentException if the level parameter is null.
     * */
    public void StartGame(GameLevel level)
    {
        if(level == null)
        {
            throw new IllegalArgumentException("Level cannot be null.");
        }

        this.InitNewLevel(level);
        _timer.start();
        EventLogger.info("Game started.");
    }

    /**
     * Loads a saved GameLevel from file
     * */
    private void LoadGame()
    {
        EventLogger.info("Loading game..");
        this._isPaused = true;
        this._timer.stop();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "SpaceInvaders saved game",
                "save");
        fileChooser.addChoosableFileFilter(filter);

        int response = fileChooser.showOpenDialog(null);
        if(response != JFileChooser.APPROVE_OPTION)
        {
            EventLogger.debug("User cancelled load operation.");
            return;
        }

        GameLevel loadedLevel = SavedGameHelper.LoadGame(fileChooser.getSelectedFile().getPath());
        if(loadedLevel != null)
        {
            EventLogger.info("Game level loaded from file.");
            this.InitNewLevel(loadedLevel);
            return;
        }
        EventLogger.error("Failed to load game level.");

    }

    /**
     * Saves a GameLevel to file. The resulting file can be loaded again to recreate the GameLevel.
     * */
    private void SaveGame()
    {
        EventLogger.info("Saving game..");
        this._isPaused = true;
        this._timer.stop();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int response = fileChooser.showOpenDialog(null);
        if(response != JFileChooser.APPROVE_OPTION)
        {
            EventLogger.debug("User cancelled save operation.");
            return;
        }

        ArrayList<GameObject> gameObjects=this._gamePane
                .getChildren()
                .stream()
                .map(o-> ((ObservableGameObject)o).GetGameObject())
                .collect(Collectors.toCollection(ArrayList::new));

        GameLevel level=new GameLevel(gameObjects, this._scoreHelper.GetScore(),this._scoreHelper.GetLevel());

        String message;
        String folderPathToSave=fileChooser.getSelectedFile().getPath();
        if(SavedGameHelper.SaveGame(folderPathToSave, level))
        {
            message="Game saved successfully.";
        }
        else
        {
            message="Failed to save game.";
        }
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Resets score, level and begins a new game from level one.
     * */
    private void RestartGame()
    {
        EventLogger.info("Game Restarted");
        this._isPaused = false;
        this._timer.stop();
        this._scoreHelper = new ScoreHelper(0,1);
        this.StartGame(GameLevel.GetInitialLevel());
    }

    /**
     * Ends the game and analyzes results. It will raise pop-up windows based on the outcome of the game.
     * */
    private void EndGame(boolean playerWon)
    {
        EventLogger.info("Game over.");

        this._timer.stop();
        this._isPaused=true;

        EventLogger.debug("Saving player score..");
        try
        {
            if(HighScoreHelper.SaveScore(this._scoreHelper.GetScore()))
            {
                EventLogger.info("Player achieved new high score.");
                JOptionPane.showMessageDialog(
                        null,
                        String.format("New HIGH SCORE: %s !!", this._scoreHelper.GetScore()));
            }
        }catch (IOException ex)
        {
            EventLogger.warn("Something went wrong while saving player score." +
                    " Score has not been saved. Details: %s", ex);
        }

        String message;
        if(playerWon)
        {
            message = String.format("Congratulations! You beat the game with a score of: %s", this._scoreHelper.GetScoreAsString());
            EventLogger.info("Player won.");
        }
        else
        {
            message = String.format("You are Dead! You made it to level: %s with a score of %s",
                    this._scoreHelper.GetLevelAsString(),
                    this._scoreHelper.GetScoreAsString());

            EventLogger.info(String.format("Player lost the game. Level %s, Score %s",
                    this._scoreHelper.GetLevelAsString(),
                    this._scoreHelper.GetScoreAsString()));
        }
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Main rendering method. Handles most of the UI logic.
     * */
    private void UpdateScene() {
        EventLogger.debug("Updating scene..");

        this.UpdatePlayerTorpedoes();
        this.UpDateEnemyTorpedoes();

        if (this._playerMoveLeft) {
            this.TryPlayerMoveLeft();
        }

        if (this._playerMoveRight) {
            this.TryPlayerMoveRight();
        }

        //Remove Dead Objects from the GamePane
        this.RemoveDeadObjects();

        //Check if player is alive
        this.CheckPlayerStatus();

        //Check if there are remaining enemies, else increase level
        if (!this.AnyEnemyShipAlive())
        {
            this.IncreaseLevel();
        }

        //Try to shoot torpedoes every 50 nanoseconds (enemies hav 50% chance to shoot)
        if(_enemyMoveTimer % 50 == 0)
        {
            this.EnemyShootTorpedoes();
        }

        this.MoveEnemyShips();

        if (this._playerShootCooldown > 0.0)
        {
            this._playerShootCooldown -= 0.1;
        }

        this._enemyMoveTimer++;
    }

    /**
     * Moves the enemy ships. Move algorithm: move every ship in one direction, until the first failed attempt
     * to move into that direction. Then change direction.
     * */
    private void MoveEnemyShips()
    {
        EventLogger.debug("Moving enemies.");
        List<SpaceShip> enemyShips = GetGameObjects()
                .stream()
                .filter(o-> o.GetGameObject().GetType().equals(EnemyShip))
                .map(o->(SpaceShip)o)
                .collect(Collectors.toList());

        for(SpaceShip enemyShip : enemyShips)
        {

                if (this._enemyMoveToRight)
                {
                    if(!enemyShip.TryMoveRight())
                    {
                        this._enemyMoveToRight=!this._enemyMoveToRight;
                    }
                }
                else
                {
                    if(!enemyShip.TryMoveLeft())
                    {
                        this._enemyMoveToRight=!this._enemyMoveToRight;
                    }
                }
            }

    }

    /**
     * Shot enemy torpedoes. Shooting algorithm: every enemy ship has a chance of 50% to shot a torpedo
     * */
    private void EnemyShootTorpedoes()
    {
       List<SpaceShip> enemyShips = GetGameObjects()
                .stream()
                .filter(o-> o.GetGameObject().GetType().equals(EnemyShip))
               .map(o->(SpaceShip)o)
                .collect(Collectors.toList());

       for (SpaceShip enemy : enemyShips)
       {
           if(_randomGenerator.nextDouble()>0.5)
           {
               this.Shoot(enemy);
               EventLogger.debug("Enemy shoot a torpedo.");
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
     * */
    private void IncreaseLevel()
    {
        if(this._scoreHelper.GetLevel()< AppConstants.MaxLevelNumber)
        {
            EventLogger.info("Increasing game level..");
            this._scoreHelper.IncreaseLevel();
            this.InitNewLevel(new GameLevel(
                    null,
                    this._scoreHelper.GetScore(),
                    this._scoreHelper.GetLevel()));
        }
        else if(this._scoreHelper.GetLevel() == AppConstants.MaxLevelNumber)
        {
            this.EndGame(true);
        }
    }

    /**
     * Maps the children of GamePane to List.
     * */
    private List<ObservableGameObject> GetGameObjects() {
        return this._gamePane.getChildren().stream().map(o -> (ObservableGameObject) o).collect(Collectors.toList());
    }

    /**
     * Moves the torpedoes shot by the player and checks if there is a hit.
     * */
    private void UpdatePlayerTorpedoes()
    {
        for (ObservableGameObject playerTorpedo : this.GetGameObjects())
        {
            if (playerTorpedo.GetGameObject().GetType().equals(PlayerTorpedo))
            {
                playerTorpedo.TryMoveUp();
                this.GetGameObjects()
                        .stream()
                        .filter(enemy -> enemy.GetGameObject().GetType().equals(EnemyShip))
                        .forEach(enemy ->
                        {
                            if (playerTorpedo.GetGameObject().Intersect(enemy.GetGameObject()))
                            {
                                enemy.SetDeath();
                                playerTorpedo.SetDeath();
                                this._scoreHelper.IncreaseScore();
                                this._scoreLabel.setText(this._scoreHelper.GetScoreAsString());
                                EventLogger.info("Enemy ship destroyed.");
                            }
                        });
            }
        }
    }

    /**
     * Moves torpedoes shot by enemy ships and checks if there is a hit.
     * */
    private void UpDateEnemyTorpedoes()
    {
        for (ObservableGameObject enemyTorpedo : this.GetGameObjects())
        {
            if (enemyTorpedo.GetGameObject().GetType().equals(EnemyTorpedo))
            {
                enemyTorpedo.TryMoveDown();
                if (enemyTorpedo.GetGameObject().Intersect(this._player.GetGameObject()))
                {
                    this._player.SetDeath();
                    enemyTorpedo.SetDeath();
                    EventLogger.info("Player ship destroyed.");
                }
            }
        }
    }

    /**
     * Checks whether the player is alive. If not, the game is ended.
     * */
    private void CheckPlayerStatus()
    {
        if (this._player.GetGameObject().GetIsDead())
        {
            EventLogger.info("Player is Dead.");
            this.EndGame(false);
        }
    }

    /**
     * Removes objects from the UI marked as 'dead'. An object is marked 'dead' when one of the following occurs:
     * torpedo reached GamePane boundary and failed to hit anything, enemy ship was hit by player's torpedo,
     * player was hit by enemy torpedo.
     * */
    private void RemoveDeadObjects()
    {
        EventLogger.debug("Removing dead objects.");
        List<ObservableGameObject> deadObjects = this.GetGameObjects()
                .stream()
                .filter(obj -> obj.GetGameObject().GetIsDead())
                .collect(Collectors.toList());

        this._gamePane.getChildren().removeAll(deadObjects);
        deadObjects.clear();

        EventLogger.debug("Removed dead objects.");
    }

    /**
     * Initializes a new game level. It can be used to load any game level.
     * */
    private void InitNewLevel(GameLevel gameLevel)
    {
        //Remove left-over objects and data
        EventLogger.debug("Removing left-over objects..");
        this._gamePane.getChildren().removeAll(this.GetGameObjects());
        this._enemyMoveTimer = 0;

        this._levelLabel.setText(String.format("%s",gameLevel.getLevel()));
        this._scoreLabel.setText(String.format("%s", gameLevel.getScore()));

        if(gameLevel.getGameObjects() == null)
        {
            this._player = ObservableGameObjectFactory.CreatePlayerShip();
            EventLogger.debug("Player created.");

            this._gamePane.getChildren().add(_player);
            EventLogger.debug("PLayer ship added to GamePane.");

            this._gamePane.getChildren().addAll(ObservableGameObjectFactory.CreateEnemyShips(gameLevel.getLevel()));
            EventLogger.debug("Enemy ships added to GamePane.");
        }
        else
            {
            for (GameObject gameObject : gameLevel.getGameObjects())
            {
                ArrayList<ObservableGameObject> objectsLoaded= new ArrayList<>();
                switch (gameObject.GetType())
                {
                    case PlayerShip:
                        this._player=new SpaceShip(gameObject);
                        objectsLoaded.add(this._player);
                        break;
                    case EnemyShip:
                        objectsLoaded.add(new SpaceShip(gameObject));
                        break;
                    case PlayerTorpedo:
                    case EnemyTorpedo:
                        objectsLoaded.add(new Torpedo(gameObject));
                        break;
                }

                this._scoreHelper=new ScoreHelper(gameLevel.getScore(), gameLevel.getLevel());

                this._gamePane.getChildren().addAll(objectsLoaded);
                EventLogger.info("Saved game loaded.");
            }
        }
    }

    /**
     * Creates a torpedo object on the UI linked. The type of the torpedo is defined by it's parent (shooter).
     * @param shooter parent of the torpedo object. Either the player or an enemy ship.
     * */
    private void Shoot(SpaceShip shooter)
    {
        Torpedo torpedo = ObservableGameObjectFactory.CreateTorpedo(shooter);
        this._gamePane.getChildren().add(torpedo);
        EventLogger.debug(String.format("Torpedo shot by %s.", shooter.toString()));
    }

    /**
     * If the shooting cool down is 0, will shoot a torpedo (owned by the player). If the game is on pause,
     * the command will be ignored.
     * */
    private void TryPlayerShoot() {
        if (!this._isPaused && this._playerShootCooldown <= 0.0)
        {
            this.Shoot(this._player);
            this._playerShootCooldown=1.5;
            EventLogger.debug("Player shot torpedo.");
        }
    }


    /**
     * If it can move to left, then it will move the player to the left.
     * If the game is on pause, the command will be ignored.
     * */
    private void TryPlayerMoveLeft() {
        if (!this._isPaused) {
            this._player.TryMoveLeft();
        }
    }

    /**
     * If it can move to right, then it will move the player to the right.
     * If the game is on pause, the command will be ignored.
     * */
    private void TryPlayerMoveRight() {
        if (!this._isPaused) {
            this._player.TryMoveRight();
        }
    }

    /**
    * Pauses the game. Scene update will be interrupted, UI objects will freeze.
    * */
    private void PauseGame()
    {
        if(this._player.GetGameObject().GetIsDead())
        {
            return;
        }

        if (this._isPaused)
        {
            this._timer.start();
            EventLogger.debug("Game Continued");
        }
        else
        {
            this._timer.stop();
            EventLogger.debug("Game Paused");
        }
        this._isPaused =! this._isPaused;
    }
}