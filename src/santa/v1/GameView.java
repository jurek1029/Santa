package santa.v1;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import Objects.ConveyorBelt;
import Objects.Object;
import Objects.Line;
import Objects.PresentFactory;
import Objects.PresentSigns;

public class GameView extends GLSurfaceView 
{
	
	public GLES2Renderer renderer;
	Context context;
	public GameView(Context _context) {
		super(_context);
		context = _context;
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
	
	public GameView(Context _context, AttributeSet attrs)
	{	
		super(_context, attrs);
		context = _context;
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

		Engine.ps = new PresentSigns(R.drawable.signs);
		Engine.pf = new PresentFactory();
		Engine.line = new Line();

		Engine.PCSpriteHandle = Graphic.loadTextureGLES2(context, Engine.PCSpriteTexture);
		Engine.pf.spawnPresent(0.5f,0.5f);
		
		Engine.Ctest = new ConveyorBelt(0.5f, 0.5f, 2f, 1f);
	}
	public void load(GL10 gl)
	{
		Engine.ObjTab[0] = new Object(R.drawable.statek,gl);
		Engine.line = new Line();

		Engine.ps = new PresentSigns(R.drawable.signs,gl);
		Engine.pf = new PresentFactory(R.drawable.presents,gl);

		Engine.PCSpriteHandle = Graphic.loadTextureGLES1(context, Engine.PCSpriteTexture, gl);
		Engine.pf.spawnPresent(0.5f,0.5f);
		
		Engine.Ctest = new ConveyorBelt(.5f, .5f, 5f, 1f);

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