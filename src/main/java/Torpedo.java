
public class Torpedo extends GameObject
{
    protected Torpedo(ConfigObject config)
    {
        super(config);
    }

    @Override
    public void MoveRight(){}

    @Override
    public void MoveLeft(){ }

    @Override
    public void MoveUp()
    {
        if(this.getTranslateY() - 5 > 0)
        {
            this.setTranslateY(this.getTranslateY() - 5);
        }
        else
        {
            this.IsDead=true;
        }
    }

    @Override
    public void MoveDown()
    {
        if(this.getTranslateY() + 5 < AppConstants.GamePanePreferredHeight)
        {
            this.setTranslateY(this.getTranslateY() + 5);
        }
        else
        {
            this.IsDead=true;
        }
    }
}
