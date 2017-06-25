package santa.v1;

import java.util.Vector;

import Objects.*;
import Objects.Object;
import Shapes.*;
import android.content.Context;
import android.os.Vibrator;
import android.util.Pair;

public class Engine 
{
	public static boolean inGame = false;
	public static boolean paused = false;
	public static int resumed = 0;
	public static final int GAME_THREAD_FPS_SLEEP = (1000/60);
	public static Context ctx;
	public static Object[] ObjTab = new Object[1];
	public static int height;
	public static int width;
	public static int score = 0;
	public static int bestScore;
	public static int textureBackground = R.drawable.bg;
	public static int fadingDuration = 15; // in frames
	public static int animationCounter = 0;
	public static int animationType = 0; // 0 = null, 1 = in, 2 = out
	public static SantaActivity MainActivity;
	public static Vibrator vib;
	
//Hearts
	public static int healthMax = 3;
	public static int health = healthMax;
	public static int textureHeart = R.drawable.heart;
	public static Object[] Hearts = new Object[healthMax];
	public static float heartWidth = 0.1f;
	public static float heartHeight;// liczone w renderze 
	public static float heartXstart = 0.015f;
	public static float heartXoffset = heartWidth/4f;
	public static float heartYoffset = heartXstart;

//Tutorial 
	public static enum TutorialState{
		Null,Screen1,Screen2,Screen3,Screen4,Return
	}
	public static TutorialState TutorialCurrentState = TutorialState.Null;
	public static boolean inTutorial= false;
	public static boolean inTutorialDrawSigns = true;
	public static boolean TutorialTextFinished = false;
    public static boolean TutorialInit = true;
    public static int TutorialAnimCounter = 0;
    public static int TutorialAnimLength = 180;
    public static float TutorialLeftPointX = 0.25f,TutorialLeftPointY = 0.5f;
    public static float TutorialShapeSize = 0.5f;
    public static int TutorialFingerTexture = R.drawable.finger;
    public static float TutorialFingerScale = 0.15f;
    public static boolean TutorialDrawAnim = true;
    public static float TutorialGrayY=0.66f,TutorialGrayRMin = 0.15f,TutorialGrayRMax = 0.25f;
	
//Line
	public static Vector<Pair<Float, Float>> pLine;
	public static Line line;
	public static boolean update = false;
	public static int minDeltaToRegisterMove = 3;

//Shape
	public enum shape
	{
		lineV,lineH,V,A,square,circle,tringle,NULL
	}
	public static shape currentShape = shape.NULL;
	public static Vector<Shapes> shapes;
	public static final float MAX_NORM_DISTORTION = Float.MAX_VALUE; // TODO set value
	public static final float MIN_RATIO_FOR_VA = 0.3f;
	
	public static void setShapes()
	{
		shapes = new Vector<Shapes>();
		shapes.add(new LineV());
		shapes.add(new LineH());
		shapes.add(new V());
		shapes.add(new A());
		
	}

//PresentSigns
	public static int signsMaxNormalNumber = 8;
	public static int signsMaxNumber = 4;
	public static int signsMinNumber = 1;
	public static int signsNormalNumber = 2;
	public static float signSize = 0.05f;
	public static float signGapAbovePresent = 0.013f;
	public static int signSpriteSize = 2;
	public static float signsStandardDeviation = 0.95f;

	public static PresentSigns ps;


//Presents
	public static int PCSpriteTexture = R.drawable.conveyor_sprite;
	public static int PCSpriteHandle;
	public static float presentMinSize = 0.1f;
	public static float presentMaxSize = 0.19f;
	public static int presentSpriteSize = 2;
	public static int presentTypeQuantity = 4;
	public static int presentMaxQuantity = 7;

