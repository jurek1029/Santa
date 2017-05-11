package santa.v1;

import java.util.Vector;

import santa.v1.Engine.TutorialState;

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
import android.os.Vibrator;
import android.util.Pair;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
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
	TextView tv,bestScore,endScore;
	public static TextView score;
	ImageButton playButton,soundButton,pausePlayButton,pauseSoundButton;
	Button btnTutorial;
	TextView title;
	public static Typewriter TutorialText;
	ImageView pauseBG;
	
	AlphaAnimation animIN ,animOUT;
	ScaleAnimation animScIN, animScOUT;	
	
	
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
		sound = settings.getBoolean("Sound",true);
		Engine.MainActivity = this;
		Engine.vib = (Vibrator) Engine.ctx.getSystemService(Context.VIBRATOR_SERVICE);
        //-------------------------------------Menu--------------------------------------------       
        
		animIN = new AlphaAnimation(0.0f, 1.0f);
		animIN.setDuration(300);
		animOUT = new AlphaAnimation(1.0f, 0f);
		animOUT.setDuration(300);
		animOUT.setFillAfter(false);
		
		animScIN = new ScaleAnimation(1, 0.9f, 1, 0.9f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		animScIN.setDuration(80);
		animScIN.setFillAfter(true);
		
		animScOUT = new ScaleAnimation(0.9f, 1, 0.9f, 1,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		animScOUT.setDuration(50);
		animScOUT.setFillAfter(true);
		animScOUT.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation arg0) {}
			public void onAnimationRepeat(Animation arg0) {}
			public void onAnimationEnd(Animation arg0) {
				if(!Engine.inGame)
					play();
				else
					playFromPause();
			}
		});
		
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
        
        endScore = (TextView)findViewById(R.id.endScore);
        endScore.setTypeface(font);
        
        mp = MediaPlayer.create(this,R.raw.music);
		mp.setLooping(true);

		pausePlayButton = (ImageButton)findViewById(R.id.pausePalyButton);
		pauseSoundButton = (ImageButton)findViewById(R.id.pauseSoundButton);
		pauseBG = (ImageView)findViewById(R.id.pauseBG);


		setSoundBtnListeners();
		initSoundButtons();

        playButton = (ImageButton)findViewById(R.id.StartButton);
        playButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(arg1.getAction() == MotionEvent.ACTION_DOWN){playButton.startAnimation(animScIN);}
				if(arg1.getAction() == MotionEvent.ACTION_UP){playButton.startAnimation(animScOUT);}
				return false;
			}
		});
        playButton.bringToFront();
        
        pausePlayButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(arg1.getAction() == MotionEvent.ACTION_DOWN){pausePlayButton.startAnimation(animScIN);}
				if(arg1.getAction() == MotionEvent.ACTION_UP){pausePlayButton.startAnimation(animScOUT);}
				return false;
			}
		});
        
        btnTutorial = (Button)findViewById(R.id.buttonTutorial);
        btnTutorial.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				startTutorial();
			}
		});
        
        TutorialText = (Typewriter)findViewById(R.id.textViewTutorial);
        TutorialText.setTypeface(font);
        
        //---------------------------------Ladowanie gry--------------------------------------- 
		


		
		
		Engine.pLine = new Vector<Pair<Float, Float>>();	
		Engine.setShapes();

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
				else if (Engine.inTutorial)
				{
					Engine.TutorialCurrentState = nextState();
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
    	
    	if(Engine.inGame || Engine.inTutorial)
    	{  		
    		if(Engine.paused || Engine.inTutorial)//-------------------------loadmenu
    		{	
    			//Engine.vPresents.clear();
    			Engine.inTutorial = false;
    			Engine.TutorialCurrentState = TutorialState.Null;
    			Engine.animationType = 2;	
    			
    			if(Engine.score > Engine.bestScore)
    			{
    				SharedPreferences settings = getSharedPreferences("best", 0);
    			    SharedPreferences.Editor editor = settings.edit();
    			    editor.putInt("bestScore", Engine.score);
    			    editor.commit();
    			    Engine.bestScore = Engine.score; 			    
    			}
    			Engine.score = 0;
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
	    		
		        btnTutorial.setEnabled(true);
		        btnTutorial.setVisibility(View.VISIBLE);
		        btnTutorial.startAnimation(animIN);
		        
	            setSoundBtnListeners();
    		}
    		else//-------------------------------------loadpausemenu
    		{ 			
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

    		mp.pause();
    	}
    }
    
    @Override
	protected void onResume()
	{
		super.onResume();
		if(Engine.inGame)
		{
			gameView.onResume();
			if (sound) mp.start();
			else mp.pause();
		}
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		if(Engine.inGame)
		{
			gameView.onPause();
			//Engine.paused = true;
			
			mp.pause();

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
		SharedPreferences settings = getSharedPreferences("best", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("Sound",sound);
		editor.commit();

	}

	private void initSoundButtons()
	{
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
			mp.start();
			mp.pause();
		}
	}

	private void setSoundBtnListeners()
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

	public void endScreen()
	{
		Engine.fadingDuration = 120;
		Engine.animationType = 2;	
		
		
		
		Engine.health = Engine.healthMax;	
		Engine.paused = false;
		
		new Thread(new Runnable() {
			public void run() {
				final AlphaAnimation animIN2,animOUT2;
				animIN2 = new AlphaAnimation(0.0f, 1.0f);
				animIN2.setDuration(1000);
				animOUT2 = new AlphaAnimation(1f, 0f); // idfk jak bylo aminOUT to nie dzia³a³o 
				animOUT2.setDuration(300);
				endScore.post(new Runnable() {
					public void run() {
						endScore.setText("Score : "+Engine.score);
						endScore.setVisibility(View.VISIBLE);
						endScore.startAnimation(animIN2);
					}
				});
				try {
					Thread.sleep(1700);
				} catch (InterruptedException e) {e.printStackTrace();}
				endScore.post(new Runnable() {
					public void run() {					
						endScore.setVisibility(View.INVISIBLE);
						endScore.startAnimation(animOUT2);
					}
				});		
			}
		}).start();
		
		if(Engine.score > Engine.bestScore)
		{
			SharedPreferences settings = getSharedPreferences("best", 0);
		    SharedPreferences.Editor editor = settings.edit();
		    editor.putInt("bestScore", Engine.score);
		    editor.commit();
		    Engine.bestScore = Engine.score; 			    
		}
		Engine.score = 0;
		
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Engine.fadingDuration = 15;
				
				title.post(new Runnable() {
					public void run() {
						title.setVisibility(View.VISIBLE);
						title.startAnimation(animIN);
					}
				});
				
				bestScore.post(new Runnable() {
					public void run() {
						bestScore.setVisibility(View.VISIBLE);
				        bestScore.startAnimation(animIN);
				        bestScore.setText("Best Score : "+Engine.bestScore);
					}
				});
				
				soundButton.post(new Runnable() {
					public void run() {
						soundButton.setEnabled(true);
						soundButton.setVisibility(View.VISIBLE);
						soundButton.startAnimation(animIN);
					}
				});
				
				playButton.post(new Runnable() {
					public void run() {
						playButton.setEnabled(true);
						playButton.setVisibility(View.VISIBLE);
						playButton.startAnimation(animIN);
					}
				});
			
				score.post(new Runnable() {
					public void run() {
						score.setVisibility(View.GONE);
						score.startAnimation(animOUT);
					}
				});
				
				pausePlayButton.post(new Runnable() {
					public void run() {
						pausePlayButton.setEnabled(false);
				        pausePlayButton.setVisibility(View.GONE);
					}
				});
		        
				pauseSoundButton.post(new Runnable() {
					public void run() {
				        pauseSoundButton.setEnabled(false);
				        pauseSoundButton.setVisibility(View.GONE);
					}
				});
		        
				pauseBG.post(new Runnable() {
					public void run() {
				        pauseBG.setVisibility(View.GONE);
					}
				});
				
			}
		}).start();
		

	}
	
	public void play()
	{
		Engine.inGame = true;
		//setContentView(R.layout.activity_santa);	
       	Engine.animationType = 1;							
		
        score.setText("Score: "+Engine.score);
        score.setVisibility(View.VISIBLE);
        score.startAnimation(animIN);
        
        setMenuVisibilyGone();
        
        playButton.startAnimation(animOUT);
        soundButton.startAnimation(animOUT);
        title.startAnimation(animOUT);
        bestScore.startAnimation(animOUT);
        btnTutorial.startAnimation(animOUT);
	}
	
	public void playFromPause()
	{
		System.out.println("clicked");
		Engine.paused = false;
		
        score.setText("Score: "+Engine.score);
        score.setVisibility(View.VISIBLE);
        
        setMenuVisibilyGone();
        
        pausePlayButton.startAnimation(animOUT);        
        pauseSoundButton.startAnimation(animOUT);      
        pauseBG.startAnimation(animOUT);       
        
	}
	
	private void setMenuVisibilyGone()
	{
		playButton.setEnabled(false);
        playButton.setVisibility(View.GONE);
               
        soundButton.setEnabled(false);
        soundButton.setVisibility(View.GONE);
                
        title.setVisibility(View.GONE);
                
        bestScore.setVisibility(View.GONE);
            
        pausePlayButton.setEnabled(false);
        pausePlayButton.setVisibility(View.GONE);
        
        pauseSoundButton.setEnabled(false);
        pauseSoundButton.setVisibility(View.GONE);  
        
        pauseBG.setVisibility(View.GONE);
        
        btnTutorial.setEnabled(false);
        btnTutorial.setVisibility(View.GONE);       
	}
	
	
	private void startTutorial()
	{
		Engine.inTutorial = true;
		Engine.inGame = true;
		Engine.paused = true;
		
		Engine.animationType = 1;
		
		setMenuVisibilyGone();
		
		playButton.startAnimation(animOUT);
	    soundButton.startAnimation(animOUT);
	    title.startAnimation(animOUT);
	    bestScore.startAnimation(animOUT);
	    btnTutorial.startAnimation(animOUT);
	    
	    TutorialText.setVisibility(View.VISIBLE);
	    TutorialText.startAnimation(animIN);
	    
	    Engine.TutorialCurrentState = TutorialState.Screen1;
	    TutorialText.animateText("To jest nie zapakowany            prezent, nie mo¿esz pozwaliæ \naby z taœmoci¹gu spad³y nie   zapakowane prezenty  ");
	    
	}
	
	private Engine.TutorialState nextState()
	{
		switch (Engine.TutorialCurrentState) {
		case Return:
			return TutorialState.Null;
		case Screen1:
			TutorialText.animateText("Aby zapakowaæ znaczek nale¿y narysowaæ symbol na prezentem");
			return TutorialState.Screen2;
		case Screen2:
			return TutorialState.Screen3;
		case Screen3:
			return TutorialState.Screen4;
		case Screen4:
			return TutorialState.Return;
		}
		return TutorialState.Null;
	}
}
