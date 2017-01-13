package santa.v1;

import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Pair;

import Objects.ConveyorBelt;
import Objects.Object;
import Objects.Line;
import Objects.Present;
import Objects.PresentFactory;
import Objects.PresentSigns;
import Objects.SpawnLocation;

public class GameView extends GLSurfaceView 
{
	
	public GLES2Renderer renderer;
	Context context;
	@SuppressLint("NewApi") 
	public GameView(Context _context) {
		super(_context);
		context = _context;
		setPreserveEGLContextOnPause(true);
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
	
	@SuppressLint("NewApi")
	public GameView(Context _context, AttributeSet attrs)
	{	
		super(_context, attrs);
		context = _context;
		setPreserveEGLContextOnPause(true);
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
//		Engine.ObjTab[0] = new Object(Engine.backgroundTexture,new float[]{0,0,1,0,1,(float)Engine.height/Engine.width,0,(float)Engine.height/Engine.width});
		Engine.ObjTab[0] = new Object(Engine.textureBackground);
		
		Engine.PCSpriteHandle = Graphic.loadTextureGLES2(context, Engine.PCSpriteTexture);
		Engine.vPresents = new Vector<Present>();
		Engine.ps = new PresentSigns(R.drawable.signs);
		Engine.pf = new PresentFactory();
		Engine.line = new Line();
		Engine.vSpawnLocation = new Vector<SpawnLocation>();


		Engine.vCBelt = new Vector<ConveyorBelt>();
		Engine.vCBelt.add(new ConveyorBelt(0, .75f  , 6, .1f));
		Engine.vCBelt.add(new ConveyorBelt(1, .75f , 6, -.1f));
		Engine.vCBelt.add(new ConveyorBelt(.5f, .55f , 2, .1f));
		Engine.vCBelt.add(new ConveyorBelt(0, .35f , 4, 0.05f));
		Engine.vCBelt.add(new ConveyorBelt(1, .15f , 8, -.1f));

		Engine.pf.getSpawnLocations();

	}
	public void load(GL10 gl)
	{
		Engine.ObjTab[0] = new Object(Engine.textureBackground,new float[]{0,0,1,0,1,(float)Engine.height/Engine.width,0,(float)Engine.height/Engine.width},gl);
		Engine.line = new Line();
		
		Engine.PCSpriteHandle = Graphic.loadTextureGLES1(context, Engine.PCSpriteTexture, gl);
		Engine.vPresents = new Vector<Present>();
		Engine.ps = new PresentSigns(R.drawable.signs,gl);
		Engine.pf = new PresentFactory();

		Engine.vSpawnLocation = new Vector<SpawnLocation>();
		
		Engine.vCBelt = new Vector<ConveyorBelt>();
		Engine.vCBelt.add(new ConveyorBelt(0, .75f  , 6, .1f));
		Engine.vCBelt.add(new ConveyorBelt(1, .75f , 6, -.1f));
		Engine.vCBelt.add(new ConveyorBelt(.5f, .55f , 2, .1f));
		Engine.vCBelt.add(new ConveyorBelt(0, .35f , 4, .05f));
		Engine.vCBelt.add(new ConveyorBelt(1, .15f , 8, -.1f));

		Engine.pf.getSpawnLocations();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Engine.paused = false;
		Engine.resumed = 3;
		
	}
	@Override
	public void onPause()
	{
		super.onPause();
	}
}