attribute vec3 a_position;

uniform float u_radius;
uniform vec2 u_center;
uniform vec4 u_color;
uniform mat4 u_proj;
uniform vec2 u_resolution;

varying vec2 v_center;
varying vec4 v_color;

void main(void) {
	v_center = (u_proj * vec4(u_center, 1.0, 1.0)).xy;
	gl_Position = u_proj * vec4(a_position, 1.0);
	
	vec2 radius = (vec4(u_radius, u_radius, 1.0, 1.0) * u_proj).xy; 

	float len = length((gl_Position.xy - v_center)) / length(radius);

	v_color = vec4(len, len, len, len);//mix(u_color, vec4(len, len, len, 1), 0.5);
}
