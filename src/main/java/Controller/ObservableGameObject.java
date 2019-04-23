package Controller;

import Model.AppConstants;
import Model.GameObject;
import Model.GameObjectType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents the base class for UI objects on the GamePane
 */
public abstract class ObservableGameObject extends ImageView {
    private GameObject _gameObject;

    protected ObservableGameObject(GameObject gameObject) {
        super(ObjectTypeToImage(gameObject.GetType()));

        this._gameObject = gameObject;
        this.setTranslateX(this._gameObject.GetX());
        this.setTranslateY(this._gameObject.GetY());
    }

    /**
     * Tries to move the object to the left by 5 pixels.
     *
     * @return true if the object was moved.
     */
    public boolean TryMoveLeft() {
        if (this._gameObject.TrySetX(this._gameObject.GetX() - AppConstants.MoveOffset)) {
            this.setTranslateX(this._gameObject.GetX());
            return true;
        }
        return false;
    }

    /**
     * Tries to move the object to the right by 5 pixels.
     *
     * @return true if the object was moved.
     */
    public boolean TryMoveRight() {
        if (this._gameObject.TrySetX(this._gameObject.GetX() + AppConstants.MoveOffset)) {
            this.setTranslateX(this._gameObject.GetX());
            return true;
        }
        return false;
    }

    /**
     * Tries to move the object up by 5 pixels.
     *
     * @return true if the object was moved.
     */
    public boolean TryMoveUp() {
        if (this._gameObject.TrySetY(this._gameObject.GetY() - AppConstants.MoveOffset)) {
            this.setTranslateY(this._gameObject.GetY());
            return true;
        }
        return false;
    }

    /**
     * Tries to move the object down by 5 pixels.
     *
     * @return true if the object was moved.
     */
    public boolean TryMoveDown() {
        if (this._gameObject.TrySetY(this._gameObject.GetY() + AppConstants.MoveOffset)) {
            this.setTranslateY(this._gameObject.GetY());
            return true;
        }
        return false;
    }

    /**
     * Sets the object's state to 'dead' - this will trigger a removal from the UI.
     */
    public void SetDeath() {
        this._gameObject.SetToDead();
    }

    /**
     * Retrieves the underlying object.
     */
    public GameObject GetGameObject() {
        return this._gameObject;
    }

    private static final Image ObjectTypeToImage(GameObjectType type) {
        switch (type) {
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