	public static Vector<Present> vPresents;
	public static PresentFactory pf;

//ConveyorBelt
	public static Vector<ConveyorBelt> vCBelt;
	public static float ConveyorBeltScale = 10f;
	public static Vector<SpawnLocation>  vSpawnLocation;
	public static float cbSpeedMultilier = 1f;
	public static long slowStartTime=-1;
	public static long cbSlowTime = 2500;

//time
	public static long framesCounter = 0;
	public static double timeCounter = 0;


//bonus
	public static int bonusSpriteTexture = R.drawable.bon;
	public static int bonusTextureHandle;
	public static float bonusWidth = 0.1f;
	public static float bonusVelocity = 0.35f;
	public static Bonus bonus = null;
	public static Present bonusLastPresent = null; //prezent na podstawie ktorego ma powstac bonus
	public static  boolean bonusToCreate = false; //czy bonus czeka do utworzenia
	public static int bonusMinPresentsForBonus = 15;
	public static int bonusMinEndingPresentsForBonus = 8;
	public static long bonusSlowTime = 3000;


//slaw
	public static boolean slowByBonus;
	public static long slowTime;


	
// Physics 
	public static float gravityConst = -2f;
	public static void gravity(long loopRunTime)
	{
		if(loopRunTime<Engine.GAME_THREAD_FPS_SLEEP)loopRunTime = Engine.GAME_THREAD_FPS_SLEEP;
		float s = (float)(loopRunTime)/1000f;


		int collision = 0;
		for(Present p : Engine.vPresents)
		{
			p.Vy += Engine.gravityConst*s;
			p.y += p.Vy*s;
			
			for(ConveyorBelt cb : Engine.vCBelt)
			{
				collision = isColliding(cb, p, s);
				if(collision == 1) // flat
				{
					p.Vy = 0;
					p.x += cb.speed*s*cbSpeedMultilier;
					p.lastCollision = collision;
					break;					
				}
				else if(collision == 2)//left
				{
					p.Vy = 0;
					p.x += cb.speed*s;
					float d = (cb.x - cb.halfLength + 1f/Engine.ConveyorBeltScale/2f - p.x - p.width/2f)/(p.width/2 + 1/Engine.ConveyorBeltScale/2);
					if(d > 1) d = 1;			
					p.rotationAngle = (float) (Math.asin(d)*180/Math.PI) + p.rotationFull90*90;					
					p.y = (float) (cb.y + (Math.sqrt(1 - d*d))/Engine.ConveyorBeltScale*Engine.width/(float)(Engine.height));
					p.lastCollision = collision;
					break;
				}
				else if(collision == 3)//right
				{
					p.Vy = 0;
					p.x += cb.speed*s;
					float d = (p.x + p.width/2f - cb.halfLength - cb.x - 3f/Engine.ConveyorBeltScale/2f)/(p.width/2 + 1/Engine.ConveyorBeltScale/2);
					if(d > 1) d = 1;
					p.rotationAngle = -(float) (Math.asin(d)*180/Math.PI) + p.rotationFull90*90;
					p.y = (float) (cb.y + (Math.sqrt(1 - d*d))/Engine.ConveyorBeltScale*Engine.width/(float)(Engine.height));
					p.lastCollision = collision;
					break;
				}	
			}
			if(collision == 0)
			{
				if(p.lastCollision == 2)
				{
					p.rotationFull90++;
					p.lastCollision = 0;
				}
				else if (p.lastCollision == 3)
				{
					p.rotationFull90--;
					p.lastCollision = 0;
				}
			}
		}
	}
	/**
	 * @param s seconds of last frame
	 * @return 0 - no Collision, 1 - flat collision, 2 - left collision, 3 - right collision
	 */
	
	public static int isColliding(ConveyorBelt cb ,Present p, float s) 
	{
		if(p.y - p.Vy*s + p.height/2 >= cb.y + 1/Engine.ConveyorBeltScale*Engine.width/(float)(Engine.height)/2f) // czy w poprzedniej klatce srodek P byl ponad polowa tasmy
		{
			if(p.y > cb.y + 1/Engine.ConveyorBeltScale*Engine.width/(float)(Engine.height)) return 0;		
			if(p.x + p.width < cb.x - cb.halfLength || p.x > cb.x + cb.halfLength + 2f/Engine.ConveyorBeltScale) return 0;
			
			if(p.x + p.width/2f < cb.x - cb.halfLength + 1f/Engine.ConveyorBeltScale/2f) return 2;
			if(p.x + p.width/2f < cb.x + cb.halfLength + 3f/Engine.ConveyorBeltScale/2f) 
			{
				p.y = cb.y + 1f/Engine.ConveyorBeltScale*Engine.width/(float)(Engine.height);
				return 1;
			}
			return 3;
		}
		return 0;
	}

	public static void slowCb()
	{
		if (slowByBonus) slowTime = bonusSlowTime;
		else slowTime = cbSlowTime;
		long dt = System.currentTimeMillis()-slowStartTime;

		if (dt<.55*slowTime)
		{
			cbSpeedMultilier-=0.007;
			if (cbSpeedMultilier<0) cbSpeedMultilier=0;
			return;
		}
		else if (dt<=1.45*slowTime) return;
		else if (dt>=1.45*slowTime && dt<=2*slowTime)
		{
			cbSpeedMultilier+=0.008;
			if (cbSpeedMultilier>1) cbSpeedMultilier=1;
			return;
		}
		else

		{
			cbSpeedMultilier=1f;
			slowStartTime=-1;
			slowByBonus = false;
		}
		return;
	}
}


