package singularity.graphic.graphic3d.shaders;

import arc.graphics.VertexAttribute;
import arc.graphics.gl.Shader;
import singularity.graphic.SglShaders;
import singularity.graphic.graphic3d.ForwardRenderShaderProgram;

public class SolarShaderProgram extends ForwardRenderShaderProgram {
  private static final VertexAttribute[] meshFormat = new VertexAttribute[]{
      VertexAttribute.position3,
      VertexAttribute.normal,
      VertexAttribute.color
  };

  @Override
  protected Shader setupShader() {
    return SglShaders.solar;
  }

  @Override
  protected void applyShader(Shader shader) {}

  @Override
  protected VertexAttribute[] meshFormat() {
    return meshFormat;
  }
}
