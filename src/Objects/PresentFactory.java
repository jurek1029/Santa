package Objects;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Pair;

import java.util.Iterator;
import java.util.ListIterator;
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
    Random rand;


    public PresentFactory() {

        rand=new Random();
        textureHandle=Engine.PCSpriteHandle;
    }

    private void spawnPresent(SpawnLocation sl)
    {

        float s;
        int t;
        Present p;

        s=rand.nextFloat()*(Engine.presentMaxSize-Engine.presentMinSize)+Engine.presentMinSize;
        t=rand.nextInt(Engine.presentTypeQuantity);

        Engine.vPresents.add(p = new Present(sl.x,sl.y,s,t,textureHandle));
        sl.setLastPresent(p);

    }

    public void checkSigns()
    {
        if (Engine.currentShape== Engine.shape.NULL) return;

            for (Present p : Engine.vPresents) {
                synchronized (p.signs) {
                if (p.signs.size() == 0) continue;
                if (p.signs.firstElement() == Engine.currentShape.ordinal())
                    p.signs.remove(0);
            }
            }
    }

    public void drawPresents()
    {
        Iterator<Present> it = Engine.vPresents.iterator();
        while(it.hasNext())
        {
            Present p=it.next();

            Matrix.setIdentityM(GLES2Renderer.mModelMatrix,0);
            Matrix.translateM(GLES2Renderer.mModelMatrix,0,p.x,p.y,0);
            Matrix.scaleM(GLES2Renderer.mModelMatrix,0,p.width,p.height,1);
            Matrix.translateM(GLES2Renderer.mModelMatrix,0,0.5f,0.5f,0);
            Matrix.rotateM(GLES2Renderer.mModelMatrix,0,p.rotationAngle,0,0,1);
            Matrix.translateM(GLES2Renderer.mModelMatrix,0,-0.5f,-0.5f,0);

            Matrix.setIdentityM(GLES2Renderer.mTextureMatrix,0);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
            GLES20.glUniform1i(GLES2Renderer.mTextureUniformHandle, 0);

            p.draw();

            Engine.ps.drawSigns(p);

            if (p.y<-Engine.presentMaxSize-0.1f) it.remove();
        }

    }

    public void drawPresents(GL10 gl)
    {
        Iterator<Present> it = Engine.vPresents.iterator();
        while(it.hasNext())
        {
            Present p=it.next();
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
            gl.glTranslatef(p.x, p.y, 0);
            gl.glScalef(p.width, p.height, 1);
            gl.glTranslatef(0.5f,0.5f, 0f);
            gl.glRotatef(p.rotationAngle, 0,0,1);
            gl.glTranslatef(-0.5f,-0.5f, 0f);

            gl.glMatrixMode(GL10.GL_TEXTURE);
            gl.glLoadIdentity();

            p.draw(gl);

            Engine.ps.drawSigns(p,gl);

            if (p.y<-Engine.presentMaxSize-0.1f) it.remove();
        }

    }

    public void getSpawnLocations()
    {
        Engine.vSpawnLocation.add(new SpawnLocation(0.01f,1f,1));
        Engine.vSpawnLocation.add(new SpawnLocation(0.95f-Engine.presentMaxSize,1f,-1));
        Engine.vSpawnLocation.add(new SpawnLocation(-Engine.presentMaxSize,0.35f+1/Engine.ConveyorBeltScale,1));
        Engine.vSpawnLocation.add(new SpawnLocation(1.1f,0.15f+1/Engine.ConveyorBeltScale,-1));
    }

    public void spawn()
    {
        if (rand.nextFloat()<0.5f || Engine.vPresents.size()>=Engine.presentMaxQuantity) return;
        int n = rand.nextInt(Engine.vSpawnLocation.size());
        SpawnLocation s = Engine.vSpawnLocation.get(n);
        if (s.canSpawn()) spawnPresent(s);

    }
}
