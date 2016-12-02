package santa.v1;

import java.io.InputStream;
import java.util.Vector;

import Shapes.NormShape;
import Shapes.Shapes;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

public class SantaActivity extends Activity {

	public static boolean supportsEs2;
	public static GameView gameView;
	Display display;
	int c;
	float x,y;
	// temp
	TextView tv;
	Movie m;
	
    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //-------------------------------------Menu--------------------------------------------       
        
        setContentView(R.layout.menu_layout);
        
        TextView title = (TextView)findViewById(R.id.Title);
        Typeface font = Typeface.createFromAsset(getAssets(), "candcu_font.ttf");
        title.setTypeface(font);
        
        title.bringToFront();
        
        GifMovieView gif1 = (GifMovieView) findViewById(R.id.gifMovieView1);
		gif1.setMovieResource(R.drawable.snow_g);
        
        ImageButton imgbnt = (ImageButton)findViewById(R.id.StartButton);
        imgbnt.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View arg0) 
			{
				Engine.inGame = true;
				setContentView(R.layout.activity_santa);
		        gameView = (GameView)findViewById(R.id.gl_surface_view);
			}
		});
        imgbnt.bringToFront();
        
        
        
        //---------------------------------Ladowanie gry---------------------------------------
        Engine.ctx = this.getApplicationContext();
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
	    supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

	    display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		Engine.width = size.x;
		Engine.height = size.y;
		
		
		Engine.pLine = new Vector<Pair<Float, Float>>();	
		Engine.SetShapes();

        //setContentView(R.layout.activity_santa);
        //gameView = (GameView)findViewById(R.id.gl_surface_view);
    }
    
    @Override
	public boolean onTouchEvent(MotionEvent event)
	{
    	c = event.getActionIndex();
		x = event.getX(c);
		y = event.getY(c);
		y = Engine.height - y;
			
		switch(event.getActionMasked())
		{
			case MotionEvent.ACTION_DOWN:
			{
				if(Engine.inGame)
				{
					Engine.pLine.add(new Pair<Float, Float>(x, y));
					Engine.update = true;
				}
				break;
			}			
			case MotionEvent.ACTION_POINTER_DOWN:
			{	
				break;
			}
			case MotionEvent.ACTION_MOVE:
			{
				if(Engine.inGame)
				{
					if(Math.sqrt((Engine.pLine.lastElement().first - x) * (Engine.pLine.lastElement().first - x) + (Engine.pLine.lastElement().second - y) * (Engine.pLine.lastElement().second - y))
							> Engine.minDeltaToRegisterMove)
					{
						Engine.pLine.add(new Pair<Float, Float>(x, y));					
					}
				}
				break;
			}
			case MotionEvent.ACTION_UP:
			{
				if(Engine.inGame)
				{
					Engine.currentShape = recogniseShape();
					tv = (TextView)findViewById(R.id.textView1);
					tv.setText(Engine.currentShape.toString());
					Engine.pLine.removeAllElements();
					Engine.update = false;
				}
				break;
			}
			case MotionEvent.ACTION_POINTER_UP:
			{
				break;
			}
		}
		return false;
	}
    
    Engine.shape recogniseShape()
    {
    	NormShape nsMin = new NormShape();
    	NormShape nsTemp = new NormShape();
    	for(Shapes s : Engine.shapes)
    	{
    		nsTemp = s.calculateNorm();
    		if(nsMin.norm > nsTemp.norm) nsMin = nsTemp;	
    		System.out.println(nsTemp.shape + " "+ nsTemp.norm);
    	}
    	
    	if(nsMin.norm > Engine.MAX_NORM_DISTORTION) return Engine.shape.NULL;
    	else return nsMin.shape;	
    }
    
    @Override
    public void onBackPressed() 
    {
    	
    	if(Engine.inGame)
    	{
    		Engine.inGame = false;
    		setContentView(R.layout.menu_layout);
    		TextView title = (TextView)findViewById(R.id.Title);
            Typeface font = Typeface.createFromAsset(getAssets(), "candcu_font.ttf");
            title.setTypeface(font);
            
            title.bringToFront();
            
            GifMovieView gif1 = (GifMovieView) findViewById(R.id.gifMovieView1);
    		gif1.setMovieResource(R.drawable.snow_g);
            
            ImageButton imgbnt = (ImageButton)findViewById(R.id.StartButton);
            imgbnt.setOnClickListener(new View.OnClickListener() {	
    			@Override
    			public void onClick(View arg0) 
    			{
    				Engine.inGame = true;
    				setContentView(R.layout.activity_santa);
    		        gameView = (GameView)findViewById(R.id.gl_surface_view);
    			}
    		});
            imgbnt.bringToFront();
    	}
    	else
    	{
    		super.onBackPressed();
    	}
    }
    
    @Override
	protected void onResume()
	{
		super.onResume();
		if(Engine.inGame)
			gameView.onResume();
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		if(Engine.inGame)
			gameView.onPause();
	}

}
