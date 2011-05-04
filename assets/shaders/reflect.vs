precision mediump float;
uniform mat4 uMVMatrix;
uniform mat4 uPMatrix;
attribute vec4 aPosition;
attribute vec3 aNormal;
varying vec3 reflected;

mat3 GetLinearPart( mat4 m )
{
	mat3 result;
	
	result[0][0] = m[0][0]; 
	result[0][1] = m[0][1]; 
	result[0][2] = m[0][2]; 

	result[1][0] = m[1][0]; 
	result[1][1] = m[1][1]; 
	result[1][2] = m[1][2]; 
	
	result[2][0] = m[2][0]; 
	result[2][1] = m[2][1]; 
	result[2][2] = m[2][2]; 
	
	return result;
}

void main() {
	mat4 mvpmatrix = uPMatrix * uMVMatrix;
	vec4 worldpos = mvpmatrix * aPosition;
	gl_Position = worldpos;
	mat3 mvmat = GetLinearPart( uMVMatrix );
	vec3 normal = normalize( mvmat *  aNormal );
	vec3 incident = normalize( worldpos.xyz );
	reflected = reflect( incident, normal );
}
