package edu.dhbw.andar;

import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.util.Log;

import edu.dhbw.andar.util.GraphicsUtil;

public class DynamicCubemap {
	
	private float[] mFrontFaceVerts = new float[ edu.dhbw.andar.Config.CUBEMAP_QUALITY * 8 * 3 * 3 ];
	private float[] mFrontFaceUVs = new float[ edu.dhbw.andar.Config.CUBEMAP_QUALITY * 8 * 3 * 2 ];
	private float[] mOtherFaceVerts = new float[] { 
			-1.0f, -1.0f, 0.0f,
			 1.0f, -1.0f, 0.0f,
			-1.0f,  1.0f, 0.0f,
			 1.0f,  1.0f, 0.0f };
	private float[][] mOtherFaceUVs = {
			{ // Positive X
				0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 1.0f,
				0.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f,
			},
			{ // Negative X
				0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 1.0f,
				0.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f,
			},
			{ // Positive Y
				0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 1.0f,
				0.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f,
			},
			{ // Negative Y
				0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 1.0f,
				0.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f,
			},
			{ // Positive Z
				0.0f, 0.0f,
				1.0f, 0.0f, 
				0.0f, 1.0f,
				1.0f, 1.0f
			}
		};
	private FloatBuffer mFrontFaceVBuffer;
	private FloatBuffer mFrontFaceTBuffer;
	private FloatBuffer mOtherFaceVBuffer;
	private FloatBuffer[] mOtherFaceTBuffers = new FloatBuffer[5];
	private static float PERCENT_ENLARGE = 0.05f;
	private static float PERCENT_BORDER = 0.15f;
	
	public DynamicCubemap() {
		Log.v("DynamicCubemap", "Initializing Vertices!");
		InitVertices();
		Log.v("DynamicCubemap", "Initialized Vertices!");
		mFrontFaceTBuffer = GraphicsUtil.makeFloatBuffer( mFrontFaceUVs );
		for( int i = 0; i < 5; i++ ) {
			mOtherFaceTBuffers[i] = GraphicsUtil.makeFloatBuffer( mOtherFaceUVs[i] );
		}
	}
	
