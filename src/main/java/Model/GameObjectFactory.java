package model;

/**
 * Factory to obtain GameObjects.
 */
public final class GameObjectFactory {
    public static GameObject createPlayerObject() {
        return new GameObject(
                AppConstants.PLAYER_SHIP_X_COORDINATE,
                AppConstants.PLAYER_SHIP_Y_COORDINATE,
                AppConstants.PLAYER_SHIP_WIDTH,
                AppConstants.PLAYER_SHIP_HEIGHT,
                GameObjectType.PlayerShip);
    }

    public static GameObject createEnemyObject() {
        return new GameObject(
                AppConstants.ENEMY_SHIP_X_COORDINATE,
                AppConstants.ENEMY_SHIP_Y_COORDINATE,
                AppConstants.ENEMY_SHIP_WIDTH,
                AppConstants.ENEMY_SHIP_HEIGHT,
                GameObjectType.EnemyShip);
    }

    public static GameObject createPlayerTorpedoObject(final double parentXCoordinate, final double parentYCoordinate) {
        return innerCreateTorpedoObject(parentXCoordinate, parentYCoordinate, GameObjectType.PlayerTorpedo);
    }

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
