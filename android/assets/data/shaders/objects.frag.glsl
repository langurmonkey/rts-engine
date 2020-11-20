#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec4 v_position;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_viewport_size;
uniform vec2 u_camera_offset;

void main() {
	vec4 texColor = texture2D(u_texture, v_texCoords);
	gl_FragColor = v_color * texColor;
}
