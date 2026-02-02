package singularity.graphic.graphic3d;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.Draw;
import arc.graphics.g3d.Camera3D;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.GLFrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.geom.BoundingBox;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.OrderedSet;
import arc.struct.Seq;

public abstract class ShaderProgram implements Comparable<ShaderProgram> {
  private static final Mat3D matrix = new Mat3D();

  /**着色程序优先级*/
  public float priority = 0;
  /**渲染过程是否开启深度测试*/
  public boolean depthTest = true;
  /**渲染过程是否开启深度写入*/
  public boolean depthMask = true;
  /**渲染过程是否开启面剔除*/
  public boolean cullFace = true;
  /**面剔除模式*/
  public int cullFunc = Gl.back;

  // Material model
  public OrderedSet<ShaderData<?>> shaderData = new OrderedSet<>();
  protected int textureUnitActives = 0;

  // Stage status
  public boolean renderValid;
  // Stage uniforms
  protected Camera3D camera;
  protected Color ambientLight;
  protected Vec3 minPos = new Vec3();
  protected Vec3 maxPos = new Vec3();

  // Deferred rendering data
  protected Pass[] passes;
  protected FrameBuffer gBuffer;
  protected Shader baseShader;
  protected boolean drawingObjects = false;

  public ShaderProgram(){
    init();
  }

  public void init(){
    baseShader = setupShader();
    passes = buildPasses();
    gBuffer = buildBuffer();

    if (!isForwardRender()) {
      for (Pass pass : passes) {
        pass.init();
      }
    }
  }

  public <T> ShaderData<T> assignData(ShaderData<T> data){
    shaderData.add(data);
    return data;
  }

  public void setupStage(Stage3D stage) {
    this.camera = stage.camera3D;
    this.ambientLight = stage.ambientLight;

    BoundingBox box = stage.boundingBox;
    minPos.set(box.min);
    maxPos.set(box.max);
  }

  public void prePass(Stage3D stage){
    for (Pass pass : passes) {
      pass.prePass(stage);
    }
  }

  protected boolean isForwardRender(){
    return false;
  }

  protected abstract Pass[] buildPasses();
  protected abstract Shader setupShader();
  protected abstract FrameBuffer buildBuffer();
  protected abstract void applyShader(Shader shader);
  protected abstract VertexAttribute[] meshFormat();

  public void reset() {
    if (isForwardRender()) return;

    gBuffer.begin();
    Gl.depthMask(depthMask);
    Gl.clear(Gl.depthBufferBit | Gl.colorBufferBit);
    Gl.clearDepthf(1f);
    Gl.clearColor(0f, 0f, 0f, 0f);
    gBuffer.end();
  }

  public void begin(){
    if (isForwardRender() || drawingObjects) return;
    drawingObjects = true;

    gBuffer.begin();
    if (depthTest) Gl.enable(Gl.depthTest);
    else Gl.disable(Gl.depthTest);
    Gl.depthMask(depthMask);
    if (cullFace) {
      Gl.enable(Gl.cullFace);
      Gl.cullFace(cullFunc);
    }
    else Gl.disable(Gl.cullFace);
  }

  public void drawObject(RendererObject object){
    if (!isForwardRender() && !drawingObjects) throw new IllegalStateException("Material passing not started");

    Shader shader = baseShader;
    shader.bind();
    shader.apply();
    applyShader(shader);
    shader.setUniformMatrix4("u_proj", camera.projection.val);
    shader.setUniformMatrix4("u_view", camera.view.val);
    shader.setUniformf("u_ambientLight", ambientLight);
    shader.setUniformf("u_minPos", minPos);
    shader.setUniformf("u_maxPos", maxPos);
    shader.setUniformMatrix4("u_transform", object.getAbsTransform(matrix).val);

    object.mesh().render(shader, object.verticesPrimitive(), object.meshOffset(), object.meshCount());
  }

  public void end(){
    if (isForwardRender() || !drawingObjects) return;
    drawingObjects = false;

    gBuffer.end();
  }

  public void blitDepth(GLFrameBuffer<?> target) {
    if (isForwardRender()) return;
    Gl.bindFramebuffer(GL30.GL_READ_FRAMEBUFFER, gBuffer.getFramebufferHandle());
    Gl.bindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, target == null? 0: target.getFramebufferHandle());
    Core.gl30.glBlitFramebuffer(
        0, 0, gBuffer.getWidth(), gBuffer.getHeight(),
        0, 0, Core.graphics.getWidth(), Core.graphics.getHeight(),
        Gl.depthBufferBit, Gl.nearest
    );
  }

  public void passing(){
    if (isForwardRender()) return;

    Seq<Texture> lastTextures = gBuffer.getTextureAttachments();
    for (Pass pass : passes){
      lastTextures = pass.pass(lastTextures);
    }
  }

  public void blit(Shader shader){
    if (isForwardRender()) return;

    if (passes.length == 0) {
      gBuffer.blit(shader);
    }
    else {
      passes[passes.length - 1].blit(shader);
    }
  }

  @Override
  public int compareTo(ShaderProgram o) {
    return Float.compare(priority, o.priority);
  }

  public void checkMesh(Mesh mesh) {
    VertexAttribute[] format = meshFormat();
    if (mesh.attributes.length != format.length)
      throw new IllegalArgumentException("Mesh format doesn't match material format");

    for (int i = 0; i < mesh.attributes.length; i++) {
      VertexAttribute attr = mesh.attributes[i];
      VertexAttribute form = format[i];

      if (!attr.alias.equals(form.alias) || attr.size != form.size || attr.type != form.type || attr.components != form.components)
        throw new IllegalArgumentException("Mesh format doesn't match material format");
    }
  }

  public abstract class Pass {
    protected FrameBuffer gBuffer;
    protected Shader passShader;

    protected abstract Shader setupShader();
    protected abstract FrameBuffer buildBuffer();
    protected abstract void applyShader(Shader shader, Seq<Texture> lastTextures);
    protected abstract void blit(Shader shader);

    public void init(){
      passShader = setupShader();
      gBuffer = buildBuffer();
    }

    public Seq<Texture> pass(Seq<Texture> lastTextures){
      gBuffer.begin(Color.clear);
      passShader.bind();
      passShader.apply();
      passShader.setUniformf("u_minPos", minPos);
      passShader.setUniformf("u_maxPos", maxPos);
      passShader.setUniformf("u_cameraPos", camera.position);
      passShader.setUniformf("u_ambientLight", ambientLight);
      applyShader(passShader, lastTextures);
      Draw.blit(passShader);
      gBuffer.end();

      return gBuffer.getTextureAttachments();
    }

    public void prePass(Stage3D stage) {}
  }
}
