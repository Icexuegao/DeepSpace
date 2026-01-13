package singularity.graphic;

import arc.Core;
import arc.Events;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.*;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.GLFrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.game.EventType;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import singularity.Sgl;
import singularity.util.func.Floatc3;

import java.util.Arrays;

import static arc.Core.settings;

@SuppressWarnings("unchecked")
public class SglDraw{
  private static final Rect rect = new Rect();

  static final Vec2 v1 = new Vec2(), v2 = new Vec2(), v3 = new Vec2(), v4 = new Vec2(), v5 = new Vec2(),
      v6 = new Vec2(), v7 = new Vec2(), v8 = new Vec2(), v9 = new Vec2(), v10 = new Vec2();
  static final Vec3 v31 = new Vec3(), v32 = new Vec3(), v33 = new Vec3(), v34 = new Vec3(), v35 = new Vec3(),
      v36 = new Vec3(), v37 = new Vec3(), v38 = new Vec3(), v39 = new Vec3(), v310 = new Vec3();

  static final Color c1 = new Color(), c2 = new Color(), c3 = new Color(), c4 = new Color(), c5 = new Color(),
      c6 = new Color(), c7 = new Color(), c8 = new Color(), c9 = new Color(), c10 = new Color();

  private static DrawTask[] drawTasks = new DrawTask[16];
  private static FrameBuffer[] taskBuffer = new FrameBuffer[16];

  private static Bloom[] blooms = new Bloom[16];

  private static int idCount = 0;

  public static int nextTaskID(){
    return idCount++;
  }

  public static final float mirrorField = 135f;
  public static final FrameBuffer effectBuffer = new FrameBuffer();

  public static final int sharedUnderBlockBloomID = nextTaskID();
  public static final int sharedUponFlyUnitBloomID = nextTaskID();
  public static final int sharedUnderFlyUnitBloomID = nextTaskID();

  static {
    Events.run(EventType.Trigger.draw, () -> {
     // Particle.maxAmount = Sgl.config.enableParticle? Sgl.config.maxParticleCount: 0;

      Draw.draw(mirrorField - 0.01f, () -> {
        effectBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
        effectBuffer.begin(Color.clear);
      });
      Draw.draw(mirrorField + 0.51f, () -> {
        effectBuffer.end();

        SglShaders.mirrorField.waveMix = Tmp.c1.set(SglDrawConst.matrixNet);
        SglShaders.mirrorField.waveScl = 0.03f;
        SglShaders.mirrorField.gridStroke = 0.8f;
        SglShaders.mirrorField.maxThreshold = 1f;
        SglShaders.mirrorField.minThreshold = 0.7f;
        SglShaders.mirrorField.stroke = 2;
        SglShaders.mirrorField.sideLen = 10;
        SglShaders.mirrorField.offset.set(Time.time/10, Time.time/10);

        effectBuffer.blit(SglShaders.mirrorField);
      });
    });

    Time.run(0, () -> Sgl.ui.debugInfos.addMonitor("drawTaskCount", () -> idCount));
  }

  /**发布缓存的任务并在首次发布时的z轴时进行绘制，传递的一些参数只在初始化时起了效果，之后都被选择性的无视了
   *
   * @param taskId 任务的标识ID，用于区分任务缓存
   * @param target 传递给绘制任务的数据目标，这是为了优化lambda的内存而添加的，避免产生大量闭包的lambda实例造成不必要的内存占用
   * @param drawFirst <strong>选择性的参数，若任务已初始化，这个参数无效</strong>，用于声明这个任务组在执行前要进行的操作
   * @param drawLast <strong>选择性的参数，若任务已初始化，这个参数无效</strong>，用于声明这个任务组在完成主绘制后要执行的操作
   * @param draw 添加到任务缓存的绘制任务，即此次绘制的操作*/
  public static <T, D> void drawTask(int taskId, T target, D defTarget, DrawAcceptor<D> drawFirst, DrawAcceptor<D> drawLast, DrawAcceptor<T> draw){
    while (taskId >= drawTasks.length){
      drawTasks = Arrays.copyOf(drawTasks, drawTasks.length*2);
    }

    DrawTask task = drawTasks[taskId];
    if (task == null){
      task = drawTasks[taskId] = new DrawTask();
    }
    if (!task.init){
      task.defaultFirstTask = drawFirst;
      task.defaultLastTask = drawLast;
      task.defaultTarget = defTarget;
      task.init = true;
      Draw.draw(Draw.z(), task::flush);
    }
    task.addTask(target, draw);
  }

  /**发布缓存的任务并在首次发布时的z轴时进行绘制，传递的一些参数只在初始化时起了效果，之后都被选择性的无视了
   *
   * @param taskId 任务的标识ID，用于区分任务缓存
   * @param target 递给绘制任务的数据目标，这是为了优化lambda的内存而添加的，避免产生大量闭包的lambda实例造成不必要的内存占用
   * @param drawFirst <strong>选择性的参数，若任务已初始化，这个参数无效</strong>，用于声明这个任务组在执行前要进行的操作
   * @param drawLast <strong>选择性的参数，若任务已初始化，这个参数无效</strong>，用于声明这个任务组在完成主绘制后要执行的操作
   * @param draw 添加到任务缓存的绘制任务，即此次绘制的操作*/
  public static <T> void drawTask(int taskId, T target, DrawAcceptor<T> drawFirst, DrawAcceptor<T> drawLast, DrawAcceptor<T> draw){
    drawTask(taskId, target, target, drawFirst, drawLast, draw);
  }

