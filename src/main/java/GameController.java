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

/**
 * Main controller class
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
    protected GameController(Stage primaryStage) throws IOException, IllegalArgumentException
    {
        if (primaryStage == null) {
            EventLogger.debug("primarystage was null. ");
            throw new IllegalArgumentException("primaryStage cannot be null. Pass a valid stage to build upon.");
        }

        AnchorPane root;
        try {
            root = FXMLLoader.load(getClass().getResource("SpaceInvadersMainScene.fxml"));
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

    protected void StartGame(GameLevel gameStance)
    {
        this.InitNewLevel(gameStance);
        _timer.start();
        EventLogger.info("Game started.");
    }

    private void LoadGame()
    {
        EventLogger.info("Loading game..");
        this._isPaused=true;
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
        if(loadedLevel!=null)
        {
            EventLogger.info("Game level loaded from file.");
            this.InitNewLevel(loadedLevel);
            return;
        }
        EventLogger.error("Failed to load game level.");
    }

    private void SaveGame()
    {
        EventLogger.info("Saving game..");
        this._isPaused=true;
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

    private void RestartGame()
    {
        EventLogger.info("Game Restarted");
        this._scoreHelper= new ScoreHelper(0,1);
        this.StartGame(new GameLevel(
                null,
                this._scoreHelper.GetScore(),
                this._scoreHelper.GetLevel()));
    }

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
            message = String.format("You are Dead! You made it to level: %s", this._scoreHelper.GetLevelAsString());
            EventLogger.info(String.format("Player lost the game. Made it to level %s", this._scoreHelper.GetScoreAsString()));
        }
        JOptionPane.showMessageDialog(null, message);
    }

    private void UpdateScene() {
        EventLogger.debug("Updating scene..");

        this.UpdatePlayerTorpedos();
        this.UpDateEnemyTorpedos();

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

        //If any enemy is alive, shoot a random torpedo and move
        if(_enemyMoveTimer % 50 == 0)
        {
            this.EnemyShootTorpedos();
        }


        this.MoveEnemyShips();

        if (this._playerShootCooldown > 0.0)
        {
            this._playerShootCooldown-=0.1;
        }

        this._enemyMoveTimer++;
    }

    private void MoveEnemyShips()
    {
        EventLogger.debug("Moving enemies.");
        List<SpaceShip> enemyShips = GetGameObjects()
                .stream()
                .filter(o-> o.GetGameObject().GetType().equals(GameObjectType.EnemyShip))
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

    private void EnemyShootTorpedos()
    {
       List<SpaceShip> enemyShips = GetGameObjects()
                .stream()
                .filter(o-> o.GetGameObject().GetType().equals(GameObjectType.EnemyShip))
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
            if (gameObject.GetGameObject().GetType().equals(GameObjectType.EnemyShip)
                    && !gameObject.GetGameObject().GetIsDead()) {
                return true;
            }
        }
        return false;
    }

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

    private List<ObservableGameObject> GetGameObjects() {
        return this._gamePane.getChildren().stream().map(o -> (ObservableGameObject) o).collect(Collectors.toList());
    }

    private void UpdatePlayerTorpedos()
    {
        for (ObservableGameObject observableGameObject : this.GetGameObjects())
        {
            if (observableGameObject.GetGameObject().GetType().equals(GameObjectType.PlayerTorpedo))
            {
                observableGameObject.TryMoveUp();
                this.GetGameObjects()
                        .stream()
                        .filter(enemy -> enemy.GetGameObject().GetType().equals(GameObjectType.EnemyShip))
                        .forEach(enemy ->
                        {
                            if (enemy.GetGameObject().IntersectLowerBounds(observableGameObject.GetGameObject()))
                            {
                                enemy.SetDeath();
                                observableGameObject.SetDeath();
                                this._scoreHelper.IncreaseScore();
                                this._scoreLabel.setText(this._scoreHelper.GetScoreAsString());
                                EventLogger.info("Enemy ship destroyed.");
                            }
                        });
            }
        }
    }

    private void UpDateEnemyTorpedos()
    {
        for (ObservableGameObject observableGameObject : this.GetGameObjects())
        {
            if (observableGameObject.GetGameObject().GetType().equals(GameObjectType.EnemyTorpedo))
            {
                observableGameObject.TryMoveDown();
                if (this._player.GetGameObject().IntersectUpperBounds(observableGameObject.GetGameObject()))
                {
                    this._player.SetDeath();
                    observableGameObject.SetDeath();
                    EventLogger.info("Player ship destroyed.");
                }
            }
        }
    }

    private void CheckPlayerStatus()
    {
        if (this._player.GetGameObject().GetIsDead())
        {
            EventLogger.info("Player is Dead.");
            this.EndGame(false);
        }
    }

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

    private void InitNewLevel(GameLevel gameLevel)
    {
        //Remove left-over objects
        EventLogger.debug("Removing left-over objects..");
        this._gamePane.getChildren().removeAll(this.GetGameObjects());
        this._enemyMoveTimer =0;
        this._isPaused=false;

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

    private void Shoot(SpaceShip shooter)
    {
        Torpedo torpedo = ObservableGameObjectFactory.CreateTorpedo(shooter);
        this._gamePane.getChildren().add(torpedo);
        EventLogger.debug(String.format("Torpedo shot by %s.", shooter.toString()));
    }

    private void TryPlayerShoot() {
        if (!this._isPaused && this._playerShootCooldown <= 0.0)
        {
            this.Shoot(this._player);
            this._playerShootCooldown=1.5;
            EventLogger.debug("Player shot torpedo.");
        }
    }

    private void TryPlayerMoveLeft() {
        if (!this._isPaused) {
            this._player.TryMoveLeft();
        }
    }

    private void TryPlayerMoveRight() {
        if (!this._isPaused) {
            this._player.TryMoveRight();
        }
    }


    private void PauseGame()
    {
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
        this._isPaused=!this._isPaused;
    }
}
