precision mediump float;
uniform mat4 uMVMatrix;
uniform mat4 uPMatrix;
uniform mat4 uNormalMatrix;
attribute vec4 aPosition;
attribute vec3 aNormal;
varying vec3 normal;
varying vec3 view;

mat3 GetTransNormal( mat4 m )
{
	float det = m[0][0]*(m[1][1]*m[2][2]-m[1][2]*m[2][1])
              - m[0][1]*(m[1][0]*m[2][2]-m[1][2]*m[2][0]) 
              + m[0][2]*(m[1][0]*m[2][1]-m[1][1]*m[2][0]);
    
	mat3 ret;
	ret[0][0] = ( m[1][1]*m[2][2]-m[2][1]*m[1][2] ) / det;
	ret[1][0] = ( m[0][2]*m[2][1]-m[2][2]*m[0][1] ) / det;
	ret[2][0] = ( m[0][1]*m[1][2]-m[1][1]*m[0][2] ) / det;
	ret[0][1] = ( m[1][2]*m[2][0]-m[2][2]*m[1][0] ) / det;
	ret[1][1] = ( m[0][0]*m[2][2]-m[2][0]*m[0][2] ) / det;
	ret[2][1] = ( m[0][2]*m[1][0]-m[1][2]*m[0][0] ) / det;
	ret[0][2] = ( m[1][0]*m[2][1]-m[2][0]*m[1][1] ) / det;
	ret[1][2] = ( m[0][1]*m[2][0]-m[2][1]*m[0][0] ) / det;
	ret[2][2] = ( m[0][0]*m[1][1]-m[1][0]*m[0][1] ) / det;
	
	return ret;
}

void main(void){
	mat4 mvpmatrix = uPMatrix * uMVMatrix;
	vec4 worldpos = mvpmatrix * aPosition;
	gl_Position = worldpos;
	normal = normalize( GetTransNormal( uMVMatrix ) * aNormal );
	view = normalize( uMVMatrix * aPosition ).xyz;
}
