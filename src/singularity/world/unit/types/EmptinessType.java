package singularity.world.unit.types;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.UnitSorts;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.LightningBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.meta.BlockFlag;
import singularity.Sgl;
import singularity.contents.SglItems;
import singularity.contents.SglTurrets;
import singularity.graphic.MathRenderer;
import singularity.graphic.SglDraw;
import singularity.graphic.SglDrawConst;
import singularity.ui.UIUtils;
import singularity.util.MathTransform;
import singularity.world.SglFx;
import singularity.world.SglUnitSorts;
import singularity.world.blocks.turrets.EmpBulletType;
import singularity.world.blocks.turrets.LightLaserBulletType;
import singularity.world.blocks.turrets.MultiTrailBulletType;
import singularity.world.draw.part.CustomPart;
import singularity.world.unit.DataWeapon;
import singularity.world.unit.SglUnitType;
import singularity.world.unit.SglWeapon;
import singularity.world.unit.abilities.MirrorArmorAbility;
import universecore.world.lightnings.LightningContainer;
import universecore.world.lightnings.generator.RandomGenerator;
import universecore.world.lightnings.generator.VectorLightningGenerator;

import static mindustry.Vars.headless;
import static mindustry.Vars.world;

public class EmptinessType extends SglUnitType<UnitEntity> {
  private static final Rand rand = new Rand();

