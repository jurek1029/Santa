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

    private class Shape extends Object
    {
        float size;
        float textureScale;

        Shape() {
        super();
            size= Engine.signSize;
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

    public PresentSigns(int texture, GL10 gl)
    {
        if (SantaActivity.supportsEs2)
            shape.textureHandle = Graphic.loadTextureGLES2(Engine.ctx, texture);
        else
            shape.textureHandle = Graphic.loadTextureGLES1(Engine.ctx, texture, gl);
    }

    public Vector<Integer> genSigns()
    {
        Vector<Integer> vect = new Vector<Integer>();

        int signsNumber = rand.nextInt(Engine.signsMaxNumber)+1;
        System.out.println(signsNumber);
        for (int i=0; i<signsNumber; i++)
        {
            int n= rand.nextInt(Engine.shapes.size());
            System.out.println(n);
            vect.add(n);
        }

        return vect;
    }

    private void setSignPos()
    {
        float x,y;
        float all = signVect.size();
        x=actPresentX + actPresentWidth/2 + shape.size*(actSignIndex-all/2);
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
        Matrix.scaleM(GLES2Renderer.mModelMatrix,0,shape.size,shape.size,1);


        //settexture
        Matrix.setIdentityM(GLES2Renderer.mTextureMatrix, 0);
        Matrix.scaleM(GLES2Renderer.mTextureMatrix,0,shape.textureScale,shape.textureScale,1);
        Matrix.translateM(GLES2Renderer.mTextureMatrix,0,textureXpos,textureYpos,0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shape.textureHandle);
        GLES20.glUniform1i(GLES2Renderer.mTextureUniformHandle, 0);


        shape.draw();

    }

    private void drawSingleSign(int number,GL10 gl)
    {
        setSignPos();
        float textureXpos, textureYpos;

        textureXpos = number%Engine.signSpriteSize;
        textureYpos = number/Engine.signSpriteSize;

        //TODO
        //set matrixes for GL10

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        gl.glTranslatef(shape.x,shape.y, 0);
        gl.glScalef(shape.size, shape.size, 1);

        gl.glMatrixMode(GL10.GL_TEXTURE);
        gl.glLoadIdentity();
        gl.glScalef(shape.textureScale, shape.textureScale,1);
        gl.glTranslatef(textureXpos, textureYpos, 0);

        shape.draw(gl);




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
        actPresentHeight=p.size;
        actPresentWidth=p.size;
        signVect=p.signs;
    }


    public void drawSigns(Present p)
    {
        setPresent(p);
        actSignIndex=0;
        for (Integer num:signVect)
        {
            drawSingleSign(num);
            actSignIndex++;

        }
    }

    public void drawSigns(Present p,GL10 gl)
    {

        setPresent(p);
        actSignIndex=0;
        for (Integer num:signVect)
        {
            drawSingleSign(num,gl);
            actSignIndex++;

        }
    }
}
