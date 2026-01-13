package singularity.world;

import arc.graphics.Color;
import arc.graphics.g2d.Bloom;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.Effect;
import mindustry.entities.effect.MultiEffect;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import singularity.Sgl;
import singularity.graphic.SglDraw;
import singularity.graphic.SglDrawConst;
import singularity.world.blocks.defence.GameOfLife;
import singularity.world.blocks.product.NormalCrafter;
import singularity.world.unit.abilities.MirrorFieldAbility;
import universecore.math.Functions;
import universecore.world.lightnings.LightningContainer;
import universecore.world.lightnings.generator.RandomGenerator;

import static arc.graphics.g2d.Draw.alpha;
import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.*;
import static arc.math.Mathf.*;

public class SglFx{
  private static final Rand rand = new Rand();

  public final static Effect gasLeak = new Effect(90, e -> {
    if(!(e.data() instanceof Number)) return;
    float param = ((Number) e.data()).floatValue();

    Draw.color(e.color, Color.lightGray, e.fin());
    Draw.alpha(0.75f*param*e.fout());

    randLenVectors(e.id, 1, 8f + e.fin()*(param + 3), (x, y) -> {
      Fill.circle(e.x + x, e.y + y, 0.55f + e.fslope()*4.5f);
    });
  });

  public final static Effect moveParticle = new Effect(90, e -> {
    Draw.color(e.color);

    Tmp.v1.setZero();
    if (e.data instanceof Number n){
      Tmp.v1.set(n.floatValue(), 0).setAngle(e.rotation);
    }

    float rad = Mathf.randomSeed(e.id, 1f, 3f)*e.fout(Interp.pow2Out);

    Fill.circle(e.x + Tmp.v1.x*e.fin(), e.y + Tmp.v1.y*e.fin(), rad);
  });

  public final static Effect moveDiamondParticle = new Effect(90, e -> {
    Draw.color(e.color);

    Tmp.v1.setZero();
    if (e.data instanceof Number n){
      Tmp.v1.set(n.floatValue(), 0).setAngle(e.rotation);
    }

    float rad = Mathf.randomSeed(e.id, 1.6f, 3.4f)*e.fout(Interp.pow2Out);

    if (Mathf.randomSeed(e.id) > 0.5f){
      Lines.stroke(rad/2f);
      Lines.square(e.x + Tmp.v1.x*e.fin(), e.y + Tmp.v1.y*e.fin(), rad, e.fin()*Mathf.randomSeed(e.id, 180f, 480f));
    }
    else {
      Fill.square(e.x + Tmp.v1.x*e.fin(), e.y + Tmp.v1.y*e.fin(), rad, e.fin()*Mathf.randomSeed(e.id, 180f, 480f));
    }
  });

  public final static Effect cloudGradient = new Effect(45, e -> {
    Draw.color(e.color, 0f);

    Draw.z(Layer.flyingUnit + 1);
    SglDraw.gradientCircle(e.x, e.y, 14*e.fout(), 0.6f);
  });

  public final static Effect shootRecoilWave = new Effect(40, e -> {
    Draw.color(e.color);
    for(int i : signs){
      Drawf.tri(e.x, e.y, 15f * e.fout(), 50f, e.rotation + 40f*i);
    }
  });

  public final static Effect impactWaveSmall = new Effect(18, e -> {
    Draw.color(e.color);
    Lines.stroke(5*e.fout());
    Lines.circle(e.x, e.y, 36*e.fin(Interp.pow3Out));
  });

  public final static Effect impactWave = new Effect(24, e -> {
    Draw.color(e.color);
    Lines.stroke(6*e.fout());
    Lines.circle(e.x, e.y, 48*e.fin(Interp.pow3Out));
  });

  public final static Effect impactWaveBig = new Effect(30, e -> {
    Draw.color(e.color);
    Lines.stroke(6.5f*e.fout());
    Lines.circle(e.x, e.y, 55*e.fin(Interp.pow3Out));
  });

  public final static Effect impactWaveLarge = new Effect(38, e -> {
    Draw.color(e.color);
    Lines.stroke(7.3f*e.fout());
    Lines.circle(e.x, e.y, 80*e.fin(Interp.pow3Out));
  });

  public final static Effect polyParticle = new Effect(150, e -> {//这段代码很特殊，闪光线的代码并不是我写的，这来自一个bug，大概是来自Lines的闭合线问题，意料之外，但效果还不错，就留着了
    randLenVectors(e.id, 1, 24, e.rotation + 180, 20, (x, y) -> {
      int vertices = randomSeed((int) (e.id + x), 3, 6);
      float step = 360f/vertices;

      Fill.polyBegin();
      Lines.beginLine();

      for(int i = 0; i < vertices; i++){
        float radius = randomSeed(e.id + i, 1.5f, 4f)*e.fout(Interp.pow3Out);
        float lerp = e.fin(Interp.pow2Out);
        float rot = randomSeed(e.id + i, -360, 360);
        float off = randomSeed(e.id + i + 1, -step/2, step/2);
        float angle = step*i + rot*lerp + off;
        float dx = trnsx(angle, radius) + x*lerp;
        float dy = trnsy(angle, radius) + y*lerp;

        Fill.polyPoint(e.x + dx, e.y + dy);
        Lines.linePoint(e.x + dx, e.y + dy);
      }

      Draw.z(Layer.bullet - 5f);
      Draw.color(e.color, 0.5f);
      Fill.polyEnd();

      Draw.z(Layer.effect);
      Lines.stroke(0.4f*e.fout(), e.color);
      Lines.endLine(true);
    });
  });

  public final static Effect impactBubbleSmall = new Effect(40, e -> {
    Draw.color(e.color);
    randLenVectors(e.id, 9, 20, (x, y) -> {
      float s = randomSeed((int) (e.id + x), 3f, 6f);
      Fill.circle(e.x + x*e.fin(), e.y + y*e.fin(), s*e.fout());
    });
  });

  public final static Effect impactBubble = new Effect(60, e -> {
    Draw.color(e.color);
    randLenVectors(e.id, 12, 26, (x, y) -> {
      float s = randomSeed((int) (e.id + x), 4f, 8f);
      Fill.circle(e.x + x*e.fin(), e.y + y*e.fin(), s*e.fout());
    });
  });

  public final static Effect impactBubbleBig = new Effect(79, e -> {
    Draw.color(e.color);
    randLenVectors(e.id, 15, 45, (x, y) -> {
      float s = randomSeed((int) (e.id + x), 5f, 10f);
      Fill.circle(e.x + x*e.fin(), e.y + y*e.fin(), s*e.fout());
    });
  });

