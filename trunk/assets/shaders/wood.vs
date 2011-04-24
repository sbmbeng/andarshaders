varying vec3 normal;
varying vec3 position_eyespace;
varying vec3 position_worldspace;

// a shader for a black & white checkerboard

void main(void) {

  // the fragment shader requires both the world space position (to determine which
  // black/white square we are in) & eyespace position (for lighting)
  position_eyespace = vec3(gl_ModelViewMatrix * gl_Vertex);
  position_worldspace = gl_Vertex.xyz;

  // pass along the normal 
  normal = normalize(gl_NormalMatrix * gl_Normal);

  gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}
