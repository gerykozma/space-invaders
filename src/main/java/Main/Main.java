package main;

import controller.GameController;
import model.GameLevel;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public final void start(final Stage primaryStage) throws Exception {
        GameController game = new GameController(primaryStage);
        game.StartGame(GameLevel.getInitialLevel());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
