precision mediump float;
varying vec3 vTextureCoord;
uniform sampler2D sTexture;

void main() {
	gl_FragColor = texture2DProj(sTexture, vTextureCoord);
}