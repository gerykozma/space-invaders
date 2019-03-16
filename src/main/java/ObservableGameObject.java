import javafx.scene.shape.Rectangle;

public abstract class ObservableGameObject extends Rectangle
{
    private GameObject _gameObject;

    protected ObservableGameObject(ConfigObject config)
    {
        super(config.getWidth(), config.getHeight(), config.getColor());
        this._gameObject = new GameObject(config.getXCoordinate(), config.getYCoordinate(), config.getType());

        this.setTranslateX(this._gameObject.GetX());
        this.setTranslateY(this._gameObject.GetY());
    }

    public boolean TryMoveLeft()
    {
        if(this._gameObject.TrySetX(this._gameObject.GetX() - 5))
        {
            this.setTranslateX(this._gameObject.GetX());
            return true;
        }
        return false;
    }

    public boolean TryMoveRight()
    {
        if(this._gameObject.TrySetX(this._gameObject.GetX() + 5))
        {
            this.setTranslateX(this._gameObject.GetX());
            return true;
        }
        return false;
    }

    public boolean TryMoveUp()
    {
        if(this._gameObject.TrySetY(this._gameObject.GetY() - 5 ))
        {
            this.setTranslateY(this._gameObject.GetY());
            return true;
        }
        return false;
    }

    public boolean TryMoveDown()
    {
        if(this._gameObject.TrySetY(this._gameObject.GetY() + 5))
        {
            this.setTranslateY(this._gameObject.GetY());
            return true;
        }
        return false;
    }

    public void SetDeath()
    {
        this._gameObject.SetToDead();
    }

    public GameObject GetGameObject()
    {
        return this._gameObject;
    }
}
