package controller;

import model.AppConstants;
import model.GameObject;
import model.GameObjectType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents the base class for UI objects on the game pane.
 */
public abstract class ObservableGameObject extends ImageView {
    private GameObject gameObject;

    ObservableGameObject(final GameObject gameObject) {
        super(objectTypeToImage(gameObject.getType()));

        this.gameObject = gameObject;
        this.setTranslateX(this.gameObject.getX());
        this.setTranslateY(this.gameObject.getY());
    }

    /**
     * Tries to move the object to the left by 5 pixels.
     *
     * @return true if the object was moved.
     */
    public boolean tryMoveLeft() {
        if (this.gameObject.trySetX(this.gameObject.getX() - AppConstants.MOVE_OFFSET)) {
            this.setTranslateX(this.gameObject.getX());
            return true;
        }
        return false;
    }

    /**
     * Tries to move the object to the right by 5 pixels.
     *
     * @return true if the object was moved.
     */
    public boolean tryMoveRight() {
        if (this.gameObject.trySetX(this.gameObject.getX() + AppConstants.MOVE_OFFSET)) {
            this.setTranslateX(this.gameObject.getX());
            return true;
        }
        return false;
    }

    /**
     * Tries to move the object up by 5 pixels.
     *
     * @return true if the object was moved.
     */
    public boolean tryMoveUp() {
        if (this.gameObject.trySetY(this.gameObject.getY() - AppConstants.MOVE_OFFSET)) {
            this.setTranslateY(this.gameObject.getY());
            return true;
        }
        return false;
    }

    /**
     * Tries to move the object down by 5 pixels.
     *
     * @return true if the object was moved.
     */
    public boolean tryMoveDown() {
        if (this.gameObject.trySetY(this.gameObject.getY() + AppConstants.MOVE_OFFSET)) {
            this.setTranslateY(this.gameObject.getY());
            return true;
        }
        return false;
    }

    /**
     * Sets the object's state to 'dead' - this will trigger a removal from the UI.
     */
    public void setDeath() {
        this.gameObject.setToDead();
    }

    /**
     * Retrieves the underlying object.
     * @return the underlying game object associated with the observable.
     */
    public GameObject getGameObject() {
        return this.gameObject;
    }

    private static final Image objectTypeToImage(final GameObjectType type) {
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
