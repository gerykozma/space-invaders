
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends Application {
    private SpaceShip Player;
    private Pane GamePane;
    private Label ScoreLabel;
    private Integer Score = 0;
    private int Level=1;
    private final static Logger logger = Logger.getLogger(Main.class);
    private int timer=0;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        //GameController game = new GameController(primaryStage);
        //game.Start();

        AnchorPane root = FXMLLoader.load(getClass().getResource("SpaceInvadersMainScene.fxml"));
        primaryStage.setTitle("Space Invaders");
        primaryStage.setResizable(false);



        Scene mainScene = new Scene(root);

        this.GamePane = (Pane) mainScene.lookup("#GamePane");
        this.ScoreLabel = (Label) mainScene.lookup("#ScoreLabel");

        if (this.GamePane == null) {
            throw new UnsupportedOperationException("GamePane was null. Error while loading element from fxml.");
        }

        if (this.ScoreLabel == null) {
            throw new UnsupportedOperationException("ScoreLabel was null. Error while loading element from fxml.");
        }

        this.GamePane = this.Init(this.GamePane);
        this.GamePane.setFocusTraversable(true);

        this.GamePane.setOnKeyPressed(event ->
        {
            switch (event.getCode()) {
                case LEFT:
                    this.Player.MoveLeft();
                    logger.debug("Player moved to the left.");
                    break;
                case RIGHT:
                    this.Player.MoveRight();
                    logger.debug("Player moved to the right.");
                    break;
                case SPACE:
                    this.Shoot(this.Player);
                    logger.debug("Player shot torpedo.");
                    break;
            }
        });

        primaryStage.setScene((mainScene));
        primaryStage.show();

//        primaryStage.setScene(mainScene);
//        primaryStage.show();
        //GameController.StartGame();
    }

    private Pane Init(Pane root)
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

    private SpaceShip[] CreateEnemies(int numberOfEnemiesToCreate) {
        ArrayList<SpaceShip> ships = new ArrayList<>();
        for (int i = 0; i < numberOfEnemiesToCreate; i++) {
            ConfigObject config = new ConfigObject(
                    90 + i * 100,
                    AppConstants.EnemyShipYCoordinate,
                    AppConstants.EnemyShipHeight,
                    AppConstants.EnemyShipWidth,
                    "Enemy",
                    Color.RED);
            ships.add(new SpaceShip(config));
            logger.debug("Enemy ship created.");
        }

        SpaceShip[] a = new SpaceShip[ships.size()];
        return ships.toArray(a);
    }

    private List<ObservableGameObject> GetGameObjects()
    {
        return this.GamePane.getChildren().stream().map(o-> (ObservableGameObject)o).collect(Collectors.toList());
    }

    private void Shoot(SpaceShip shooter)
    {
        ConfigObject config = new ConfigObject(
                shooter.getTranslateX()+20,
                shooter.getTranslateY()-10,
                15,
                5,
                shooter.Type+"Torpedo",
                Color.BLACK);

        this.GamePane.getChildren().add(new Torpedo(config));
        logger.debug(String.format("Torpedo shot by %s.", shooter.Type));
    }

    private void UpdateScene()
    {
        logger.debug("Updating scene..");
        for(ObservableGameObject gameObject : this.GetGameObjects())
        {
            if (gameObject.Type.equals("PlayerTorpedo"))
            {
                gameObject.MoveUp();
                this.GetGameObjects()
                        .stream()
                        .filter(enemy-> enemy.Type.equals("Enemy"))
                        .forEach(enemy->
                        {
                            if(gameObject.getBoundsInParent().intersects(enemy.getBoundsInParent()))
                            {
                                enemy.IsDead=true;
                                gameObject.IsDead=true;
                                this.Score=this.Score+100*this.Level;
                                this.ScoreLabel.setText(this.Score.toString());
                                logger.info("Enemy ship destroyed.");
                            }
                        });
            }

            if (gameObject.Type.equals("EnemyTorpedo"))
            {
                gameObject.MoveDown();
                if(gameObject.getBoundsInParent().intersects(this.Player.getBoundsInParent()))
                {
                    this.Player.IsDead=true;
                    gameObject.IsDead=true;
                    logger.info("Player ship destroyed.");
                }
            }
        }

        //Remove Dead Objects
        logger.debug("Removing dead objects.");
        List<ObservableGameObject> deadObjects = GamePane
                .getChildren()
                .stream()
                .map(obj-> (ObservableGameObject)obj)
                .filter(obj-> obj.IsDead)
                .collect(Collectors.toList());
        GamePane.getChildren().removeAll(deadObjects);
        deadObjects.clear();
        logger.debug("Removed dead objects.");

        for(ObservableGameObject enemy : this.GamePane.getChildren().stream().map(o-> (ObservableGameObject)o).filter(o-> o.Type.equals("Enemy")).collect(Collectors.toList()))
        {
            if(timer%100==0)
            {
                SpaceShip enemyShip= (SpaceShip)enemy;
                this.Shoot(enemyShip);
                break;
            }
        }

        timer++;

        if(this.Player.IsDead)
        {

        }
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
