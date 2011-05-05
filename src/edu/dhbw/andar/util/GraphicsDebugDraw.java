package edu.dhbw.andar.util;

import java.nio.FloatBuffer;
import java.util.Vector;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.Matrix;
/**
 * Simple class for debug drawing of objects onto the screen
 * @author Griffin Milsap
 *
 */
public class GraphicsDebugDraw
{
	private int mProgram;
	private float[] mMVMatrix = new float[16];
	private float[] mPMatrix = new float[16];
	
	private int muMVMatrixHandle;
	private int muPMatrixHandle;
	private int maPositionHandle;
	private int muColor;
	
	Vector<FloatBuffer> mBuffers = new Vector<FloatBuffer>();

    public GraphicsDebugDraw( Activity activity ) {
    	// Load and compile the program, grab the attribute for transformation matrix
		mProgram = GraphicsUtil.loadProgram( activity, "shaders/debugshader.vs", "shaders/debugshader.fs" );
		muMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVMatrix");
        GraphicsUtil.checkGlError("GraphicsDebugDraw glGetUniformLocation uMVMatrix");
        if (muMVMatrixHandle == -1) {
            throw new RuntimeException("Requested shader does not have a uniform named uMVMatrix");
        }
        muPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uPMatrix");
        GraphicsUtil.checkGlError("GraphicsDebugDraw glGetUniformLocation uPMatrix");
        if (muPMatrixHandle == -1) {
            throw new RuntimeException("Requested shader does not have a uniform named uPMatrix");
        } 
    	maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GraphicsUtil.checkGlError("GraphicsDebugDraw glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("DebugShader missing attribute aPosition");
        }
        muColor = GLES20.glGetUniformLocation(mProgram, "uColor");
        GraphicsUtil.checkGlError("GraphicsDebugDraw glGetUniformLocation uColor");
        if (muColor == -1) {
            throw new RuntimeException("Could not get uniform location for uColor");
        }
        
        Matrix.setIdentityM(mMVMatrix, 0);
        Matrix.orthoM(mPMatrix, 0, 0.0f, 1.0f, 0.0f, 1.0f, -1.0f, 1.0f);
    }
    
    public void debugTriangleStrip( float[] verts ) {
    	FloatBuffer buf = GraphicsUtil.makeFloatBuffer(verts);
    	buf.position( 0 );
    	mBuffers.add(buf);
    }
    
    public void debugDraw() {
    	// Grab the current viewport and program for restoration later
		int[] OldProgram = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_CURRENT_PROGRAM, OldProgram, 0);
        
        GLES20.glUseProgram( mProgram );
        
        GLES20.glUniformMatrix4fv(muMVMatrixHandle, 1, false, mMVMatrix, 0);
		GraphicsUtil.checkGlError("glUniformMatrix4fv muMVMatrixHandle");
		GLES20.glUniformMatrix4fv(muPMatrixHandle, 1, false, mPMatrix, 0);
		GraphicsUtil.checkGlError("glUniformMatrix4fv muPMatrixHandle");
        GLES20.glUniform4f( muColor, 0.0f, 1.0f, 0.0f, 0.5f );
        GraphicsUtil.checkGlError("GraphicsDebugDraw glUniform4f");
        
        // Draw the objects
        for( int i = 0; i < mBuffers.size(); i++ )
        {
	        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
	                GraphicsUtil.TRIANGLE_VERTICES_DATA_STRIDE_BYTES, mBuffers.elementAt(i) );
	        GraphicsUtil.checkGlError("GraphicsDebugDraw glVertexAttribPointer maPosition");
	        GLES20.glEnableVertexAttribArray(maPositionHandle);
	        GraphicsUtil.checkGlError("GraphicsDebugDraw glEnableVertexAttribArray maPositionHandle");
	        
	        GLES20.glDrawArrays( GLES20.GL_TRIANGLE_STRIP, 0, ( mBuffers.elementAt(i).capacity() / 3 ) );
	        mBuffers.elementAt(i).clear();
        }
        
        // Clean out the draw queue
        mBuffers.clear();
        
        // Cleanup
        GLES20.glDisableVertexAttribArray(maPositionHandle);
		GLES20.glUseProgram( OldProgram[0] );
    }
}