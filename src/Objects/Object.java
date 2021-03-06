package Objects;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.opengl.Matrix;

import santa.v1.Engine;
import santa.v1.GLES2Renderer;
import santa.v1.Graphic;
import santa.v1.SantaActivity;


public class Object {

	protected float vertices[] = {
			0.0f, 0.0f, 0.0f,
			1.0f, 0.0f, 0.0f,
			1.0f, 1.0f, 0.0f,
			0.0f, 1.0f, 0.0f
	};
	
	protected float texture[] = {
			0.0f, 0.0f,
			1.0f, 0.0f,
			1.0f, 1.0f,
			0.0f, 1.0f
	};
	
	protected byte indices[]={
			0, 1, 2,
			0, 2, 3
		};
	
	public int textureHandle;
	public float x = 0,y = 0;
	protected FloatBuffer vertexBuffer;
	protected FloatBuffer textureBuffer;
	protected Buffer indexBuffer; 
	protected final int[] vbo = new int[1];
	protected final int[] tbo = new int[1];
	protected final int[] ibo = new int[1];
	
	public Object() 
	{
		allocate();
		if(SantaActivity.supportsEs2)allocateGLES2();
	}
	
	public Object(int texture)
	{
		if(SantaActivity.supportsEs2)
			textureHandle = Graphic.loadTextureGLES2(Engine.ctx, texture);
		else
			textureHandle = texture;
		allocate();
		if(SantaActivity.supportsEs2)allocateGLES2();
	}

	
	public Object(int texture, float[] _texture)
	{
		this(texture);
		this.texture = _texture;		
		allocate();
		if(SantaActivity.supportsEs2)allocateGLES2();
	}

	
	public void allocateGLES2()
	{
		GLES20.glGenBuffers(1, vbo, 0);
 		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);
				
		GLES20.glGenBuffers(1, tbo, 0);
 		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, tbo[0]);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, textureBuffer.capacity() * 4, textureBuffer, GLES20.GL_STATIC_DRAW);

		GLES20.glGenBuffers(1, ibo, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity(), indexBuffer, GLES20.GL_STATIC_DRAW);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		if(vbo[0] == 0 || tbo[0] == 0 || ibo[0] == 0)
			System.out.println("error binding bufers");
	}
	
	public void allocate()
	{
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
 		byteBuf.order(ByteOrder.nativeOrder());
 		vertexBuffer = byteBuf.asFloatBuffer();
 		vertexBuffer.put(vertices);
 		vertexBuffer.position(0); 		
 		
 		byteBuf = ByteBuffer.allocateDirect(texture.length * 4);
 		byteBuf.order(ByteOrder.nativeOrder());
 		textureBuffer = byteBuf.asFloatBuffer();
 		textureBuffer.put(texture);
 		textureBuffer.position(0);
 		
		byteBuf = ByteBuffer.allocateDirect(indices.length);
 		byteBuf.order(ByteOrder.nativeOrder());
 		indexBuffer = byteBuf;
 		for(int i =0;i<indices.length;i++)
 			((ByteBuffer) indexBuffer).put((Byte)(indices[i]));
 		indexBuffer.position(0);
		
	}
	
	@SuppressLint("NewApi") 
	public void draw()
	{
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

		GLES20.glVertexAttribPointer(GLES2Renderer.mPositionHandle, 3, GLES20.GL_FLOAT, false, 3*4, 0);
		GLES20.glEnableVertexAttribArray(GLES2Renderer.mPositionHandle);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, tbo[0]);
		GLES20.glVertexAttribPointer(GLES2Renderer.mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 2*4, 0);
		GLES20.glEnableVertexAttribArray(GLES2Renderer.mTextureCoordinateHandle);
		
		GLES20.glUniformMatrix4fv(GLES2Renderer.mTextureMatrixHandle, 1, false, GLES2Renderer.mTextureMatrix, 0);     
		
		Matrix.multiplyMM(GLES2Renderer.mMVPMatrix, 0, GLES2Renderer.mViewMatrix, 0, GLES2Renderer.mModelMatrix, 0);   
        GLES20.glUniformMatrix4fv(GLES2Renderer.mMVMatrixHandle, 1, false, GLES2Renderer.mMVPMatrix, 0);     
        
        Matrix.multiplyMM(GLES2Renderer.mMVPMatrix, 0, GLES2Renderer.mProjectionMatrix, 0, GLES2Renderer.mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(GLES2Renderer.mMVPMatrixHandle, 1, false, GLES2Renderer.mMVPMatrix, 0);
          
        GLES20.glUniform3f(GLES2Renderer.mLightPosHandle, GLES2Renderer.mLightPosInEyeSpace[0], GLES2Renderer.mLightPosInEyeSpace[1], GLES2Renderer.mLightPosInEyeSpace[2]);
		GLES20.glUniform4f(GLES2Renderer.mColorHandle, GLES2Renderer.mColor[0], GLES2Renderer.mColor[1], GLES2Renderer.mColor[2],GLES2Renderer.mColor[3]);
        
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_BYTE, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	

}
