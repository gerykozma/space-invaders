
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        GameController game = new GameController(primaryStage);
        game.StartGame(new GameLevel(null, 0, 1));
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