  /**发布缓存的任务并在首次发布时的z轴时进行绘制，传递的一些参数只在初始化时起了效果，之后都被选择性的无视了
   *
   * @param taskID 任务的标识id，用于区分任务缓存
   * @param target 递给绘制任务的数据目标，这是为了优化lambda的内存而添加的，避免产生大量闭包的lambda实例造成不必要的内存占用
   * @param shader <strong>选择性的参数，若任务已初始化，这个参数无效</strong>，在这组任务绘制时使用的着色器
   * @param draw 添加到任务缓存的绘制任务，即此次绘制的操作*/
  public static <T, S extends Shader> void drawTask(int taskID, T target, S shader, DrawAcceptor<T> draw){
    if(!Sgl.config.enableShaders){
      draw.draw(target);
      return;
    }

    while (taskID >= taskBuffer.length){
      taskBuffer = Arrays.copyOf(taskBuffer, taskBuffer.length*2);
    }

    FrameBuffer buffer = taskBuffer[taskID];
    if (buffer == null){
      buffer = taskBuffer[taskID] = new FrameBuffer();
    }
    FrameBuffer b = buffer;
    drawTask(taskID, target, shader, e -> {
      b.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
      b.begin(Color.clear);
    }, e -> {
      b.end();
      b.blit(e);
    }, draw);
  }

  /**发布缓存的任务并在首次发布时的z轴时进行绘制，传递的一些参数只在初始化时起了效果，之后都被选择性的无视了
   *
   * @param taskID 任务的标识id，用于区分任务缓存
   * @param target 递给绘制任务的数据目标，这是为了优化lambda的内存而添加的，避免产生大量闭包的lambda实例造成不必要的内存占用
   * @param shader <strong>选择性的参数，若任务已初始化，这个参数无效</strong>，在这组任务绘制时使用的着色器
   * @param applyShader <strong>选择性的参数，若任务已初始化，这个参数无效</strong>，绘制前对着色器进行的操作
   * @param draw 添加到任务缓存的绘制任务，即此次绘制的操作*/
  public static <T, S extends Shader> void drawTask(int taskID, T target, S shader, DrawAcceptor<S> applyShader, DrawAcceptor<T> draw){
    drawTask(taskID, target, FrameBuffer::new, shader, applyShader, draw);
  }

  public static <T, S extends Shader> void drawTask(int taskID, T target, Prov<FrameBuffer> bufferProv, S shader, DrawAcceptor<S> applyShader, DrawAcceptor<T> draw){
    if(!Sgl.config.enableShaders){
      draw.draw(target);
      return;
    }

    while (taskID >= taskBuffer.length){
      taskBuffer = Arrays.copyOf(taskBuffer, taskBuffer.length*2);
    }

    if (taskBuffer[taskID] == null){
      taskBuffer[taskID] = bufferProv.get();
    }
    drawTask(taskID, target, shader, e -> {
      taskBuffer[taskID].resize(Core.graphics.getWidth(), Core.graphics.getHeight());
      taskBuffer[taskID].begin(Color.clear);
    }, e -> {
      taskBuffer[taskID].end();
      applyShader.draw(e);
      taskBuffer[taskID].blit(e);
    }, draw);
  }

  /**发布缓存的任务并在首次发布时的z轴时进行绘制，传递的一些参数只在初始化时起了效果，之后都被选择性的无视了
   * <p><strong>如果这个方法的调用频率非常高，同时描述绘制行为的lambda表达式需要访问局部变量，那么为了优化堆占用，请使用{@link SglDraw#drawTask(int, Object, Shader, DrawAcceptor)}</strong>
   *
   * @param taskID 任务的标识ID，用于区分任务缓存
   * @param shader <strong>选择性的参数，若任务已初始化，这个参数无效</strong>，在这组任务绘制时使用的着色器
   * @param draw 添加到任务缓存的绘制任务，即此次绘制的操作*/
  public static void drawTask(int taskID, Shader shader, DrawDef draw){
    drawTask(taskID, null, shader, draw);
  }

  /**发布缓存的任务并在首次发布时的z轴时进行绘制
   *
   * @param taskID 任务的标识ID，用于区分任务缓存
   * @param target 递给绘制任务的数据目标，这是为了优化lambda的内存而添加的，避免产生大量闭包的lambda实例造成不必要的内存占用
   * @param draw 添加到任务缓存的绘制任务，即此次绘制的操作*/
  public static <T> void drawTask(int taskID, T target, DrawAcceptor<T> draw){
    while (taskID >= drawTasks.length){
      drawTasks = Arrays.copyOf(drawTasks, drawTasks.length*2);
    }

    DrawTask task = drawTasks[taskID];
    if (task == null){
      task = drawTasks[taskID] = new DrawTask();
    }

    if (!task.init){
      task.init = true;
      Draw.draw(Draw.z(), task::flush);
    }
    task.addTask(target, draw);
  }

  /**发布缓存的任务并在首次发布时的z轴时进行绘制
   * <p><strong>如果这个方法的调用频率非常高，同时描述绘制行为的lambda表达式需要访问局部变量，那么为了优化堆占用，请使用{@link SglDraw#drawTask(int, Object, DrawAcceptor)}</strong>
   *
   * @param taskID 任务的标识ID，用于区分任务缓存
   * @param draw 添加到任务缓存的绘制任务，即此次绘制的操作*/
  public static void drawTask(int taskID, DrawDef draw){
    while (taskID >= drawTasks.length){
      drawTasks = Arrays.copyOf(drawTasks, drawTasks.length*2);
    }

    DrawTask task = drawTasks[taskID];
    if (task == null){
      task = drawTasks[taskID] = new DrawTask();
    }

    if (!task.init){
      task.init = true;
      Draw.draw(Draw.z(), task::flush);
    }
    task.addTask(null, draw);
  }

  public static <T, B extends FrameBuffer> void drawToBuffer(int taskID, B buffer, T target, DrawAcceptor<T> draw){
    drawToBuffer(taskID, buffer, target, b -> {}, draw);
  }

  public static <T, B extends FrameBuffer> void drawToBuffer(int taskID, B buffer, T target, DrawAcceptor<B> endBuffer, DrawAcceptor<T> draw){
    drawTask(taskID, target, buffer, b -> {
      b.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
      b.begin(Color.clear);
    }, b -> {
      b.end();
      endBuffer.draw(b);
    }, draw);
  }

