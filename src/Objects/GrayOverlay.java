package Objects;

import santa.v1.GLES2Renderer;
import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class GrayOverlay extends Object{
	
	public GrayOverlay() {
		super();
	}
	
	@SuppressLint("NewApi")
	public void draw(float centerX,float centerY, float radMin, float radMax, float alphaMin, float alphaMax) {
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);

		GLES20.glVertexAttribPointer(GLES2Renderer.mPositionHandle, 3, GLES20.GL_FLOAT, false, 3*4, 0);
		GLES20.glEnableVertexAttribArray(GLES2Renderer.mPositionHandle);
   
		
		Matrix.multiplyMM(GLES2Renderer.mMVPMatrix, 0, GLES2Renderer.mViewMatrix, 0, GLES2Renderer.mModelMatrix, 0);   
        GLES20.glUniformMatrix4fv(GLES2Renderer.mMVMatrixHandle, 1, false, GLES2Renderer.mMVPMatrix, 0);     
        
        Matrix.multiplyMM(GLES2Renderer.mMVPMatrix, 0, GLES2Renderer.mProjectionMatrix, 0, GLES2Renderer.mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(GLES2Renderer.mMVPMatrixHandle, 1, false, GLES2Renderer.mMVPMatrix, 0);
          
        GLES20.glUniform2f(GLES2Renderer.mGrayCenter,centerX , centerY);
        GLES20.glUniform1f(GLES2Renderer.mGrayRadiousMin, radMin);
        GLES20.glUniform1f(GLES2Renderer.mGrayRadiousMax, radMax);
        GLES20.glUniform1f(GLES2Renderer.mGrayAlphaMin, alphaMin);
        GLES20.glUniform1f(GLES2Renderer.mGrayAlphaMax, alphaMax);
		
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.length, GLES20.GL_UNSIGNED_BYTE, 0);

		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

}
