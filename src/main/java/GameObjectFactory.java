import javafx.scene.paint.Color;
import org.apache.log4j.Logger;

import java.util.ArrayList;

public final class GameObjectFactory {
    private final static Logger EventLogger = Logger.getLogger(GameObjectFactory.class);

    public static final SpaceShip CreatePlayerShip() {
        ConfigObject playerConfig = new ConfigObject(
                AppConstants.PlayerShipXCoordinate,
                AppConstants.PlayerShipYCoordinate,
                AppConstants.PlayerShipHeight,
                AppConstants.PlayerShipWidth,
                GameObjectType.PlayerShip,
                Color.BLUE);
        return new SpaceShip(playerConfig);
    }

    public static final SpaceShip[] CreateEnemyShips(int numberOfEnemiesToCreate) {
        ArrayList<SpaceShip> ships = new ArrayList<>();
        for (int i = 0; i < numberOfEnemiesToCreate; i++) {
            ConfigObject config = new ConfigObject(
                    100 + i * 50,
                    AppConstants.EnemyShipYCoordinate + i * 50,
                    AppConstants.EnemyShipHeight,
                    AppConstants.EnemyShipWidth,
                    GameObjectType.EnemyShip,
                    Color.RED);
            ships.add(new SpaceShip(config));
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
                torpedoType=GameObjectType.PlayerTorped;
                break;
                default:
                    return null;
        }

        ConfigObject torpedoConfig = new ConfigObject(
                shooter.GetGameObject().GetX()+20,
                shooter.GetGameObject().GetY()-10,
                15,
                5,
                torpedoType,
                Color.BLACK);
        return new Torpedo(torpedoConfig);
    }
}
