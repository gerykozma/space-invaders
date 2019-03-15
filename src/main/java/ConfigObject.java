
import javafx.scene.paint.Color;

public class ConfigObject
{
    private double _xCoordinate;
    private double _yCoordinate;
    private int _height;
    private int _width;
    private GameObjectType _type;
    private javafx.scene.paint.Color _color;

    public ConfigObject(
            double xCoordinate,
            double yCoordinate,
            int height,
            int width,
            GameObjectType type,
            Color color)
    {
        this._xCoordinate =xCoordinate;
        this._yCoordinate =yCoordinate;
        this._height =height;
        this._width =width;
        this._type =type;
        this._color =color;
    }

    public double getXCoordinate() {
        return _xCoordinate;
    }

    public double getYCoordinate() {
        return _yCoordinate;
    }

    public int getHeight() {
        return _height;
    }

    public int getWidth() {
        return _width;
    }

    public GameObjectType getType() {
        return _type;
    }

    public javafx.scene.paint.Color getColor() {
        return _color;
    }
}