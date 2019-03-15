
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends Application {
    private SpaceShip Player;
    private Pane GamePane;
    private Label ScoreLabel;
    private Integer Score = 0;
    private int Level=1;

    @Override
    public void start(Stage primaryStage) throws Exception {
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
                    break;
                case RIGHT:
                    this.Player.MoveRight();
                    break;
                case SPACE:
                    this.Shoot(this.Player);
                    break;
            }
        });

        primaryStage.setScene((mainScene));
        primaryStage.show();

//        primaryStage.setScene(mainScene);
//        primaryStage.show();
        //GameController.StartGame();
    }

    private Pane Init(Pane root) {
        root.setPrefSize(AppConstants.GamePanePreferredWidth, AppConstants.GamePanePreferredHeight);
        ConfigObject playerConfig = new ConfigObject(
                AppConstants.PlayerShipXCoordinate,
                AppConstants.PlayerShipYCoordinate,
                AppConstants.PlayerShipHeight,
                AppConstants.PlayerShipWidth,
                "Player",
                Color.BLUE);
        Player = new SpaceShip(playerConfig);
        root.getChildren().add(Player);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                UpdateScene();
            }
        };

        timer.start();

        root.getChildren().addAll(CreateEnemies(5));

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
        }

        SpaceShip[] a = new SpaceShip[ships.size()];
        return ships.toArray(a);
    }

    private List<GameObject> GetGameObjects()
    {
        return this.GamePane.getChildren().stream().map(o-> (GameObject)o).collect(Collectors.toList());
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
    }

    private void UpdateScene()
    {
        for(GameObject gameObject : this.GetGameObjects())
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
                            }
                        });
            }

            if (gameObject.Type.equals("EnemyTorpedo"))
            {
                gameObject.MoveDown();

            }
        }

        //Remove Dead Objects
        List<GameObject> deadObjects = GamePane
                .getChildren()
                .stream()
                .map(obj-> (GameObject)obj)
                .filter(obj-> obj.IsDead)
                .collect(Collectors.toList());
        GamePane.getChildren().removeAll(deadObjects);

        //Remove missed torpedos
//        List<Node> missedTorpedos=GamePane
//                .getChildren()
//                .stream()
//                .filter(obj-> obj.getTranslateY()-AppConstants.GamePanePreferredHeight<=5)
//                .collect(Collectors.toList());
//        GamePane.getChildren().removeAll(missedTorpedos);


    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
