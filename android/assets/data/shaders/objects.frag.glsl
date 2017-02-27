#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec4 v_position;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_viewport_size;
uniform vec2 u_camera_offset;

uniform int u_draw_shadows;
uniform vec2 u_shadow_pos;
uniform vec2 u_shadow_size;

void main() {
	vec4 texColor = texture2D(u_texture, v_texCoords);
	if(texColor.a == 0.0 && u_draw_shadows > 0){
		// If we're in an alpha position we may paint the shadow
	
	    vec2 spos = u_shadow_pos - u_camera_offset;
		float distSum = (pow(gl_FragCoord.x - spos.x, 2.0) / u_shadow_size.x)+(pow(gl_FragCoord.y - spos.y, 2.0) / u_shadow_size.y);
		
		float alpha = 0.0;
		if(distSum <= 1.0) {
			// Shadow zone
			alpha = .8 - distSum;
		}
		gl_FragColor = vec4(0, 0, 0, alpha);
	} 
	else {
		// Otherwise, just go with the color
		gl_FragColor = v_color * texColor;
	}
}
