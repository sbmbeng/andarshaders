package edu.dhbw.andar.pub;



import android.opengl.GLES20;
import android.util.Log;

import edu.dhbw.andar.ARGLES20Object;
import edu.dhbw.andar.AndARGLES20Renderer;
import edu.dhbw.andar.util.GraphicsUtil;
import com.openglesbook.common.ESShapes;

/**
 * An example of an AR object being drawn on a marker.
 * @author tobi
 *
 */
public class ReflectiveObject extends ARGLES20Object {	
	private int maPositionHandle;
	private int maNormalHandle;
	private int muCubemap;

	public ReflectiveObject(String name, String patternName,
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
        GraphicsUtil.checkGlError("CustomGL20Object glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GraphicsUtil.checkGlError("CustomGL20Object glEnableVertexAttribArray maPositionHandle");
        
        GLES20.glVertexAttribPointer(maNormalHandle, 3, GLES20.GL_FLOAT, false, 0, mSphere.getNormals());
        GraphicsUtil.checkGlError("CustomGL20Object glVertexAttribPointer maNormalHandle");
        GLES20.glEnableVertexAttribArray(maNormalHandle);
        GraphicsUtil.checkGlError("CustomGL20Object glEnableVertexAttribArray maNormalHandle");
        
        GLES20.glUniform1i( muCubemap, 1 );
        GraphicsUtil.checkGlError("CustomGL20Object glUniform1i");
        
        GLES20.glDrawElements ( GLES20.GL_TRIANGLES, mSphere.getNumIndices(), GLES20.GL_UNSIGNED_SHORT, mSphere.getIndices() );
	    GraphicsUtil.checkGlError("glDrawElements");
	    
	    GLES20.glDisableVertexAttribArray(maPositionHandle);
		GLES20.glDisableVertexAttribArray(maNormalHandle);
		
        GLES20.glDisable(GLES20.GL_CULL_FACE);
		GraphicsUtil.checkGlError("CustomGL20Object glDisableAttrib");
	}
	
	@Override
	public void initGLES20() {
		//REFLECTION: EASY MODE???
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
        
        // Generate the vertex data
        mSphere.genSphere( 20, 75.0f );
	}

	/**
	 * Set the shader program files for this object
	 */
	@Override
	public String vertexProgramPath() { return "shaders/reflect.vs"; }

	@Override
	public String fragmentProgramPath() { return "shaders/reflect.fs"; }
}
