package santa.v1;

import java.util.Random;

public class SnowFlake extends Object
{	
	public float velocity;
	public float size;
	public float textureScale;
	public float textureXpos;
	public float textureYpos;
	public float rotationAngle = 0;
	public float rotationXaxis;
	public float rotationYaxis;
	public float rotationSpeed;
	Random rng;
	
	public SnowFlake(int texture,int inSpritNumber)
	{
		super();
		rng = new Random();
		velocity = rng.nextFloat()*(Engine.snowMaxSpeed-Engine.snowMinSpeed)+Engine.snowMinSpeed;
		size = rng.nextFloat()*(Engine.snowMaxSize-Engine.snowMinSize)+Engine.snowMinSize;
		rotationSpeed = rng.nextFloat()*(Engine.snowMaxRotationSpeed-Engine.snowMinRotationSpeed)+Engine.snowMinRotationSpeed;
		rotationXaxis = rng.nextFloat();
		rotationYaxis = rng.nextFloat();
		textureHandle = texture;
		textureScale = 1f/Engine.snowSpriteSize;
		textureYpos = inSpritNumber/Engine.snowSpriteSize ;
		textureXpos = inSpritNumber%Engine.snowSpriteSize ;
		randomXY();
	}
	
	public void randomXY()
	{
		x = rng.nextFloat();
		y = rng.nextFloat() + 1; 
	}
}
