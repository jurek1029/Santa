package santa.v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class Graphic
{
	public int[]textures;
	
	public Graphic(int count) { textures = new int[count]; }
	
	public void initParameters(GL10 gl)
	{
		gl.glGenTextures(textures.length, textures,0);
	}
	
	
	public static int loadTextureGLES2(final Context context, final int resourceId)
	{
		final int[] textureHandle = new int[1];
		
		GLES20.glGenTextures(1, textureHandle, 0);
		
		if (textureHandle[0] != 0)
		{
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false;	// No pre-scaling

			final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
						
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
			
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);			
			
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
			bitmap.recycle();						
		}
		
		if (textureHandle[0] == 0)
		{
			throw new RuntimeException("Error loading texture.");
		}
		
		return textureHandle[0];
	}
	
	public static int loadTextureGLES1(final Context context, final int resourceId,GL10 gl)
	{
		final int[] textureHandle = new int[1];
		
		gl.glGenTextures(1, textureHandle, 0);
		
		if (textureHandle[0] != 0)
		{
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false;	// No pre-scaling

			final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
						
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureHandle[0]);
			
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
			
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
			bitmap.recycle();						
		}
		
		if (textureHandle[0] == 0)
		{
			throw new RuntimeException("Error loading texture.");
		}
		
		return textureHandle[0];
	}
	
	@SuppressWarnings("resource")
	public static int loadDDSTexture(final Context context, final int texture)
	{
		final int FOURCC_DXT1 = 0x31545844; // Equivalent to "DXT1" in ASCII
		final int FOURCC_DXT3 = 0x33545844; // Equivalent to "DXT3" in ASCII
		final int FOURCC_DXT5 = 0x35545844; // Equivalent to "DXT5" in ASCII
		
		File file = new File(context.getFilesDir().getPath() + "/"+context.getString(texture));
		FileInputStream fis;
		try 
		{
			fis = new FileInputStream(file);
			FileChannel chan = fis.getChannel();
			final ByteBuffer buf = chan.map(FileChannel.MapMode.READ_ONLY, 0, (int) file.length());
			buf.order(ByteOrder.LITTLE_ENDIAN);
			buf.position(0);
			
			int[] header = new int[31];
		 
			if(buf.getChar() != 'D' || buf.getChar() != 'D' || buf.getChar() != 'S' || buf.getChar() != ' ')
			{
				System.out.println("This file is not a DDS file");
				return -1;
			}
			
			for(int i = 0; i < header.length; i++)header[i] = buf.getInt();

			int height      = header[2];
			int width	     = header[3];
			int linearSize	 = header[4];
			int mipMapCount = header[6];
			int fourCC      = header[20];

		 
			ByteBuffer buffer;
			int bufsize;
			/* how big is it going to be including all mipmaps? */ 
			bufsize = (mipMapCount > 1) ? linearSize * 2 : linearSize; 
			buffer = ByteBuffer.allocateDirect(bufsize);
			byte[] b = new byte[bufsize];			
			buf.get(b);
			buffer = ByteBuffer.wrap(b);
			
			/* close the file pointer */ 

			//int components  = (fourCC == FOURCC_DXT1) ? 3 : 4; 
			int format;
			switch(fourCC) 
			{ 
			case FOURCC_DXT1: 
				format = 33777; //GL_COMPRESSED_RGBA_S3TC_DXT1_EXT; 
				break; 
			case FOURCC_DXT3: 
				format = 33778; //GL_COMPRESSED_RGBA_S3TC_DXT3_EXT; 
				break; 
			case FOURCC_DXT5: 
				format = 33779; //GL_COMPRESSED_RGBA_S3TC_DXT5_EXT; 
				break; 
			default: 				
				return -2; 
			}

			// Create one OpenGL texture
			final int[] textureHandle = new int[1];
			
			GLES20.glGenTextures(1, textureHandle, 0);

			// "Bind" the newly created texture : all future texture functions will modify this texture
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
			GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT,1);	
			
			int blockSize = (format == 33777) ? 8 : 16; 
			int offset = 0;

			/* load the mipmaps */ 
			for (int level = 0; level < mipMapCount && (width != 0 || height != 0); ++level) 
			{ 
				int size = ((width+3)/4)*((height+3)/4)*blockSize; 
				buffer.position(offset);
				GLES20.glCompressedTexImage2D(GLES20.GL_TEXTURE_2D, level, format, width, height,  
					0, size, buffer ); 
			 
				offset += size; 
				width  /= 2; 
				height /= 2; 

				// Deal with Non-Power-Of-Two textures. This code is not included in the webpage to reduce clutter.
				if(width < 1) width = 1;
				if(height < 1) height = 1;

			} 

			return textureHandle[0];

			
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public static int compileShader(final int shaderType, final String shaderSource) 
	{
		int shaderHandle = GLES20.glCreateShader(shaderType);

		if (shaderHandle != 0) 
		{
			GLES20.glShaderSource(shaderHandle, shaderSource);
			GLES20.glCompileShader(shaderHandle);

			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

			if (compileStatus[0] == 0) 
			{
				Log.e("Shader", "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0)
		{			
			throw new RuntimeException("Error creating shader.");
		}
		
		return shaderHandle;
	}
	
	public static int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes) 
	{
		int programHandle = GLES20.glCreateProgram();
		
		if (programHandle != 0) 
		{
			GLES20.glAttachShader(programHandle, vertexShaderHandle);			
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);
			
			if (attributes != null)
			{
				final int size = attributes.length;
				for (int i = 0; i < size; i++)
				{
					GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
				}						
			}
			
			GLES20.glLinkProgram(programHandle);

			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

			if (linkStatus[0] == 0) 
			{				
				Log.e("Shader", "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}
		
		if (programHandle == 0)
		{
			throw new RuntimeException("Error creating program.");
		}
		
		return programHandle;
	}
	
	
}
