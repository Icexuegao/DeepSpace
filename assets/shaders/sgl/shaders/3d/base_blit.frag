
uniform sampler2D u_texture;

in vec2 v_texCoords;

void main() {
    gl_FragColor = texture(u_texture, v_texCoords);
}