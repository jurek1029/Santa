package santa.v1;

import java.util.Vector;

import Objects.ConveyorBelt;
import Objects.Present;
import Objects.PresentFactory;
import Objects.PresentSigns;
import Objects.Snow;
import Objects.Line;
import Objects.Object;
import Shapes.*;
import android.content.Context;
import android.util.Pair;

public class Engine 
{
	public static boolean inGame = false;
	public static final int GAME_THREAD_FPS_SLEEP = (1000/60);
	public static Context ctx;
	public static Object[] ObjTab = new Object[1];
	public static int height;
	public static int width;

//Line
	public static Vector<Pair<Float, Float>> pLine;
	public static int minDeltaToRegisterMove = 3;

//Shape
	public enum shape
	{
		lineV,lineH,V,A,square,circle,tringle,NULL
	}
	public static shape currentShape = shape.NULL;
	public static Line line;
	public static boolean update = false;
	public static Vector<Shapes> shapes;
	public static final float MAX_NORM_DISTORTION = Float.MAX_VALUE; // TODO set value
	public static final float MIN_RATIO_FOR_VA = 0.3f;
	
	public static void SetShapes()
	{
		shapes = new Vector<Shapes>();
		shapes.add(new LineV());
		shapes.add(new LineH());
		shapes.add(new V());
		shapes.add(new A());
		
	}
//Snow
	public static float snowMinSpeed = 0.001f;
	public static float snowMaxSpeed = 0.004f;
	public static float snowMinSize = 0.02f;
	public static float snowMaxSize = 0.06f;
	public static float snowMinRotationSpeed = 1f;
	public static float snowMaxRotationSpeed = 2f;
	public static int snowFlakesCount = 30;
	
	public static Snow snow;
	
	public static int menuBackgroudTexture = R.drawable.menu0;
	public static int snowFlakesTexture = R.drawable.snow_flakes_sprite;
	public static int snowSpriteSize = 5;

//PresentSigns
	public static int signsMaxNumber = 6;
	public static float signSize = 0.05f;
	public static float signGapAbovePresent = 0f;
	public static int signSpriteSize = 2;

	public static PresentSigns ps;
	public static Vector<Integer> vect;

//Presents
	public static int PCSpriteTexture = R.drawable.conveyor_sprite;
	public static int PCSpriteHandle;
	public static float presentMinSize = 0.1f;
	public static float presentMaxSize = 0.19f;
	public static int presentSpriteSize = 2;
	public static int presentTypeQuantity = 4;

	public static PresentFactory pf;
	
//ConveyorBelt
	public static Vector<ConveyorBelt> vCBelt;
	public static float ConveyorBeltScale = 10f;
	
// Physics 
	public static float gravityConst = -2f;
	public static void gravity(long loopRunTime)
	{
		if(loopRunTime<Engine.GAME_THREAD_FPS_SLEEP)loopRunTime = Engine.GAME_THREAD_FPS_SLEEP;
		float s = (float)(loopRunTime)/1000f;
		
		int collision = 0;
		for(Present p : Engine.pf.vect)
		{
			p.Vy += Engine.gravityConst*s;
			p.y += p.Vy*s;
			
			for(ConveyorBelt cb : Engine.vCBelt)
			{
				collision = isColliding(cb, p, s);
				if(collision == 1) // flat
				{
					p.Vy = 0;
					p.x += cb.speed*s;
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
}


