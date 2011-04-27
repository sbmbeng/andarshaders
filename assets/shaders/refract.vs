uniform mat4 uMVPMatrix;
uniform vec4 uColor;
uniform vec4 uCamera;
attribute vec4 aNormal;
attribute vec4 aPosition;
varying vec4 vNormal;
varying vec4 vPosition;
varying vec4 vSPosition;

// a shader for Refraction!

void main(void) {
  // pass along the normal 
  float a = uColor.x;
  vNormal = aNormal;
  vPosition = aPosition;
  vSPosition = uMVPMatrix * aPosition;
  gl_Position = uMVPMatrix * aPosition;
}
