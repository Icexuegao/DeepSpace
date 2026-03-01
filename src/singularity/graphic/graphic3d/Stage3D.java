package singularity.graphic.graphic3d;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.Pixmap;
import arc.graphics.g3d.Camera3D;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.math.geom.BoundingBox;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.ObjectMap;
import arc.struct.OrderedMap;
import arc.struct.OrderedSet;
import singularity.graphic.SglShaders;
import singularity.world.GameObject;
import universecore.util.Empties;

import java.util.Comparator;

public class Stage3D {
  private static final Vec3 v1 = new Vec3(), v2 = new Vec3();
  private static final Mat3D mat = new Mat3D();
  private static final BoundingBox b1 = new BoundingBox();

  private int objectIndex = 0;

  public final OrderedSet<GameObject> objects = new OrderedSet<>();
  public final OrderedSet<RendererObject> renderObjects = new OrderedSet<>();
  public final OrderedSet<LightSource> lightSources = new OrderedSet<>();

  public Skybox skybox;

  private final OrderedMap<ShaderProgram, OrderedSet<RendererObject>> forwardRenderGroup = new OrderedMap<>();
  private final OrderedMap<ShaderProgram, OrderedSet<RendererObject>> deferredRenderGroup = new OrderedMap<>();

  public FrameBuffer renderBuffer = new FrameBuffer(
      Pixmap.Format.rgba8888,
      Core.graphics.getWidth(), Core.graphics.getHeight(),
      true, false
  );

  public final Camera3D camera3D = new Camera3D();
  public final BoundingBox boundingBox = new BoundingBox();
  public final Color ambientLight = new Color(0, 0, 0, 0);

  public Shader blitShader;
  public boolean renderSkybox = false;

  private Thread autoUpdateThread;

  public Stage3D(){
    blitShader = new Shader(
        Core.files.internal("shaders/screenspace.vert"),
        SglShaders.internalShaderDir.child("3d").child("base_blit.frag")
    );
  }

  public Comparator<RendererObject> comparator = (a, b) -> Float.compare(
      camera3D.position.dst2(a.getX(), a.getY(), a.getZ()),
      camera3D.position.dst2(b.getX(), b.getY(), b.getZ())
  );

  @SuppressWarnings("BusyWait")
  public void postAutoUpdate(float duration){
    if (autoUpdateThread != null) throw new IllegalStateException("Auto update thread is already running");

    autoUpdateThread = new Thread(() -> {
      while (!Thread.interrupted()) {
        try {
          Thread.sleep((long) (duration*(1000/60f)));
          update();
        } catch (InterruptedException e) {
          break;
        }
      }
    });
    autoUpdateThread.start();
  }

  public void stopAutoUpdate(){
    autoUpdateThread.interrupt();
    autoUpdateThread = null;
  }

  public void update(){
    renderObjects.orderedItems().sort(comparator);
    for (GameObject object : objects) object.update();
  }

  public void renderer(){
    Camera3D cam = camera3D;
    FrameBuffer renderBuf = renderBuffer;

    cam.update();

    renderBuf.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
    renderBuf.begin(Color.clear);

    for (ShaderProgram shaderProgram : forwardRenderGroup.keys()) shaderProgram.renderValid = false;
    for (ShaderProgram shaderProgram : deferredRenderGroup.keys()) shaderProgram.renderValid = false;

    for (RendererObject object : renderObjects) {
     // object.parTransformed(false);
    }

    for (RendererObject object : renderObjects) {
      object.updateParentTransform();
      object.renderValid(checkFrustum(object));
      object.material().shader.renderValid |= object.renderValid();
    }

    calculateBounds(cam);

    for (ShaderProgram shaderProgram : forwardRenderGroup.keys()) {
      if (!shaderProgram.renderValid) continue;

      shaderProgram.reset();
      shaderProgram.setupStage(this);
    }
    for (ShaderProgram shaderProgram : deferredRenderGroup.keys()) {
      if (!shaderProgram.renderValid) continue;

      shaderProgram.reset();
      shaderProgram.setupStage(this);
    }

    Gl.depthMask(true);
    Gl.clear(Gl.depthBufferBit);
    Gl.clearDepthf(1f);
    Gl.depthFunc(Gl.less);

    //DEFERRED RENDERING
    deferredRendering(renderBuf);
    //FORWARD RENDERING
    forwardRendering();

    Gl.enable(Gl.depthTest);
    Gl.depthMask(false);

    renderSkybox(cam); // render skybox at last for better performance with depth test

    renderBuf.end();
    renderBuf.blit(SglShaders.simpleScreen);

    Gl.disable(Gl.depthTest);
    Gl.disable(Gl.cullFace);
    Gl.activeTexture(Gl.texture0);
  }

