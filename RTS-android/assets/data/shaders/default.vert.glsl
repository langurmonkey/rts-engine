attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;

varying vec4 v_color;
varying vec4 v_position;
varying vec2 v_texCoords;

void main() {
    v_color = a_color;
    v_texCoords = a_texCoord0;
 	v_position = a_position;
    gl_Position = u_projTrans * a_position;
}