package singularity.graphic.graphic3d;

import arc.graphics.gl.FrameBuffer;

public abstract class ForwardRenderShaderProgram extends ShaderProgram {
  @Override
  protected boolean isForwardRender() {
    return true;
  }

  @Override
  protected Pass[] buildPasses() {
    return null;
  }

  @Override
  protected FrameBuffer buildBuffer() {
    return null;
  }
}