  protected void renderSkybox(Camera3D cam) {
    if (skybox != null && renderSkybox) {
      Vec3 lastPos = v1.set(cam.position);
      cam.position.setZero();
      cam.update();

      skybox.render(cam.combined, cam.far/Mathf.sqrt2 - 0.1f/*error correction*/);

      cam.position.set(lastPos);
      cam.update();
    }
  }

  protected void calculateBounds(Camera3D cam) {
    float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
    float maxX = -Float.MAX_VALUE, maxY = -Float.MAX_VALUE, maxZ = -Float.MAX_VALUE;

    Vec3 camPos = cam.position;
    minX = Math.min(minX, camPos.x);
    minY = Math.min(minY, camPos.y);
    minZ = Math.min(minZ, camPos.z);
    maxX = Math.max(maxX, camPos.x);
    maxY = Math.max(maxY, camPos.y);
    maxZ = Math.max(maxZ, camPos.z);

    for (RendererObject object : renderObjects) {
      if (!object.renderValid()) continue;

      BoundingBox bounds = object.getTransformedBounds(b1, mat);
      Vec3 min = bounds.min; Vec3 max = bounds.max;
      minX = Math.min(minX, min.x); minY = Math.min(minY, min.y); minZ = Math.min(minZ, min.z);
      maxX = Math.max(maxX, max.x); maxY = Math.max(maxY, max.y); maxZ = Math.max(maxZ, max.z);
    }

    for (LightSource source : lightSources) {
      if (source instanceof RendererObject) continue;

      minX = Math.min(minX, source.getX());
      minY = Math.min(minY, source.getY());
      minZ = Math.min(minZ, source.getZ());

      maxX = Math.max(maxX, source.getX());
      maxY = Math.max(maxY, source.getY());
      maxZ = Math.max(maxZ, source.getZ());
    }

    Vec3 min = boundingBox.min;
    Vec3 max = boundingBox.max;
    min.x = minX;
    min.y = minY;
    min.z = minZ;
    max.x = maxX;
    max.y = maxY;
    max.z = maxZ;
  }

  protected void forwardRendering() {
    boolean modified = false;

    for (ObjectMap.Entry<ShaderProgram, OrderedSet<RendererObject>> entry : forwardRenderGroup) {
      ShaderProgram shaderProgram = entry.key;
      if (!shaderProgram.renderValid) continue;

      Gl.depthMask(shaderProgram.depthMask);
      if (shaderProgram.depthTest) Gl.enable(Gl.depthTest);
      else Gl.disable(Gl.depthTest);
      if (shaderProgram.cullFace) {
        Gl.enable(Gl.cullFace);
        Gl.cullFace(shaderProgram.cullFunc);
      }
      else Gl.disable(Gl.cullFace);

      for (OrderedSet<RendererObject>.OrderedSetIterator iterator = entry.value.iterator(); iterator.hasNext(); ) {
        RendererObject object = iterator.next();
        Material material = object.material();
        ShaderProgram shader = material.shader;
        if (shader != shaderProgram) {
          iterator.remove();
          var group = shader.isForwardRender()? forwardRenderGroup: deferredRenderGroup;
          group.get(shader, OrderedSet::new).add(object);
          modified = true;
          continue;
        }
        if (!object.renderValid()) continue;
        object.renderer();
      }
    }

    if (modified) forwardRenderGroup.orderedKeys().sort();
  }

