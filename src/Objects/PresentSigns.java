package Objects;

/**
 * Created by Przemek on 07.12.2016.
 */
import java.lang.*;
import java.util.Random;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;
import android.opengl.Matrix;

import santa.v1.Engine;
import santa.v1.GLES2Renderer;
import santa.v1.Graphic;
import santa.v1.SantaActivity;

public class PresentSigns {

    int textureHandle;
    float actPresentX;
    float actPresentY;
    float actPresentWidth;
    float actPresentHeight;
    Vector<Integer> signVect;
    int actSignIndex;
    Random rand = new Random();

    public static int signsMaxNumber = Engine.signsMaxNumber;
    public static int signsMinNumber = Engine.signsMinNumber;
    public static int signsNormalNumber = Engine.signsNormalNumber;

    private class Shape extends Object
    {
        float width;
        float height;
        float textureScale;

        Shape() {
        super();
            width= Engine.signSize;
            height = Engine.signSize*Engine.width/Engine.height;
            textureScale= 1f/Engine.signSpriteSize;
        }
    }

    Shape shape = new Shape();

    public PresentSigns(int texture) {
        if (SantaActivity.supportsEs2)
            shape.textureHandle = Graphic.loadTextureGLES2(Engine.ctx, texture);
        else
            shape.textureHandle = texture;
    }


    public Vector<Integer> genSigns()
    {
        Vector<Integer> vect = new Vector<Integer>();

        int signsNumber = getSignsQuantity();
        for (int i=0; i<signsNumber; i++)
        {
            int n= rand.nextInt(Engine.shapes.size());
            if(n > 1)signsNumber--; // wagi dla znakow
            vect.add(n);
        }

        return vect;
    }

    private int getSignsQuantity()
    {
        double d = rand.nextGaussian();
        d=d*Engine.signsStandardDeviation+signsNormalNumber;
        int n = (d-(int)d < 0.5f ? (int)Math.floor(d):(int)Math.ceil(d));

        if (n>=signsMaxNumber) return signsMaxNumber;
        if (n<=signsMinNumber) return signsMinNumber;
        return n;
    }

    private void setSignPos()
    {
        float x,y;

        float all = signVect.size();
        x=actPresentX + actPresentWidth/2 + shape.width*(actSignIndex-all/2);
        y=actPresentY +actPresentHeight + Engine.signGapAbovePresent;

        shape.x=x;
        shape.y=y;
    }

    private void drawSingleSign(int number)
    {
        setSignPos();
        float textureXpos, textureYpos;

        textureXpos = number%Engine.signSpriteSize;
        textureYpos = number/Engine.signSpriteSize;

        //scale and set pos
        Matrix.setIdentityM(GLES2Renderer.mModelMatrix, 0);
        Matrix.translateM(GLES2Renderer.mModelMatrix,0,shape.x, shape.y,0);
        Matrix.scaleM(GLES2Renderer.mModelMatrix,0,shape.width,shape.height,1);

        //settexture
        Matrix.setIdentityM(GLES2Renderer.mTextureMatrix, 0);
        Matrix.scaleM(GLES2Renderer.mTextureMatrix,0,shape.textureScale,shape.textureScale,1);
        Matrix.translateM(GLES2Renderer.mTextureMatrix,0,textureXpos,textureYpos,0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shape.textureHandle);
        GLES20.glUniform1i(GLES2Renderer.mTextureUniformHandle, 0);


        shape.draw();

    }



    public void setPresentParam(float x, float y, float w, float h, Vector<Integer> vect)
    {
        actPresentX=x;
        actPresentY=y;
        actPresentHeight=h;
        actPresentWidth=w;
        signVect=vect;
    }

    public void setPresent(Present p)
    {
        actPresentX=p.x;
        actPresentY=p.y;
        actPresentHeight=p.height;
        actPresentWidth=p.width;
        signVect=p.signs;
    }


    public void drawSigns(Present p)
    {
        setPresent(p);
        synchronized (signVect) {
            actSignIndex = 0;
            for (Integer num : signVect) {
                drawSingleSign(num);
                actSignIndex++;
            }
        }
    }



    public static void increaseAmount()
    {
        signsMaxNumber++;
        signsNormalNumber++;
        signsMinNumber++;
    }
}
