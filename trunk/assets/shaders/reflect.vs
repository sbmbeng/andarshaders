precision mediump float;
uniform mat4 uMVMatrix;
uniform mat4 uPMatrix;
attribute vec4 aPosition;
attribute vec3 aNormal;
varying vec3 reflected;
void main() {
	gl_Position = uPMatrix * uMVMatrix * aPosition;
	vec3 normal = uMVMatrix*aNormal;
	vec4 incident = uMVMatrix*aPosition;
	reflected = reflect(incident, normal);
}
