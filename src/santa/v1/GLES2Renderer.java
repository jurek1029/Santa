package santa.v1;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;

public class GLES2Renderer implements Renderer
{

	private final Context mActivityContext;
	public static float[] mModelMatrix = new float[16];
	public static float[] mTextureMatrix = new float[16];
	public static float[] mViewMatrix = new float[16];
	public static float[] mProjectionMatrix = new float[16];
	public static float[] mMVPMatrix = new float[16];
	public static float[] mLightModelMatrix = new float[16];	
	
	public static int mMVPMatrixHandle;
	public static int mMVMatrixHandle;
	public static int mLightPosHandle;
	public static int mTextureUniformHandle;
	public static int mPositionHandle;
	public static int mTextureCoordinateHandle;
	public static int mTextureMatrixHandle;
	
	private final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	public static final float[] mLightPosInWorldSpace = new float[4];
	public static final float[] mLightPosInEyeSpace = new float[4];
	
	private int mProgramHandle;
	
	private long loopStart = 0;
	private long loopEnd = 0;
	private long loopRunTime = 0;
	public GLES2Renderer(final Context activityContext) { mActivityContext = activityContext; }

	@Override
	public void onDrawFrame(GL10 gl) 
	{
		loopStart = System.currentTimeMillis();
		try
		{
			if(loopRunTime<Engine.GAME_THREAD_FPS_SLEEP)
			{
				Thread.sleep(Engine.GAME_THREAD_FPS_SLEEP - loopRunTime);
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		
		GLES20.glUseProgram(mProgramHandle); 	
    	getLocations(mProgramHandle);
    
        Matrix.setIdentityM(mModelMatrix, 0); 
        Matrix.setIdentityM(mTextureMatrix, 0);
        Matrix.translateM(mTextureMatrix, 0, 0, 0.5f, 0);
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, Engine.ObjTab[0].textureHandle);
      	GLES20.glUniform1i(mTextureUniformHandle, 0);
        
		Engine.ObjTab[0].draw(); 			

		loopEnd = System.currentTimeMillis();
		loopRunTime = (loopEnd-loopStart);
	}

	private void getLocations(int program)
	{
		if(program == mProgramHandle)
		{
			mMVPMatrixHandle = GLES20.glGetUniformLocation(program, "u_MVPMatrix");
			mMVMatrixHandle = GLES20.glGetUniformLocation(program, "u_MVMatrix"); 
			mLightPosHandle = GLES20.glGetUniformLocation(program, "u_LightPos");
			mTextureUniformHandle = GLES20.glGetUniformLocation(program, "u_Texture");
			mPositionHandle = GLES20.glGetAttribLocation(program, "a_Position");
			mTextureCoordinateHandle = GLES20.glGetAttribLocation(program, "a_TexCoordinate");		
			mTextureMatrixHandle = GLES20.glGetUniformLocation(program, "u_TextureMatrix");
			
			Matrix.setIdentityM(mLightModelMatrix, 0);
			Matrix.translateM(mLightModelMatrix, 0, 0.0f, 2.0f, 6.0f);
			       
			Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
			Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);    
		}
	}

	@SuppressLint("NewApi") @Override
	public void onSurfaceChanged(GL10 gl, int width, int height) 
	{
		GLES20.glViewport(0, 0, width, height);
		final float ratio = (float) width / height;

		Matrix.perspectiveM(mProjectionMatrix, 0, 50, ratio, .1f, 100f);	
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	{
		GLES20.glClearColor(66f/255f, 134f/255f, 244f/255f, 1f);
	    GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
	    final float eyeX = 0.5f;
	    final float eyeY = 0.5f;
	    final float eyeZ = 1.0f;
	    final float lookX = 0.5f;
	    final float lookY = 0.5f;
	    final float lookZ = 0.0f;
	    final float upX = 0.0f;
	    final float upY = 1.0f;
	    final float upZ = 0.0f;
	    Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
	    
	    String vertexShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_vertex_shader_no_normals);
 		String fragmentShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_fragment_shader_no_normals);		
		
		int vertexShaderHandle = Graphic.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);		
		int fragmentShaderHandle = Graphic.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);		
		
		mProgramHandle = Graphic.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, 
				new String[] {"a_Position", "a_TexCoordinate"});				
               			
		SantaActivity.gameView.load();
	 }

}