  public EmptinessType() {
    super("emptinessunc");
    requirements(
        Items.phaseFabric, 200,
        Items.surgeAlloy, 280,
        SglItems.aerogel, 400,
        SglItems.crystal_FEX_power, 300,
        SglItems.strengthening_alloy, 560,
        SglItems.iridium, 380,
        SglItems.matrix_alloy, 420,
        SglItems.degenerate_neutron_polymer, 420,
        SglItems.anti_metter, 280
    );

    armor = 9;
    speed = 0.8f;
    accel = 0.065f;
    drag = 0.05f;
    rotateSpeed = 0.8f;
    faceTarget = true;
    health = 82500;
    lowAltitude = true;
    flying = true;
    hitSize = 85;
    targetFlags = BlockFlag.allLogic;
    drawShields = false;

    engineSize = 0;

    abilities.addAll(new MirrorArmorAbility(){{
      strength = 240;
      maxShield = 8200;
      recoverSpeed = 3f;
      cooldown = 5500;
      minAlbedo = 0.5f;
      maxAlbedo = 0.8f;

      shieldArmor = 10;
    }});

    setEnginesMirror(

    );

    class MayflyWeapon extends DataWeapon {
      float delay;

      final BulletType subBullet = new BulletType() {
        {
          damage = 80;
          splashDamage = 120;
          splashDamageRadius = 24;
          speed = 3;

          hitShake = 5;

          rangeOverride = 550;

          fragBullet = new LightningBulletType() {{
            lightningLength = 16;
            lightningLengthRand = 8;
            damage = 24;
          }};
          fragBullets = 3;

          hitSize = 3;
          lifetime = 90;
          homingDelay = 20;
          despawnHit = true;
          hitEffect = new MultiEffect(
              SglFx.explodeImpWave,
              SglFx.diamondSpark
          );
          hitColor = SglDrawConst.matrixNet;

          homingRange = 620;
          homingPower = 0.05f;

          trailColor = SglDrawConst.matrixNet;
          trailLength = 20;
          trailWidth = 2.4f;
          trailEffect = SglFx.trailParticle;
          trailChance = 0.16f;
        }

        @Override
        public void draw(Bullet b) {
          drawTrail(b);
          Draw.color(hitColor);
          Fill.circle(b.x, b.y, 6f);
          Draw.color(Color.black);
          Fill.circle(b.x, b.y, 3f);
        }

        @Override
        public void updateHoming(Bullet b) {
          if (b.time < homingDelay) return;

          Posc target = Units.closestTarget(b.team, b.x, b.y, homingRange,
              e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
              t -> t != null && collidesGround && !b.hasCollided(t.id));

          if (target != null) {
            b.vel.lerpDelta(Tmp.v1.set(target.x() - b.x, target.y() - b.y).setLength(10), homingPower);
          }
        }
      };

      private MayflyWeapon() {
        super(Sgl.modName + "-emptiness_mayfly");
        mirror = true;
        alternate = false;

        rotate = false;
        shootCone = 180;
        reload = 60;
        shootWarmupSpeed = 0.03f;
        linearWarmup = false;
        minWarmup = 0.9f;

        //shootSound = Sounds.lasershoot;

        bullet = new BulletType() {
          {
            damage = 320;

            pierceCap = 3;
            pierceBuilding = true;

            fragBullets = 1;
            fragRandomSpread = 0;
            fragAngle = 0;
            fragBullet = new LightLaserBulletType() {
              {
                length = 140;
                damage = 160;
                empDamage = 41;
              }

              @Override
              public void init(Bullet b, LightningContainer c) {
                Teamc target = Units.closestTarget(b.team, b.x, b.y, range,
                    e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                    t -> t != null && collidesGround && !b.hasCollided(t.id));

                if (target != null) {
                  b.rotation(b.angleTo(target));
                }

                super.init(b, c);
              }
            };

            hitShake = 4;

            shootEffect = new MultiEffect(
                SglFx.impactBubbleSmall,
                Fx.colorSparkBig
            );

            hitColor = trailColor = SglDrawConst.matrixNet;
            hitEffect = SglFx.diamondSparkLarge;

            trailEffect = new MultiEffect(SglFx.movingCrystalFrag);
            trailChance = 0.4f;

            despawnHit = true;

            speed = 10;
            lifetime = 120;
            homingDelay = 30;
            homingPower = 0.15f;
            rangeOverride = 500;
            homingRange = 600;

            trailLength = 28;
            trailWidth = 4;
          }

          @Override
          public void draw(Bullet b) {
            super.draw(b);

            float delay = 1 - Mathf.pow(1 - Mathf.clamp(b.time/homingDelay), 2);

            Draw.color(SglDrawConst.matrixNet);
            Fill.circle(b.x, b.y, 6 + 2*delay);
            SglDraw.drawDiamond(b.x, b.y, 24, 8*delay, b.rotation());

            SglDraw.drawTransform(b.x, b.y, 4*delay, 0, b.rotation(), (x, y, r) -> {
              SglDraw.gapTri(x, y, 12*delay, 16 + 16*delay, 14, r);
            });

            Draw.color(Color.black);
            Fill.circle(b.x, b.y, 5*delay);
          }

          @Override
          public void updateHoming(Bullet b) {
            if (b.time < homingDelay) {
              b.vel.lerpDelta(0, 0, 0.06f);
            }

            if (Mathf.chanceDelta(0.3f*b.vel.len()/speed)) {
              Fx.colorSparkBig.at(b.x, b.y, b.rotation(), b.type.hitColor);
            }

            if (b.time >= homingDelay) {
              float realAimX = b.aimX < 0 ? b.x : b.aimX;
              float realAimY = b.aimY < 0 ? b.y : b.aimY;

              Teamc target;
              if (b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && !b.hasCollided(b.aimTile.build.id)) {
                target = b.aimTile.build;
              } else {
                target = Units.closestTarget(b.team, realAimX, realAimY, homingRange,
                    e -> e != null && !b.hasCollided(e.id),
                    t -> t != null && !b.hasCollided(t.id));
              }

              if (target != null) {
                float v = Mathf.lerpDelta(b.vel.len(), speed, 0.08f);
                b.vel.setLength(v);
                b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower*(v/speed)*Time.delta*50f));
              } else {
                b.vel.lerpDelta(0, 0, 0.06f);
              }

              if (b.vel.len() >= speed*0.8f) {
                if (b.timer(3, 3)) SglFx.weaveTrail.at(b.x, b.y, b.rotation(), hitColor);
              }
            }
          }
        };
      }

      @Override
      public void addStats(UnitType u, Table t) {
        super.addStats(u, t);

        Table ic = new Table();
        UIUtils.buildAmmo(ic, subBullet);
        Collapser coll = new Collapser(ic, true);
        coll.setDuration(0.1f);

        t.table(ft -> {
          ft.left().defaults().left();
          ft.add(Core.bundle.format("infos.shots", 2));
          ft.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
        });
        t.row();
        t.add(coll).padLeft(16);
      }