  /**发布一个泛光绘制任务，基于{@link SglDraw#drawTask(int, Object, DrawAcceptor, DrawAcceptor, DrawAcceptor)}实现
   * 
   * @param taskID 任务的标识ID，用于区分任务缓存
   * @param obj 传递给绘制任务的数据对象
   * @param draw 绘制任务*/
  public static <T> void drawBloom(int taskID, T obj, DrawAcceptor<T> draw){
    if (!settings.getBool("bloom")){
      draw.draw(obj);
      return;
    }

    while (taskID >= blooms.length){
      blooms = Arrays.copyOf(blooms, blooms.length*2);
    }

    Bloom bloom = blooms[taskID];
    if (bloom == null){
      bloom = blooms[taskID] = new Bloom(true);
    }
    drawTask(taskID, obj, bloom, e -> {
      e.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
      e.setBloomIntensity(settings.getInt("bloomintensity", 6) / 4f + 1f);
      e.blurPasses = settings.getInt("bloomblur", 1);
      e.capture();
    }, Bloom::render, draw);
  }

  /**@see SglDraw#drawBloom(int, Object, DrawAcceptor) */
  public static void drawBloom(int taskID, DrawDef draw){
    drawBloom(taskID, (DrawAcceptor<Bloom>) draw);
  }

  /**@see SglDraw#drawBloom(int, Object, DrawAcceptor) */
  public static void drawBloom(int taskID, DrawAcceptor<Bloom> draw){
    while (taskID >= blooms.length){
      blooms = Arrays.copyOf(blooms, blooms.length*2);
    }

    Bloom bloom = blooms[taskID];
    if (bloom == null){
      bloom = blooms[taskID] = new Bloom(true);
    }

    if (!settings.getBool("bloom", false)){
      draw.draw(bloom);
      return;
    }

    drawTask(taskID, bloom, e -> {
      e.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
      e.setBloomIntensity(settings.getInt("bloomintensity", 6) / 4f + 1f);
      e.blurPasses = settings.getInt("bloomblur", 1);
      e.capture();
    }, Bloom::render, draw);
  }

  /**@see SglDraw#drawBloomUnderBlock(Object, DrawAcceptor)*/
  public static void drawBloomUnderBlock(DrawDef draw){
    drawBloomUnderBlock(null, (DrawAcceptor<? extends Object>) draw);
  }

  /**在共享的泛光绘制组中发布一个泛光绘制任务，绘制的层位于方块下方（{@link Layer#block}-1, 29）
   * <p>关于泛光绘制任务，请参阅{@link SglDraw#drawBloom(int, Object, DrawAcceptor)}
   *
   * @param target 传递给绘制任务的数据对象
   * @param draw 绘制任务*/
  public static <T> void drawBloomUnderBlock(T target, DrawAcceptor<T> draw){
    float z = Draw.z();
    Draw.z(Layer.block + 1);
    drawBloom(sharedUnderBlockBloomID, target, draw);
    Draw.z(z);
  }

  /**@see SglDraw#drawBloomUponFlyUnit(Object, DrawAcceptor)*/
  public static void drawBloomUponFlyUnit(DrawDef draw){
    drawBloomUponFlyUnit(null, draw);
  }

  /**在共享的泛光绘制组中发布一个泛光绘制任务，绘制的层位于方块下方（{@link Layer#flyingUnit}+1, 116）
   * <p>关于泛光绘制任务，请参阅{@link SglDraw#drawBloom(int, Object, DrawAcceptor)}
   *
   * @param target 传递给绘制任务的数据对象
   * @param draw 绘制任务*/
  public static <T> void drawBloomUponFlyUnit(T target, DrawAcceptor<T> draw){
    float z = Draw.z();
    Draw.z(Layer.flyingUnit + 1);
    drawBloom(sharedUponFlyUnitBloomID, target, draw);
    Draw.z(z);
  }

  /**@see SglDraw#drawBloomUnderFlyUnit(Object, DrawAcceptor) */
  public static void drawBloomUnderFlyUnit(DrawDef draw){
    drawBloomUnderFlyUnit(null, draw);
  }

  /**在共享的泛光绘制组中发布一个泛光绘制任务，绘制的层位于低空单位下方（86, {@link Layer#flyingUnitLow}-1）
   * <p>关于泛光绘制任务，请参阅{@link SglDraw#drawBloom(int, Object, DrawAcceptor)}
   *
   * @param target 传递给绘制任务的数据对象
   * @param draw 绘制任务*/
  public static <T> void drawBloomUnderFlyUnit(T target, DrawAcceptor<T> draw){
    float z = Draw.z();
    Draw.z(Layer.plans + 1);
    drawBloom(sharedUnderFlyUnitBloomID, target, draw);
    Draw.z(z);
  }

  /**发布一个扭曲绘制任务，基于{@link SglDraw#drawTask(int, Object, DrawAcceptor, DrawAcceptor, DrawAcceptor)}实现
   *
   * @param taskID 任务的标识ID，用于区分任务缓存
   * @param target 传递给绘制任务的数据对象
   * @param distortion 扭曲绘制工具
   * @param draw 绘制任务*/
  public static <T> void drawDistortion(int taskID, T target, Distortion distortion, DrawAcceptor<T> draw){
    if(!Sgl.config.enableDistortion) return;

    drawTask(taskID, target, distortion, e -> {
      e.resize();
      e.capture();
    }, Distortion::render, draw);
  }

  /**发布一个高斯模糊遮罩层绘制任务，基于{@link SglDraw#drawTask(int, Object, DrawAcceptor, DrawAcceptor, DrawAcceptor)}实现
   *
   * @param taskID 任务的标识ID，用于区分任务缓存
   * @param target 传递给绘制任务的数据对象
   * @param blur 模糊绘制对象
   * @param draw 绘制任务*/
  public static <T> void drawBlur(int taskID, T target, Blur blur, DrawAcceptor<T> draw){
    if(!Sgl.config.enableShaders) return;

    drawTask(taskID, target, blur, e -> {
      e.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
      e.capture();
    }, Blur::render, draw);
  }

