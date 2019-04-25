package controller;

import model.AppConstants;
import model.GameObject;

/**
 * Class that represents a Torpedo object on the UI.
 */
public class Torpedo extends ObservableGameObject {
    protected Torpedo(final GameObject gameObject) {
        super(gameObject);
    }

    @Override
    /**
     * A torpedo object cannot move right.
     * @return always returns false.
     * */
    public final boolean tryMoveRight() {
        return false;
    }

    @Override
    /**
     * A torpedo object cannot move left.
     * @return always returns false.
     * */
    public final boolean tryMoveLeft() {
        return false;
    }

    /**
     * Try to move the Torpedo object UP on the Y axis.
     *
     * @return true if the object was moved.
     */
    @Override
    public final boolean tryMoveUp() {
        if (this.getGameObject()
                .trySetY(this.getGameObject().getY() - AppConstants.MOVE_OFFSET)) {
            this.setTranslateY(this.getTranslateY() - AppConstants.MOVE_OFFSET);
            return true;
        } else {
            //Override was the easiest way to implement dead torpedo removal:
            //when a torpedo reached the top of the GamePane aka didn't hit
            // anything and it's unable to move, set it's status to 'dead'.
            this.setDeath();
            return false;
        }
    }

    /**
     * Try to move the Torpedo object DOWN on the Y axis.
     *
     * @return true if the object was moved.
     */
    @Override
    public final boolean tryMoveDown() {
        if (this.getGameObject()
                .trySetY(this.getGameObject().getY() + AppConstants.MOVE_OFFSET)) {
            this.setTranslateY(this.getTranslateY() + AppConstants.MOVE_OFFSET);
            return true;
        } else {
            //Override was the easiest way to implement dead torpedo removal:
            //when a torpedo reached the bottom of the GamePane aka didn't hit
            // anything and it's unable to move, set it's status to 'dead'.
            this.setDeath();
            return false;
        }
    }
}
