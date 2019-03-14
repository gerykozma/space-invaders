package main.java.sample;


import javafx.scene.paint.Color;

public class ConfigObject {
    public double XCoordinate;
    public double YCoordinate;
    public int Height;
    public int Width;
    public String Type;
    public javafx.scene.paint.Color Color;

    public ConfigObject(double xCoordinate, double yCoordinate, int height, int width, String type, Color color)
    {
        this.XCoordinate=xCoordinate;
        this.YCoordinate=yCoordinate;
        this.Height=height;
        this.Width=width;
        this.Type=type;
        this.Color=color;
    }
}
