package singularity.graphic;

import arc.graphics.Pixmap;
import arc.math.Mathf;

/**包含了一些在图形绘制时会用到的实用方法*/
public class GraphicUtils{
  /**按90度的倍数旋转一个{@link Pixmap}。该方法不改变原pixmap,返回为一个拷贝
   *
   * @param target 要旋转的目标pixmap
   * @param rotate 旋转角度系数，旋转的实际角度为90*rotate
   * @return 一个经过旋转的pixmap拷贝*/
  @SuppressWarnings("SuspiciousNameCombination")
  public static Pixmap rotatePixmap90(Pixmap target, int rotate){
    Pixmap res = new Pixmap(target.width, target.height);

    for(int x = 0; x < target.width; x++){
      for(int y = 0; y < target.height; y++){
        int c = target.get(x, y);
        switch(Mathf.mod(-rotate, 4)){
          case 0 -> res.set(x, y, c);
          case 1 -> res.set(target.width - y - 1, x, c);
          case 2 -> res.set(target.width - x - 1, target.height - y - 1, c);
          case 3 -> res.set(y, target.height - x - 1, c);
        }
      }
    }

    return res;
  }
}
