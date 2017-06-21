package santa.v1;

import java.security.acl.LastOwnerException;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import santa.v1.Engine.TutorialState;

import Objects.ConveyorBelt;
import Objects.GrayOverlay;
import Objects.Object;
import Objects.Bonus;
import Objects.Present;
import Objects.SpawnLocation;
import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Pair;
import android.view.TextureView;

public class GLES2Renderer implements Renderer
{

	private final Context mActivityContext;
	public static float[] mModelMatrix = new float[16];
	public static float[] mTextureMatrix = new float[16];
	public static float[] mViewMatrix = new float[16];
	public static float[] mProjectionMatrix = new float[16];
	public static float[] mMVPMatrix = new float[16];
	public static float[] mLightModelMatrix = new float[16];	
	public static float[] mColor = new float[] {1,1,0,1};
	
	public static int mMVPMatrixHandle;
	public static int mMVMatrixHandle;
	public static int mLightPosHandle;
	public static int mTextureUniformHandle;
	public static int mPositionHandle;
	public static int mTextureCoordinateHandle;
	public static int mTextureMatrixHandle;
	public static int mColorHandle;
	public static int mColorWrapHandle;
	public static int mProcentHandle;
	public static int mWrapTextureUniformHandle;
	
	public static int mWrapTextureHandle;
	
	public static int mGrayCenter,mGrayRadiousMin,mGrayRadiousMax,mGrayAlphaMin,mGrayAlphaMax;
	
	public static final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	public static final float[] mLightPosInWorldSpace = new float[4];
	public static final float[] mLightPosInEyeSpace = new float[4];
	
	public static int mProgramHandle;
	public static int mProgramPercentHandle;
	public static int mProgramLineHandle;
	int mProgramGray;
	
	GrayOverlay mGrayOverlay;
	Objects.Object finger;
	float tempx,tempy;
	
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
		
    	mColor = new float[]{1,1,1,1};
    	
