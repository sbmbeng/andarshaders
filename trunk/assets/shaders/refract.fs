precision mediump float;
varying vec3 normal;
varying vec3 view;
uniform samplerCube uCubemap;

void main(void){
	vec3 refrTexCoord = refract( view, -normal, 0.1);
	gl_FragColor = textureCube( uCubemap, refrTexCoord );
}