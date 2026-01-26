//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package singularity.world.blocks.turrets;

import mindustry.Vars;
import mindustry.ai.types.MissileAI;
import mindustry.ctype.Content;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.units.UnitController;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.world.blocks.ControlBlock;
import universecore.util.handler.ObjectHandler;

public class WarpedBulletType<$Type extends BulletType> extends BulletType {
  private final $Type inst;

  public WarpedBulletType($Type inst) {
    this.inst = ($Type) inst.copy();
    ObjectHandler.copyField(inst, this);
  }

  public void init() {
    super.init();
    ObjectHandler.copyFieldAsBlack(this, this.inst, "inst");
  }

  public String toString() {
    return super.toString();
  }

  public Bullet create(Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY) {
    if (this.spawnUnit != null) {
      if (!Vars.net.client()) {
        Unit spawned = this.spawnUnit.create(team);
        spawned.set(x, y);
        spawned.rotation = angle;
        if (this.spawnUnit.missileAccelTime <= 0.0F) {
          spawned.vel.trns(angle, this.spawnUnit.speed);
        }

        UnitController var15 = spawned.controller();
        if (var15 instanceof MissileAI ai) {
          if (owner instanceof Unit unit) {
            ai.shooter = unit;
          }

          if (owner instanceof ControlBlock control) {
            ai.shooter = control.unit();
          }
        }

        spawned.add();
      }

      return null;
    } else {
      Bullet bullet = Bullet.create();
      bullet.type = this;
      bullet.owner = owner;
      bullet.team = team;
      bullet.time = 0.0F;
      bullet.originX = x;
      bullet.originY = y;
      bullet.aimTile = Vars.world.tileWorld(aimX, aimY);
      bullet.aimX = aimX;
      bullet.aimY = aimY;
      bullet.initVel(angle, this.speed * velocityScl);
      bullet.set(x, y);
      bullet.lifetime = this.lifetime * lifetimeScl;
      bullet.data = data;
      bullet.hitSize = this.hitSize;
      bullet.mover = mover;
      bullet.damage = (damage < 0.0F ? this.damage : damage) * bullet.damageMultiplier();
      if (bullet.trail != null) {
        bullet.trail.clear();
      }

      bullet.add();
      if (this.keepVelocity && owner instanceof Velc) {
        Velc v = (Velc)owner;
        bullet.vel.add(v.vel());
      }

      return bullet;
    }
  }

  public void drawTrail(Bullet b) {
    this.inst.drawTrail(b);
  }

  public void loadIcon() {
    this.inst.loadIcon();
  }

  public void updateBulletInterval(Bullet b) {
    this.inst.updateBulletInterval(b);
  }

  public void createIncend(Bullet b, float x, float y) {
    this.inst.createIncend(b, x, y);
  }

  public void updateTrail(Bullet b) {
    this.inst.updateTrail(b);
  }

  public void update(Bullet b) {
    this.inst.update(b);
  }

  public void postInit() {
    this.inst.postInit();
  }

  public int compareTo(Content c) {
    return this.inst.compareTo(c);
  }

  public float damageMultiplier(Bullet b) {
    return this.inst.damageMultiplier(b);
  }

  public boolean hasErrored() {
    return this.inst.hasErrored();
  }

  public void createSplashDamage(Bullet b, float x, float y) {
    this.inst.createSplashDamage(b, x, y);
  }

  public void hit(Bullet b, float x, float y) {
    this.inst.hit(b, x, y);
  }

  public void hit(Bullet b) {
    this.inst.hit(b);
  }

  public void load() {
    this.inst.load();
  }

  public void despawned(Bullet b) {
    this.inst.despawned(b);
  }

  public void createUnits(Bullet b, float x, float y) {
    this.inst.createUnits(b, x, y);
  }

  public void createFrags(Bullet b, float x, float y) {
    this.inst.createFrags(b, x, y);
  }

  public Bullet create(Entityc owner, Team team, float x, float y, float angle) {
    return this.inst.create(owner, team, x, y, angle);
  }

  public Bullet create(Teamc owner, float x, float y, float angle) {
    return this.inst.create(owner, x, y, angle);
  }

  public Bullet create(Entityc owner, Team team, float x, float y, float angle, float velocityScl, float lifetimeScl, Mover mover) {
    return this.inst.create(owner, team, x, y, angle, velocityScl, lifetimeScl, mover);
  }

  public Bullet create(Entityc owner, Team team, float x, float y, float angle, float velocityScl, float lifetimeScl) {
    return this.inst.create(owner, team, x, y, angle, velocityScl, lifetimeScl);
  }

  public Bullet create(Bullet parent, float x, float y, float angle) {
    return this.inst.create(parent, x, y, angle);
  }

  public Bullet create(Bullet parent, float x, float y, float angle, float velocityScl, float lifeScale) {
    return this.inst.create(parent, x, y, angle, velocityScl, lifeScale);
  }

  public Bullet create(Bullet parent, float x, float y, float angle, float velocityScl) {
    return this.inst.create(parent, x, y, angle, velocityScl);
  }

  public Bullet create(Entityc owner, Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY, Teamc target) {
    return this.inst.create(owner, shooter, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target);
  }

  public Bullet create(Entityc owner, Team team, float x, float y, float angle, float velocityScl) {
    return this.inst.create(owner, team, x, y, angle, velocityScl);
  }

  public Bullet create(Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover) {
    return this.inst.create(owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover);
  }

  public Bullet create(Entityc owner, Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY) {
    return this.inst.create(owner, shooter, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY);
  }

  public Bullet create(Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data) {
    return this.inst.create(owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data);
  }

  public float buildingDamage(Bullet b) {
    return this.inst.buildingDamage(b);
  }

  public BulletType copy() {
    return this.inst.copy();
  }

  public void drawLight(Bullet b) {
    this.inst.drawLight(b);
  }

  public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
    this.inst.hitTile(b, build, x, y, initialHealth, direct);
  }

  public void afterPatch() {
    this.inst.afterPatch();
  }

  public void drawParts(Bullet b) {
    this.inst.drawParts(b);
  }

  public void init(Bullet b) {
    this.inst.init(b);
  }

  public void createPuddles(Bullet b, float x, float y) {
    this.inst.createPuddles(b, x, y);
  }

  public float continuousDamage() {
    return this.inst.continuousDamage();
  }

  public void updateHoming(Bullet b) {
    this.inst.updateHoming(b);
  }

  public boolean heals() {
    return this.inst.heals();
  }

  public boolean isVanilla() {
    return this.inst.isVanilla();
  }

  public void hitEntity(Bullet b, Hitboxc entity, float health) {
    this.inst.hitEntity(b, entity, health);
  }

  public void createNet(Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl) {
    this.inst.createNet(team, x, y, angle, damage, velocityScl, lifetimeScl);
  }

  public void draw(Bullet b) {
    this.inst.draw(b);
  }

  public float estimateDPS() {
    return this.inst.estimateDPS();
  }

  public float shieldDamage(Bullet b) {
    return this.inst.shieldDamage(b);
  }

  public boolean testCollision(Bullet bullet, Building tile) {
    return this.inst.testCollision(bullet, tile);
  }

  public boolean isModded() {
    return this.inst.isModded();
  }

  public void updateWeaving(Bullet b) {
    this.inst.updateWeaving(b);
  }

  public void removed(Bullet b) {
    this.inst.removed(b);
  }

  public void handlePierce(Bullet b, float initialHealth, float x, float y) {
    this.inst.handlePierce(b, initialHealth, x, y);
  }

  public void updateTrailEffects(Bullet b) {
    this.inst.updateTrailEffects(b);
  }
}
