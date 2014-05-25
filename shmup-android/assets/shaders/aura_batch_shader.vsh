attribute vec3 s_osition;
attribute vec4 a_color

uniform mat4 u_projection;

varying vec4 v_color;

void main(void) {
	v_color = a_color;
	gl_Position =  vec4(aVertexPosition, 1.0);
}