  public static <T> void drawMirrorField(int taskID, T target, DrawAcceptor<SglShaders.MirrorFieldShader> pre, DrawAcceptor<T> draw){
    SglDraw.drawTask(taskID, target, SglShaders.mirrorField, pre, draw);
  }

  public static <T> void drawMask(int taskID, SglShaders.MaskShader shader, GLFrameBuffer<? extends Texture> baseBuffer, T target, DrawAcceptor<T> draw){
    drawTask(taskID, target, shader, s -> {
      baseBuffer.end();
      shader.texture = baseBuffer.getTexture();
    }, draw);
  }

  public static void drawTransform(float originX, float originY, Vec2 vec, float rotate, Floatc3 draw){
    drawTransform(originX, originY, 0, vec.x, vec.y, rotate, draw);
  }

  public static void drawTransform(float originX, float originY, float dx, float dy, float rotate, Floatc3 draw){
    drawTransform(originX, originY, 0, dx, dy, rotate, draw);
  }

  public static void drawTransform(float originX, float originY, float originAngle, float dx, float dy, float rotate, Floatc3 draw){
    v1.set(dx, dy).rotate(rotate);
    draw.get(originX + v1.x, originY + v1.y, originAngle + rotate);
  }

  public static boolean clipDrawable(float x, float y, float clipSize){
    Core.camera.bounds(rect);
    return rect.overlaps(x - clipSize/2, y - clipSize/2, clipSize, clipSize);
  }

  public static void drawLink(float origX, float origY, float othX, float othY, TextureRegion linkRegion, TextureRegion capRegion, float lerp){
    drawLink(origX, origY, 0, othX, othY, 0, linkRegion, capRegion, lerp);
  }
  
  public static void drawLink(float origX, float origY, float offsetO, float othX, float othY, float offset, TextureRegion linkRegion, @Nullable TextureRegion capRegion, float lerp){
    v1.set(othX - origX, othY - origY).setLength(offsetO);
    float ox = origX + v1.x;
    float oy = origY + v1.y;
    v1.scl(-1).setLength(offset);
    float otx = othX + v1.x;
    float oty = othY + v1.y;

    v1.set(otx, oty).sub(ox, oy);
    v2.set(v1).scl(lerp);
    v3.set(0, 0);
    
    if(capRegion != null){
      v3.set(v1).setLength(capRegion.width/4f);
      Draw.rect(capRegion, ox + v3.x/2, oy + v3.y/2, v2.angle());
      Draw.rect(capRegion, ox + v2.x - v3.x/2, oy + v2.y - v3.y/2, v2.angle() + 180);
    }

    Lines.stroke(8);
    Lines.line(linkRegion,
        ox + v3.x, oy + v3.y,
        ox + v2.x - v3.x,
        oy + v2.y - v3.y,
        false);
  }

  public static void drawLightEdge(float x, float y, float vertLength, float vertWidth, float horLength, float horWidth){
    Color color = Draw.getColor();
    drawDiamond(x, y, vertLength, vertWidth, 90, color, color);
    drawDiamond(x, y, horLength, horWidth, 0, color, color);
  }

  public static void drawLightEdge(float x, float y, float vertLength, float vertWidth, float horLength, float horWidth, float rotation){
    Color color = Draw.getColor();
    drawDiamond(x, y, vertLength, vertWidth, 90 + rotation, color, color);
    drawDiamond(x, y, horLength, horWidth, 0 + rotation, color, color);
  }

  public static void drawLightEdge(float x, float y, float vertLength, float vertWidth, float horLength, float horWidth, float rotation, float gradientAlpha){
    drawLightEdge(x, y, vertLength, vertWidth, horLength, horWidth, rotation, Tmp.c1.set(Draw.getColor()).a(gradientAlpha));
  }

  public static void drawLightEdge(float x, float y, float vertLength, float vertWidth, float horLength, float horWidth, float rotation, Color gradientTo){
    Color color = Draw.getColor();
    drawDiamond(x, y, vertLength, vertWidth, 90 + rotation, color, gradientTo);
    drawDiamond(x, y, horLength, horWidth, 0 + rotation, color, gradientTo);
  }

  public static void drawLightEdge(float x, float y, Color color, float vertLength, float vertWidth, float rotationV, Color gradientV,
                                   float horLength, float horWidth, float rotationH, Color gradientH){
    drawDiamond(x, y, vertLength, vertWidth, 90 + rotationV, color, gradientV);
    drawDiamond(x, y, horLength, horWidth, rotationH, color, gradientH);
  }

  public static void drawLightEdge(float x, float y, float vertLength, float vertWidth, float rotationV, float gradientV,
                                   float horLength, float horWidth, float rotationH, float gradientH){
    Color color = Draw.getColor(), gradientColorV = color.cpy().a(gradientV), gradientColorH = color.cpy().a(gradientH);
    drawDiamond(x, y, vertLength, vertWidth, 90 + rotationV, color, gradientColorV);
    drawDiamond(x, y, horLength, horWidth, rotationH, color, gradientColorH);
  }

  public static void drawDiamond(float x, float y, float length, float width, float rotation){
    drawDiamond(x, y, length, width, rotation, Draw.getColor());
  }

  public static void drawDiamond(float x, float y, float length, float width, float rotation, float gradientAlpha){
    drawDiamond(x, y, length, width, rotation, Draw.getColor(), gradientAlpha);
  }

  public static void drawDiamond(float x, float y, float length, float width, float rotation, Color color){
    drawDiamond(x, y, length, width, rotation, color, 1);
  }

  public static void drawDiamond(float x, float y, float length, float width, float rotation, Color color, float gradientAlpha){
    drawDiamond(x, y, length, width, rotation, color, Tmp.c1.set(color).a(gradientAlpha));
  }

