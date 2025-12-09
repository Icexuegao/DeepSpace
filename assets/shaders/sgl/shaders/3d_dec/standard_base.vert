
attribute vec3 a_position;
attribute vec2 a_texCoord0;
attribute vec2 a_texCoord1;
attribute vec2 a_texCoord2;
attribute vec4 a_color;
attribute vec3 a_normal;
attribute vec3 a_tangent;

uniform mat4 u_proj;
uniform mat4 u_view;
uniform mat4 u_transform;

varying vec2 v_texCoords;
varying vec4 v_color;
varying vec3 v_fragPos;
varying vec3 v_normal;

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
    v_texCoords = a_texCoord0;
    v_color = a_color;
    mat3 v = transpose(inverse(mat3(
          u_transform[0][0], u_transform[0][1], u_transform[0][2],
          u_transform[1][0], u_transform[1][1], u_transform[1][2],
          u_transform[2][0], u_transform[2][1], u_transform[2][2]
    )));
    v_normal = v * a_normal;
    v_fragPos = vec3(u_transform * vec4(a_position, 1.0));
    //v_normal = a_normal;
    //v_fragPos = a_position;

    gl_Position = u_proj * u_view * u_transform * vec4(a_position, 1.0);
}
