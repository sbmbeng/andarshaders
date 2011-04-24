uniform mat4 uMVPMatrix;
uniform vec4 uColor;
attribute vec4 aNormal;
attribute vec4 aPosition;
varying vec4 vNormal;
varying vec4 vPosition;
// a shader for wood

void main(void) {
  // pass along the normal 
  float a = uColor.x;
  vNormal = aNormal;
  vPosition = aPosition;
  gl_Position = uMVPMatrix * aPosition;
}
