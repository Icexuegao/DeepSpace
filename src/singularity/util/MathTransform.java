package singularity.util;

import arc.func.Floatc2;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;

public class MathTransform{
  private static final Vec2 tmp = new Vec2();
  
  /**使用极坐标形式的傅里叶级数计算向量坐标
   *
   * @param time 为傅里叶级数传入的参数（或者插值）
   * @param params 参数组，每三个数据确定一个sin函数，参数格式：{@code {角速度, 初相位, 极值, ...}}
   * @return 指定的傅里叶级数在给定插值下计算的向量，为正交坐标系形式*/
  public static Vec2 fourierSeries(float time, float... params){
    tmp.setZero();
    for(int i = 0; i < params.length; i+=3){
      float w = params[i];
      float f = params[i + 1];
      float l = params[i + 2];

      tmp.add(
          Angles.trnsx(f + time*w, l),
          Angles.trnsy(f + time*w, l)
      );
    }

    return tmp;
  }

  /**使用极坐标形式的傅里叶级数计算向量坐标，计算结果通过函数回调
   *
   * @param transRecall 计算结果的回调函数
   * @param time 为傅里叶级数传入的参数（或者插值）
   * @param params 参数组，每三个数据确定一个sin函数，参数格式：{@code {角速度, 初相位, 极值, ...}}*/
  public static void fourierSeries(Floatc2 transRecall, float time, float... params){
    Vec2 v = fourierSeries(time, params);
    transRecall.get(v.x, v.y);
  }

  public static float gradientRotate(float rad, float fine){
    return gradientRotate(rad, fine, 0.25f, 4);
  }

  public static float gradientRotate(float rad, float fine, int sides){
    return gradientRotate(rad, fine, 1f/sides, 4);
  }

  public static float gradientRotate(float rad, float fine, float off, int sides){
    return rad - off*Mathf.sin(rad*sides + fine) + fine/sides;
  }

  public static float gradientRotateDeg(float deg, float fine){
    return gradientRotate(deg*Mathf.degRad, fine*Mathf.degRad, 0.25f, 4)*Mathf.radDeg;
  }

  public static float gradientRotateDeg(float deg, float fine, int sides){
    return gradientRotate(deg*Mathf.degRad, fine*Mathf.degRad, 1f/sides, 4)*Mathf.radDeg;
  }

  public static float gradientRotateDeg(float deg, float fine, float off, int sides){
    return gradientRotate(deg*Mathf.degRad, fine*Mathf.degRad, off, sides)*Mathf.radDeg;
  }

  public static float innerAngle(float a, float b) {
    a %= 360;
    b %= 360;
    return b - a > 180? b - a - 360: b - a < -180? b - a + 360: b - a;
  }
}
