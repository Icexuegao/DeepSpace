package singularity.world.unit.types;

import arc.math.geom.Vec2;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import singularity.graphic.SglDraw;
import singularity.graphic.SglDrawConst;
import singularity.world.SglFx;

public class EdgeFragBullet extends BulletType {
  {
    damage = 80;
    splashDamage = 40;
    splashDamageRadius = 24;
    speed = 4;
    hitSize = 3;
    lifetime = 120;
    despawnHit = true;
    hitEffect = SglFx.diamondSpark;
    hitColor = SglDrawConst.matrixNet;

    collidesTiles = false;

    homingRange = 160;
    homingPower = 0.075f;

    trailColor = SglDrawConst.matrixNet;
    trailLength = 25;
    trailWidth = 3f;
  }

  @Override
  public void draw(Bullet b) {
    super.draw(b);
    SglDraw.drawDiamond(b.x, b.y, 10, 4, b.rotation());
  }

  @Override
  public void update(Bullet b) {
    super.update(b);

    b.vel.lerpDelta(Vec2.ZERO, 0.04f);
  }
}
