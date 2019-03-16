
public class GameObject
{
    private GameObjectType _type;
    private double _xCoordinate;
    private double _yCoordinate;
    private boolean _isDead;

    protected GameObject(double x, double y, GameObjectType type)
    {
        if (x < 0 || x > AppConstants.MaxGamePaneWidth)
        {
            throw new IllegalArgumentException("Invalid X value.");
        }

        if (y < 0 || y > AppConstants.MaxGamePaneHeight)
        {
            throw new IllegalArgumentException("Invalid Y value.");
        }

        this._type=type;
        this._xCoordinate = x;
        this._yCoordinate = y;
        this._isDead = false;
    }

    public double GetX()
    {
        return this._xCoordinate;
    }

    public double GetY()
    {
        return this._yCoordinate;
    }

    public boolean GetIsDead()
    {
        return  this._isDead;
    }

    public GameObjectType GetType()
    {
        return this._type;
    }

    protected boolean TrySetX(double x)
    {
        if(x<0 || x>AppConstants.MaxGamePaneWidth)
        {
            return false;
        }
        this._xCoordinate=x;
        return true;
    }

    protected boolean TrySetY(double y)
    {
        if(y<0 || y>AppConstants.MaxGamePaneWidth)
        {
            return false;
        }
        this._yCoordinate=y;
        return true;
    }

    protected void SetToDead()
    {
        this._isDead=true;
    }
}