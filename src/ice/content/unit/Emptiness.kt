package ice.content.unit

import arc.func.Func2
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Mathf
import arc.scene.style.TextureRegionDrawable
import arc.util.Time
import arc.util.pooling.Pools
import ice.content.IUnitTypes.lightning
import ice.content.block.turret.TurretBullets
import ice.entities.bullet.BlastLaser
import ice.entities.bullet.MultiTrailBulletType
import ice.entities.bullet.SglEmpBulletType
import ice.entities.bullet.base.TrailMoveLightning
import ice.entities.effect.MultiEffect
import ice.graphics.IceColor
import ice.graphics.lightnings.LightningVertex
import ice.graphics.lightnings.generator.RandomGenerator
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.weapon.IceWeapon
import ice.world.content.unit.weapon.MayflyWeapon
import ice.world.draw.part.CustomPart
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.entities.Units
import mindustry.entities.part.DrawPart
import mindustry.entities.units.WeaponMount
import mindustry.gen.Bullet
import mindustry.gen.Sounds
import mindustry.gen.Teamc
import mindustry.gen.Unit
import mindustry.graphics.Layer
import mindustry.graphics.Trail
import mindustry.world.meta.BlockFlag
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.util.MathTransform
import singularity.world.SglFx
import singularity.world.particles.SglParticleModels
import singularity.world.unit.abilities.MirrorArmorAbility
import kotlin.math.abs
import kotlin.math.max

