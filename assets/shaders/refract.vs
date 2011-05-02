uniform mat4 uMVPMatrix;
uniform vec4 uCamera;
attribute vec4 aNormal;
attribute vec4 aPosition;
varying vec4 vNormal;
varying vec4 vPosition;
varying vec4 vSPosition;
uniform vec2 uViewport;

// a shader for Refraction!

void main(void) {
  // pass along the normal 
  vNormal = aNormal;
  vPosition = aPosition;
  vSPosition = uMVPMatrix * aPosition;
  vSPosition.x = (vSPosition.x/uViewport.x) + 0.5;
  vSPosition.y = (vSPosition.y/uViewport.y) + 0.5;
  
  gl_Position = uMVPMatrix * aPosition;
}