		Matrix.setIdentityM(mModelMatrix, 0); 
        Matrix.setIdentityM(mTextureMatrix, 0);
    	GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, Engine.ObjTab[0].textureHandle);
      	GLES20.glUniform1i(mTextureUniformHandle, 0);
    	Engine.ObjTab[0].draw();
    	
		if(Engine.inGame)
		{		
			if(!Engine.paused && !Engine.inTutorial)
			{
				Engine.gravity(loopRunTime);
				if (Engine.bonus!=null) Engine.bonus.update(loopRunTime);
				if (Engine.slowStartTime>0) Engine.slowCb();
			}
			
		//	GLES20.glUseProgram(mProgramHandle); 	
	    //	getLocations(mProgramHandle);
	    	
			if(Engine.animationType == 1) // -------------------animacje fadeIN fadeOUT-----------------------------------
			{
				if(Engine.animationCounter < Engine.fadingDuration)++Engine.animationCounter;
			}
			else if (Engine.animationType == 2)
			{
				if(Engine.animationCounter > 0)--Engine.animationCounter;
				else 	
					{
						Engine.inGame = false;
						Engine.pf.backToBeginning();
						Bonus.backToBeginning();
					}
			}
			mColor = new float[]{1,1,1,(float)Engine.animationCounter/(float)Engine.fadingDuration};
			//------------------------------------------------------------------------------------------------------------


	    	for(ConveyorBelt cb : Engine.vCBelt)
	    		cb.draw();
	
	    	if(!Engine.paused && !Engine.inTutorial)
	    	{
				Engine.pf.spawn();
				Engine.pf.makeGameHarder();
	    	}

		    Engine.pf.drawPresents();

			if (Engine.bonusToCreate) Bonus.makeBonus();
			drawBonus();
		    if(Engine.health > 0)
		    {
			   drawHearts();
			   
		    }
		    else Engine.MainActivity.endScreen();
		    
	    	drawTutorial();
		    
	    	if(Engine.update) // --------------------------------rysowanie lini------------------------------------------
			{
	    		mColor = new float[]{1,1,0,(float)Engine.animationCounter/(float)Engine.fadingDuration};
	    		Engine.line.updateVertices((Vector<Pair<Float, Float>>) Engine.pLine.clone());
				GLES20.glUseProgram(mProgramLineHandle); 	
		    	getLocations(mProgramLineHandle);
		    	Matrix.setIdentityM(mModelMatrix, 0); 
		    	Engine.line.draw();
		    	mColor = new float[]{1,1,1,(float)Engine.animationCounter/(float)Engine.fadingDuration};
			} // --------------------------------------------------------------------------------------------------------
	    	
			if(Engine.resumed>0)
			{
				--Engine.resumed;
				if(Engine.resumed == 0)
					Engine.paused = true;
			}
		}
		loopEnd = System.currentTimeMillis();
		loopRunTime = (loopEnd-loopStart);
	
	}

	public static void getLocations(int program)
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
			mColorHandle = GLES20.glGetUniformLocation(program, "u_color");
			
			Matrix.setIdentityM(mLightModelMatrix, 0);
			Matrix.translateM(mLightModelMatrix, 0, 0.0f, 2.0f, 6.0f);
			       
			Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
			Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);    
		}
		else if(program == mProgramPercentHandle)
		{
			mMVPMatrixHandle = GLES20.glGetUniformLocation(program, "u_MVPMatrix");
			mMVMatrixHandle = GLES20.glGetUniformLocation(program, "u_MVMatrix"); 
			mLightPosHandle = GLES20.glGetUniformLocation(program, "u_LightPos");
			mTextureUniformHandle = GLES20.glGetUniformLocation(program, "u_Texture");
			mPositionHandle = GLES20.glGetAttribLocation(program, "a_Position");
			mTextureCoordinateHandle = GLES20.glGetAttribLocation(program, "a_TexCoordinate");		
			mTextureMatrixHandle = GLES20.glGetUniformLocation(program, "u_TextureMatrix");
			mWrapTextureUniformHandle = GLES20.glGetUniformLocation(program, "u_WrapTexture");
			mProcentHandle = GLES20.glGetUniformLocation(program, "u_pro");
			mColorHandle = GLES20.glGetUniformLocation(program, "u_color");
			mColorWrapHandle = GLES20.glGetUniformLocation(program, "u_colorWrap");
			
			Matrix.setIdentityM(mLightModelMatrix, 0);
			Matrix.translateM(mLightModelMatrix, 0, 0.0f, 2.0f, 6.0f);
			       
			Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
			Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);    
		}
		else if(program == mProgramLineHandle)
		{
			mMVPMatrixHandle = GLES20.glGetUniformLocation(program, "u_MVPMatrix");
			mPositionHandle = GLES20.glGetAttribLocation(program, "a_Position");
			mColorHandle = GLES20.glGetUniformLocation(program, "u_color");
		}
	}

	@SuppressLint("NewApi") @Override
	public void onSurfaceChanged(GL10 gl, int width, int height) 
	{
		GLES20.glViewport(0, 0, width, height);
		final float ratio = (float) width / height;
		//Engine.width = width;
		//Engine.height = height;

		Matrix.orthoM(mProjectionMatrix, 0, -.5f, .5f, -.5f, .5f, .1f, 100f);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) 
	{
		GLES20.glClearColor(66f/255f, 134f/255f, 244f/255f, 1f);
	    GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc( GLES20.GL_LEQUAL );
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
        
		vertexShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_vertex_shader_percent);
		fragmentShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.per_pixel_fragment_shader_procent);		
		vertexShaderHandle = Graphic.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
		fragmentShaderHandle = Graphic.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
		
		mProgramPercentHandle = Graphic.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, 
				new String[] {"a_Position", "a_TexCoordinate"});	
		
		vertexShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.line_vertex_shader);
		fragmentShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.line_fragment_shader);		
		
		vertexShaderHandle = Graphic.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
		fragmentShaderHandle = Graphic.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
		
		mProgramLineHandle = Graphic.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, 
				new String[] {"a_Position"});
		
		vertexShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.vertex_shader_gray_overlay);
		fragmentShader = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.fragment_shader_gray_overlay);		
		
		vertexShaderHandle = Graphic.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);
		fragmentShaderHandle = Graphic.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);
		
		mProgramGray = Graphic.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, 
				new String[] {"a_Position"});
		
		SantaActivity.gameView.load();
		mWrapTextureHandle = Graphic.loadTextureGLES2(Engine.ctx, R.drawable.present_temp);
		Engine.heartHeight = Engine.heartWidth*Engine.width/Engine.height;
		Engine.heartYoffset = Engine.heartXstart*Engine.width/Engine.height;
		for(int i = 0; i < Engine.health ; i++)
		{
			Engine.Hearts[i] = new Objects.Object(Engine.textureHeart);
			Engine.Hearts[i].x = Engine.heartXstart + (Engine.heartXoffset + Engine.heartWidth) * i;
			Engine.Hearts[i].y = 1 - Engine.heartHeight - Engine.heartYoffset;
		}
		
		mGrayOverlay = new GrayOverlay();
		finger = new Object(Engine.TutorialFingerTexture);
	 }
	
	private void drawHearts()
	{
		for(int i = 0; i < Engine.health;i++)
	    {
	    	Matrix.setIdentityM(mTextureMatrix,0);
	    	Matrix.setIdentityM(mModelMatrix, 0); 
	    	Matrix.translateM(GLES2Renderer.mModelMatrix,0,Engine.Hearts[i].x,Engine.Hearts[i].y,0);
	        Matrix.scaleM(GLES2Renderer.mModelMatrix,0,Engine.heartWidth,Engine.heartHeight,1);
	        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, Engine.Hearts[i].textureHandle);
	        GLES20.glUniform1i(GLES2Renderer.mTextureUniformHandle, 0);
	    	Engine.Hearts[i].draw();
	    }	
	}

	private void drawBonus()
	{
		if (Engine.bonus!=null)
		{
			Matrix.setIdentityM(GLES2Renderer.mTextureMatrix,0);
			Matrix.setIdentityM(GLES2Renderer.mModelMatrix, 0);
			Matrix.translateM(GLES2Renderer.mModelMatrix,0,Engine.bonus.x,Engine.bonus.y,0);
			Matrix.scaleM(GLES2Renderer.mModelMatrix,0,Engine.bonusWidth,Engine.bonusWidth*Engine.width/Engine.height,1);
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, Engine.bonusTextureHandle);
			GLES20.glUniform1i(GLES2Renderer.mTextureUniformHandle, 0);

			Engine.bonus.draw();
		}
	}


	private void drawGrayOverlay(float centerX,float centerY, float radMin, float radMax, float alphaMin, float alphaMax)
	{
		GLES20.glUseProgram(mProgramGray); 	
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramGray, "u_MVPMatrix");
		mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramGray, "u_MVMatrix"); 
		mPositionHandle = GLES20.glGetAttribLocation(mProgramGray, "a_Position");
		mGrayCenter = GLES20.glGetUniformLocation(mProgramGray, "center");
		mGrayRadiousMin = GLES20.glGetUniformLocation(mProgramGray, "radiousMin");
		mGrayRadiousMax = GLES20.glGetUniformLocation(mProgramGray, "radiousMax");
		mGrayAlphaMin = GLES20.glGetUniformLocation(mProgramGray, "alphaMin");
		mGrayAlphaMax = GLES20.glGetUniformLocation(mProgramGray, "alphaMax");
		Matrix.setIdentityM(mModelMatrix, 0);
		GLES20.glUniform1f(GLES20.glGetUniformLocation(mProgramGray, "ratio"),(float)Engine.width/(float)Engine.height);
		mGrayOverlay.draw(centerX, centerY, radMin, radMax, alphaMin, alphaMax);
		
	}
	
	private void drawTutorial()
	{
		switch (Engine.TutorialCurrentState) 
		{
		case Null:			
			break;
		case Screen1:
			drawTutorialScreen1();
			break;
		case Screen2:
			drawTutorialScreen2();
			break;
		case Screen3:
			drawTutorialScreen3();
			break;
		case Screen4:
			drawTutorialScreen4();
			break;
		default:
			break;
		}
	}
	
	private void drawTutorialScreen1()
	{
		if(Engine.TutorialInit) {
			Engine.vPresents.add(new Present(0.4f,0.611f,0.2f,0,Engine.PCSpriteHandle));
			Engine.TutorialInit = false;
		}
		drawGrayOverlay(0.5f, Engine.TutorialGrayY, Engine.TutorialGrayRMin, Engine.TutorialGrayRMax, 0.75f, 0); // potencjalnie zmiana na dno 
	}
	
	private void drawTutorialScreen2()
	{
		if(Engine.TutorialInit) {
			Engine.vPresents.get(0).signs.clear();
			Engine.vPresents.get(0).startingSignsCount = 1;
			Engine.vPresents.get(0).signs.add(2);
			Engine.inTutorialDrawSigns = true;
			Engine.TutorialInit = false;
			Engine.pLine = new Vector<Pair<Float,Float>>();
			Engine.update = true;
			Engine.paused = false;
		}
		if(Engine.TutorialDrawAnim) {
			if(Engine.TutorialAnimCounter < Engine.TutorialAnimLength) {
				if(Engine.TutorialAnimCounter < Engine.TutorialAnimLength/2f) {
					tempx = (float)Engine.TutorialAnimCounter/Engine.TutorialAnimLength*Engine.TutorialShapeSize;
					tempy = -tempx + Engine.TutorialLeftPointY;
					tempx += Engine.TutorialLeftPointX;
					Engine.pLine.add(new Pair<Float, Float>(tempx*Engine.width, tempy*Engine.height));				
				}
				else {
					tempx = (Engine.TutorialAnimCounter - Engine.TutorialAnimLength/2f)/Engine.TutorialAnimLength*Engine.TutorialShapeSize;
					tempy = tempx + Engine.TutorialLeftPointY - Engine.TutorialShapeSize/2f;
					tempx += Engine.TutorialLeftPointX + Engine.TutorialShapeSize/2f;
					Engine.pLine.add(new Pair<Float, Float>(tempx*Engine.width, tempy*Engine.height));	
					
				}
				Engine.TutorialAnimCounter++;
			}
			else{
				Engine.TutorialAnimCounter = 0; // loopipul
				Engine.pLine.clear();
			}
		}	
		drawGrayOverlay(0.5f, Engine.TutorialGrayY, Engine.TutorialGrayRMin, Engine.TutorialGrayRMax, 0.75f, 0);
		
		if(Engine.TutorialDrawAnim) {	
			GLES20.glUseProgram(mProgramHandle); 
			getLocations(mProgramHandle);
			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, tempx - 0.2f*Engine.TutorialFingerScale, tempy - (-0.2f*Engine.TutorialFingerScale+Engine.TutorialFingerScale)*Engine.width/Engine.height, 0.0f);
			Matrix.scaleM(mModelMatrix, 0, Engine.TutorialFingerScale, Engine.TutorialFingerScale*Engine.width/Engine.height, 1);
			Matrix.setIdentityM(mTextureMatrix,0);
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, finger.textureHandle);
		    GLES20.glUniform1i(GLES2Renderer.mTextureUniformHandle, 0);
			finger.draw();
		}			
	}
	
	private void drawTutorialScreen3()
	{
		if(Engine.TutorialInit) {
			Engine.vPresents.get(0).signs.clear();
			Engine.vPresents.get(0).startingSignsCount = 2;
			Engine.vPresents.get(0).signs.add(0);
			Engine.vPresents.get(0).signs.add(2);
			Engine.inTutorialDrawSigns = true;
			Engine.TutorialInit = false;
			Engine.update = false;
			Engine.paused = true;
		}
		
		drawGrayOverlay(0.5f, 0.76f, 0.1f, 0.15f, 0.75f, 0);
	}
	
	private void drawTutorialScreen4()
	{
		if(Engine.TutorialInit) {
			Engine.vPresents.clear();
			Engine.TutorialInit = false;
			Engine.paused = true;
		}
	}

}
