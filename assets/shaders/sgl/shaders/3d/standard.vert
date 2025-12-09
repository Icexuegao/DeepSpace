
in vec3 a_position;
in vec2 a_texCoord0;
in vec2 a_texCoord1;
in vec2 a_texCoord2;
in vec3 a_normal;
in vec3 a_tangent;
in vec4 a_color;

uniform mat4 u_proj;
uniform mat4 u_view;
uniform mat4 u_transform;

out vec2 v_diffCoords;
out vec2 v_normCoords;
out vec2 v_specCoords;
out vec4 v_color;
out vec3 v_position;
out vec3 v_tbn;

#if version < 300
mat3 inverse(mat3 m) {
    float a00 = m[1][1] * m[2][2] - m[1][2] * m[2][1];
    float a01 = m[1][0] * m[2][2] - m[1][2] * m[2][0];
    float a02 = m[1][0] * m[2][1] - m[1][1] * m[2][0];
    float det = m[0][0] * a00 - m[0][1] * a01 + m[0][2] * a02;

    float invDet = 1.0 / det;

    mat3 result;
    result[0][0] = a00 * invDet;
    result[0][1] = (m[0][2] * m[2][1] - m[0][1] * m[2][2]) * invDet;
    result[0][2] = (m[0][1] * m[1][2] - m[0][2] * m[1][1]) * invDet;
    result[1][0] = a01 * invDet;
    result[1][1] = (m[0][0] * m[2][2] - m[0][2] * m[2][0]) * invDet;
    result[1][2] = (m[0][2] * m[1][0] - m[0][0] * m[1][2]) * invDet;
    result[2][0] = a02 * invDet;
    result[2][1] = (m[0][1] * m[2][0] - m[0][0] * m[2][1]) * invDet;
    result[2][2] = (m[0][0] * m[1][1] - m[0][1] * m[1][0]) * invDet;

    return result;
}

mat3 transpose(mat3 m) {
    mat3 result;
    result[0][0] = m[0][0];
    result[0][1] = m[1][0];
    result[0][2] = m[2][0];
    result[1][0] = m[0][1];
    result[1][1] = m[1][1];
    result[1][2] = m[2][1];
    result[2][0] = m[0][2];
    result[2][1] = m[1][2];
    result[2][2] = m[2][2];

    return result;
}
#endif

void main(){
    v_diffCoords = a_texCoord0;
    v_normCoords = a_texCoord1;
    v_specCoords = a_texCoord2;
    v_color = a_color;
    v_position = (u_transform * vec4(a_position, 1.0)).xyz;

    vec3 bitangent = normalize(cross(a_normal, a_tangent));
    mat3 trans = transpose(inverse(mat3(
        u_transform[0][0], u_transform[0][1], u_transform[0][2],
        u_transform[1][0], u_transform[1][1], u_transform[1][2],
        u_transform[2][0], u_transform[2][1], u_transform[2][2]
    )));
    vec3 T = normalize(a_tangent);
    vec3 B = normalize(bitangent);
    vec3 N = normalize(a_normal);

    v_tbn = trans * transpose(mat3(T, B, N));

    gl_Position = u_proj * u_view * u_transform * vec4(a_position, 1.0);
}
