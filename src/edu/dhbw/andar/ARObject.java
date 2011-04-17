/**
	Copyright (C) 2009,2010  Tobias Domhan

    This file is part of AndOpenGLCam.

    AndObjViewer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AndObjViewer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with AndObjViewer.  If not, see <http://www.gnu.org/licenses/>.
 
 */
package edu.dhbw.andar;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import edu.dhbw.andar.util.GraphicsUtil;

/**
 * 
 * @author tobi
 *
 */
public abstract class ARObject {
	/**
	 * Is this object visible? -> is the marker belonging to this object visible?
	 */
	private boolean visible = false;
	private String name;
	private String patternName;
	private double markerWidth;
	private double[] center;
	//this object must be locked while altering the glMatrix
	private float[] glMatrix = new float[16];
	protected static float[] glCameraMatrix = new float[16];
	private FloatBuffer glMatrixBuffer;
	protected static FloatBuffer glCameraMatrixBuffer;
	
	//this object must be locked while altering the transMat
	private double[] transMat = new double[16];//[3][4] array
	private int id;
	private boolean initialized = false;
	
	/**
	 * Create a new AR object.
	 * @param name the name of the the object, an arbitrary string
	 * @param patternName the file name of the pattern(the file must reside in the res/raw folder)
	 * @param markerWidth
	 * @param markerCenter
	 */
	public ARObject(String name, String patternName, double markerWidth, double[] markerCenter) {
		this.name = name;
		this.patternName = patternName;
		this.markerWidth = markerWidth;
		if(markerCenter.length == 2) {
			this.center = markerCenter;
		} else {
			this.center = new double[]{0,0};
		}
		glMatrixBuffer = GraphicsUtil.makeFloatBuffer(glMatrix);		
	}
	
	
	
	
	public double getMarkerWidth() {
		return markerWidth;
	}




	public double[] getCenter() {
		return center;
	}




	public int getId() {
		return id;
	}




	protected void setId(int id) {
		this.id = id;
	}

	



	public String getPatternName() {
		return patternName;
	}


	/**
	 * 
	 * @return Is this object visible? -> is the marker belonging to this object visible?
	 */
	public boolean isVisible() {
		return visible;
	}


	/**
	 * Get the current translation matrix.
	 * @return
	 */
	public synchronized double[] getTransMatrix() {
		return transMat;
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
	 * Do OpenGL stuff.
	 * Everything draw here will be drawn directly onto the marker.
	 * TODO replace wrap by real floatbuffer
	 * @param gl
	 */
	public synchronized void draw(GL10 gl, AndARRenderer renderer) {
		if(!initialized) {
			init(gl, renderer);
			initialized = true;
		}
		if( glCameraMatrixBuffer != null) {
			glMatrixBuffer.put(glMatrix);
			glMatrixBuffer.position(0);
			
			//argDrawMode3D
			gl.glMatrixMode(GL10.GL_MODELVIEW);
		    gl.glLoadIdentity();
		    //argDraw3dCamera
		    gl.glMatrixMode(GL10.GL_PROJECTION);
		    gl.glLoadMatrixf( glCameraMatrixBuffer );
		    
		    gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadMatrixf(glMatrixBuffer);
		}
	}
	
	public abstract void init(GL10 gl, AndARRenderer renderer); 
	
}
