package singularity.world.unit.abilities;

import mindustry.gen.Hitboxc;
import mindustry.gen.Unit;

public interface ICollideBlockerAbility {
  boolean blockedCollides(Unit unit, Hitboxc other);
}
