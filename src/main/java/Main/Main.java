package Main;

import Model.GameLevel;
import Controller.GameController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        GameController game = new GameController(primaryStage);
        game.StartGame(GameLevel.GetInitialLevel());
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
