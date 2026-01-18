package singularity.world.unit.types;

import arc.Core;
import arc.func.Func2;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.entities.bullet.PointLaserBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.RegionPart;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Trail;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.weapons.PointDefenseWeapon;
import mindustry.ui.Styles;
import mindustry.world.meta.BlockFlag;
import singularity.Sgl;
import singularity.contents.SglItems;
import singularity.graphic.MathRenderer;
import singularity.graphic.SglDraw;
import singularity.graphic.SglDrawConst;
import singularity.ui.UIUtils;
import singularity.world.SglFx;
import singularity.world.blocks.turrets.MultiTrailBulletType;
import singularity.world.draw.part.CustomPart;
import singularity.world.unit.DataWeapon;
import singularity.world.unit.SglUnitType;
import singularity.world.unit.SglWeapon;
import singularity.world.unit.abilities.MirrorFieldAbility;

public class KaguyaType extends SglUnitType<UnitEntity> {
  private static final Rand rand = new Rand();

  public KaguyaType() {
    super("kaguya");
    requirements(
        Items.silicon, 460,
        Items.phaseFabric, 480,
        Items.surgeAlloy, 450,
        SglItems.aluminium, 520,
        SglItems.aerogel, 480,
        SglItems.crystal_FEX_power, 280,
        SglItems.strengthening_alloy, 340,
        SglItems.iridium, 140,
        SglItems.matrix_alloy, 220
    );

    armor = 20;
    speed = 1.1f;
    accel = 0.06f;
    drag = 0.04f;
    rotateSpeed = 1.5f;
    faceTarget = true;
    flying = true;
    health = 45000;
    lowAltitude = true;
    //canBoost = true;
    //boostMultiplier = 2.5f;
    hitSize = 70;
    targetFlags = BlockFlag.all;
    drawShields = false;

    engineSize = 0;

    abilities.addAll(new MirrorFieldAbility(){{
      strength = 350;
      maxShield = 15800;
      recoverSpeed = 8f;
      cooldown = 6500;
      minAlbedo = 1f;
      maxAlbedo = 1f;
      rotation = false;

      shieldArmor = 22;

      nearRadius = 160;

      Func2<Float, Float, ShieldShape> a = (ofx, ofy) -> new ShieldShape(6, 0, 0, 0, 48) {{
        movement = new ShapeMove() {{
          x = ofx;
          y = ofy;
          rotateSpeed = 0.35f;

          childMoving = new ShapeMove() {{
            rotateSpeed = -0.2f;
          }};
        }};
      }};
      Func2<Float, Float, ShieldShape> b = (ofx, ofy) -> new ShieldShape(5, 0, 0, 0, 48) {{
        movement = new ShapeMove() {{
          x = ofx;
          y = ofy;
          rotateSpeed = -0.25f;

          childMoving = new ShapeMove() {{
            rotateSpeed = 0.15f;
          }};
        }};
      }};

      shapes.addAll(
          new ShieldShape(10, 0, 0, 0, 112) {{
            movement = new ShapeMove() {{
              rotateSpeed = -0.1f;
            }};
          }},
          a.get(90f, 0f),
          a.get(-90f, 0f),
          a.get(0f, 90f),
          a.get(0f, -90f),
          b.get(100f, 0f),
          b.get(-100f, 0f),
          b.get(0f, 100f),
          b.get(0f, -100f)
      );
    }});

    Func2<Float, Float, Weapon> laser = (dx, dy) -> new SglWeapon(Sgl.modName + "-kaguya_laser") {{
      this.x = dx;
      this.y = dy;
      mirror = true;
      reload = 30;
      recoil = 4;
      recoilTime = 30;
      shadow = 4;
      rotate = true;
      layerOffset = 0.1f;
     // shootSound = Sounds.laser;

      shake = 3;

      bullet = new LaserBulletType() {{
        damage = 165f;
        lifetime = 20;
        sideAngle = 90f;
        sideWidth = 1.25f;
        sideLength = 15f;
        width = 16f;
        length = 450f;
        hitEffect = Fx.circleColorSpark;
        shootEffect = Fx.colorSparkBig;
        colors = new Color[]{SglDrawConst.matrixNetDark, SglDrawConst.matrixNet, Color.white};
        hitColor = colors[0];
      }};
    }};

    weapons.addAll(
        laser.get(19.25f, 16f),
        laser.get(13.5f, 33.5f),
        new SglWeapon(Sgl.modName + "-kaguya_cannon") {
          {
            x = 30.5f;
            y = -3.5f;
            mirror = true;

            cooldownTime = 120;
            recoil = 0;
            recoilTime = 120;
            reload = 90;
            shootX = 2;
            shootY = 22;
            rotate = true;
            rotationLimit = 30;
            rotateSpeed = 10;

            shake = 5;

            layerOffset = 0.1f;

            //shootSound = Sounds.shockBlast;

            shoot.shots = 3;
            shoot.shotDelay = 10;

            parts.addAll(
                new RegionPart("_shooter") {{
                  heatColor = SglDrawConst.matrixNet;
                  heatProgress = PartProgress.heat;
                  moveY = -6;
                  progress = PartProgress.recoil;
                }},
                new RegionPart("_body")
            );

            bullet = new MultiTrailBulletType() {
              {
                speed = 6;
                lifetime = 75;
                damage = 180;
                splashDamage = 240;
                splashDamageRadius = 36;

                hitEffect = new MultiEffect(
                    Fx.shockwave,
                    Fx.bigShockwave,
                    SglFx.impactWaveSmall,
                    SglFx.spreadSparkLarge,
                    SglFx.diamondSparkLarge
                );
                despawnHit = true;

                smokeEffect = Fx.shootSmokeSmite;
                shootEffect = SglFx.railShootRecoil;
                hitColor = SglDrawConst.matrixNet;
                trailColor = SglDrawConst.matrixNet;
                hitSize = 8;
                trailLength = 36;
                trailWidth = 4;

                hitShake = 4;
               // hitSound = Sounds.dullExplosion;
                hitSoundVolume = 3.5f;

                trailEffect = SglFx.trailParticle;
                trailChance = 0.5f;

                fragBullet = new EdgeFragBullet();
                fragBullets = 4;
                fragLifeMin = 0.7f;
                fragOnHit = true;
                fragOnAbsorb = true;
              }

              @Override
              public void draw(Bullet b) {
                super.draw(b);
                Drawf.tri(b.x, b.y, 12, 30, b.rotation());
                Drawf.tri(b.x, b.y, 12, 12, b.rotation() + 180);
              }
            };
          }
        },
        new PointDefenseWeapon(Sgl.modName + "-kaguya_point_laser") {{
          x = 30.5f;
          y = -3.5f;
          mirror = true;

          recoil = 0;
          reload = 12;
          targetInterval = 0;
          targetSwitchInterval = 0;

          layerOffset = 0.2f;

          bullet = new BulletType() {{
            damage = 62;
            rangeOverride = 420;
          }};
        }},
        new DataWeapon(Sgl.modName + "-lightedge") {
          {
            x = 0;
            y = -14;
            minWarmup = 0.98f;
            shootWarmupSpeed = 0.02f;
            linearWarmup = false;
            rotate = false;
            shootCone = 10;
            rotateSpeed = 10;
            shootY = 80;
            reload = 30;
            recoilTime = 60;
            recoil = 2;
            recoilPow = 0;
            targetSwitchInterval = 300;
            targetInterval = 0;

            mirror = false;
            continuous = true;
            alwaysContinuous = true;

            Weapon s = this;

            bullet = new PointLaserBulletType() {
              {
                damage = 240;
                damageInterval = 5;
                rangeOverride = 450;
                shootEffect = SglFx.railShootRecoil;
                hitColor = SglDrawConst.matrixNet;
                hitEffect = SglFx.diamondSparkLarge;
                shake = 5;
              }

              @Override
              public float continuousDamage() {
                return damage*(60/damageInterval);
              }

              @Override
              public void update(Bullet b) {
                super.update(b);

                if (b.owner instanceof Unit u) {
                  for (WeaponMount mount : u.mounts) {
                    if (mount.weapon == s) {
                      float bulletX = u.x + Angles.trnsx(u.rotation - 90, x + shootX, y + shootY),
                          bulletY = u.y + Angles.trnsy(u.rotation - 90, x + shootX, y + shootY);

                      b.set(bulletX, bulletY);
                      Tmp.v2.set(mount.aimX - bulletX, mount.aimY - bulletY);
                      float angle = Mathf.clamp(Tmp.v2.angle() - u.rotation, -shootCone, shootCone);
                      Tmp.v2.setAngle(u.rotation).rotate(angle);

                      Tmp.v1.set(b.aimX - bulletX, b.aimY - bulletY).lerpDelta(Tmp.v2, 0.1f).clampLength(80, range);

                      b.aimX = bulletX + Tmp.v1.x;
                      b.aimY = bulletY + Tmp.v1.y;

                      shootEffect.at(bulletX, bulletY, Tmp.v1.angle(), hitColor);
                    }
                  }
                }
              }

              @Override
              public void draw(Bullet b) {
                super.draw(b);
                Draw.draw(Draw.z(), () -> {
                  Draw.color(hitColor);
                  MathRenderer.setDispersion(0.1f);
                  MathRenderer.setThreshold(0.4f, 0.6f);

                  rand.setSeed(b.id);
                  for (int i = 0; i < 3; i++) {
                    MathRenderer.drawSin(b.x, b.y, b.aimX, b.aimY,
                        rand.random(4f, 6f)*b.fslope(),
                        rand.random(360f, 720f),
                        rand.random(360f) - Time.time*rand.random(4f, 7f)
                    );
                  }
                });
              }
            };

            parts.addAll(
                new CustomPart() {{
                  layer = Layer.effect;
                  progress = PartProgress.warmup;
                  draw = (x, y, r, p) -> {
                    Draw.color(SglDrawConst.matrixNet);
                    Fill.circle(x, y, 8);
                    Lines.stroke(1.4f);
                    SglDraw.dashCircle(x, y, 12, Time.time);

                    Draw.draw(Draw.z(), () -> {
                      MathRenderer.setThreshold(0.65f, 0.8f);
                      MathRenderer.setDispersion(1f);
                      MathRenderer.drawCurveCircle(x, y, 15, 2, 6, Time.time);
                      MathRenderer.setDispersion(0.6f);
                      MathRenderer.drawCurveCircle(x, y, 16, 3, 6, -Time.time);
                    });

                    Draw.alpha(0.65f);
                    SglDraw.gradientCircle(x, y, 20, 12, 0);

                    Draw.alpha(1);
                    SglDraw.drawDiamond(x, y, 24 + 18*p, 3 + 3*p, Time.time*1.2f);
                    SglDraw.drawDiamond(x, y, 30 + 18*p, 4 + 4*p, -Time.time*1.2f);
                  };
                }}
            );
          }

          final BulletType subBull = new BulletType(){
            {
              damage = 62;
              speed = 5;
              homingDelay = 30;
              homingPower = 0.1f;
              homingRange = 460;
              lifetime = 150;
              hitSize = 2;
              keepVelocity = false;
              pierceArmor = true;

              hitColor = trailColor = SglDrawConst.matrixNet;
              trailWidth = 1;
              trailLength = 38;

              hitEffect = SglFx.neutronWeaveMicro;
              despawnEffect = SglFx.constructSpark;
            }

            @Override
            public void draw(Bullet b) {
              super.draw(b);
              Draw.color(hitColor);
              Fill.circle(b.x, b.y, hitSize/2);
            }

            @Override
            public void updateHoming(Bullet b) {
              if (b.time > homingDelay){
                float realAimX = b.aimX < 0 ? b.x : b.aimX;
                float realAimY = b.aimY < 0 ? b.y : b.aimY;

                Teamc target;

                if(b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)){
                  target = b.aimTile.build;
                }else{
                  target = Units.closestTarget(b.team, realAimX, realAimY, homingRange,
                      e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                      t -> t != null && collidesGround && !b.hasCollided(t.id));
                }

                if(target != null){
                  Tmp.v1.set(target).sub(b).setLength(homingPower).scl(Time.delta);
                  if (b.vel.len() < speed) b.vel.add(Tmp.v1);
                  b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower * Time.delta * 50f));
                }
              }
            }
          };

