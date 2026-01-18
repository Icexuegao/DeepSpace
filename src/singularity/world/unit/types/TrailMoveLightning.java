package singularity.world.unit.types;

import arc.math.Mathf;
import arc.util.Time;
import arc.util.pooling.Pool;

public class TrailMoveLightning implements Pool.Poolable {
  public float off;
  public float offDelta;

  public float chance = 0.3f;
  public float maxOff = 4;
  public float range = 4;

  {
    flushDelta(0);

  }

  private void flushDelta(int i) {
    offDelta = Mathf.random(i <= 0 ? -range : 0, i >= 0 ? range : 0);
  }

  public void update() {
    if (Mathf.chanceDelta(chance) || off >= maxOff || off <= -maxOff)
      flushDelta(off >= maxOff ? -1 : off <= -maxOff ? 1 : 0);
    off += offDelta*Time.delta;
  }


  @Override
  public void reset() {
    off = 0;
    offDelta = 0;
    maxOff = 4;
    range = 4;
  }
}
