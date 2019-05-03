package model;

/**
 * Factory to obtain GameObjects.
 */
public final class GameObjectFactory {

    /**
     * Creates a new player object.
     * @return a new player object.
     * */
    public static GameObject createPlayerObject() {
        return new GameObject(
                AppConstants.PLAYER_SHIP_X_COORDINATE,
                AppConstants.PLAYER_SHIP_Y_COORDINATE,
                AppConstants.PLAYER_SHIP_WIDTH,
                AppConstants.PLAYER_SHIP_HEIGHT,
                GameObjectType.PlayerShip);
    }

    /**
     * Creates a new enemy object.
     * @return a new enemy object.
     * */
    public static GameObject createEnemyObject() {
        return new GameObject(
                AppConstants.ENEMY_SHIP_X_COORDINATE,
                AppConstants.ENEMY_SHIP_Y_COORDINATE,
                AppConstants.ENEMY_SHIP_WIDTH,
                AppConstants.ENEMY_SHIP_HEIGHT,
                GameObjectType.EnemyShip);
    }

    /**
     * Create a new torpedo object belonging to the player.
     * @param parentXCoordinate X coordinate of the parent object.
     * @param parentYCoordinate Y coordinate of the parent object.
     * @return a new torpedo object.
     * */
    public static GameObject createPlayerTorpedoObject(final double parentXCoordinate, final double parentYCoordinate) {
        return innerCreateTorpedoObject(parentXCoordinate, parentYCoordinate, GameObjectType.PlayerTorpedo);
    }

    /**
     * Create a new torpedo object belonging to an enemy.
     * @param parentXCoordinate X coordinate of the parent object.
     * @param parentYCoordinate Y coordinate of the parent object.
     * @return a new torpedo object.
     * */
    public static GameObject createEnemyTorpedoObject(final double parentXCoordinate, final double parentYCoordinate) {
        return innerCreateTorpedoObject(parentXCoordinate, parentYCoordinate, GameObjectType.EnemyTorpedo);
    }

    private static GameObject innerCreateTorpedoObject(
            final double parentXCoordinate,
            final double parentYCoordinate,
            final GameObjectType type) {
        return new GameObject(
                parentXCoordinate + AppConstants.TORPEDO_X_OFFSET,
                parentYCoordinate,
                AppConstants.TORPEDO_WIDTH,
                AppConstants.TORPEDO_HEIGHT,
                type);
    }
}
