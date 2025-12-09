
in vec2 v_texCoords;

uniform sampler2D u_positionTex;
uniform sampler2D u_colorTex;
uniform sampler2D u_specularTex;
uniform sampler2D u_normalTex;

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

vec3 calculateLighting(
    LightSource light, vec3 normalDir,
    vec3 fragPos, vec3 camPos,
    vec3 objectColor, vec3 specular
) {
    vec3 cameraDir = normalize(camPos - fragPos);
    vec3 lightDir = normalize(light.position - fragPos);

    float dst = length(light.position - fragPos) + length(camPos - fragPos);
    float inten = light.color.a*pow(clamp(1.0 - dst/light.radius, 0.0, 1.0), light.attenuation);

    float diff = max(dot(lightDir, normalDir), 0.0);
    vec3 diffuseC = diff * objectColor * light.color.rgb;

    vec3 halfwayDir = normalize(lightDir + cameraDir);
    float spec = pow(max(dot(normalDir, halfwayDir), 0.0), 32.0);

    vec3 specularC = spec * specular * light.color.rgb;

    return inten * (diffuseC + specularC);
}

void main() {
    vec3 len = u_maxPos - u_minPos;

    vec4 coordColor = texture(u_colorTex, v_texCoords);

    if (coordColor.a < 0.001){
        gl_FragColor = vec4(0.0);
    }
    else {
        vec3 position = texture(u_positionTex, v_texCoords).rgb*len + u_minPos;
        vec3 color = coordColor.rgb;
        vec3 specular = texture(u_specularTex, v_texCoords).rgb;
        vec3 normalDir = texture(u_normalTex, v_texCoords).rgb*2.0 - 1.0;

        vec3 calcColor = u_ambientLight.rgb*color;
        for (int i = 0; i < LIGHTS_COUNT; i++) {
            LightSource light = u_lightSources[i];
            calcColor += calculateLighting(light, normalDir, position, u_cameraPos, color, specular);
        }

        gl_FragColor = vec4(calcColor, 1.0);
    }
}