package singularity.world.unit;

import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import mindustry.gen.Unit;

public class EMPModel {
  public float maxEmpHealth;
  public float empArmor;
  public float empRepair;
  public float empContinuousDamage;

  public boolean disabled;

  public EMPHealth generate(Unit unit){
    EMPHealth res = Pools.obtain(EMPHealth.class, EMPHealth::new);
    res.model = this;
    res.empHealth = maxEmpHealth;
    res.unit = unit;
    res.bind = true;

    return res;
  }

  public static class EMPHealth implements Pool.Poolable {
    public EMPModel model;
    public float empHealth;
    Unit unit;
    boolean bind;

    @Override
    public void reset() {
      model = null;
      bind = false;
      unit = null;
      empHealth = 0;
    }
  }
}
