package edu.dhbw.andar.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.hardware.Camera.Size;
import android.opengl.GLES20;
import android.util.Log;

public class GraphicsUtil {
	//private final static double epsilon = 0.001;
	//this epsilon being so large is intended, as often there will not be an adequate resolution with
	//the correct aspect ratio available
	//so we trade the correct aspect ratio for faster rendering
	private final static double epsilon = 0.17;
	
	/**
	 * Make a direct NIO FloatBuffer from an array of floats
	 * @param arr The array
	 * @return The newly created FloatBuffer
	 */
	public static FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length*4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}
	/**
	 * Make a direct NIO ByteBuffer from an array of floats
	 * @param arr The array
	 * @return The newly created FloatBuffer
	 */
	public static ByteBuffer makeByteBuffer(byte[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length);
		bb.order(ByteOrder.nativeOrder());
		bb.put(arr);
		bb.position(0);
		return bb;
	}
	public static ByteBuffer makeByteBuffer(int size) {
		ByteBuffer bb = ByteBuffer.allocateDirect(size);
		bb.position(0);
		return bb;
	}
	
	/**
	 * Get the optimal preview size for the given screen size.
	 * @param sizes
	 * @param screenWidth
	 * @param screenHeight
	 * @return
	 */
	public static Size getOptimalPreviewSize(List<Size> sizes, int screenWidth, int screenHeight) {
		double aspectRatio = ((double)screenWidth)/screenHeight;
		Size optimalSize = null;
		for (Iterator<Size> iterator = sizes.iterator(); iterator.hasNext();) {
			Size currSize =  iterator.next();
			double curAspectRatio = ((double)currSize.width)/currSize.height;
			//do the aspect ratios equal?
			if ( Math.abs( aspectRatio - curAspectRatio ) < epsilon ) {
				//they do
				if(optimalSize!=null) {
					//is the current size smaller than the one before
					if(optimalSize.height>currSize.height && optimalSize.width>currSize.width) {
						optimalSize = currSize;
					}
				} else {
					optimalSize = currSize;
				}
			}
		}
		if(optimalSize == null) {
			//did not find a size with the correct aspect ratio.. let's choose the smallest instead
			for (Iterator<Size> iterator = sizes.iterator(); iterator.hasNext();) {
				Size currSize =  iterator.next();
				if(optimalSize!=null) {
					//is the current size smaller than the one before
					if(optimalSize.height>currSize.height && optimalSize.width>currSize.width) {
						optimalSize = currSize;
					} else {
						optimalSize = currSize;
					}
				}else {
					optimalSize = currSize;
				}
				
			}
		}
		return optimalSize;
	}
	
	public static boolean containsSize(List<Size> sizes, Size size) {
		for (Iterator<Size> iterator = sizes.iterator(); iterator.hasNext();) {
			Size currSize =  iterator.next();
			if(currSize.width == size.width && currSize.height == size.height) {
				return true;
			}			
		}
		return false;
	}
	
	public static Size getSmallestSize(List<Size> sizes) {
		Size optimalSize = null;
		for (Iterator<Size> iterator = sizes.iterator(); iterator.hasNext();) {
			Size currSize =  iterator.next();		
			if(optimalSize == null) {
				optimalSize = currSize;
			} else if(optimalSize.height>currSize.height && optimalSize.width>currSize.width) {
				optimalSize = currSize;
			}
		}
		return optimalSize;
	}
	
	/**
	 * Calculate axis aligned bounding box vertices
	 * @param arr The array
	 * @return [minx][miny][minz][maxx][maxy][maxz]
	 */
	public static float[] calcAABB( float[] vertices )
	{
		// TODO: Find min(XYZ) and max(XYZ) vertices
		return vertices;
	}
	
	/**
	 * Create and link a gl shader program
	 * @param shaderType GL Shader type constant
	 * @param source Source code for the shader
	 */
	public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e("GraphicsUtil", "Could not compile shader " + shaderType + ":");
                Log.e("GraphicsUtil", GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }
	
	/**
	 * Setup a shader program in hardware
	 * @param vertexSource Vertex shader source code in a string
	 * @param fragmentSource Fragment shader source code in a string
	 */
	public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }

        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) {
            return 0;
        }
        
        Log.i("GraphicsUtil", "Vertex Source: " + vertexSource );
        Log.i("GraphicsUtil", "Fragment Source: " + fragmentSource );

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e("GraphicsUtil", "Could not link program: ");
                Log.e("GraphicsUtil", GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }
	
	public static int loadProgram( Context context, String vspath, String fspath ) {
		String vs = "";
		String fs = "";
		try {
			InputStream is;
			is = context.getAssets().open("shaders/passthrough.vs");
			vs = IO.convertStreamToString(is);
			is.close();
			is = context.getAssets().open("shaders/passthrough.fs");
			fs = IO.convertStreamToString(is);
			is.close();
		} catch(IOException e) {
			Log.e( "GraphicsUtil", "Could not load requested shader" );
		}
		
		// Set up a shader program and grab attributes
		return createProgram(vs, fs);
	}
	
	/**
	 * GL 2.0 Error checking
	 * @param op GLFunction to check
	 */
	public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("GraphicsUtil", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }
	
}
