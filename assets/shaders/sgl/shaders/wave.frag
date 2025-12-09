#define HIGHP

uniform sampler2D u_texture;

uniform vec2 u_campos;
uniform vec2 u_resolution;

uniform vec4 mix_color;

uniform float u_time;
uniform float mix_alpha;
uniform float mix_omiga;
uniform float wave_scl;
uniform float max_threshold;
uniform float min_threshold;

varying vec2 v_texCoords;

void main() {
    vec4 col = texture2D(u_texture, v_texCoords).rgba;

    if(col.a > 0.0){
        vec2 c = v_texCoords;
        vec2 coords = vec2(c.x * u_resolution.x + u_campos.x, c.y * u_resolution.y + u_campos.y);

        float stime = u_time*wave_scl;

        float res = (sin((coords.x + coords.y)*mix_omiga + stime) + 1.0)/2.0;

        float alpha = max(min((res - min_threshold)/(max_threshold - min_threshold), 1.0), 0.0)*mix_alpha;

        gl_FragColor = mix(col, mix_color, alpha);
    }
    else {
        gl_FragColor = col;
    }
}