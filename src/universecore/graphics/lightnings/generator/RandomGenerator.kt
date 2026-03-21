package universecore.graphics.lightnings.generator;

import arc.math.geom.Vec2;
import arc.util.Tmp;
import universecore.graphics.lightnings.LightningVertex;

/**随机路径的闪电生成器，给出起点路径总长度生成随机闪电路径
 *
 * @since 2.3
 * @author EBwilson
 * */
public class RandomGenerator extends LightningGenerator {
  public float maxLength = 80;
  public float maxDeflect = 70;
  public float originAngle = Float.MIN_VALUE;

  float currLength;
  Vec2 curr = new Vec2();

  boolean first;
  float maxDistance;

  @Override
  public void reset(){
    super.reset();
    currLength = 0;
    maxDistance = 0;
    first = true;
    if(originAngle == Float.MIN_VALUE){
      curr.rnd(0.001f);
    }
    else{
      curr.set(0.001f, 0).setAngle(originAngle);
    }
  }

  @Override
  protected void handleVertex(LightningVertex vertex){
    if(first){
      vertex.isStart = true;
      vertex.valid = true;
      first = false;
    }
    else{
      float distance = seed.random(minInterval, maxInterval);
      if(currLength + distance > maxLength){
        vertex.isEnd = true;
      }

      currLength += distance;
      Tmp.v1.setLength(distance).setAngle(curr.angle() + seed.random(-maxDeflect, maxDeflect));
      curr.add(Tmp.v1);
      maxDistance = Math.max(maxDistance, curr.len());
    }

    vertex.x = curr.x;
    vertex.y = curr.y;
  }

  @Override
  public float clipSize(){
    return maxDistance;
  }

  @Override
  public boolean hasNext(){
    return super.hasNext() && currLength < maxLength;
  }
}
