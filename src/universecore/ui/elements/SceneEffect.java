package universecore.ui.elements;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mat;
import arc.math.Rand;
import arc.scene.Element;
import arc.scene.Group;
import arc.scene.ui.layout.Scl;
import arc.util.Align;
import arc.util.Time;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import mindustry.entities.Effect;

/**场景特效工具，用于在UI中显示{@link Effect}的布局工具。
 * <p>此工具已池化，使用内部静态方法获取并添加特效实例，播放完毕后自动回收。
 *
 * <p>工具使用常规的世界中使用的{@linkplain Effect 效果}进行显示，
 * 一般而言，世界坐标与屏幕坐标的像素长度比例为1:4，使用为游戏内环境编写的特效时，建议提供4倍的放大比例：
 * <pre>{@code
 * SceneEffect effect = SceneEffect.showOnStage(Fx.regenSuppressParticle, x, y);
 * effect.scaleX = 4;
 * effect.scaleY = 4;
 * }</pre>
 * */
public class SceneEffect extends Element implements Pool.Poolable {
  private static final Rand idRand = new Rand();

  private final Effect.EffectContainer container = new Effect.EffectContainer();
  private final Mat transform = new Mat(), last = new Mat();
  private final Color tmpColor = new Color();

  public int id;
  public Effect effect;
  public float lifetime;
  public float time;
  public Object data;

  /**@see SceneEffect#showOnStage(Effect, float, float, float, Color, Object) */
  public static SceneEffect showOnStage(Effect fx, float x, float y){
    return showOnStage(fx, x, y, 0, Color.white, null);
  }
  /**@see SceneEffect#showOnStage(Effect, float, float, float, Color, Object) */
  public static SceneEffect showOnStage(Effect fx, float x, float y, float rotation){
    return showOnStage(fx, x, y, rotation, Color.white, null);
  }
  /**@see SceneEffect#showOnStage(Effect, float, float, float, Color, Object) */
  public static SceneEffect showOnStage(Effect fx, float x, float y, float rotation, Color color){
    return showOnStage(fx, x, y, rotation, color, null);
  }
  /**@see SceneEffect#showOnStage(Effect, float, float, float, Color, Object) */
  public static SceneEffect showOnStage(Effect fx, float x, float y, float rotation, Object data){
    return showOnStage(fx, x, y, rotation, Color.white, data);
  }
  /**使用给出的特效及参数向根UI元素添加一个特效元素，坐标系统为屏幕坐标，原点为屏幕左下角。
   *
   * @param fx 显示的目标特效
   * @param x 特效的x显示坐标
   * @param y 特效的y显示坐标
   * @param rotation 特效的旋转角度
   * @param color 特效的颜色参数
   * @param data 特效的参数数据*/
  public static SceneEffect showOnStage(Effect fx, float x, float y, float rotation, Color color, Object data){
    SceneEffect e = Pools.obtain(SceneEffect.class, SceneEffect::new);
    Core.scene.add(e);

    return setDefAttr(fx, x, y, rotation, color, data, e);
  }

  /**@see SceneEffect#showOnStage(Effect, float, float, float, Color, Object) */
  public static SceneEffect showOnGroup(Group target, Effect fx, float x, float y){
    return showOnGroup(target, fx, x, y, 0, Color.white, null);
  }
  /**@see SceneEffect#showOnStage(Effect, float, float, float, Color, Object) */
  public static SceneEffect showOnGroup(Group target, Effect fx, float x, float y, float rotation){
    return showOnGroup(target, fx, x, y, rotation, Color.white, null);
  }
  /**@see SceneEffect#showOnStage(Effect, float, float, float, Color, Object) */
  public static SceneEffect showOnGroup(Group target, Effect fx, float x, float y, float rotation, Color color){
    return showOnGroup(target, fx, x, y, rotation, color, null);
  }
  /**@see SceneEffect#showOnStage(Effect, float, float, float, Color, Object) */
  public static SceneEffect showOnGroup(Group target, Effect fx, float x, float y, float rotation, Object data){
    return showOnGroup(target, fx, x, y, rotation, Color.white, data);
  }
  /**使用给出的特效及参数向目标显示容器中添加一个特效元素，坐标为目标父级元素的局部坐标系，原点为容器的边界左下角
   *
   * @param target 特效显示的父级容器
   * @param fx 显示的目标特效
   * @param x 特效的x显示坐标偏移量
   * @param y 特效的y显示坐标偏移量
   * @param rotation 特效的旋转角度
   * @param color 特效的颜色参数
   * @param data 特效的参数数据*/
  public static SceneEffect showOnGroup(Group target, Effect fx, float x, float y, float rotation, Color color, Object data){
    SceneEffect e = Pools.obtain(SceneEffect.class, SceneEffect::new);
    target.addChild(e);

    return setDefAttr(fx, x, y, rotation, color, data, e);
  }

  private static SceneEffect setDefAttr(Effect fx, float x, float y, float rotation, Color color, Object data, SceneEffect e) {
    idRand.setSeed(System.nanoTime());
    e.id = idRand.nextInt();
    e.effect = fx;
    e.lifetime = fx.lifetime;
    e.data = data;
    e.time = 0f;
    e.setPosition(x, y, Align.center);
    e.setRotation(rotation);
    e.color.set(color);

    return e;
  }

  private SceneEffect(){}

  @Override
  public void act(float delta) {
    super.act(delta);

    time += Time.delta;
    if (time > lifetime){
      remove();
    }
  }

  @Override
  public void draw() {
    super.draw();
    transform.set(last.set(Draw.trans()));
    transform.translate(x + width/2, y + height/2);
    transform.scale(Scl.scl(scaleX), Scl.scl(scaleY));

    Draw.reset();
    Draw.trans(transform);
    container.set(id, tmpColor.set(color).a(color.a*parentAlpha), time, lifetime, rotation, 0, 0, data);
    effect.render(container);
    Draw.reset();
    Draw.trans(last);
  }

  @Override
  public boolean remove() {
    boolean b = super.remove();
    if (b) Pools.free(this);
    return b;
  }

  @Override
  public void reset() {
    effect = null;
    lifetime = 0;
    time = 0;
    data = null;
    width = 0;
    height = 0;
    scaleX = 1;
    scaleY = 1;
    color.set(Color.white);
    x = 0;
    y = 0;
    rotation = 0;
    id = 0;
  }
}
