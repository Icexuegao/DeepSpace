package singularity.graphic.graphic3d.shaders;

import arc.Core;
import arc.graphics.Gl;
import arc.graphics.Pixmap;
import arc.graphics.Texture;
import arc.graphics.VertexAttribute;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.GLFrameBuffer;
import arc.graphics.gl.Shader;
import arc.struct.Seq;
import singularity.graphic.SglShaders;
import singularity.graphic.graphic3d.LightSource;
import singularity.graphic.graphic3d.ShaderProgram;
import singularity.graphic.graphic3d.Stage3D;

public class StandardGeneralShaderProgram extends ShaderProgram {
  public Texture diffuseTexture;
  public Texture specularTexture;
  public Texture normalTexture;

  @Override
  protected Pass[] buildPasses() {
    return new Pass[]{
        new Pass(){
          final Seq<LightSource> lights = new Seq<>(LightSource.class);

          Shader buildShd(int lights){
            return new Shader(
                Core.files.internal("shaders/screenspace.vert"),
                SglShaders.internalShaderDir.child("3d").child("standard_pass.frag")
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
            builder.addBasicDepthRenderBuffer();
            builder.addBasicColorTextureAttachment(Pixmap.Format.rgba8888);
            builder.addDepthTextureAttachment(Gl.rgba, Gl.unsignedByte);

            return builder.build();
          }

          @Override
          protected void applyShader(Shader shader, Seq<Texture> lastTextures) {
            LightSource[] arr = lights.items;
            for (int i = 0; i < arr.length; i++) {
              arr[i].apply(shader, i);
            }

            for (int i = 0; i < lastTextures.size; i++) {
              lastTextures.get(i).bind(i);
            }
            shader.setUniformi("u_positionTex", 0);
            shader.setUniformi("u_colorTex", 1);
            shader.setUniformi("u_specularTex", 2);
            shader.setUniformi("u_normalDirTex", 3);
            shader.setUniformi("u_tangentTex", 4);
            shader.setUniformi("u_bitangentTex", 5);
            shader.setUniformi("u_normalTex", 6);
            shader.setUniformi("u_depthTex", 7);
          }

          @Override
          protected void blit(Shader shader) {
            gBuffer.getTextureAttachments().get(0).bind(0);
            gBuffer.getTextureAttachments().get(1).bind(1);
            shader.bind();
            shader.apply();
            shader.setUniformi("u_texture", 0);
            shader.setUniformi("u_depth", 1);

            Draw.blit(shader);
          }
        }
    };
  }

  @Override
  protected Shader setupShader() {
    return new Shader(
        SglShaders.internalShaderDir.child("3d").child("standard.vert"),
        SglShaders.internalShaderDir.child("3d").child("standard.frag")
    );
  }

  @Override
  protected FrameBuffer buildBuffer() {
    GLFrameBuffer.FrameBufferBuilder builder = new GLFrameBuffer.FrameBufferBuilder(
        Core.graphics.getWidth(),
        Core.graphics.getHeight()
    );
    builder.addBasicDepthRenderBuffer();
    builder.addBasicColorTextureAttachment(Pixmap.Format.rgba8888);
    builder.addBasicColorTextureAttachment(Pixmap.Format.rgba8888);
    builder.addBasicColorTextureAttachment(Pixmap.Format.rgba8888);
    builder.addBasicColorTextureAttachment(Pixmap.Format.rgba8888);
    builder.addBasicColorTextureAttachment(Pixmap.Format.rgba8888);
    builder.addBasicColorTextureAttachment(Pixmap.Format.rgba8888);
    builder.addBasicColorTextureAttachment(Pixmap.Format.rgba8888);
    builder.addDepthTextureAttachment(Gl.rgba, Gl.unsignedByte);

    return builder.build();
  }

  @Override
  protected void applyShader(Shader shader) {
    diffuseTexture.bind(0);
    specularTexture.bind(1);
    normalTexture.bind(2);

    shader.setUniformi("u_texture", 0);
    shader.setUniformi("u_specularTex", 1);
    shader.setUniformi("u_normalTex", 2);

    shader.setUniformMatrix4("u_proj", camera.projection.val);
    shader.setUniformMatrix4("u_view", camera.view.val);
  }

  @Override
  protected VertexAttribute[] meshFormat() {
    return new VertexAttribute[0];
  }
}
