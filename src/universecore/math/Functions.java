package universecore.math;

/**
 * 提供一些复合、高等数学函数的静态方法
 * 全部需要输入一个x坐标，返回其y轴坐标映射
 * @author EBwilson
 * @since 1.1
 */
public class Functions {
  /** 渐近函数，由指定起始点到目标点的逼近 */
  public static double lerp(double origin, double dest, double rate, double x) {
    double a = 1 - rate;
    double b = rate * dest;

    double powered = Math.pow(a, x - 1);

    return origin * powered + (b * powered - b / a) / (1 - 1 / a);
  }

  /** 对应一条s形态曲线的函数 */
  public static double sCurve(double left, double right, double dx, double dy, double rate, double x) {
    double diff = right - left;
    double xValue = dx * rate;

    return diff / Math.pow(2, xValue - rate * x) + dy + left;
  }

  /** 增减曲线，在最适值处达到最高点，两侧递减，参数可分别控制左右衰减速度 */
  public static double lerpIncrease(double lerpLeft, double lerpRight, double max, double optimal, double x) {
    if (x < 0) return 0;
    return x >= 0 && x < optimal ? -max * Math.pow(1 - x / optimal, lerpLeft) + max :
            -max * Math.pow(1 - optimal / x, lerpRight) + max;
  }
}
