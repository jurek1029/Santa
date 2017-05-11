package Objects;

/**
 * Created by Przemek on 10.12.2016.
 */

import java.util.Random;
import java.util.Vector;

import santa.v1.Engine;
import santa.v1.SantaActivity;

public class Present extends Object {

    public float width;
    public float height;
    int type;
    float textureXpos;
    float textureYpos;
    float textureScale;

    public Vector<Integer> signs;
    public float startingSignsCount;
    public float rotationFull90;
    public int lastCollision;
    public float rotationAngle;
    public float Vy = 0;
    public int color;

    public Present(float x, float y, float size,int type,int textureH)
    {
        this.x = x;
        this.y = y;
        this.width = size;
        this.height = size*Engine.width/Engine.height;

        textureHandle = textureH;
        rotationAngle = 0;
        rotationFull90 = 0;
        lastCollision =0;
        
        Random rng = new Random();
        color = rng.nextInt(3);

        this.texture = new float[]{0f,.5f,.5f,.5f,.5f,1f,0f,1f};
        allocate();
        if(SantaActivity.supportsEs2)
            allocateGLES2();

        signs = Engine.ps.genSigns();        
        startingSignsCount = signs.size();
    }


}
