package singularity.world.blocks.turrets;

import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.gen.Unit;
import singularity.Sgl;

public class EmpBulletType extends BulletType {
  public float empDamage;
  public float empRange;

  public EmpBulletType(){
  }

  public EmpBulletType(float speed, float damage){
    this.speed = speed;
    this.damage = damage;
  }

  @Override
  public void hitEntity(Bullet b, Hitboxc entity, float health) {
    super.hitEntity(b, entity, health);
    if (empDamage > 0) {
      if (entity instanceof Unit unit) {
        Sgl.empHealth.empDamage(unit, empDamage, false);
      }
    }
  }

  @Override
  public void createSplashDamage(Bullet b, float x, float y) {
    super.createSplashDamage(b, x, y);
    if (empRange > 0 && empDamage > 0) Units.nearbyEnemies(b.team, b.x, b.y, empRange, u -> {
      Sgl.empHealth.empDamage(u, empDamage, false);
    });
  }
}
