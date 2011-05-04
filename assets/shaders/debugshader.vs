uniform mat4 uMVMatrix;
uniform mat4 uPMatrix;
uniform vec4 uColor;
attribute vec4 aPosition;

void main() {
	mat4 mvpmatrix = uPMatrix * uMVMatrix;
	gl_Position = mvpmatrix * aPosition;
}