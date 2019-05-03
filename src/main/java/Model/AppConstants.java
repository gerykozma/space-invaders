package model;

/**
 * Class containing constants that are used across the project.
 */
public final class AppConstants {

    /**
     * Maximum height of the UI game pane.
     * */
    public static final int MAX_GAME_PANE_HEIGHT = 700;

    /**
     * Maximum width of the UI game pane.
     * */
    public static final int MAX_GAME_PANE_WIDTH = 600;

    /**
     * Enemy ship base Y coordinate on the game pane.
     * */
    public static final int ENEMY_SHIP_Y_COORDINATE = 50;

    /**
     * Enemy ship base X coordinate on the game pane.
     * */
    public static final int ENEMY_SHIP_X_COORDINATE = 100;

    /**
     * Player ship base Y coordinate on the game pane.
     * */
    public static final int PLAYER_SHIP_Y_COORDINATE = MAX_GAME_PANE_HEIGHT - 100;

    /**
     * Player ship base X coordinate on the game pane.
     * */
    public static final int PLAYER_SHIP_X_COORDINATE = MAX_GAME_PANE_WIDTH / 2;

    /**
     * Width of the player ship.
     * */
    public static final int PLAYER_SHIP_WIDTH = 30;

    /**
     * Height of the player ship.
     * */
    public static final int PLAYER_SHIP_HEIGHT = 30;

    /**
     * Width of the enemy ship.
     * */
    public static final int ENEMY_SHIP_WIDTH = 30;

    /**
     * Height of the enemy ship.
     * */
    public static final int ENEMY_SHIP_HEIGHT = 30;

    /**
     * Base score acquired by the player for destroying an enemy ship.
     * */
    public static final int DESTROYED_ENEMY_SHIP_BASE_SCORE = 100;

    /**
     * Maximum game level.
     * */
    public static final int MAX_LEVEL_NUMBER = 10;

    /**
     * Width of the torpedo.
     * */
    public static final int TORPEDO_WIDTH = 5;

    /**
     * Height of the torpedo.
     * */
    public static final int TORPEDO_HEIGHT = 15;

    /**
     * X offset of the torpedo relative to its parent.
     * */
    public static final int TORPEDO_X_OFFSET = 15;

    /**
     * Number of pixels per move.
     * */
    public static final int MOVE_OFFSET = 5;

    /**
     * Distance between enemy ships.
     * */
    public static final int ENEMY_SHIP_POSITION_OFFSET = 50;
}
