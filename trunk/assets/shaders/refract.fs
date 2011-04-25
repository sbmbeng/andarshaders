precision mediump float;
uniform vec4 uCamera;
varying vec4 vNormal;
varying vec4 vPosition;
varying vec4 vSPosition;
varying vec2 vTextureCoord;
uniform sampler2D uDTex1;
uniform sampler2D uDTex2;
// a shader for Refraction!



void main (void) {  
  float eta = 0.5;
  vec4 I = vPosition-uCamera;
  vec4 N1 = vNormal;
  vec4 T1 = refract(I,N1,eta);
  vec3 color = vec3(0.,0.,0.);
  
  float dist = texture2D(uDTex1, vSPosition.xy).x - texture2D(uDTex2, vSPosition.xy).x;
  
  color = vec3(dist,dist,dist);

  gl_FragColor = vec4 (color, 1.0);
}