          @Override
          public void init(Unit unit, DataWeaponMount mount) {
            Shooter[] shooters = new Shooter[3];
            for (int i = 0; i < shooters.length; i++) {
              shooters[i] = new Shooter();
            }
           // mount.setVar(SglUnits.SHOOTERS, shooters);
          //  mount.setVar(SglUnits.TIMER, new Interval());
          }

          @Override
          public void update(Unit unit, DataWeaponMount mount) {
           /* Shooter[] shooters = mount.getVar(SglUnits.SHOOTERS);
            Interval timer = mount.getVar(SglUnits.TIMER);
            for (Shooter shooter : shooters) {
              Vec2 v = MathTransform.fourierSeries(Time.time, shooter.param).scl(mount.warmup);
              Tmp.v1.set(mount.weapon.x, mount.weapon.y).rotate(unit.rotation - 90);
              shooter.x = Tmp.v1.x + v.x;
              shooter.y = Tmp.v1.y + v.y;
              shooter.trail.update(unit.x + shooter.x, unit.y + shooter.y);
            }

            if (mount.warmup > 0.8f && timer.get(120)){
              for (int i = 0; i < shooters.length; i++) {
                Shooter shooter = shooters[i];
                Time.run(i*40, () -> {
                  SglFx.explodeImpWaveMini.at(unit.x + shooter.x, unit.y + shooter.y, SglDrawConst.matrixNet);
                  for (int l = 0; l < 10; l++) {
                    Time.run(l*4, () -> {
                      Vec2 v = MathTransform.fourierSeries(Time.time, shooter.param).scl(mount.warmup);
                      subBull.create(unit, unit.team, unit.x + shooter.x, unit.y + shooter.y, Angles.angle(v.x, v.y), 0.2f);
                    });
                  }
                });
              }
            }*/
          }

