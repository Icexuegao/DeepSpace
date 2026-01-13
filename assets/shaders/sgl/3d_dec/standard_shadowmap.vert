
attribute vec3 a_position;
attribute vec2 a_texCoord0;
attribute vec2 a_texCoord1;
attribute vec2 a_texCoord2;
attribute vec4 a_color;
attribute vec3 a_normal;
attribute vec3 a_tangent;

uniform mat4 u_projLightSpace;
uniform mat4 u_transform;

varying vec3 v_fragPos;

void main() {
    v_fragPos = vec3(u_transform * vec4(a_position, 1.0));
    gl_Position = u_projLightSpace * u_transform * vec4(a_position, 1.0);
}