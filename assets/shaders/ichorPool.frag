uniform sampler2D u_texture;
uniform sampler2D u_noise;//噪音

uniform vec2 u_campos;//屏幕中心
uniform vec2 u_resolution;//分辨率
uniform float u_time;//游戏刻

varying vec2 v_texCoords;
#define NSCALE 180.0 / 2.0

const float mscl = 40.0;
const float mth = 7.0;

void main(){
    vec2 c = v_texCoords;
    vec2 v = vec2(1.0/u_resolution.x, 1.0/u_resolution.y);
    vec2 coords = vec2(c.x / v.x + u_campos.x, c.y / v.y + u_campos.y);

    float stime = u_time / 5.0;

    vec4 sampled = texture2D(u_texture, c + vec2(sin(stime/3.0 + coords.y/0.75) * v.x, 0.0));
    vec3 color = sampled.rgb * vec3(0.9, 0.9, 1);

    float tester = mod((coords.x + coords.y*1.1 + sin(stime / 8.0 + coords.x/5.0 - coords.y/100.0)*2.0) +
    sin(stime / 20.0 + coords.y/3.0) * 1.0 +
    sin(stime / 10.0 - coords.y/2.0) * 2.0 +
    sin(stime / 7.0 + coords.y/1.0) * 0.5 +
    sin(coords.x *3.0 + coords.y / 2.0) +
    sin(stime / 20.0 + coords.x*4.0) * 1.0, mscl);

    if (tester < mth){
        color *= 1.2;
    }



    float btime = u_time / 8000.0;
    float noise = (texture2D(u_noise, (coords) / NSCALE + vec2(btime) * vec2(-0.9, 0.8)).r + texture2D(u_noise, (coords) / NSCALE + vec2(btime * 1.1) * vec2(-0.8, -1.0)).r) / 2.0;


    if (!(noise > 0.54 && noise < 0.58)){
        color.rgb *= vec3(0.6, 0.6, 0.7);
    }

    gl_FragColor = vec4(color.rgb, min(sampled.a * 100.0, 1.0));
}





















/*
void main(){
    // 计算波纹效果
    float waveX = sin(u_time * u_waveSpeed + v_texCoords.x * 10.0) * u_waveScale;
    float waveY = cos(u_time * u_waveSpeed + v_texCoords.y * 10.0) * u_waveScale;
    vec2 distortedTexCoord = v_texCoords + vec2(waveX, waveY);

    // 确保纹理坐标在0到1之间
    distortedTexCoord = fract(distortedTexCoord);

    // 采样纹理
    vec4 color = texture(u_texture, distortedTexCoord);

    gl_FragColor = color;


   // vec2 c = v_texCoords.xy;
   // vec2 coords = vec2(c.x * u_resolution.x + u_campos.x, c.y * u_resolution.y + u_campos.y)/100;


   */
/* vec2 c = v_texCoords.xy;
    vec2 coords = vec2(c.x * u_resolution.x + u_campos.x, c.y * u_resolution.y + u_campos.y);

    float btime = u_time / 5000.0;
    float noise = (texture2D(u_noise, (coords) / NSCALE + vec2(btime) * vec2(0.9, -0.8)).r +

    texture2D(u_noise, (coords) / NSCALE + vec2(btime * 1.1) * vec2(-0.8, 1.0)).r) / 2.0;
    vec4 color = texture2D(u_texture, c);

    if (noise > 0.54){
        color.rgb = S1;
    }
    if(noise>0.6){
        color.rgb=S2;
    }

    gl_FragColor = color;*//*




    // 叠加纹理颜色

  // vec4 color = texture2D(u_texture, v_texCoords.xy);

    // 直接丢弃原本就透明的像素
   // if (color.a == 0.0) discard;

    // 初相位（正值表现为向左移动，负值则表现为向右移动）
    // cc.time 是 Cocos Creator 提供的运行时间全局变量（类型：vec4）
  //  float initiaPhase = frequency * u_time;

    // 代入正弦曲线公式计算 y 值
    // y = Asin(ωx ± φt) + k
   // float y = amplitude * cos(angularVelocity * coords.x + initiaPhase) + offset;

    // 丢弃 y 值以上的像素（左上角为原点 [0.0, 0.0]）
  //  if (coords.y < y) discard;

    // 输出颜色
   // gl_FragColor = color;
}*/
