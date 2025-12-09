#define HIGHP
#define PI 3.14159265359

uniform sampler2D u_texture;

uniform vec2 u_campos;
uniform vec2 u_resolution;

uniform vec4 mix_color;

uniform vec2 offset;
uniform float u_step;
uniform float u_time;
uniform float u_stroke;
uniform float mix_alpha;
uniform float u_alpha;
uniform float wave_scl;
uniform float max_threshold;
uniform float min_threshold;

uniform float side_len;

varying lowp vec2 v_texCoords;

const float TR = 1.73205;
const float TB = 3.0;

vec2 selectCenter(vec2 coords){
    int indexX = int(coords.x / TB / side_len);
    int indexY = int(coords.y / TR / side_len);
    vec2 v1, v2, samp;

    if(indexX / 2 * 2 == indexX) {
        if(indexY / 2 * 2 == indexY) {
            v1 = vec2(float(indexX) * side_len * TB, float(indexY) * side_len * TR);
            v2 = vec2(float(indexX + 1) * side_len * TB, float(indexY + 1) * side_len * TR);
        }else{
            v1 = vec2(float(indexX) * side_len * TB, float(indexY + 1) * side_len * TR);
            v2 = vec2(float(indexX + 1) * side_len * TB, float(indexY) * side_len * TR);
        }
    }else{
        if(indexY / 2 * 2 == indexY) {
            v1 = vec2(float(indexX) * side_len * TB, float(indexY + 1) * side_len * TR);
            v2 = vec2(float(indexX + 1) * side_len * TB, float(indexY) * side_len * TR);
        }else{
            v1 = vec2(float(indexX) * side_len * TB, float(indexY) * side_len * TR);
            v2 = vec2(float(indexX + 1) * side_len * TB, float(indexY + 1) * side_len * TR);
        }
    }

    float s1 = distance(coords, v1);
    float s2 = distance(coords, v2);

    if(s1 < s2){//use v1
        samp = v1;
    }else{//use v2
        samp = v2;
    }

    return samp;
}

float random (vec2 st){
    return fract(sin(dot(st.xy, vec2(12.9898,78.233))) * 43758.5453123);
}

void main(void) {
    vec4 baseColor = texture2D(u_texture, v_texCoords);

    vec2 v = 1.0 / u_resolution;

    vec4 maxed = max(max(max(
    texture2D(u_texture, v_texCoords + vec2(0, u_step) * v),
    texture2D(u_texture, v_texCoords + vec2(0, -u_step) * v)),
    texture2D(u_texture, v_texCoords + vec2(u_step, 0) * v)),
    texture2D(u_texture, v_texCoords + vec2(-u_step, 0) * v));

    vec2 worldCoord = vec2(v_texCoords.x * u_resolution.x + u_campos.x, v_texCoords.y * u_resolution.y + u_campos.y);
    worldCoord += offset;

    float time = u_time*0.1;
    float a =
    sin((worldCoord.x + worldCoord.y)*0.0831 + time) +
    sin((-worldCoord.x + worldCoord.y)*0.075 + time) +
    sin((worldCoord.x - worldCoord.y)*0.0546 + time) +
    sin((-worldCoord.x - worldCoord.y)*0.03432 + time);
    a = (a/4.0 + 1.0)/2.0;

    if(texture2D(u_texture, v_texCoords).a < 0.9 && maxed.a > 0.9){
        gl_FragColor = mix(vec4(maxed.rgb, 1.0), vec4(mix_color.rgb, 1.0), a);
    }else {
        if (baseColor.a <= 0.0){
            gl_FragColor = vec4(0.0);
        }
        else {
            vec2 coords = selectCenter(worldCoord);

            vec2 diff = worldCoord - coords;
            float dst = distance(worldCoord, coords);
            float angle = mod(atan(diff.y, diff.x), PI/3.0);
            float realRad = TR*side_len/(sin(2.0*PI/3.0 - angle));

            if (realRad - dst < u_stroke){
                gl_FragColor = mix(vec4(baseColor.rgb, u_alpha), mix_color, a);
            }
            else{
                float stime = u_time*wave_scl;
                float res = (sin(stime*(0.5 + random(coords)) + random(coords)*PI*2.0) + 1.0)/2.0;

                float alpha = max(min((res - min_threshold)/(max_threshold - min_threshold), 1.0), 0.0);

                gl_FragColor = mix(vec4(baseColor.rgb, u_alpha), vec4(mix_color.rgb, mix_alpha), alpha);
            }
        }
    }
}
