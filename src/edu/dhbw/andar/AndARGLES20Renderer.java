/**
	Copyright (C) 2009,2010  Tobias Domhan

    This file is part of AndOpenGLCam.

    AndObjViewer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AndObjViewer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AndObjViewer.  If not, see <http://www.gnu.org/licenses/>.
 
 */
package edu.dhbw.andar;


import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.dhbw.andar.interfaces.OpenGLRenderer;
import edu.dhbw.andar.interfaces.PreviewFrameSink;
import edu.dhbw.andar.util.IO;
import edu.dhbw.andar.util.GraphicsUtil;

import android.R;
import android.content.res.Resources;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Bitmap.Config;
import android.opengl.GLDebugHelper;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

/**
 * Sets up an AndAR OpenGL ES 2.0 renderer derived from AndARRenderer
 * @author Griffin Milsap
 *
 */
public class AndARGLES20Renderer extends AndARRenderer {
	private final String TAG = "AndARGLES20Renderer";
	
	private static final int FLOAT_SIZE_BYTES = 4;
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 3 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_UV_STRIDE_BYTES = 2 * FLOAT_SIZE_BYTES;
	
	// GLES 2.0 doesn't do matrix math for us for free.
    private float[] mMVPMatrix = new float[16]; // Projection*ModelView Matrix
    private float[] mProjMatrix = new float[16]; // Projection Matrix
    private float[] mVMatrix = new float[16]; // ModelView Matrix

    private int mProgram;
    private int mSamplerLoc;
	private int muMVPMatrixHandle;
	private int maPositionHandle;
	private int maTextureHandle;
	
	/**
	 * mode, being either GLES20.GL_RGB or GLES20.GL_LUMINANCE
	 */
	private int mode = GLES20.GL_RGB;
	
	/**
	 * the default constructor
	 * @param res Resources
	 * @param markerInfo The ARToolkit instance
	 * @param activity The spawning activity
	 */
	public AndARGLES20Renderer(Resources res, ARToolkit markerInfo, AndARActivity activity)  {
		super(res, markerInfo, activity);
		Log.i( TAG, "Using GLES Version 2.0" );
	}
	
	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
	 */
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Load shaders from assets, compile and load a program
		mProgram = GraphicsUtil.loadProgram( activity, "shaders/passthrough.vs", "shaders/passthrough.fs" );
        if (mProgram == 0) { 
            return;
        }
        
        // Grab Attributes and uniforms from the loaded shader
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GraphicsUtil.checkGlError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        GraphicsUtil.checkGlError("glGetAttribLocation aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GraphicsUtil.checkGlError("glGetUniformLocation uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }
        mSamplerLoc = GLES20.glGetUniformLocation (mProgram, "sTexture");
        GraphicsUtil.checkGlError("glGetUniformLocation sTexture");
        if (mSamplerLoc == -1) {
        	throw new RuntimeException("Could not get uniform location for SamplerLoc");
        }
        
