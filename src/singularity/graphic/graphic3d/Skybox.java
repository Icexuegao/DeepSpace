package singularity.graphic.graphic3d;

import arc.graphics.*;
import arc.graphics.gl.Shader;
import arc.math.geom.Mat3D;
import singularity.graphic.SglShaders;

public class Skybox {
  private static final float[] vertices = {
      -1.0f,  1.0f, -1.0f,
      -1.0f, -1.0f, -1.0f,
      1.0f, -1.0f, -1.0f,
      1.0f, -1.0f, -1.0f,
      1.0f,  1.0f, -1.0f,
      -1.0f,  1.0f, -1.0f,

      -1.0f, -1.0f,  1.0f,
      -1.0f, -1.0f, -1.0f,
      -1.0f,  1.0f, -1.0f,
      -1.0f,  1.0f, -1.0f,
      -1.0f,  1.0f,  1.0f,
      -1.0f, -1.0f,  1.0f,

      1.0f, -1.0f, -1.0f,
      1.0f, -1.0f,  1.0f,
      1.0f,  1.0f,  1.0f,
      1.0f,  1.0f,  1.0f,
      1.0f,  1.0f, -1.0f,
      1.0f, -1.0f, -1.0f,

      -1.0f, -1.0f,  1.0f,
      -1.0f,  1.0f,  1.0f,
      1.0f,  1.0f,  1.0f,
      1.0f,  1.0f,  1.0f,
      1.0f, -1.0f,  1.0f,
      -1.0f, -1.0f,  1.0f,

      -1.0f,  1.0f, -1.0f,
      1.0f,  1.0f, -1.0f,
      1.0f,  1.0f,  1.0f,
      1.0f,  1.0f,  1.0f,
      -1.0f,  1.0f,  1.0f,
      -1.0f,  1.0f, -1.0f,

      -1.0f, -1.0f, -1.0f,
      -1.0f, -1.0f,  1.0f,
      1.0f, -1.0f, -1.0f,
      1.0f, -1.0f, -1.0f,
      -1.0f, -1.0f,  1.0f,
      1.0f, -1.0f,  1.0f
  };

  protected Shader shader;
  protected Mesh mesh;

  public Cubemap cubemap;

  public Skybox(Cubemap map){
    this.cubemap = map;
    this.cubemap.setFilter(Texture.TextureFilter.linear);
    this.mesh = new Mesh(true, vertices.length, 0, VertexAttribute.position3);
    mesh.getVerticesBuffer().limit(vertices.length);
    mesh.getVerticesBuffer().put(vertices, 0, vertices.length);

    shader = setupShader();
  }

  public void render(Mat3D proj, float far) {
    cubemap.bind();
    shader.bind();
    shader.setUniformi("u_cubemap", 0);
    shader.setUniformMatrix4("u_proj", proj.val);
    shader.setUniformf("u_far", far);
    mesh.render(shader, Gl.triangles);
  }

  protected Shader setupShader() {
    return new Shader(
        SglShaders.internalShaderDir.child("3d").child("skybox.vert"),
        SglShaders.internalShaderDir.child("3d").child("skybox.frag")
    );
  }
}
