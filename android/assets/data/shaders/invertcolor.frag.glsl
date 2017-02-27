#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec4 v_position;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main() {
	vec4 texColor = texture2D(u_texture, v_texCoords);
	texColor.rgb = 1.0 - texColor.rgb;
	
    gl_FragColor = v_color * texColor;
    //gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
}


