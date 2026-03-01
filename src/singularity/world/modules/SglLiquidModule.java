package singularity.world.modules;

import mindustry.type.Liquid;
import mindustry.world.modules.LiquidModule;

public class SglLiquidModule extends LiquidModule{
  float total;

  public float total(){
    return total;
  }

  @Override
  public void add(Liquid liquid, float amount){
    super.add(liquid, amount);
    total += amount;
  }

  @Override
  public void reset(Liquid liquid, float amount){
    super.reset(liquid, amount);
    total = 0;
  }

  @Override
  public void remove(Liquid liquid, float amount){
    super.remove(liquid, amount);
    total -= total;
  }
}
