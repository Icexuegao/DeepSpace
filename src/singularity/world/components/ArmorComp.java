package singularity.world.components;

import mindustry.gen.Healthc;
import singularity.world.armor.Armor;

public interface ArmorComp extends Healthc{
  Armor<ArmorComp> armorType();
}
