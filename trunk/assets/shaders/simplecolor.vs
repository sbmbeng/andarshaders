uniform mat4 uMVPMatrix;
uniform vec4 uColor;
attribute vec4 aPosition;
attribute vec4 aNormal;
varying vec4 vNormal;

void main() {
	vNormal = aNormal;
	gl_Position = uMVPMatrix * aPosition;
}