	private void InitVertices() {
		int n = edu.dhbw.andar.Config.CUBEMAP_QUALITY;
		int vidx = 0;
		float vw = 1.0f / ( float ) n;
		
        // Zone 1
		Log.v("DynamicCubemap", "Initializing Zone 1");
        for( int i = 0; i < n; i++ ) {
        	mFrontFaceVerts[ vidx++ ] = ( vw * i ) - 1.0f;
        	mFrontFaceVerts[ vidx++ ] = 1.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	
        	mFrontFaceVerts[ vidx++ ] = ( vw * ( i + 1 ) ) - 1.0f;
        	mFrontFaceVerts[ vidx++ ] = 1.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        }
        
        // Zone 3
        Log.v("DynamicCubemap", "Initializing Zone 3");
        for( int i = 0; i < n; i++ ) {
        	mFrontFaceVerts[ vidx++ ] = ( vw * i ) - 1.0f;
        	mFrontFaceVerts[ vidx++ ] = -1.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	
        	mFrontFaceVerts[ vidx++ ] = ( vw * ( i + 1 ) ) - 1.0f;
        	mFrontFaceVerts[ vidx++ ] = -1.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        }
        
        // Zone 2
        Log.v("DynamicCubemap", "Initializing Zone 2");
        for( int i = 0; i < n; i++ ) {
        	mFrontFaceVerts[ vidx++ ] = ( vw * i );
        	mFrontFaceVerts[ vidx++ ] = 1.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	
        	mFrontFaceVerts[ vidx++ ] = ( vw * ( i + 1 ) );
        	mFrontFaceVerts[ vidx++ ] = 1.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        }
        
        // Zone 4
        Log.v("DynamicCubemap", "Initializing Zone 4");
        for( int i = 0; i < n; i++ ) {
        	mFrontFaceVerts[ vidx++ ] = ( vw * i );
        	mFrontFaceVerts[ vidx++ ] = -1.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	
        	mFrontFaceVerts[ vidx++ ] = ( vw * ( i + 1 ) );
        	mFrontFaceVerts[ vidx++ ] = -1.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        }
        
        // Zone 6
        Log.v("DynamicCubemap", "Initializing Zone 6");
        for( int i = 0; i < n; i++ ) {
        	mFrontFaceVerts[ vidx++ ] = -1.0f;
        	mFrontFaceVerts[ vidx++ ] = ( vw * i ) - 1.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	
        	mFrontFaceVerts[ vidx++ ] = -1.0f;
        	mFrontFaceVerts[ vidx++ ] = ( vw * ( i + 1 ) ) - 1.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        }
        
        // Zone 8
        Log.v("DynamicCubemap", "Initializing Zone 8");
        for( int i = 0; i < n; i++ ) {
        	mFrontFaceVerts[ vidx++ ] = 1.0f;
        	mFrontFaceVerts[ vidx++ ] = ( vw * i ) - 1.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	
        	mFrontFaceVerts[ vidx++ ] = 1.0f;
        	mFrontFaceVerts[ vidx++ ] = ( vw * ( i + 1 ) ) - 1.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        }
        
        // Zone 5
        Log.v("DynamicCubemap", "Initializing Zone 5");
        for( int i = 0; i < n; i++ ) {
        	mFrontFaceVerts[ vidx++ ] = -1.0f;
        	mFrontFaceVerts[ vidx++ ] = ( vw * i );
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	
        	mFrontFaceVerts[ vidx++ ] = -1.0f;
        	mFrontFaceVerts[ vidx++ ] = ( vw * ( i + 1 ) );
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        }
        
        // Zone 7
        Log.v("DynamicCubemap", "Initializing Zone 7");
        for( int i = 0; i < n; i++ ) {
        	mFrontFaceVerts[ vidx++ ] = 1.0f;
        	mFrontFaceVerts[ vidx++ ] = ( vw * i );
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	
        	mFrontFaceVerts[ vidx++ ] = 1.0f;
        	mFrontFaceVerts[ vidx++ ] = ( vw * ( i + 1 ) );
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        	mFrontFaceVerts[ vidx++ ] = 0.0f;
        }
        
        // Make a float buffer for the front face
        mFrontFaceVBuffer = GraphicsUtil.makeFloatBuffer( mFrontFaceVerts );
        
        // Other faces
		mOtherFaceVBuffer = GraphicsUtil.makeFloatBuffer( mOtherFaceVerts );
	}
	