          @Override
          public void addStats(UnitType u, Table t) {
            super.addStats(u, t);
            t.row();

            Table ic = new Table();
            UIUtils.buildAmmo(ic, subBull);
            Collapser coll = new Collapser(ic, true);
            coll.setDuration(0.1f);

            t.table(it -> {
              it.left().defaults().left();

              it.add(Core.bundle.format("bullet.interval", 15));
              it.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
            });
            t.row();
            t.add(coll).padLeft(16);
          }

          @Override
          protected void shoot(Unit unit, DataWeaponMount mount, float shootX, float shootY, float rotation) {
            float mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);

            SglFx.shootRecoilWave.at(shootX, shootY, rotation, SglDrawConst.matrixNet);
            SglFx.impactWave.at(shootX, shootY, SglDrawConst.matrixNet);

            SglFx.impactWave.at(mountX, mountY, SglDrawConst.matrixNet);
            SglFx.crossLight.at(mountX, mountY, SglDrawConst.matrixNet);
          //  Shooter[] shooters = mount.getVar(SglUnits.SHOOTERS);
           /* for (Shooter shooter : shooters) {
              SglFx.impactWaveSmall.at(mountX + shooter.x, mountY + shooter.y);
            }*/
          }

          @Override
          public void draw(Unit unit, DataWeaponMount mount) {
         //   Shooter[] shooters = mount.getVar(SglUnits.SHOOTERS);
            Draw.z(Layer.effect);

            float mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);

            float bulletX = mountX + Angles.trnsx(unit.rotation - 90, shootX, shootY),
                bulletY = mountY + Angles.trnsy(unit.rotation - 90, shootX, shootY);

            Draw.color(SglDrawConst.matrixNet);
            SglDraw.drawDiamond(bulletX, bulletY, mount.recoil*18 + 10, mount.recoil*4, Time.time*1.2f);
            SglDraw.drawDiamond(bulletX, bulletY, mount.recoil*24 + 12, mount.recoil*6, -Time.time);

            float lerp = Math.max(mount.recoil, 0.5f*mount.warmup);
            Fill.circle(bulletX, bulletY, 6*lerp);
            Draw.color(Color.white);
            Fill.circle(bulletX, bulletY, 3*lerp);

           /* for (Shooter shooter : shooters) {
              Draw.color(SglDrawConst.matrixNet);
              shooter.trail.draw(SglDrawConst.matrixNet, 3*mount.warmup);

              float drawx = unit.x + shooter.x, drawy = unit.y + shooter.y;
              Fill.circle(drawx, drawy, 4*mount.warmup);
              Lines.stroke(0.65f*mount.warmup);
              SglDraw.dashCircle(drawx, drawy, 6f*mount.warmup, 4, 180, Time.time);
              SglDraw.drawDiamond(drawx, drawy, 4 + 8*mount.warmup, 3*mount.warmup, Time.time*1.45f);
              SglDraw.drawDiamond(drawx, drawy, 8 + 10*mount.warmup, 3.6f*mount.warmup, -Time.time*1.45f);

              Lines.stroke(3*lerp, SglDrawConst.matrixNet);
              Lines.line(drawx, drawy, bulletX, bulletY);
              Lines.stroke(1.75f*lerp, Color.white);
              Lines.line(drawx, drawy, bulletX, bulletY);

              Draw.alpha(0.5f);
              Lines.line(mountX, mountY, drawx, drawy);
            }*/
          }

          static class Shooter {
            final Trail trail = new Trail(45);
            final float[] param;

            float x, y;

            {
              param = new float[9];
              for (int d = 0; d < 3; d++) {
                param[d*3] = Mathf.random(0.5f, 3f)/(d + 1)*Mathf.randomSign();
                param[d*3 + 1] = Mathf.random(0f, 360f);
                param[d*3 + 2] = Mathf.random(18f, 48f)/((d + 1)*(d + 1));
              }
            }
          }
        }
    );
  }
}