      @Override
      public void init(Unit unit, DataWeaponMount mount) {
        super.init(unit, mount);

        MayflyStatus status = new MayflyStatus();
        status.x = unit.x + Angles.trnsx(unit.rotation() - 90, mount.weapon.x, mount.weapon.y);
        status.y = unit.y + Angles.trnsy(unit.rotation() - 90, mount.weapon.x, mount.weapon.y);

        status.rot.set(1, 0).setAngle(unit.rotation() + mount.rotation);

       // mount.setVar(SglUnits.STATUS, status);
      //  mount.setVar(SglUnits.PHASE, Mathf.random(0, 360f));
      }

      @Override
      public void update(Unit unit, DataWeaponMount mount) {
        super.update(unit, mount);

      /*  if (mount.getVar(SglUnits.STATUS) instanceof MayflyStatus stat) {
          stat.update(unit, mount);
        }*/
      }

      @Override
      public void draw(Unit unit, WeaponMount mount) {
      /*  if (mount instanceof DataWeaponMount m && m.getVar(SglUnits.STATUS) instanceof MayflyStatus stat) {
          stat.draw(unit, m);
        }*/
      }

      @Override
      protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation) {
        if (mount instanceof DataWeaponMount m /* && m.getVar(SglUnits.STATUS) instanceof MayflyStatus stat*/) {
        /*  Time.run(delay, () -> {
            bullet.create(unit, stat.x, stat.y, stat.rot.angle());
            bullet.shootEffect.at(stat.x, stat.y, stat.rot.angle(), bullet.hitColor);

            Time.run(12, () -> {
              for (int sign : Mathf.signs) {
                subBullet.create(unit, stat.x, stat.y, stat.rot.angle() + 25*sign);
              }
            });
          }*/;
        }
      }

      class MayflyStatus {
        float x, y;
        final Vec2 vel = new Vec2(), rot = new Vec2(), tmp1 = new Vec2(), tmp2 = new Vec2();
        final Trail trail1 = new Trail(20), trail2 = new Trail(20);
        final Trail trail = new Trail(28);
        final float off;

        final float[] farg1 = new float[9];
        final float[] farg2 = new float[9];

        {
          off = Mathf.random(0f, 360f);
          for (int d = 0; d < 3; d++) {
            farg1[d*3] = Mathf.random(0.5f, 3f)/(d + 1)*Mathf.randomSign();
            farg1[d*3 + 1] = Mathf.random(0f, 360f);
            farg1[d*3 + 2] = Mathf.random(8f, 16f)/((d + 1)*(d + 1));
          }
          for (int d = 0; d < 3; d++) {
            farg2[d*3] = Mathf.random(0.5f, 3f)/(d + 1)*Mathf.randomSign();
            farg2[d*3 + 1] = Mathf.random(0f, 360f);
            farg2[d*3 + 2] = Mathf.random(8f, 16f)/((d + 1)*(d + 1));
          }
        }

        public void update(Unit unit, DataWeaponMount mount) {
          float movX = Angles.trnsx(mount.rotation, 0, 14)*mount.warmup;
          float movY = Angles.trnsy(mount.rotation, 0, 14)*mount.warmup;

          float targetX = unit.x + Angles.trnsx(unit.rotation() - 90, mount.weapon.x + movX, mount.weapon.y + movY);
          float targetY = unit.y + Angles.trnsy(unit.rotation() - 90, mount.weapon.x + movX, mount.weapon.y + movY);

          if (Mathf.chanceDelta(0.03f*mount.warmup)) {
            float dx = unit.x + Angles.trnsx(unit.rotation, -28, 0) - x;
            float dy = unit.y + Angles.trnsy(unit.rotation, -28, 0) - y;

            float dst = Mathf.dst(dx, dy);
            float ang = Mathf.angle(dx, dy);

            Tmp.v1.rnd(3);
            SglFx.moveParticle.at(x + Tmp.v1.x, y + Tmp.v1.y, ang, SglDrawConst.matrixNet, dst);
          }

          float dx = targetX - x;
          float dy = targetY - y;

          float dst = Mathf.len(dx, dy);
          Tmp.v1.set(1, 0).setAngle(unit.rotation() + mount.rotation);

          rot.lerpDelta(Tmp.v1, 0.05f);
          float speed = 2*(dst/24);

         // vel.lerpDelta(Tmp.v1.set(dx, dy).setLength(speed).add(Tmp.v2.set(0.12f, 0).setAngle(Time.time*(mount.weapon.x > 0 ? 1 : -1) + mount.getVar(SglUnits.PHASE, 0f))), 0.075f);

          x += vel.x*Time.delta;
          y += vel.y*Time.delta;

          tmp1.set(MathTransform.fourierSeries(Time.time, farg1)).scl(mount.warmup);
          tmp2.set(MathTransform.fourierSeries(Time.time, farg2)).scl(mount.warmup);

          trail.update(x, y);
          trail1.update(x + tmp1.x, y + tmp1.y);
          trail2.update(x + tmp2.x, y + tmp2.y);
        }

        public void draw(Unit unit, DataWeaponMount mount) {
          float angle = rot.angle() - 90;
          Draw.rect(mount.weapon.region, x, y, angle);

          SglDraw.drawBloomUnderFlyUnit(() -> {
            trail.draw(SglDrawConst.matrixNet, 4);
            Draw.color(Color.black);
            Fill.circle(x, y, 4);
            Draw.reset();
          });

          float z = Draw.z();
          Draw.z(Layer.effect - 1);

          Draw.color(SglDrawConst.matrixNet);

          Draw.draw(Draw.z(), () -> {
            float dx = Angles.trnsx(unit.rotation, -28, 0);
            float dy = Angles.trnsy(unit.rotation, -28, 0);
            MathRenderer.setDispersion((0.2f + Mathf.absin(Time.time/3f + off, 6, 0.4f))*mount.warmup);
            MathRenderer.setThreshold(0.3f, 0.8f);
            MathRenderer.drawSin(x, y, 3, unit.x + dx, unit.y + dy, 5, 120, -3*Time.time + off);
          });

          Draw.z(Draw.z() + 0.01f);

          trail1.draw(SglDrawConst.matrixNet, 3*mount.warmup);
          trail2.draw(SglDrawConst.matrixNet, 3*mount.warmup);

          Draw.color(SglDrawConst.matrixNet);
          Fill.circle(x + tmp1.x, y + tmp1.y, 4f);
          Fill.circle(x + tmp2.x, y + tmp2.y, 4f);

          SglDraw.drawDiamond(x, y, 24, 10, angle);
          SglDraw.drawTransform(x, y, 0, 12, angle, (x, y, r) -> {
            SglDraw.gapTri(x, y, 12*mount.warmup, 22 + 24*mount.warmup, 8, r + 90);
          });
          SglDraw.drawTransform(x, y, 0, 10, angle - 180, (x, y, r) -> {
            SglDraw.gapTri(x, y, 9*mount.warmup, 12 + 8*mount.warmup, 6, r + 90);
          });

          Fill.circle(x, y, 6);
          Draw.color(Color.black);
          Fill.circle(x, y, 4);
          Draw.reset();

          Draw.z(Math.min(Layer.darkness, z - 1f));
          float e = Mathf.clamp(unit.elevation, shadowElevation, 1f)*shadowElevationScl*(1f - unit.drownTime);
          float x = this.x + shadowTX*e, y = this.y + shadowTY*e;
          Floor floor = world.floorWorld(x, y);

          float dest = floor.canShadow ? 1f : 0f;
          unit.shadowAlpha = unit.shadowAlpha < 0 ? dest : Mathf.approachDelta(unit.shadowAlpha, dest, 0.11f);
          Draw.color(Pal.shadow, Pal.shadow.a*unit.shadowAlpha);

          Draw.z(Draw.z() + 0.02f);
          Draw.rect(mount.weapon.region, this.x + shadowTX*e, this.y + shadowTY*e, angle);
          Draw.color();
          Draw.z(z);
        }
      }
    }

    BulletType turretBullet = new EmpBulletType() {
      {
        damage = 420;
        empDamage = 37;

        pierceCap = 4;
        pierceBuilding = true;
        laserAbsorb = true;

        speed = 16;
        lifetime = 35;

        hitSize = 6;

        trailEffect = new MultiEffect(
            SglFx.trailLineLong,
            Fx.colorSparkBig
        );
        trailChance = 1;
        trailRotation = true;

       // hitSound = Sounds.spark;

        hitEffect = Fx.circleColorSpark;
        hitColor = SglDrawConst.matrixNet;

        shootEffect = Fx.circleColorSpark;

        despawnHit = true;

        trailLength = 38;
        trailWidth = 4;
        trailColor = SglDrawConst.matrixNet;
      }

      @Override
      public void init(Bullet b) {
        super.init(b);
        TrailMoveLightning l = Pools.obtain(TrailMoveLightning.class, TrailMoveLightning::new);
        l.chance = 0.5f;
        l.maxOff = 6;
        l.range = 12;
        b.data = l;
      }

      @Override
      public void updateTrail(Bullet b) {
        if (!headless && trailLength > 0) {
          if (b.trail == null) {
            b.trail = new Trail(trailLength);
          }
          b.trail.length = trailLength;

          if (!(b.data instanceof TrailMoveLightning m)) return;
          m.update();
          SglDraw.drawTransform(b.x, b.y, 0, m.off, b.rotation(), (x, y, r) -> b.trail.update(x, y));
        }
      }

      @Override
      public void removed(Bullet b) {
        super.removed(b);
        if (b.data instanceof TrailMoveLightning) {
          Pools.free(b.data);
        }
      }
    };
    weapons.addAll(
        new SglWeapon(Sgl.modName + "-emptiness_turret") {
          {
            x = 17f;
            y = 26.5f;
            rotate = true;
            shootCone = 6;
            rotateSpeed = 5f;
            recoilTime = 45;
            recoil = 6;

            shake = 4;

            reload = 30;
           // shootSound = Sounds.spark;

            bullet = turretBullet;
          }
        },
        new SglWeapon(Sgl.modName + "-emptiness_turret") {
          {
            x = 22f;
            y = -1f;
            rotate = true;
            shootCone = 6;
            rotateSpeed = 5f;
            recoilTime = 45;
            recoil = 6;

            shake = 4;

            reload = 30;
          //  shootSound = Sounds.spark;

            bullet = turretBullet;
          }
        },
        new SglWeapon(Sgl.modName + "-emptiness_cannon") {
          {
            x = 27f;
            y = -35f;
            rotate = true;
            shootCone = 6;
            rotateSpeed = 3.5f;
            recoilTime = 60;
            recoil = 6;

           // shootSound = Sounds.plasmaboom;

            shake = 5;

            reload = 60;

            bullet = new MultiTrailBulletType() {
              {
                damage = 60;
                splashDamage = 560;
                splashDamageRadius = 18;

                pierceCap = 5;
                pierceBuilding = true;

                hitEffect = new MultiEffect(
                    SglFx.diamondSparkLarge,
                    SglFx.spreadSparkLarge
                );
                despawnEffect = SglFx.explodeImpWaveSmall;

                hitShake = 6;
               // hitSound = Sounds.spark;

                speed = 10;
                lifetime = 60;
                trailEffect = new MultiEffect(
                    Fx.colorSparkBig,
                    SglFx.movingCrystalFrag,
                    SglFx.polyParticle
                );
                trailChance = 0.3f;
                trailColor = SglDrawConst.matrixNet;
                trailRotation = true;

                shootEffect = SglFx.shootRail;
                smokeEffect = Fx.shootSmokeSmite;
                hitColor = SglDrawConst.matrixNet;

                trailLength = 34;
                trailWidth = 4;
                hitSize = 6;
              }

              @Override
              public void draw(Bullet b) {
                super.draw(b);

                Draw.color(hitColor);
                SglDraw.gapTri(b.x, b.y, 12, 28, -10, b.rotation());
              }
            };
          }
        },
        new MayflyWeapon() {{
          x = 58.5f;
          y = -13.75f;
          baseRotation = -45;
        }},
        new MayflyWeapon() {{
          x = 57.5f;
          y = -37.75f;
          baseRotation = -90;

          delay = 20;
        }},
        new MayflyWeapon() {{
          x = 52.5f;
          y = -65.75f;
          baseRotation = -135;

          delay = 40;
        }},
        new SglWeapon(Sgl.modName + "-lightedge") {
          {
            x = 0;
            y = -28f;
            mirror = false;
            recoil = 0;

            targetSwitchInterval = 80;

         //   shootSound = Sounds.laserblast;

            reload = 750;
            cooldownTime = 30;

            minWarmup = 0.95f;
            linearWarmup = false;
            shootWarmupSpeed = 0.014f;

            class BlastLaser extends singularity.world.blocks.turrets.EmpLightningBulletType {
              float blastDelay = 30;
              float damageInterval = 5;
              float laserShake = 5, damageShake = 12;
              Effect laserEffect = Fx.none;
            //  Sound laserSound = Sounds.laserbig;
              boolean blackZone = true;

              {
                collides = false;
                hittable = false;
                absorbable = false;
                pierce = true;
                pierceBuilding = true;
                pierceArmor = true;

                fragOnHit = true;//仅用于禁用despawnen时生成子弹

                keepVelocity = false;

                speed = 0;

                hitColor = SglDrawConst.matrixNet;
              }

              static final VectorLightningGenerator gen = new VectorLightningGenerator();

              @Override
              public float continuousDamage() {
                return damage*(60/damageInterval);
              }

              @Override
              public void init() {
                super.init();
                drawSize = range;
              }

              @Override
              public void update(Bullet b) {
                super.update(b);

                Effect.shake(laserShake, laserShake, b);
                if (b.timer(1, damageInterval)) {
                  Damage.collideLaser(b, Mathf.len(b.aimX - b.x, b.aimY - b.y)*Mathf.clamp(b.time/blastDelay), true, false, -1);
                }
              }

              @Override
              public void updateBulletInterval(Bullet b) {
                Vec2 n = Tmp.v1.set(b.x, b.y).lerp(b.aimX, b.aimY, Mathf.clamp(b.time/blastDelay));

                if(intervalBullet != null && b.time >= intervalDelay && b.timer.get(2, bulletInterval)){
                  float ang = b.rotation();
                  for(int i = 0; i < intervalBullets; i++){
                    intervalBullet.create(b, n.x, n.y, ang + Mathf.range(intervalRandomSpread) + intervalAngle + ((i - (intervalBullets - 1f)/2f) * intervalSpread), rand.random(0.4f, 1f), rand.random(0.8f, 1f));
                  }
                }
              }

              @Override
              public void hit(Bullet b, float x, float y) {
                hitEffect.at(x, y, b.rotation(), hitColor);
                hitSound.at(x, y, hitSoundPitch, hitSoundVolume);

                Effect.shake(hitShake, hitShake, b);
              }

              @Override
              public void init(Bullet b, LightningContainer cont) {
                gen.maxSpread = hitSize*2.3f;
                gen.minInterval = 2f*hitSize;
                gen.maxInterval = 3.8f*hitSize;

                gen.vector.set(b.aimX - b.x, b.aimY - b.y).limit(range);
                b.aimX = b.x + gen.vector.x;
                b.aimY = b.y + gen.vector.y;
                cont.lerp = Interp.pow4Out;
                cont.lifeTime = lifetime;
                cont.time = blastDelay;
                cont.maxWidth = 5;
                cont.minWidth = 3;

                float ax = b.aimX, ay = b.aimY;

                for (int i = 0; i < 4; i++) {
                  cont.create(gen);
                }
                Time.run(blastDelay, () -> {
                  Effect.shake(damageShake, damageShake, b);
                  createSplashDamage(b, ax, ay);
              //    laserSound.at(ax, ay);
                  laserEffect.at(ax, ay, b.rotation(), hitColor);
                  createFrags(b, b.aimX, b.aimY);
                });
              }

              @Override
              public void createFrags(Bullet b, float x, float y) {
                if (fragBullet != null && fragBullets > 0) {
                  Unit[] arr = SglUnitSorts.findEnemies(fragBullets, b, fragBullet.range, (us, u) -> {
                    if (b.dst(u) < fragBullet.splashDamageRadius) return false;

                    for (Unit e : us) {
                      if (e == null) break;

                      if (e.dst(u) < fragBullet.splashDamageRadius) return false;
                    }

                    return true;
                  }, UnitSorts.farthest);
                  for (int i = 0; i < arr.length; i++) {
                    if (arr[i] != null) {
                      Tmp.v1.set(arr[i].x - x, arr[i].y - y);

                      fragBullet.create(b.owner, b.team, x, y, Tmp.v1.angle(),
                          fragBullet.damage, 1, 1, null, null,
                          x + Tmp.v1.x, y + Tmp.v1.y
                      );
                    } else {
                      float a = b.rotation() + Mathf.range(fragRandomSpread/2) + fragAngle + ((i - fragBullets/2f)*fragSpread);

                      Tmp.v1.set(fragBullet.range*Mathf.random(0.6f, 1f), 0).setAngle(a);
                      fragBullet.create(b.owner, b.team, x, y, a,
                          fragBullet.damage, 1, 1, null, null,
                          x + Tmp.v1.x, y + Tmp.v1.y
                      );
                    }
                  }
                }
              }

              @Override
              public void draw(Bullet b) {
                float in = Mathf.clamp(b.time/blastDelay);
                Tmp.v1.set(b.aimX - b.x, b.aimY - b.y).scl(in);

                float dx = b.x + Tmp.v1.x;
                float dy = b.y + Tmp.v1.y;

                Draw.color(hitColor);
                float fout = b.fout(Interp.pow3Out);
                Fill.circle(dx, dy, hitSize*1.6f*fout);
                Lines.stroke(hitSize*(1 + in)*fout);
                Lines.line(b.x, b.y, dx, dy);

                Lines.stroke(hitSize*0.1f*fout);
                Lines.circle(dx, dy, hitSize*1.9f*fout);

                Draw.color(Color.white);
                Fill.circle(dx, dy, hitSize*1.2f*fout);
                Lines.stroke(hitSize*(1 + in)*fout*0.75f);
                Lines.line(b.x, b.y, dx, dy);

                if (blackZone) {
                  float z = Draw.z();
                  Draw.z(z + 0.0001f);
                  Draw.color(Color.black);
                  Fill.circle(dx, dy, hitSize*0.6f*fout);
                  Lines.stroke(hitSize*(1 + in)*fout/2.8f);
                  Lines.line(b.x, b.y, dx, dy);
                  Draw.z(z);
                }

                Draw.color(hitColor);
                Fill.circle(b.x, b.y, hitSize*(1.2f + in)*fout);

                super.draw(b);
              }
            }

            bullet = new BlastLaser() {
              {
                damage = 160;
                damageInterval = 5;

                blastDelay = 38;
                rangeOverride = 600;
                splashDamage = 3280;
                splashDamageRadius = 120;
                empDamage = 530;
                empRange = 120;
                lifetime = 245;
                hitSize = 12;

                intervalBullet = SglTurrets.spilloverEnergy.copy();
                intervalBullet.damage = 160;
                intervalBullet.splashDamage = 160;
                intervalBullet.splashDamageRadius = 45;
                intervalBullets = 2;
                intervalDelay = 3;
                intervalRandomSpread = 360;

                laserEffect = new MultiEffect(
                    SglFx.laserBlastWeaveLarge,
                    SglFx.circleSparkLarge,
                    SglFx.impactBubbleBig
                );
                shootEffect = new MultiEffect(
                    SglFx.shootCrossLightLarge,
                    SglFx.explodeImpWaveBig,
                    SglFx.impactWaveBig,
                    SglFx.impactBubble
                );
                hitEffect = new MultiEffect(
                    Fx.colorSparkBig,
                    SglFx.diamondSparkLarge
                );

                hitColor = SglDrawConst.matrixNet;

                fragBullets = 3;
                fragSpread = 120;
                fragRandomSpread = 72;
                fragBullet = new BlastLaser() {
                  {
                    damage = 120;
                    damageInterval = 5;

                    rangeOverride = 360;
                    splashDamage = 1400;
                    splashDamageRadius = 60;
                    empDamage = 220;
                    empRange = 60;
                    lifetime = 186;
                    hitSize = 9;

                    hitEffect = new MultiEffect(
                        Fx.circleColorSpark,
                        SglFx.diamondSparkLarge
                    );

                    blackZone = false;

                    laserEffect = SglFx.explodeImpWaveLaserBlase;

                    final RandomGenerator branch = new RandomGenerator();
                    RandomGenerator g = new RandomGenerator() {{
                      maxLength = 140;
                      maxDeflect = 55;

                      branchChance = 0.2f;
                      minBranchStrength = 0.8f;
                      maxBranchStrength = 1;
                      branchMaker = (vert, strength) -> {
                        branch.maxLength = 60*strength;
                        branch.originAngle = vert.angle + Mathf.random(-90, 90);

                        return branch;
                      };
                    }};

                    fragBullets = 8;
                    fragBullet = SglTurrets.Companion.lightning(128, 32, 62, 5.2f, SglDrawConst.matrixNet, b -> {
                      g.originAngle = b.rotation();
                      return g;
                    });
                    fragBullet.rangeOverride = 120;
                  }
                };
              }

              @Override
              public void createSplashDamage(Bullet b, float x, float y) {
                super.createSplashDamage(b, x, y);

                Angles.randLenVectors(System.nanoTime(), Mathf.random(15, 22), 4, 6.5f,
                    (dx, dy) ->{});
                        /*    SglParticleModels.INSTANCE.getFloatParticle().create(x, y, hitColor, dx, dy, Mathf.random(5.25f, 7f)));*/

            }};

            parts.addAll(
                new CustomPart() {{
                  layer = Layer.effect;
                  progress = PartProgress.warmup;

                  draw = (x, y, r, p) -> {
                    Draw.color(SglDrawConst.matrixNet);

                    float dx = Angles.trnsx(r, 1, 0);
                    float dy = Angles.trnsy(r, 1, 0);

                    for (int i = 0; i < 4; i++) {
                      int len = 20 + i*25 - (i%2)*6;
                      float rx = x + dx*len;
                      float ry = y + dy*len;

                      SglDraw.gapTri(rx, ry, Mathf.absin(Time.time/4 - i*Mathf.pi, 1, (10 - 2*i)*p), (10 + (6 + (i%2)*6)*p) - i, (i%2 == 0 ? -1 : 1)*(5 + 4*p - i), r);
                    }

                    SglDraw.drawDiamond(x, y, 44 + 20*p, 8 + 4*p, Time.time);
                    SglDraw.drawDiamond(x, y, 38 + 14*p, 6 + 4*p, -Time.time*1.2f);
                    SglDraw.drawDiamond(x, y, 32 + 8*p, 4 + 3*p, Time.time*1.3f);
                    Fill.circle(x, y, 12f);
                    Draw.color(Color.white);
                    Fill.circle(x, y, 9f);
                    Draw.color(Color.black);
                    Fill.circle(x, y, 7.5f);
                  };
                }}
            );
          }

          @Override
          protected Teamc findTarget(Unit unit, float x, float y, float range, boolean air, boolean ground) {
            return Units.bestTarget(unit.team, x, y, range, u -> unit.checkTarget(air, ground), t -> ground, SglUnitSorts.denser);
          }

          @Override
          public void draw(Unit unit, WeaponMount mount) {
            float x = unit.x + Angles.trnsx(unit.rotation() - 90, mount.weapon.x, mount.weapon.y);
            float y = unit.y + Angles.trnsy(unit.rotation() - 90, mount.weapon.x, mount.weapon.y);

            float angle = Mathf.angle(mount.aimX - x, mount.aimY - y);
            float dst = Mathf.dst(mount.aimX - x, mount.aimY - y);

            float angDiff = Angles.angleDist(angle, unit.rotation());

            float lerp = Mathf.clamp((18 - Math.abs(angDiff))/18f)*Mathf.clamp(mount.warmup - 0.05f);
            float stLerp = lerp*(1f - Mathf.clamp((dst - 500f)/100f));

            float z = Draw.z();
            Draw.z(Layer.effect);
            Lines.stroke(4f*stLerp*Mathf.clamp(1 - mount.reload/mount.weapon.reload), unit.team.color);
            Lines.line(x, y, mount.aimX, mount.aimY);
            Lines.square(mount.aimX, mount.aimY, 18, 45);

            float l = Math.max(Mathf.clamp(mount.warmup/mount.weapon.minWarmup)*Mathf.clamp(1 - mount.reload/mount.weapon.reload), mount.heat);
            Lines.stroke(4f*l*stLerp);
            SglDraw.arc(mount.aimX, mount.aimY, 62, 360*l, -Time.time*1.2f);

            Lines.stroke(4f*Mathf.clamp(mount.warmup/mount.weapon.minWarmup), SglDrawConst.matrixNet);
            SglDraw.drawCornerTri(mount.aimX, mount.aimY, 46, 8, MathTransform.gradientRotateDeg(Time.time*0.85f, 38, 1/3f, 3), true);

            Draw.z(Draw.z() + 0.01f);
            for (int i = 0; i < 3; i++) {
              SglDraw.drawTransform(mount.aimX, mount.aimY, 54, 0, -1.4f*Time.time + i*120, (rx, ry, r) -> {
                Draw.rect(((TextureRegionDrawable) SglDrawConst.matrixArrow).getRegion(), rx, ry, 12*stLerp, 12*stLerp, r + 90);
              });
            }

            Draw.z(z);

            super.draw(unit, mount);
          }
        }
    );
  }

  @Override
  public void load() {
    super.load();
    shadowRegion = region;
  }
}
