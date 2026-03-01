package universecore.graphics;

import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import mindustry.graphics.Trail;

public class PoolableTrail extends Trail implements Pool.Poolable {
  private PoolableTrail() {
    super(0);
  }

  @Override
  public Trail copy() {
    throw new UnsupportedOperationException("poolable trail cannot be copied");
  }

  public static PoolableTrail get(int length){
    PoolableTrail trail = Pools.obtain(PoolableTrail.class, PoolableTrail::new);
    trail.length = length;

    return trail;
  }

  @Override
  public void reset() {
    clear();
  }
}
