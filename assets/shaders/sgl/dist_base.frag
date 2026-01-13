
uniform sampler2D u_texture;

varying vec2 v_texCoords;

void main() {
    gl_FragColor.rgb = texture2D(u_texture, v_texCoords).rgb;
    gl_FragColor.a = 1.0;
}
