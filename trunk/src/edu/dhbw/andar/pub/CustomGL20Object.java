package edu.dhbw.andar.pub;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

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
	
	private int fbuf;
	private int texbuf1;
	private int texbuf2;
	
	private int[] framebuffers = new int[1];
	private int[] textures = new int[2];
	
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
		box.normals().position(0);
		depthDrawGLES20();
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false,
                VERTEX_NORMAL_DATA_STRIDE, box.verts());
        GraphicsUtil.checkGlError("glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GraphicsUtil.checkGlError("glEnableVertexAttribArray maPositionHandle");
        
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
        
        GLES20.glUniform1i(muDTex1, 0);
       
        GLES20.glUniform1i(muDTex2, 1);
               
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
		
		int w = mRenderer.screenWidth;
		int h = mRenderer.screenHeight;
		
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texbuf1);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT, 64, 64, 0, GLES20.GL_DEPTH_COMPONENT, GLES20.GL_UNSIGNED_SHORT, null);
		
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbuf);
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, texbuf1, 0);
		
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
		else{
			System.out.println("You're all good!");
		}
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 4, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 8, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 12, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 16, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 20, 4);
	    GraphicsUtil.checkGlError("glDrawArrays");
	    
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texbuf2);
	    GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT, 64, 64, 0, GLES20.GL_DEPTH_COMPONENT, GLES20.GL_UNSIGNED_SHORT, null);
	    GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, texbuf2, 0);
	    GLES20.glDepthFunc(GLES20.GL_LESS);
	    
	    e = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
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
		else{
			System.out.println("You're all good!");
		}
	    // Draw the cube faces
		box.normals().position(0);
		box.verts().position(0);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	    System.out.println("1?");
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 4, 4);
	    System.out.println("2?");
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 8, 4);
	    System.out.println("3?");
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 12, 4);
	    System.out.println("4?");
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 16, 4);
	    System.out.println("5?");
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 20, 4);
	    System.out.println("6?");
	    GraphicsUtil.checkGlError("glDrawArrays");
	    System.out.println("7?");
	    
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
		
        GLES20.glGenFramebuffers(1, framebuffers, 0);
		GLES20.glGenTextures(2, textures, 0);
		fbuf = framebuffers[0];
		texbuf1 = textures[0];
		texbuf2 = textures[1];
	}

	/**
	 * Set the shader program files for this object
	 */
	@Override
	public String vertexProgramPath() { return "shaders/refract.vs"; }

	@Override
	public String fragmentProgramPath() { return "shaders/refract.fs"; }
}
