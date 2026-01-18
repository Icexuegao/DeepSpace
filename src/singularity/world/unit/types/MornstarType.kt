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
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pool;
import arc.util.pooling.Pools;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ContinuousBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;
import mindustry.world.meta.BlockFlag;
import singularity.Sgl;
import singularity.contents.SglItems;
import singularity.graphic.SglDraw;
import singularity.graphic.SglDrawConst;
import singularity.world.SglFx;
import singularity.world.blocks.turrets.EmpMultiTrailBulletType;
import singularity.world.blocks.turrets.LightLaserBulletType;
import singularity.world.unit.AirSeaAmphibiousUnit;
import singularity.world.unit.RelatedWeapon;
import singularity.world.unit.SglWeapon;
import singularity.world.unit.abilities.MirrorFieldAbility;
import universecore.world.lightnings.LightningContainer;

import static mindustry.Vars.headless;

public class MornstarType extends AirSeaAmphibiousUnit {
  private static final Rand rand = new Rand();

  public MornstarType() {
    super("mornstar");
      constructor = UnitEntity::create;
    requirements(
        Items.silicon, 420,
        Items.phaseFabric, 360,
        Items.surgeAlloy, 320,
        SglItems.aluminium, 380,
        SglItems.aerogel, 320,
        SglItems.crystal_FEX_power, 220,
        SglItems.strengthening_alloy, 280,
        SglItems.iridium, 200,
        SglItems.matrix_alloy, 220
    );

    armor = 19;
    speed = 0.84f;
    accel = 0.065f;
    drag = 0.03f;
    rotateSpeed = 1.8f;
    riseSpeed = 0.02f;
    boostMultiplier = 1.25f;
    faceTarget = true;
    health = 42500;
    lowAltitude = true;
    hitSize = 64;
    targetFlags = BlockFlag.allLogic;

    drawShields = false;

    engineOffset = 0;
    engineSize = 0;

    abilities.addAll(
        new MirrorFieldAbility() {{
          strength = 340;
          maxShield = 12500;
          recoverSpeed = 6f;
          cooldown = 2680;
          minAlbedo = 0.7f;
          maxAlbedo = 1f;
          rotation = false;

          shieldArmor = 10;

          nearRadius = 156;

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

          shapes.addAll(
              new ShieldShape(8, 0, 0, 0, 102) {{
                movement = new ShapeMove() {{
                  rotateSpeed = -0.1f;
                }};
              }},
              a.get(90f, 0f),
              a.get(-90f, 0f),
              a.get(0f, 90f),
              a.get(0f, -90f)
          );
        }}
    );

    setEnginesMirror(
        new UnitEngine() {{
          x = 16f;
          y = -44f;
          radius = 10;
          rotation = 45;
        }},
        new UnitEngine() {{
          x = 24f;
          y = -52f;
          radius = 6;
          rotation = 45;
        }},
        new UnitEngine() {{
          x = 34f;
          y = -52f;
          radius = 8;
          rotation = -45;
        }}
    );

    weapons.addAll(
        new SglWeapon(Sgl.modName + "-mornstar_cannon") {{
          recoil = 0;
          recoilTime = 120;
          cooldownTime = 120;

          reload = 90;
          rotate = true;
          mirror = false;

          rotateSpeed = 2.5f;

          layerOffset = 1;

          x = 0;
          y = 4;
          shootY = 25;

          shoot = new ShootBarrel() {{
            barrels = new float[]{
                5.75f, 0, 0,
                -5.75f, 0, 0
            };
            shots = 2;
            shotDelay = 0;
          }};

          bullet = new EmpMultiTrailBulletType() {
            {
              hitColor = trailColor = SglDrawConst.matrixNet;
              trailLength = 22;
              trailWidth = 2f;
              trailEffect = new MultiEffect(
                  SglFx.trailLineLong,
                  SglFx.railShootRecoil,
                  SglFx.movingCrystalFrag
              );
              trailRotation = true;
              trailChance = 1;

              lightColor = SglDrawConst.matrixNet;
              lightRadius = 120;
              lightOpacity = 0.8f;

              shootEffect = new MultiEffect(
                  SglFx.shootRecoilWave,
                  SglFx.shootRail
              );
              hitEffect = SglFx.lightConeHit;
              despawnEffect = new MultiEffect(
                  SglFx.impactWaveSmall,
                  SglFx.spreadSparkLarge,
                  SglFx.diamondSparkLarge
              );
              smokeEffect = Fx.shootSmokeSmite;

              shootSound = Sounds.shootSmite;

              damage = 500;
              empDamage = 100;
              lifetime = 45;
              speed = 8;
              pierceCap = 4;
              hittable = false;

              fragBullet = new EdgeFragBullet();
              fragOnHit = true;
              fragBullets = 3;
              fragRandomSpread = 115;
              fragLifeMin = 0.7f;
            }

            @Override
            public void draw(Bullet b) {
              super.draw(b);
              Draw.color(SglDrawConst.matrixNet);
              SglDraw.gapTri(b.x, b.y, 18, 28, 16, b.rotation());
              SglDraw.drawTransform(b.x, b.y, -6, 0, b.rotation(), (x, y, r) -> {
                SglDraw.drawDiamond(x, y, 16, 8, r);
              });
            }

            @Override
            public void hit(Bullet b) {
              super.hit(b);
              b.damage -= 125;
            }

            @Override
            public void init(Bullet b) {
              super.init(b);
              b.data = Pools.obtain(TrailMoveLightning.class, TrailMoveLightning::new);
            }

            @Override
            public void despawned(Bullet b) {
              super.despawned(b);
              if (b.data instanceof TrailMoveLightning l) Pools.free(l);
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
          };

          parts.addAll(
              new RegionPart("_blade") {{
                under = true;
                progress = PartProgress.recoil;
                moveY = -3;
                heatColor = Pal.turretHeat;
                heatProgress = PartProgress.heat;
              }},
              new RegionPart("_body") {{
                under = true;
              }}
          );
        }},
        new SglWeapon(Sgl.modName + "-mornstar_turret") {{
          x = 26;
          y = -28;
          shootY = 0;
          recoil = 5;
          recoilTime = 60;
          reload = 4;

          rotate = true;
          rotateSpeed = 8;

          customDisplay = (b, t) -> {
            t.row();
            t.add(Core.bundle.get("infos.damageAttenuationWithDist")).color(Pal.accent);
          };

          bullet = new BulletType() {
            {
              speed = 12;
              lifetime = 30;
              damage = 180;

              lightColor = SglDrawConst.matrixNet;
              lightRadius = 58;
              lightOpacity = 0.6f;

              pierceCap = 1;

              despawnHit = true;

            //  shootSound = Sounds.bolt;

              hitColor = trailColor = SglDrawConst.matrixNet;
              hitEffect = new MultiEffect(
                  SglFx.spreadDiamondSmall,
                  SglFx.movingCrystalFrag
              );
              smokeEffect = Fx.colorSpark;
              shootEffect = new MultiEffect(
                  SglFx.railShootRecoil,
                  SglFx.crossLightMini
              );
              trailWidth = 3;
              trailLength = 8;

              trailEffect = SglFx.glowParticle;
              trailChance = 0.12f;
            }

            @Override
            public void draw(Bullet b) {
              super.draw(b);
              Draw.color(SglDrawConst.matrixNet);
              Tmp.v1.set(1, 0).setAngle(b.rotation());
              SglDraw.gapTri(b.x + Tmp.v1.x*3*b.fout(), b.y + Tmp.v1.y*3*b.fout(), 15, 22, 14, b.rotation());
              SglDraw.gapTri(b.x - Tmp.v1.x*3*b.fout(), b.y - Tmp.v1.y*3*b.fout(), 12, 18, 10, b.rotation());
              SglDraw.gapTri(b.x - Tmp.v1.x*5*b.fout(), b.y - Tmp.v1.y*5*b.fout(), 9, 12, 8, b.rotation());
              SglDraw.drawDiamond(b.x, b.y, 18, 6, b.rotation());
            }

            @Override
            public void update(Bullet b) {
              super.update(b);
              b.damage = (b.type.damage + b.type.damage*b.fout())*0.5f;
            }
          };
        }},
        new RelatedWeapon(Sgl.modName + "-lightedge") {
          {
            useAlternative = isFlying;
            mirror = false;
            x = 0;
            y = -25;
            shootCone = 180;

            recoil = 0;
            recoilTime = 1;

            alternate = false;

            linearWarmup = false;
            shootWarmupSpeed = 0.025f;
            minWarmup = 0.9f;

            reload = 60;

            bullet = new ContinuousBulletType() {
              {
                speed = 0;
                lifetime = 180;
                length = 420;

                damage = 80;

                lightColor = SglDrawConst.matrixNet;
                lightRadius = 96;
                lightOpacity = 1;

                hitEffect = SglFx.railShootRecoil;

                trailColor = hitColor = SglDrawConst.matrixNet;
                trailEffect = SglFx.movingCrystalFrag;
                trailInterval = 4;

                shootEffect = new MultiEffect(
                    SglFx.shootCrossLight,
                    SglFx.explodeImpWaveSmall
                );
              }

              @Override
              public void init(Bullet b) {
                super.init(b);

               // Sounds.laserblast.at(b.x, b.y, 1.25f);

                if (b.owner instanceof Unit u) {
                  b.rotation(b.angleTo(u.aimX, u.aimY));
                }
              }

              @Override
              public void update(Bullet b) {
                super.update(b);

                Effect.shake(4, 4, b.x, b.y);
                updateTrailEffects(b);
                if (b.owner instanceof Unit u) {
                  b.rotation(Angles.moveToward(b.rotation(), b.angleTo(u.aimX, u.aimY), 8*Time.delta));
                }
              }

              @Override
              public void applyDamage(Bullet b) {
                Damage.collideLaser(b, length, largeHit, laserAbsorb, pierceCap);
              }

              @Override
              public void draw(Bullet b) {
                super.draw(b);

                float realLen = b.fdata;
                float lerp = Mathf.clamp(b.time/40);
                float out = Mathf.clamp((b.type.lifetime - b.time)/30);
                lerp *= out;

                lerp = 1 - Mathf.pow(1 - lerp, 3);

                Draw.color(SglDrawConst.matrixNet);
                Drawf.tri(b.x, b.y, 12*lerp, realLen, b.rotation());
                Draw.color(Color.black);
                Drawf.tri(b.x, b.y, 5*lerp, realLen*0.8f, b.rotation());
                Draw.color(SglDrawConst.matrixNet);
                Fill.circle(b.x, b.y, 4*out + 3*lerp);
                SglDraw.drawDiamond(b.x, b.y, 24, 10*lerp, Time.time);
                SglDraw.drawDiamond(b.x, b.y, 28, 12*lerp, -Time.time*1.2f);
                Lines.stroke(0.8f);
                Lines.circle(b.x, b.y, 6);
                Draw.color(Color.black);
                Fill.circle(b.x, b.y, 4f*lerp);

                Drawf.light(b.x, b.y,
                    b.x + Angles.trnsx(b.rotation(), realLen), b.y + Angles.trnsy(b.rotation(), realLen),
                    46*lerp, lightColor, lightOpacity);

                Draw.color(SglDrawConst.matrixNet);
                SglDraw.gapTri(b.x + Angles.trnsx(Time.time, 8, 0), b.y + Angles.trnsy(Time.time, 8, 0), 8*lerp, 12 + 14*lerp, 8, Time.time);
                SglDraw.gapTri(b.x + Angles.trnsx(Time.time + 180, 8, 0), b.y + Angles.trnsy(Time.time + 180, 8, 0), 8*lerp, 12 + 14*lerp, 8, Time.time + 180);
                SglDraw.drawDiamond(b.x + Angles.trnsx(-Time.time*1.2f, 12, 0), b.y + Angles.trnsy(-Time.time*1.2f, 12, 0), 16, 5*lerp, -Time.time*1.2f);
                SglDraw.drawDiamond(b.x + Angles.trnsx(-Time.time*1.2f + 180, 12, 0), b.y + Angles.trnsy(-Time.time*1.2f + 180, 12, 0), 16, 5*lerp, -Time.time*1.2f + 180);

                float out2 = Mathf.pow(1 - out, 3);
                Tmp.v1.set(35 + 30*out2, 0).setAngle(b.rotation());
                Tmp.v2.set(Tmp.v1).setLength(8 + 10*lerp).rotate90(1);

                float len = 100 + out2*80;
                float an = Mathf.atan2(len/2, 8*lerp)*Mathf.radDeg;

                Drawf.tri(b.x + Tmp.v1.x + Tmp.v2.x, b.y + Tmp.v1.y + Tmp.v2.y, len, 8*lerp, b.rotation() - 90 - an);
                Drawf.tri(b.x + Tmp.v1.x - Tmp.v2.x, b.y + Tmp.v1.y - Tmp.v2.y, len, 8*lerp, b.rotation() + 90 + an);
              }
            };

            alternativeBullet = new BulletType() {
              {
                splashDamage = 380;
                splashDamageRadius = 32;

                speed = 8;
                lifetime = 360;
                rangeOverride = 360;
                homingDelay = 60;

                homingPower = 0.03f;
                homingRange = 360;

                lightColor = SglDrawConst.matrixNet;
                lightRadius = 75;
                lightOpacity = 0.8f;

                despawnShake = 6;

                collides = false;
                absorbable = false;
                hittable = false;

                keepVelocity = false;

                trailColor = hitColor = SglDrawConst.matrixNet;
                trailLength = 34;
                trailWidth = 4.5f;

                despawnEffect = new MultiEffect(
                    SglFx.explodeImpWave,
                    SglFx.crossLightSmall,
                    SglFx.diamondSparkLarge
                );

                trailEffect = SglFx.movingCrystalFrag;
                trailInterval = 4;

                fragBullet = new BulletType() {
                  {
                    damage = 60;
                    splashDamage = 80;
                    splashDamageRadius = 24;
                    speed = 4;
                    hitSize = 3;
                    lifetime = 120;
                    despawnHit = true;
                    hitEffect = SglFx.diamondSpark;
                    hitColor = SglDrawConst.matrixNet;

                    collidesTiles = false;

                    homingRange = 240;
                    homingPower = 0.035f;

                    trailColor = SglDrawConst.matrixNet;
                    trailLength = 25;
                    trailWidth = 3f;
                    trailEffect = SglFx.movingCrystalFrag;
                    trailInterval = 5;
                  }

                  @Override
                  public void draw(Bullet b) {
                    drawTrail(b);
                    Draw.color(hitColor);
                    Fill.circle(b.x, b.y, 4);
                    Draw.color(Color.black);
                    Fill.circle(b.x, b.y, 2.5f);
                  }

                  @Override
                  public void updateHoming(Bullet b) {
                    Posc target = Units.closestTarget(b.team, b.x, b.y, homingRange,
                        e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                        t -> t != null && collidesGround && !b.hasCollided(t.id));

                    if (target == null) {
                      b.vel.lerpDelta(Vec2.ZERO, homingPower);
                    } else {
                      b.vel.lerpDelta(Tmp.v1.set(target.x() - b.x, target.y() - b.y).setLength(speed*0.5f), homingPower);
                    }
                  }
                };
                fragBullets = 5;
                fragLifeMin = 0.7f;

                intervalBullet = new LightLaserBulletType() {
                  {
                    damage = 150;
                    empDamage = 20;
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
                bulletInterval = 15f;
              }

              @Override
              public void draw(Bullet b) {
                super.draw(b);
                Draw.color(SglDrawConst.matrixNet);

                float lerp = Mathf.clamp(b.time/homingDelay);
                lerp = 1 - Mathf.pow(1 - lerp, 2);

                Fill.circle(b.x, b.y, 4 + 2*lerp);
                SglDraw.drawDiamond(b.x, b.y, 22, 10*lerp, Time.time);
                Lines.stroke(0.8f);
                Lines.circle(b.x, b.y, 6);
                Draw.color(Color.black);
                Fill.circle(b.x, b.y, 3.75f*lerp);
                Draw.color(SglDrawConst.matrixNet);

                rand.setSeed(b.id);
                for (int i = 0; i < 7; i++) {
                  float w = rand.random(1f, 2.5f)*(rand.random(1f) > 0.5 ? 1 : -1);
                  float f = rand.random(360f);
                  float r = rand.random(12f, 28f);
                  float size = rand.random(18f, 26f)*lerp;

                  float a = f + Time.time*w;
                  Tmp.v1.set(r, 0).setAngle(a);

                  SglDraw.drawHaloPart(b.x + Tmp.v1.x, b.y + Tmp.v1.y, size, size*0.5f, a);
                }
              }

              @Override
              public void despawned(Bullet b) {
                super.despawned(b);
             //   Sounds.malignShoot.at(b, 2);
              }

              @Override
              public void update(Bullet b) {
                super.update(b);

                if (b.timer(4, 18f)) {
                  Teamc target = Units.closestTarget(b.team, b.x, b.y, range,
                      e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                      t -> t != null && collidesGround && !b.hasCollided(t.id));

                  fragBullet.create(b, b.x, b.y, target != null ? b.angleTo(target) : Mathf.random(0, 360));
                }
              }

              @Override
              public void updateHoming(Bullet b) {
                if (homingPower > 0.0001f && b.time >= homingDelay) {
                  float realAimX = b.aimX < 0 ? b.x : b.aimX;
                  float realAimY = b.aimY < 0 ? b.y : b.aimY;

                  Posc target;
                  if (b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)) {
                    target = b.aimTile.build;
                  } else {
                    target = Units.closestTarget(b.team, realAimX, realAimY, homingRange,
                        e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                        t -> t != null && collidesGround && !b.hasCollided(t.id));
                  }

                  if (target != null) {
                    float dst = target.dst(b);
                    float v = Mathf.lerpDelta(b.vel.len(), speed*(dst/homingRange), 0.05f);
                    b.vel.setLength(v);

                    float degA = b.rotation();
                    float degB = b.angleTo(target);

                    if (degA - degB > 180) {
                      degB += 360;
                    } else if (degA - degB < -180) {
                      degB -= 360;
                    }

                    b.vel.setAngle(Mathf.lerpDelta(degA, degB, homingPower));
                  } else {
                    b.vel.lerpDelta(0, 0, 0.03f);
                  }
                }
              }
            };
          }

          @Override
          public void init(Unit unit, DataWeaponMount mount) {
            super.init(unit, mount);
           // mount.setVar(SglUnits.EPHEMERAS, new Seq<>(Ephemera.class));
          //  mount.setVar(SglUnits.TIMER, new Interval(3));
          }

          @Override
          protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation) {
            DataWeaponMount m = (DataWeaponMount) mount;
            /*Seq<Ephemera> seq = m.getVar(SglUnits.EPHEMERAS);
            for (Ephemera ephemera : seq) {
              if (ephemera.alpha > 0.9f) {
                ephemera.shoot(unit, useAlternative.alt(unit) ? alternativeBullet : bullet);
                ephemera.removed = true;
                break;
              }
            }*/
          }

          @Override
          public void draw(Unit unit, DataWeaponMount mount) {
            super.draw(unit, mount);
            Draw.z(Layer.effect);
            SglDraw.drawTransform(unit.x, unit.y, mount.weapon.x, mount.weapon.y, unit.rotation - 90, (x, y, r) -> {
              Draw.color(SglDrawConst.matrixNet);
              Fill.circle(x, y, 6);
              Lines.stroke(0.8f);
              SglDraw.dashCircle(x, y, 8, 4, 180, Time.time);
              Lines.stroke(0.5f);
              Lines.circle(x, y, 10);

              Draw.alpha(1);
              SglDraw.drawDiamond(x, y, 20 + 14*mount.warmup, 2 + 3*mount.warmup, Time.time*1.2f);
              SglDraw.drawDiamond(x, y, 26 + 14*mount.warmup, 3 + 4*mount.warmup, -Time.time*1.2f);

             /* for (Ephemera ephemera : mount.<Seq<Ephemera>>getVar(SglUnits.EPHEMERAS)) {
                Draw.color(SglDrawConst.matrixNet);

                if (!ephemera.removed) {
                  Fill.circle(ephemera.x, ephemera.y, 4);
                  Lines.stroke(0.8f);
                  Lines.circle(ephemera.x, ephemera.y, 6);
                }

                Drawf.light(ephemera.x, ephemera.y, 60, lightColor, 0.45f);

                for (int i = 0; i < 3; i++) {
                  Tmp.v1.set(16, 0).setAngle(Time.time + i*120);
                  float sin = Mathf.absin((Time.time*4 + i*120)*Mathf.degRad, 0.5f, 1);
                  sin = Math.max(sin, mount.warmup)*ephemera.alpha;
                  float w = sin*sin*sin*4;
                  SglDraw.drawDiamond(ephemera.x + Tmp.v1.x, ephemera.y + Tmp.v1.y, 20, w, Time.time + i*120);
                  SglDraw.drawDiamond(ephemera.x - Tmp.v1.x, ephemera.y - Tmp.v1.y, 20, w, Time.time + i*120);
                }

                ephemera.trail.draw(SglDrawConst.matrixNet, 4f*ephemera.alpha);
              }*/
            });
          }

          @Override
          public void update(Unit unit, DataWeaponMount mount) {
            Tmp.v1.set(mount.weapon.x, mount.weapon.y).rotate(unit.rotation - 90);
            float mx = unit.x + Tmp.v1.x;
            float my = unit.y + Tmp.v1.y;
          /*  Seq<Ephemera> seq = mount.getVar(SglUnits.EPHEMERAS);

            if (seq.size < 4) {
              if (mount.<Interval>getVar(SglUnits.TIMER).get(0, 240)) {
                mount.totalShots++;

                Ephemera ephemera = Pools.obtain(Ephemera.class, Ephemera::new);
                ephemera.x = mx;
                ephemera.y = my;
                ephemera.move = Mathf.random(0.02f, 0.04f);
                ephemera.angelOff = Mathf.random(15, 45)*(mount.totalShots%2 == 0 ? 1 : -1);
                ephemera.bestDst = Mathf.random(18, 36);
                ephemera.vel.rnd(Mathf.random(0.6f, 2));

                seq.add(ephemera);
              }
            }*/

          /*  if (seq.isEmpty()) {
              mount.reload = mount.weapon.reload;
            }

            for (Iterator<Ephemera> iterator = seq.iterator(); iterator.hasNext(); ) {
              Ephemera ephemera = iterator.next();
              ephemera.alpha = Mathf.lerpDelta(ephemera.alpha, ephemera.removed ? 0 : 1, 0.015f);
              ephemera.trail.update(ephemera.x, ephemera.y);
              if (ephemera.removed) {
                if (ephemera.alpha <= 0.05f) {
                  iterator.remove();
                  Pools.free(ephemera);
                }
                continue;
              }

              ephemera.x += ephemera.vel.x*Time.delta;
              ephemera.y += ephemera.vel.y*Time.delta;

              float dst = (ephemera.bestDst + ephemera.bestDst*mount.warmup) - Mathf.dst(ephemera.x - mx, ephemera.y - my);
              float speed = dst/30;

              ephemera.vel.lerpDelta(Tmp.v1.set(ephemera.x - unit.x, ephemera.y - unit.y).setLength2(1).scl(speed).rotate(ephemera.angelOff), 0.15f);
              Tmp.v1.set(ephemera.move + ephemera.move*mount.warmup, 0).rotate(Time.time);
              ephemera.vel.add(Tmp.v1);
            }*/
          }

          static class Ephemera implements Pool.Poolable {
            float x, y, angelOff, move;
            float bestDst, alpha;
            boolean removed;
            final Vec2 vel = new Vec2();
            final Trail trail = new Trail(60);

            @Override
            public void reset() {
              x = y = 0;
              alpha = 0;
              removed = false;
              vel.setZero();
              trail.clear();
              bestDst = 0;
              move = 0;
              angelOff = 0;
            }

            public void shoot(Unit u, BulletType bullet) {
              Bullet b = bullet.create(u, x, y, vel.angle());
              if (b.type.speed > 0.01f) b.vel.set(vel);
              b.set(x, y);
              bullet.shootEffect.at(b.x, b.y, vel.angle(), b.type.hitColor);
              bullet.smokeEffect.at(b.x, b.y, vel.angle(), b.type.hitColor);
            }
          }
        }
    );
  }
}
