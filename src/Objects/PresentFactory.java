package Objects;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.util.Random;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import santa.v1.Engine;
import santa.v1.GLES2Renderer;
import santa.v1.Graphic;
import santa.v1.SantaActivity;

/**
 * Created by Przemek on 10.12.2016.
 */

public class PresentFactory {

    int textureHandle;
    Vector<Present> vect;
    Random rand;

    public PresentFactory(int texture) {

        vect = new Vector<Present>();
        rand=new Random();

        if (SantaActivity.supportsEs2)
            textureHandle = Graphic.loadTextureGLES2(Engine.ctx, texture);
        else
            textureHandle = texture;
    }

    public PresentFactory(int texture, GL10 gl)
    {
        vect = new Vector<Present>();
        rand=new Random();

        if (SantaActivity.supportsEs2)
            textureHandle = Graphic.loadTextureGLES2(Engine.ctx, texture);
        else
            textureHandle = Graphic.loadTextureGLES1(Engine.ctx, texture, gl);
    }

    public void spawnPresent(float x, float y)
    {

        float s;
        int t;

        s=rand.nextFloat()*(Engine.presentMaxSize-Engine.presentMinSize)+Engine.presentMinSize;
        t=rand.nextInt(Engine.presentTypeQuantity);

        vect.add(new Present(x,y,s,t,textureHandle));

    }

    public void checkSigns()
    {
        if (Engine.currentShape== Engine.shape.NULL) return;
        for (Present p:vect)
        {
            if (p.signs.size()==0) return;
            if (p.signs.firstElement()==Engine.currentShape.ordinal())
                p.signs.remove(0);
        }
    }

    public void drawPresents()
    {

        for (Present p:vect)
        {
            Matrix.setIdentityM(GLES2Renderer.mModelMatrix,0);
            Matrix.translateM(GLES2Renderer.mModelMatrix,0,p.x,p.y,0);
            Matrix.scaleM(GLES2Renderer.mModelMatrix,0,p.width,p.height,1);
            Matrix.rotateM(GLES2Renderer.mModelMatrix,0,p.rotationAngle,0,0,1);
            Matrix.translateM(GLES2Renderer.mModelMatrix,0,-0.5f,-0.5f,0);

            Matrix.setIdentityM(GLES2Renderer.mTextureMatrix,0);
            Matrix.scaleM(GLES2Renderer.mTextureMatrix,0,p.textureScale,p.textureScale,1);
            Matrix.translateM(GLES2Renderer.mTextureMatrix,0,p.textureXpos,p.textureYpos,0);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, p.textureHandle);
            GLES20.glUniform1i(GLES2Renderer.mTextureUniformHandle, 0);

            p.draw();

            Engine.ps.drawSigns(p);
        }

    }

    public void drawPresents(GL10 gl)
    {
        for (Present p:vect) {
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glTranslatef(p.x, p.y, 0);
            gl.glScalef(p.width, p.height, 1);
            gl.glRotatef(p.rotationAngle, 0,0,1);
            gl.glTranslatef(-0.5f,-0.5f, 0f);

            gl.glMatrixMode(GL10.GL_TEXTURE);
            gl.glLoadIdentity();
            gl.glScalef(p.textureScale, p.textureScale, 1);
            gl.glTranslatef(p.textureXpos, p.textureYpos, 0);

            p.draw(gl);

            Engine.ps.drawSigns(p,gl);
        }

    }
}
