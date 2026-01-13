
in vec2 v_diffCoords;
in vec2 v_normCoords;
in vec2 v_specCoords;
in vec4 v_color;
in vec3 v_position;
in mat3 v_tbn;

uniform sampler2D u_texture;
uniform sampler2D u_specularTex;
uniform sampler2D u_normalTex;

uniform vec3 u_minPos;
uniform vec3 u_maxPos;

out vec4 g_fragPos;
out vec4 g_fragColor;
out vec4 g_fragSpecular;
out vec4 g_fragNormalDir;
out vec4 g_fragTangent;
out vec4 g_fragBitangent;
out vec4 g_fragNormal;

void main() {
    vec3 color = texture2D(u_texture, v_diffCoords).rgb;
    vec3 specColor = texture2D(u_specularTex, v_specCoords).rgb;
    vec3 normal = texture2D(u_normalTex, v_normCoords).rgb;

    vec3 v_tangent = vec3(v_tbn[0]);
    vec3 v_bitangent = vec3(v_tbn[1]);
    vec3 v_normal = vec3(v_tbn[2]);

    vec3 normalDir = normalize(normal * 2.0 - 1.0)*(float(gl_FrontFacing) - 0.5)*2.0;

    //POSITION
    g_fragPos = vec4((v_position - u_minPos)/(u_maxPos - u_minPos), 1.0);
    //FRAGINFO
    g_fragColor = color*v_color;
    g_fragSpecular = specColor;
    g_fragNormalDir = vec4(normalize(normalDir)*0.5 + 0.5, 1.0);
    //TBN
    g_fragTangent = vec4(normalize(v_tangent)*0.5 + 0.5, 1.0);
    g_fragBitangent = vec4(normalize(v_bitangent)*0.5 + 0.5, 1.0);
    g_fragNormal = vec4(normalize(v_normal)*0.5 + 0.5, 1.0);
}
