package santa.v1;

import java.util.Vector;

import Objects.ConveyorBelt;
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
	public static float signGapAbovePresent = 0.01f;
	public static int signSpriteSize = 2;

	public static PresentSigns ps;
	public static Vector<Integer> vect;

//Presents
	public static int PCSpriteTexture = R.drawable.conveyor_sprite;
	public static int PCSpriteHandle;
	public static float presentMinSize = 0.1f;
	public static float presentMaxSize = 0.2f;
	public static int presentSpriteSize = 2;
	public static int presentTypeQuantity = 4;

	public static PresentFactory pf;
	
//ConveyorBelt
	public static ConveyorBelt Ctest;
	public static float ConveyorBeltScale = 10f;
}


