package santa.v1;

import java.util.Vector;

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
		Engine.ObjTab[0] = new Object(R.drawable.wood,new float[]{0,0,1,0,1,(float)Engine.height/Engine.width,0,(float)Engine.height/Engine.width});

		Engine.ps = new PresentSigns(R.drawable.signs);
		Engine.pf = new PresentFactory(R.drawable.presents);
		Engine.line = new Line();

		Engine.PCSpriteHandle = Graphic.loadTextureGLES2(context, Engine.PCSpriteTexture);
		Engine.pf.spawnPresent(.8f,1f);
		
		Engine.vCBelt = new Vector<ConveyorBelt>();
		Engine.vCBelt.add(new ConveyorBelt(0, .75f  , 6, .1f));
		Engine.vCBelt.add(new ConveyorBelt(1, .75f , 6, -.1f));
		Engine.vCBelt.add(new ConveyorBelt(.5f, .55f , 2, .1f));
		Engine.vCBelt.add(new ConveyorBelt(0, .35f , 5, .03f));
		Engine.vCBelt.add(new ConveyorBelt(1, .15f , 8, -.1f));
	}
	public void load(GL10 gl)
	{
		Engine.ObjTab[0] = new Object(R.drawable.wood,new float[]{0,0,1,0,1,(float)Engine.height/Engine.width,0,(float)Engine.height/Engine.width},gl);
		Engine.line = new Line();

		Engine.ps = new PresentSigns(R.drawable.signs,gl);
		Engine.pf = new PresentFactory(R.drawable.presents,gl);

		Engine.PCSpriteHandle = Graphic.loadTextureGLES1(context, Engine.PCSpriteTexture, gl);
		Engine.pf.spawnPresent(0.5f,0.8f);
		
		Engine.vCBelt = new Vector<ConveyorBelt>();
		Engine.vCBelt.add(new ConveyorBelt(0, .75f  , 6, .1f));
		Engine.vCBelt.add(new ConveyorBelt(1, .75f , 6, -.1f));
		Engine.vCBelt.add(new ConveyorBelt(.5f, .55f , 2, .1f));
		Engine.vCBelt.add(new ConveyorBelt(0, .35f , 5, .03f));
		Engine.vCBelt.add(new ConveyorBelt(1, .15f , 8, -.1f));
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