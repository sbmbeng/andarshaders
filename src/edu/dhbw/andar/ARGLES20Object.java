package edu.dhbw.andar;

import javax.microedition.khronos.opengles.GL10;

import edu.dhbw.andar.util.GraphicsUtil;

import android.opengl.GLES20;
import android.opengl.Matrix;

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
		// TODO: Calculate this shit. using the modelview and projection matrices
		// http://www.opengl.org/sdk/docs/man/xhtml/gluProject.xml
		// vector4 t;
		// t = view_matrix * aabb; // v3 * m(4x3)
		// t = proj_matrix * t; // v4 * m(4x4)
	    // p_win.x = win_sizex * (t.x / t.w + 1.0f) * 0.5f;
	    // p_win.y = win_sizey * (t.y / t.w + 1.0f) * 0.5f;
		// Grab Min(XY) and Max(XY)
		return aabb;
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
