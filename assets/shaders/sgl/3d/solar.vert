
layout(location = 0) in vec3 a_position;
layout(location = 1) in vec3 a_normal;
layout(location = 2) in vec4 a_color;

uniform mat4 u_proj;
uniform mat4 u_view;
uniform mat4 u_transform;

out vec4 v_color;

void main(){
    v_color = a_color;
    gl_Position = u_proj * u_view * u_transform * vec4(a_position, 1.0);
}
