package universecore.math.gravity;

import arc.math.geom.Vec2;

/**引力系统接口，引力运算时的处理单元
 *
 * @since 1.3
 * @author EBwilson*/
public interface GravitySystem{
  /**该系统所激发的引力场*/
 // @Annotations.BindField("gravityField")
  default GravityField field(){
    return null;
  }

  /**系统总质量，单位千吨(kt)，允许质量为负数，两个系统质量符号相同互相吸引，否则互相排斥*/
  float mass();

  /**系统重心的位置矢量*/
  Vec2 position();

  /**引力更新调用此方法，将计算出的加速度作为参数传入*/
  void gravityUpdate(Vec2 acceleration);
}
