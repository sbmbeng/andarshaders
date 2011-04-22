package edu.dhbw.andar.pub;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import edu.dhbw.andar.util.GraphicsUtil;

public class SimpleBox {
	private FloatBuffer box;
	private FloatBuffer normals;
	private final float[] boxverts =  {
			// FRONT
			-25.0f, -25.0f,  25.0f,
			 25.0f, -25.0f,  25.0f,
			-25.0f,  25.0f,  25.0f,
			 25.0f,  25.0f,  25.0f,
			// BACK
			-25.0f, -25.0f, -25.0f,
			-25.0f,  25.0f, -25.0f,
			 25.0f, -25.0f, -25.0f,
			 25.0f,  25.0f, -25.0f,
			// LEFT
			-25.0f, -25.0f,  25.0f,
			-25.0f,  25.0f,  25.0f,
			-25.0f, -25.0f, -25.0f,
			-25.0f,  25.0f, -25.0f,
			// RIGHT
			 25.0f, -25.0f, -25.0f,
			 25.0f,  25.0f, -25.0f,
			 25.0f, -25.0f,  25.0f,
			 25.0f,  25.0f,  25.0f,
			// TOP
			-25.0f,  25.0f,  25.0f,
			 25.0f,  25.0f,  25.0f,
			 -25.0f,  25.0f, -25.0f,
			 25.0f,  25.0f, -25.0f,
			// BOTTOM
			-25.0f, -25.0f,  25.0f,
			-25.0f, -25.0f, -25.0f,
			 25.0f, -25.0f,  25.0f,
			 25.0f, -25.0f, -25.0f,
	};
	private final float boxnormals[] =  {
			// FRONT
			0.0f, 0.0f,  1.0f,
			0.0f, 0.0f,  1.0f,
			0.0f, 0.0f,  1.0f,
			0.0f, 0.0f,  1.0f,
			// BACK
			0.0f, 0.0f,  -1.0f,
			0.0f, 0.0f,  -1.0f,
			0.0f, 0.0f,  -1.0f,
			0.0f, 0.0f,  -1.0f,
			// LEFT
			-1.0f, 0.0f,  0.0f,
			-1.0f, 0.0f,  0.0f,
			-1.0f, 0.0f,  0.0f,
			-1.0f, 0.0f,  0.0f,
			// RIGHT
			1.0f, 0.0f,  0.0f,
			1.0f, 0.0f,  0.0f,
			1.0f, 0.0f,  0.0f,
			1.0f, 0.0f,  0.0f,
			// TOP
			0.0f, 1.0f,  0.0f,
			0.0f, 1.0f,  0.0f,
			0.0f, 1.0f,  0.0f,
			0.0f, 1.0f,  0.0f,
			// BOTTOM
			0.0f, -1.0f,  0.0f,
			0.0f, -1.0f,  0.0f,
			0.0f, -1.0f,  0.0f,
			0.0f, -1.0f,  0.0f,
	};
	public SimpleBox() {
		box = GraphicsUtil.makeFloatBuffer(boxverts);
		normals = GraphicsUtil.makeFloatBuffer(boxnormals);
	}
	
	public final void draw(GL10 gl) {	
	    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	    gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
	    
	    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, box);
	    gl.glNormalPointer(GL10.GL_FLOAT,0, normals);
	    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
	    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
	    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);
	    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
	    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);
	    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	    gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
	}
	
	// Accessors
	public FloatBuffer verts() { return box; }
	public FloatBuffer normals() { return normals; }
}
