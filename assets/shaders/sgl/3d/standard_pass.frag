
in vec2 v_texCoord;

uniform sampler2D u_positionTex;
uniform sampler2D u_colorTex;
uniform sampler2D u_specularTex;
uniform sampler2D u_normalDirTex;
uniform sampler2D u_tangentTex;
uniform sampler2D u_bitangentTex;
uniform sampler2D u_normalTex;
uniform sampler2D u_depthTex;

struct LightSource {
    vec3 position;
    vec4 color;
    float radius;
    float attenuation;
};
uniform LightSource u_lightSources[LIGHTS_COUNT];
uniform vec4 u_ambientLight;

uniform vec3 u_minPos;
uniform vec3 u_maxPos;

uniform vec3 u_cameraPos;

vec3 calculateDirLighting(mat3 TBN,
    vec3 lightDir, vec3 normalDir,
    vec3 fragPos, vec3 camPos,
    vec3 objectColor, vec4 lightColor, vec3 specColor
) {
    vec3 tangCamPos = TBN * camPos;
    vec3 tangFragPos = TBN * fragPos;

    vec3 lightDir = normalize(-lightDir);
    vec3 cameraDir = normalize(tangCamPos - tangFragPos);

    float diff = max(dot(lightDir, normalDir), 0.0);
    vec3 diffuse = diff * objectColor * lightColor.rgb;

    vec3 halfwayDir = normalize(lightDir + cameraDir);
    float spec = pow(max(dot(normalDir, halfwayDir), 0.0), 32.0);

    vec3 specular = spec * specColor * lightColor.rgb;

    return (diffuse + specular)*lightColor.a;
}

vec3 calculateLighting(mat3 TBN,
    LightSource light, vec3 normalDir,
    vec3 fragPos, vec3 camPos,
    vec3 objectColor, vec3 specColor
) {
    vec3 tangCamPos = TBN * camPos;
    vec3 tangFragPos = TBN * fragPos;
    vec3 tangLightPos = TBN * light.position;

    vec3 cameraDir = normalize(tangCamPos - tangFragPos);
    vec3 lightDir = normalize(tangLightPos - tangFragPos);

    float dst = length(tangLightPos - tangFragPos) + length(tangCamPos - tangFragPos);
    float inten = light.color.a*pow(clamp(1.0 - dst/light.radius, 0.0, 1.0), light.attenuation);

    float diff = max(dot(lightDir, normalDir), 0.0);
    vec3 diffuse = diff * objectColor * light.color.rgb;

    vec3 halfwayDir = normalize(lightDir + cameraDir);
    float spec = pow(max(dot(normalDir, halfwayDir), 0.0), 32.0);

    vec3 specular = spec * light.color.rgb * specColor;

    return inten * (diffuse + specular);
}

void main() {
    vec3 len = u_maxPos - u_minPos;

    vec3 position = texture(u_positionTex, v_texCoord).rgb*len + u_minPos;
    vec3 color = texture(u_colorTex, v_texCoord).rgb;
    vec3 specular = texture(u_specularTex, v_texCoord).rgb;
    vec3 normalDir = texture(u_normalDirTex, v_texCoord).rgb*2.0 - 1.0;
    vec3 tangent = texture(u_tangentTex, v_texCoord).rgb*2.0 - 1.0;
    vec3 bitangent = texture(u_bitangentTex, v_texCoord).rgb*2.0 - 1.0;
    vec3 normal = texture(u_normalTex, v_texCoord).rgb*2.0 - 1.0;

    mat3 TBN = mat3(tangent, bitangent, normalDir);

    vec3 calcColor = u_ambientLight.rgb;
    for (int i = 0; i < LIGHTS_COUNT; i++) {
        LightSource light = u_lightSources[i];
        calcColor += calculateLighting(TBN, light, normalDir, position, u_cameraPos, color, specular);
    }

    gl_FragColor = vec4(calcColor, 1.0);
    gl_FragDepth = texture(u_depthTex, v_texCoord).r;
}