package universecore.math.gravity;

import arc.func.Boolf;
import arc.func.Func;
import arc.math.geom.Vec2;
import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.util.Time;

/**引力场模拟的容器，记录了一些引力场的行为
 *
 * @since 1.3
 * @author EBwilson*/
public class GravityField{
  public static final float GRAV_CONST = 0.667259f;
  private static final Vec2 tmp = new Vec2();

  public final GravitySystem system;

  private final ObjectSet<GravityField> otherFields = new ObjectSet<>();
  private final ObjectMap<GravityField, Vec2> bufferAccelerations = new ObjectMap<>();

  public GravityField(GravitySystem system){
    this.system = system;
  }

  /**设置关联的另一个场，这会重置当前的场状态，然后与给出的场产出准则创建新的关联关系
   *
   * @param itr 这个迭代器需要迭代所有需要处理的对象，以绑定到一个引力场
   * @param filter 元素的过滤器
   * @param getter 对某一目标创建一个关联引力场的方法*/
  public <T> void setAssociatedFields(Iterable<T> itr, Boolf<T> filter, Func<T, GravityField> getter){
    for(GravityField field: otherFields){
      remove(field);
    }
    for(T t: itr){
      if(filter.get(t)) add(getter.get(t));
    }
  }

  /**设置关联所有其他场，这会重置当前的场状态，然后与给出的场产出准则创建新的关联关系
   *
   * @param itr 这个迭代器需要迭代所有需要处理的其他引力场
   * @param filter 元素的过滤器*/
  public <T extends GravityField> void setAssociatedFields(Iterable<T> itr, Boolf<T> filter){
    for(GravityField field: otherFields){
      remove(field);
    }
    for(T t: itr){
      if(filter.get(t)) add(t);
    }
  }

  /**将一个引力场加入到关联场中*/
  public void add(GravityField field){
    if(field == null) return;
    otherFields.add(field);
    field.otherFields.add(this);
  }

  /**与目标场取消关联*/
  public void remove(GravityField field){
    if(field == null) return;
    otherFields.remove(field);
    field.otherFields.remove(this);
  }

  /**将自身从关联的所有其他引力场取消关联*/
  public void remove(){
    for(GravityField field: otherFields){
      remove(field);
    }
  }

  /**更新引力系统，结果通过引力系统的方法gravityUpdate传入，不要在一次游戏刷新内多次调用这个方法*/
  public void update(){
    Vec2 speedDelta;
    float distance;
    float force;
    float delta;

    tmp.setZero();
    for(GravityField field: otherFields){
      if((speedDelta = bufferAccelerations.get(field)) == null || speedDelta.isZero()){
        if(speedDelta == null) speedDelta = new Vec2();
        GravitySystem sys = field.system;

        distance = speedDelta.set(sys.position()).sub(system.position()).len();
        force = GRAV_CONST*sys.mass()*system.mass()/(distance*distance);
        delta = 60/Time.delta;
        bufferAccelerations.put(field, speedDelta.setLength(force/system.mass()/delta));
        field.bufferAccelerations.get(this, Vec2::new).set(speedDelta).setLength(force/sys.mass()/delta).scl(-1);
      }
      tmp.add(speedDelta);
    }

    system.gravityUpdate(tmp);
    clearBuffer(false);
  }

  public void clearBuffer(boolean all){
    if(all){
      for(GravityField field: otherFields){
        field.clearBuffer(false);
      }
    }
    for(Vec2 vec2: bufferAccelerations.values()){
      vec2.setZero();
    }
  }
}
