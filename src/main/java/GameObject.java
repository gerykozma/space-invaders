import java.lang.reflect.Array;
import java.util.ArrayList;

public class GameObject {
    private GameObjectType _type;
    private double _xCoordinate;
    private double _yCoordinate;
    private boolean _isDead;
    private int _height;
    private int _width;

    /**
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param type
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

    protected boolean TrySetX(double x) {
        if (x <= 0 || x + AppConstants.PlayerShipWidth > AppConstants.MaxGamePaneWidth) {
            return false;
        }
        this._xCoordinate = x;
        return true;
    }

    protected boolean TrySetY(double y) {
        if (y <= 0 || y > AppConstants.MaxGamePaneHeight) {
            return false;
        }
        this._yCoordinate = y;
        return true;
    }

//    protected boolean IntersectLowerBounds(GameObject other)
//    {
//        if((this.GetX() < other.GetX() && other.GetX() < this.GetX() + this.GetWidth()
//        ||  this.GetX() < other.GetX() + other.GetWidth() && other.GetX() + other.GetWidth() < this.GetX() + this.GetWidth()))
//        {
//            if(this.GetY() + this.GetHeight() >= other.GetY() && other.GetY() + other.GetHeight() <= this.GetY())
//            {
//                return true;
//            }
//        }
//        return  false;
//    }

//    protected boolean IntersectUpperBounds(GameObject other)
//    {
//        if((this.GetX() < other.GetX() && other.GetX() < this.GetX() + this.GetWidth()
//                ||  this.GetX() < other.GetX() + other.GetWidth() && other.GetX() + other.GetWidth() < this.GetX() + this.GetWidth()))
//        {
//            if(other.GetY() + other.GetHeight() >= this.GetY() && other.GetY() < this.GetY() + this.GetHeight())
//            {
//                return true;
//            }
//        }
//        return  false;
//    }

    protected boolean Intersect(GameObject other)
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

    protected void SetToDead()
    {
        this._isDead=true;
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