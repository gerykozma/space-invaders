import java.util.List;

public final class EnemyAI
{

    private int _timer;

    protected EnemyAI()
    {
        this._timer=0;
    }

    protected void Update(List<ObservableGameObject> enemyShipsToUpdate)
    {
        this.ShootEnemyTorpedos()
    }
}
