attribute vec3 a_position;

varying vec3 v_texCoords;

uniform mat4 u_proj;
uniform float u_far;

void main(){
    v_texCoords = a_position;
    gl_Position = u_proj * vec4(a_position * u_far, 1.0);
}
