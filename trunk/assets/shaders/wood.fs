varying vec3 normal;
varying vec3 position_eyespace;
varying vec3 position_worldspace;

// a shader for a black & white checkerboard

float xzdist(vec3 a){
	float d = pow(a.x-0.21,2) + pow(a.z-0.34,2);
	d = sqrt(d);
	return d;
}
float yzdist(vec3 a){
	float d = pow(a.y,2) + pow(a.z,2);
	d = sqrt(d);
	return d;
}
float xydist(vec3 a){
	float d = pow(a.x,2) + pow(a.y,2);
	d = sqrt(d);
	return d;
}
float xdist(vec3 a){
	float d = a.x;
	return d;
}
float zdist(vec3 a){
	float d = a.z;
	return d;
}
float realdist(vec3 a){
	float d = pow(a.x,2) + pow(a.y,2) + pow(a.z,2);
	d = sqrt(d);
	return d;
}
float altrealdist(vec3 a){
	float d = pow(a.x-7,2) + pow(a.y-3,2) + pow(a.z-4,2);
	d = sqrt(d);
	return d;
}

void main (void) {  

  vec3 color = vec3(0.3,0.15,0);
  float c = sin(60*xzdist(position_worldspace)) + 0.1*cos(10*realdist(position_worldspace)) + 0.2*sin(5*realdist(position_worldspace)+32) + 0.3*sin(2*xydist(position_worldspace)+25)+0.4*sin(0.1*zdist(position_worldspace));
  c -= 0.3*sin(12.7*altrealdist(position_worldspace)) + 0.6;

  c += 0.15*cos(80*altrealdist(position_worldspace)) - 0.16*sin(85*altrealdist(position_worldspace)+77);

  if(c < 0) c*=-1;
  if(c < 0.3) c-= 0.15;
  c/=1.3;
  if(c>0.8) c= 0.8;
  
  
  color += vec3(0.5*c,0.5*c,0.5*c);

  // direction to the light
  vec3 light = normalize(gl_LightSource[1].position.xyz - position_eyespace);

  // basic diffuse lighting
  float ambient = 0.3;
  float diffuse = 0.7*max(dot(normal,light),0.0);
  color = ambient*color + diffuse*color;
  gl_FragColor = vec4 (color, 1.0);
}