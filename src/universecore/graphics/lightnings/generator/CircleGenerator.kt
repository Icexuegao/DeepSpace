package universecore.graphics.lightnings.generator;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import universecore.graphics.lightnings.LightningVertex;

/**环形闪电的生成器，通过指定的圆心和半径生成闪电顶点
 *
 * @since 2.3
 * @author EBwilson
 * */
public class CircleGenerator extends LightningGenerator {
  /**闪电基于的圆的半径*/
  public float radius = 16;
  /**圆的原始起点角度，这会影响time不为0时生成圆的闪电蔓延起点*/
  public float originAngle;
  /**圆的旋转方向，大于0为逆时针，否则为顺时针*/
  public int directory = 1;
  /**这个圆是否闭合，这会决定闪电的头部和尾部是连接的还是断开的*/
  public boolean enclosed = true;

  Vec2 rad = new Vec2();
  float currentRotated;

  boolean first;
  LightningVertex firstOne;

  @Override
  public void reset(){
    super.reset();
    rad.set(1, 0).setLength(radius).setAngle(originAngle);
    currentRotated = 0;
    first = true;
    firstOne = null;
  }

  @Override
  public boolean hasNext(){
    return super.hasNext() && currentRotated < 360;
  }

  @Override
  protected void handleVertex(LightningVertex vertex){
    float step = seed.random(minInterval, maxInterval);
    float rotated = step/(Mathf.pi*radius/180)*(directory >= 0? 1: -1);

    if(rotated + currentRotated >= 360){
      if (firstOne == null){
        currentRotated = 361;
        return;
      }

      vertex.isEnd = !enclosed;
      if(enclosed){
        vertex.x = firstOne.x;
        vertex.y = firstOne.y;
      }
      currentRotated = 360;
    }
    else{
      currentRotated += rotated;

      float offset = seed.random(-maxSpread, maxSpread);
      Tmp.v2.set(Tmp.v1.set(rad.rotate(rotated))).setLength(offset).scl(offset < 0? -1: 1);
      Tmp.v1.add(Tmp.v2);

      vertex.x = Tmp.v1.x;
      vertex.y = Tmp.v1.y;
    }

    if(first){
      vertex.valid = true;
      vertex.isStart = !enclosed;
      if(enclosed){
        firstOne = vertex;
      }
      first = false;
    }
  }

  @Override
  public boolean isEnclosed(){
    return enclosed;
  }

  @Override
  public float clipSize(){
    return radius + maxSpread;
  }
}
