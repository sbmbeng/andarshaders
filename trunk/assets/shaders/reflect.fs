precision mediump float;
varying vec4 reflected;
uniform samplerCube cubemap;

// a shader for Refraction!

void main (void) {   
  vec4 color;
  color=vec4(textureCube(cubemap,reflected.xyz));
  gl_FragColor = color;
}