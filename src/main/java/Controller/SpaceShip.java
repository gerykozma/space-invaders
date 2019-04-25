package controller;

import model.GameObject;

/**
 * Serves as the skeleton for player and enemy ships.
 */
public class SpaceShip extends ObservableGameObject {

    /**
     * Creates a new observable spaceship object.
     * @param gameObject underlying game object.
     * */
    public SpaceShip(final GameObject gameObject) {
        super(gameObject);
    }
}
