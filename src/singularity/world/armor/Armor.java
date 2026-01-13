package singularity.world.armor;

import mindustry.gen.Bullet;
import mindustry.world.meta.Stats;
import singularity.world.components.ArmorComp;

public abstract class Armor<E extends ArmorComp>{
  public abstract void update(E entity);

  public abstract boolean onCollision(Bullet bullet, E entity);

  public abstract boolean shouldPenetrate(Bullet bullet, E entity);

  public abstract void draw(E entity);

  public abstract void setStats(Stats stats);
}
