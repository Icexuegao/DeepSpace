vec4 getHex(vec2 p) {
    const vec2 s = vec2(1, ROOT_3);
    vec4 hC = floor(vec4(p, p - vec2(.5, 1)) / s.xyxy) + .5;
    vec4 h = vec4(p - hC.xy * s, p - (hC.zw + .5) * s);
    return dot(h.xy, h.xy) < dot(h.zw, h.zw) ? vec4(h.xy, hC.xy) : vec4(h.zw, hC.zw + .5);
}

float hexDist(in vec2 p) {
    const vec2 s = vec2(1, ROOT_3);
    p = abs(p);
    return max(dot(p, s * .5), p.x);
}

vec3 mainPass(in vec2 fragCoord) {
    const float r = PI / 6.0;
    const mat2 rot = mat2(cos(r), sin(r), -sin(r), cos(r));

    vec2 uv0 = 2.0 * (fragCoord / iResolution.xy) - 1.0;
    uv0.x *= iResolution.x / iResolution.y;
    vec2 uv = uv0;

    vec2 h0 = getHex(0.5 * uv).xy;
    vec2 h = h0;

    float d0 = length(uv0);
    vec3 color = vec3(0.0);

    for (float i = 0.0; i < 5.0; i++)
    {
        h = getHex(1.1 * ROOT_3 * h * rot).xy;

        float d = hexDist(h);
        d = 2.0 * d * pow(0.2, d0);
        d = 0.5 * sin(4.0 * d - 0.5 * iTime + i * 2.0 * PI / 7.0);
        d = 0.04 / d;
        d = pow(d, 2.0);

        vec3 c = d * palette(length(h0) + 0.3 * i);
        color += c;
    }
    return color;
}

vec3 gamma(in vec3 color) {
    return pow(color, vec3(1.0 / 2.2));
}

void mainImage(out vec4 fragColor, in vec2 fragCoord) {
    vec3 color = mainPass(fragCoord);
    color = gamma(color);
    fragColor = vec4(color, 1.0);
}