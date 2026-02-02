package singularity.world.blocks.turrets;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Interp;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import universecore.world.lightnings.LightningContainer;
import universecore.world.lightnings.generator.VectorLightningGenerator;

public class LightLaserBulletType extends EmpLightningBulletType{
  public Effect laserEffect = Fx.lancerLaserShootSmoke;
  public float length = 80;
  public float innerScl = 0.75f, edgeScl = 1.2f;
  public float innerWidth = 3, width = 6, edgeWidth = 10;

  public Color[] colors = {Pal.lancerLaser.cpy().mul(1f, 1f, 1f, 0.4f), Pal.lancerLaser, Color.white};

  public int lightnings = 2;
  public float lightningTime = 5;
  public VectorLightningGenerator generator = new VectorLightningGenerator() {{
    minInterval = 4;
    maxInterval = 12;
    maxSpread = 9;
  }};
  public float lightningMinWidth = 1.8f;
  public float lightningMaxWidth = 2.75f;

  public LightLaserBulletType(){
    speed = 0;
    lifetime = 30;
    collides = false;
    absorbable = false;
    hittable = false;

    keepVelocity = false;

    hitEffect = Fx.hitLaserBlast;
    shootEffect = Fx.hitLancer;

    despawnEffect = Fx.none;

    pierce = true;
  }

  @Override
  public void init() {
    super.init();
    range = length;
    hitColor = colors[1];
    drawSize = range;
  }

  @Override
  public void init(Bullet b, LightningContainer c) {
   // Sounds.spark.at(b.x, b.y, 1.2f);

    c.lifeTime = lifetime;
    c.time = lightningTime;
    c.lerp = Interp.linear;
    c.minWidth = lightningMinWidth;
    c.maxWidth = lightningMaxWidth;

    Damage.collideLaser(b, range, false, true, -1);
    laserEffect.at(b.x, b.y, b.rotation(), b.fdata * 0.75f);
    generator.vector.set(b.fdata, 0).setAngle(b.rotation());

    for (int i = 0; i < lightnings; i++) {
      c.create(generator);
    }
  }

  @Override
  public void draw(Bullet b) {
    super.draw(b);

    float len = Math.min(b.fdata, range)/2;

    float fin2 = b.fin(Interp.pow2);
    float out2 = 1 - fin2;
    Tmp.v1.set(len/3, 0).setAngle(b.rotation()).scl(1 + fin2);
    float dx = b.x + Tmp.v1.x;
    float dy = b.y + Tmp.v1.y;

    Draw.color(colors[0]);
    Drawf.tri(dx, dy, edgeWidth*out2, (len/3 - 6)*(1 + fin2)*edgeScl, b.rotation() - 180);
    Drawf.tri(dx, dy, edgeWidth*out2, len*(1 + 1.85f*fin2), b.rotation());

    Draw.color(colors[1]);
    Drawf.tri(dx, dy, width*out2, (len/3 - 6)*(1 + fin2), b.rotation() - 180);
    Drawf.tri(dx, dy, width*out2, len*(1 + 1.85f*fin2), b.rotation());

    Draw.color(colors[2]);
    Drawf.tri(dx, dy, innerWidth*out2, (len/3 - 6)*(1 + fin2)*innerScl, b.rotation() - 180);
    Drawf.tri(dx, dy, innerWidth*out2, len*(1 + 1.85f*fin2), b.rotation());
  }
}
