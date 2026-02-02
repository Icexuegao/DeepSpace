package singularity.world.unit.abilities;

import arc.Core;
import arc.func.Cons;
import arc.func.Floatf;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import singularity.graphic.SglDraw;

public class MirrorFieldAbility extends MirrorShieldBase{
  public boolean rotation;
  public Seq<ShieldShape> shapes = new Seq<>(ShieldShape.class);

  /**搜索子弹的范围，若不设置则为默认值，若护盾的一部分会运动则须手动将此数据设置为覆盖子部分的最大范围*/
  public float nearRadius = -1;

  @Override
  public String localized() {
    return Core.bundle.get("ability.mirror_shield");
  }

  @Override
  public void init(UnitType type) {
    super.init(type);
    if (nearRadius < 0){
      for (ShieldShape shape : shapes) {
        nearRadius = Math.max(nearRadius, Mathf.dst(shape.x, shape.y) + shape.radius);
      }
    }
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    MirrorFieldAbility res = (MirrorFieldAbility) super.clone();
    for (int i = 0; i < shapes.size; i++) {
      if (res.shapes.get(i) != null) res.shapes.set(i, shapes.get(i).clone());
    }
    return res;
  }

  @Override
  public boolean shouldReflect(Unit unit, Bullet bullet) {
    for (ShieldShape shape : shapes) {
      if ((rotation && shape.inlerp(unit, unit.rotation() - 90, bullet, radScl))
      || (!rotation && shape.inlerp(unit, 0, bullet, radScl))) return true;
    }
    return false;
  }

  @Override
  public void eachNearBullets(Unit unit, Cons<Bullet> cons) {
    Groups.bullet.intersect(unit.x - nearRadius, unit.y - nearRadius, nearRadius*2, nearRadius*2, b -> {
      if (unit.team != b.team) cons.get(b);
    });
  }

  @Override
  public void update(Unit unit) {
    super.update(unit);

    for (ShieldShape shape : shapes) {
      shape.flushMoves(unit);
    }
  }

  @Override
  public void draw(Unit unit) {
    if(unit.shield > 0) {
      Draw.color(unit.team.color, Color.white, Mathf.clamp(alpha));
      Draw.z(SglDraw.mirrorField + 0.001f*alpha);

      for (ShieldShape shape : shapes) {
        if (rotation) shape.draw(unit, unit.rotation() - 90, alpha, radScl);
        else shape.draw(unit, 0, alpha, radScl);
      }
    }
  }

  public static class ShieldShape implements Cloneable{
    float x, y, angle;
    int sides;
    float radius;

    @Nullable public ShapeMove movement;

    private float moveOffsetX, moveOffsetY, moveOffsetRot;

    public ShieldShape(int sides, float x, float y, float angle, float radius) {
      this.sides = sides;
      this.x = x;
      this.y = y;
      this.radius = radius;
      this.angle = angle;
    }

    public boolean inlerp(Unit unit, float rotation, Bullet bullet, float scl) {
      return Intersector.isInRegularPolygon(sides,
          unit.x + moveOffsetX + Angles.trnsx(rotation, x*scl, y*scl),
          unit.y + moveOffsetY + Angles.trnsy(rotation, x*scl, y*scl),
          radius*scl,
          rotation + angle + moveOffsetRot,
          bullet.x(), bullet.y());
    }

    public void draw(Unit unit, float rotation, float alpha, float scl){
      float drawX = unit.x + moveOffsetX + Angles.trnsx(rotation, x*scl, y*scl);
      float drawY = unit.y + moveOffsetY + Angles.trnsy(rotation, x*scl, y*scl);

      Draw.color(unit.team.color, Color.white, Mathf.clamp(alpha));

      if(true){
        Draw.z(SglDraw.mirrorField + 0.001f * alpha);
        Fill.poly(drawX, drawY, sides, radius*scl, rotation + angle + moveOffsetRot);
      }else{
        Draw.z(SglDraw.mirrorField + 1);
        Lines.stroke(1.5f);
        Draw.alpha(0.09f);
        Fill.poly(drawX, drawY, sides, radius*scl, rotation + angle + moveOffsetRot);

        for (int i = 1; i <= 4; i++) {
          Draw.alpha(i/4f);
          Lines.poly(drawX, drawY, sides, radius*scl*(i/4f), rotation + angle + moveOffsetRot);
        }
      }
    }

    public void flushMoves(Unit unit) {
      if (movement == null) return;

      Vec2 off = movement.offset(unit);
      moveOffsetX = off.x;
      moveOffsetY = off.y;
      moveOffsetRot = movement.rotation(unit);
    }

    @Override
    public ShieldShape clone() {
      try {
        //fuck java
        return (ShieldShape) super.clone();
      } catch (CloneNotSupportedException e) {
        throw new AssertionError();
      }
    }
  }

  public static class ShapeMove{
    public float x, y, angle;
    public float moveX, moveY, moveRot;
    public float rotateSpeed = 0;
    public Interp interp = Interp.linear;
    public Floatf<Unit> lerp = e -> 1;

    private final Vec2 vec2 = new Vec2();
    private static final Vec2 tmp = new Vec2();

    @Nullable public ShapeMove childMoving;

    protected float lerp(Unit unit){
      return interp.apply(lerp.get(unit));
    }

    public Vec2 offset(Unit unit){
      vec2.set(x, y).add(tmp.set(moveX, moveY).scl(lerp(unit))).rotate(rotateSpeed == 0? moveRot*lerp(unit): Time.time*rotateSpeed);

      return childMoving != null? vec2.add(childMoving.offset(unit)): vec2;
    }

    public float rotation(Unit unit){
      return angle + (childMoving != null? childMoving.rotation(unit): rotateSpeed == 0? moveRot*lerp(unit): Time.time*rotateSpeed);
    }
  }
}
