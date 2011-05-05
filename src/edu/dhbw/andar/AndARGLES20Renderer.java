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
import edu.dhbw.andar.util.GraphicsDebugDraw;
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
	public GraphicsDebugDraw mDebugDraw;
	public boolean mDebug = true;
	
	// GLES 2.0 doesn't do matrix math for us for free.
    private float[] mMVPMatrix = new float[16]; // Projection*ModelView Matrix
    private float[] mProjMatrix = new float[16]; // Projection Matrix
    private float[] mVMatrix = new float[16]; // ModelView Matrix

    private int mProgram;
    private int mSamplerLoc;
	private int muMVPMatrixHandle;
	private int maPositionHandle;
	private int maTextureHandle;
	private int[] mFrameBuffers;
	
	private int mProjProgram;
	private int mProjSamplerLoc;
	private int muProjMVPMatrixHandle;
	private int maProjPositionHandle;
	private int maProjTextureHandle;
	private int mCubeMapTexture;
	private DynamicCubemap mDC;
	private int mDebugFace = -1; // -1 for no debug
	
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
		mDebugDraw = new GraphicsDebugDraw( activity );
		
		// Load shaders from assets, compile and load a program
		mProgram = GraphicsUtil.loadProgram( activity, "shaders/simpletexture.vs", "shaders/simpletexture.fs" );
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
        
        // Load the projective texture program
		mProjProgram = GraphicsUtil.loadProgram( activity, "shaders/projtexture.vs", "shaders/projtexture.fs" );
        if (mProjProgram == 0) { 
            return;
        }
        
        // Grab Attributes and uniforms from the loaded shader
        maProjPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GraphicsUtil.checkGlError("glGetAttribLocation aPosition");
        if (maProjPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maProjTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        GraphicsUtil.checkGlError("glGetAttribLocation aTextureCoord");
        if (maProjTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }
        muProjMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GraphicsUtil.checkGlError("glGetUniformLocation uMVPMatrix");
        if (muProjMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }
        mProjSamplerLoc = GLES20.glGetUniformLocation (mProgram, "sTexture");
        GraphicsUtil.checkGlError("glGetUniformLocation sTexture");
        if (mProjSamplerLoc == -1) {
        	throw new RuntimeException("Could not get uniform location for SamplerLoc");
        }
        
        // Set the GL clear color here
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		// Generate Cubemap Textures
		int[] cubemaptextures = new int[1];
		GLES20.glGenTextures(1, cubemaptextures, 0 );
		mCubeMapTexture = cubemaptextures[0];
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, mCubeMapTexture);
		GraphicsUtil.checkGlError("glBindTexture");
		for( int i = 0; i < 6; i++ ) {		
			initializeTexture( edu.dhbw.andar.Config.CUBEMAP_SIZE, GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i );
		}
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, 0);
		
		// Create a set of FrameBuffers for the cubemap
		mFrameBuffers = new int[6];
		GLES20.glGenFramebuffers(6, mFrameBuffers, 0);
		for( int i = 0; i < 6; i++ ) {
			GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[i]);
			GLES20.glFramebufferTexture2D( GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
	        		GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, mCubeMapTexture, 0 );
			GLES20.glCheckFramebufferStatus( GLES20.GL_FRAMEBUFFER );
		}
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		
		// Set up the cubemap structure
		mDC = new DynamicCubemap();
		
		// Generate a Texture object for our frame
		int[] textureNames = new int[1];
		GLES20.glGenTextures(1, textureNames, 0);
		textureName = textureNames[0];
		textureBuffer = makeFloatBuffer(textureCoords);
		
		//register unchaught exception handler
		Thread.currentThread().setUncaughtExceptionHandler(activity); 

		markerInfo.initGL(null);
		if(customRenderer != null)
			customRenderer.initGL(null);
		
		// Tell GL what to look at
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, 0, 0.0f, 0.0f, 5.0f, 0f, 1.0f, 0.0f);
	}

	/* (non-Javadoc)
	 * @see android.opengl.GLSurfaceView.Renderer#onDrawFrame(javax.microedition.khronos.opengles.GL10)
	 */
	@Override
	public void onDrawFrame(GL10 glUnused) {
		//load new preview frame as a texture, if needed
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);
		if (frameEnqueued) {
			frameLock.lock();
			if(!isTextureInitialized) {
				initializeTexture( textureSize, GLES20.GL_TEXTURE_2D );
				isTextureInitialized = true;
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
		
		if(customRenderer != null)
			customRenderer.setupEnv(null);
		
		markerInfo.predraw(null);
		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glUseProgram(mProgram);
		GraphicsUtil.checkGlError("glUseProgram");
		
		//draw camera preview frame:
		squareBuffer.position(0);
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                GraphicsUtil.TRIANGLE_VERTICES_DATA_STRIDE_BYTES, squareBuffer);
        GraphicsUtil.checkGlError("glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GraphicsUtil.checkGlError("glEnableVertexAttribArray maPositionHandle");
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
        		GraphicsUtil.TRIANGLE_VERTICES_UV_STRIDE_BYTES, textureBuffer);
        GraphicsUtil.checkGlError("glVertexAttribPointer maTextureHandle");
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        GraphicsUtil.checkGlError("glEnableVertexAttribArray maTextureHandle");

        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform1i(mSamplerLoc, 0);
        
		//draw camera square
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
		GraphicsUtil.checkGlError("glDrawArrays");
		
		// Debug a cubemap face
		if( mDebugFace != -1 ) {
			float[] projmatrix = new float[16]; // Projection Matrix
			Matrix.orthoM(projmatrix, 0, -1.0f, 5.0f, -1.0f, 5.0f, -1.0f, 1.0f);
			Matrix.multiplyMM(mMVPMatrix, 0, projmatrix, 0, mVMatrix, 0);
			GLES20.glUseProgram(mProjProgram);
			GLES20.glUniformMatrix4fv(muProjMVPMatrixHandle, 1, false, mMVPMatrix, 0);
	        GLES20.glUniform1i(mProjSamplerLoc, 0); // Use the camera texture (bound in unit zero)
			GraphicsUtil.checkGlError("glUseProgram");
			mDC.DrawFace( mDebugFace, maProjPositionHandle, maProjTextureHandle );
		}
		
		GLES20.glDisableVertexAttribArray(maPositionHandle);
		GLES20.glDisableVertexAttribArray(maTextureHandle);
		
		markerInfo.draw(null);
		
		if(customRenderer != null)
			customRenderer.draw(null);
		
		if( mDebug )
			mDebugDraw.debugDraw();
		
		//take a screenshot, if desired
		if(takeScreenshot) {
			takeScreenshot = false;
			captureScreenshot(glUnused);		
		}
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
	 * @param ssbb a screen space bounding box
	 */
	public void generateCubemap( float[] ssbb ) {
		// Grab the current viewport and program for restoration later
		int[] OldViewport = new int[4], OldProgram = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, OldViewport, 0);
        GLES20.glGetIntegerv(GLES20.GL_CURRENT_PROGRAM, OldProgram, 0);
		
		// Correct SSBB dimensions and generate new UVs based on this SSBB
	    float widthcorrection = (float) previewFrameWidth / (float) textureSize;
        float heightcorrection = (float) previewFrameHeight / (float) textureSize;
		mDC.UpdateUVs( DynamicCubemap.CorrectSSBB( ssbb ), widthcorrection, heightcorrection ); 
		
		// Set up the program used to render to the texture
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, 0); // Ensure we aren't rendering to the same texture we're using
		float[] projmatrix = new float[16]; // Projection Matrix
		Matrix.orthoM(projmatrix, 0, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
		Matrix.multiplyMM(mMVPMatrix, 0, projmatrix, 0, mVMatrix, 0);
		
		// Render to the Projective cubemap faces
		for( int i = 0; i < 4; i++ ) {
			GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[i]);
			GLES20.glViewport( 0, 0, edu.dhbw.andar.Config.CUBEMAP_SIZE, edu.dhbw.andar.Config.CUBEMAP_SIZE);
			GLES20.glClear( GLES20.GL_COLOR_BUFFER_BIT );
	        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);
			GLES20.glUseProgram(mProjProgram);
			GLES20.glUniformMatrix4fv(muProjMVPMatrixHandle, 1, false, mMVPMatrix, 0);
	        GLES20.glUniform1i(mProjSamplerLoc, 0); // Use the camera texture (bound in unit zero)
			GraphicsUtil.checkGlError("glUseProgram");
			mDC.DrawFace( i, maProjPositionHandle, maProjTextureHandle );
		}
        
		// Render to the cubemap faces
		for( int i = 4; i < 6; i++ ) {
			GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[i]);
			GLES20.glViewport( 0, 0, edu.dhbw.andar.Config.CUBEMAP_SIZE, edu.dhbw.andar.Config.CUBEMAP_SIZE);
			GLES20.glClear( GLES20.GL_COLOR_BUFFER_BIT );
	        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureName);
			GLES20.glUseProgram(mProgram);
			GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
	        GLES20.glUniform1i(mSamplerLoc, 0); // Use the camera texture (bound in unit zero)
			GraphicsUtil.checkGlError("glUseProgram");
			mDC.DrawFace( i, maPositionHandle, maTextureHandle );
		}
		
		// Unbind the framebuffer, we no longer need to render to textures.
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		
		// Ensure the newly generated cubemap is bound to the correct texture unit
		GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, mCubeMapTexture);
		
		// Bind the old program and viewport
		GLES20.glUseProgram( OldProgram[0] );
		GLES20.glViewport( OldViewport[0], OldViewport[1], OldViewport[2], OldViewport[3] );
	}
	
	private void initializeTexture( int size, int target ) {
		byte[] frame;
		switch(mode) {
		default:
			mode = GLES20.GL_RGB;
		case GLES20.GL_RGB:
			frame = new byte[size*size*3];
			break;
		case GLES20.GL_LUMINANCE:
			frame = new byte[size*size];
			break;
		}
		GLES20.glTexImage2D(target, 0, mode, size,
				size, 0, mode, GLES20.GL_UNSIGNED_BYTE ,
				ByteBuffer.wrap(frame));
		GraphicsUtil.checkGlError("glTexImage2D");		
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

