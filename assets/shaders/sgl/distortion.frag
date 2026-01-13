
uniform sampler2D u_texture0;
uniform sampler2D u_texture1;

uniform float width;
uniform float height;
uniform float strength;

varying vec2 v_texCoords;

void main() {
    vec4 col = texture2D(u_texture0, v_texCoords);

    if(col.a > 0.0) {
        float lerp = col.b * col.a;
        float rx = (col.r - 0.5) * 2.0;
        float gy = (col.g - 0.5) * 2.0;

        vec2 off = vec2(rx * lerp, gy * lerp);

        float w = strength / width;
        float h = strength / height;

        gl_FragColor = vec4(texture2D(u_texture1, v_texCoords + vec2(off.r * w, off.g * h)).rgb, 1.0);
    }
    else{
        gl_FragColor = vec4(0.0);
    }
}
