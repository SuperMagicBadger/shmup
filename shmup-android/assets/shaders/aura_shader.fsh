#ifdef GL_ES
#define mediump lowp
precision mediump float;
#else
#define lowp 
#endif

uniform float u_radius;
uniform vec2 u_center;
uniform vec4 u_color;
uniform vec2 u_resolution;

varying vec2 v_center;
varying vec4 v_color;

void main(void) {

	// find distance from center
	//vec2 relative = (gl_FragCoord.xy - v_center) / u_resolution;
	//relative -= vec2(0.5);
	//float len = length(relative);

	// mod color by radius
	// float shade = smoothstep(0.25, 0.25, len);

	gl_FragColor = mix(u_color, v_color, 0.75);//vec4(vec3(len),1.0);
}
