
uniform sampler2D u_texture;
uniform float u_coef;

varying vec2 v_texCoords;

void main() {
    vec4 c = texture2D(u_texture, v_texCoords);

    gl_FragColor = vec4(c.rgb, max(min(c.a + u_coef, 1.0), 0.0));
}