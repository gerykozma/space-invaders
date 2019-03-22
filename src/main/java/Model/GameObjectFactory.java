package Model;

/**
 * Factory to obtain GameObjects.
 * */
public final class GameObjectFactory
{
    public static GameObject CreatePlayerObject()
    {
        return new GameObject(
                AppConstants.PlayerShipXCoordinate,
                AppConstants.PlayerShipYCoordinate,
                AppConstants.PlayerShipWidth,
                AppConstants.PlayerShipHeight,
                GameObjectType.PlayerShip);
    }

    public static GameObject CreateEnemyObject()
    {
      return new GameObject(
              AppConstants.EnemyShipXCoordinate,
              AppConstants.EnemyShipYCoordinate ,
              AppConstants.EnemyShipWidth,
              AppConstants.EnemyShipHeight,
              GameObjectType.EnemyShip);
    }

    public static GameObject CreatePlayerTorpedoObject(double parentXCoordinate, double parentYCoordinate)
    {
       return InnerCreateTorpedoObject(parentXCoordinate, parentYCoordinate, GameObjectType.PlayerTorpedo);
    }

    public static GameObject CreateEnemyTorpedoObject(double parentXCoordinate, double parentYCoordinate)
    {
        return InnerCreateTorpedoObject(parentXCoordinate, parentYCoordinate, GameObjectType.EnemyTorpedo);
    }

    private static GameObject InnerCreateTorpedoObject(
            double parentXCoordinate,
            double parentYCoordinate,
            GameObjectType type)
    {
        return new GameObject(
            parentXCoordinate+15,
            parentYCoordinate,
            AppConstants.TorpedoWidth,
            AppConstants.TorpedoHeight,
            type);
    }
}
