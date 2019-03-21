import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public abstract class ObservableGameObject extends ImageView
{
    private GameObject _gameObject;

    protected ObservableGameObject(GameObject gameObject)
    {
        super(ObjectTypeToImage(gameObject.GetType()));

        this._gameObject = gameObject;
        this.setTranslateX(this._gameObject.GetX());
        this.setTranslateY(this._gameObject.GetY());
        this.setFitHeight(this._gameObject.GetHeight());
        this.setFitWidth(this._gameObject.GetWidth());
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

    private static final Image ObjectTypeToImage(GameObjectType type)
    {
        switch (type)
        {
            case PlayerShip:
                return new Image("PlayerShip.png");
            case EnemyShip:
                return new Image("AlienShip.png");
            case PlayerTorpedo:
                return new Image("PlayerRocket.png");
            case EnemyTorpedo:
                return new Image("AlienRocket.png");
                default:
                    return null;
        }
    }
}
