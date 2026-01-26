package singularity.contents;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.StatusEntry;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.meta.StatUnit;
import singularity.Sgl;
import singularity.graphic.SglDraw;
import singularity.graphic.SglDrawConst;
import singularity.world.SglFx;
import singularity.world.meta.SglStat;

public class OtherContents implements ContentList{
  private static final Rand rand = new Rand();


  public static StatusEffect
  emp_damaged,
  frost,
  frost_freeze,
  meltdown;



  @Override
  public void load(){
    emp_damaged = new StatusEffect("emp_damaged"){
      {
        color = Pal.accent;

        speedMultiplier = 0.5f;
        buildSpeedMultiplier = 0.1f;
        reloadMultiplier = 0.6f;
        damageMultiplier = 0.7f;

        init(() -> stats.add(SglStat.effect, t -> {
          t.defaults().left().padLeft(5);
          t.row();
          t.add(Core.bundle.format("data.bulletDeflectAngle", 45 + StatUnit.degrees.localized())).color(Color.lightGray);
          t.row();
          t.add(Core.bundle.get("infos.banedAbilities")).color(Color.lightGray);
          t.row();
          t.add(Core.bundle.get("infos.empDamagedInfo"));
        }));
      }

      @Override
      public void update(Unit unit, StatusEntry entry) {
        super.update(unit, entry);

        if (/*Sgl.empHealth.empDamaged(unit)*/true){
          if (unit.getDuration(this) <= 60){
            unit.apply(this, 60);
          }
          else{
            unit.speedMultiplier = 0.01f;
            unit.reloadMultiplier = 0;
            unit.buildSpeedMultiplier = 0;
          }

          unit.shield = 0;
          unit.damageContinuousPierce((1 - Sgl.empHealth.healthPresent(unit))*Sgl.empHealth.get(unit).model.empContinuousDamage);

          for (int i = 0; i < unit.abilities.length; i++) {
            if (!(unit.abilities[i] instanceof BanedAbility)){
              BanedAbility baned = Pools.obtain(BanedAbility.class, BanedAbility::new);
              baned.index = i;
              baned.masked = unit.abilities[i];
              unit.abilities[i] = baned;
            }
          }
        }
        else{
          unit.unapply(this);
        }
      }

      static class BanedAbility extends Ability implements Pool.Poolable {
        Ability masked;
        int index;

        @Override
        public void update(Unit unit) {
          if (!unit.hasEffect(emp_damaged)) {
            unit.abilities[index] = masked;
            Pools.free(this);
          }
        }

        @Override
        public void reset() {
          masked = null;
          index = -1;
        }
      }
    };

    frost = new StatusEffect("frost"){
      {
        color = SglDrawConst.frost;
        speedMultiplier = 0.5f;
        reloadMultiplier = 0.8f;
        effect = Fx.freezing;

        init(() -> {
          opposite(StatusEffects.burning, StatusEffects.melting);

          affinity(meltdown, (e, s, t) -> {
            e.damage(s.time);
            s.time -= t;
          });

          stats.add(SglStat.effect, t -> {
            t.add(Core.bundle.get("infos.frostInfo"));
            t.image(frost_freeze.uiIcon).size(25);
            t.add(frost_freeze.localizedName).color(Pal.accent);
          });
        });
      }

      @Override
      public void update(Unit unit, StatusEntry entry){
        super.update(unit, entry);
        if(entry.time >= 30*unit.hitSize + unit.maxHealth/unit.hitSize){
          if(unit.getDuration(frost_freeze) <= 0){
            unit.unapply(this);
            unit.apply(frost_freeze, Math.max(entry.time/2, 180));
          }
        }
      }

      @Override
      public void draw(Unit unit, float time){
        super.draw(unit);
        if(unit.hasEffect(frost_freeze)) return;
        float rate = time/(30*unit.hitSize + unit.maxHealth/unit.hitSize);

        rand.setSeed(unit.id);
        float ro = rand.random(360);
        Draw.color(SglDrawConst.frost);
        Draw.alpha(0.85f*rate);
        Draw.z(Layer.flyingUnit);
        SglDraw.drawDiamond(unit.x, unit.y, unit.hitSize*2.35f*rate, unit.hitSize*2*rate, ro, 0.2f*rate);
      }
    };
    frost_freeze = new StatusEffect("frost_freeze"){
      {
        speedMultiplier = 0f;
        reloadMultiplier = 0f;
        dragMultiplier = 10;

        effect = SglFx.particleSpread;

        init(() -> {
          opposite(StatusEffects.burning, StatusEffects.melting);

          stats.add(SglStat.effect, t -> {
            t.image(frost.uiIcon).size(25);
            t.add(frost.localizedName).color(Pal.accent);
            t.add(Core.bundle.get("infos.frostFreezeInfo"));
          });
        });
      }

      @Override
      public void update(Unit unit, StatusEntry entry){
        super.update(unit, entry);
        if(unit.getDuration(frost) >= 60*unit.hitSize + 3*unit.maxHealth/unit.hitSize){
          Fx.pointShockwave.at(unit.x, unit.y);
          SglFx.freezingBreakDown.at(unit.x, unit.y, 0, unit);
          unit.kill();
          unit.unapply(frost_freeze);
          Effect.shake(8f, 8, unit);
        }
      }

      @Override
      public void draw(Unit unit){
        super.draw(unit);
        rand.setSeed(unit.id);
        float ro = rand.random(360);

        float time = unit.getDuration(frost);
        float rate = time/(60*unit.hitSize + 3*unit.maxHealth/unit.hitSize);
        Draw.color(SglDrawConst.frost, SglDrawConst.winter, rate);
        Draw.alpha(0.85f);
        Draw.z(Layer.flyingUnit);
        SglDraw.drawDiamond(unit.x, unit.y, unit.hitSize*2.35f, unit.hitSize*2, ro, 0.3f);

        Draw.alpha(0.7f);
        int n = (int) (unit.hitSize/8 + rand.random(2, 5));
        for(int i = 0; i < n; i++){
          float v = rand.random(0.75f);
          float re = 1 - Mathf.clamp((1 - rate - v)/(1 - v));

          float off = rand.random(unit.hitSize*0.5f, unit.hitSize);
          float len = rand.random(unit.hitSize)*re;
          float wid = rand.random(unit.hitSize*0.4f, unit.hitSize*0.8f)*re;
          float rot = rand.random(360);

          SglDraw.drawDiamond(unit.x + Angles.trnsx(rot, off), unit.y + Angles.trnsy(rot, off), len, wid, rot, 0.2f);
        }
      }
    };
    meltdown = new StatusEffect("meltdown"){
      {
        damage = 2.2f;
        effect = Fx.melting;

        init(() -> {
          opposite(StatusEffects.freezing, StatusEffects.wet);

          affinity(StatusEffects.tarred, (unit, result, time) -> {
            unit.damagePierce(8f);
            Fx.burning.at(unit.x + Mathf.range(unit.bounds() / 2f), unit.y + Mathf.range(unit.bounds() / 2f));
            result.set(meltdown, 180 + result.time);
          });

          affinity(frost, (e, s, t) -> {
            e.damage(t);
            s.time -= t;
          });

          trans(frost_freeze, (e, s, t) -> {
            s.time -= t;
            e.apply(StatusEffects.blasted);
            e.damage(Math.max(e.getDuration(frost_freeze), t)/2f);
          });

          stats.add(SglStat.exShieldDamage, Core.bundle.get("infos.meltdownDamage"));
        });
      }

      @Override
      public void update(Unit unit,StatusEntry entry) {
        super.update(unit, entry);
        if (unit.shield > 0){
          unit.shieldAlpha = 1;
          unit.shield -= Time.delta*entry.time/6;
        }
      }

      @Override
      public void draw(Unit unit, float time){
        super.draw(unit, time);

        SglDraw.drawBloomUponFlyUnit(unit, u -> {
          float rate = Mathf.clamp(90/(time/30));
          Lines.stroke(2.2f*rate, Pal.lighterOrange);
          Draw.alpha(rate*0.7f);
          Lines.circle(u.x, u.y, u.hitSize/2 + rate*u.hitSize/2);

          rand.setSeed(unit.id);

          for(int i = 0; i < 8; i++){
            SglDraw.drawTransform(u.x, u.y, u.hitSize/2 + rate*u.hitSize/2, 0, Time.time + rand.random(360f), (x, y, r) -> {
              float len = rand.random(u.hitSize/4, u.hitSize/1.5f);
              SglDraw.drawDiamond(x, y, len, len*0.135f, r);
            });
          }
          Draw.reset();
        });
      }
    };



  }


}
