precision mediump float;
varying vec3 reflected;
uniform samplerCube uCubemap;
void main() {
	gl_FragColor = textureCube( uCubemap, reflected );
}