  public static void drawDiamond(float x, float y, float length, float width, float rotation, Color color, Color gradient){
    v1.set(length/2, 0).rotate(rotation);
    v2.set(0, width/2).rotate(rotation);

    float originColor = color.toFloatBits();
    float gradientColor = gradient.toFloatBits();

    Fill.quad(x, y, originColor, x, y, originColor,
        x + v1.x, y + v1.y, gradientColor,
        x + v2.x, y + v2.y, gradientColor);
    Fill.quad(x, y, originColor, x, y, originColor,
        x + v1.x, y + v1.y, gradientColor,
        x - v2.x, y - v2.y, gradientColor);
    Fill.quad(x, y, originColor, x, y, originColor,
        x - v1.x, y - v1.y, gradientColor,
        x + v2.x, y + v2.y, gradientColor);
    Fill.quad(x, y, originColor, x, y, originColor,
        x - v1.x, y - v1.y, gradientColor,
        x - v2.x, y - v2.y, gradientColor);
  }

  public static void drawCrystal(float x, float y, float length, float width, float height, float centOffX, float centOffY, float edgeStoke,
                                 float edgeLayer, float botLayer, float crystalRotation, float rotation, Color color, Color edgeColor){
    v31.set(length/2, 0, 0);
    v32.set(0, width/2, 0).rotate(Vec3.X, crystalRotation);
    v33.set(centOffX, centOffY, height/2).rotate(Vec3.X, crystalRotation);

    float w1, w2;
    float widthReal = Math.max(w1 = Math.abs(v32.y), w2 = Math.abs(v33.y));

    v31.rotate(Vec3.Z, -rotation);
    v32.rotate(Vec3.Z, -rotation);
    v33.rotate(Vec3.Z, -rotation);

    float z = Draw.z();
    Draw.z(botLayer);
    Draw.color(color);

    float mx = Angles.trnsx(rotation + 90, widthReal), my = Angles.trnsy(rotation + 90, widthReal);
    Fill.quad(
        x + v31.x, y + v31.y,
        x + mx, y + my,
        x - v31.x, y - v31.y,
        x - mx, y - my
    );

    if(edgeStoke > 0.01f && edgeColor.a > 0.01){
      Lines.stroke(edgeStoke, edgeColor);
      crystalEdge(x, y, w1 >= widthReal, v32.z > v33.z, edgeLayer, botLayer, v32);
      crystalEdge(x, y, w2 >= widthReal, v33.z > v32.z, edgeLayer, botLayer, v33);
    }

    Draw.z(z);
  }

  private static void crystalEdge(float x, float y, boolean w, boolean r, float edgeLayer, float botLayer, Vec3 v){
    Draw.z(r || w? edgeLayer: botLayer - 0.01f);
    Lines.line(
        x + v.x, y + v.y,
        x + v31.x, y + v31.y
    );
    Lines.line(
        x + v.x, y + v.y,
        x - v31.x, y - v31.y
    );
    Draw.z(!r || w? edgeLayer: botLayer - 0.01f);
    Lines.line(
        x - v.x, y - v.y,
        x + v31.x, y + v31.y
    );
    Lines.line(
        x - v.x, y - v.y,
        x - v31.x, y - v31.y
    );
  }

  public static void drawCornerTri(float x, float y, float rad, float cornerLen, float rotate, boolean line){
    drawCornerPoly(x, y, rad, cornerLen, 3, rotate, line);
  }

  public static void drawCornerPoly(float x, float y, float rad, float cornerLen, float sides, float rotate, boolean line){
    float step = 360/sides;

    if(line) Lines.beginLine();
    for(int i = 0; i < sides; i++){
      v1.set(rad, 0).setAngle(step*i + rotate);
      v2.set(v1).rotate90(1).setLength(cornerLen);

      if(line){
        Lines.linePoint(x + v1.x - v2.x, y + v1.y - v2.y);
        Lines.linePoint(x + v1.x + v2.x, y + v1.y + v2.y);
      }
      else{
        Fill.tri(x, y,
            x + v1.x - v2.x, y + v1.y - v2.y,
            x + v1.x + v2.x, y + v1.y + v2.y
        );
      }
    }
    if(line) Lines.endLine(true);
  }

  public static void drawHaloPart(float x, float y, float width, float len, float rotate){
    drawHaloPart(x, y, width*0.2f, len*0.7f, width, len*0.3f, rotate);
  }

  public static void drawHaloPart(float x, float y, float interWidth, float interLen, float width, float len, float rotate){
    Drawf.tri(x, y, interWidth, interLen, rotate + 180);
    Drawf.tri(x, y, width, len, rotate);
  }

  public static void gradientTri(float x, float y, float length, float width, float rotation){
    gradientTri(x, y, length, width, rotation, Draw.getColor());
  }

  public static void gradientTri(float x, float y, float length, float width, float rotation, float gradientAlpha){
    gradientTri(x, y, length, width, rotation, Draw.getColor(), gradientAlpha);
  }

  public static void gradientTri(float x, float y, float length, float width, float rotation, Color color){
    gradientTri(x, y, length, width, rotation, color, color);
  }

  public static void gradientTri(float x, float y, float length, float width, float rotation, Color color, float gradientAlpha){
    gradientTri(x, y, length, width, rotation, color, Tmp.c1.set(color).a(gradientAlpha));
  }

  public static void gradientTri(float x, float y, float length, float width, float rotation, Color color, Color gradient){
    v1.set(length/2, 0).rotate(rotation);
    v2.set(0, width/2).rotate(rotation);

    float originColor = color.toFloatBits();
    float gradientColor = gradient.toFloatBits();

    Fill.quad(x, y, originColor, x, y, originColor,
        x + v1.x, y + v1.y, gradientColor,
        x + v2.x, y + v2.y, gradientColor);
    Fill.quad(x, y, originColor, x, y, originColor,
        x + v1.x, y + v1.y, gradientColor,
        x - v2.x, y - v2.y, gradientColor);
  }

  public static void gradientCircle(float x, float y, float radius, Color gradientColor){
    gradientCircle(x, y, radius, x, y, gradientColor);
  }

  public static void gradientCircle(float x, float y, float radius, float gradientAlpha){
    gradientCircle(x, y, radius, x, y, Tmp.c1.set(Draw.getColor()).a(gradientAlpha));
  }

