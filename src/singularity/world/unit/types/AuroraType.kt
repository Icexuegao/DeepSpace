package singularity.world.unit.types;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.Damage;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.entities.bullet.LightningBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.HaloPart;
import mindustry.entities.pattern.ShootPattern;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Trail;
import mindustry.type.Weapon;
import mindustry.world.meta.BlockFlag;
import singularity.Sgl;
import singularity.contents.SglItems;
import singularity.graphic.SglDraw;
import singularity.graphic.SglDrawConst;
import singularity.world.SglFx;
import singularity.world.blocks.turrets.EmpBulletType;
import singularity.world.blocks.turrets.LightLaserBulletType;
import singularity.world.draw.part.CustomPart;
import singularity.world.unit.AirSeaAmphibiousUnit;
import singularity.world.unit.RelatedWeapon;
import singularity.world.unit.SglWeapon;
import singularity.world.unit.abilities.MirrorArmorAbility;

import static mindustry.Vars.headless;

public class AuroraType extends AirSeaAmphibiousUnit {
  private static final Rand rand = new Rand();

  public AuroraType() {
    super("aurora");

    requirements(
        Items.silicon, 360,
        Items.phaseFabric, 380,
        Items.surgeAlloy, 390,
        SglItems.aluminium, 400,
        SglItems.aerogel, 430,
        SglItems.crystal_FEX, 280,
        SglItems.crystal_FEX_power, 280,
        SglItems.strengthening_alloy, 340,
        SglItems.iridium, 320,
        SglItems.matrix_alloy, 380,
        SglItems.degenerate_neutron_polymer, 200
    );

    armor = 10;
    speed = 0.65f;
    accel = 0.06f;
    drag = 0.04f;
    rotateSpeed = 1.25f;
    riseSpeed = 0.02f;
    boostMultiplier = 1.2f;
    faceTarget = true;
    health = 52500;
    lowAltitude = true;
    hitSize = 75;
    targetFlags = BlockFlag.allLogic;
    drawShields = false;

    engineOffset = 50;
    engineSize = 16;

    abilities.addAll(new MirrorArmorAbility(){{
      strength = 380;
      maxShield = 9500;
      recoverSpeed = 4f;
      cooldown = 6050;
      minAlbedo = 0.6f;
      maxAlbedo = 0.9f;

      shieldArmor = 12;
    }});

    setEnginesMirror(
        new UnitEngine() {{
          x = 38f;
          y = -12;
          radius = 8;
          rotation = -45;
        }},
        new UnitEngine() {{
          x = 40f;
          y = -54;
          radius = 10;
          rotation = -45;
        }}
    );

    weapons.addAll(
        new SglWeapon(Sgl.modName + "-aurora_lightcone") {
          {
            shake = 5f;
           // shootSound = Sounds.pulseBlast;
            x = 29;
            y = -30;
            shootY = 8;
            rotate = true;
            rotateSpeed = 3;
            recoil = 6;
            recoilTime = 60;
            cooldownTime = 60;
            reload = 60;
            shadow = 45;
            linearWarmup = false;
            shootWarmupSpeed = 0.03f;
            minWarmup = 0.8f;

            layerOffset = 1;

            parts.addAll(
                new CustomPart() {{
                  x = 0;
                  y = -16f;
                  layer = Layer.effect;
                  progress = PartProgress.warmup;

                  draw = (x, y, r, p) -> {
                    Lines.stroke(p*1.6f, SglDrawConst.matrixNet);
                    Lines.circle(x, y, 3*p);

                    Tmp.v1.set(0, 9*p).setAngle(r + 180 + 40*p);
                    Tmp.v2.set(0, 9*p).setAngle(r + 180 - 40*p);

                    SglDraw.drawDiamond(x + Tmp.v1.x, y + Tmp.v1.y, 9*p, 7f*p, Tmp.v1.angle());
                    SglDraw.drawDiamond(x + Tmp.v2.x, y + Tmp.v2.y, 9*p, 7f*p, Tmp.v2.angle());

                    Tmp.v1.set(0, 9*p).setAngle(r + 180 + 100*p);
                    Tmp.v2.set(0, 9*p).setAngle(r + 180 - 100*p);

                    SglDraw.drawDiamond(x + Tmp.v1.x, y + Tmp.v1.y, 9*p, 7f*p, Tmp.v1.angle());
                    SglDraw.drawDiamond(x + Tmp.v2.x, y + Tmp.v2.y, 9*p, 7f*p, Tmp.v2.angle());
                  };
                }},
                new CustomPart() {{
                  x = 0;
                  y = -16f;
                  layer = Layer.effect;
                  progress = PartProgress.warmup.delay(0.7f);

                  draw = (x, y, r, p) -> {
                    Tmp.v1.set(0, 14*p).setAngle(r + 195);
                    Tmp.v2.set(0, 14*p).setAngle(r + 165);

                    SglDraw.gapTri(x + Tmp.v1.x, y + Tmp.v1.y, 3*p, 10, -3, Tmp.v1.angle());
                    SglDraw.gapTri(x + Tmp.v2.x, y + Tmp.v2.y, 3*p, 10, -3, Tmp.v2.angle());

                    Tmp.v1.set(0, 14*p).setAngle(r + 250);
                    Tmp.v2.set(0, 14*p).setAngle(r + 110);

                    SglDraw.gapTri(x + Tmp.v1.x, y + Tmp.v1.y, 4*p, 12f, -4, Tmp.v1.angle());
                    SglDraw.gapTri(x + Tmp.v2.x, y + Tmp.v2.y, 4*p, 12f, -4, Tmp.v2.angle());

                    Tmp.v1.set(0, 14*p).setAngle(r + 305);
                    Tmp.v2.set(0, 14*p).setAngle(r + 55);

                    SglDraw.gapTri(x + Tmp.v1.x, y + Tmp.v1.y, 3*p, 10, -3, Tmp.v1.angle());
                    SglDraw.gapTri(x + Tmp.v2.x, y + Tmp.v2.y, 3*p, 10, -3, Tmp.v2.angle());
                  };
                }}
            );

            bullet = new EmpBulletType() {
              {
                trailLength = 36;
                trailWidth = 2.2f;
                trailColor = SglDrawConst.matrixNet;
                trailRotation = true;
                trailChance = 1;
                hitSize = 8;
                speed = 12;
                lifetime = 40;
                damage = 620;
                range = 480;

                empDamage = 26;

                pierce = true;
                hittable = false;
                reflectable = false;
                pierceArmor = true;
                pierceBuilding = true;
                absorbable = false;

                trailEffect = new MultiEffect(
                    SglFx.lightConeTrail,
                    SglFx.lightCone,
                    SglFx.trailLineLong
                );
                hitEffect = SglFx.lightConeHit;
                hitColor = SglDrawConst.matrixNet;

                intervalBullet = new BulletType() {
                  {
                    damage = 132;
                    speed = 8;
                    hitSize = 3;
                    keepVelocity = false;
                    lifetime = 45;
                    hitColor = SglDrawConst.matrixNet;
                    hitEffect = SglFx.circleSparkMini;

                    despawnHit = true;

                    trailColor = SglDrawConst.matrixNet;
                    trailWidth = 3;
                    trailLength = 23;
                  }

                  @Override
                  public void draw(Bullet b) {
                    super.draw(b);
                    Draw.color(hitColor);
                    SglDraw.drawDiamond(b.x, b.y, 12*b.fout(), 6*b.fout(), b.rotation());
                  }

                  @Override
                  public void drawTrail(Bullet b) {
                    float z = Draw.z();
                    Draw.z(z - 0.0001f);
                    b.trail.draw(trailColor, trailWidth*b.fout());
                    Draw.z(z);
                  }

                  public void removed(Bullet b) {
                    if (trailLength > 0 && b.trail != null && b.trail.size() > 0) {
                      Fx.trailFade.at(b.x, b.y, trailWidth*b.fout(), trailColor, b.trail.copy());
                    }
                  }
                };
                bulletInterval = 4;
              }

              @Override
              public void init(Bullet b) {
                super.init(b);
                TrailMoveLightning l = Pools.obtain(TrailMoveLightning.class, TrailMoveLightning::new);
                l.range = 8f;
                l.maxOff = 7.5f;
                l.chance = 0.4f;

                b.data = l;
              }

              @Override
              public void updateBulletInterval(Bullet b) {
                if (b.timer.get(2, bulletInterval)) {
                  Bullet bull = intervalBullet.create(b, b.x, b.y, b.rotation());
                  bull.vel.scl(b.fout());
                  rand.setSeed(bull.id);
                  float scl = rand.random(3.65f, 5.25f)*(rand.random(1f) > 0.5f ? 1 : -1);
                  float mag = rand.random(2.8f, 5.6f)*b.fout();
                  bull.mover = e -> {
                    e.moveRelative(0f, Mathf.cos(e.time, scl, mag));
                  };
                }
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

              @Override
              public void draw(Bullet b) {
                super.draw(b);
                Draw.color(SglDrawConst.matrixNet);
                Drawf.tri(b.x, b.y, 8, 18, b.rotation());
                for (int i : Mathf.signs) {
                  Drawf.tri(b.x, b.y, 8f, 26f, b.rotation() + 156f*i);
                }
              }

              @Override
              public void update(Bullet b) {
                super.update(b);
                Damage.damage(b.team, b.x, b.y, hitSize, damage*Time.delta);
              }
            };
          }
        },
        new SglWeapon(Sgl.modName + "-aurora_turret") {{
          shake = 4f;
       //   shootSound = Sounds.laser;
          x = 22;
          y = 20;
          shootY = 6;
          rotate = true;
          rotateSpeed = 6;
          recoil = 4;
          recoilTime = 20;
          cooldownTime = 60;
          reload = 20;
          shadow = 25;

          bullet = new LightLaserBulletType() {{
            damage = 425f;
            empDamage = 96;
            lifetime = 24;
            width = 16f;
            length = 480f;
            shootEffect = new MultiEffect(
                SglFx.crossLightSmall,
                SglFx.shootRecoilWave
            );
            colors = new Color[]{SglDrawConst.matrixNetDark, SglDrawConst.matrixNet, Color.white};
            hitColor = colors[0];

            generator.maxSpread = 11.25f;
            generator.minInterval = 6;
            generator.maxInterval = 15;

            lightningMinWidth = 2.2f;
            lightningMaxWidth = 3.8f;
          }};
        }},
        new RelatedWeapon(Sgl.modName + "-lightedge") {
          {
            x = 0;
            y = -22;
            shootY = 0;
            reload = 600;
            mirror = false;
            rotateSpeed = 0;
            shootCone = 0.5f;
            rotate = true;
           // shootSound = Sounds.laserblast;
            ejectEffect = SglFx.railShootRecoil;
            recoilTime = 30;
            shake = 4;

            minWarmup = 0.9f;
            shootWarmupSpeed = 0.03f;

            shoot.firstShotDelay = 80;

            alternativeShoot = new ShootPattern() {
              @Override
              public void shoot(int totalShots, BulletHandler handler) {
                for (int i = 0; i < shots; i++) {
                  handler.shoot(0, 0, Mathf.random(0, 360f), firstShotDelay + i*shotDelay);
                }
              }
            };
            alternativeShoot.shots = 12;
            alternativeShoot.shotDelay = 3;
            alternativeShoot.firstShotDelay = 0;
            useAlternative = isFlying;
            parentizeEffects = true;

            parts.addAll(
                new HaloPart() {{
                  progress = PartProgress.warmup;
                  color = SglDrawConst.matrixNet;
                  layer = Layer.effect;
                  haloRotateSpeed = -1;
                  shapes = 2;
                  triLength = 0f;
                  triLengthTo = 26f;
                  haloRadius = 0;
                  haloRadiusTo = 14f;
                  tri = true;
                  radius = 6;
                }},
                new HaloPart() {{
                  progress = PartProgress.warmup;
                  color = SglDrawConst.matrixNet;
                  layer = Layer.effect;
                  haloRotateSpeed = -1;
                  shapes = 2;
                  triLength = 0f;
                  triLengthTo = 8f;
                  haloRadius = 0;
                  haloRadiusTo = 14f;
                  tri = true;
                  radius = 6;
                  shapeRotation = 180f;
                }},
                new HaloPart() {{
                  progress = PartProgress.warmup;
                  color = SglDrawConst.matrixNet;
                  layer = Layer.effect;
                  haloRotateSpeed = 1;
                  shapes = 2;
                  triLength = 0f;
                  triLengthTo = 12f;
                  haloRadius = 8;
                  tri = true;
                  radius = 8;
                }},
                new HaloPart() {{
                  progress = PartProgress.warmup;
                  color = SglDrawConst.matrixNet;
                  layer = Layer.effect;
                  haloRotateSpeed = 1;
                  shapes = 2;
                  triLength = 0f;
                  triLengthTo = 8f;
                  haloRadius = 8;
                  tri = true;
                  radius = 8;
                  shapeRotation = 180f;
                }},
                new CustomPart() {{
                  layer = Layer.effect;
                  progress = PartProgress.warmup;

                  draw = (x, y, r, p) -> {
                    Draw.color(SglDrawConst.matrixNet);
                    SglDraw.gapTri(x + Angles.trnsx(r + Time.time, 16, 0), y + Angles.trnsy(r + Time.time, 16, 0), 12*p, 42, 12, r + Time.time);
                    SglDraw.gapTri(x + Angles.trnsx(r + Time.time + 180, 16, 0), y + Angles.trnsy(r + Time.time + 180, 16, 0), 12*p, 42, 12, r + Time.time + 180);
                  };
                }}
            );

            Weapon s = this;
            bullet = new ContinuousLaserBulletType() {
              {
                damage = 210;
                lifetime = 180;
                fadeTime = 30;
                length = 720;
                width = 6;
                hitColor = SglDrawConst.matrixNet;
                shootEffect = SglFx.explodeImpWave;
                chargeEffect = SglFx.auroraCoreCharging;
               // chargeSound = Sounds.lasercharge;
                fragBullets = 2;
                fragSpread = 10;
                fragOnHit = true;
                fragRandomSpread = 60;
                fragLifeMin = 0.7f;
                shake = 5;
                incendAmount = 0;
                incendChance = 0;

                drawSize = 620;
                pointyScaling = 0.7f;
                oscMag = 0.85f;
                oscScl = 1.1f;
                frontLength = 70;
                lightColor = SglDrawConst.matrixNet;
                colors = new Color[]{
                    Color.valueOf("8FFFF0").a(0.6f),
                    Color.valueOf("8FFFF0").a(0.85f),
                    Color.valueOf("B6FFF7"),
                    Color.valueOf("D3FDFF")
                };
              }

              @Override
              public void update(Bullet b) {
                super.update(b);
                if (b.owner instanceof Unit u) {
                  u.vel.lerp(0, 0, 0.1f);

                  float bulletX = u.x + Angles.trnsx(u.rotation - 90, x + shootX, y + shootY),
                      bulletY = u.y + Angles.trnsy(u.rotation - 90, x + shootX, y + shootY),
                      angle = u.rotation;

                  b.rotation(angle);
                  b.set(bulletX, bulletY);

                  for (WeaponMount mount : u.mounts) {
                    mount.reload = mount.weapon.reload;
                    if (mount.weapon == s) {
                      mount.recoil = 1;
                    }
                  }

                  if (ejectEffect != null) ejectEffect.at(bulletX, bulletY, angle, b.type.hitColor);
                }
              }

              @Override
              public void draw(Bullet b) {
                float realLength = Damage.findLaserLength(b, length);
                float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime))/fadeTime : 1f);
                float baseLen = realLength*fout;
                float rot = b.rotation();

                for (int i = 0; i < colors.length; i++) {
                  Draw.color(Tmp.c1.set(colors[i]).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)));

                  float colorFin = i/(float) (colors.length - 1);
                  float baseStroke = Mathf.lerp(strokeFrom, strokeTo, colorFin);
                  float stroke = (width + Mathf.absin(Time.time, oscScl, oscMag))*fout*baseStroke;
                  float ellipseLenScl = Mathf.lerp(1 - i/(float) (colors.length), 1f, pointyScaling);

                  Lines.stroke(stroke);
                  Lines.lineAngle(b.x, b.y, rot, baseLen - frontLength, false);

                  //back ellipse
                  Drawf.flameFront(b.x, b.y, divisions, rot + 180f, backLength, stroke/2f);

                  //front ellipse
                  Tmp.v1.trnsExact(rot, baseLen - frontLength);
                  Drawf.flameFront(b.x + Tmp.v1.x, b.y + Tmp.v1.y, divisions, rot, frontLength*ellipseLenScl, stroke/2f);
                }

                Tmp.v1.trns(b.rotation(), baseLen*1.1f);

                Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, lightStroke, lightColor, 0.7f);

                Draw.color(SglDrawConst.matrixNet);

                float step = 1/45f;
                Tmp.v1.set(length, 0).setAngle(b.rotation());
                float dx = Tmp.v1.x;
                float dy = Tmp.v1.y;
                for (int i = 0; i < 45; i++) {
                  if (i*step*length > realLength) break;

                  float lerp = Mathf.clamp(b.time/(fadeTime*step*i))*Mathf.sin(Time.time/2 - i*step*Mathf.pi*6);
                  Draw.alpha(0.4f + 0.6f*lerp);
                  SglDraw.drawDiamond(b.x + dx*step*i, b.y + dy*step*i, 8*fout, 16 + 20*lerp + 80*(1 - fout), b.rotation());
                }
                Draw.reset();
              }
            };

            alternativeBullet = new BulletType() {
              {
                pierceArmor = true;
                hitShake = 6;
                damage = 280;
                splashDamage = 420;
                splashDamageRadius = 32;
                absorbable = false;
                hittable = true;
                speed = 10;
                lifetime = 120;
                homingRange = 450;
                homingPower = 0.25f;
                hitColor = SglDrawConst.matrixNet;
                hitEffect = new MultiEffect(
                    SglFx.explodeImpWave,
                    SglFx.diamondSpark
                );

                trailLength = 32;
                trailWidth = 3;
                trailColor = SglDrawConst.matrixNet;
                trailEffect = new MultiEffect(
                    SglFx.movingCrystalFrag,
                    Fx.colorSparkBig
                );
                trailRotation = true;
                trailInterval = 4;

                despawnHit = true;

                homingDelay = 30;

                fragBullet = new BulletType() {
                  {
                    collides = false;
                    absorbable = false;

                    splashDamage = 260;
                    splashDamageRadius = 24;
                    speed = 1.2f;
                    lifetime = 64;

                    hitShake = 4;
                    hitSize = 3;

                    despawnHit = true;
                    hitEffect = new MultiEffect(
                        SglFx.explodeImpWaveSmall,
                        SglFx.diamondSpark
                    );
                    hitColor = SglDrawConst.matrixNet;

                    trailColor = SglDrawConst.matrixNet;
                    trailEffect = SglFx.glowParticle;
                    trailRotation = true;
                    trailInterval = 15f;

                    fragBullet = new LightningBulletType() {{
                      lightningLength = 14;
                      lightningLengthRand = 4;
                      damage = 24;
                    }};
                    fragBullets = 1;
                  }

                  @Override
                  public void draw(Bullet b) {
                    Draw.color(hitColor);
                    float fout = b.fout(Interp.pow3Out);
                    Fill.circle(b.x, b.y, 5f*fout);
                    Draw.color(Color.black);
                    Fill.circle(b.x, b.y, 2.6f*fout);
                  }
                };
                fragBullets = 3;
                fragLifeMin = 0.7f;
              }

              @Override
              public void updateHoming(Bullet b) {
                if (Mathf.chanceDelta(0.3f*b.vel.len()/speed)) {
                  Fx.colorSpark.at(b.x, b.y, b.rotation(), b.type.hitColor);
                }

                if (b.time < homingDelay) {
                  b.vel.lerpDelta(0, 0, 0.06f);
                } else if (homingPower > 0.0001f && b.time >= homingDelay) {
                  float realAimX = b.aimX < 0 ? b.x : b.aimX;
                  float realAimY = b.aimY < 0 ? b.y : b.aimY;

                  Teamc target;
                  if (b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)) {
                    target = b.aimTile.build;
                  } else {
                    target = Units.closestTarget(b.team, realAimX, realAimY, homingRange,
                        e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
                        t -> t != null && collidesGround && !b.hasCollided(t.id));
                  }

                  if (target != null) {
                    float v = Mathf.lerpDelta(b.vel.len(), speed, 0.08f);
                    b.vel.setLength(v);
                    b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower*(v/speed)*Time.delta*50f));
                  } else {
                    b.vel.lerpDelta(0, 0, 0.06f);
                  }
                }
              }

              @Override
              public void draw(Bullet b) {
                super.draw(b);
                Draw.color(SglDrawConst.matrixNet);
                Drawf.tri(b.x, b.y, 8, 24, b.rotation());
                Drawf.tri(b.x, b.y, 8, 10, b.rotation() + 180);

                Tmp.v1.set(1, 0).setAngle(b.rotation());
                SglDraw.gapTri(b.x + Tmp.v1.x*8*b.fout(), b.y + Tmp.v1.y*3*b.fout(), 16, 24, 18, b.rotation());
                SglDraw.gapTri(b.x + Tmp.v1.x*2*b.fout(), b.y - Tmp.v1.y*3*b.fout(), 12, 20, 16, b.rotation());
                SglDraw.gapTri(b.x - Tmp.v1.x*2*b.fout(), b.y - Tmp.v1.y*5*b.fout(), 8, 14, 10, b.rotation());
              }

              public void hitEntity(Bullet b, Hitboxc entity, float health) {
                if (entity instanceof Unit unit && unit.shield > 0) {
                  float damageShield = Math.min(Math.max(unit.shield, 0), b.type.damage*1.25f);
                  unit.shield -= damageShield;
                  Fx.colorSparkBig.at(b.x, b.y, b.rotation(), SglDrawConst.matrixNet);
                }
                super.hitEntity(b, entity, health);
              }
            };
          }

          @Override
          public void draw(Unit unit, WeaponMount mount) {
            super.draw(unit, mount);
            Tmp.v1.set(0, y).rotate(unit.rotation - 90);
            float dx = unit.x + Tmp.v1.x;
            float dy = unit.y + Tmp.v1.y;

            Lines.stroke(1.6f*(mount.charging ? 1 : mount.warmup*(1 - mount.recoil)), SglDrawConst.matrixNet);
            Draw.alpha(0.7f*mount.warmup*(1 - unit.elevation));
            float disX = Angles.trnsx(unit.rotation - 90, 3*mount.warmup, 0);
            float disY = Angles.trnsy(unit.rotation - 90, 3*mount.warmup, 0);

            Tmp.v1.set(0, 720).rotate(unit.rotation - 90);
            float angle = Tmp.v1.angle();
            float distX = Tmp.v1.x;
            float distY = Tmp.v1.y;

            Lines.line(dx + disX, dy + disY, dx + distX + disX, dy + distY + disY);
            Lines.line(dx - disX, dy - disY, dx + distX - disX, dy + distY - disY);
            float step = 1/30f;
            float rel = (1 - mount.reload/reload)*mount.warmup*(1 - unit.elevation);
            for (float i = 0.001f; i <= 1; i += step) {
              Draw.alpha(rel > i ? 1 : Mathf.maxZero(rel - (i - step))/step);
              Drawf.tri(dx + distX*i, dy + distY*i, 3, 2.598f, angle);
            }

            Draw.reset();

            Draw.color(SglDrawConst.matrixNet);
            float relLerp = mount.charging ? 1 : 1 - mount.reload/reload;
            float edge = Math.max(relLerp, mount.recoil*1.25f);
            Lines.stroke(0.8f*edge);
            Draw.z(Layer.bullet);
            SglDraw.dashCircle(dx, dy, 10, 4, 240, Time.time*0.8f);
            Lines.stroke(edge);
            Lines.circle(dx, dy, 8);
            Fill.circle(dx, dy, 5*relLerp);

            SglDraw.drawDiamond(dx, dy, 6 + 12*relLerp, 3*relLerp, Time.time);
            SglDraw.drawDiamond(dx, dy, 5 + 10*relLerp, 2.5f*relLerp, -Time.time*0.87f);
          }

          @Override
          public void update(Unit unit, WeaponMount mount) {
            float axisX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
                axisY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);

            if (mount.charging) mount.reload = mount.weapon.reload;

            if (unit.isFlying()) {
              mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - unit.rotation;
              mount.rotation = mount.targetRotation;
            } else {
              mount.rotation = 0;
            }

            if (mount.warmup < 0.01f) {
              mount.reload = Math.max(mount.reload - 0.2f*Time.delta, 0);
            }

            super.update(unit, mount);
          }
        }
    );
  }

  @Override
  public void init() {
    super.init();

    omniMovement = true;
  }
}
