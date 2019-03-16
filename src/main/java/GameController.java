import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
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

    private AnimationTimer _timer;
    private boolean _isPaused = false;

    private int _enemyFireRate;
    private boolean _enemyMoveToRight=false;
    private Random _randomGenerator;

    public GameController(Stage primaryStage) throws IOException, IllegalArgumentException {
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
        this._levelLabel=(Label) mainScene.lookup("#LevelLabel");

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
                    this.TryPlayerMoveLeft();
                    break;

                case RIGHT:
                    this.TryPlayerMoveRight();
                    break;

                case SPACE:
                    this.TryPlayerShoot();
                    break;

                case P:
                    this.PauseGame();
                    break;
            }
        });

        primaryStage.setScene((mainScene));
        primaryStage.show();
    }

    public void StartGame() {

        this.InitNewLevel();
        _timer.start();
        EventLogger.info("Game started.");
    }

    private void EndGame() {
        _timer.stop();
        EventLogger.info("Game over.");
    }

    private void UpdateScene() {
        EventLogger.debug("Updating scene..");

        this.UpdatePlayerTorpedos();
        this.UpDateEnemyTorpedos();

        //Check if player is alive
        this.CheckPlayerStatus();

        //Remove Dead Objects from the GamePane
        this.RemoveDeadObjects();

        //Check if there are remaining enemies, else increase level
        if (!this.AnyEnemyShipAlive()) {
            this.IncreaseLevel();
        }

        //If any enemy is alive, shoot a random torpedo and move
        this.EnemyShootTorpedos();

        this.MoveEnemyShips();

    }

    private void MoveEnemyShips()
    {
        List<SpaceShip> enemyShips = GetGameObjects()
                .stream()
                .filter(o-> o.GetGameObject().GetType().equals(GameObjectType.EnemyShip))
                .map(o->(SpaceShip)o)
                .collect(Collectors.toList());

        if(_enemyFireRate%100==0)
        {
            this._enemyMoveToRight=!this._enemyMoveToRight;
        }

        for(SpaceShip enemyShip : enemyShips)
        {
            if(this._enemyMoveToRight)
            {
                enemyShip.TryMoveRight();
            }
            else
            {
                enemyShip.TryMoveLeft();

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

       if(this._enemyFireRate % 200 == 0)
       {
            int randomIndex = this._randomGenerator.nextInt(enemyShips.size());
            this.Shoot(enemyShips.get(randomIndex));
       }

        this._enemyFireRate++;
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
            this._scoreHelper.IncreaseLevel();
            this.InitNewLevel();
        }
    }

    private List<ObservableGameObject> GetGameObjects() {
        return this._gamePane.getChildren().stream().map(o -> (ObservableGameObject) o).collect(Collectors.toList());
    }

    private void UpdatePlayerTorpedos() {
        for (ObservableGameObject observableGameObject : this.GetGameObjects()) {
            if (observableGameObject.GetGameObject().GetType().equals(GameObjectType.PlayerTorped)) {
                observableGameObject.TryMoveUp();
                this.GetGameObjects()
                        .stream()
                        .filter(enemy -> enemy.GetGameObject().GetType().equals(GameObjectType.EnemyShip))
                        .forEach(enemy ->
                        {
                            if (observableGameObject.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
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
            this.EndGame();
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

    private void InitNewLevel() {

        //Remove left-over objects
        this._gamePane.getChildren().removeAll(this.GetGameObjects());
        this._enemyFireRate =0;

        this._levelLabel.setText(this._scoreHelper.GetLevelAsString());

        this._player = GameObjectFactory.CreatePlayerShip();
        EventLogger.debug("Player created.");

        this._gamePane.getChildren().add(_player);
        EventLogger.debug("PLayer ship added to GamePane.");

        this._gamePane.getChildren().addAll(GameObjectFactory.CreateEnemyShips(this._scoreHelper.GetLevel()));
        EventLogger.debug("Enemy ships added to GamePane.");
    }

    private void Shoot(SpaceShip shooter)
    {
        Torpedo torpedo = GameObjectFactory.CreateTorpedo(shooter);
        this._gamePane.getChildren().add(torpedo);
        EventLogger.debug(String.format("Torpedo shot by %s.", shooter.toString()));
    }

    private void TryPlayerShoot() {
        if (!this._isPaused) {
            this.Shoot(this._player);
            EventLogger.debug("Player shot torpedo.");
        }
    }

    private void TryPlayerMoveLeft() {
        if (!this._isPaused) {
            this._player.TryMoveLeft();
            EventLogger.debug("Player moved to the left.");
        }
    }

    private void TryPlayerMoveRight() {
        if (!this._isPaused) {
            this._player.TryMoveRight();
            EventLogger.debug("Player moved to the right.");
        }
    }

    private void PauseGame()
    {
        if (this._isPaused)
        {
            this._timer.start();
        }
        else
        {
            this._timer.stop();
        }
        this._isPaused=!this._isPaused;
    }
}
