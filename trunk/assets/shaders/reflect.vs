uniform mat4 uMVPMatrix;
attribute vec4 aPosition;
attribute vec3 aNormal;
varying vec3 v_normal;
void main() {
	gl_Position = uMVPMatrix * aPosition;
	//v_normal = reflect(aPosition,vec4(aNormal,1.0)).xyz;
	//v_normal = aNormal;
	v_normal = ( vec4( aNormal, 1.0 ) * uMVPMatrix ).xyz;
}
