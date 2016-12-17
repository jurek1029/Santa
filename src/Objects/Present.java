package Objects;

/**
 * Created by Przemek on 10.12.2016.
 */

import java.util.Vector;

import santa.v1.Engine;

public class Present extends Object {

    public float width;
    public float height;
    int type;
    float textureXpos;
    float textureYpos;
    float textureScale;
    Vector<Integer> signs;
    public float rotationFull90;
    public int lastCollision;
    public float rotationAngle;
    public float Vy = 0;

    public Present(float x, float y, float size,int type, int texture)
    {
        this.x = x;
        this.y = y;
        this.width = size;
        this.height = size*Engine.width/Engine.height;
        textureXpos = type%Engine.presentSpriteSize;
        textureYpos = type/Engine.presentSpriteSize;
        textureScale = 1f/Engine.presentSpriteSize;
        textureHandle = texture;
        rotationAngle = 0;
        rotationFull90 = 0;
        lastCollision =0;

        signs = Engine.ps.genSigns();
    }


}
