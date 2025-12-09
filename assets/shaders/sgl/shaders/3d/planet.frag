
in vec3 v_position;
in vec3 v_normal;
in vec4 v_color;

uniform vec3 u_minPos;
uniform vec3 u_maxPos;

layout(location = 0) out vec4 g_fragPos;
layout(location = 1) out vec4 g_fragColor;
layout(location = 2) out vec4 g_specularColor;
layout(location = 3) out vec4 g_fragNormalDir;

void main() {
    //POSITION
    g_fragPos = vec4((v_position - u_minPos)/(u_maxPos - u_minPos), 1.0);
    //FRAGINFO
    g_fragColor = vec4(v_color.rgb, 1.0);
    g_specularColor = vec4(v_color.a);
    g_fragNormalDir = vec4(normalize(v_normal)*0.5 + 0.5, 1.0);
}
