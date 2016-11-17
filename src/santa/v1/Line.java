package santa.v1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Pair;

public class Line extends Object {

	public Line() 
	{
		super();		
	}
	
	public void updateVertices(Vector<Pair<Float, Float>> pLine)
	{
		vertices = new float[pLine.size()*3];
		int i=0;
		for(Pair<Float,Float> p : pLine)
		{
			vertices[i++] = p.first/Engine.width;
			vertices[i++] = p.second/Engine.height;
			vertices[i++] = 0.0f;
		}
		allocate();
	}
	
	@Override
	public void allocateGLES2()
	{

	}
	@Override
	public void allocate()
	{
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
 		byteBuf.order(ByteOrder.nativeOrder());
 		vertexBuffer = byteBuf.asFloatBuffer();
 		vertexBuffer.put(vertices);
 		vertexBuffer.position(0); 		
		
	}
	@Override
	@SuppressLint("NewApi") 
	public void draw()
	{
		GLES20.glEnableVertexAttribArray(GLES2Renderer.mPositionHandle);
		GLES20.glVertexAttribPointer(GLES2Renderer.mPositionHandle, 3, GLES20.GL_FLOAT, false, 3*4, vertexBuffer);		
		
		Matrix.multiplyMM(GLES2Renderer.mMVPMatrix, 0, GLES2Renderer.mViewMatrix, 0, GLES2Renderer.mModelMatrix, 0);              
        Matrix.multiplyMM(GLES2Renderer.mMVPMatrix, 0, GLES2Renderer.mProjectionMatrix, 0, GLES2Renderer.mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(GLES2Renderer.mMVPMatrixHandle, 1, false, GLES2Renderer.mMVPMatrix, 0);         
		
        GLES20.glUniform4fv(GLES2Renderer.mColorHandle, 1, GLES2Renderer.mColor, 0);
        GLES20.glLineWidth(10f);
		GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vertices.length/3);

		GLES20.glDisableVertexAttribArray(GLES2Renderer.mPositionHandle);
	}
	@Override
	public void draw(GL10 gl) 
	{
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glDrawArrays(GLES20.GL_LINE_STRIP, 0, vertices.length/3);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}
