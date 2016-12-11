package Objects;

import android.opengl.GLES20;
import android.opengl.Matrix;
import santa.v1.Engine;
import santa.v1.GLES2Renderer;
import santa.v1.SantaActivity;

public class ConveyorBelt 
{
	float speed;
	float halfLength;
	float x,y;
	Object leftEnd,RightEnd,Middle;
	
	public ConveyorBelt(float _x, float _y, float length, float _speed)
	{
		x = _x - 1f/Engine.ConveyorBeltScale;
		y = _y - 1f/Engine.ConveyorBeltScale;
		speed = _speed;
		halfLength = length/2f/Engine.ConveyorBeltScale;
		
		leftEnd = new Object();
		leftEnd.texture = new float[]{.5f,.5f,1f,.5f,1,1f,.5f,1f};
		leftEnd.vertices = new float[]{0,0,0,
				1f/Engine.ConveyorBeltScale,0,0,
				1f/Engine.ConveyorBeltScale,1f/Engine.ConveyorBeltScale,0,
				0,1f/Engine.ConveyorBeltScale,0,};
		leftEnd.allocate();
		if(SantaActivity.supportsEs2)
			leftEnd.allocateGLES2();
		
		RightEnd = new Object();
		RightEnd.texture = new float[]{1f,1f,.5f,1f,.5f,.5f,1f,.5f};
		RightEnd.vertices = new float[]{0,10f/128f/Engine.ConveyorBeltScale,0,
				1f/Engine.ConveyorBeltScale,10f/128f/Engine.ConveyorBeltScale,0,
				1f/Engine.ConveyorBeltScale,(1f + 10f/128f)/Engine.ConveyorBeltScale,0,
				0,(1f + 10f/128f)/Engine.ConveyorBeltScale,0,};
		RightEnd.allocate();
		if(SantaActivity.supportsEs2)
			RightEnd.allocateGLES2();
		
		Middle = new Object();
		Middle.texture = new float[]{-length/2f + .5f,0,.5f,0,.5f,.5f,-length/2f + .5f,.5f};
		Middle.vertices = new float[]{0,0,0,
				2*halfLength,0,0,		
				2*halfLength,1f/Engine.ConveyorBeltScale,0,
				0,1f/Engine.ConveyorBeltScale,0};
		Middle.allocate();
		if(SantaActivity.supportsEs2)
			Middle.allocateGLES2();
	}

	public void draw()
	{
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, Engine.PCSpriteHandle);
      	GLES20.glUniform1i(GLES2Renderer.mTextureUniformHandle, 0);
      	Matrix.setIdentityM(GLES2Renderer.mTextureMatrix, 0);
		Matrix.setIdentityM(GLES2Renderer.mModelMatrix, 0);
		Matrix.translateM(GLES2Renderer.mModelMatrix, 0, x-halfLength, y, 0);
		leftEnd.draw();
		Matrix.translateM(GLES2Renderer.mModelMatrix, 0, 1f/Engine.ConveyorBeltScale, 0, 0);
		Middle.draw();
		Matrix.translateM(GLES2Renderer.mModelMatrix, 0, 2*halfLength, 0, 0);
		RightEnd.draw();
	}
	
}
