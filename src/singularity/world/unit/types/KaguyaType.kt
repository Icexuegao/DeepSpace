package singularity.world.unit.types

import arc.Core
import arc.func.Boolf
import arc.func.Func2
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Mathf
import arc.math.Rand
import arc.math.geom.Vec2
import arc.scene.ui.layout.Collapser
import arc.scene.ui.layout.Table
import arc.util.Interval
import arc.util.Time
import arc.util.Tmp
import ice.library.struct.AttachedProperty
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.content.Fx
import mindustry.entities.Units
import mindustry.entities.bullet.BulletType
import mindustry.entities.bullet.LaserBulletType
import mindustry.entities.bullet.PointLaserBulletType
import mindustry.entities.effect.MultiEffect
import mindustry.entities.part.RegionPart
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Trail
import mindustry.type.UnitType
import mindustry.type.Weapon
import mindustry.type.weapons.PointDefenseWeapon
import mindustry.ui.Styles
import mindustry.world.meta.BlockFlag
import singularity.Sgl
import singularity.graphic.MathRenderer
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.ui.UIUtils
import singularity.util.MathTransform
import singularity.world.SglFx
import singularity.world.blocks.turrets.MultiTrailBulletType
import singularity.world.draw.part.CustomPart
import singularity.world.unit.DataWeapon
import singularity.world.unit.SglUnitEntity
import singularity.world.unit.SglUnitType
import singularity.world.unit.SglWeapon
import singularity.world.unit.abilities.MirrorFieldAbility
import kotlin.math.max

class KaguyaType : SglUnitType<SglUnitEntity>("kaguya", SglUnitEntity::class.java) {
  companion object {
    private val rand = Rand()
  }

