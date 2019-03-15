import javafx.animation.AnimationTimer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;

public class GameController
{
    private final static Logger EventLogger = Logger.getLogger(GameController.class);
    private ArrayList<ObservableGameObject> _gameObjects;
    private SpaceShip _player;
    private Pane _gamePane;
    private Label _scoreLabel;
    private Integer _score = 0;
    private int _level=1;
    private AnimationTimer _timer;


    public GameController(Stage primaryStage) throws  IOException, IllegalArgumentException
    {
        if(primaryStage == null)
        {
            EventLogger.debug("primarystage was null. ");
            throw  new IllegalArgumentException("primaryStage cannor be null. Pass a valid stage to build upon.");
        }

        AnchorPane root;
        try
        {
            root = FXMLLoader.load(getClass().getResource("SpaceInvadersMainScene.fxml"));
        }
        catch (IOException ex)
        {
            EventLogger.debug("Cannot load fxml resource file. Make sure it is in the resources folder. " +
                    "Details: ", ex);
            throw ex;
        }

        this._gameObjects = new ArrayList<>();

        primaryStage.setTitle("Space Invaders");
        primaryStage.setResizable(false);

        Scene mainScene = new Scene(root);
        this._gamePane = (Pane) mainScene.lookup("#GamePane");
        this._scoreLabel = (Label) mainScene.lookup("#ScoreLabel");

        if (this._gamePane == null)
        {
            throw new UnsupportedOperationException("GamePane was null. Error while loading element from fxml.");
        }

        if (this._scoreLabel == null)
        {
            throw new UnsupportedOperationException("ScoreLabel was null. Error while loading element from fxml.");
        }

        this._gamePane.setFocusTraversable(true);

        this._gamePane.setOnKeyPressed(event ->
        {
            switch (event.getCode()) {
                case LEFT:
                    this._player.MoveLeft();
                    EventLogger.debug("Player moved to the left.");
                    break;
                case RIGHT:
                    this._player.MoveRight();
                    EventLogger.debug("Player moved to the right.");
                    break;
                case SPACE:
                    this.Shoot(this._player);
                    EventLogger.debug("Player shot torpedo.");
                    break;
            }
        });

        _timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                UpdateScene();
            }
        };



        primaryStage.setScene((mainScene));
        primaryStage.show();
    }

    public void StartGame()
    {

        this.InitGamePane();

        EventLogger.info("Game started.");
        _timer.start();
        while (true)
        {

        }
        EventLogger.info("Game over.");
    }

    private Pane InitGamePane()
    {
        root.setPrefSize(AppConstants.MaxGamePaneWidth, AppConstants.MaxGamePaneHeight);
        ConfigObject playerConfig = new ConfigObject(
                AppConstants.PlayerShipXCoordinate,
                AppConstants.PlayerShipYCoordinate,
                AppConstants.PlayerShipHeight,
                AppConstants.PlayerShipWidth,
                "Player",
                Color.BLUE);
        logger.debug("Player created.");

        Player = new SpaceShip(playerConfig);
        root.getChildren().add(Player);
        logger.debug("PLayer ship added to GamePane.");

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                UpdateScene();
            }
        };

        timer.start();

        root.getChildren().addAll(CreateEnemies(5));
        logger.debug("Enemy ships added to GamePane.");

        return root;
    }


}
