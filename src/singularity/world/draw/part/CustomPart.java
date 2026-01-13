package singularity.world.draw.part;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.entities.part.DrawPart;

public class CustomPart extends DrawPart{
  public Drawer draw;
  public Seq<PartMove> moves = new Seq<>();

  public PartProgress progress = p -> 1;
  public float layer = -1;

  public float x, y, drawRadius, rotation;
  public float moveX, moveY, drawRadiusTo, moveRot;
  public boolean mirror;
  
  private final Vec2 vec = new Vec2();
  private final Vec2 vec2 = new Vec2();

  @Override
  public void draw(PartParams params){
    float prog = Mathf.clamp(progress.get(params));
    float z = Draw.z();

    float dx = 0, dy = 0, dr = 0;
    for(PartMove move: moves){
      dx += move.x*move.progress.get(params);
      dy += move.y*move.progress.get(params);
      dr += move.rot*move.progress.get(params);
    }

    float rot = rotation + moveRot*prog + dr;
    vec.set(
        x + moveX*prog + dx,
        y + moveY*prog + dy
    ).rotate(params.rotation - 90);
    vec2.set(drawRadius + (drawRadiusTo - drawRadius)*prog, 0).setAngle(rot).rotate(params.rotation);

    float drawX = vec.x + vec2.x;
    float drawY = vec.y + vec2.y;

    if(layer >= 0) Draw.z(layer);
    draw.draw(params.x + drawX, params.y + drawY, params.rotation + rot, prog);

    if(mirror){
      vec.setAngle(2*params.rotation - vec.angle());
      vec2.setAngle(-rot).rotate(params.rotation);
      drawX = vec.x + vec2.x;
      drawY = vec.y + vec2.y;
      draw.draw(params.x + drawX, params.y + drawY, params.rotation - rot, prog);
    }
    Draw.z(z);
  }

  @Override
  public void load(String name){}

  public interface Drawer{
    void draw(float x, float y, float rotation, float progress);
  }
}
