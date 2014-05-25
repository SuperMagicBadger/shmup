#ifdef GL_ES
precision highp float;
#endif

varying v_color;

void main(void) {
	gl_FragColor = a_color;
}
