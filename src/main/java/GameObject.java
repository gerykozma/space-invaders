
import javafx.scene.shape.Rectangle;

public abstract class GameObject extends Rectangle
{
    public final String Type;
    public  boolean IsDead;

    protected GameObject(ConfigObject config)
    {
        super(config.Width, config.Height, config.Color);
        this.Type=config.Type;
        this.IsDead=false;

        this.setTranslateX(config.XCoordinate);
        this.setTranslateY(config.YCoordinate);
    }

    public void MoveLeft()
    {

        this.setTranslateX(this.getTranslateX() - 5);

    }

    public void MoveRight()
    {

        this.setTranslateX(this.getTranslateX() + 5);

    }

    public void MoveUp()
    {
        if(this.getTranslateY() - 5 > 0)
        {
            this.setTranslateY(this.getTranslateY() - 5);
        }
    }

    public void MoveDown()
    {
        if(this.getTranslateY() + 5 < AppConstants.GamePanePreferredHeight)
        {
            this.setTranslateY(this.getTranslateY() + 5);
        }
    }

    public void SetDeath()
    {
        this.IsDead  = true;
    }
}
