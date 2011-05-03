precision mediump float;
uniform mat4 uMVPMatrix;
attribute vec4 aNormal;
attribute vec4 aPosition;
varying vec4 reflected;

// a shader for Refraction!

void main(void) {
  // pass along the normal 
  gl_Position = uMVPMatrix * aPosition;
  
  reflected = reflect(aPosition,aNormal);
  
}
