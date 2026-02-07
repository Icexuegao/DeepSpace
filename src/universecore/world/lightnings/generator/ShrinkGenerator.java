package universecore.world.lightnings.generator;

import arc.math.geom.Vec2;
import arc.util.Tmp;
import universecore.world.lightnings.LightningVertex;

/**
 * 收缩闪电的生成器，这会生成一定范围内向中心蔓延的闪电
 * @author EBwilson
 * @since 1.5
 */
public class ShrinkGenerator extends LightningGenerator {
  public float minRange, maxRange;

  Vec2 vec = new Vec2();
  float distance;
  float currentDistance;
  boolean first;

  @Override
  public void reset() {
    super.reset();
    vec.rnd(distance = seed.random(minRange, maxRange));
    currentDistance = distance;
    first = true;
  }

  @Override
  public boolean hasNext() {
    return super.hasNext() && currentDistance > 0;
  }

  @Override
  protected void handleVertex(LightningVertex vertex) {
    currentDistance -= seed.random(minInterval, maxInterval);

    if (currentDistance > minInterval) {
      if (first) {
        Tmp.v2.set(vec);
      } else {
        float offset = seed.random(-maxSpread, maxSpread);
        Tmp.v2.set(vec).setLength(currentDistance).add(Tmp.v1.set(vec).rotate90(1).setLength(offset).scl(offset < 0 ? -1 : 1));
      }
    } else {
      currentDistance = 0;
      Tmp.v2.setZero();
      vertex.isEnd = true;
    }

    vertex.x = Tmp.v2.x;
    vertex.y = Tmp.v2.y;

    if (first) {
      vertex.isStart = true;
      vertex.valid = true;
      first = false;
    }
  }

  @Override
  public float clipSize() {
    return 0;
  }
}
