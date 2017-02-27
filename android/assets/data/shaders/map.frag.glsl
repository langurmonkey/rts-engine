#version 120
#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif

#define SMOOTH 2
#define MAX_LIGHTS 20

varying LOWP vec4 v_color;
varying vec4 v_position;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_viewport_size;
uniform vec2 u_camera_offset;

// Vectors with x, y, radius for all the entities
uniform int u_light_count;
uniform vec3 u_lights[MAX_LIGHTS];

void main() {
	float alpha = 0.0;
	
	for(int i = 0; i < u_light_count; i++){
		vec3 light = u_lights[i];
		// 1 - x
		// 2 - y
		// 3 - radius
		float radius = light.z;
		
		// Determine camera position offset vector. Starts at (0,0)
		vec2 lpos = light.xy - u_camera_offset;
		float dist = distance(gl_FragCoord.xy, lpos);
		
		if(dist <= radius){
			// Area between shadow and visibility fade
			alpha = 1.0;
		}else if(dist > radius && dist < radius + radius * SMOOTH){
			// Visibility fade
			float aux = smoothstep(radius + radius * SMOOTH, radius, dist);
			if(alpha < 1.0 && alpha < aux){
				alpha = aux;
			}
		}
	}
	
	// Default dim light
	alpha += .15;
	
	vec4 texColor = texture2D(u_texture, v_texCoords);
    gl_FragColor = vec4(texColor.r * alpha, texColor.g * alpha, texColor.b * 1.2 * alpha, texColor.a);
}


