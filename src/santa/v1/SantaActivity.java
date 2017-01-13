package santa.v1;

import java.util.Vector;

import Shapes.NormShape;
import Shapes.Shapes;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Pair;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SantaActivity extends Activity {

	public static boolean supportsEs2;
	public static GameView gameView;
	Display display;
	int c;
	float x,y;
	boolean sound=true;
	MediaPlayer mp;
	// temp
	TextView tv,bestScore;
	public static TextView score;
	ImageButton playButton,soundButton,pausePlayButton,pauseSoundButton;
	TextView title;
	ImageView pauseBG;
	
	AlphaAnimation animIN ,animOUT;
	
	
	
	
    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //-----------------------------------Device Info---------------------------------------
        
        Engine.ctx = this.getApplicationContext();
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
	    supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
	    //supportsEs2=false;
	    
		display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		Engine.width = size.x;
		Engine.height = size.y;
		
		SharedPreferences settings = getSharedPreferences("best", 0);
	    Engine.bestScore = settings.getInt("bestScore", 0);
	  
        //-------------------------------------Menu--------------------------------------------       
        
		animIN = new AlphaAnimation(0.0f, 1.0f);
		animIN.setDuration(300);
		animOUT = new AlphaAnimation(1.0f, 0f);
		animOUT.setDuration(300);
		
        setContentView(R.layout.menu_layout);
        gameView = (GameView)findViewById(R.id.menuView1);
        
        title = (TextView)findViewById(R.id.Title);
        Typeface font = Typeface.createFromAsset(getAssets(), "hemi_head_bd_it.ttf");
        title.setTypeface(font);
        title.bringToFront();
        
        bestScore = (TextView)findViewById(R.id.bestScore);
        bestScore.setText("Best Score : "+Engine.bestScore);
        bestScore.setTypeface(font);
        bestScore.bringToFront();
        
        score = (TextView)findViewById(R.id.textView2);
        score.setTypeface(font);
        score.bringToFront();
        
        mp = MediaPlayer.create(this,R.raw.music);
		mp.setLooping(true);
		mp.start();
		
		pausePlayButton = (ImageButton)findViewById(R.id.pausePalyButton);
		pauseSoundButton = (ImageButton)findViewById(R.id.pauseSoundButton);
		pauseBG = (ImageView)findViewById(R.id.pauseBG);

		setSound();
        
        playButton = (ImageButton)findViewById(R.id.StartButton);

        playButton.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View arg0) 
			{
				Engine.inGame = true;
				//setContentView(R.layout.activity_santa);	
		       	Engine.animationType = 1;							
				
		        score.setText("Score: "+Engine.score);
		        score.setVisibility(View.VISIBLE);
		        score.startAnimation(animIN);
		        
		        playButton.setEnabled(false);
		        playButton.setVisibility(View.GONE);
		        playButton.startAnimation(animOUT);
		        
		        soundButton.setEnabled(false);
		        soundButton.setVisibility(View.GONE);
		        soundButton.startAnimation(animOUT);
		        
		        title.setVisibility(View.GONE);
		        title.startAnimation(animOUT);
		        
		        bestScore.setVisibility(View.GONE);
		        bestScore.startAnimation(animOUT);
		        
		        pausePlayButton.setEnabled(false);
		        pausePlayButton.setVisibility(View.GONE);
		       // pausePlayButton.startAnimation(animOUT);
		        
		        pauseSoundButton.setEnabled(false);
		        pauseSoundButton.setVisibility(View.GONE);
		      //  pauseSoundButton.startAnimation(animOUT);
		        
		        pauseBG.setVisibility(View.GONE);
		      //  pauseBG.startAnimation(animOUT);
		        
			}
		});
        playButton.bringToFront();

        pausePlayButton.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View arg0) 
			{
				System.out.println("clicked");
				Engine.paused = false;
				
		        score.setText("Score: "+Engine.score);
		        score.setVisibility(View.VISIBLE);
		        
		        playButton.setEnabled(false);
		        playButton.setVisibility(View.GONE);
		       // playButton.startAnimation(animOUT);
		        
		        soundButton.setEnabled(false);
		        soundButton.setVisibility(View.GONE);
		       // soundButton.startAnimation(animOUT);
		        
		        title.setVisibility(View.GONE);
		       // title.startAnimation(animOUT);
		        bestScore.setVisibility(View.GONE);
		        
		        pausePlayButton.setEnabled(false);
		        pausePlayButton.setVisibility(View.GONE);
		        pausePlayButton.startAnimation(animOUT);
		        
		        pauseSoundButton.setEnabled(false);
		        pauseSoundButton.setVisibility(View.GONE);
		        pauseSoundButton.startAnimation(animOUT);
		        
		        pauseBG.setVisibility(View.GONE);
		        pauseBG.startAnimation(animOUT);
		        
			}
		});
        
        
        //---------------------------------Ladowanie gry--------------------------------------- 
		


		
		
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
				if(Engine.inGame && !Engine.paused)
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
				if(Engine.inGame && !Engine.paused)
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
				if(Engine.inGame && !Engine.paused)
				{
					Engine.currentShape = recogniseShape();
				//	tv = (TextView)findViewById(R.id.textView1);
				//	tv.setText(Engine.currentShape.toString());
					Engine.pLine.removeAllElements();
					Engine.update = false;

					Engine.pf.checkSigns();
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
    		if(Engine.paused)
    		{	
    			//loadmenu
    			
    			//Engine.vPresents.clear();
    			
    			Engine.animationType = 2;	
    			
    			if(Engine.score > Engine.bestScore)
    			{
    				SharedPreferences settings = getSharedPreferences("best", 0);
    			    SharedPreferences.Editor editor = settings.edit();
    			    editor.putInt("bestScore", Engine.score);
    			    editor.commit();
    			    Engine.bestScore = Engine.score;
    			}
    			
    			//Engine.inGame = false;    	
    			Engine.paused = false;
	    		title.setVisibility(View.VISIBLE);
	    		title.startAnimation(animIN);
	    		
	    		bestScore.setVisibility(View.VISIBLE);
		        bestScore.startAnimation(animIN);
		        bestScore.setText("Best Score : "+Engine.bestScore);
	    		
	    		soundButton.setEnabled(true);
	    		soundButton.setVisibility(View.VISIBLE);
	    		soundButton.startAnimation(animIN);
	    		
	    		playButton.setEnabled(true);
	    		playButton.setVisibility(View.VISIBLE);
	    		playButton.startAnimation(animIN);
	    		
	    		score.setVisibility(View.GONE);
	    		score.startAnimation(animOUT);
	
	    		pausePlayButton.setEnabled(false);
		        pausePlayButton.setVisibility(View.GONE);
		       // pausePlayButton.startAnimation(animOUT);
		        
		        pauseSoundButton.setEnabled(false);
		        pauseSoundButton.setVisibility(View.GONE);
		      //  pauseSoundButton.startAnimation(animOUT);
		        
		        pauseBG.setVisibility(View.GONE);
		      //  pauseBG.startAnimation(animOUT);
	    		
	            setSound();
    		}
    		else
    		{
    			//loadpausemenu
    			Engine.paused = true;
    			
    			title.setVisibility(View.GONE);
    			//title.startAnimation(animOUT);
    			
	    		bestScore.setVisibility(View.GONE);
	    		
	    		soundButton.setEnabled(false);
	    		soundButton.setVisibility(View.GONE);
	    		//soundButton.startAnimation(animOUT);
	    		
	    		playButton.setEnabled(false);
	    		playButton.setVisibility(View.GONE);
	    	//	playButton.startAnimation(animOUT);
	    		
	    		score.setVisibility(View.VISIBLE);
	    	//	score.startAnimation(animIN);
    			
    			pausePlayButton.setEnabled(true);
 		        pausePlayButton.setVisibility(View.VISIBLE);
 		        pausePlayButton.startAnimation(animIN);
 		        
 		        pauseSoundButton.setEnabled(true);
 		        pauseSoundButton.setVisibility(View.VISIBLE);
 		        pauseSoundButton.startAnimation(animIN);
 		        
 		        pauseBG.setVisibility(View.VISIBLE);
 		        pauseBG.startAnimation(animIN);
    		} 
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
		{
			//TODO serializable
			gameView.onPause();
			//Engine.paused = true;
			
			title.setVisibility(View.GONE);
			
    		bestScore.setVisibility(View.GONE);
    		
    		soundButton.setEnabled(false);
    		soundButton.setVisibility(View.GONE);
    		
    		playButton.setEnabled(false);
    		playButton.setVisibility(View.GONE);
    		
    		score.setVisibility(View.VISIBLE);
			
			pausePlayButton.setEnabled(true);
			pausePlayButton.setVisibility(View.VISIBLE);
			pausePlayButton.startAnimation(animIN);
			
			pauseSoundButton.setEnabled(true);
			pauseSoundButton.setVisibility(View.VISIBLE);
			pauseSoundButton.startAnimation(animIN);
			
			pauseBG.setVisibility(View.VISIBLE);
			pauseBG.startAnimation(animIN);			
		}
	}
    

	private void setSound()
	{
		soundButton = (ImageButton)findViewById(R.id.soundButton);
		soundButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				sound=!sound;

				if (sound)
				{
					soundButton.setBackgroundResource(R.drawable.sound_on);
					pauseSoundButton.setBackgroundResource(R.drawable.sound_on);
					mp.start();
				}
				else
				{
					soundButton.setBackgroundResource(R.drawable.sound_off);
					pauseSoundButton.setBackgroundResource(R.drawable.sound_off);
					mp.pause();
				}
			}
		});
		soundButton.bringToFront();
		
		pauseSoundButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				sound=!sound;

				if (sound)
				{
					soundButton.setBackgroundResource(R.drawable.sound_on);
					pauseSoundButton.setBackgroundResource(R.drawable.sound_on);
					mp.start();
				}
				else
				{
					pauseSoundButton.setBackgroundResource(R.drawable.sound_off);
					soundButton.setBackgroundResource(R.drawable.sound_off);
					mp.pause();
				}
			}
		});
		pauseSoundButton.bringToFront();
	}

}
