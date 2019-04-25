package controller;

import model.AppConstants;
import model.GameObject;
import model.GameObjectFactory;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Factory to obtain UI objects: player ship, enemy ships, torpedoes.
 */
final class ObservableGameObjectFactory {

    private static final Logger EVENT_LOGGER = Logger.getLogger(ObservableGameObjectFactory.class);

    /**
     * Creates a UI compatible player ship.
     *
     * @return a new player spaceship object.
     */
    static SpaceShip createPlayerShip() {
        return new SpaceShip(GameObjectFactory.createPlayerObject());
    }

    /**
     * Creates the given number of UI compatible enemy ships.
     *
     * @param numberOfEnemiesToCreate number of enemy ships to create.
     * @return the specified number of new enemy spaceships.
     */
    static SpaceShip[] createEnemyShips(final int numberOfEnemiesToCreate) {
        ArrayList<SpaceShip> ships = new ArrayList<>();

        for (int i = 0; i < numberOfEnemiesToCreate; i++) {
            GameObject gameObject = GameObjectFactory.createEnemyObject();
            gameObject.trySetX(gameObject.getX() + i * AppConstants.ENEMY_SHIP_POSITION_OFFSET);
            gameObject.trySetY(gameObject.getY() + i * AppConstants.ENEMY_SHIP_POSITION_OFFSET);
            ships.add(new SpaceShip(gameObject));
            EVENT_LOGGER.debug("Enemy ship created.");
        }

        SpaceShip[] a = new SpaceShip[ships.size()];
        return ships.toArray(a);
    }

    /**
     * Creates a UI compatible torpedo based on the shooter's type.
     *
     * @param shooter parent of the torpedo.
     * @return creates a new torpedo object.
     */
    static Torpedo createTorpedo(final SpaceShip shooter) {
        if (shooter == null) {
            throw new IllegalArgumentException("Shooter must be specified to create Torpedo.");
        }

        switch (shooter.getGameObject().getType()) {
            case EnemyShip:
                return new Torpedo(GameObjectFactory.createEnemyTorpedoObject(
                        shooter.getGameObject().getX(),
                        shooter.getGameObject().getY()));
            case PlayerShip:
                return new Torpedo(GameObjectFactory.createPlayerTorpedoObject(
                        shooter.getGameObject().getX(),
                        shooter.getGameObject().getY()));
            default:
                return null;
        }
    }
}
