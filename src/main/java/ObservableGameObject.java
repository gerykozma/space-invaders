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

    public void MoveLeft()
    {
        if(this._gameObject.TrySetX(this._gameObject.GetX() - 5))
        {
            this.setTranslateX(this._gameObject.GetX());
        }
    }

    public void MoveRight()
    {
        if(this._gameObject.TrySetX(this._gameObject.GetX() +5))
        {
            this.setTranslateX(this._gameObject.GetX());
        }
    }

    public void MoveUp()
    {
        if(this._gameObject.TrySetY(this._gameObject.GetY() - 5 ))
        {
            this.setTranslateY(this._gameObject.GetY());
        }
    }

    public void MoveDown()
    {
        if(this._gameObject.TrySetY(this._gameObject.GetY() + 5))
        {
            this.setTranslateY(this._gameObject.GetY());
        }
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