  public static void gradientCircle(float x, float y, float radius, float offset, float gradientAlpha){
    gradientCircle(x, y, radius, x, y, offset, Tmp.c1.set(Draw.getColor()).a(gradientAlpha));
  }

  public static void gradientCircle(float x, float y, float radius, float offset, Color gradientColor){
    gradientCircle(x, y, radius, x, y, offset, gradientColor);
  }

  public static void gradientCircle(float x, float y, float radius, float gradientCenterX, float gradientCenterY, Color gradientColor){
    gradientCircle(x, y, radius, gradientCenterX, gradientCenterY, -radius, gradientColor);
  }

  public static void gradientCircle(float x, float y, float radius, float gradientCenterX, float gradientCenterY, float offset, Color gradientColor){
    gradientPoly(x, y, Lines.circleVertices(radius), radius, Draw.getColor(), gradientCenterX, gradientCenterY, offset, gradientColor, 0);
  }

  public static void gradientSqrt(float x, float y, float radius, float rotation, float offset, Color gradientColor){
    gradientSqrt(x, y, radius, x, y, offset, gradientColor, rotation);
  }

  public static void gradientSqrt(float x, float y, float radius, float gradientCenterX, float gradientCenterY, float offset, Color gradientColor, float rotation){
    gradientPoly(x, y, 4, 1.41421f*(radius/2), Draw.getColor(), gradientCenterX, gradientCenterY, offset, gradientColor, rotation);
  }

  public static void gradientPoly(float x, float y, int edges, float radius, Color color, float gradientCenterX, float gradientCenterY,
                                  float offset, Color gradientColor, float rotation){
    gradientFan(x, y, edges, radius, color, gradientCenterX, gradientCenterY, offset, gradientColor, 360, rotation);
  }

  public static  void drawFan(float x, float y, float radius, float fanAngle, float rotation){
    gradientFan(x, y, radius, Draw.getColor().a, fanAngle, rotation);
  }

  public static void gradientFan(float x, float y, float radius, float gradientAlpha, float fanAngle, float rotation){
    gradientFan(x, y, radius, -radius, gradientAlpha, fanAngle, rotation);
  }

  public static void gradientFan(float x, float y, float radius, float offset, float gradientAlpha, float fanAngle, float rotation){
    gradientFan(x, y, radius, offset, c1.set(Draw.getColor()).a(gradientAlpha), fanAngle, rotation);
  }

  public static void gradientFan(float x, float y, float radius, float offset, Color gradientColor, float fanAngle, float rotation){
    gradientFan(x, y, Lines.circleVertices(radius), radius, Draw.getColor(), x, y, offset, gradientColor, fanAngle, rotation);
  }

  public static void gradientFan(float x, float y, float radius, Color color, float gradientCenterX, float gradientCenterY, float offset,
                                 Color gradientColor, float fanAngle, float rotation){
    gradientFan(x, y, Lines.circleVertices(radius), radius, color, gradientCenterX, gradientCenterY, offset, gradientColor, fanAngle, rotation);
  }

  public static void gradientFan(float x, float y, int edges, float radius, Color color, float gradientCenterX, float gradientCenterY,
                                 float offset, Color gradientColor, float fanAngle, float rotation){
    fanAngle = Mathf.clamp(fanAngle, 0, 360);

    v1.set(gradientCenterX - x, gradientCenterY - y).rotate(rotation);
    gradientCenterX = x + v1.x;
    gradientCenterY = y + v1.y;

    v1.set(1, 0).setLength(radius).rotate(rotation - fanAngle%360/2);
    float step = fanAngle/edges;

    float lastX = -1, lastY = -1;
    float lastGX = -1, lastGY = -1;

    for(int i = 0; i < edges + (fanAngle == 360? 1: 0); i++){
      v1.setAngle(i*step + rotation - fanAngle%360/2);
      v2.set(v1).sub(gradientCenterX - x, gradientCenterY - y);

      if(lastX != -1){
        v3.set(v2).setLength(offset).scl(offset < 0? -1: 1);
        v4.set(lastGX, lastGY).setLength(offset).scl(offset < 0? -1: 1);
        Fill.quad(
            lastX, lastY, color.toFloatBits(),
            x + v1.x, y + v1.y, color.toFloatBits(),
            gradientCenterX + v2.x + v3.x, gradientCenterY + v2.y + v3.y, gradientColor.toFloatBits(),
            gradientCenterX + lastGX + v4.x, gradientCenterY + lastGY + v4.y, gradientColor.toFloatBits()
        );
      }

      lastX = x + v1.x;
      lastY = y + v1.y;
      lastGX = v2.x;
      lastGY = v2.y;
    }
  }

  public static void arc(float x, float y, float radius, float innerAngel, float rotate){
    arc(x, y, radius, 1.8f, innerAngel, rotate);
  }

  public static void arc(float x, float y, float radius, float scaleFactor, float innerAngel, float rotate){
    int sides = 40 + (int)(radius * scaleFactor);

    float step = 360f/sides;
    int sing = innerAngel > 0? 1: -1;
    innerAngel = Math.min(Math.abs(innerAngel), 360f);

    Lines.beginLine();

    float overed = 0;
    for (float ang = 0; ang <= innerAngel - step; ang += step){
      overed += step;
      v1.set(radius, 0).setAngle(ang*sing + rotate);
      Lines.linePoint(x + v1.x, y + v1.y);
    }

    if (innerAngel >= 360f - 0.01f) {
      Lines.endLine(true);
      return;
    }

    if (overed < innerAngel){
      v1.set(radius, 0).setAngle(innerAngel*sing + rotate);
      Lines.linePoint(x + v1.x, y + v1.y);
    }

    Lines.endLine();
  }

  public static void dashCircle(float x, float y, float radius){
    dashCircle(x, y, radius, 0);
  }

  public static void dashCircle(float x, float y, float radius, float rotate){
    dashCircle(x, y, radius, 1.8f, 6, 180, rotate);
  }

  public static void dashCircle(float x, float y, float radius, int dashes, float totalDashDeg, float rotate){
    dashCircle(x, y, radius, 1.8f, dashes, totalDashDeg, rotate);
  }