  init {
    bundle{
      desc(zh_CN,"辉夜","搭载光束引擎的巨型攻击舰,具有强大的火力和相当灵活的机动性,其武装足以将绝大多数防线夷为平地")
    }
    armor = 20f
    speed = 1.1f
    accel = 0.06f
    drag = 0.04f
    rotateSpeed = 1.5f
    faceTarget = true
    flying = true
    health = 45000f
    lowAltitude = true
    hitSize = 70f
    targetFlags = BlockFlag.all
    drawShields = false

    engineSize = 0f

    abilities.addAll(object : MirrorFieldAbility() {
      init {
        strength = 350f
        maxShield = 15800f
        recoverSpeed = 8f
        cooldown = 6500f
        minAlbedo = 1f
        maxAlbedo = 1f
        rotation = false

        shieldArmor = 22f

        nearRadius = 160f

        val a: Func2<Float?, Float?, ShieldShape?> = Func2 { ofx: Float?, ofy: Float? ->
          object : ShieldShape(6, 0f, 0f, 0f, 48f) {
            init {
              movement = object : ShapeMove() {
                init {
                  x = ofx!!
                  y = ofy!!
                  rotateSpeed = 0.35f

                  childMoving = object : ShapeMove() {
                    init {
                      rotateSpeed = -0.2f
                    }
                  }
                }
              }
            }
          }
        }
        val b: Func2<Float?, Float?, ShieldShape?> = Func2 { ofx: Float?, ofy: Float? ->
          object : ShieldShape(5, 0f, 0f, 0f, 48f) {
            init {
              movement = object : ShapeMove() {
                init {
                  x = ofx!!
                  y = ofy!!
                  rotateSpeed = -0.25f

                  childMoving = object : ShapeMove() {
                    init {
                      rotateSpeed = 0.15f
                    }
                  }
                }
              }
            }
          }
        }

        shapes.addAll(
          object : ShieldShape(10, 0f, 0f, 0f, 112f) {
            init {
              movement = object : ShapeMove() {
                init {
                  rotateSpeed = -0.1f
                }
              }
            }
          }, a.get(90f, 0f), a.get(-90f, 0f), a.get(0f, 90f), a.get(0f, -90f), b.get(100f, 0f), b.get(-100f, 0f), b.get(0f, 100f), b.get(0f, -100f)
        )
      }
    })

    val laser: Func2<Float?, Float?, Weapon?> = Func2 { dx: Float?, dy: Float? ->
      object : SglWeapon(Sgl.modName + "-kaguya_laser") {
        init {
          this.x = dx!!
          this.y = dy!!
          mirror = true
          reload = 30f
          recoil = 4f
          recoilTime = 30f
          shadow = 4f
          rotate = true
          layerOffset = 0.1f
          shootSound = Sounds.shootLaser

          shake = 3f

          bullet = object : LaserBulletType() {
            init {
              damage = 165f
              lifetime = 20f
              sideAngle = 90f
              sideWidth = 1.25f
              sideLength = 15f
              width = 16f
              length = 450f
              hitEffect = Fx.circleColorSpark
              shootEffect = Fx.colorSparkBig
              colors = arrayOf<Color?>(SglDrawConst.matrixNetDark, SglDrawConst.matrixNet, Color.white)
              hitColor = colors[0]
            }
          }
        }
      }
    }

    weapons.addAll(laser.get(19.25f, 16f), laser.get(13.5f, 33.5f), object : SglWeapon(Sgl.modName + "-kaguya_cannon") {
      init {
        x = 30.5f
        y = -3.5f
        mirror = true

        cooldownTime = 120f
        recoil = 0f
        recoilTime = 120f
        reload = 90f
        shootX = 2f
        shootY = 22f
        rotate = true
        rotationLimit = 30f
        rotateSpeed = 10f

        shake = 5f

        layerOffset = 0.1f

        shootSound = Sounds.blockExplode1Alt

        shoot.shots = 3
        shoot.shotDelay = 10f

        parts.addAll(
          object : RegionPart("_shooter") {
            init {
              heatColor = SglDrawConst.matrixNet
              heatProgress = PartProgress.heat
              moveY = -6f
              progress = PartProgress.recoil
            }
          }, RegionPart("_body")
        )

        bullet = object : MultiTrailBulletType() {
          init {
            speed = 6f
            lifetime = 75f
            damage = 180f
            splashDamage = 240f
            splashDamageRadius = 36f

            hitEffect = MultiEffect(
              Fx.shockwave, Fx.bigShockwave, SglFx.impactWaveSmall, SglFx.spreadSparkLarge, SglFx.diamondSparkLarge
            )
            despawnHit = true

            smokeEffect = Fx.shootSmokeSmite
            shootEffect = SglFx.railShootRecoil
            hitColor = SglDrawConst.matrixNet
            trailColor = SglDrawConst.matrixNet
            hitSize = 8f
            trailLength = 36
            trailWidth = 4f

            hitShake = 4f
            hitSound = Sounds.explosion
            hitSoundVolume = 3.5f

            trailEffect = SglFx.trailParticle
            trailChance = 0.5f

            fragBullet = EdgeFragBullet()
            fragBullets = 4
            fragLifeMin = 0.7f
            fragOnHit = true
            fragOnAbsorb = true
          }

          override fun draw(b: Bullet) {
            super.draw(b)
            Drawf.tri(b.x, b.y, 12f, 30f, b.rotation())
            Drawf.tri(b.x, b.y, 12f, 12f, b.rotation() + 180)
          }
        }
      }
    }, object : PointDefenseWeapon(Sgl.modName + "-kaguya_point_laser") {
      init {
        x = 30.5f
        y = -3.5f
        mirror = true

        recoil = 0f
        reload = 12f
        targetInterval = 0f
        targetSwitchInterval = 0f

        layerOffset = 0.2f

        bullet = object : BulletType() {
          init {
            damage = 62f
            rangeOverride = 420f
          }
        }
      }
    }, object : DataWeapon(Sgl.modName + "-lightedge") {
      init {
        x = 0f
        y = -14f
        minWarmup = 0.98f
        shootWarmupSpeed = 0.02f
        linearWarmup = false
        rotate = false
        shootCone = 10f
        rotateSpeed = 10f
        shootY = 80f
        reload = 30f
        recoilTime = 60f
        recoil = 2f
        recoilPow = 0f
        targetSwitchInterval = 300f
        targetInterval = 0f

        mirror = false
        continuous = true
        alwaysContinuous = true

        val s: Weapon = this

        bullet = object : PointLaserBulletType() {
          init {
            damage = 240f
            damageInterval = 5f
            rangeOverride = 450f
            shootEffect = SglFx.railShootRecoil
            hitColor = SglDrawConst.matrixNet
            hitEffect = SglFx.diamondSparkLarge
            shake = 5f
          }

          override fun continuousDamage(): Float {
            return damage * (60 / damageInterval)
          }

          override fun update(b: Bullet) {
            super.update(b)

            val owner = b.owner
            if (owner is Unit) {
              for (mount in owner.mounts) {
                if (mount.weapon === s) {
                  val bulletX: Float = owner.x + Angles.trnsx(owner.rotation - 90, x + shootX, y + shootY)
                  val bulletY: Float = owner.y + Angles.trnsy(owner.rotation - 90, x + shootX, y + shootY)

                  b.set(bulletX, bulletY)
                  Tmp.v2.set(mount.aimX - bulletX, mount.aimY - bulletY)
                  val angle = Mathf.clamp(Tmp.v2.angle() - owner.rotation, -shootCone, shootCone)
                  Tmp.v2.setAngle(owner.rotation).rotate(angle)

                  Tmp.v1.set(b.aimX - bulletX, b.aimY - bulletY).lerpDelta(Tmp.v2, 0.1f).clampLength(80f, range)

                  b.aimX = bulletX + Tmp.v1.x
                  b.aimY = bulletY + Tmp.v1.y

                  shootEffect.at(bulletX, bulletY, Tmp.v1.angle(), hitColor)
                }
              }
            }
          }

          override fun draw(b: Bullet) {
            super.draw(b)
            Draw.draw(Draw.z()) {
              Draw.color(hitColor)
              MathRenderer.setDispersion(0.1f)
              MathRenderer.setThreshold(0.4f, 0.6f)

              rand.setSeed(b.id.toLong())
              repeat((0..2).count()) {
                MathRenderer.drawSin(
                  b.x, b.y, b.aimX, b.aimY, rand.random(4f, 6f) * b.fslope(), rand.random(360f, 720f), rand.random(360f) - Time.time * rand.random(4f, 7f)
                )
              }
            }
          }
        }

        parts.addAll(object : CustomPart() {
          init {
            layer = Layer.effect
            progress = PartProgress.warmup
            draw = Drawer { x: Float, y: Float, _: Float, p: Float ->
              Draw.color(SglDrawConst.matrixNet)
              Fill.circle(x, y, 8f)
              Lines.stroke(1.4f)
              SglDraw.dashCircle(x, y, 12f, Time.time)

              Draw.draw(Draw.z()) {
                MathRenderer.setThreshold(0.65f, 0.8f)
                MathRenderer.setDispersion(1f)
                MathRenderer.drawCurveCircle(x, y, 15f, 2, 6f, Time.time)
                MathRenderer.setDispersion(0.6f)
                MathRenderer.drawCurveCircle(x, y, 16f, 3, 6f, -Time.time)
              }

              Draw.alpha(0.65f)
              SglDraw.gradientCircle(x, y, 20f, 12f, 0f)

              Draw.alpha(1f)
              SglDraw.drawDiamond(x, y, 24 + 18 * p, 3 + 3 * p, Time.time * 1.2f)
              SglDraw.drawDiamond(x, y, 30 + 18 * p, 4 + 4 * p, -Time.time * 1.2f)
            }
          }
        })
      }

      val subBull: BulletType = object : BulletType() {
        init {
          damage = 62f
          speed = 5f
          homingDelay = 30f
          homingPower = 0.1f
          homingRange = 460f
          lifetime = 150f
          hitSize = 2f
          keepVelocity = false
          pierceArmor = true

          trailColor = SglDrawConst.matrixNet
          hitColor = trailColor
          trailWidth = 1f
          trailLength = 38

          hitEffect = SglFx.neutronWeaveMicro
          despawnEffect = SglFx.constructSpark
        }

        override fun draw(b: Bullet) {
          super.draw(b)
          Draw.color(hitColor)
          Fill.circle(b.x, b.y, hitSize / 2)
        }

        override fun updateHoming(b: Bullet) {
          if (b.time > homingDelay) {
            val realAimX = if (b.aimX < 0) b.x else b.aimX
            val realAimY = if (b.aimY < 0) b.y else b.aimY

            val target = if (b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team !== b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)) {
              b.aimTile.build
            } else {
              Units.closestTarget(b.team, realAimX, realAimY, homingRange, Boolf { e: Unit? -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id) }, Boolf { t: Building? -> t != null && collidesGround && !b.hasCollided(t.id) })
            }

            if (target != null) {
              Tmp.v1.set(target).sub(b).setLength(homingPower).scl(Time.delta)
              if (b.vel.len() < speed) b.vel.add(Tmp.v1)
              b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower * Time.delta * 50f))
            }
          }
        }
      }
      var DataWeaponMount.SHOOTERS: Array<Shooter?> by AttachedProperty(arrayOfNulls(3))
      var DataWeaponMount.TIMER: Interval by AttachedProperty(Interval())
      override fun init(unit: Unit?, mount: DataWeaponMount) {
        val shooters: Array<Shooter?> = arrayOfNulls(3)
        for (i in shooters.indices) {
          shooters[i] = Shooter()
        }
        mount.SHOOTERS = shooters
      }

      override fun update(unit: Unit?, mount: DataWeaponMount) {
        val shooters: Array<Shooter?> = mount.SHOOTERS
        val timer: Interval = mount.TIMER
        for (shooter in shooters) {
          val v: Vec2 = MathTransform.fourierSeries(Time.time, *shooter!!.param).scl(mount.warmup)
          Tmp.v1.set(mount.weapon.x, mount.weapon.y).rotate(unit!!.rotation - 90)
          shooter.x = Tmp.v1.x + v.x
          shooter.y = Tmp.v1.y + v.y
          shooter.trail.update(unit.x + shooter.x, unit.y + shooter.y)
        }

        if (mount.warmup > 0.8f && timer.get(120f)) {
          for (i in shooters.indices) {
            val shooter = shooters[i]
            Time.run(i * 40f) {
              SglFx.explodeImpWaveMini.at(unit!!.x + shooter!!.x, unit.y + shooter.y, SglDrawConst.matrixNet)
              for (l in 0..9) {
                Time.run(l * 4f) {
                  val v: Vec2 = MathTransform.fourierSeries(Time.time, *shooter.param).scl(mount.warmup)
                  subBull.create(unit, unit.team, unit.x + shooter.x, unit.y + shooter.y, Angles.angle(v.x, v.y), 0.2f)
                }
              }
            }
          }
        }
      }

      override fun addStats(u: UnitType?, t: Table) {
        super.addStats(u, t)
        t.row()

        val ic = Table()
        UIUtils.buildAmmo(ic, subBull)
        val coll = Collapser(ic, true)
        coll.setDuration(0.1f)

        t.table { it ->
          it.left().defaults().left()
          it.add(Core.bundle.format("bullet.interval", 15))
          it.button(Icon.downOpen, Styles.emptyi) { coll.toggle(false) }.update { i -> i.style.imageUp = (if (!coll.isCollapsed) Icon.upOpen else Icon.downOpen) }.size(8f).padLeft(16f).expandX()
        }
        t.row()
        t.add(coll).padLeft(16f)
      }

      override fun shoot(unit: Unit, mount: DataWeaponMount?, shootX: Float, shootY: Float, rotation: Float) {
        val mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y)
        val mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y)

        SglFx.shootRecoilWave.at(shootX, shootY, rotation, SglDrawConst.matrixNet)
        SglFx.impactWave.at(shootX, shootY, SglDrawConst.matrixNet)

        SglFx.impactWave.at(mountX, mountY, SglDrawConst.matrixNet)
        SglFx.crossLight.at(mountX, mountY, SglDrawConst.matrixNet)
        mount!!.SHOOTERS.forEach {
          SglFx.impactWaveSmall.at(mountX + it!!.x, mountY + it.y)
        }
      }

      override fun draw(unit: Unit, mount: DataWeaponMount) {

        Draw.z(Layer.effect)

        val mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y)
        val mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y)

        val bulletX = mountX + Angles.trnsx(unit.rotation - 90, shootX, shootY)
        val bulletY = mountY + Angles.trnsy(unit.rotation - 90, shootX, shootY)

        Draw.color(SglDrawConst.matrixNet)
        SglDraw.drawDiamond(bulletX, bulletY, mount.recoil * 18 + 10, mount.recoil * 4, Time.time * 1.2f)
        SglDraw.drawDiamond(bulletX, bulletY, mount.recoil * 24 + 12, mount.recoil * 6, -Time.time)

        val lerp = max(mount.recoil, 0.5f * mount.warmup)
        Fill.circle(bulletX, bulletY, 6 * lerp)
        Draw.color(Color.white)
        Fill.circle(bulletX, bulletY, 3 * lerp)
        mount.SHOOTERS.forEach {
          Draw.color(SglDrawConst.matrixNet)
          it!!.trail.draw(SglDrawConst.matrixNet, 3 * mount.warmup)

          var drawx = unit.x + it.x
          var drawy = unit.y + it.y
          Fill.circle(drawx, drawy, 4 * mount.warmup)
          Lines.stroke(0.65f * mount.warmup)
          SglDraw.dashCircle(drawx, drawy, 6f * mount.warmup, 4, 180f, Time.time)
          SglDraw.drawDiamond(drawx, drawy, 4 + 8 * mount.warmup, 3 * mount.warmup, Time.time * 1.45f)
          SglDraw.drawDiamond(drawx, drawy, 8 + 10 * mount.warmup, 3.6f * mount.warmup, -Time.time * 1.45f)

          Lines.stroke(3 * lerp, SglDrawConst.matrixNet)
          Lines.line(drawx, drawy, bulletX, bulletY)
          Lines.stroke(1.75f * lerp, Color.white)
          Lines.line(drawx, drawy, bulletX, bulletY)

          Draw.alpha(0.5f)
          Lines.line(mountX, mountY, drawx, drawy)
        }
      }
    })
  }

  class Shooter {
    val trail: Trail = Trail(45)
    val param: FloatArray = FloatArray(9)

    var x: Float = 0f
    var y: Float = 0f

    init {
      for (d in 0..2) {
        param[d * 3] = Mathf.random(0.5f, 3f) / (d + 1) * Mathf.randomSign()
        param[d * 3 + 1] = Mathf.random(0f, 360f)
        param[d * 3 + 2] = Mathf.random(18f, 48f) / ((d + 1) * (d + 1))
      }
    }
  }
}