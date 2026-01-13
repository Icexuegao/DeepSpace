package singularity.graphic.graphic3d.shaders;

import arc.Core;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.VertexAttribute;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.GLFrameBuffer;
import arc.graphics.gl.Shader;
import arc.struct.Seq;
import singularity.graphic.SglShaders;
import singularity.graphic.graphic3d.LightSource;
import singularity.graphic.graphic3d.ShaderProgram;
import singularity.graphic.graphic3d.Stage3D;

public class PlanetShaderProgram extends ShaderProgram {
  private static final VertexAttribute[] meshFormat = {
      VertexAttribute.position3,
      VertexAttribute.normal,
      VertexAttribute.color
  };

  @Override
  protected Pass[] buildPasses() {
    return new Pass[]{
        new Pass(){
          final Seq<LightSource> lights = new Seq<>(LightSource.class);

          Shader buildShd(int lights){
            return new SglShaders.SglShader(
                Core.files.internal("shaders/screenspace.vert"),
                SglShaders.internalShaderDir.child("3d").child("planet_pass.frag")
            ){
              @Override
              protected String preprocess(String source, boolean fragment) {
                if (fragment) source = "#define LIGHTS_COUNT " + lights + "\n" + source;
                return super.preprocess(source, fragment);
              }
            };
          }

          @Override
          public void prePass(Stage3D stage) {
            if (lights.size != stage.lightSources.size){
              if (passShader != null) passShader.dispose();
              passShader = buildShd(stage.lightSources.size);
            }

            lights.clear();
            lights.addAll(stage.lightSources);
          }

          @Override
          protected Shader setupShader() {
            return buildShd(1);
          }

          @Override
          protected FrameBuffer buildBuffer() {
            GLFrameBuffer.FrameBufferBuilder builder = new GLFrameBuffer.FrameBufferBuilder(
                Core.graphics.getWidth(),
                Core.graphics.getHeight()
            );
            builder.addBasicColorTextureAttachment(Pixmap.Format.rgba8888);

            builder.addBasicDepthRenderBuffer();

            return builder.build();
          }

          @Override
          protected void applyShader(Shader shader, Seq<Texture> lastTextures) {
            LightSource[] arr = lights.items;
            for (int i = 0; i < lights.size; i++) {
              arr[i].apply(shader, i);
            }

            for (int i = 0; i < lastTextures.size; i++) {
              lastTextures.get(i).bind(i);
            }
            shader.setUniformi("u_positionTex", 0);
            shader.setUniformi("u_colorTex", 1);
            shader.setUniformi("u_specularTex", 2);
            shader.setUniformi("u_normalTex", 3);
          }

          @Override
          protected void blit(Shader shader) {
            shader.bind();
            shader.apply();
            shader.setUniformi("u_texture", 0);

            gBuffer.blit(shader);
          }
        }
    };
  }

  @Override
  protected VertexAttribute[] meshFormat() {
    return meshFormat;
  }

  @Override
  protected Shader setupShader() {
    return SglShaders.planet;
  }

  @Override
  protected FrameBuffer buildBuffer() {
    GLFrameBuffer.FrameBufferBuilder builder = new GLFrameBuffer.FrameBufferBuilder(
        Core.graphics.getWidth(),
        Core.graphics.getHeight()
    );
    builder.addBasicColorTextureAttachment(Pixmap.Format.rgba8888);
    builder.addBasicColorTextureAttachment(Pixmap.Format.rgba8888);
    builder.addBasicColorTextureAttachment(Pixmap.Format.rgba8888);
    builder.addBasicColorTextureAttachment(Pixmap.Format.rgba8888);

    builder.addBasicDepthRenderBuffer();

    return builder.build();
  }

  @Override
  protected void applyShader(Shader shader) {}
}
