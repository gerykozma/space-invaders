
public class Torpedo extends ObservableGameObject
{
    protected Torpedo(GameObject gameObject)
    {
        super(gameObject);
    }

    @Override
    public boolean TryMoveRight(){return false;}

    @Override
    public boolean TryMoveLeft(){ return false;}

    @Override
    public boolean TryMoveUp()
    {
        if(this.GetGameObject().TrySetY(this.GetGameObject().GetY() - 5 ))
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
        if(this.GetGameObject().TrySetY(this.GetGameObject().GetY() + 5))
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
