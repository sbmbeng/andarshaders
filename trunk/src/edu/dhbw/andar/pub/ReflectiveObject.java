package edu.dhbw.andar.pub;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL10Ext;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import edu.dhbw.andar.ARGLES20Object;
import edu.dhbw.andar.ARObject;
import edu.dhbw.andar.AndARGLES20Renderer;
import edu.dhbw.andar.AndARRenderer;
import edu.dhbw.andar.util.GraphicsUtil;
import edu.dhbw.andar.util.IO;

/**
 * An example of an AR object being drawn on a marker.
 * @author tobi
 *
 */
public class ReflectiveObject extends ARGLES20Object {	
	private int maPositionHandle;
	private int maNormalHandle;

	public ReflectiveObject(String name, String patternName,
			double markerWidth, double[] markerCenter, AndARGLES20Renderer renderer) {
		super(name, patternName, markerWidth, markerCenter, renderer);
	}
	
	private SimpleBox box = new SimpleBox();

	@Override
	public final void predrawGLES20() {
		// Create a cubemap for this object from vertices
		GenerateCubemap( box.vertArray() );
	}
	
	@Override
	public final void drawGLES20() {
		box.verts().position(0);
		box.normals().position(0);
		
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                GraphicsUtil.TRIANGLE_VERTICES_DATA_STRIDE_BYTES, box.verts());
        GraphicsUtil.checkGlError("CustomGL20Object glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GraphicsUtil.checkGlError("CustomGL20Object glEnableVertexAttribArray maPositionHandle");
        
        GLES20.glVertexAttribPointer(maNormalHandle, 3, GLES20.GL_FLOAT, false,
                GraphicsUtil.VERTEX_NORMAL_DATA_STRIDE, box.normals());
        GraphicsUtil.checkGlError("CustomGL20Object glVertexAttribPointer maNormalHandle");
        GLES20.glEnableVertexAttribArray(maNormalHandle);
        GraphicsUtil.checkGlError("CustomGL20Object glEnableVertexAttribArray maNormalHandle");
        
        Log.v( "CustomGLES20Object", "Drawing the final image" );
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 4, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 8, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 12, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 16, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 20, 4);
	    GraphicsUtil.checkGlError("glDrawArrays");
	    
	    GLES20.glDisableVertexAttribArray(maPositionHandle);
		GLES20.glDisableVertexAttribArray(maNormalHandle);
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
	}

	/**
	 * Set the shader program files for this object
	 */
	@Override
	public String vertexProgramPath() { return "shaders/reflect.vs"; }

	@Override
	public String fragmentProgramPath() { return "shaders/reflect.fs"; }
}
