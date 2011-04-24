precision mediump float;
varying vec4 vNormal;
varying vec4 vPosition;

// a shader for a black & white checkerboard

float xzdist(vec4 a){
	float d = pow(a.x-0.21,2.) + pow(a.z-0.34,2.);
	d = sqrt(d);
	return d;
}
float yzdist(vec4 a){
	float d = pow(a.y,2.) + pow(a.z,2.);
	d = sqrt(d);
	return d;
}
float xydist(vec4 a){
	float d = pow(a.x,2.) + pow(a.y,2.);
	d = sqrt(d);
	return d;
}
float xdist(vec4 a){
	float d = a.x;
	return d;
}
float zdist(vec4 a){
	float d = a.z;
	return d;
}
float realdist(vec4 a){
	float d = pow(a.x,2.) + pow(a.y,2.) + pow(a.z,2.);
	d = sqrt(d);
	return d;
}
float altrealdist(vec4 a){
	float d = pow(a.x-7.,2.) + pow(a.y-3.,2.) + pow(a.z-4.,2.);
	d = sqrt(d);
	return d;
}

void main (void) {  

  vec3 color = vec3(0.3,0.15,0);
  float c = sin(6.0*xzdist(vPosition)) + 0.1*cos(10.*realdist(vPosition)) + 0.2*sin(5.*realdist(vPosition)+32.) + 0.3*sin(2.*xydist(vPosition)+25.)+0.4*sin(0.1*zdist(vPosition));
  c -= 0.3*sin(1.7*altrealdist(vPosition)) + 0.6;

  c += 0.15*cos(8.*altrealdist(vPosition)) - 0.16*sin(8.*altrealdist(vPosition)+77.);

  if(c < 0.) c*=-1.;
  if(c < 0.3) c-= 0.15;
  c/=1.3;
  if(c>0.8) c= 0.8;
  
  
  color += vec3(0.5*c,0.5*c,0.5*c);

  gl_FragColor = vec4 (color, 1.0);
}