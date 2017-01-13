package santa.v1;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import Objects.ConveyorBelt;
import android.opengl.GLU;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Pair;

public class GameRenderer implements Renderer
{

	private long loopStart = 0;
	private long loopEnd = 0;
	private long loopRunTime = 0;
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
		
		if(!Engine.paused)
		{
		
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			
			gl.glColor4f(1, 1, 1, 1);
			
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glLoadIdentity();
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			Engine.ObjTab[0].draw(gl);
			
			if(Engine.inGame)
			{
				Engine.gravity(loopRunTime);
		
				if(Engine.animationType == 1)
				{
					if(Engine.animationCounter < Engine.fadingDuration)++Engine.animationCounter;
				}
				else if (Engine.animationType == 2)
				{
					if(Engine.animationCounter > 0)--Engine.animationCounter;
					else 	Engine.inGame = false;
				}
				gl.glColor4f(1,1,1,(float)Engine.animationCounter/(float)Engine.fadingDuration);
				
				for(ConveyorBelt cb : Engine.vCBelt)
		    		cb.draw(gl);
		
				Engine.pf.spawn();
				Engine.pf.drawPresents(gl); //TWOJ PREZENT COS PSUJE
				
				if(Engine.update)
				{
					Engine.line.updateVertices((Vector<Pair<Float, Float>>) Engine.pLine.clone());
					gl.glColor4f(0,0, 0, (float)Engine.animationCounter/(float)Engine.fadingDuration);
					gl.glLineWidth(10f);
			    	Engine.line.draw(gl);
			  		gl.glColor4f(1, 1, 1, (float)Engine.animationCounter/(float)Engine.fadingDuration);
				}
			}
		}
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
		
		SantaActivity.gameView.load(gl);
	}

}
