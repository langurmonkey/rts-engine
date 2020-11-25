#version 120
#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#define SMOOTH_DOWN 0.6
#define SMOOTH_UP 1.0
#define MAX_LIGHTS 10

varying LOWP vec4 v_color;
varying vec4 v_position;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_viewport_size;

// Vectors with x, y, radius for all the entities
uniform int u_light_count;
uniform vec3 u_lights[MAX_LIGHTS];

void main() {
    float alpha;

    if(u_light_count > 0){
        alpha = 0.0;
        for (int i = 0; i < u_light_count; i++){
            vec3 light = u_lights[i];
            // 1 - x
            // 2 - y
            // 3 - radius
            float radius = light.z;

            // Determine camera position offset vector. Starts at (0,0)
            vec2 lpos = light.xy;
            float dist = distance(gl_FragCoord.xy, lpos);

            if (dist <= radius * SMOOTH_DOWN){
                // Area between shadow and visibility fade
                alpha = 1.0;
            } else if (dist <= radius * SMOOTH_UP){
                // Visibility fade
                alpha += 1.0 - smoothstep(radius * SMOOTH_DOWN, radius * SMOOTH_UP, dist);
            }
        }
    } else {
        alpha = 1.0;
    }

    // Default dim light
    alpha = clamp(alpha, 0.1, 1.0);

    vec4 texColor = texture2D(u_texture, v_texCoords);
    gl_FragColor = vec4(texColor.r * alpha, texColor.g * alpha, texColor.b * alpha, texColor.a);
}


