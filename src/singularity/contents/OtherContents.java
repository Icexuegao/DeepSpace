package singularity.contents;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.struct.ObjectMap;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Puddles;
import mindustry.entities.abilities.Ability;
import mindustry.entities.units.StatusEntry;
import mindustry.game.Team;
import mindustry.gen.Puddle;
import mindustry.gen.Tex;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import singularity.Sgl;
import singularity.graphic.SglDraw;
import singularity.graphic.SglDrawConst;
import singularity.type.AtomSchematic;
import singularity.ui.UIUtils;
import singularity.world.SglFx;
import singularity.world.meta.SglStat;

import static singularity.contents.SglTurrets.crushCrystal;

public class OtherContents implements ContentList{
  private static final Rand rand = new Rand();

  public static AtomSchematic copper_schematic,
  lead_schematic,
  silicon_schematic,
  titanium_schematic,
  thorium_schematic,
  uranium_schematic,
  iridium_schematic;

  public static StatusEffect
  emp_damaged,
  electric_disturb,
  locking,
  spring_coming,
  wild_growth,
  frost,
  frost_freeze,
  meltdown,
  crystallize,
  mirror;

  static final ObjectMap<Unit, float[]> lastHealth = new ObjectMap<>();

  @Override
  public void load(){
    copper_schematic = new AtomSchematic(Items.copper, 14000){{
      request.medium(0.23f);
      request.time(30);
    }};

    lead_schematic = new AtomSchematic(Items.lead, 14000){{
      request.medium(0.26f);
      request.time(30);
    }};

    silicon_schematic = new AtomSchematic(Items.silicon, 18000){{
      request.medium(0.41f);
      request.item(Items.sand, 1);
      request.time(45);
    }};

    emp_damaged = new StatusEffect("emp_damaged"){
      {
        color = Pal.accent;

        speedMultiplier = 0.5f;
        buildSpeedMultiplier = 0.1f;
        reloadMultiplier = 0.6f;
        damageMultiplier = 0.7f;

        init(() -> {
          stats.add(SglStat.effect, t -> {
            t.defaults().left().padLeft(5);
            t.row();
            t.add(Core.bundle.format("data.bulletDeflectAngle", 45 + StatUnit.degrees.localized())).color(Color.lightGray);
            t.row();
            t.add(Core.bundle.get("infos.banedAbilities")).color(Color.lightGray);
            t.row();
            t.add(Core.bundle.get("infos.empDamagedInfo"));
          });
        });
      }

      @Override
      public void update(Unit unit, StatusEntry entry) {
        super.update(unit, entry);

        if (Sgl.empHealth.empDamaged(unit)){
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

    electric_disturb = new StatusEffect("electric_disturb"){
      {
        color = Pal.accent;

        stats.addPercent(Stat.damageMultiplier, 0.8f);
        stats.addPercent(Stat.speedMultiplier, 0.6f);
        stats.addPercent(Stat.reloadMultiplier, 0.75f);
        stats.add(Stat.damage, 12f, StatUnit.perSecond);
        stats.add(SglStat.special, t -> {
          t.row();
          t.add(Core.bundle.format("data.bulletDeflectAngle", 12.4f + StatUnit.degrees.localized()));
          t.row();
          t.add("[lightgray]" + Core.bundle.get("infos.attenuationWithTime") + "[]").padLeft(15);
        });
      }

      @Override
      public void update(Unit unit, StatusEntry entry){
        super.update(unit, entry);
        float scl = Mathf.clamp(entry.time/120);
        unit.shield -= 0.4f*(entry.time/120)*Time.delta;
        unit.damageContinuousPierce(0.2f*(entry.time/120));
        unit.speedMultiplier *= (0.6f + 0.4f*(1 - scl));
        unit.damageMultiplier *= (0.8f + 0.2f*(1 - scl));
        unit.reloadMultiplier *= (0.75f + 0.25f*(1 - scl));
      }
    };

    locking = new StatusEffect("locking"){
      {
        color = Pal.remove;

        init(() -> {
          stats.add(SglStat.damagedMultiplier, Core.bundle.get("infos.lockingMult"));
          stats.add(SglStat.damageProbably, Core.bundle.get("infos.lockingProb"));
        });
      }

      @Override
      public void draw(Unit unit){
        super.draw(unit);
        Draw.z(Layer.overlayUI);
        Draw.color(Pal.gray);
        Fill.square(unit.x, unit.y, 2);
        Draw.color(Pal.remove);
        Fill.square(unit.x, unit.y, 1);
        Drawf.square(unit.x, unit.y, unit.hitSize, Pal.remove);
        Tmp.v1.set(unit.hitSize + 4, 0);
        Tmp.v2.set(unit.hitSize + 12, 0);

        for(int i = 0; i < 4; i++){
          Drawf.line(Pal.remove,
              unit.x + Tmp.v1.x, unit.y + Tmp.v1.y,
              unit.x + Tmp.v2.x, unit.y + Tmp.v2.y
          );
          Tmp.v1.rotate90(1);
          Tmp.v2.rotate90(1);
        }
      }
    };

    spring_coming = new StatusEffect("spring_coming"){{
      color = Pal.heal;
      speedMultiplier = 1.23f;
      reloadMultiplier = 1.16f;
      damageMultiplier = 1.1f;
      damage = -1;
    }};

    wild_growth = new StatusEffect("wild_growth"){{
      color = Tmp.c1.set(Pal.heal).lerp(Color.black, 0.25f).cpy();
      speedMultiplier = 0.3f;
      reloadMultiplier = 1.2f;
      damageMultiplier = 0.6f;
      damage = 1.5f;
    }};

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

    crystallize = new StatusEffect("crystallize"){
      {
        speedMultiplier = 0.34f;
        reloadMultiplier = 0.8f;

        effect = SglFx.crystalFragFex;
        effectChance = 0.1f;

        init(() -> {
          stats.add(SglStat.damagedMultiplier, "115%");
          stats.add(SglStat.effect, t -> {
            t.defaults().left().padLeft(5);
            t.row();
            t.table(a -> {
              a.add(Core.bundle.get("infos.attach"));
              a.image(SglLiquids.phase_FEX_liquid.uiIcon).size(25);
              a.add(SglLiquids.phase_FEX_liquid.localizedName).color(Pal.accent);
            });
            t.row();
            t.add(Core.bundle.format("infos.shots", 3));
            t.row();
            t.table(Tex.underline, b -> {
              UIUtils.buildAmmo(b, crushCrystal);
            }).padLeft(10);
          });
        });
      }

      @Override
      public void update(Unit unit, StatusEntry entry){
        super.update(unit, entry);

        Tile t = unit.tileOn();
        Puddle p;
        if(t != null && (p = Puddles.get(t)) != null && p.liquid == SglLiquids.phase_FEX_liquid && Mathf.chanceDelta(0.02f)){
          for(int i = 0; i < 3; i++){
            float len = Mathf.random(1f, 7f);
            float a = Mathf.range(360f);
            crushCrystal.create(
                null,
                Team.derelict,
                unit.x + Angles.trnsx(a, len),
                unit.y + Angles.trnsy(a, len),
                a,
                Mathf.random(0.2f, 1),
                Mathf.random(0.6f, 1)
            );
          }
        }
      }
    };

    mirror = new StatusEffect("mirror"){
      {
        init(() -> {

        });
      }

      @Override
      public void draw(Unit unit, float time) {
        super.draw(unit, time);
      }

    };
  }

  static {
 /*   UncCore.aspects.addAspect(new EntityAspect<Bullet>(EntityAspect.Group.bullet, r -> true)
        .setEntryTrigger(bullet -> {
          if(bullet.owner instanceof Unit u){
            if(u.hasEffect(electric_disturb)){
              float deflect = 16.4f*Mathf.clamp(u.getDuration(electric_disturb)/120);
              float rot = Mathf.random(-deflect, deflect);
              bullet.rotation(bullet.rotation() + rot);
              Tmp.v1.set(bullet.aimX - bullet.x, bullet.aimY - bullet.y).rotate(rot);
              bullet.aimX = Tmp.v1.x;
              bullet.aimY = Tmp.v1.y;
            }

            if (u.hasEffect(emp_damaged)){
              float rot = Mathf.random(-45, 45);
              bullet.rotation(bullet.rotation() + rot);
              Tmp.v1.set(bullet.aimX - bullet.x, bullet.aimY - bullet.y).rotate(rot);
              bullet.aimX = Tmp.v1.x;
              bullet.aimY = Tmp.v1.y;
            }
          }
        }).setExitTrigger(e -> {}));

    UncCore.aspects.addAspect(new EntityAspect<Unit>(EntityAspect.Group.unit, u -> true)
        .setTrigger(new TriggerEntry<>(EventType.Trigger.update, unit -> {
          if(unit.hasEffect(locking) || unit.hasEffect(crystallize)){

            float[] health = lastHealth.get(unit, () -> new float[]{unit.health});
            if(health[0] != unit.health){
              if(health[0] - 10 > unit.health){
                float damageBase = health[0] - unit.health;

                //locking
                if(unit.hasEffect(locking)){
                  float str = unit.getDuration(locking);
                  if(Mathf.chance(0.1f + str/100)){
                    unit.damage(damageBase*12*str/100f, false);
                  }
                }

                //crystallize
                if(unit.hasEffect(crystallize)) unit.damage(damageBase*0.15f);
              }
              health[0] = unit.health;
            }
          }
        })).setExitTrigger(e -> {}).setEntryTrigger(e -> {}));
 */ }

  static class IconcStatus extends StatusEffect{

    public IconcStatus(String name){
      super(name);
    }

    @Override
    public void init(){
      super.init();


    }
  }
}
