package singularity.world.unit;

import arc.Core;
import arc.func.Boolf;
import arc.func.Cons2;
import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import ice.world.IceBulletHandler;
import mindustry.entities.Effect;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.pattern.ShootPattern;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import singularity.ui.UIUtils;

public class RelatedWeapon extends DataWeapon {
  public static final RelatedAlt
      isFlying = new DefaultAlt(Core.bundle.get("infos.wenUnitIsFlying"), (u)->false),
      none = new DefaultAlt("", u -> false);

  public ShootPattern alternativeShoot;
  public BulletType alternativeBullet;
  public RelatedAlt useAlternative = none;

  public Cons2<BulletType, Table> customAltDisplay;
  public boolean overrideAlt;

  public RelatedWeapon() {
  }

  public RelatedWeapon(String name) {
    super(name);
  }

  @Override
  public void addStats(UnitType u, Table t) {
    if (alternativeBullet != null) {
      if (inaccuracy > 0) {
        t.row();
        t.add("[lightgray]" + Stat.inaccuracy.localized() + ": [white]" + (int) inaccuracy + " " + StatUnit.degrees.localized());
      }

      t.row();
      t.add(Core.bundle.get("misc.preferred")).color(Pal.accent);
      t.row();
      t.table(ta -> {
        ta.left().defaults().left().fill();
        if (!alwaysContinuous && reload > 0) {
          ta.add("[lightgray]" + Stat.reload.localized() + ": " + (mirror ? "2x " : "") + "[white]" + Strings.autoFixed(60f/reload*shoot.shots, 2) + " " + StatUnit.perSecond.localized());
        }
        if (!override) {
          UIUtils.buildAmmo(ta, bullet);
        }
        if (customDisplay != null){
          customDisplay.get(bullet, ta);
        }
      }).padLeft(16);

      t.row();
      t.image().color(Color.lightGray).height(3).growX().pad(0).padTop(3).padBottom(3).colspan(2);
      t.row();

      t.add(useAlternative.getInfo() == null? Core.bundle.get("misc.alternative"): useAlternative.getInfo()).color(Pal.accent);
      t.row();
      t.table(ta -> {
        ta.left().defaults().left().fill();
        if (!alwaysContinuous && reload > 0) {
          ta.row();
          ta.add("[lightgray]" + Stat.reload.localized() + ": " + (mirror ? "2x " : "") + "[white]" + Strings.autoFixed(60f/reload*(alternativeShoot == null ? shoot.shots : alternativeShoot.shots), 2) + " " + StatUnit.perSecond.localized());
        }
        if (!overrideAlt) {
          UIUtils.buildAmmo(ta, alternativeBullet);
        }
        if (customAltDisplay != null){
          customDisplay.get(alternativeBullet, ta);
        }
      }).padLeft(16);

      t.row();
      t.image().color(Color.lightGray).height(3).growX().pad(0).padTop(3).padBottom(3).colspan(2);
    }
    else super.addStats(u, t);
  }

  @Override
  protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation){
    unit.apply(shootStatus, shootStatusDuration);

    ShootPattern shoot = alternativeShoot != null && useAlternative.alt(unit)? alternativeShoot: this.shoot;

    if(shoot.firstShotDelay > 0){
      mount.charging = true;
      chargeSound.at(shootX, shootY, Mathf.random(soundPitchMin, soundPitchMax));
      bullet.chargeEffect.at(shootX, shootY, rotation, bullet.keepVelocity || parentizeEffects ? unit : null);
    }

    var a=new IceBulletHandler(){
      @Override
      public void shoot(float xOffset, float yOffset, float angle, float delay, Mover mover) {
        mount.totalShots++;
        if(delay > 0f){
          Time.run(delay, () -> bullet(unit, mount, xOffset, yOffset, angle, mover));
        }else{
          bullet(unit, mount, xOffset, yOffset, angle, mover);
        }
      }
    };
    shoot.shoot(mount.totalShots,a);

    if(mount instanceof DataWeaponMount m){
      shoot(unit, m, shootX, shootY, rotation);
    }
  }

  @Override
  protected void bullet(Unit unit, WeaponMount mount, float xOffset, float yOffset, float angleOffset, Mover mover){
    if(!unit.isAdded()) return;

    mount.charging = false;
    float
        xSpread = Mathf.range(xRand),
        weaponRotation = unit.rotation - 90 + (rotate ? mount.rotation : baseRotation),
        mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
        mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y),
        bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX + xOffset + xSpread, this.shootY + yOffset),
        bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX + xOffset + xSpread, this.shootY + yOffset),
        shootAngle = bulletRotation(unit, mount, bulletX, bulletY) + angleOffset,
        lifeScl = bullet.scaleLife ? Mathf.clamp(Mathf.dst(bulletX, bulletY, mount.aimX, mount.aimY) / bullet.range) : 1f,
        angle = angleOffset + shootAngle + Mathf.range(inaccuracy + bullet.inaccuracy);

    mount.bullet = (alternativeBullet != null && useAlternative.alt(unit)? alternativeBullet: bullet)
        .create(unit, unit.team, bulletX, bulletY, angle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd), lifeScl, null, mover, mount.aimX, mount.aimY);
    handleBullet(unit, mount, mount.bullet);

    if(!continuous){
      shootSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));
    }

    ejectEffect.at(mountX, mountY, angle * Mathf.sign(this.x));
    mount.bullet.type.shootEffect.at(bulletX, bulletY, angle, bullet.hitColor, unit);
    mount.bullet.type.smokeEffect.at(bulletX, bulletY, angle, bullet.hitColor, unit);

    unit.vel.add(Tmp.v1.trns(shootAngle + 180f, bullet.recoil));
    Effect.shake(shake, shake, bulletX, bulletY);
    mount.recoil = 1f;
    mount.heat = 1f;
  }

  public interface RelatedAlt{
    boolean alt(Unit unit);
    String getInfo();
  }

  private static class DefaultAlt implements RelatedAlt{
    public String info;
    public Boolf<Unit> boolf;

    public DefaultAlt(String info, Boolf<Unit> boolf){
      this.info = info;
      this.boolf = boolf;
    }

    @Override
    public boolean alt(Unit unit) {
      return boolf.get(unit);
    }

    @Override
    public String getInfo() {
      return info;
    }
  }
}
