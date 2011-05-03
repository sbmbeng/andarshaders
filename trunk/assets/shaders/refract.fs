precision mediump float;
uniform vec4 uCamera;
varying vec4 vNormal;
varying vec4 vPosition;
varying vec4 vSPosition;
varying vec2 vTextureCoord;
uniform sampler2D uDTex1;
uniform sampler2D uDTex2;
uniform vec2 uViewport;

// a shader for Refraction!

void main (void) {  
  float eta = 0.5;
  vec4 I = vPosition-uCamera;
  vec4 N1 = vNormal;
  vec4 T1 = refract(I,N1,eta);
  vec3 color = vec3(0.,0.,0.);
  
  float dist0 = texture2D(uDTex1, vSPosition.xy).x;
  float dist1 = texture2D(uDTex1, vSPosition.xy).y;
  float dist2 = texture2D(uDTex1, vSPosition.xy).z;  
  
  float dist3 = texture2D(uDTex2, vSPosition.xy).x;
  float dist4 = texture2D(uDTex2, vSPosition.xy).y;
  float dist5 = texture2D(uDTex2, vSPosition.xy).z;
  
  float dist6 = dist0+dist1+dist2+dist3+dist4+dist5;
  
  color = vec3(dist6,dist6,dist6);

  gl_FragColor = vec4 (color, 1.0);
}