precision mediump float;
varying vec3 v_normal;
uniform samplerCube uCubemap;
void main() {
	gl_FragColor = textureCube( uCubemap, v_normal );
}