import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.Resource;
import org.apache.log4j.xml.DOMConfigurator;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

    public GameController(Stage primaryStage) throws IOException, IllegalArgumentException
    {

       // DOMConfigurator.configure("log4j2.xml");

        if (primaryStage == null) {
            EventLogger.debug("primarystage was null. ");
            throw new IllegalArgumentException("primaryStage cannor be null. Pass a valid stage to build upon.");
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
                    this.LoadGame(primaryStage);
                    break;
            }
        });

        primaryStage.setScene((mainScene));
        primaryStage.show();
    }

    public void StartGame(GameLevel gameStance)
    {
        this.InitNewLevel(gameStance);
        _timer.start();
        EventLogger.info("Game started.");
    }

    private void LoadGame(Stage root)
    {
        EventLogger.info("Loading game..");
        this._isPaused=true;
        this._timer.stop();

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(root);
        if(file == null)
        {
            return;
        }

        SavedGameHelper.LoadGame(file.getPath());
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
        if(this._scoreHelper.GetLevel()< AppConstants.BossBattleLevelNumber)
        {
            EventLogger.info("Increasing game level..");
            this._scoreHelper.IncreaseLevel();
            this.InitNewLevel(new GameLevel(
                    null,
                    this._scoreHelper.GetScore(),
                    this._scoreHelper.GetLevel()));
        }
        else if(this._scoreHelper.GetLevel() == AppConstants.BossBattleLevelNumber)
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
                            if (observableGameObject.getBoundsInParent().intersects(enemy.getBoundsInParent()))
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
                if (observableGameObject.getBoundsInParent().intersects(this._player.getBoundsInParent()))
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
            this._player = GameObjectFactory.CreatePlayerShip();
            EventLogger.debug("Player created.");

            this._gamePane.getChildren().add(_player);
            EventLogger.debug("PLayer ship added to GamePane.");

            this._gamePane.getChildren().addAll(GameObjectFactory.CreateEnemyShips(gameLevel.getLevel()));
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

                this._gamePane.getChildren().addAll(objectsLoaded);
                EventLogger.info("Saved game loaded.");
            }
        }
    }

    private void Shoot(SpaceShip shooter)
    {
        Torpedo torpedo = GameObjectFactory.CreateTorpedo(shooter);
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
