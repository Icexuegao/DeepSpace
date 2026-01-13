
uniform vec3 u_lightPos;
uniform float u_shadowRadius;

varying vec3 v_fragPos;

void main() {
    float lightDistance = length(v_fragPos - u_lightPos);

    lightDistance = lightDistance / u_shadowRadius;

    gl_FragColor = vec4(vec3(lightDistance), 1.0);
}