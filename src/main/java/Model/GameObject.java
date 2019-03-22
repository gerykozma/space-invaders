package Model;

import java.util.ArrayList;

/**
 * It is the underlying type of every UI element on the GamePane.
 * */
public class GameObject {
    private GameObjectType _type;
    private double _xCoordinate;
    private double _yCoordinate;
    private boolean _isDead;
    private int _height;
    private int _width;

    /**
     *Creates a new GameObject. It is the underlying type of every UI element on the GamePane.
     * @param x x coordinate of the object.
     * @param y y coordinate of the object.
     * @param width width of the object.
     * @param height height of the object.
     * @param type type of the object.
     */
    protected GameObject(double x, double y, int width, int height, GameObjectType type) {
        if (x < 0 || x > AppConstants.MaxGamePaneWidth) {
            throw new IllegalArgumentException("Invalid X value.");
        }

        if (y < 0 || y > AppConstants.MaxGamePaneHeight) {
            throw new IllegalArgumentException("Invalid Y value.");
        }

        if (width <= 0) {
            throw new IllegalArgumentException("Invalid width value.");
        }

        if (height <= 0) {
            throw new IllegalArgumentException("Invalid height value.");
        }

        this._type = type;
        this._xCoordinate = x;
        this._yCoordinate = y;
        this._width = width;
        this._height = height;
        this._isDead = false;
    }

    public double GetX() {
        return this._xCoordinate;
    }

    public double GetY() {
        return this._yCoordinate;
    }

    public boolean GetIsDead() {
        return this._isDead;
    }

    public GameObjectType GetType() {
        return this._type;
    }

    public int GetWidth()
    {
        return this._width;
    }

    public int GetHeight()
    {
        return this._height;
    }

    /**
     * Validates the argument and sets it as the new X coordinate if it passes.
     * @param x the new value on X axis.
     * @return true if the value was set.
     * */
    public boolean TrySetX(double x) {
        if (x <= 0 || x + AppConstants.PlayerShipWidth > AppConstants.MaxGamePaneWidth) {
            return false;
        }
        this._xCoordinate = x;
        return true;
    }

    /**
     * Validates the argument and sets it as the new Y coordinate if it passes.
     * @param y the new value on Y axis.
     * @return true if the value was set.
     * */
    public boolean TrySetY(double y) {
        if (y <= 0 || y > AppConstants.MaxGamePaneHeight) {
            return false;
        }
        this._yCoordinate = y;
        return true;
    }

    /**
     * Realises collision detection. Represents an accurate way to determine if the objects are overlapping.
     * @param other Other GameObject to check collision.
     * */
    public boolean Intersect(GameObject other)
    {
        ArrayList<Integer> currentObjectXVector = GenerateVector((int)this._xCoordinate, this._width);
        ArrayList<Integer> otherObjectXVector = GenerateVector((int)other.GetX(), other.GetWidth());
        for(int x : currentObjectXVector)
        {
            if(otherObjectXVector.contains(x))
            {
                ArrayList<Integer> currentObjectYVector = GenerateVector((int)this._yCoordinate, this._height);
                ArrayList<Integer> otherObjectYVector = GenerateVector((int)other.GetY(), other.GetHeight());

                for (int y : currentObjectYVector)
                {
                    if(otherObjectYVector.contains(y))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Sets object status to 'dead'.
     * */
    public void SetToDead()
    {
        this._isDead = true;
    }

    private static ArrayList<Integer> GenerateVector(int startIndex, int offset)
    {
        ArrayList<Integer> vector=new ArrayList<>();
        for (int i = startIndex; i <= startIndex + offset; i++)
        {
            vector.add(i);
        }
        return vector;
    }
}