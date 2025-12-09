
varying vec2 v_texCoords;
varying vec2 v_normCoords;
varying vec4 v_color;
varying vec3 v_normal;
varying vec3 v_tangLightPos[LIGHTS];
varying vec3 v_tangCamPos;
varying vec3 v_tangFragPos;

uniform sampler2D u_texture;
uniform sampler2D u_normalTex;

struct LightSource {
    vec3 position;
    vec4 color;
    float radius;
    float attenuation;
};

uniform LightSource u_light[LIGHTS];
uniform int u_activeLights;

uniform vec3 u_lightDir;
uniform vec4 u_lightColor;
uniform vec4 u_ambientColor;

vec3 calculateDirLighting(vec3 objectColor, vec3 normalDir) {
    vec3 lightDir = normalize(-u_lightDir);
    vec3 cameraDir = normalize(v_tangCamPos - v_tangFragPos);

    float diff = max(dot(lightDir, normalDir), 0.0);
    vec3 diffuse = diff * objectColor * u_lightColor.rgb;

    vec3 halfwayDir = normalize(lightDir + cameraDir);
    float spec = pow(max(dot(normalDir, halfwayDir), 0.0), 32.0);

    vec3 specular = spec * u_lightColor.rgb;

    return (diffuse + spec)*u_lightColor.a;
}

vec3 calculateLighting(LightSource light, vec3 normalDir, vec3 tangLightPos, vec3 objectColor) {
    vec3 cameraDir = normalize(v_tangCamPos - v_tangFragPos);
    vec3 lightDir = normalize(tangLightPos - v_tangFragPos);

    float dst = length(tangLightPos - v_tangFragPos) + length(v_tangCamPos - v_tangFragPos);
    float inten = light.color.a*pow(clamp(1.0 - dst/light.radius, 0.0, 1.0), light.attenuation);

    float diff = max(dot(lightDir, normalDir), 0.0);
    vec3 diffuse = diff * objectColor * light.color.rgb;

    vec3 halfwayDir = normalize(lightDir + cameraDir);
    float spec = pow(max(dot(normalDir, halfwayDir), 0.0), 32.0);

    vec3 specular = spec * light.color.rgb;

    return inten * (diffuse + specular);
}

void main() {
    vec4 color = texture2D(u_texture, v_texCoords);
    vec4 normal = texture2D(u_normalTex, v_normCoords);

    vec3 normalDir = mix(v_normal, normalize(normal.rgb * 2.0 - 1.0), normal.a)*(float(gl_FrontFacing) - 0.5)*2.0;

    vec3 lightedColor = u_ambientColor.rgb * u_ambientColor.a;

    lightedColor += calculateDirLighting(color.rgb, normalDir);

    #if version >= 300
    for (int i = 0; i < u_activeLights; i++) {
        LightSource l = u_light[i];
        vec3 pos = v_tangLightPos[i];
        lightedColor += calculateLighting(l, normalDir, pos, color.rgb);
    }
    #else
    for (int i = 0; i < LIGHTS; i++) {
        LightSource l = u_light[i];
        vec3 pos = v_tangLightPos[i];
        float n = 1.0 - step(float(u_activeLights), float(i));
        lightedColor += n*calculateLighting(l, normalDir, pos, color.rgb);
    }
    #endif

    gl_FragColor = vec4(lightedColor, color.a);
}