  public static void dashCircle(float x, float y, float radius, float scaleFactor, int dashes, float totalDashDeg, float rotate){
    if (Mathf.equal(totalDashDeg, 0)) return;

    int sides = 40 + (int)(radius * scaleFactor);
    if(sides % 2 == 1) sides++;

    v1.set(0, 0);
    float per = totalDashDeg < 0? -360f/sides: 360f/sides;
    totalDashDeg = Math.min(Math.abs(totalDashDeg), 360);

    float rem = 360 - totalDashDeg;
    float dashDeg = totalDashDeg/dashes;
    float empDeg = rem/dashes;

    Lines.beginLine();
    v1.set(radius, 0).setAngle(rotate + 90);
    Lines.linePoint(v1.x + x, v1.y + y);

    boolean drawing = true;
    for(int i = 0; i < sides; i++){
      if(i*Math.abs(per)%(dashDeg+empDeg) > dashDeg) {
        if (drawing) {
          Lines.endLine();
          drawing = false;
        }
        continue;
      }

      if (!drawing) Lines.beginLine();
      drawing = true;
      v1.set(radius, 0).setAngle(rotate + per * (i + 1) + 90);
      float x1 = v1.x;
      float y1 = v1.y;

      Lines.linePoint(x1 + x, y1 + y);
    }
    if (drawing) Lines.endLine();
  }

  public static void drawLaser(float originX, float originY, float otherX, float otherY, TextureRegion linkRegion,
                               TextureRegion capRegion, float stoke){
    float rot = Mathf.angle(otherX - originX, otherY - originY);

    if(capRegion != null){
      Draw.rect(capRegion, otherX, otherY, rot);
    }

    Lines.stroke(stoke);
    Lines.line(linkRegion, originX, originY, otherX, otherY, capRegion != null);
  }

  public static void gradientLine(float originX, float originY, float targetX, float targetY, Color origin, Color target, int gradientDir){
    float halfWidth = Lines.getStroke()/2;
    v1.set(halfWidth, 0).rotate(Mathf.angle(targetX - originX, targetY - originY) + 90);

    float c1, c2, c3, c4;
    switch(gradientDir){
      case 0 -> {
        c1 = origin.toFloatBits();
        c2 = origin.toFloatBits();
        c3 = target.toFloatBits();
        c4 = target.toFloatBits();
      }
      case 1 -> {
        c1 = target.toFloatBits();
        c2 = origin.toFloatBits();
        c3 = origin.toFloatBits();
        c4 = target.toFloatBits();
      }
      case 2 -> {
        c1 = target.toFloatBits();
        c2 = target.toFloatBits();
        c3 = origin.toFloatBits();
        c4 = origin.toFloatBits();
      }
      case 3 -> {
        c1 = origin.toFloatBits();
        c2 = target.toFloatBits();
        c3 = target.toFloatBits();
        c4 = origin.toFloatBits();
      }
      default -> {throw new IllegalArgumentException("gradient rotate must be 0 to 3, currently: " + gradientDir);}
    }

    Fill.quad(
      originX + v1.x, originY + v1.y, c1,
      originX - v1.x, originY - v1.y, c2,
      targetX - v1.x, targetY - v1.y, c3,
      targetX + v1.x, targetY + v1.y, c4
    );
  }

  public static void oval(float x, float y, float horLen, float vertLen, float rotation, float offset, Color gradientColor){
    int sides = Lines.circleVertices(Math.max(horLen, vertLen));
    float step = 360f/sides;

    float c1 = Draw.getColor().toFloatBits();
    float c2 = gradientColor.toFloatBits();

    for (int i = 0; i < sides; i++) {
      float dx = horLen*Mathf.cosDeg(i*step);
      float dy = vertLen*Mathf.sinDeg(i*step);
      float dx1 = horLen*Mathf.cosDeg((i + 1)*step);
      float dy1 = vertLen*Mathf.sinDeg((i + 1)*step);

      v1.set(dx, dy).setAngle(rotation);
      v2.set(dx1, dy1).setAngle(rotation);
      v3.set(v1).setLength(v1.len() + offset);
      v4.set(v2).setLength(v2.len() + offset);

      Fill.quad(
          x + v1.x, y + v1.y, c1,
          x + v2.x, y + v2.y, c1,
          x + v4.x, y + v4.y, c2,
          x + v3.x, y + v3.y, c2
      );
    }
  }

  public static void drawRectAsCylindrical(float x, float y, float rowWidth, float rowHeight,
                                           float cycRadius, float cycRotation, float rotation){
    drawRectAsCylindrical(x, y, rowWidth, rowHeight, cycRadius, cycRotation, rotation, Draw.getColor());
  }

  public static void drawRectAsCylindrical(float x, float y, float rowWidth, float rowHeight,
                                           float cycRadius, float cycRotation, float rotation, Color color){
    drawRectAsCylindrical(x, y, rowWidth, rowHeight, cycRadius, cycRotation, rotation, color, color, Draw.z(), Draw.z() - 0.01f);
  }