class Emptiness : IceUnitType("emptiness") {
  init {
    bundle {
      desc(zh_CN, "虚宿", "巨型光棱战列舰,光束反应堆的最终产物,火力至上原则的最终答案,拥有强大的能量护盾")
    }
    armor = 9f
    speed = 0.8f
    accel = 0.065f
    drag = 0.05f
    rotateSpeed = 0.8f
    faceTarget = true
    health = 102500f
    lowAltitude = true
    flying = true
    hitSize = 85f
    targetFlags = BlockFlag.allLogic
    drawShields = false
    requirements(Items.phaseFabric, 200, Items.surgeAlloy, 280)
    engineSize = 0f
    setEnginesMirror(object : UnitEngine(-15f, -60f, 8f, -90f) {
      override fun draw(unit: Unit) = kotlin.Unit
    }, object : UnitEngine(-40f, -50f, 8f, -90f) {
      override fun draw(unit: Unit) = kotlin.Unit
    })
    abilities.addAll(MirrorArmorAbility().apply {
      strength = 240f
      maxShield = 8200f
      recoverSpeed = 3f
      cooldown = 5500f
      minAlbedo = 0.5f
      maxAlbedo = 0.8f
      shieldArmor = 10f
    })
    val turretBullet = object : SglEmpBulletType() {
      init {
        damage = 420f
        empDamage = 37f
        pierceCap = 4
        pierceBuilding = true
        laserAbsorb = true
        speed = 16f
        lifetime = 35f
        hitSize = 6f
        trailEffect = MultiEffect(SglFx.trailLineLong, Fx.colorSparkBig)
        trailChance = 1f
        trailRotation = true

        hitSound = Sounds.shootCorvus

        hitEffect = Fx.circleColorSpark
        hitColor = IceColor.matrixNet

        shootEffect = Fx.circleColorSpark

        despawnHit = true

        trailLength = 38
        trailWidth = 4f
        trailColor = IceColor.matrixNet
      }

      override fun init(b: Bullet) {
        super.init(b)
        val l = Pools.obtain(TrailMoveLightning::class.java) { TrailMoveLightning() }
        l.chance = 0.5f
        l.maxOff = 6f
        l.range = 12f
        b.data = l
      }

      override fun updateTrail(b: Bullet) {
        if (!Vars.headless && trailLength > 0) {
          if (b.trail == null) {
            b.trail = Trail(trailLength)
          }
          b.trail.length = trailLength

          if (b.data !is TrailMoveLightning) return
          var data = b.data as TrailMoveLightning
          data.update()
          SglDraw.drawTransform(
            b.x, b.y, 0f, data.off, b.rotation()
          ) { x: Float, y: Float, _: Float -> b.trail.update(x, y) }
        }
      }

      override fun removed(b: Bullet) {
        super.removed(b)
        if (b.data is TrailMoveLightning) {
          Pools.free(b.data)
        }
      }
    }
    setWeapon("turret") {
      x = 17f
      y = 26.5f
      rotate = true
      shootCone = 6f
      rotateSpeed = 5f
      recoilTime = 45f
      recoil = 6f
      shake = 4f
      reload = 30f
      shootSound = Sounds.shootCorvus
      bullet = turretBullet
    }
    setWeapon("turret") {
      x = 22f
      y = -1f
      rotate = true
      shootCone = 6f
      rotateSpeed = 5f
      recoilTime = 45f
      recoil = 6f
      shake = 4f
      reload = 30f
      shootSound = Sounds.shootCorvus
      bullet = turretBullet
    }
    setWeapon("cannon") {
      x = 27f
      y = -35f
      rotate = true
      shootCone = 6f
      rotateSpeed = 3.5f
      recoilTime = 60f
      recoil = 6f
      shootSound = Sounds.shootBeamPlasma
      shake = 5f
      reload = 60f
      bullet = object : MultiTrailBulletType() {
        init {
          damage = 60f
          splashDamage = 560f
          splashDamageRadius = 18f

          pierceCap = 5
          pierceBuilding = true

          hitEffect = mindustry.entities.effect.MultiEffect(SglFx.diamondSparkLarge, SglFx.spreadSparkLarge)
          despawnEffect = SglFx.explodeImpWaveSmall

          hitShake = 6f
          hitSound = Sounds.shootBeamPlasma

          speed = 10f
          lifetime = 60f
          trailEffect = mindustry.entities.effect.MultiEffect(
            Fx.colorSparkBig, SglFx.movingCrystalFrag, SglFx.polyParticle
          )
          trailChance = 0.3f
          trailColor = IceColor.matrixNet
          trailRotation = true

          shootEffect = SglFx.shootRail
          smokeEffect = Fx.shootSmokeSmite
          hitColor = IceColor.matrixNet

          trailLength = 34
          trailWidth = 4f
          hitSize = 6f
        }

        override fun draw(b: Bullet) {
          super.draw(b)

          Draw.color(hitColor)
          SglDraw.gapTri(b.x, b.y, 12f, 28f, -10f, b.rotation())
        }
      }
    }
    setWeaponT<MayflyWeapon>("mayfly") {
      x = 58.5f
      y = -13.75f
      baseRotation = -45f
    }
    setWeaponT<MayflyWeapon>("mayfly") {
      x = 57.5f
      y = -37.75f
      baseRotation = -90f
      delay = 20f
    }
    setWeaponT<MayflyWeapon>("mayfly") {
      x = 52.5f
      y = -65.75f
      baseRotation = -135f
      delay = 40f
    }
    weapons.add(object : IceWeapon("ice-lightedge") {
      init {
        x = 0f
        y = -28f
        mirror = false
        recoil = 0f
        targetSwitchInterval = 80f
        shootSound = Sounds.shootLaser
        reload = 750f
        cooldownTime = 30f
        minWarmup = 0.95f
        linearWarmup = false
        shootWarmupSpeed = 0.014f


        bullet = object : BlastLaser() {
          init {
            damage = 260f
            damageInterval = 5f
            blastDelay = 38f
            rangeOverride = 600f
            splashDamage = 3280f
            splashDamageRadius = 120f
            empDamage = 530f
            empRange = 120f
            lifetime = 245f
            hitSize = 12f

            intervalBullet = TurretBullets.溢出能量.copy()
            intervalBullet.damage = 160f
            intervalBullet.splashDamage = 160f
            intervalBullet.splashDamageRadius = 45f
            intervalBullets = 2
            intervalDelay = 3f
            intervalRandomSpread = 360f

            laserEffect = mindustry.entities.effect.MultiEffect(
              SglFx.laserBlastWeaveLarge, SglFx.circleSparkLarge, SglFx.impactBubbleBig
            )
            shootEffect = mindustry.entities.effect.MultiEffect(
              SglFx.shootCrossLightLarge, SglFx.explodeImpWaveBig, SglFx.impactWaveBig, SglFx.impactBubble
            )
            hitEffect = mindustry.entities.effect.MultiEffect(Fx.colorSparkBig, SglFx.diamondSparkLarge)

            hitColor = IceColor.matrixNet

            fragBullets = 3
            fragSpread = 120f
            fragRandomSpread = 72f
            fragBullet = BlastLaser().apply {
              damage = 120f
              damageInterval = 5f

              rangeOverride = 360f
              splashDamage = 1400f
              splashDamageRadius = 60f
              empDamage = 220f
              empRange = 60f
              lifetime = 186f
              hitSize = 9f

              hitEffect = MultiEffect(Fx.circleColorSpark, SglFx.diamondSparkLarge)

              blackZone = false

              laserEffect = SglFx.explodeImpWaveLaserBlase
              val branch = RandomGenerator()
              val g = RandomGenerator().apply {
                maxLength = 140f
                maxDeflect = 55f
                branchChance = 0.2f
                minBranchStrength = 0.8f
                maxBranchStrength = 1f
                branchMaker = Func2 { vert: LightningVertex?, strength: Float? ->
                  branch.maxLength = 60 * strength!!
                  branch.originAngle = vert!!.angle + Mathf.random(-90, 90)
                  branch
                }
              }
              fragBullets = 8
              fragBullet = lightning(128f, 32f, 62f, 5.2f, IceColor.matrixNet) { b: Bullet? ->
                g.originAngle = b!!.rotation()
                g
              }
              fragBullet.rangeOverride = 120f
            }
          }

          override fun createSplashDamage(b: Bullet, x: Float, y: Float) {
            super.createSplashDamage(b, x, y)

            Angles.randLenVectors(
              System.nanoTime(), Mathf.random(15, 22), 4f, 6.5f
            ) { dx: Float, dy: Float ->
              SglParticleModels.floatParticle.create(x, y, hitColor, dx, dy, Mathf.random(5.25f, 7f))
            }
          }
        }
        parts.addAll(CustomPart { x: Float, y: Float, r: Float, p: Float ->
          Draw.color(IceColor.matrixNet)
          val dx = Angles.trnsx(r, 1f, 0f)
          val dy = Angles.trnsy(r, 1f, 0f)

          for (i in 0..3) {
            val len = 20 + i * 25 - (i % 2) * 6
            val rx = x + dx * len
            val ry = y + dy * len

            SglDraw.gapTri(
              rx, ry, Mathf.absin(Time.time / 4 - i * Mathf.pi, 1f, (10 - 2 * i) * p), (10 + (6 + (i % 2) * 6) * p) - i, (if (i % 2 == 0) -1 else 1) * (5 + 4 * p - i), r
            )
          }
          SglDraw.drawDiamond(x, y, 44 + 20 * p, 8 + 4 * p, Time.time)
          SglDraw.drawDiamond(x, y, 38 + 14 * p, 6 + 4 * p, -Time.time * 1.2f)
          SglDraw.drawDiamond(x, y, 32 + 8 * p, 4 + 3 * p, Time.time * 1.3f)
          Fill.circle(x, y, 12f)
          Draw.color(Color.white)
          Fill.circle(x, y, 9f)
          Draw.color(Color.black)
          Fill.circle(x, y, 7.5f)
        }.apply {
          layer = Layer.effect
          progress = DrawPart.PartProgress.warmup
        })
      }

      override fun findTarget(unit: Unit, x: Float, y: Float, range: Float, air: Boolean, ground: Boolean): Teamc? {
        return Units.bestTarget(unit.team, x, y, range, { _: Unit -> unit.checkTarget(air, ground) }, { _: Teamc? -> ground }, { _: Unit, _: Float, _: Float ->
          1f
        })
      }

      override fun draw(unit: Unit, mount: WeaponMount) {
        val x: Float = unit.x + Angles.trnsx(unit.rotation() - 90, mount.weapon.x, mount.weapon.y)
        val y: Float = unit.y + Angles.trnsy(unit.rotation() - 90, mount.weapon.x, mount.weapon.y)
        val angle = Mathf.angle(mount.aimX - x, mount.aimY - y)
        val dst = Mathf.dst(mount.aimX - x, mount.aimY - y)
        val angDiff = Angles.angleDist(angle, unit.rotation())
        val lerp = Mathf.clamp((18 - abs(angDiff)) / 18f) * Mathf.clamp(mount.warmup - 0.05f)
        val stLerp = lerp * (1f - Mathf.clamp((dst - 500f) / 100f))
        val z = Draw.z()
        Draw.z(Layer.effect)
        Lines.stroke(4f * stLerp * Mathf.clamp(1 - mount.reload / mount.weapon.reload), unit.team.color)
        Lines.line(x, y, mount.aimX, mount.aimY)
        Lines.square(mount.aimX, mount.aimY, 18f, 45f)
        val l = max(
          Mathf.clamp(mount.warmup / mount.weapon.minWarmup) * Mathf.clamp(
            1 - mount.reload / mount.weapon.reload
          ), mount.heat
        )
        Lines.stroke(4f * l * stLerp)
        SglDraw.arc(mount.aimX, mount.aimY, 62f, 360 * l, -Time.time * 1.2f)

        Lines.stroke(4f * Mathf.clamp(mount.warmup / mount.weapon.minWarmup), IceColor.matrixNet)
        SglDraw.drawCornerTri(
          mount.aimX, mount.aimY, 46f, 8f, MathTransform.gradientRotateDeg(Time.time * 0.85f, 38f, 1 / 3f, 3), true
        )

        Draw.z(Draw.z() + 0.01f)
        for (i in 0..2) {
          SglDraw.drawTransform(
            mount.aimX, mount.aimY, 54f, 0f, -1.4f * Time.time + i * 120
          ) { rx: Float, ry: Float, r: Float ->
            Draw.rect(
              (SglDrawConst.matrixArrow as TextureRegionDrawable).getRegion(), rx, ry, 12 * stLerp, 12 * stLerp, r + 90
            )
          }
        }

        Draw.z(z)

        super.draw(unit, mount)
      }
    })
  }
}