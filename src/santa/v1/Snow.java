package santa.v1;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

public class Snow 
{
	SnowFlake[] snowFlakes;
	public Snow( GL10 gl)
	{
		Random rng = new Random();
		int snowTextureHandle = Graphic.loadTextureGLES1(Engine.ctx, Engine.snowFlakesTexture, gl);
		snowFlakes = new SnowFlake[Engine.snowFlakesCount];
		for( int i = 0; i < snowFlakes.length; i++)
		{
			snowFlakes[i] = new SnowFlake(snowTextureHandle,rng.nextInt(Engine.snowSpriteSize*Engine.snowSpriteSize));
		}
	}
	
	public void drawSnowFlakes(GL10 gl)
	{
		for ( SnowFlake s : snowFlakes)
		{
			if(s.y <= 0)s.randomXY();
			s.y -= s.velocity;
			s.rotationAngle += s.rotationSpeed;
			gl.glPushMatrix();
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glTranslatef(s.x, s.y, 0);
			gl.glScalef(s.size, s.size, 1);	
			gl.glRotatef(s.rotationAngle, s.rotationXaxis, s.rotationYaxis, 0);
			gl.glTranslatef(-.5f, -.5f, 0);
			gl.glMatrixMode(GL10.GL_TEXTURE);
			gl.glLoadIdentity();
			gl.glScalef(s.textureScale, s.textureScale,1);
			gl.glTranslatef(s.textureXpos, s.textureYpos, 0);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			s.drawBothSides(gl);
			gl.glLoadIdentity();
			gl.glPopMatrix();
			
		}
	}
}
