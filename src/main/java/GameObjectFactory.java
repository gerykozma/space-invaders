import com.sun.javafx.iio.ImageLoader;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public final class GameObjectFactory {
    private final static Logger EventLogger = Logger.getLogger(GameObjectFactory.class);

    public static final SpaceShip CreatePlayerShip()
    {
        return new SpaceShip(new GameObject(
                AppConstants.PlayerShipXCoordinate,
                AppConstants.PlayerShipYCoordinate,
                AppConstants.PlayerShipWidth,
                AppConstants.PlayerShipHeight,
                GameObjectType.PlayerShip));
    }

    public static final SpaceShip[] CreateEnemyShips(int numberOfEnemiesToCreate) {
        ArrayList<SpaceShip> ships = new ArrayList<>();
        for (int i = 0; i < numberOfEnemiesToCreate; i++) {
            GameObject gameObject = new GameObject(
                    100 + i * 50,
                    AppConstants.EnemyShipYCoordinate + i * 50,
                    AppConstants.EnemyShipWidth,
                    AppConstants.EnemyShipHeight,
                    GameObjectType.EnemyShip);
            ships.add(new SpaceShip(gameObject));
            EventLogger.debug("Enemy ship created.");
        }

        SpaceShip[] a = new SpaceShip[ships.size()];
        return ships.toArray(a);
    }

    public static final Torpedo CreateTorpedo(SpaceShip shooter)
    {
        if (shooter == null)
        {
            throw new IllegalArgumentException("Shooter must be specified to create Torpedo.");
        }

        GameObjectType torpedoType;
        switch (shooter.GetGameObject().GetType())
        {
            case EnemyShip:
                torpedoType=GameObjectType.EnemyTorpedo;
                break;
            case PlayerShip:
                torpedoType=GameObjectType.PlayerTorpedo;
                break;
                default:
                    return null;
        }

        GameObject gameObject = new GameObject(
                shooter.GetGameObject().GetX()+20,
                shooter.GetGameObject().GetY()-10,
                5,
                15,
                torpedoType);

        return new Torpedo(gameObject);
    }
}
