uniform mat4 uMVPMatrix;
attribute vec4 aPosition;
attribute vec3 aTextureCoord;
varying vec3 vTextureCoord;

void main() {
	gl_Position = uMVPMatrix * aPosition;
	vTextureCoord = aTextureCoord;
}