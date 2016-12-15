package Objects;

/**
 * Created by Przemek on 10.12.2016.
 */

import java.util.Vector;

import santa.v1.Engine;
import santa.v1.SantaActivity;

public class Present extends Object {

    float width;
    float height;

    Vector<Integer> signs;
    float rotationAngle;

    public Present(float x, float y, float size,int type,int textureH)
    {
        this.x = x;
        this.y = y;
        this.width = size;
        this.height = size*Engine.width/Engine.height;

        textureHandle = textureH;
        rotationAngle = 0;

        this.texture = new float[]{0f,.5f,.5f,.5f,.5f,1f,0f,1f};
        allocate();
        if(SantaActivity.supportsEs2)
            allocateGLES2();

        signs = Engine.ps.genSigns();
    }


}
