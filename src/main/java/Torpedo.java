
public class Torpedo extends ObservableGameObject
{
    protected Torpedo(ConfigObject config)
    {
        super(config);
    }

    @Override
    public boolean TryMoveRight(){return false;}

    @Override
    public boolean TryMoveLeft(){ return false;}

    @Override
    public boolean TryMoveUp()
    {
        if(this.getTranslateY() - 5 > 0)
        {
            this.setTranslateY(this.getTranslateY() - 5);
            return true;
        }
        else
        {
            this.SetDeath();
            return false;
        }
    }

    @Override
    public boolean TryMoveDown()
    {
        if(this.getTranslateY() + 5 < AppConstants.MaxGamePaneHeight)
        {
            this.setTranslateY(this.getTranslateY() + 5);
            return true;
        }
        else
        {
            this.SetDeath();
            return false;
        }
    }
}