	/**
	 * Updates UV coordinates for all faces of the cubemap
	 * @param ssbb A Corrected screen space bounding box
	 * @param uCorrection A Correction to be multiplied by the U coordinate
	 * @param vCorrection A Correction to be multiplied by the V coordinate
	 */
	public void UpdateUVs( float[] ssbb, float uCorrection, float vCorrection ) {
        int tidx = 0;
        float n = edu.dhbw.andar.Config.CUBEMAP_QUALITY;
        float width = ssbb[2] - ssbb[0];
		float height = ssbb[3] - ssbb[1];
		float cx = ssbb[0] + ( width / 2.0f );
		float cy = ssbb[1] + ( height / 2.0f );
        float ow = cx / ( float )( n + 1 );
        float iw = ( ( ssbb[0] / 2.0f ) + ( cx - ssbb[0] ) ) / ( float ) n;
        float ac = ssbb[3] + ( ( 1.0f - ssbb[3] ) / 2.0f );
        float is = ssbb[0] / 2.0f;
        
        // Zone 1
        for( int i = 0; i < n; i++ ) {
        	mFrontFaceUVs[ tidx++ ] = ( is + ( iw * i ) ) * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ac * vCorrection;
        	
        	mFrontFaceUVs[ tidx++ ] = ( ow * ( i + 1 ) ) * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = 1.0f * vCorrection;
        	
        	mFrontFaceUVs[ tidx++ ] = ( is + ( iw * ( i + 1) ) ) * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ac * vCorrection;
        }
        
        // Zone 3
        ac = ssbb[1] / 2.0f;
        for( int i = 0; i < n; i++ ) {
        	mFrontFaceUVs[ tidx++ ] = ( is + ( iw * i ) ) * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ac * vCorrection;
        	
        	mFrontFaceUVs[ tidx++ ] = ( is + ( iw * ( i + 1) ) ) * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ac * vCorrection;
        	
        	mFrontFaceUVs[ tidx++ ] = ( ow * ( i + 1 ) ) * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = 0.0f * vCorrection;
        }
        
        // Zone 2
        ow = ( 1.0f - cx ) / ( float ) ( n + 1 ); 
        iw = ( 1.0f - cx - ( ( 1.0f - ssbb[2] ) / 2.0f ) ) / ( float ) n;
        ac = ssbb[3] + ( ( 1.0f - ssbb[3] ) / 2.0f );
        for( int i = 0; i < n; i++ ) {
        	mFrontFaceUVs[ tidx++ ] = ( cx + ( iw * i ) ) * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ac * vCorrection;
        	
        	mFrontFaceUVs[ tidx++ ] = ( cx + ( ow * ( i + 1 ) ) ) * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = 1.0f * vCorrection;
        	
        	mFrontFaceUVs[ tidx++ ] = ( cx + ( iw * ( i + 1 ) ) ) * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ac * vCorrection;
        }
        
        // Zone 4
        ac = ssbb[1] / 2.0f;
        for( int i = 0; i < n; i++ ) {
        	mFrontFaceUVs[ tidx++ ] = ( cx + ( iw * i ) ) * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ac * vCorrection;
        	
        	mFrontFaceUVs[ tidx++ ] = ( cx + ( iw * ( i + 1) ) ) * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ac * vCorrection;

        	mFrontFaceUVs[ tidx++ ] = ( cx + ( ow * ( i + 1 ) ) ) * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = 0.0f * vCorrection;
        }
        
        // Zone 6
        ow = cy / ( float )( n + 1 );
        iw = ( ( ssbb[1] / 2.0f ) + ( cy - ssbb[1] ) ) / ( float ) n;
        ac = ssbb[0] / 2.0f;
        is = ssbb[1] / 2.0f;
        for( int i = 0; i < n; i++ ) {
        	mFrontFaceUVs[ tidx++ ] = ac * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ( is + ( iw * i ) ) * vCorrection;
        	
        	mFrontFaceUVs[ tidx++ ] = ac * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ( is + ( iw * ( i + 1) ) ) * vCorrection;
        	
        	mFrontFaceUVs[ tidx++ ] = 0.0f * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ( ow * ( i + 1 ) ) * vCorrection;
        }
        
        // Zone 8
        ac = ssbb[2] + ( ( 1.0f - ssbb[2] ) / 2.0f );
        for( int i = 0; i < n; i++ ) {
        	mFrontFaceUVs[ tidx++ ] = ac * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ( is + ( iw * i ) ) * vCorrection;
        	
        	mFrontFaceUVs[ tidx++ ] = 1.0f * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ( ow * ( i + 1 ) ) * vCorrection;
        	
        	mFrontFaceUVs[ tidx++ ] = ac * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ( is + ( iw * ( i + 1) ) ) * vCorrection;
        }
        
        // Zone 5
        ow = ( 1.0f - cy ) / ( float ) ( n + 1 ); 
        iw = ( 1.0f - cy - ( ( 1.0f - ssbb[3] ) / 2.0f ) ) / ( float ) n;
        ac = ssbb[0] / 2.0f;
        for( int i = 0; i < n; i++ ) {
        	mFrontFaceUVs[ tidx++ ] = ac * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ( cy + ( iw * i ) ) * vCorrection;
        	
        	mFrontFaceUVs[ tidx++ ] = ac * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ( cy + ( iw * ( i + 1 ) ) ) * vCorrection;
        	
        	mFrontFaceUVs[ tidx++ ] = 0.0f * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ( cy + ( ow * ( i + 1 ) ) ) * vCorrection;
        }
        
        // Zone 7
        ac = ssbb[2] + ( ( 1.0f - ssbb[2] ) / 2.0f );
        for( int i = 0; i < n; i++ ) {
        	mFrontFaceUVs[ tidx++ ] = ac * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ( cy + ( iw * i ) )  * vCorrection;
        	
        	mFrontFaceUVs[ tidx++ ] = ac * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ( cy + ( iw * ( i + 1 ) ) ) * vCorrection;
        	
        	mFrontFaceUVs[ tidx++ ] = 1.0f * uCorrection;
        	mFrontFaceUVs[ tidx++ ] = ( cy + ( ow * ( i + 1 ) ) ) * vCorrection;
        }
        
        // Update the TBuffer
        mFrontFaceTBuffer.clear();
        mFrontFaceTBuffer.put( mFrontFaceUVs );
        
        // Update all other face UV Buffers
   
        // Perspective Calculations first
        float ay = ( ssbb[3] + ( ( 1.0f - ssbb[3] ) / 2.0f ) );
        float by = ( ssbb[1] / 2.0f );
        float lw = ay - by;
        float q = lw / ( ssbb[3] - ssbb[1] );
        
        // Positive X (Left)
        mOtherFaceUVs[0][0]  = ssbb[0] * uCorrection;
        mOtherFaceUVs[0][1]  = ssbb[1] * vCorrection;
        mOtherFaceUVs[0][2]  = 1.0f;
        mOtherFaceUVs[0][3]  = ( ssbb[0] / 2.0f ) * uCorrection;
        mOtherFaceUVs[0][4]  = ( ssbb[1] / 2.0f ) * vCorrection;
        mOtherFaceUVs[0][5]  = 1.0f; // q
        mOtherFaceUVs[0][6]  = ssbb[0] * uCorrection;
        mOtherFaceUVs[0][7]  = ssbb[3] * vCorrection;
        mOtherFaceUVs[0][8]  = 1.0f;
        mOtherFaceUVs[0][9]  = ( ssbb[0] / 2.0f ) * uCorrection;
        mOtherFaceUVs[0][10] = ( ssbb[3] + ( ( 1.0f - ssbb[3] ) / 2.0f ) ) * vCorrection;
        mOtherFaceUVs[0][11] = 1.0f; // q
        
        // Negative X (Right)
        mOtherFaceUVs[1][0]  = ( ssbb[2] + ( ( 1.0f - ssbb[2] ) / 2.0f ) ) * uCorrection;
        mOtherFaceUVs[1][1]  = ( ssbb[1] / 2.0f ) * vCorrection;
        mOtherFaceUVs[1][2]  = 1.0f; //q
        mOtherFaceUVs[1][3]  = ssbb[2] * uCorrection;
        mOtherFaceUVs[1][4]  = ssbb[1] * vCorrection;
        mOtherFaceUVs[1][5]  = 1.0f;
        mOtherFaceUVs[1][6]  = ( ssbb[2] + ( ( 1.0f - ssbb[2] ) / 2.0f ) ) * uCorrection;
        mOtherFaceUVs[1][7]  = ( ssbb[3] + ( ( 1.0f - ssbb[3] ) / 2.0f ) ) * vCorrection;
        mOtherFaceUVs[1][8]  = 1.0f; //q
        mOtherFaceUVs[1][9]  = ssbb[2] * uCorrection;
        mOtherFaceUVs[1][10] = ssbb[3] * vCorrection;
        mOtherFaceUVs[1][11] = 1.0f;
        
        // Positive Y (Top)
        mOtherFaceUVs[2][0]  = ( ssbb[2] + ( ( 1.0f - ssbb[2] ) / 2.0f ) ) * uCorrection;
        mOtherFaceUVs[2][1]  = ( 1.0f - ( ssbb[3] + ( ( 1.0f - ssbb[3] ) / 2.0f ) ) ) * vCorrection;
        mOtherFaceUVs[2][2]  = 1.0f;
        mOtherFaceUVs[2][3]  = ( ssbb[0] / 2.0f ) * uCorrection;
        mOtherFaceUVs[2][4] = ( 1.0f - ( ssbb[3] + ( ( 1.0f - ssbb[3] ) / 2.0f ) ) ) * vCorrection;
        mOtherFaceUVs[2][5] = 1.0f;
        mOtherFaceUVs[2][6]  = ssbb[2] * uCorrection;
        mOtherFaceUVs[2][7]  = ( 1.0f - ssbb[3] ) * vCorrection;
        mOtherFaceUVs[2][8]  = 1.0f;
        mOtherFaceUVs[2][9]  = ssbb[0] * uCorrection;
        mOtherFaceUVs[2][10]  = ( 1.0f - ssbb[3] ) * vCorrection;
        mOtherFaceUVs[2][11]  = 1.0f;
       
        
        // Negative Y (Bottom)
        mOtherFaceUVs[3][0]  = ssbb[2] * uCorrection;
        mOtherFaceUVs[3][1]  = ( 1.0f - ssbb[1] ) * vCorrection;
        mOtherFaceUVs[3][2]  = 1.0f;
        mOtherFaceUVs[3][3]  = ssbb[0] * uCorrection;
        mOtherFaceUVs[3][4] = ( 1.0f - ssbb[1] ) * vCorrection;
        mOtherFaceUVs[3][5] = 1.0f;
        mOtherFaceUVs[3][6]  = ( ssbb[2] + ( ( 1.0f - ssbb[2] ) / 2.0f ) ) * uCorrection;
        mOtherFaceUVs[3][7]  = ( 1.0f - ( ssbb[1] / 2.0f ) ) * vCorrection;
        mOtherFaceUVs[3][8]  = 1.0f;
        mOtherFaceUVs[3][9]  = ( ssbb[0] / 2.0f ) * uCorrection;
        mOtherFaceUVs[3][10]  = ( 1.0f - ( ssbb[1] / 2.0f ) ) * vCorrection;
        mOtherFaceUVs[3][11]  = 1.0f;
        
        
        // Positive Z
        mOtherFaceUVs[4][0] = ssbb[0] * uCorrection;
        mOtherFaceUVs[4][1] = ( 1.0f - ssbb[1] ) * vCorrection;
        mOtherFaceUVs[4][2] = ssbb[2] * uCorrection;
        mOtherFaceUVs[4][3] = ( 1.0f - ssbb[1] ) * vCorrection;
        mOtherFaceUVs[4][4] = ssbb[0] * uCorrection;
        mOtherFaceUVs[4][5] = ( 1.0f - ssbb[3] ) * vCorrection;
        mOtherFaceUVs[4][6] = ssbb[2] * uCorrection;
        mOtherFaceUVs[4][7] = ( 1.0f - ssbb[3] ) * vCorrection;
        
        // Update the floatbuffers
        for( int i = 0; i < 5; i++ ) {
        	mOtherFaceTBuffers[i].clear();
        	mOtherFaceTBuffers[i].put( mOtherFaceUVs[i] );
        }
       
	}
	
