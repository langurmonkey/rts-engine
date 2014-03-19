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
uniform vec2 screenRes;

// Vectors with x, y, radius for all the entities
uniform int lightCount;
uniform int shadowCount;
uniform vec3 lights[MAX_LIGHTS];
uniform vec4 shadows[MAX_LIGHTS];
uniform vec2 cameraPosition;


void main() {
	float alpha = 0.0;
	vec2 offset = cameraPosition - screenRes / 2.0;
	
	for(int i = 0; i < lightCount; i++){
		vec3 light = lights[i];
		// 1 - x
		// 2 - y
		// 3 - radius
		float radius = light.z;
		
		// Determine camera position offset vector. Starts at (0,0)
		vec2 lpos = light.xy - offset;
		float dist = distance(gl_FragCoord.xy, lpos);
		
		if(dist >= 10 && dist <= radius){
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
	
	for(int i = 0; i < shadowCount; i++){
		vec4 shadow = shadows[i];
		// 1 - x
		// 2 - y
		// 3 - a**2
		// 4 - b**2
		vec2 spos = shadow.xy - offset - vec2(-1, 2);
		float distSum = (pow(gl_FragCoord.x - spos.x, 2.0) / shadow.z)+(pow(gl_FragCoord.y - spos.y, 2.0) / shadow.a);
		
		if(distSum <= 1 && alpha > distSum) {
			// Shadow zone
			alpha = distSum;
		}
	}

	// Default dim light
	alpha += .15;
	
	vec4 texColor = texture2D(u_texture, v_texCoords);
    gl_FragColor = vec4(texColor.rgb * alpha, texColor.a);
}


