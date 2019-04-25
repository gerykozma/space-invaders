package model;

import java.util.ArrayList;

/**
 * It is the underlying type of every UI element on the GamePane.
 */
public class GameObject {
    private GameObjectType type;
    private double xCoordinate;
    private double yCoordinate;
    private boolean isDead;
    private int height;
    private int width;

    /**
     * Creates a new GameObject. It is the underlying type of every UI element on the GamePane.
     *
     * @param x      x coordinate of the object.
     * @param y      y coordinate of the object.
     * @param width  width of the object.
     * @param height height of the object.
     * @param type   type of the object.
     */
    protected GameObject(
            final double x,
            final double y,
            final int width,
            final int height,
            final GameObjectType type) {

        if (x < 0 || x > AppConstants.MAX_GAME_PANE_WIDTH) {
            throw new IllegalArgumentException("Invalid X value.");
        }

        if (y < 0 || y > AppConstants.MAX_GAME_PANE_HEIGHT) {
            throw new IllegalArgumentException("Invalid Y value.");
        }

        if (width <= 0) {
            throw new IllegalArgumentException("Invalid width value.");
        }

        if (height <= 0) {
            throw new IllegalArgumentException("Invalid height value.");
        }

        this.type = type;
        this.xCoordinate = x;
        this.yCoordinate = y;
        this.width = width;
        this.height = height;
        this.isDead = false;
    }

    public final double getX() {
        return this.xCoordinate;
    }

    public final double getY() {
        return this.yCoordinate;
    }

    public final boolean getIsDead() {
        return this.isDead;
    }

    public GameObjectType getType() {
        return this.type;
    }

    public final int getWidth() {
        return this.width;
    }

    public final int getHeight() {
        return this.height;
    }

    /**
     * Validates the argument and sets it as the new X coordinate if it passes.
     *
     * @param x the new value on X axis.
     * @return true if the value was set.
     */
    public final boolean trySetX(final double x) {
        if (x <= 0 || x + AppConstants.PLAYER_SHIP_WIDTH > AppConstants.MAX_GAME_PANE_WIDTH) {
            return false;
        }
        this.xCoordinate = x;
        return true;
    }

    /**
     * Validates the argument and sets it as the new Y coordinate if it passes.
     *
     * @param y the new value on Y axis.
     * @return true if the value was set.
     */
    public final boolean trySetY(final double y) {
        if (y <= 0 || y > AppConstants.MAX_GAME_PANE_HEIGHT) {
            return false;
        }
        this.yCoordinate = y;
        return true;
    }

    /**
     * Realises collision detection. Represents an accurate way to determine if the objects are overlapping.
     *
     * @param other Other GameObject to check collision.
     * @return true, if the two objects are colliding. False otherwise.
     */
    public final boolean intersect(final GameObject other) {
        ArrayList<Integer> currentObjectXVector = generateVector((int) this.xCoordinate, this.width);
        ArrayList<Integer> otherObjectXVector = generateVector((int) other.getX(), other.getWidth());
        for (int x : currentObjectXVector) {
            if (otherObjectXVector.contains(x)) {
                ArrayList<Integer> currentObjectYVector = generateVector((int) this.yCoordinate, this.height);
                ArrayList<Integer> otherObjectYVector = generateVector((int) other.getY(), other.getHeight());

                for (int y : currentObjectYVector) {
                    if (otherObjectYVector.contains(y)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Sets object status to 'dead'.
     */
    public final void setToDead() {
        this.isDead = true;
    }

    private static ArrayList<Integer> generateVector(final int startIndex, final int offset) {
        ArrayList<Integer> vector = new ArrayList<>();
        for (int i = startIndex; i <= startIndex + offset; i++) {
            vector.add(i);
        }
        return vector;
    }
}
