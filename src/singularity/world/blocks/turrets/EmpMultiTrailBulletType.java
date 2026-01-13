package singularity.world.blocks.turrets;

import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.gen.Bullet;
import mindustry.graphics.Trail;
import singularity.graphic.SglDraw;

public class EmpMultiTrailBulletType extends EmpBulletType{
  private static final Rand rand = new Rand();

  public int subTrails = 2;
  public float subTrailWidth = 2;
  public float subRotSpeed = 8;
  public float offset = 8;
  public boolean offsetMove = true;

  @Override
  public void init(Bullet b){
    super.init(b);


    Trail[] sub = new Trail[subTrails];
    b.data = sub;

    for(int i = 0; i < sub.length; i++){
      sub[i] = new Trail(trailLength/2);
    }
  }

  @Override
  public void removed(Bullet b){
    super.removed(b);
    if(b.data instanceof Trail[] trails){
      for(Trail trail: trails){
        Fx.trailFade.at(b.x, b.y, 2, trailColor, trail.copy());
      }
    }
  }

  @Override
  public void update(Bullet b){
    super.update(b);
    if(b.data instanceof Trail[] trails){
      float step = 360f/trails.length;
      Tmp.v1.set(4 + offset*(offsetMove? b.fslope(): 1), 0).setAngle(b.rotation() + 90);
      for(int i = 0; i < trails.length; i++){
        rand.setSeed(b.id);
        float lerp = Mathf.sinDeg(rand.random(0f, 360f) + Time.time*subRotSpeed + step*i);
        trails[i].update(b.x + Tmp.v1.x*lerp, b.y + Tmp.v1.y*lerp);
      }
    }
  }

  @Override
  public void drawTrail(Bullet b){
    super.drawTrail(b);
    if(b.data instanceof Trail[] trails){
      float step = 360f/trails.length;
      Tmp.v1.set(4 + offset*(offsetMove? b.fslope(): 1), 0).setAngle(b.rotation() + 90);
      for(int i = 0; i < trails.length; i++){
        rand.setSeed(b.id);
        float lerp = Mathf.sinDeg(rand.random(0f, 360f) + Time.time*subRotSpeed + step*i);
        SglDraw.drawDiamond(
            b.x + Tmp.v1.x*lerp, b.y + Tmp.v1.y*lerp,
            8, 4,
            b.rotation() + Tmp.v2.set(b.vel.len(), -Mathf.sinDeg(Time.time*subRotSpeed + step*i)*2*b.fslope()).angle()
        );
        trails[i].draw(trailColor, subTrailWidth);
      }
    }
  }
}