  public final static Effect crystalConstructed = new Effect(60, e -> {
    Draw.color(e.color);
    Lines.stroke(4*e.fout());

    Draw.z(Layer.effect);
    Lines.square(e.x, e.y, 12*e.fin(), 45);
  });

  public final static Effect hadronReconstruct = new Effect(60, e -> {
    Draw.color(Pal.reactorPurple);
    Lines.stroke(3f*e.fout());

    Draw.z(Layer.effect);
    randLenVectors(e.id, 3, 12, (x, y) -> {
      Lines.square(e.x + x, e.y + y, (14 + randomSeed(e.id + (int) (x*y), -2, 2))*e.fin(), e.fin()* randomSeed(e.id + (int) (x*y), -90, 90));
    });
  });

  public final static Effect polymerConstructed = new Effect(60, e -> {
    Draw.color(Pal.reactorPurple);
    Lines.stroke(6*e.fout());

    Lines.square(e.x, e.y, 30*e.fin());
    Lines.square(e.x, e.y, 30*e.fin(), 45);
  });

  public final static Effect spreadField = new Effect(60, e -> {
    Draw.color(e.color);
    Lines.stroke(8*e.fout());

    Lines.square(e.x, e.y, 38*e.fin(Interp.pow2Out));
    Lines.square(e.x, e.y, 38*e.fin(Interp.pow2Out), 45);
  });

  public final static Effect forceField = new Effect(45, e -> {
    Draw.color(e.color);
    if(e.data instanceof Float f){
      Draw.alpha(f);
    }
    float endRot = ((int) Math.ceil(e.rotation/45) + 1)*45;

    Draw.z(Layer.effect);
    Lines.stroke(lerp(1.5f, 0.4f, e.fin()));
    Lines.square(e.x, e.y, lerp(35, 3, e.fin()), lerp(e.rotation, endRot, e.fin()));
  });

  public final static Effect FEXsmoke = new Effect(80, e -> {
    float move = clamp(e.fin()/0.35f);
    float size = 1 - clamp((e.fin() - 0.65f)/0.35f);

    Draw.z(Layer.effect);
    randLenVectors(e.id, 6, 4f + (float) Functions.lerp(0, 9, 0.1f, move*40), (x, y) -> {
      color(SglDrawConst.fexCrystal, Color.lightGray, clamp(e.fin() + random(-0.1f, 0.1f)));
      Fill.square(e.x + x, e.y + y, 0.2f + size*2f + random(-0.15f, 0.15f), 45);
    });
  });

