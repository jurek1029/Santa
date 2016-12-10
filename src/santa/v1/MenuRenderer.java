package santa.v1;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;

import Objects.Snow;
import Objects.Object;

public class MenuRenderer implements Renderer{

	private long loopStart = 0;
	private long loopEnd = 0;
	private long loopRunTime = 0;
	private Object back;
	@Override
	public void onDrawFrame(GL10 gl) 
	{
		loopStart = System.currentTimeMillis();
		try
		{
			if(loopRunTime<Engine.GAME_THREAD_FPS_SLEEP)
			{
				Thread.sleep(Engine.GAME_THREAD_FPS_SLEEP - loopRunTime);
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		//Draw
		
		
		gl.glPushMatrix();
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, -2);
		back.draw(gl);
		gl.glPopMatrix();
		Engine.snow.drawSnowFlakes(gl);
		
		loopEnd = System.currentTimeMillis();
		loopRunTime = (loopEnd-loopStart);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) 
	{
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof( -.5f, .5f, -.5f, .5f, .1f, 100f);
		GLU.gluLookAt(gl, 0.5f, 0.5f, 1, 0.5f, 0.5f, 0, 0, 1, 0);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glPushMatrix();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	{
		gl.glEnable(GL10.GL_TEXTURE_2D);

		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(66f/255f, 134f/255f, 244f/255f, 1f);
	    gl.glEnable(GL10.GL_CULL_FACE);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		back = new Object(Engine.menuBackgroudTexture,gl);
		Engine.snow = new Snow(gl);
		
	}

}