  protected void deferredRendering(FrameBuffer renderBuf) {
    ShaderProgram lastMat = null;
    boolean modified = false;

    Gl.disable(Gl.blend);
    for (ObjectMap.Entry<ShaderProgram, OrderedSet<RendererObject>> entry : deferredRenderGroup) {
      ShaderProgram shaderProgram = entry.key;
      if (!shaderProgram.renderValid) continue;
      if (lastMat != null) lastMat.blitDepth(shaderProgram.gBuffer);
      shaderProgram.begin();
      for (OrderedSet<RendererObject>.OrderedSetIterator iterator = entry.value.iterator(); iterator.hasNext(); ) {
        RendererObject object = iterator.next();
        Material material = object.material();
        ShaderProgram shader = material.shader;
        if (shader != shaderProgram) {
          iterator.remove();
          var group = shader.isForwardRender()? forwardRenderGroup: deferredRenderGroup;
          group.get(shader, OrderedSet::new).add(object);
          modified = true;
          continue;
        }
        if (!object.renderValid()) continue;
        object.renderer();
      }
      shaderProgram.end();
      lastMat = shaderProgram;
    }

    Gl.enable(Gl.blend);

    if (modified) deferredRenderGroup.orderedKeys().sort();

    Gl.depthMask(true);

    if (lastMat != null) lastMat.blitDepth(renderBuf); // the last material depth will use to forward rendering

    Gl.disable(Gl.depthTest);
    Gl.depthMask(false);
    for (ShaderProgram shaderProgram : deferredRenderGroup.keys()) {
      if (!shaderProgram.renderValid) continue;
      shaderProgram.prePass(this);
      shaderProgram.passing();
    }

    Shader blit = blitShader;
    for (ShaderProgram shaderProgram : deferredRenderGroup.keys()) {
      if (!shaderProgram.renderValid) continue;
      shaderProgram.blit(blit);
    }
  }

  public boolean checkFrustum(RendererObject object){
    BoundingBox b = object.getTransformedBounds(b1, mat);
    return camera3D.frustum.containsBounds(b);
  }

  public void add(GameObject object){
  //  object.setID(objectIndex++);
    objects.add(object);
    if (object instanceof RendererObject rendObj) {
      renderObjects.add(rendObj);
      ShaderProgram shaderProgram = rendObj.material().shader;
      var group = shaderProgram.isForwardRender()? forwardRenderGroup: deferredRenderGroup;
      group.get(shaderProgram, OrderedSet::new).add(rendObj);
      group.orderedKeys().sort();
    }
    if (object instanceof LightSource lightSource) lightSources.add(lightSource);
  }

  public void remove(GameObject object){
    objects.remove(object);
    if (object instanceof RendererObject rendObj) {
      renderObjects.remove(rendObj);
      ShaderProgram shaderProgram = rendObj.material().shader;
      OrderedMap<ShaderProgram, OrderedSet<RendererObject>> group = shaderProgram.isForwardRender()? forwardRenderGroup: deferredRenderGroup;
      OrderedSet<RendererObject> matSet = group.get(shaderProgram, Empties.nilSetOD());
      if (!matSet.remove(rendObj)){
        for (var iterator = deferredRenderGroup.values().iterator(); iterator.hasNext(); ) {
          OrderedSet<RendererObject> set = iterator.next();
          if (set.remove(rendObj) && set.isEmpty()) iterator.remove();
        }
        for (var iterator = forwardRenderGroup.values().iterator(); iterator.hasNext(); ) {
          OrderedSet<RendererObject> set = iterator.next();
          if (set.remove(rendObj) && set.isEmpty()) iterator.remove();
        }
      }
      else if (matSet.isEmpty()) group.remove(shaderProgram);
    }
    if (object instanceof LightSource lightSource) lightSources.remove(lightSource);
  }
}