  public final static Effect shootSmokeMissileSmall = new Effect(130f, 300f, e -> {
    color(e.color);
    alpha(0.5f);
    rand.setSeed(e.id);
    for(int i = 0; i < 18; i++){
      Tmp.v1.trns(e.rotation + 180f + rand.range(19f), rand.random(e.finpow() * 60f)).add(rand.range(2f), rand.range(2f));
      e.scaled(e.lifetime * rand.random(0.2f, 1f), b -> {
        Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, b.fout() * 3.5f + 0.3f);
      });
    }
  });

  public final static Effect impWave = new Effect(10f, e -> {
    Draw.color(Color.white);

    Lines.stroke(e.fout());
    Lines.circle(e.x, e.y, randomSeed(e.id, 8, 10)*e.fin());
  });

  public final static Effect glowParticle = new Effect(45, e -> {
    Draw.color(e.color, Color.white, e.fin());

    randLenVectors(e.id, 1, 3.5f, e.rotation, 5, (x, y) -> {
      Fill.circle(e.x + x*e.fin(Interp.pow2Out), e.y + y*e.fin(Interp.pow2Out), 1.6f*e.fout(Interp.pow2Out));
    });
  });

  public final static Effect freezingBreakDown = new Effect(180, e -> {
    if(!(e.data instanceof Unit u)) return;
    float size = u.hitSize*1.2f;

    float intensity = size/32 - 2.2f;
    float baseLifetime = 25f + intensity*11f;

    e.scaled(baseLifetime, b -> {
      Draw.color();
      b.scaled(5 + intensity*2f, i -> {
        stroke((3.1f + intensity/5f)*i.fout());
        Lines.circle(b.x, b.y, (3f + i.fin()*14f)*intensity);
        Drawf.light(b.x, b.y, i.fin()*14f*2f*intensity, Color.white, 0.9f*b.fout());
      });

      color(SglDrawConst.winter, SglDrawConst.frost, b.fin());
      stroke((2f*b.fout()));

      Draw.z(Layer.effect + 0.001f);
      randLenVectors(b.id + 1, b.finpow() + 0.001f, (int) (8*intensity), 28f*intensity, (x, y, in, out) -> {
        lineAngle(b.x + x, b.y + y, Mathf.angle(x, y), 1f + out*4*(4f + intensity));
        Drawf.light(b.x + x, b.y + y, (out*4*(3f + intensity))*3.5f, Draw.getColor(), 0.8f);
      });
    });

    float rate = e.fout(Interp.pow2In);
    float l = size*rate*1.2f;
    float w = size*rate*0.2f;

    float x = e.x;
    float y = e.y;
    float fout = e.fout();
    float fin = e.fin();
    Drawf.light(x, y, fout*size, SglDrawConst.winter, 0.7f);

    float lerp = e.fin(Interp.pow3Out);
    int id = e.id;

    SglDraw.drawBloomUponFlyUnit(null, n -> {
      Draw.color(SglDrawConst.winter);
      SglDraw.drawLightEdge(x, y, l, w, l, w);
      Lines.stroke(5f* fout);
      Lines.circle(x, y, 55* fout);

      SglDraw.gradientCircle(x, y, size*lerp, -size*lerp*fout, 1);
      Draw.reset();
    });

    Draw.z(Layer.flyingUnit + 1);
    randLenVectors(id, (int) randomSeed(id, size/6, size/3), size/2, size*2, (dx, dy) -> {
      float s = randomSeed((int) (id + dx), size/4, size/2);

      float le = 1 - pow(fin, 4);
      SglDraw.drawCrystal(x + dx*lerp, y + dy*lerp, s, s*le*0.35f, s*le*0.24f, 0, 0, 0.8f*le,
          Layer.effect, Layer.bullet - 1, Time.time*Mathf.randomSeed(id, -3.5f, 3.35f) + randomSeed((long) (id + dx), 360),
          Mathf.angle(dx, dy), Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter);
    });
  });

  public final static Effect crossLightMini = new Effect(22, e -> {
    Draw.color(e.color);
    for(int i: signs){
      SglDraw.drawDiamond(e.x, e.y, 12 + 64*e.fin(Interp.pow3Out), 5*e.fout(Interp.pow3Out), e.rotation + 45 + i*45);
    }
  });

  public final static Effect crossLightSmall = new Effect(26, e -> {
    Draw.color(e.color);
    for(int i: signs){
      SglDraw.drawDiamond(e.x, e.y, 22 + 74*e.fin(Interp.pow3Out), 8*e.fout(Interp.pow3Out), e.rotation + 45 + i*45);
    }
  });

  public final static Effect crossLight = new Effect(30, e -> {
    Draw.color(e.color);
    for(int i: signs){
      SglDraw.drawDiamond(e.x, e.y, 32 + 128*e.fin(Interp.pow3Out), 12*e.fout(Interp.pow3Out), e.rotation + 45 + i*45);
    }
  });

  public final static Effect shootCrossLight = new Effect(120, e -> {
    Draw.color(e.color);

    float l = e.fout(Interp.pow3Out);
    SglDraw.drawLightEdge(e.x, e.y, 140, 5.5f*l, 140, 5.5f*l, e.rotation + 220*e.fin(Interp.pow3Out));
  });

  public final static Effect shootCrossLightLarge = new Effect(140, e -> {
    Draw.color(e.color);

    float l = e.fout(Interp.pow3Out);
    SglDraw.drawLightEdge(e.x, e.y, 240, 12.5f*l, 240, 12.5f*l, e.rotation + 237*e.fin(Interp.pow3Out));
  });

  public final static Effect ploymerGravityField = new Effect(32, e -> {
    Draw.color(e.color);
    if(e.data instanceof NormalCrafter.NormalCrafterBuild b){
      float fout = e.fout();
      float x = e.x, y = e.y, r = e.rotation;

      SglDraw.drawBloomUponFlyUnit(b, bu -> {
        Lines.stroke(2.6f*bu.workEfficiency()*fout);
        Lines.circle(x, y, r*fout);
        Draw.reset();
      });
    }
  });

  public final static Effect auroraCoreCharging = new Effect(80, 100, e -> {
    Draw.color(SglDrawConst.matrixNet);
    stroke(e.fin() * 2f);
    Lines.circle(e.x, e.y, 4f + e.fout() * 100f);

    Fill.circle(e.x, e.y, e.fin() * 10);

    randLenVectors(e.id, 20, 40f * e.fout(), (x, y) -> {
      Fill.circle(e.x + x, e.y + y, e.fin() * 5f);
      Drawf.light(e.x + x, e.y + y, e.fin() * 15f, Pal.heal, 0.7f);
    });

    color();

    Fill.circle(e.x, e.y, e.fin() * 8);
    Drawf.light(e.x, e.y, e.fin() * 16f, Pal.heal, 0.7f);
  }).rotWithParent(true).followParent(true);

  public final static Effect explodeImpWaveMini = impactExplode(16, 36f);
  public final static Effect explodeImpWaveSmall = impactExplode(22, 40f);

  public final static Effect explodeImpWave = impactExplode(32, 50f);

  public final static Effect explodeImpWaveBig = impactExplode(40, 65f);

  public final static Effect explodeImpWaveLarge = impactExplode(60, 95f);

  public final static Effect explodeImpWaveLaserBlase = impactExplode(86, 200f);

  public final static Effect reactorExplode = new MultiEffect(Fx.reactorExplosion, new Effect(180, e -> {
    float size = e.data() instanceof Float ? e.data() : 120;

    float fin1 = clamp(e.fin()/0.1f);
    float fin2 = clamp((e.fin() - 0.1f)/0.3f);

    Draw.color(Pal.reactorPurple);
    Lines.stroke(6*e.fout());
    float radius = size*(1 - pow(e.fout(), 3));
    Lines.circle(e.x, e.y, radius);

    Draw.z(Layer.effect + 10);
    SglDraw.gradientCircle(e.x, e.y, radius-3*e.fout(), -(size/6)*(1 - e.fin(Interp.pow3)), Draw.getColor().cpy().a(0));
    Draw.z(Layer.effect);

    float h, w;
    float rate = e.fin() > 0.1f ? 1 - fin2 : fin1;
    h = size/2*rate;
    w = h/5;

    Lines.stroke(3f*rate);
    Lines.circle(e.x, e.y, h/2);

    Fill.quad(
        e.x + h, e.y,
        e.x, e.y + w,
        e.x - h, e.y,
        e.x, e.y - w
    );
    Fill.quad(
        e.x + w, e.y,
        e.x, e.y + h,
        e.x - w, e.y,
        e.x, e.y - h
    );

    float intensity = size/32 - 2.2f;
    float baseLifetime = 25f + intensity*11f;

    color(Pal.reactorPurple2);
    alpha(0.7f);
    for(int i = 0; i < 4; i++){
      rand.setSeed(e.id*2L + i);
      float lenScl = rand.random(0.4f, 1f);
      int fi = i;
      e.scaled(e.lifetime*lenScl, b -> {
        randLenVectors(b.id + fi - 1, b.fin(Interp.pow10Out), (int) (2.9f*intensity), 22f*intensity, (x, y, in, out) -> {
          float fout = b.fout(Interp.pow5Out)* rand.random(0.5f, 1f);
          float rad = fout*((2f + intensity)*2.35f);

          Fill.circle(b.x + x, b.y + y, rad);
          Drawf.light(b.x + x, b.y + y, rad*2.5f, Pal.reactorPurple, 0.5f);
        });
      });
    }

    e.scaled(baseLifetime, b -> {
      Draw.color();
      b.scaled(5 + intensity*2f, i -> {
        stroke((3.1f + intensity/5f)*i.fout());
        Lines.circle(b.x, b.y, (3f + i.fin()*14f)*intensity);
        Drawf.light(b.x, b.y, i.fin()*14f*2f*intensity, Color.white, 0.9f*b.fout());
      });

      color(Pal.lighterOrange, Pal.reactorPurple, b.fin());
      stroke((2f*b.fout()));

      Draw.z(Layer.effect + 0.001f);
      randLenVectors(b.id + 1, b.finpow() + 0.001f, (int) (8*intensity), 28f*intensity, (x, y, in, out) -> {
        lineAngle(b.x + x, b.y + y, Mathf.angle(x, y), 1f + out*4*(4f + intensity));
        Drawf.light(b.x + x, b.y + y, (out*4*(3f + intensity))*3.5f, Draw.getColor(), 0.8f);
      });
    });
  }));

  public final static Effect weaveTrail = new Effect(12, e -> {
    Draw.color(e.color, Color.white, e.fin());
    SglDraw.drawDiamond(e.x, e.y, 15 + 45*e.fin(), 8*e.fout(), e.rotation + 90);
  });

  public final static Effect steam = new Effect(90, e -> {
    Vec2 motion = e.data() instanceof Vec2 ? e.data() : new Vec2(0, 0);
    float len = motion.len();
    Draw.color(Color.white);
    Draw.alpha(0.75f*e.fout());

    for(int i = 0; i < 5; i++){
      Vec2 curr = motion.cpy().rotate(randomSeed(e.id, -20, 20)).setLength(len*e.finpow());
      Fill.circle(e.x + curr.x, e.y + curr.y, randomSeed(e.id, 3.5f, 5)*(0.3f + 0.7f*e.fslope()));
    }
  });

  public final static Effect steamBreakOut = new Effect(24, e -> {
    float[] data = e.data instanceof float[] ? e.data() : new float[]{18, 24, 0.3f};

    float leng = random(data[0], data[1]);
    for(int i = 0; i < 4; i++){
      if(chanceDelta(data[2]))
        steam.at(e.x, e.y, 0, new Vec2(leng*Geometry.d8(i*2 + 1).x, leng*Geometry.d8(i*2 + 1).y));
    }
  });

  public final static Effect lightCone = new Effect(16, e -> {
    Draw.color(e.color);

    SglDraw.drawDiamond(e.x, e.y, 8, 26 * e.fout(), e.rotation);
  });

  public final static Effect lightConeHit = new Effect(30, e -> {
    Draw.color(e.color);

    float fout = e.fout(Interp.pow2Out);
    float fin = e.fin(Interp.pow2Out);
    randLenVectors(e.id, randomSeed(e.id + 1, 3, 4), 30, e.rotation, 60, (dx, dy) -> {
      Drawf.tri(e.x - dx*fin, e.y - dy*fin, 6f*fout, 6 + 15*fout, Mathf.angle(dx, dy) + 180);
      Drawf.tri(e.x - dx*fin, e.y - dy*fin, 6f*fout, 6f*fout, Mathf.angle(dx, dy));
    });
  });

  public final static Effect matrixDrill = new Effect(45, e -> {
    Draw.color(e.color);

    Lines.stroke(1.8f*e.fout());
    Lines.square(e.x, e.y, 3f + 12*e.fin(), 45);
    Fill.square(e.x, e.y, 3*e.fout(), 45 + 360*e.fin(Interp.pow3Out));
  });

  public final static Effect lightConeTrail = new Effect(20, e -> {
    Draw.color(e.color);

    int i = randomSeed(e.id) > 0.5f? 1: -1;
    float off = randomSeed(e.id, -10, 10);
    float fout = e.fout(Interp.pow2Out);

    float rot = e.rotation + 156f*i + off;
    float dx = Angles.trnsx(rot, 24, 0)*e.fin(Interp.pow2Out);
    float dy = Angles.trnsy(rot, 24, 0)*e.fin(Interp.pow2Out);

    Drawf.tri(e.x + dx, e.y + dy, 8f*fout, 8 + 24*fout, rot);
    Drawf.tri(e.x + dx, e.y + dy, 8f*fout, 8f*fout, rot + 180);
  });

  public final static Effect trailLine = new Effect(24, e -> {
    color(e.color);

    Drawf.tri(e.x, e.y, 2f*e.fout(), 2 + 6*e.fout(), e.rotation);
    Drawf.tri(e.x, e.y, 2f*e.fout(), 8 + 10*e.fout(), e.rotation + 180);
  });

  public final static Effect trailLineLong = new Effect(30, e -> {
    color(e.color);

    Drawf.tri(e.x, e.y, 4*e.fout(), 6 + 8*e.fout(), e.rotation);
    Drawf.tri(e.x, e.y, 4*e.fout(), 10 + 16*e.fout(), e.rotation + 180);
  });

  public final static Effect spreadSparkLarge = new Effect(28, e -> {
    color(Color.white, e.color, e.fin());
    stroke(e.fout()*1.2f + 0.5f);

    randLenVectors(e.id, 20, 10f*e.fin(), 27f, (x, y) -> {
      lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope()*5f + 0.5f);
    });
  });

  public final static Effect circleSparkMini = new Effect(32, e -> {
    color(Color.white, e.color, e.fin());
    stroke(e.fout()*0.8f + 0.2f);

    randLenVectors(e.id, 22, 4f*e.fin(), 12f, (x, y) -> {
      lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope()*2.2f);
    });
  });

  public final static Effect constructSpark = new Effect(24, e -> {
    Color c = e.color;
    float fin = e.fin();
    float fs = e.fslope();
    float ex = e.x, ey = e.y;
    int id = e.id;

    SglDraw.drawBloomUponFlyUnit(() -> {
      color(Color.white, c, fin);
      stroke((1 - fin)*0.8f + 0.2f);

      randLenVectors(id, 22, 4f*fin, 12f, (x, y) -> {
        lineAngle(ex + x, ey + y, Mathf.angle(x, y), fs*2.2f);
      });
      Draw.reset();
    });
  });

  public final static Effect circleSparkLarge = new Effect(65, e -> {
    color(Color.white, e.color, e.fin());
    stroke(e.fout()*1.4f + 0.5f);

    randLenVectors(e.id, 37, 28f*e.fin(), 49f, (x, y) -> {
      lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope()*9f);
    });
  });

  public final static Effect diamondSpark = new Effect(30, e -> {
    color(Color.white, e.color, e.fin());
    stroke(e.fout()*1.2f + 0.5f);

    randLenVectors(e.id, 7, 6f*e.fin(), 20f, (x, y) -> {
      SglDraw.drawDiamond(e.x + x, e.y + y, 10, e.fout(Interp.pow2Out)*4f, Mathf.angle(x, y));
    });
  });

  public final static Effect diamondSparkLarge = new Effect(30, e -> {
    color(Color.white, e.color, e.fin());
    stroke(e.fout()*1.2f + 0.5f);

    randLenVectors(e.id, 9, 8f*e.fin(), 24f, (x, y) -> {
      SglDraw.drawDiamond(e.x + x, e.y + y, 12, e.fout(Interp.pow2Out)*5f, Mathf.angle(x, y));
    });
  });

  public final static Effect railShootRecoil = new Effect(12, e -> {
    Draw.color(e.color);

    randLenVectors(e.id, randomSeed(e.id, 2, 4), 24, e.rotation + 180, 60, (x, y) -> {
      float size = randomSeed((int) (e.id + x), 6, 12);
      float lerp = e.fin(Interp.pow2Out);
      SglDraw.drawDiamond(e.x + x*lerp, e.y + y*lerp, size, size/2f*e.fout(), Mathf.angle(x, y));
    });
  });

  public final static Effect trailParticle = new Effect(95, e -> {
    Draw.color(e.color);

    randLenVectors(e.id, 3, 35, (x, y) -> {
      Fill.circle(e.x + x * e.fin(Interp.pow2In), e.y + y * e.fin(Interp.pow2In), 1.2f * e.fout());
    });
  });

  public final static Effect iceParticle = new Effect(124, e -> {
    Draw.color(e.color);

    int amo = randomSeed(e.id, 2, 5);
    for(int i = 0; i < amo; i++){
      float len = randomSeed(e.id + i*2L, 40)*e.fin();
      float off = randomSeed(e.id + i, -8, 8);
      float x = Angles.trnsx(e.rotation, len) + Angles.trnsx(e.rotation + 90, off);
      float y = Angles.trnsy(e.rotation, len) + Angles.trnsy(e.rotation + 90, off);
      Fill.circle(e.x + x, e.y + y, 0.9f*e.fout(Interp.pow2Out));
    }
  });

  public final static Effect iceExplode = new Effect(128, e -> {
    float rate = e.fout(Interp.pow2In);
    float l = 176*rate;
    float w = 38*rate;

    float x = e.x;
    float y = e.y;
    float fout = e.fout();
    float fin = e.fin();
    Drawf.light(x, y, fout*192, SglDrawConst.winter, 0.7f);

    Draw.z(Layer.flyingUnit + 1);

    float lerp = e.fin(Interp.pow3Out);
    int id = e.id;

    SglDraw.drawBloomUponFlyUnit(null, n -> {
      Draw.color(SglDrawConst.winter);
      SglDraw.drawLightEdge(x, y, l, w, l, w);
      Lines.stroke(5f* fout);
      Lines.circle(x, y, 55* fout);
      Lines.stroke(8f* fout);
      Lines.circle(x, y,  116*lerp);

      randLenVectors(id, randomSeed(id, 16, 22), 128, (dx, dy) -> {
        float size = randomSeed((int) (id + dx), 14, 24);

        SglDraw.drawDiamond(x + dx*lerp, y + dy*lerp, size, size*(1 - pow(fin, 2))*0.35f, Mathf.angle(dx, dy));
      });
      Draw.reset();
    });
  });

  public final static Effect particleSpread = new Effect(125, e -> {
    Draw.color(e.color);

    randLenVectors(e.id, 3, 32, (x, y) -> {
      Fill.circle(e.x + x*e.fin(), e.y + y*e.fin(), 0.9f*e.fout(Interp.pow2Out));
    });
  });

  public final static Effect movingCrystalFrag = new Effect(45, e -> {
    float size = randomSeed(e.id, 4, 6)*e.fout();
    Draw.color(e.color);
    randLenVectors(e.id, 1, 3, 6, e.rotation, 20, (x, y) -> {
      SglDraw.drawDiamond(e.x + x*e.fin(Interp.pow2Out), e.y + y*e.fin(Interp.pow2Out), size*2.5f, size, Angles.angle(x, y));
    });
  });

  public final static Effect crystalFrag = new Effect(26, e -> {
    float size = randomSeed(e.id, 2, 4)*e.fout();
    Draw.color(e.color);
    SglDraw.drawDiamond(e.x, e.y, size*2.5f, size, Mathf.randomSeed(e.id, 0f, 360f));
  });

  public final static Effect crystalFragFex = new Effect(26, e -> {
    float size = randomSeed(e.id, 2, 4)*e.fout();
    Draw.color(SglDrawConst.fexCrystal, 0.7f);
    SglDraw.drawDiamond(e.x, e.y, size*2.5f, size, Mathf.randomSeed(e.id, 0f, 360f));
  });

  public final static Effect iceCrystal = new Effect(120, e -> {
    float size = randomSeed(e.id, 2, 6)*e.fslope();
    Draw.color(e.color);
    float rot = randomSeed(e.id + 1, 360);
    float blingX = Angles.trnsx(rot, size*2), blingY = Angles.trnsy(rot, size*2);
    SglDraw.drawDiamond(e.x, e.y, size*2, size/2, rot);
    e.scaled(45, ec -> {
      SglDraw.drawDiamond(ec.x + blingX, ec.y + blingY, 85*ec.fslope(), 1.2f*ec.fslope(),
          randomSeed(ec.id + 2, 360) + randomSeed(ec.id + 3, -15, 15)*ec.fin());
    });
  });

  public final static Effect shootRail = new Effect(60, e -> {
    e.scaled(12f, b -> {
      Lines.stroke(b.fout()*4f + 0.2f, e.color);
      Lines.circle(b.x, b.y, b.fin()*70f);
      Lines.stroke(b.fout()*2.3f + 0.15f);
      Lines.circle(b.x, b.y, b.fin()*62f);
    });

    float lerp = e.fout(Interp.pow2Out);
    Draw.color(e.color);
    SglDraw.drawLightEdge(e.x, e.y, 64 + 64*lerp, 10*lerp, 60 + 80*lerp, 6*lerp, e.rotation + 90);
  });

  public final static Effect winterShooting = new Effect(60, e -> {
    e.scaled(12f, b -> {
      Lines.stroke(b.fout()*4f + 0.2f, SglDrawConst.winter);
      Lines.circle(b.x, b.y, b.fin()*75f);
    });

    float lerp = e.fout(Interp.pow2Out);
    Draw.color(SglDrawConst.winter);
    SglDraw.drawLightEdge(e.x, e.y, 64 + 64*lerp, 12*lerp, 60 + 80*lerp, 6*lerp, e.rotation + 90);

    float l = e.fin(Interp.pow2Out);
    randLenVectors(e.id, randomSeed(e.id, 8, 16), 48, e.rotation + 180, 60, (x, y) -> {
      float size = randomSeed((int) (e.id + x), 12, 20);
      SglDraw.drawDiamond(e.x + x*l, e.y + y*l, size, size/2f*e.fout(), Mathf.angle(x, y));
    });
  });

  public final static Effect laserBlastWeaveLarge = new Effect(280, 200, e -> {
    float size = 140;

    float fin1 = clamp(e.fin()/0.1f);
    float fin2 = clamp((e.fin() - 0.1f)/0.3f);

    Draw.color(e.color);
    float radius = size*e.fin(Interp.pow4Out);

    Draw.alpha(0.6f);
    SglDraw.gradientCircle(e.x, e.y, radius, -radius*e.fout(Interp.pow2Out), 0);
    Draw.alpha(1);
    Lines.stroke(6*e.fout(Interp.pow2Out));
    Lines.circle(e.x, e.y, radius);

    float h, w;
    float rate = e.fin() > 0.1f ? 1 - fin2 : fin1;
    h = size*1.26f*rate;
    w = h/4;

    Lines.stroke(3f*rate);
    Lines.circle(e.x, e.y, h/2);

    SglDraw.drawLightEdge(e.x, e.y, h, w, h, w);

    rand.setSeed(e.id);
    for (int i = 0; i < 14; i++) {
      float rot = rand.random(0, 360f);
      float wi = rand.random(12, 18);
      float le = rand.random(wi*2f, wi*4f);

      SglDraw.drawTransform(e.x, e.y, radius, 0, rot, (x, y, ro) -> {
        Drawf.tri(x, y, wi*e.fout(), le, ro - 180);
      });
    }

    e.scaled(100, ef -> {
      Angles.randLenVectors(e.id, 9, 45, 164, (x, y) -> {
        float lerp = ef.fin(Interp.pow4Out);
        float si = Mathf.len(x, y)*Mathf.randomSeed((long) (x + y), 0.6f, 0.8f);
        SglDraw.drawDiamond(e.x + x*lerp, e.y + y*lerp, si, si/10*ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90);
      });
    });

    e.scaled(120, ef -> {
      Angles.randLenVectors(e.id*2L, 9, 40, 154, (x, y) -> {
        float lerp = Mathf.clamp((ef.fin(Interp.pow4Out) - 0.2f)/0.8f);
        float si = Mathf.len(x, y)*Mathf.randomSeed((long) (x + y), 0.7f, 0.9f);
        SglDraw.drawDiamond(e.x + x*lerp, e.y + y*lerp, si, si/10*ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90);
      });
    });

    e.scaled(140, ef -> {
      Angles.randLenVectors(e.id*2L, 10, 36, 150, (x, y) -> {
        float lerp = Mathf.clamp((ef.fin(Interp.pow4Out) - 0.4f)/0.6f);
        float si = Mathf.len(x, y)*Mathf.randomSeed((long) (x + y), 0.7f, 0.9f);
        SglDraw.drawDiamond(e.x + x*lerp, e.y + y*lerp, si, si/10*ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90);
      });
    });

    e.scaled(160, ef -> {
      Angles.randLenVectors(e.id*3L, 12, 32, 144, (x, y) -> {
        float lerp = Mathf.clamp((ef.fin(Interp.pow4Out) - 0.5f)/0.5f);
        float si = Mathf.len(x, y)*Mathf.randomSeed((long) (x + y), 0.9f, 1f);
        SglDraw.drawDiamond(e.x + x*lerp, e.y + y*lerp, si, si/10*ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90);
      });

      Lines.stroke(4*ef.fout());
      randLenVectors(e.id*4L, ef.finpow() + 0.001f, 58, size*1.2f, (dx, dy, in, out) -> {
        lineAngle(e.x + dx, e.y + dy, Mathf.angle(dx, dy), 8 + out*64f);
        Drawf.light(e.x + dx, e.y + dy, out*size/2, Draw.getColor(), 0.8f);
      });
    });
  });

  public final static Effect randomLightning = new LightningEffect(){
    final RandomGenerator branch = new RandomGenerator();

    final RandomGenerator generator = new RandomGenerator(){{
      branchChance = 0.15f;
      branchMaker = (vert, str) -> {
        branch.originAngle = vert.angle + random(-90, 90);

        branch.maxLength = 60*str;

        return branch;
      };
    }};

    {
      branch.maxDeflect = 60;
      lifetime = 60;
    }

    @Override
    public void render(EffectContainer e){
      if(e.data == null) return;
      LightningContainer con = e.data();
      Draw.color(e.color);
      Draw.z(Layer.effect);
      if(!Vars.state.isPaused()) con.update();
      con.draw(e.x, e.y);
    }

    @Override
    public LightningContainer createLightning(float x, float y){
      if(!Sgl.config.enableLightning) return null;

      if(!(data instanceof Float)) data = 90f;
      LightningContainer.PoolLightningContainer lightning = LightningContainer.PoolLightningContainer.create(lifetime, 1.4f, 2.5f);

      lightning.lerp = Interp.pow2Out;
      lightning.time = lifetime/2;
      generator.maxLength = random(((float)data)/2, (float)data);
      lightning.create(generator);

      Time.run(lifetime + 5, () -> Pools.free(lightning));
      return lightning;
    }
  };

  public final static Effect spreadLightning = new LightningEffect(){
    final RandomGenerator branch = new RandomGenerator(){{
      maxDeflect = 50;
    }};

    final RandomGenerator generator = new RandomGenerator(){{
      maxDeflect = 60;
      branchChance = 0.15f;
      branchMaker = (vert, str) -> {
        branch.originAngle = vert.angle + random(-90, 90);
        branch.maxLength = 60*str;

        return branch;
      };
    }};

    {
      lifetime = 45;
    }

    @Override
    public void render(EffectContainer e){
      if(e.data == null) return;
      LightningContainer con = e.data();
      Draw.color(e.color);
      Draw.z(Layer.effect);
      Fill.circle(e.x, e.y, 2.5f*e.fout());
      Lines.stroke(1*e.fout());
      Lines.circle(e.x, e.y, 6*e.fout());
      if(!Vars.state.isPaused()) con.update();
      con.draw(e.x, e.y);
    }

    public LightningContainer createLightning(float x, float y){
      if(!Sgl.config.enableLightning) return null;

      LightningContainer.PoolLightningContainer lightning = LightningContainer.PoolLightningContainer.create(lifetime, 1.5f,2.6f);

      lightning.lerp = Interp.pow2Out;
      lightning.time = lifetime/2;
      int amount = random(4, 6);
      for(int i = 0; i < amount; i++){
        generator.maxLength = random(50, 75);
        lightning.create(generator);
      }

      Time.run(lifetime + 5, () -> Pools.free(lightning));
      return lightning;
    }
  };

  public final static Effect shrinkIceParticleSmall = new Effect(120, e -> {
    Draw.color(SglDrawConst.winter);

    randLenVectors(e.id, randomSeed(e.id, 6, 12), 32, (x, y) -> {
      float size = randomSeed((int) (e.id + x), 8, 16);
      float lerp = e.fout(Interp.pow3Out);
      SglDraw.drawDiamond(e.x + x*lerp, e.y + y*lerp, size, size/2f*e.fin(), Mathf.angle(x, y));
    });
  });

  public final static Effect shrinkParticleSmall = shrinkParticle(12, 2, 120, null);

  public final static Effect blingSmall = new Effect(320, e -> {
    Draw.z(Layer.effect);
    Draw.color(e.color);
    float size = randomSeed(e.id, 6, 10);
    size *= e.fout(Interp.pow4In);
    size += absin(Time.time + randomSeed(e.id, 2* pi), 3.5f, 2f);
    float i = e.fin(Interp.pow3Out);
    float dx = randomSeed(e.id, 16), dy = randomSeed(e.id + 1, 16);
    SglDraw.drawLightEdge(e.x + dx*i, e.y + dy*i, size, size*0.15f, size, size*0.15f);
  });

  public final static Effect staticBlingSmall = new Effect(320, e -> {
    Draw.z(Layer.effect);
    Draw.color(e.color);
    float size = randomSeed(e.id, 6, 10);
    size *= e.fout(Interp.pow4In);
    size += absin(Time.time + randomSeed(e.id, 2* pi), 3.5f, 2f);
    SglDraw.drawLightEdge(e.x, e.y, size, size*0.15f, size, size*0.15f);
  });

  public final static Effect lightningBoltWave = new Effect(90, e -> {
    Draw.color(e.color);
    float rate = e.fout(Interp.pow2In);
    float l = 168*rate;
    float w = 36*rate;

    Drawf.light(e.x, e.y, e.fout()*96, e.color, 0.7f);

    float lerp = e.fin(Interp.pow3Out);
    SglDraw.drawLightEdge(e.x, e.y, l, w, l, w);
    Lines.stroke(5f*e.fout());
    Lines.circle(e.x, e.y, 45*e.fout());
    Lines.stroke(8f*e.fout());
    Lines.circle(e.x, e.y,  84*lerp);

    randLenVectors(e.id, randomSeed(e.id, 15, 20), 92, (x, y) -> {
      float size = randomSeed((int) (e.id + x), 18, 26);
      SglDraw.drawDiamond(e.x + x*lerp, e.y + y*lerp, size, size*0.23f*e.fout(), Mathf.angle(x, y));
    });

    e.scaled(45, ef -> {
      Angles.randLenVectors(e.id, 8, 25, 94, (x, y) -> {
        float le = ef.fin(Interp.pow4Out);
        float si = Mathf.len(x, y)*Mathf.randomSeed((long) (x + y), 0.6f, 0.8f);
        SglDraw.drawDiamond(e.x + x*le, e.y + y*le, si, si/10*ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90);
      });
    });

    e.scaled(56, ef -> {
      Angles.randLenVectors(e.id*2L, 8, 20, 82, (x, y) -> {
        float le = Mathf.clamp((ef.fin(Interp.pow4Out) - 0.3f)/0.7f);
        float si = Mathf.len(x, y)*Mathf.randomSeed((long) (x + y), 0.7f, 0.9f);
        SglDraw.drawDiamond(e.x + x*le, e.y + y*le, si, si/10*ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90);
      });
    });

    e.scaled(75, ef -> {
      Angles.randLenVectors(e.id*3L, 9, 14, 69, (x, y) -> {
        float le = Mathf.clamp((ef.fin(Interp.pow4Out) - 0.5f)/0.5f);
        float si = Mathf.len(x, y)*Mathf.randomSeed((long) (x + y), 0.9f, 1f);
        SglDraw.drawDiamond(e.x + x*le, e.y + y*le, si, si/10*ef.fout(Interp.pow2Out), Mathf.angle(x, y) - 90);
      });

      Lines.stroke(3*ef.fout());
      randLenVectors(e.id*4L, ef.finpow() + 0.001f, 48, 102, (dx, dy, in, out) -> {
        lineAngle(e.x + dx, e.y + dy, Mathf.angle(dx, dy), 4 + out*34f);
        Drawf.light(e.x + dx, e.y + dy, out*96, Draw.getColor(), 0.8f);
      });
    });
  });

  public final static Effect neutronWeaveMicro = new Effect(45, e -> {
    Draw.color(e.color);

    Lines.stroke(e.fout());
    Lines.square(e.x, e.y, 2.2f + 6.8f*e.fin(), 45);
    Fill.square(e.x, e.y, 2.2f*e.fout(), 45);
  });

  public final static Effect neutronWeaveMini = new Effect(45, e -> {
    Draw.color(e.color);

    Lines.stroke(1.5f*e.fout());
    Lines.square(e.x, e.y, 3f + 9f*e.fin(), 45);
    Fill.square(e.x, e.y, 3f*e.fout(), 45);
  });

  public final static Effect neutronWeave = new Effect(45, e -> {
    Draw.color(e.color);

    Lines.stroke(1.8f*e.fout());
    Lines.square(e.x, e.y, 4f + 12*e.fin(), 45);
    Fill.square(e.x, e.y, 4*e.fout(), 45);
  });

  public final static Effect neutronWeaveBig = new Effect(45, e -> {
    Draw.color(e.color);

    Lines.stroke(2f*e.fout());
    Lines.square(e.x, e.y, 5f + 18*e.fin(), 45);
    Fill.square(e.x, e.y, 5*e.fout(), 45);
  });

  public final static Effect cellScan = new Effect(45, e -> {
    Draw.color(e.color, 0.6f);

    if(e.data instanceof GameOfLife b){
      Fill.square(e.x, e.y, b.getCellSize() /2*e.fslope(), e.rotation);
    }
  });

  public final static Effect spreadSizedDiamond = new Effect(42, e -> {
    Draw.color(e.color);

    Lines.stroke(12f*e.fout());
    Lines.square(e.x, e.y, e.rotation*e.fin(Interp.pow2Out), 45);
  });

  public final static Effect spreadDiamond = new Effect(35, e -> {
    Draw.color(e.color);

    Lines.stroke(12f*e.fout());
    Lines.square(e.x, e.y, 32*e.fin(Interp.pow2Out), 45);
  });

  public final static Effect spreadDiamondSmall = new Effect(25, e -> {
    Draw.color(e.color);

    Lines.stroke(8f*e.fout());
    Lines.square(e.x, e.y, 18*e.fin(Interp.pow2Out), 45);
  });

  public final static Effect cellDeath = new Effect(45, e -> {
    Draw.color(e.color);

    if(e.data instanceof GameOfLife b){
      Lines.stroke(b.getCellSize() /2*e.fout());

      Lines.square(e.x, e.y, b.getGridSize() *2*e.fin(Interp.pow2Out), e.rotation);
    }
  });

  public final static Effect lightningCont = new Effect(200, e -> {
    if(e.data instanceof LightningContainer cont){
      cont.update();

      Draw.color(e.color);
      cont.draw(e.x, e.y);
    }
  });

  public final static Effect colorLaserCharge = new Effect(38f, e -> {
    color(e.color);

    randLenVectors(e.id, 14, 1f + 20f * e.fout(), e.rotation, 120f, (x, y) -> {
      lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 3f + 1f);
    });
  }),

  colorLaserChargeBegin = new Effect(60f, e -> {
    float margin = 1f - Mathf.curve(e.fin(), 0.9f);
    float fin = Math.min(margin, e.fin());

    color(e.color);
    Fill.circle(e.x, e.y, fin * 3f);

    color();
    Fill.circle(e.x, e.y, fin * 2f);
  });

  public static Effect mirrorShieldBreak = new Effect(40, e -> {
    stroke(1.4f * e.fout());

    float radius = 130;
    if (e.data instanceof MirrorFieldAbility base){
      radius = base.nearRadius;
    }

    rand.setSeed(e.id);
    Angles.randLenVectors(e.id, rand.random((int) radius/5, (int) radius/3), 0, radius, (x, y) -> {
      float offX = rand.random(-16f, 16f)*e.fout(Interp.pow2Out);
      float offY = rand.random(-16f, 16f)*e.fout(Interp.pow2Out);
      color(e.color, e.color.a*0.4f);
      Fill.poly(e.x + x + offX, e.y + y + offY, 6, 10*e.fout(Interp.pow4));
      alpha(1);
      Lines.poly(e.x + x + offX, e.y + y + offY, 6, 10);
    });
  }).followParent(true);

  public static Effect impactExplode(float size, float lifeTime){
    return impactExplode(size, lifeTime, false);
  }

  public static Effect impactExplode(float size, float lifeTime, boolean heightBloom){
    return new Effect(lifeTime, e -> {
      float rate = e.fout(Interp.pow2In);
      float l = size*1.16f*rate;
      float w = size*0.1f*rate;

      float fout = e.fout();
      float fin = e.fin();
      Drawf.light(e.x, e.y, fout *size*1.15f, e.color, 0.7f);

      float x = e.x, y = e.y;
      int id = e.id;
      SglDraw.DrawAcceptor<Bloom> draw = n -> {
        Draw.color(e.color);
        SglDraw.drawLightEdge(x, y, l, w, l, w);
        Lines.stroke(size*0.08f* fout);
        Lines.circle(x, y, size*0.55f* fout);
        Lines.stroke(size*0.175f* fout);
        Lines.circle(x, y, size*1.25f*(1 - pow(fout, 3)));

        randLenVectors(id, 12, 26, (dx, dy) -> {
          float s = randomSeed((int) (id + dx), 4f, 8f);
          Fill.circle(x + dx* fin, y + dy* fin, s*fout);
        });
        Draw.reset();
      };

      if(heightBloom){
        Draw.z(Layer.flyingUnit + 1);
        SglDraw.drawBloomUponFlyUnit(null, draw);
      }
      else draw.draw(null);

      Draw.z(Layer.effect + 0.001f);
      Lines.stroke((size*0.065f* fout));
      randLenVectors(e.id + 1, e.finpow() + 0.001f, (int)(size/2.25f), size*1.2f, (dx, dy, in, out) -> {
        lineAngle(e.x + dx, e.y + dy, Mathf.angle(dx, dy), 3 + out*size*0.7f);
        Drawf.light(e.x + dx, e.y + dy, out*size/2, Draw.getColor(), 0.8f);
      });
    });
  }

  public static Effect shrinkParticle(float radius, float maxSize, float lifeTime, Color color){
    return new Effect(lifeTime, e -> {
      Draw.z(Layer.effect);
      Draw.color(color == null? e.color: color);
      Draw.alpha(1 - clamp((e.fin() - 0.75f)/0.25f));

      randLenVectors(e.id, 2, radius, (x, y) -> {
        float size = randomSeed(e.id, maxSize);

        float le = e.fout(Interp.pow3Out);
        Fill.square(e.x + x*le, e.y + y*le, size*e.fin(),
            lerp(randomSeed(e.id, 360), randomSeed(e.id, 360), e.fin()));
      });
    });
  }

  public static Effect graphiteCloud(float radius, int density){
    return new Effect(360f, e -> {
      Draw.z(Layer.bullet - 5);
      Draw.color(Pal.stoneGray);
      Draw.alpha(0.6f);
      randLenVectors(e.id, density, radius, (x, y) -> {
        float size = randomSeed((int) (e.id+x), 14, 18);
        float i = e.fin(Interp.pow3Out);
        Fill.circle(e.x + x*i, e.y + y*i, size*e.fout(Interp.pow5Out));
      });
      Draw.z(Layer.effect);
      Draw.color(Items.graphite.color);
      randLenVectors(e.id + 1, (int) (density*0.65f), radius, (x, y) -> {
        float size = randomSeed((int) (e.id + x), 7, 10);
        size *= e.fout(Interp.pow4In);
        size += absin(Time.time + randomSeed((int) (e.id + x), 2* pi), 3.5f, 2f);
        float i = e.fin(Interp.pow3Out);
        SglDraw.drawLightEdge(e.x + x*i, e.y + y*i, size, size*0.15f, size, size*0.15f);
      });
    });
  }

  private static abstract class LightningEffect extends Effect{
    protected Object data;

    public void at(Position pos){
      create(pos.getX(), pos.getY(), 0, Color.white, createLightning(pos.getX(), pos.getY()));
    }

    public void at(Position pos, boolean parentize){
      create(pos.getX(), pos.getY(), 0, Color.white, createLightning(pos.getX(), pos.getY()));
    }

    public void at(Position pos, float rotation){
      create(pos.getX(), pos.getY(), rotation, Color.white, createLightning(pos.getX(), pos.getY()));
    }

    public void at(float x, float y){
      create(x, y, 0, Color.white, createLightning(x, y));
    }

    public void at(float x, float y, float rotation){
      create(x, y, rotation, Color.white, createLightning(x, y));
    }

    public void at(float x, float y, float rotation, Color color){
      create(x, y, rotation, color, createLightning(x, y));
    }

    public void at(float x, float y, Color color){
      create(x, y, 0, color, createLightning(x, y));
    }

    public void at(float x, float y, float rotation, Color color, Object data){
      this.data = data;
      create(x, y, rotation, color, createLightning(x, y));
    }

    public abstract LightningContainer createLightning(float x, float y);
  }
}