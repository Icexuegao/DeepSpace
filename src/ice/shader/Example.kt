package ice.shader

import arc.graphics.Mesh
import arc.graphics.VertexAttribute
import arc.graphics.g2d.Draw
import arc.graphics.gl.Shader
import ice.graphics.IStyles

class Example {
    val shader = Shader(
        """
        attribute vec2 a_position;
        attribute vec2 a_texCoord0;

        varying vec2 v_texCoord;
         uniform float x;
         uniform float y;
        void main() {
            v_texCoord = a_texCoord0;
            gl_Position = vec4(a_position.x+x,a_position.y+y, 0.0, 1.0);
        }
    """, """
      uniform sampler2D u_texture0;

varying vec2 v_texCoord;

void main() {
    vec4 c1 = texture2D(u_texture0, v_texCoord);
    
    gl_FragColor = c1;
}
    """
    )
    val mesh = Mesh(
        true, 4, 6,
        VertexAttribute.position,
        VertexAttribute.texCoords
    )

    init {
        mesh.setIndices(
            shortArrayOf(
                0, 1, 2, //第一个三角形
                0, 2, 3  //第二个三角形
            )
        )

    }

    fun draw() {
        Draw.shader(shader)
        Draw.rect(IStyles.time,200f,200f)
        Draw.reset()
     /*   mesh.setVertices(
            floatArrayOf(
                //顶点坐标       纹理坐标
                -1f, -1f, 1f, 1f,
                0.5f, -0.5f, 0f, 1f,
                0.5f, 0.5f, 0f, 0f,
                -1f, 1f , 1f, 0f,
            )
        )

        shader.bind()
        tex1.bind(0)
        shader.setUniformi("u_texture0", 0)
        shader.setUniformf("x",0f)
        shader.setUniformf("y",0f)
        Gl.activeTexture(Gl.texture0)
        mesh.render(shader, Gl.triangles)*/
    }
}