package Controller;

import Model.GameObject;
import Model.GameObjectFactory;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Factory to obtain UI objects: player ship, enemy ships, torpedoes.
 */
public final class ObservableGameObjectFactory {
    private final static Logger EventLogger = Logger.getLogger(ObservableGameObjectFactory.class);

    /**
     * Creates a UI compatible player ship.
     */
    public static final SpaceShip CreatePlayerShip() {
        return new SpaceShip(GameObjectFactory.CreatePlayerObject());
    }

    /**
     * Creates the given number of UI compatible enemy ships.
     *
     * @param numberOfEnemiesToCreate number of enemy ships to create.
     */
    public static final SpaceShip[] CreateEnemyShips(int numberOfEnemiesToCreate) {
        ArrayList<SpaceShip> ships = new ArrayList<>();

        for (int i = 0; i < numberOfEnemiesToCreate; i++) {
            GameObject gameObject = GameObjectFactory.CreateEnemyObject();
            gameObject.TrySetX(gameObject.GetX() + i * 50);
            gameObject.TrySetY(gameObject.GetY() + i * 50);
            ships.add(new SpaceShip(gameObject));
            EventLogger.debug("Enemy ship created.");
        }

        SpaceShip[] a = new SpaceShip[ships.size()];
        return ships.toArray(a);
    }

    /**
     * Creates a UI compatible torpedo based on the shooter's type.
     *
     * @param shooter parent of the torpedo.
     */
    public static final Torpedo CreateTorpedo(SpaceShip shooter) {
        if (shooter == null) {
            throw new IllegalArgumentException("Shooter must be specified to create Torpedo.");
        }

        switch (shooter.GetGameObject().GetType()) {
            case EnemyShip:
                return new Torpedo(GameObjectFactory.CreateEnemyTorpedoObject(
                        shooter.GetGameObject().GetX(),
                        shooter.GetGameObject().GetY()));
            case PlayerShip:
                return new Torpedo(GameObjectFactory.CreatePlayerTorpedoObject(
                        shooter.GetGameObject().GetX(),
                        shooter.GetGameObject().GetY()));
            default:
                return null;
        }
    }
}
