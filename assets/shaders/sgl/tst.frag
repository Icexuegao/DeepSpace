
const float readins2degress = 180.0 / 3.1415926;

vec3 hsv2rgb(vec3 hsv) {
    float h = mod(hsv.x / 60.0 + 6.0, 6.0);
    int i = int(h);
    float s = clamp(hsv.y, 0.0, 1.0);
    float v = clamp(hsv.z, 0.0, 1.0);

    float f = h - float(i);
    float p = v * (1.0 - s);
    float q = v * (1.0 - s * f);
    float t = v * (1.0 - s * (1.0 - f));

    switch (i) {
        case 0: return vec3(v, t, p);
        case 1: return vec3(q, v, p);
        case 2: return vec3(p, v, t);
        case 3: return vec3(p, q, v);
        case 4: return vec3(t, p, v);
        default: return vec3(v, p, q);
    }
}

void mainImage(out vec4 fragColor, in vec2 fragCoord)
{
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = fragCoord/iResolution.xy;
    vec2 originaled = uv * 2.0 - 1.0;

    float len = length(originaled);
    float angle = atan(originaled.x, originaled.y);

    // By hsv
    vec3 col = hsv2rgb(vec3(angle*readins2degress, len, 1.0));

    // Output to screen
    fragColor = vec4(col, 1.0);
}