        // Set the GL clear color here
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
        // Generate a Texture object for our frame
		int[] textureNames = new int[1];
		GLES20.glGenTextures(1, textureNames, 0);
		textureName = textureNames[0];
		textureBuffer = makeFloatBuffer(textureCoords);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);

		//register unchaught exception handler
		Thread.currentThread().setUncaughtExceptionHandler(activity);
		
		// TODO: Make these work again
		/*
		markerInfo.initGL(null);
		if(customRenderer != null)
			customRenderer.initGL(null);
			*/
		
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, 0, 0.0f, 0.0f, 5.0f, 0f, 1.0f, 0.0f);
	}

	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
	 */
	@Override
	public void onDrawFrame(GL10 glUnused) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glUseProgram(mProgram);
		GraphicsUtil.checkGlError("glUseProgram");
	
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);
		
		//load new preview frame as a texture, if needed
		if (frameEnqueued) {
			frameLock.lock();
			if(!isTextureInitialized) {
				initializeTexture();
			} else {
				GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, previewFrameWidth, previewFrameHeight,
						mode, GLES20.GL_UNSIGNED_BYTE, frameData);
				GraphicsUtil.checkGlError("glTexSubImage2D");
			}
			frameLock.unlock();
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			frameEnqueued = false;
		}
		
		//draw camera preview frame:
		squareBuffer.position(0);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                TRIANGLE_VERTICES_DATA_STRIDE_BYTES, squareBuffer);
        GraphicsUtil.checkGlError("glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GraphicsUtil.checkGlError("glEnableVertexAttribArray maPositionHandle");
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
        		TRIANGLE_VERTICES_UV_STRIDE_BYTES, textureBuffer);
        GraphicsUtil.checkGlError("glVertexAttribPointer maTextureHandle");
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        GraphicsUtil.checkGlError("glEnableVertexAttribArray maTextureHandle");

        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform1i(mSamplerLoc, 0);
        
		//draw camera square
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		GraphicsUtil.checkGlError("glDrawArrays");

		// TODO: Make these work again
		/*
		if(customRenderer != null)
			customRenderer.setupEnv(null);
		
		markerInfo.draw(null);
		
		if(customRenderer != null)
			customRenderer.draw(null);
		
		//take a screenshot, if desired
		if(takeScreenshot) {
			takeScreenshot = false;
			captureScreenshot(glUnused);		
		}
		*/
	}

	/* 
	 * @see android.opengl.GLSurfaceView.Renderer#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
	 */
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		aspectRatio = (float)width/(float)height;
		
		Matrix.orthoM(mProjMatrix, 0, -100.0f*aspectRatio, 100.0f*aspectRatio, -100.0f, 100.0f, 1.0f, -1.0f);
        Matrix.setIdentityM(mVMatrix, 0);
        
		square = new float[] { 	-100.0f*aspectRatio, -100.0f, -1.0f,
				 				 100.0f*aspectRatio, -100.0f, -1.0f,
				 				-100.0f*aspectRatio,  100.0f, -1.0f,
				 				 100.0f*aspectRatio,  100.0f, -1.0f };
		
		squareBuffer = makeFloatBuffer(square);		
		markerInfo.setScreenSize(width, height);
		screenHeight = height;
		screenWidth = width;
	}
	
	/**
	 * Wrapper for glReadPixels
	 * @param glUnused The unused gl10 context (this is a GLES2.0 context!)
	 * @param sb The screen buffer
	 */
	@Override
	protected void copyScreenToBuffer(GL10 glUnused, Buffer sb) {
		GLES20.glReadPixels(0,0,screenWidth,screenHeight, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, sb);
		GraphicsUtil.checkGlError("glReadPixels");
	}
	
	/**
	 * Generates and loads a localized cubemap for object defined by vertices
	 * @param n n*8 = number of triangles on front face
	 * @param ssbb a screen space bounding box
	 */
	public void generateCubemap( int n, float[] ssbb ) {
		// TODO: Write this method
		// Correct SSBB dimensions
		// Generate front face geometry
		// Generate texture coordinates for front face
		// Render front face into a cubemap texture via a framebuffer
		// Create a new face for rendering remaining textures
		// Generate remaining face texture coordinates
		// Render remaining faces to cubemap textures via framebuffers
	}
	
	private void initializeTexture() {
		byte[] frame;
		switch(mode) {
		default:
			mode = GLES20.GL_RGB;
		case GLES20.GL_RGB:
			frame = new byte[textureSize*textureSize*3];
			break;
		case GLES20.GL_LUMINANCE:
			frame = new byte[textureSize*textureSize];
			break;
		}
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, mode, textureSize,
				textureSize, 0, mode, GLES20.GL_UNSIGNED_BYTE ,
				ByteBuffer.wrap(frame));
		GraphicsUtil.checkGlError("glTexImage2D");
		isTextureInitialized = true;		
	}
	
	/**
	 * sets the mode(either GLES20.GL_RGB or GLES20.GL_LUMINANCE)
	 * @param pMode
	 */
	public void setMode(int pMode) {
		switch(pMode) {		
		case GLES20.GL_RGB:
		case GLES20.GL_LUMINANCE:
			this.mode = pMode;
			break;
		default:
			this.mode = GLES20.GL_RGB;
			break;
		}
		if(pMode != this.mode)
			isTextureInitialized = false;
	}
}

