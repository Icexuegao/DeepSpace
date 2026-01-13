
uniform sampler2D u_texture;
uniform sampler2D u_mask;

varying vec2 v_texCoords;

void main() {
    vec4 c = texture2D(u_texture, v_texCoords);

    gl_FragColor = vec4(c.rgb, texture2D(u_mask, v_texCoords).a*c.a);
}