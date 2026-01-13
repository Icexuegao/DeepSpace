
in vec3 v_position;
in vec2 v_texCoord;
in vec3 v_normal;
in vec4 v_color;

uniform vec3 u_minPos;
uniform vec3 u_maxPos;

uniform sampler2D u_texture;

layout(location = 0) out vec4 g_fragPos;
layout(location = 1) out vec4 g_fragColor;
layout(location = 2) out vec4 g_fragNormalDir;
//layout(location = 3) out vec4 g_fragDepth;

void main() {
   // gl_FragColor = vec4(gl_FragCoord.z);
    //POSITION
    g_fragPos = vec4((v_position - u_minPos)/(u_maxPos - u_minPos), 1.0);
    //FRAGINFO
    vec4 diffColor = texture(u_texture, v_texCoord);
    vec4 color = diffColor * v_color;
    g_fragColor = vec4(color.rgb, 1.0);
    g_fragNormalDir = vec4(normalize(v_normal)*0.5 + 0.5, 1.0);
}