  public static void drawRectAsCylindrical(float x, float y, float rowWidth, float rowHeight, float cycRadius, float cycRotation,
                                           float rotation, Color color, Color dark, float lightLayer, float darkLayer){
    if(rowWidth >= 2*Mathf.pi*cycRadius){
      v1.set(cycRadius, rowHeight).rotate(rotation);
      Draw.color(color);
      float z = Draw.z();
      Draw.z(lightLayer);
      Fill.quad(
          x + v1.x, y - v1.y,
          x + v1.x, y + v1.y,
          x - v1.x, y + v1.y,
          x - v1.x, y - v1.y
      );
      Draw.z(z);
      return;
    }

    cycRotation = Mathf.mod(cycRotation, 360);

    float phaseDiff = 180*rowWidth/(Mathf.pi*cycRadius);
    float rot = cycRotation + phaseDiff;

    v31.set(cycRadius, rowHeight/2, 0).rotate(Vec3.Y, cycRotation);
    v33.set(v31);
    v32.set(cycRadius, rowHeight/2, 0).rotate(Vec3.Y, rot);
    v34.set(v32);

    if(cycRotation < 180){
      if(rot > 180) v33.set(-cycRadius, rowHeight/2, 0);
      if(rot > 360) v34.set(cycRadius, rowHeight/2, 0);
    }
    else{
      if(rot > 360) v33.set(cycRadius, rowHeight/2, 0);
      if(rot > 540) v34.set(-cycRadius, rowHeight/2, 0);
    }

    float z = Draw.z();
    // A to C
    drawArcPart(v31.z > 0, color, dark, lightLayer, darkLayer, x, y, v31, v33, rotation);

    // B to D
    drawArcPart(v34.z > 0, color, dark, lightLayer, darkLayer, x, y, v32, v34, rotation);

    // C to D
    drawArcPart(
        (v33.z > 0 && v34.z > 0) || (Mathf.zero(v33.z) && Mathf.zero(v34.z) && v31.z < 0 && v32.z < 0)
            || (Mathf.zero(v33.z) && v34.z > 0) || (Mathf.zero(v34.z) && v33.z > 0),
        color, dark, lightLayer, darkLayer, x, y, v33, v34, rotation);

    Draw.z(z);
    Draw.reset();
  }

  private static void drawArcPart(boolean light, Color colorLight, Color darkColor, float layer, float darkLayer,
                                  float x, float y, Vec3 vec1, Vec3 vec2, float rotation){
    if(light){
      Draw.color(colorLight);
      Draw.z(layer);
    }
    else{
      Draw.color(darkColor);
      Draw.z(darkLayer);
    }

    v1.set(vec1.x, vec1.y).rotate(rotation);
    v2.set(vec2.x, vec2.y).rotate(rotation);
    v3.set(vec1.x, -vec1.y).rotate(rotation);
    v4.set(vec2.x, -vec2.y).rotate(rotation);

    Fill.quad(
        x + v3.x, y + v3.y,
        x + v1.x, y + v1.y,
        x + v2.x, y + v2.y,
        x + v4.x, y + v4.y
    );
  }

  public static void gapTri(float x, float y, float width, float length, float insideLength, float rotation) {
    v1.set(0, width/2).rotate(rotation);
    v2.set(length, 0).rotate(rotation);
    v3.set(insideLength, 0).rotate(rotation);

    Fill.quad(
        x + v1.x, y + v1.y,
        x + v2.x, y + v2.y,
        x + v3.x, y + v3.y,
        x + v1.x, y + v1.y
    );
    Fill.quad(
        x - v1.x, y - v1.y,
        x + v2.x, y + v2.y,
        x + v3.x, y + v3.y,
        x - v1.x, y - v1.y
    );
  }

  public static void drawCircleProgress(
      float x, float y, float radius,
      float stroke, float progress,
      Color color, Color backColor
  ){ drawCircleProgress(x, y, radius, stroke, stroke/2f, progress, 0, color, backColor); }

  public static void drawCircleProgress(
      float x, float y, float radius,
      float frameStroke, float barStroke, float progress,
      Color color, Color backColor
  ){ drawCircleProgress(x, y, radius, frameStroke, barStroke, progress, 0, color, backColor); }

  public static void drawCircleProgress(
      float x, float y, float radius,
      float frameStroke, float barStroke,
      float progress, float subProgress,
      Color color, Color backColor
  ){
    float parentAlpha = Draw.getColor().a;
    float rad = radius - frameStroke/2f;

    Draw.color(Color.black, parentAlpha);
    Lines.stroke(frameStroke);
    Lines.circle(x, y, rad);
    Draw.color(backColor, 0.6f*parentAlpha);
    Lines.circle(x, y, rad);
    Draw.color(Color.black, 0.6f*parentAlpha);
    Lines.stroke(barStroke);
    Lines.circle(x, y, rad);

    if (progress > 0) {
      Lines.stroke(barStroke);
      Draw.color(color, parentAlpha);
      SglDraw.arc(
          x, y, rad,
          -360f*progress, 90
      );
    }

    if (subProgress > 0){
      Lines.stroke(frameStroke);
      Draw.color(backColor, 0.5f*parentAlpha);
      float angel = -360f*Math.min(subProgress, 1 - progress);
      SglDraw.arc(
          x, y, rad, angel,
          90 - progress*360f
      );

      Draw.color(Color.black, 0.2f*parentAlpha);
      Lines.stroke(frameStroke/3f);
      SglDraw.arc(
          x, y, rad, angel,
          90 - progress*360f
      );
    }
  }

  @SuppressWarnings("unchecked")
  private static class DrawTask {
    DrawAcceptor<?> defaultFirstTask, defaultLastTask;
    protected Object defaultTarget;
    protected DrawAcceptor<?>[] tasks = new DrawAcceptor<?>[16];
    protected Object[] dataTarget = new Object[16];
    int taskCounter;
    boolean init;

    public <T> void addTask(T dataAcceptor, DrawAcceptor<T> task){
      if (tasks.length <= taskCounter){
        tasks = Arrays.copyOf(tasks, tasks.length + 1);
        dataTarget = Arrays.copyOf(dataTarget, tasks.length);
      }

      tasks[taskCounter] = task;
      dataTarget[taskCounter++] = dataAcceptor;
    }

    @SuppressWarnings("rawtypes")
    public void flush(){
      if (defaultFirstTask != null) ((DrawAcceptor)defaultFirstTask).draw(defaultTarget);

      for (int i = 0; i < taskCounter; i++) {
        ((DrawAcceptor)tasks[i]).draw(dataTarget[i]);
      }

      if (defaultLastTask != null) ((DrawAcceptor)defaultLastTask).draw(defaultTarget);

      taskCounter = 0;
      init = false;
    }
  }

  public interface DrawAcceptor<T>{
    void draw(T accept);
  }

  @SuppressWarnings("rawtypes")
  public interface DrawDef extends DrawAcceptor{
    @Override
    default void draw(Object accept){
      draw();
    }

    void draw();
  }
}
