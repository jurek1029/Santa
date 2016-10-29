package santa.v1;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class GameView extends GLSurfaceView 
{
	
	public GLES2Renderer renderer;
	public GameView(Context context) {
		super(context);
		if (SantaActivity.supportsEs2)
		{
	    	System.out.println("supports GLES2.0");
			
	        this.setEGLContextClientVersion(2);
	        renderer = new GLES2Renderer(context);	       
	        this.setRenderer(renderer);
		}
	    else
	    {
	    	System.out.println("don't support GLES2.0");
	    	this.setRenderer(new GameRenderer());
	    }
		
	}
	
	public GameView(Context context, AttributeSet attrs)
	{	
		super(context, attrs);
		if (SantaActivity.supportsEs2)
	    {
	    	System.out.println("supports GLES2.0");
	    	
	        this.setEGLContextClientVersion(2);
	        renderer = new GLES2Renderer(context);	        
	        this.setRenderer(renderer);
	    }
	    else
	    {
	    	System.out.println("don't support GLES2.0");
	    	this.setRenderer(new GameRenderer());
	    }
	}
	
	public void load()
	{
		Engine.ObjTab[0] = new Object(R.drawable.statek);
	}
	public void load(GL10 gl)
	{
		Engine.ObjTab[0] = new Object(R.drawable.statek,gl);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
	}
	@Override
	public void onPause()
	{
		super.onPause();
	}
}