package edu.dhbw.andar.pub;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL10Ext;

import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.Matrix;

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
	private int muViewport;
	
	private int fbuf;
	private int texbuf1;
	private int texbuf2;
	private int colorbuf;
	
	private int[] framebuffers = new int[1];
	private int[] textures = new int[3];
	
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
	public final void drawSetup(){
		// Create a cubemap for this object from vertices
		GenerateCubemap( box.vertArray() );
		
		// Feed in Verts
		box.verts().position(0);
		box.normals().position(0);
		
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
       
        
        int w = mRenderer.screenWidth;
		int h = mRenderer.screenHeight;
		GLES20.glUniform2f(muViewport, w, h);
        GLES20.glUniform4f(muColor, 0.0f, 1.0f, 0.0f, 1.0f);
        GLES20.glUniform4f(muCamera, 0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glUniform1i(muDTex1, 0);
        GLES20.glUniform1i(muDTex2, 1);
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	}
	public final void drawCleanup(){
		GLES20.glDisableVertexAttribArray(maPositionHandle);
		GLES20.glDisableVertexAttribArray(maNormalHandle);
	}
	
	@Override
	public final void drawGLES20() {
		drawSetup();
		int w = mRenderer.screenWidth;
		int h = mRenderer.screenHeight;
		colorTexture(colorbuf, w, h, 2);
		GLES20.glDisable(GLES20.GL_CULL_FACE);
		GLES20.glDepthFunc(GLES20.GL_GREATER);
        depthTexture(texbuf1, w, h, 0);
        GLES20.glDepthFunc(GLES20.GL_LESS);
        depthTexture(texbuf2, w, h, 1);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        
        // Draw the cube faces
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 4, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 8, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 12, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 16, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 20, 4);
	    GraphicsUtil.checkGlError("glDrawArrays");
	    
	    drawCleanup();
	    
	}
	public final void colorTexture(int buffer, int w, int h, int texture) {
		GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, buffer);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, w, h, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5, null);
		
		
		// bind the framebuffer
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbuf);
		// specify texture as color attachment
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, buffer, 0);
	}
	public final void depthTexture(int buffer, int w, int h, int texture) {
		
		GLES20.glClearDepthf(1.f);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
		
		int[] oldViewport = new int[4];
		GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, oldViewport, 0);
		
		GLES20.glViewport(0,0,w,h);
		if(texture == 0)GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		else GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, buffer);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT, w, h, 0, GLES20.GL_DEPTH_COMPONENT, GLES20.GL_UNSIGNED_SHORT, null);
		
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbuf);
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, buffer, 0);
        // Draw the cube faces
		System.out.println(GLES20.glGetError());
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 4, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 8, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 12, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 16, 4);
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 20, 4);
	    GraphicsUtil.checkGlError("glDrawArrays");
	    
	    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
	    GLES20.glViewport(oldViewport[0],oldViewport[1],oldViewport[2],oldViewport[3]);
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
        muViewport = GLES20.glGetUniformLocation(mProgram, "uViewport");
        GraphicsUtil.checkGlError("glGetUniformLocation uViewport");
        if (muViewport == -1) {
            throw new RuntimeException("Could not get uniform location for uViewport");
        }
		
        GLES20.glGenFramebuffers(1, framebuffers, 0);
		GLES20.glGenTextures(3, textures, 0);
		fbuf = framebuffers[0];
		texbuf1 = textures[0];
		texbuf2 = textures[1];
		colorbuf = textures[2];
	}

	/**
	 * Set the shader program files for this object
	 */
	@Override
	public String vertexProgramPath() { return "shaders/refract.vs"; }

	@Override
	public String fragmentProgramPath() { return "shaders/refract.fs"; }
}
