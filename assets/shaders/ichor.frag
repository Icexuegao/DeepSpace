#define HIGHP
uniform sampler2D u_texture;
uniform sampler2D u_noise;

uniform vec2 u_campos;//屏幕中心
uniform vec2 u_resolution;//分辨率
uniform float u_time;//游戏刻

varying vec2 v_texCoords;
const float NSCALE = 90.0;

const float mscl = 40.0;
const float mth = 7.0;

void main(){
    vec2 c = v_texCoords;
    vec2 v = vec2(1.0/u_resolution.x, 1.0/u_resolution.y);
    vec2 coords = vec2(c.x / v.x + u_campos.x, c.y / v.y + u_campos.y);
    float stime = u_time / 5.0;
    vec4 color =  texture2D(u_texture, c + vec2(sin(stime/3.0 + coords.y/0.75) * v.x, 0.0));

    color.rgb *= vec3(0.9, 0.9, 1.0);

    float tester = mod((coords.x + coords.y * 1.1 + sin(stime / 8.0 + coords.x / 5.0 - coords.y / 100.0) * 2.0) +
    sin(stime / 20.0 + coords.y / 3.0) * 1.0 +
    sin(stime / 10.0 - coords.y / 2.0) * 2.0 +
    sin(stime / 7.0 + coords.y / 1.0) * 0.5 +
    sin(coords.x * 3.0 + coords.y / 2.0) +
    sin(stime / 20.0 + coords.x * 4.0) * 1.0, mscl);

    if (tester < mth){
        color *= 1.2;
    }

    float btime = u_time / 8000.0;

    float noise = (texture2D(u_noise, coords / NSCALE + vec2(btime) * vec2(-0.9, 0.8)).r +
    texture2D(u_noise, coords / NSCALE + vec2(btime * 1.1) * vec2(-0.8, -1.0)).r) / 2.0;

    if (!(noise > 0.54 && noise < 0.58)){
        color.rgb *= vec3(0.6, 0.6, 0.7);
    }

    gl_FragColor =color;

}
