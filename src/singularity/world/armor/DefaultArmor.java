package singularity.world.armor;

import arc.Events;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.world.meta.Stats;
import singularity.world.components.ArmorComp;

import static mindustry.gen.Building.bulletDamageEvent;

public class DefaultArmor<E extends ArmorComp> extends Armor<E>{
  public float defence = 0;
  public float damageScl = 1;

  @Override
  public void update(E entity){}


  @Override
  public boolean onCollision(Bullet bullet, E entity){
    entity.damage(Math.max(bullet.damage()*(entity instanceof Building? bullet.type.buildingDamageMultiplier: 1)*damageScl - defence, 0));
    if(entity instanceof Building b) Events.fire(bulletDamageEvent.set(b, bullet));

    return false;
  }

  @Override
  public boolean shouldPenetrate(Bullet bullet, E entity){
    return false;
  }

  @Override
  public void draw(E entity){}

  @Override
  public void setStats(Stats stats){}
}
