public final class GameObjectFactory
{
    protected static GameObject CreatePlayerObject()
    {
        return new GameObject(
                AppConstants.PlayerShipXCoordinate,
                AppConstants.PlayerShipYCoordinate,
                AppConstants.PlayerShipWidth,
                AppConstants.PlayerShipHeight,
                GameObjectType.PlayerShip);
    }

    protected static GameObject CreateEnemyObject()
    {
      return new GameObject(
              AppConstants.EnemyShipXCoordinate,
              AppConstants.EnemyShipYCoordinate ,
              AppConstants.EnemyShipWidth,
              AppConstants.EnemyShipHeight,
              GameObjectType.EnemyShip);
    }

    protected static GameObject CreatePlayerTorpedoObject(double parentXCoordinate, double parentYCoordinate)
    {
       return InnerCreateTorpedoObject(parentXCoordinate, parentYCoordinate, GameObjectType.PlayerTorpedo);
    }

    protected static GameObject CreateEnemyTorpedoObject(double parentXCoordinate, double parentYCoordinate)
    {
        return InnerCreateTorpedoObject(parentXCoordinate, parentYCoordinate, GameObjectType.EnemyTorpedo);
    }

    private static GameObject InnerCreateTorpedoObject(
            double parentXCoordinate,
            double parentYCoordinate,
            GameObjectType type)
    {
        return new GameObject(
            parentXCoordinate+20,
            parentYCoordinate-10,
            AppConstants.TorpedoWidth,
            AppConstants.TorpedoHeight,
            type);
    }
}