	/**
	 * Corrects the Screen Space bounding box based on screen boundaries
	 * @param ssbb
	 * @return A Corrected SSBB based on screen boundaries
	 */
	public static float[] CorrectSSBB( float[] ssbb ){
		float[] correctSSBB = { 0.0f, 0.0f, 0.0f, 0.0f };
		float width = ssbb[2] - ssbb[0];
		float height = ssbb[3] - ssbb[1];
		float cx = ssbb[0] + ( width / 2.0f );
		float cy = ssbb[1] + ( height / 2.0f );
		width *= ( 1.0f + PERCENT_ENLARGE );
		height *= ( 1.0f + PERCENT_ENLARGE );
		correctSSBB[0] = ( cx - ( width / 2.0f ) < PERCENT_BORDER ) ? PERCENT_BORDER : cx - ( width / 2.0f );
		correctSSBB[1] = ( cy - ( height / 2.0f ) < PERCENT_BORDER ) ? PERCENT_BORDER : cy - ( height / 2.0f );
		correctSSBB[2] = ( cx + ( width / 2.0f ) > 1.0f - PERCENT_BORDER ) ? 1.0f - PERCENT_BORDER : cx + ( width / 2.0f );
		correctSSBB[3] = ( cy + ( height / 2.0f ) > 1.0f - PERCENT_BORDER ) ? 1.0f - PERCENT_BORDER : cy + ( height / 2.0f );
		return correctSSBB;
	}
	
