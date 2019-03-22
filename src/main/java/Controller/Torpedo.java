package Controller;

import Model.GameObject;

/**
 * Class that represents a Torpedo object on the UI
 * */
public class Torpedo extends ObservableGameObject
{
    protected Torpedo(GameObject gameObject)
    {
        super(gameObject);
    }

    @Override
    public boolean TryMoveRight(){ return false; }

    @Override
    public boolean TryMoveLeft(){ return false; }

    /**
     * Try to move the Torpedo object UP on the Y axis
     * @return true if the object was moved
     * */
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
            //Override was the easiest way to implement dead torpedo removal:
            //when a torpedo reached the top of the GamePane aka didn't hit
            // anything and it's unable to move, set it's status to 'dead'.
            this.SetDeath();
            return false;
        }
    }

    /**
     * Try to move the Torpedo object DOWN on the Y axis
     * @return true if the object was moved
     * */
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
            //Override was the easiest way to implement dead torpedo removal:
            //when a torpedo reached the bottom of the GamePane aka didn't hit
            // anything and it's unable to move, set it's status to 'dead'.
            this.SetDeath();
            return false;
        }
    }
}
