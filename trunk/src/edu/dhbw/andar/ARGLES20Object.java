package edu.dhbw.andar;

import javax.microedition.khronos.opengles.GL10;

import edu.dhbw.andar.util.GraphicsUtil;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

/**
 * This class defines an ARObject which is used with the GLES20 renderer.
 * In order to use an AndARGLES20Renderer, you need to use ARGLES20Objects with it.  
 * You cannot mix and match these classes.
 * @author Griffin Milsap
 *
 */
public abstract class ARGLES20Object extends ARObject {
	protected AndARGLES20Renderer mRenderer;
	protected int mProgram;
	protected int muMVPMatrixHandle;
	protected float[] mMVPMatrix = new float[16]; // Projection*ModelView Matrix
	
	public ARGLES20Object(String name, String patternName, double markerWidth, double[] markerCenter, AndARGLES20Renderer renderer) {
		super(name, patternName, markerWidth, markerCenter);
		mRenderer = renderer;
		mProgram = 0;
		muMVPMatrixHandle = 0;
	}
	
	/**
	 * Allow the program to draw without dealing with transformations
	 * @param glUnused an unused 1.0 gl context
	 */
	@Override
	public synchronized void draw( GL10 glUnused ) {
		if(!initialized) {
			init(glUnused);
			initialized = true;
		}
		// Ensure we're using the program we need
		GLES20.glUseProgram( mProgram );
		
		if( glCameraMatrixBuffer != null) {
			// Transform to where the marker is
			Matrix.multiplyMM(mMVPMatrix, 0, glCameraMatrix, 0, glMatrix, 0);
			GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			GraphicsUtil.checkGlError("glUniformMatrix4fv muMVPMatrixHandle");
		}
		
		// Let the object draw
		drawGLES20();
	}

	/**
	 * Initialize the shader and transform matrix attributes
	 * @param glUnused an unused 1.0 gl context
	 */
	@Override
	public void init( GL10 glUnused ) {
		setProgram( vertexProgramPath(), fragmentProgramPath() );
		initGLES20();
	}
	
	/**
	 * Compile and load a vertex and fragment program for this object
	 * @param vspath Path relative to the "assets" directory which denotes location of the vertex shader
	 * @param fspath Path relative to the "assets" directory which denotes location of the fragment shader
	 */
	public void setProgram( String vspath, String fspath )
	{
		// Load and compile the program, grab the attribute for transformation matrix
		mProgram = GraphicsUtil.loadProgram( mRenderer.activity, vspath, fspath );
		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GraphicsUtil.checkGlError("ARGLES20Object glGetUniformLocation uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Requested shader does not have a uniform named uMVPMatrix");
        }
	}
	
	/**
	 * Calculates a screen space bounding box from an axis aligned bounding box
	 * @param aabb [minx][miny][minz][maxx][maxy][maxz] -- see GraphicsUtil.calcAABB()
	 * @return normalized screen space bounding box, [minx][miny][maxx][maxy]
	 */
	public float[] calcSSBB( float[] aabb ) {
		// http://www.opengl.org/sdk/docs/man/xhtml/gluProject.xml
		float[] t = { 0.0f, 0.0f, 0.0f, 1.0f };
		float[] ssbb = { Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE };
		for( int i = 0; i < 8; i++ )
		{
			// Test all points in the aabb
			t[0] = ( ( i / 4 ) % 2 == 0 ) ? aabb[0] : aabb[3];
			t[1] = ( ( i / 2 ) % 2 == 0 ) ? aabb[1] : aabb[4];
			t[2] = ( i % 2 == 0 ) ? aabb[2] : aabb[5];
			t[3] = 1.0f;
			
			// Project the point by the ModelView matrix then the Projection Matrix
			Matrix.multiplyMV(t, 0, glMatrix, 0, t, 0);
			Matrix.multiplyMV(t, 0, glCameraMatrix, 0, t, 0);
			
			// Save mins and maxs
			float x = ( ( t[0] / t[3] ) + 1.0f ) * 0.5f;
			float y = ( ( t[1] / t[3] ) + 1.0f ) * 0.5f;
			if( x < ssbb[0] ) ssbb[0] = x;
			if( x > ssbb[2] ) ssbb[2] = x;
			if( y < ssbb[2] ) ssbb[1] = y;
			if( y > ssbb[3] ) ssbb[3] = y;
		}
		
		// Clamp SSBB from 0.0 to 1.0
		if( ssbb[0] < 0.0f ) ssbb[0] = 0.0f; if( ssbb[0] > 1.0f ) ssbb[0] = 1.0f;
		if( ssbb[1] < 0.0f ) ssbb[1] = 0.0f; if( ssbb[1] > 1.0f ) ssbb[1] = 1.0f;
		if( ssbb[2] < 0.0f ) ssbb[2] = 0.0f; if( ssbb[2] > 1.0f ) ssbb[2] = 1.0f;
		if( ssbb[3] < 0.0f ) ssbb[3] = 0.0f; if( ssbb[3] > 1.0f ) ssbb[3] = 1.0f;
		return ssbb;
	}

	/**
	 * Generates a cubemap for this object and puts it in graphics memory
	 * @param vertices A float array of vertices: [x][y][z][x][y][z]...
	 */
	public void GenerateCubemap( float[] vertices ) {
		float[] aabb = GraphicsUtil.calcAABB( vertices );
		//Log.v("ARGLES20Object", "AABB: Min: ( " + aabb[0] + ", " + aabb[1] + ", " + aabb[2] + " ), Max: ( " + aabb[3] + ", " + aabb[4] + ", " + aabb[5] + " ) " );
		float[] ssbb = calcSSBB( aabb );
		//Log.v("ARGLES20Object", "SSBB: Min: ( " + ssbb[0] + ", " + ssbb[1] + " ), Max: ( " + ssbb[2] + ", " + ssbb[3] + ") " );
		mRenderer.generateCubemap( ssbb );
		GLES20.glUseProgram( mProgram );
	}
	
	
	/**
	 * Implement this method and setup GL to render your object here
	 */
	public abstract void initGLES20();
	
	/**
	 * Implement this method and draw your object within it
	 */
	public abstract void drawGLES20();
	
	/**
	 * Return the path relative to the "assets" directory for the vertex program
	 */
	public abstract String vertexProgramPath();
	
	/**
	 * Return the path relative to the "assets" directory for the fragment program
	 */
	public abstract String fragmentProgramPath();
}
