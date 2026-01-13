package universecore.world.lightnings.generator;

import arc.math.geom.Vec2;
import arc.util.Tmp;
import universecore.world.lightnings.LightningVertex;

/**矢量闪电生成器，生成由将沿着指定的向量创建一条直线蔓延的闪电
 *
 * @since 1.5
 * @author EBwilson
 *  移动到图形模块中*/

public class VectorLightningGenerator extends LightningGenerator{
  public Vec2 vector = new Vec2();

  float distance;
  float currentDistance;
  boolean first;

  @Override
  public void reset(){
    super.reset();
    currentDistance = 0;
    first = true;
    distance = vector.len();
  }

  @Override
  public boolean hasNext(){
    return super.hasNext() && currentDistance < distance;
  }

  @Override
  protected void handleVertex(LightningVertex vertex){
    currentDistance += seed.random(minInterval, maxInterval);

    if(currentDistance < distance - minInterval){
      if(first){
        Tmp.v2.setZero();
      }
      else{
        float offset = seed.random(-maxSpread, maxSpread);
        Tmp.v2.set(vector).setLength(currentDistance).add(Tmp.v1.set(vector).rotate90(1).setLength(offset).scl(offset < 0? -1: 1));
      }
    }
    else{
      currentDistance = distance;
      Tmp.v2.set(vector);
      vertex.isEnd = true;
    }

    vertex.x = Tmp.v2.x;
    vertex.y = Tmp.v2.y;

    if(first){
      vertex.isStart = true;
      vertex.valid = true;
      first = false;
    }
  }

  @Override
  public float clipSize(){
    return distance;
  }
}
