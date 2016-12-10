package Objects;

/**
 * Created by Przemek on 10.12.2016.
 */

import java.util.Vector;

import santa.v1.Engine;

public class Present extends Object {

    float size;
    int type;
    float textureXpos;
    float textureYpos;
    float textureScale;
    Vector<Integer> signs;
    float rotationAngle;

    public Present(float x, float y, float size,int type, int texture)
    {
        this.x = x;
        this.y = y;
        this.size = size;
        textureXpos = type%Engine.presentSpriteSize;
        textureYpos = type/Engine.presentSpriteSize;
        textureScale = 1f/Engine.presentSpriteSize;
        textureHandle = texture;
        rotationAngle = 0;

        signs = Engine.ps.genSigns();
    }


}
