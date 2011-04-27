package edu.dhbw.andar.pub;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL10Ext;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.GLUtils;

import edu.dhbw.andar.ARGLES20Object;
import edu.dhbw.andar.ARObject;
import edu.dhbw.andar.AndARGLES20Renderer;
import edu.dhbw.andar.AndARRenderer;
import edu.dhbw.andar.util.GraphicsUtil;

/**
 * An example of an AR object being drawn on a marker.
 * @author tobi
 *
 */
public class CustomGL20Object extends ARGLES20Object {
	
	private int maPositionHandle;
	private int maNormalHandle;
	private int muColor;
	private int muCamera;
	private int muDTex1;
	private int muDTex2;
	private static final int FLOAT_SIZE_BYTES = 4;
    private static final int VERTEX_NORMAL_DATA_STRIDE = 3 * FLOAT_SIZE_BYTES;

	public CustomGL20Object(String name, String patternName,
			double markerWidth, double[] markerCenter, AndARGLES20Renderer renderer) {
		super(name, patternName, markerWidth, markerCenter, renderer);
	}
	
	private SimpleBox box = new SimpleBox();
	
	/**
	 * Everything drawn here will be drawn directly onto the marker,
	 * as the corresponding translation matrix will already be applied.
	 */
	@Override
	public final void drawGLES20() {
		// Create a cubemap for this object from vertices
		GenerateCubemap( box.vertArray() );
		
		// Feed in Verts
		box.verts().position(0);
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                VERTEX_NORMAL_DATA_STRIDE, box.verts());
        GraphicsUtil.checkGlError("glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GraphicsUtil.checkGlError("glEnableVertexAttribArray maPositionHandle");
        
        // Feed in Normals
        box.normals().position(0);
        GLES20.glVertexAttribPointer(maNormalHandle, 3, GLES20.GL_FLOAT, false,
                VERTEX_NORMAL_DATA_STRIDE, box.normals());
        GraphicsUtil.checkGlError("glVertexAttribPointer maNormalHandle");
        GLES20.glEnableVertexAttribArray(maNormalHandle);
        GraphicsUtil.checkGlError("glEnableVertexAttribArray maNormalHandle");
       
        // Set the color (green)
        GLES20.glUniform4f(muColor, 0.0f, 1.0f, 0.0f, 1.0f);
        
        //Set the camera position
        GLES20.glUniform4f(muCamera, 0.0f, 0.0f, 0.0f, 1.0f);
        
        //Set the camera position
        GLES20.glUniform4f(muCamera, 0.0f, 0.0f, 0.0f, 1.0f);
        
        
        
        
        depthDrawGLES20();
        
        // Draw the cube faces
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 4, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 8, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 12, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 16, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 20, 4);
	    GraphicsUtil.checkGlError("glDrawArrays");
	}
	public final void depthDrawGLES20() {
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		int[] framebuffers = new int[1];
		int[] textures = new int[2];
		int h = mRenderer.screenHeight;
		int w = mRenderer.screenWidth;
		GLES20.glGenFramebuffers(1, framebuffers, 0);
		GLES20.glGenTextures(2, textures, 0);
		int fbuf = framebuffers[0];
		int texbuf1 = textures[0];
		int texbuf2 = textures[1];
		if(GLES20.glIsEnabled(GLES20.GL_TEXTURE_2D)){
			System.out.println("LOLOLOL");
		}
		else{
			System.out.println("TROLLOLOLOL");
		}
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbuf);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texbuf1);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT, w, h,0, GLES20.GL_LUMINANCE, GLES20.GL_FLOAT, null);
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, texbuf1, 0);
		GLES20.glUniform1i(muDTex1, texbuf1);
		
		GLES20.glDepthFunc(GLES20.GL_GREATER);
        // Draw the cube faces
		int e = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if(e != GLES20.GL_FRAMEBUFFER_COMPLETE){
			if(e == GLES20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT){
				System.out.println("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
			}
			else if (e == GLES20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS){
				System.out.println("GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS");
			}
			else if (e == GLES20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT){
				System.out.println("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
			}
			else if (e == GLES20.GL_FRAMEBUFFER_UNSUPPORTED){
				System.out.println("GL_FRAMEBUFFER_UNSUPPORTED");
			}
		}
		

	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	    System.out.println("Hello1");
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 4, 4);
	    System.out.println("Hello2");
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 8, 4);
	    System.out.println("Hello3");
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 12, 4);
	    System.out.println("Hello4");
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 16, 4);
	    System.out.println("Hello5");
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 20, 4);
	    System.out.println("Hello6");
	    GraphicsUtil.checkGlError("glDrawArrays");
	    System.out.println("Hello7");
	    
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texbuf2);
	    GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT, w, h,0, GLES20.GL_DEPTH_COMPONENT, GLES20.GL_FLOAT, null);
	    GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, texbuf2, 0);
	    GLES20.glUniform1i(muDTex2, texbuf2);
	    
	    GLES20.glDepthFunc(GLES20.GL_LESS);
	    // Draw the cube faces
	    box.verts().position(0);
		box.normals().position(0);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 4, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 8, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 12, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 16, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 20, 4);
	    GraphicsUtil.checkGlError("glDrawArrays");
	    
	    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	    
	}
	
	@Override
	public void initGLES20() {
		// Grab Attributes and uniforms from the loaded shader
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
        muColor = GLES20.glGetUniformLocation(mProgram, "uColor");
        GraphicsUtil.checkGlError("glGetUniformLocation uColor");
        if (muColor == -1) {
            throw new RuntimeException("Could not get uniform location for uColor");
        }
        muCamera = GLES20.glGetUniformLocation(mProgram, "uCamera");
        GraphicsUtil.checkGlError("glGetUniformLocation uCamera");
        if (muCamera == -1) {
            throw new RuntimeException("Could not get uniform location for uCamera");
        }
        muDTex1 = GLES20.glGetUniformLocation(mProgram, "uDTex1");
        GraphicsUtil.checkGlError("glGetUniformLocation uDTex1");
        if (muDTex1 == -1) {
            throw new RuntimeException("Could not get uniform location for uDTex1");
        }
        muDTex2 = GLES20.glGetUniformLocation(mProgram, "uDTex2");
        GraphicsUtil.checkGlError("glGetUniformLocation uDTex2");
        if (muDTex2 == -1) {
            throw new RuntimeException("Could not get uniform location for uDTex2");
        }
        
        
	}

	/**
	 * Set the shader program files for this object
	 */
	@Override
	public String vertexProgramPath() { return "shaders/refract.vs"; }

	@Override
	public String fragmentProgramPath() { return "shaders/refract.fs"; }
}