	/**
	 * Draws the current face using glDrawArrays
	 * @param i the number of the face to draw following standard GL cube map conventions
	 * @param positionhandle Handle for the position attribute in program
	 * @param texturehandle Handle for the texture coordinate attribute in program
	 */
	public void DrawFace( int face, int positionhandle, int texturehandle ) {
		// Figure out what buffers to draw
		FloatBuffer vbuffer;
		FloatBuffer tbuffer;
		if( face < 5 ) {
			mOtherFaceVBuffer.position(0);
			vbuffer = mOtherFaceVBuffer;
			mOtherFaceTBuffers[face].position(0);
			tbuffer = mOtherFaceTBuffers[face];
		} else {
			mFrontFaceVBuffer.position(0);
			vbuffer = mFrontFaceVBuffer;
			mFrontFaceTBuffer.position(0);
			tbuffer = mFrontFaceTBuffer;
		}
		
		// Enable attributes in current program
		if( face < 4 ) {
			GLES20.glVertexAttribPointer(positionhandle, 3, GLES20.GL_FLOAT, false,
	                GraphicsUtil.TRIANGLE_VERTICES_DATA_STRIDE_BYTES, vbuffer);
			GraphicsUtil.checkGlError("glVertexAttribPointer maPosition");
	        
			GLES20.glVertexAttribPointer(texturehandle, 3, GLES20.GL_FLOAT, false,
	        		GraphicsUtil.TRIANGLE_VERTICES_UVQ_STRIDE_BYTES, tbuffer);
			GraphicsUtil.checkGlError("glVertexAttribPointer maTextureHandle");
		}
		else {
			GLES20.glVertexAttribPointer(positionhandle, 3, GLES20.GL_FLOAT, false,
	                GraphicsUtil.TRIANGLE_VERTICES_DATA_STRIDE_BYTES, vbuffer);
			GraphicsUtil.checkGlError("glVertexAttribPointer maPosition");
	        
			GLES20.glVertexAttribPointer(texturehandle, 2, GLES20.GL_FLOAT, false,
	        		GraphicsUtil.TRIANGLE_VERTICES_UV_STRIDE_BYTES, tbuffer);
			GraphicsUtil.checkGlError("glVertexAttribPointer maTextureHandle");
		}
			
		
		GLES20.glEnableVertexAttribArray(positionhandle);
		GraphicsUtil.checkGlError("glEnableVertexAttribArray maPositionHandle");
		GLES20.glEnableVertexAttribArray(texturehandle);
		GraphicsUtil.checkGlError("glEnableVertexAttribArray maTextureHandle");
		
		// Draw the face
		if( face < 5 ) {
			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4 );
		} else {
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, edu.dhbw.andar.Config.CUBEMAP_QUALITY * 8 * 3 );
		}
		GraphicsUtil.checkGlError("glDrawArrays");
		
		// Disable the attributes to reset to initial state
		GLES20.glDisableVertexAttribArray(positionhandle);
		GLES20.glDisableVertexAttribArray(texturehandle);	
	}
}
