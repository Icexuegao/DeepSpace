package singularity.world.blocks.turrets;

import arc.graphics.g2d.Draw;
import arc.util.Time;
import arc.util.pooling.Pools;
import ice.entities.bullet.base.BulletType;
import mindustry.gen.Bullet;
import singularity.world.SglFx;
import universecore.world.lightnings.LightningContainer;

public class LightningBulletType extends BulletType {
  public LightningBulletType(float time, float damage){
    super(time, damage);
  }

  public LightningBulletType(){
    super();
  }

  @Override
  public void init(Bullet bullet){
    super.init(bullet);

    LightningContainer cont;
    bullet.data = cont = Pools.obtain(LightningContainer.PoolLightningContainer.class, LightningContainer.PoolLightningContainer::new);

    init(bullet, cont);
  }

  public void init(Bullet b, LightningContainer cont){

  }

  @Override
  public void update(Bullet b){
    super.update(b);

    if(b.data instanceof LightningContainer c){
      update(b, c);
    }
  }

  @Override
  public void draw(Bullet b){
    super.draw(b);

    if(b.data instanceof LightningContainer c){
      draw(b, c);
    }
  }

  public void draw(Bullet b, LightningContainer c){
    Draw.color(b.type.hitColor);
    c.draw(b.x, b.y);
  }

  public void update(Bullet bullet, LightningContainer container){
    container.update();
  }

  @Override
  public void removed(Bullet b){
    super.removed(b);

    if(b.data instanceof LightningContainer c){
      SglFx.lightningCont.at(b.x, b.y, 0, b.type.hitColor, c);
      Time.run(210, () -> Pools.free(c));
    }
  }
}
