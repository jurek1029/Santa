package Objects;

import santa.v1.Engine;

/**
 * Created by Przemek on 20.12.2016.
 */

public class SpawnLocation {

    float x;
    float y;
    Present lastPresent;
    int direction;

    public SpawnLocation(float x,float y,int dir)
    {
        this.x=x;
        this.y=y;
        lastPresent=null;
        direction=dir;

    }


    public boolean canSpawn()
    {
        if (lastPresent==null) return true;
        if (direction>0)
            if (lastPresent.x-(x+Engine.presentMaxSize)>0)
            {
                lastPresent=null;
                return true;
            }

        if (direction<0)
            if (x-(lastPresent.x+lastPresent.width)>0)
            {
                lastPresent=null;
                return true;
            }

        return false;
    }

    public void setLastPresent(Present p)
    {
        lastPresent=p;
    }
}
