package Main;

import Controller.gameController;
import Model.GameLevel;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        gameController game = new gameController(primaryStage);
        game.StartGame(GameLevel.GetInitialLevel());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
