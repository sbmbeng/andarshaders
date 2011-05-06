package edu.dhbw.andar.pub;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import edu.dhbw.andar.ARGLES20Object;
import edu.dhbw.andar.AndARGLES20Renderer;
import edu.dhbw.andar.util.GraphicsUtil;
import com.openglesbook.common.ESShapes;

/**
 * A Refractive Object
 * @author Griffin Milsap
 *
 */
public class RefractiveObject extends ARGLES20Object {	
	private int maPositionHandle;
	private int maNormalHandle;
	private int muCubemap;
	//private int muNormalMatHandle;
	//private float[] mNormalMat = new float[16];

	public RefractiveObject(String name, String patternName,
			double markerWidth, double[] markerCenter, AndARGLES20Renderer renderer) {
		super(name, patternName, markerWidth, markerCenter, renderer);
	}
	
	private ESShapes mSphere = new ESShapes();

	@Override
	public final void predrawGLES20() {
		// Create a cubemap for this object from vertices
		GenerateCubemap( mSphere.getVertexData() );
	}
	
	@Override
	public final void drawGLES20() {
		GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
		
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mSphere.getVertices());
        GraphicsUtil.checkGlError("RefractiveObject glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GraphicsUtil.checkGlError("RefractiveObject glEnableVertexAttribArray maPositionHandle");
        
        GLES20.glVertexAttribPointer(maNormalHandle, 3, GLES20.GL_FLOAT, false, 0, mSphere.getNormals());
        GraphicsUtil.checkGlError("RefractiveObject glVertexAttribPointer maNormalHandle");
        GLES20.glEnableVertexAttribArray(maNormalHandle);
        GraphicsUtil.checkGlError("RefractiveObject glEnableVertexAttribArray maNormalHandle");
        
        GLES20.glUniform1i( muCubemap, 1 );
        GraphicsUtil.checkGlError("RefractiveObject glUniform1i");
        
        // Calculate the normal matrix
        /*
        Matrix.invertM(mNormalMat, 0, mMVMatrix, 0);
        Matrix.transposeM(mNormalMat, 0, mNormalMat, 0);
        GLES20.glUniformMatrix4fv(muNormalMatHandle, 1, false, mNormalMat, 0);
        */
        
        GLES20.glDrawElements ( GLES20.GL_TRIANGLES, mSphere.getNumIndices(), GLES20.GL_UNSIGNED_SHORT, mSphere.getIndices() );
	    GraphicsUtil.checkGlError("glDrawElements");
	    
	    GLES20.glDisableVertexAttribArray(maPositionHandle);
		GLES20.glDisableVertexAttribArray(maNormalHandle);
		
        GLES20.glDisable(GLES20.GL_CULL_FACE);
		GraphicsUtil.checkGlError("RefractiveObject glDisableAttrib");
	}
	
	@Override
	public void initGLES20() {
		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GraphicsUtil.checkGlError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maNormalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
        GraphicsUtil.checkGlError("glGetAttribLocation aNormal");
        if (maNormalHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aNormal");
        }
        muCubemap = GLES20.glGetUniformLocation(mProgram, "uCubemap");
        GraphicsUtil.checkGlError("glGetUniformLocation uCubemap");
        if (muCubemap == -1) {
            throw new RuntimeException("Could not get uniform location for uCubemap");
        }
        /*
        muNormalMatHandle = GLES20.glGetUniformLocation(mProgram, "uNormalMatrix");
        GraphicsUtil.checkGlError("glGetUniformLocation uNormalMatrix");
        if (muNormalMatHandle == -1) {
            throw new RuntimeException("Could not get uniform location for uNormalMatrix");
        }
        */
        
        // Generate the vertex data
        mSphere.genSphere( 20, 75.0f );
	}

	/**
	 * Set the shader program files for this object
	 */
	@Override
	public String vertexProgramPath() { return "shaders/refract.vs"; }

	@Override
	public String fragmentProgramPath() { return "shaders/refract.fs"; }
}
