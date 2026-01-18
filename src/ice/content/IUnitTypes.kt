package ice.content

import arc.func.Cons2
import arc.func.Func
import arc.func.Func2
import arc.func.Prov
import arc.graphics.Blending
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.math.Rand
import arc.math.geom.Rect
import arc.scene.style.TextureRegionDrawable
import arc.struct.Seq
import arc.util.Time
import arc.util.Tmp
import arc.util.pooling.Pools
import ice.ai.AIController
import ice.ai.CarryTaskAI
import ice.audio.ISounds
import ice.entities.IcePuddle
import ice.entities.IceRegister
import ice.entities.bullet.*
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.entities.bullet.base.TrailMoveLightning
import ice.entities.effect.MultiEffect
import ice.graphics.IceColor
import ice.graphics.lightnings.LightningContainer
import ice.graphics.lightnings.LightningVertex
import ice.graphics.lightnings.generator.Floatp2
import ice.graphics.lightnings.generator.LightningGenerator
import ice.graphics.lightnings.generator.RandomGenerator
import ice.library.util.MathTransform
import ice.library.util.toColor
import ice.library.util.toStringi
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.SglFx
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.BarAbility
import ice.world.content.unit.entity.*
import ice.world.content.unit.entity.base.Entity
import ice.world.content.unit.weapon.IceWeapon
import ice.world.content.unit.weapon.MayflyWeapon
import ice.world.draw.part.CustomPart
import ice.world.meta.IceEffects
import ice.world.meta.IceStats
import mindustry.Vars
import mindustry.ai.UnitCommand
import mindustry.ai.types.MinerAI
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.StatusEffects
import mindustry.content.UnitTypes
import mindustry.entities.*
import mindustry.entities.Effect.EffectContainer
import mindustry.entities.abilities.ArmorPlateAbility
import mindustry.entities.abilities.EnergyFieldAbility
import mindustry.entities.abilities.ForceFieldAbility
import mindustry.entities.abilities.ShieldRegenFieldAbility
import mindustry.entities.bullet.ContinuousFlameBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.effect.WrapEffect
import mindustry.entities.part.*
import mindustry.entities.pattern.ShootAlternate
import mindustry.entities.pattern.ShootBarrel
import mindustry.entities.pattern.ShootHelix
import mindustry.entities.pattern.ShootPattern
import mindustry.entities.units.WeaponMount
import mindustry.game.Team
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.graphics.Trail
import mindustry.type.UnitType
import mindustry.type.unit.MissileUnitType
import mindustry.type.weapons.PointDefenseWeapon
import mindustry.ui.Bar
import mindustry.world.meta.BlockFlag
import mindustry.world.meta.Stat
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.particles.SglParticleModels
import singularity.world.unit.abilities.MirrorArmorAbility
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

@Suppress("unused")
object IUnitTypes : Load {
  val 收割 = IceUnitType("harvester") {
    speed = 2f
    flying = true
    hitSize = 10f
    isEnemy = false
    mineTier = 2
    mineSpeed = 3f
    engineColor = IceColor.b4
    defaultCommand = UnitCommand.mineCommand
    aiController = Prov(::MinerAI)
    bundle {
      desc(zh_CN, "收割", "全新设计的采矿单位,搭载了高效的激光共振钻头")
    }
  }
  val 和弦 = IceUnitType("chord") {
    drag = 0.017f
    accel = 0.05f
    armor = 8f
    speed = 3.5f
    flying = true
    health = 60f
    hitSize = 12f
    isEnemy = false
    useUnitCap = false
    rotateSpeed = 3f
    lowAltitude = false
    itemCapacity = 30
    allowedInPayloads = false
    logicControllable = false
    playerControllable = false
    controller = Func { CarryTaskAI() }
    constructor = Prov(UnitEntity::create)
    bundle {
      desc(zh_CN, "和弦")
    }
  }
  val 突刺 = IceUnitType("barbProtrusion") {
    armor = 8f
    speed = 0.7f
    health = 700f
    hitSize = 14f
    rotateSpeed = 3.3f
    squareShape = true
    omniMovement = false
    rotateMoveFirst = true
    treadRects = arrayOf(Rect(11f - (64 / 2), 5f - (64 / 2), 16f, 53f))
    setWeapon("weapon") {
      x = 0f
      shootY += 2f
      reload = 60f
      mirror = false
      rotate = true
      rotateSpeed = 3f
      bullet = BasicBulletType(4f, 80f).apply {
        height = 8f
        width = 4f
        drag = 0f
        trailColor = Pal.accent
        trailWidth = 1.7f
        trailLength = 4
        lifetime = 40f
        shootEffect = IceEffects.baseShootEffect(Pal.accent)
      }
    }
    frontalInjuryFree(0.2f)
    bundle {
      desc(zh_CN, "突刺")
    }
  }
  val 碎甲 = IceUnitType("shatter") {
    armor = 10f
    speed = 0.8f
    health = 1200f
    hitSize = 20f
    rotateSpeed = 3.3f
    squareShape = true
    drawCell = false
    omniMovement = false
    rotateMoveFirst = true
    treadRects = arrayOf(Rect(19f - (96f / 2), 8f - (112f / 2), 20f, 96f))
    setWeapon("weapon") {
      x = 0f
      shootY += 4f
      reload = 50f
      recoil = 2.5f
      mirror = false
      rotate = true
      rotateSpeed = 3f
      bullet = LaserBulletType(100f).apply {
        width = 10f
        shootSound = Sounds.shootLaser
        shootEffect = IceEffects.squareAngle(color2 = Color.valueOf("ffa763"))
        colors = arrayOf(Color.valueOf("ffa763"), Color.valueOf("ffa763"), Color.valueOf("fabd8e"))
      }
    }
    frontalInjuryFree(0.3f)
    bundle {
      desc(zh_CN, "碎甲")
    }
  }
  val 破军 = IceUnitType("breakArmy") {
    armor = 14f
    speed = 0.7f
    health = 1200f
    hitSize = 30f
    rotateSpeed = 3.4f
    squareShape = true
    drawCell = false
    omniMovement = false
    rotateMoveFirst = true
    treadRects = arrayOf(Rect(16f - (128 / 2), 11f - (149 / 2), 33f, 127f))
    frontalInjuryFree(0.35f)
    bundle {
      desc(zh_CN, "破军")
    }
    setWeapon("weapon") {
      x = 0f
      shootY += 8f
      reload = 50f
      recoil = 2.5f
      mirror = false
      rotate = true
      rotateSpeed = 3f
      shootSound = Sounds.explosionDull
      shootCone = 2f
      bullet = BombBulletType(50f, 8 * 5f).apply {
        sprite = "missile-large"
        makeFire = true
        collidesTiles = true
        collides = true
        collidesAir = true
        collidesGround = true
        lifetime = 30f
        keepVelocity = false
        hitColor = Pal.lightOrange
        speed = 7f
        smokeEffect = Fx.shootSmokeTitan
        shootEffect = Effect(10f) { e ->
          Draw.color(Pal.lightOrange, Pal.lightishOrange, e.fin())
          val w = 1.3f + 10 * e.fout()
          Drawf.tri(e.x, e.y, w, 35f * e.fout(), e.rotation)
          Drawf.tri(e.x, e.y, w, 6f * e.fout(), e.rotation + 180f)
        }
        width = 10f
        height = 10f
        bulletInterval = 10f
        trailWidth = 2f
        trailColor = Color.valueOf("ffa763")
        trailLength = 8
        despawnEffect = Effect(30f) { e ->
          Draw.color(Pal.engine)
          e.scaled(25f) { f ->
            Lines.stroke(f.fout() * 2f)
            Lines.circle(e.x, e.y, 4f + f.finpow() * splashDamageRadius)
          }

          Lines.stroke(e.fout() * 2f)
          Angles.randLenVectors(e.id.toLong(), 24, e.finpow() * splashDamageRadius) { x: Float, y: Float ->
            val ang = Mathf.angle(x, y)
            Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 4 + 1f)
          }
        }
        hitEffect = despawnEffect
        intervalBullet = BombBulletType(40f, 3f).apply {
          speed = 0f
          lifetime = 0f
        }
      }
    }
  }
  val 路西法 = IceUnitType("lucifer") {
    armor = 1f
    speed = 3.5f
    flying = true
    health = 150f
    hitSize = 16f
    mineTier = 3
    mineSpeed = 2f
    engineSize = 3f
    faceTarget = false
    lowAltitude = true
    rotateSpeed = 7f
    itemCapacity = 5
    engineOffset = 8f
    circleTarget = true
    engineColor = IceColor.b4
    itemCapacity = 30
    buildSpeed = 0.75f
    buildRange = 8 * 40f
    setWeapon("weapon") {
      mirror = true
      rotate = true
      reload = 15f
      rotateSpeed = 9f
      predictTarget = false
      bullet = RandomDamageBulletType(8, 14, 3f).apply {
        backColor = IceColor.b4
        frontColor = IceColor.b4
        lightColor = IceColor.b4
        shootY += 1
        lifetime = 60f
        homingPower = 0.05f
        homingRange = 50f
        shootEffect = IceEffects.squareAngle(color1 = IceColor.b4, color2 = IceColor.b5)
      }
    }
    abilities
    bundle {
      desc(zh_CN, "路西法")
    }
  }
  val 仆从 = IceUnitType("footman") {
    speed = 3.2f
    flying = true
    health = 2000f
    hitSize = 30f
    engineSize = 6f
    rotateSpeed = 5.2f
    engineOffset = 19f
    engineColor = IceColor.b4
    forceMultiTarget = true
    aiController = UnitTypes.flare.aiController
    setWeapon {
      x = -2f
      y = 8f
      bullet = BulletType(6.7f, 17f).apply {
        inaccuracy = 32f
        pierceBuilding = true
        ammoMultiplier = 3f
        hitSize = 7f
        lifetime = 18f
        pierce = true
        shootSound = Sounds.loopFire
        statusDuration = 60f * 10
        shootEffect = IceEffects.changeFlame(lifetime * speed)
        hitEffect = Fx.hitFlameSmall
        despawnEffect = Fx.none
        status = StatusEffects.burning
        keepVelocity = false
        hittable = false
      }
    }
    bundle {
      desc(
        zh_CN, "仆从", "传教者的专属防空护卫,仆从搭载了双联装净化之焰喷射器,能够喷射高温火焰,专门克制无人机集群", "确定是护卫不是火刑柱?"
      )
    }
  }
  val 传教者 = IceUnitType("missionary") {
    speed = 0.9f
    flying = true
    hitSize = 90f
    health = 40000f
    targetAir = true
    faceTarget = true
    lowAltitude = true
    rotateSpeed = 0.6f
    targetGround = true
    forceMultiTarget = true
    constructor = Prov(UnitEntity::create)
    engines.add(IUnitEngine(30f, -65f, 8f, -90f, 6f))
    engines.add(IUnitEngine(0f, -80f, 8f, -90f))
    engines.add(IUnitEngine(-30f, -65f, 8f, -90f, 6f))
    setWeapon("weapon1") {
      x = -34.75f
      y = -20.75f
      top = true
      rotate = true
      mirror = true
      shootY = 8f
      reload = 30f
      rotateSpeed = 10f
      shootSound = Sounds.shootSmite
      shoot = ShootPattern().apply {
        shotDelay = 30f
        shots = 3
        shotDelay = 10f
      }
      bullet = BasicBulletType(7f, 250f, "large-orb").apply {
        width = 17f
        height = 21f
        hitSize = 8f
        shootEffect = MultiEffect(Fx.shootTitan, Fx.colorSparkBig, object : WaveEffect() {
          init {
            colorTo = Pal.accent
            colorFrom = colorTo
            lifetime = 12f
            sizeTo = 20f
            strokeFrom = 3f
            strokeTo = 0.3f
          }
        })
        smokeEffect = Fx.shootSmokeSmite
        ammoMultiplier = 1f
        pierceCap = 4
        pierce = true
        pierceBuilding = true
        trailColor = Pal.accent
        backColor = trailColor
        hitColor = backColor
        frontColor = Color.white
        trailWidth = 2.8f
        trailLength = 9
        hitEffect = Fx.hitBulletColor
        buildingDamageMultiplier = 0.3f
        despawnEffect = MultiEffect(Fx.hitBulletColor, object : WaveEffect() {
          init {
            sizeTo = 30f
            colorTo = Pal.accent
            colorFrom = colorTo
            lifetime = 12f
          }
        })
        trailRotation = true
        trailEffect = Fx.disperseTrail
        trailInterval = 3f
        bulletInterval = 3f
        intervalBullet = object : LightningBulletType() {
          init {
            damage = 30f
            collidesAir = false
            lightningColor = Pal.accent
            lightningLength = 5
            lightningLengthRand = 10
            buildingDamageMultiplier = 0.5f
            lightningType = object : BulletType(0.0001f, 0f) {
              init {
                lifetime = Fx.lightning.lifetime
                hitEffect = Fx.hitLancer
                despawnEffect = Fx.none
                status = StatusEffects.shocked
                statusDuration = 10f
                hittable = false
                lightColor = Color.white
                buildingDamageMultiplier = 0.25f
              }
            }
          }
        }
      }
    }
    setWeapon("weapon2") {
      x = -20.5f
      y = 4f
      rotate = true
      reload = 60f
      shootSound = Sounds.shootMalign
      bullet = BombBulletType(500f, 64f).apply {
        width = 10f
        height = 10f
        speed = 12f
        drag = 0.05f
        lifetime = 120f
        despawnEffect = WaveEffect().apply {
          colorTo = Pal.accent
          colorFrom = Pal.accent
          sizeFrom = 0f
          sizeTo = 8 * 5f
          lifetime = 30f
        }
        shootEffect = MultiEffect(Fx.shootTitan, ParticleEffect().apply {
          lifetime = 20f
          colorFrom = Pal.accent
          sizeFrom = 2f
          sizeTo = 8f
          length = 30f
          cone = 40f
          line = true
        })
        intervalSpread = 300f
        bulletInterval = 10f
        intervalBullet = BasicBulletType(2f, 30f, "mine-bullet").apply {
          pierce = true
          pierceBuilding = true
          status = IStatus.破甲II
          trailChance = 0.2f
          trailEffect = WaveEffect().apply {
            lifetime = 10f
            sizeTo = 24f
            sides = 3
            colorFrom = Pal.accent
          }
        }
        var i = 0f
        setUpdate {
          i += it.time()
        }
        setDraw {
          Draw.color(Pal.accent)
          Lines.stroke(1 - Interp.pow3Out.apply(it.fin()) * 3)
          Lines.poly(it.x, it.y, 3, Interp.pow3Out.apply(1 - it.fin()) * 24, i)
        }
      }
    }
    setWeapon("weapon3") {
      x = -16f
      y = 29f
      top = true
      rotate = true
      mirror = true
      reload = 6f
      shoot.apply {
        shotDelay = 15f
      }
      bullet = BasicBulletType(8f, 23f).apply {
        trailChance = 0.25f
        trailLength = 12
        trailWidth = 3.2f
        trailColor = Pal.accent
        hitEffect = WaveEffect().apply {
          lifetime = 15f
          sizeTo = 30f
          strokeFrom = 4f
          colorFrom = Pal.accent
          colorTo = Pal.accent
        }
        despawnEffect = hitEffect
        trailEffect = ParticleEffect().apply {
          particles = 1
          lifetime = 25f
          sizeFrom = 3f
          sizeTo = 0f
          cone = 360f
          length = 23f
          sizeInterp = Interp.pow10In
          colorFrom = Pal.accent
          colorTo = Pal.accent
        }
      }
    }.copyAdd {
      x = -13f
      y = 45f
    }
    bundle {
      desc(
        zh_CN, "传教者", "重型空中火力平台,枢机教廷[净化之翼]军团,搭载4门防空拦截的磁轨速射炮,2门圣裁等离子爆裂炮,以及2门对地穿甲的粒子冲击炮形成全方位立体火力网", "枢机的例行祷告"
      )
    }
  }
  val 裂片集群 = IceUnitType("clusterLobes") {
    setWeapon {
      shoot.apply {
        shots = 4
        shotDelay = 6f
      }
      mirror = false
      baseRotation = 90f
      shake = 3f
      shootCone = 360f
      rotate = false
      rotateSpeed = 0f
      reload = 60 * 3f
      inaccuracy = 60f
      bullet = object : BasicBulletType(8f, 40f) {
        init {
          smokeEffect = Fx.none
          shootSound = Sounds.shootMalign
          shootEffect = Fx.none
          lifetime = 60 * 8f / speed + 60
          trailLength = 24
          trailWidth = 3f
          trailColor = IceColor.b4
          homingRange = 100 * 8f
          homingPower = 0.2f
          homingDelay = 10f
          hitEffect = Effect(60f) { e ->
            Draw.color(IceColor.b4)
            Lines.stroke(Interp.pow3Out.apply(e.fout()) * 3)
            Lines.poly(e.x, e.y, 8, Interp.pow3Out.apply(e.fin()) * 36 + 36, e.rotation)
          }
          despawnEffect = hitEffect
          despawnHit = true
        }

        override fun removed(b: Bullet) {
          val bc = object : BombBulletType(15f, 40f) {
            init {
              collidesGround = true
              collides = true
              splashDamage = 15f
              collidesTiles = true
              speed = 0f
              collidesAir = true
              drag = 0f
              lifetime = 15f
              despawnEffect = WaveEffect().apply {
                lifetime = 15f
                sizeTo = 15f
                strokeFrom = 4f
                colorFrom = IceColor.b4
                colorTo = IceColor.b4
              }
              hitEffect = despawnEffect
            }

            override fun draw(b: Bullet) {
              Draw.color(IceColor.b4)
              b.vel.set(0f, 0f)
              Drawf.tri(b.x, b.y, 8f, 8f, b.data as Float)
              //  super.draw(b)
            }
          }
          (0 until 15).forEach { i ->
            val x = IceEffects.rand.random(-36, 36)
            val y = IceEffects.rand.random(-36, 36)
            bc.create(
              b, b.team, b.x + x, b.y + y, Random.nextInt(360).toFloat(), -1f, 1f, 1f, Random.nextInt(360).toFloat()
            )
          }
          super.removed(b)
        }

        override fun update(b: Bullet) {
          super.update(b)
          if (Mathf.chanceDelta(1f.toDouble())) {
            val x = IceEffects.rand.random(-8f, 8f)
            val y = IceEffects.rand.random(-8f, 8f)
            IceEffects.layerBullet.at(b.x + x, b.y + y, 0f, IceColor.b4, Random.nextInt(360))
          }
        }

        override fun draw(b: Bullet) {
          drawTrail(b)
          drawParts(b)
          val shrink = shrinkInterp.apply(b.fout())
          val height = this.height * ((1f - shrinkY) + shrinkY * shrink)
          val width = this.width * ((1f - shrinkX) + shrinkX * shrink)
          val mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin())

          Draw.mixcol(mix, mix.a)
          Draw.color(trailColor)
          Drawf.tri(b.x, b.y, width + 2, height, b.rotation())

          Draw.reset()
        }
      }
    }.copyAdd {
      baseRotation = 270f
    }
    abilities.add(BarAbility<ClusterLobesUnit> { unit, bars ->
      bars.add(Bar({ "${IceStats.格挡数量.localized()} ${unit.resistCont}" }, { IceColor.b4 }) { 1f }).row()
      bars.add(Bar({ "${Stat.armor.localized()}" }, { IceColor.b4 }) { unit.armor() / 100 }).row()
      bars.add(Bar({ "${IceStats.伤害减免.localized()} ${(unit.immunity() * 100).toStringi(2)}%" }, { IceColor.b4 }) {
        unit.immunity()
      }).row()
    })
    rotateSpeed = 2f
    drag = 0.05f
    constructor = IceRegister.getPutUnit<ClusterLobesUnit>()
    flying = true
    hidden = false
    faceTarget = false
    lowAltitude = true
    drawBody = false
    drawCell = false
    health = 96000f
    armor = 0f
    hitSize = 32f
    speed = 18 / 7.5f
    rotateSpeed = 4f
    range = 8 * 60f
    engineSize = 0f
    itemCapacity = 0
    Vars.content.statusEffects().forEach {
      if (it.speedMultiplier == 1f) return@forEach
      immunities.add(it)
    }
    parts.add(HaloPart().apply {
      mirror = false
      shapes = 4
      radius = 6f
      triLength = 4f
      haloRadius = 16f
      haloRotateSpeed = -1f
    })
    bundle {
      desc(zh_CN, "裂片集群")
    }
  }
  val 断业 = IceUnitType("breakUp") {
    speed = 0.48f
    armor = 26f
    health = 22000f
    hitSize = 75f
    crushDamage = 25f / 5f
    rotateSpeed = 0.8f
    treadPullOffset = 1
    squareShape = true
    omniMovement = false
    rotateMoveFirst = true
    outlineColor = Color.valueOf("24222B")
    treadRects = arrayOf(Rect(70f - (400 / 2), 53f - (500 / 2), 83f, 394f))
    setWeapon("weapon1") {
      x = 0f
      y = 11f
      shake = 4f
      shootY += 16f
      mirror = false
      rotate = true
      recoil = 4f
      reload = 240f
      shootCone = 0f
      rotateSpeed = 0.5f
      cooldownTime = 38f * 2
      shoot.firstShotDelay = 80f
      shootSound = ISounds.laser2
      parentizeEffects = true
      val laserBulletTypelength = 400f
      val bullet2 = object : ChainBulletType(12f) {
        override fun init(b: Bullet) {
          (1..3).forEach { _ ->
            super.init(b)
          }
        }
      }.apply {
        collidesGround = false
        length = laserBulletTypelength
        hitColor = IceColor.b4.cpy().a(0.4f).also { lightningColor = it }.also { lightColor = it }
      }
      val bullet1 = object : LaserBulletType(1200f) {
        override fun create(owner: Entityc?, shooter: Entityc?, team: Team?, x: Float, y: Float, angle: Float, damage: Float, velocityScl: Float, lifetimeScl: Float, data: Any?, mover: Mover?, aimX: Float, aimY: Float, target: Teamc?): Bullet? {
          bullet2.create(
            owner, shooter, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target
          )
          return super.create(
            owner, shooter, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target
          )
        }
      }.apply {
        colors = arrayOf(IceColor.b4.cpy().a(0.4f), IceColor.b4, Color.white)

        width = 45f
        lifetime = 30f
        sideAngle = 60f
        sideLength = 35f
        collidesAir = true
        collidesGround = true
        hitEffect = Fx.hitLancer
        length = laserBulletTypelength
        buildingDamageMultiplier = 1.25f
        chargeSound = ISounds.forceHoldingLaser2
        shootEffect = IceEffects.lancerLaserShoot
        chargeEffect = MultiEffect(
          Effect(38f * 2) { e ->
            IceEffects.unitMountSXY(e.data, this@setWeapon) { bulletX, bulletY ->
              Draw.color(IceColor.b4)
              Angles.randLenVectors(
                e.id.toLong(), 20, 1f + 40f * e.fout(), e.rotation, 120f
              ) { x: Float, y: Float ->
                Lines.lineAngle(bulletX + x, bulletY + y, Mathf.angle(x, y), e.fslope() * 3f + 1f)
              }
            }
          },
          Effect(45f * 2) { e: EffectContainer ->
            IceEffects.unitMountSXY(e.data, this@setWeapon) { bulletX, bulletY ->
              val margin = 1f - Mathf.curve(e.fin(), 0.9f)
              val fin = min(margin, e.fin())
              Draw.color(IceColor.b4)
              Fill.circle(bulletX, bulletY, fin * 6f)
              Draw.color()
              Fill.circle(bulletX, bulletY, fin * 4f)
            }
          },
        )
      }


      bullet = bullet1
    }
    setWeapon("weapon2") {
      shoot.apply {
        shots = 3
        shotDelay = 10f
      }
      x = 0f
      y = -30f
      shootY = 8f
      mirror = false
      rotate = true
      rotateSpeed = 3f
      reload = 150f
      recoil = 4f
      shootSound = ISounds.highExplosiveShell
      bullet = AngleBulletType(4f, 2f, 180f, 2f).apply {
        width = 8f
        height = 8f
        knockback = 0.5f
        shootEffect = Effect(32f) { e: EffectContainer ->
          Draw.color(Color.white, IceColor.b4, e.fin())
          Fx.rand.setSeed(e.id.toLong())
          (0..8).forEach { i ->
            val rot = e.rotation + Fx.rand.range(26f)
            Fx.v.trns(rot, Fx.rand.random(e.finpow() * 30f))
            Fill.poly(e.x + Fx.v.x, e.y + Fx.v.y, 4, e.fout() * 4f + 0.2f, Fx.rand.random(360f))
          }
        }
        parts = Seq.with(FlarePart().apply {
          followRotation = true
          rotMove = 180f
          progress = DrawPart.PartProgress.life
          color1 = IceColor.b4
          stroke = 6f
          radius = 5f
          radiusTo = 30f
        })
      }
      bullet = BasicBulletType().apply {
        val rand = Rand()

        speed = 13f
        lifetime = 60 * 2f
        val layer1 = Effect(300f, 1600f) { e: EffectContainer ->
          val rad = 150f
          rand.setSeed(e.id.toLong())
          Draw.color(Color.white, e.color, e.fin() + 0.6f)
          val circleRad = e.fin(Interp.circleOut) * rad * 4f
          Lines.stroke(12 * e.fout())
          Lines.circle(e.x, e.y, circleRad)
          (0..23).forEach { i ->
            Tmp.v1.set(1f, 0f).setToRandomDirection(rand).scl(circleRad)
            IceEffects.drawFunc(
              e.x + Tmp.v1.x, e.y + Tmp.v1.y, rand.random(circleRad / 16, circleRad / 12) * e.fout(), rand.random(circleRad / 4, circleRad / 1.5f) * (1 + e.fin()) / 2, Tmp.v1.angle() - 180
            )
          }

          Draw.blend(Blending.additive)
          Draw.z(Layer.effect + 0.1f)
          Fill.light(
            e.x, e.y, Lines.circleVertices(circleRad), circleRad, Color.clear, Tmp.c1.set(Draw.getColor()).a(e.fout(Interp.pow10Out))
          )
          Draw.blend()
          Draw.z(Layer.effect)
          Drawf.light(e.x, e.y, rad * e.fout(Interp.circleOut) * 4f, e.color, 0.7f)
        }
        despawnEffect = layer1
      }
    }
    setWeapon("weapon3") {
      x = 28f
      y = -25f
      rotate = true
      reload = 30f
      recoils = 3
      rotateSpeed = 3f
      shootSound = Sounds.shootBeamPlasma
      bullet = TrailFadeBulletType(19f, 200f).apply {
        lifetime = 15f
        trailLength = 10
        trailWidth = 1.6f

        trailColor = IceColor.b4
        trailLength = 20
        trailWidth = 2.4f
        trailEffect = SglFx.trailParticle
        trailChance = 0.16f

        tracerStroke -= 0.3f
        keepVelocity = true
        tracerSpacing = 10f
        tracerUpdateSpacing *= 1.25f
        lightningColor = IceColor.b4
        lightColor = lightningColor
        backColor = lightColor
        hitColor = backColor
        trailColor = IceColor.b4
        frontColor = IceColor.b4
        width = 9f
        height = 9f
        hitSound = Sounds.shootBeamPlasma
        hitShake = 5f
        despawnShake = hitShake
        pierceArmor = true
        pierceCap = 4

        splashDamage = 20f
        splashDamageRadius = 24f
        splashDamagePierce = true
        collidesAir = true
        collidesGround = true
        shootEffect = MultiEffect(
          IceEffects.squareAngle(color1 = IceColor.b4, color2 = IceColor.b4), IceEffects.lightningShoot()
        )
        hitEffect = MultiEffect(IceEffects.square(IceColor.b4, length = 16f, size = 4f), Effect(15f) { e ->
          val rad = 5f
          e.color = IceColor.b4
          IceEffects.rand.setSeed(e.id.toLong())
          Draw.color(Color.white, e.color, e.fin() + 0.6f)
          val circleRad = e.fin(Interp.circleOut) * rad * 4f
          Lines.stroke(3 * e.fout())
          Lines.circle(e.x, e.y, circleRad)
        })
        despawnEffect = hitEffect
      }
      shootY += 3f
    }
    setWeapon("weapon4") {
      x = 25.5f
      y = 10f
    }
    setWeapon("weapon4") {
      x = 25.5f
      y = 32f
    }
    bundle {
      desc(zh_CN, "断业", "断业是神殿[净罪计划]的产物,其装甲内层熔铸了经神祝圣的暮光合金,主炮能对建筑与重甲单位造成毁灭性伤害,能撕裂红雾中的畸变体集群", "帝国腐朽的装甲部队节节败退,唯有枢机的神术能短暂驱散腐化,帝国残部讥讽其为伪神的铁棺材,但无人能否认——当它的履带碾过焦土时,连红雾都会为之退散")
    }
  }
  val 焚棘 = IceUnitType("ardenThorn") {
    speed = 1.3f
    accel = 0.5f
    drag = 0.05f
    flying = true
    health = 2000f
    hitSize = 40f
    drawCell = false
    faceTarget = false
    rotateSpeed = 1.9f
    constructor = IceRegister.getPutUnit<ArdenThorn>()
    setWeapon("weapon1") {
      x = 11f
      y = 27f
      shootY += 1f
      reload = 30f
      recoil = 2f
      rotate = true
      layerOffset = -1f
      rotateSpeed = 4f
      shootSound = Sounds.shootMissile
      shoot = object : ShootHelix() {
        var bl = true
        override fun shoot(totalShots: Int, handler: BulletHandler, barrelIncrementer: Runnable?) {
          for (i in 0..<shots) {
            bl = !bl
            handler.shoot(0f, 0f, 0f, firstShotDelay + shotDelay * i) { b ->
              b.moveRelative(0f, Mathf.sin(b.time + offset, scl, mag * if (bl) 1 else -1))
            }
          }
        }

        init {
          scl = 4f
          mag = 2f
        }
      }
      bullet = BombBulletType(80f, 8 * 3f).apply {
        sprite = "mine-bullet"
        width *= 2
        height *= 2
        collidesAir = true
        splashDamagePierce = true
        collidesTiles = true
        collides = true
        var sec = 30f
        speed = (sec * 8f) / 60f
        lifetime = (25f / sec) * 60f
        trailWidth = 2f
        trailLength = 13
        trailColor = IceColor.b4
        frontColor = IceColor.b5
        lightColor = IceColor.b5
        backColor = IceColor.b5
        hitEffect = MultiEffect(Effect(16f) { e ->
          IceEffects.rand.setSeed(e.id.toLong())
          val rad = splashDamageRadius
          Draw.color(Color.white, backColor, e.fin())
          val circleRad = e.fin(Interp.circleOut) * rad
          Lines.stroke(5 * e.fout())
          Lines.circle(e.x, e.y, circleRad)
          (0..3).forEach { i ->
            Tmp.v1.set(1f, 0f).setToRandomDirection(IceEffects.rand).scl(circleRad)
            IceEffects.drawFunc(
              e.x + Tmp.v1.x, e.y + Tmp.v1.y, IceEffects.rand.random(circleRad / 16, circleRad / 12) * e.fout(), IceEffects.rand.random(circleRad / 4, circleRad / 1.5f) * (1 + e.fin()) / 2, Tmp.v1.angle() - 180
            )
          }

          Draw.blend(Blending.additive)
          Draw.z(Layer.effect + 0.1f)
          Fill.light(
            e.x, e.y, Lines.circleVertices(circleRad), circleRad, Color.clear, Tmp.c1.set(Draw.getColor()).a(e.fout())
          )
          Draw.blend()
          Draw.z(Layer.effect)
          Drawf.light(e.x, e.y, rad * e.fout(Interp.circleOut) * 4f, e.color, 0.7f)
        }, IceEffects.baseHitEffect)
        despawnEffect = hitEffect
        shootEffect = IceEffects.squareAngle(color1 = IceColor.b5, color2 = IceColor.b4)
      }
    }
    setWeapon("weapon2") {
      x = -17f
      y = -8f
      rotate = true
      shootY += 3f
      reload = 4f
      recoil = 1f
      layerOffset = -2f
      rotateSpeed = 2.5f
      reloadInterp = Interp.linear
      shootSound = ISounds.laserGun
      bullet = RandomDamageBulletType(20, 30, 7f).apply {
        pierceCap = 3
        frontColor = IceColor.b5
        lightColor = IceColor.b5
        backColor = IceColor.b5
        width = 4f
        height = 9f
        hitSize = 4f
        shootEffect = Effect(8f) { e ->
          Draw.color(IceColor.b5, IceColor.b4, e.fin())
          val w = 1f + 2 * e.fout()
          Drawf.tri(e.x, e.y, w, 8 * e.fout(), e.rotation + 30f)
          Drawf.tri(e.x, e.y, w, 8 * e.fout(), e.rotation + 15f)
          Drawf.tri(e.x, e.y, w, 4 * e.fout(), e.rotation)
          Drawf.tri(e.x, e.y, w, 8 * e.fout(), e.rotation - 15f)
          Drawf.tri(e.x, e.y, w, 8 * e.fout(), e.rotation - 30f)
        }
        hitEffect = IceEffects.baseHitEffect
        despawnEffect = hitEffect
      }
    }
    bundle {
      desc(
        zh_CN, "焚棘", "轻型侦察攻击机,配备独特的渐速式火力系统,机身尾部的两挺转管重机枪在开火时可持续提升射速,形成愈演愈烈的压制弹幕.头部则搭载了两门锁定式导弹发射器,用于精准打击轻型防御目标.虽定位为侦察单位,但其出色的滞空能力与双重火力配置,使其能在探查敌情的同時实施骚扰性攻击,成为战场上空难以驱离的刺眼存在"
      )
    }
  }
  val 青壤 = IceUnitType("schizovegeta") {
    speed = 0.3f
    health = 200f
    hitSize = 12f
    drawCell = false
    rotateSpeed = 1f
    outlineRadius = 3
    outlineColor = IceColor.r2
    createScorch = false
    deathSound = ISounds.chizovegeta

    legPhysicsLayer = false
    allowLegStep = true
    legStraightness = 0.3f
    stepShake = 0f
    legCount = 8
    legLength = 8f
    legGroupSize = 4
    lockLegBase = true
    legBaseUnder = true
    legContinuousMove = true
    legExtension = -2f
    legBaseOffset = 3f
    legMaxLength = 1.1f
    legMinLength = 0.2f
    legLengthScl = 0.96f
    legForwardScl = 1.1f
    rippleScale = 0.2f
    legMoveSpace = 1f

    deathExplosionEffect = MultiEffect(IceEffects.bloodNeoplasma, 3)
    constructor = IceRegister.getPutUnit<Schizovegeta>()
    bundle {
      desc(
        zh_CN, "青壤", "由血肉喷口缓慢孕育的活体培养囊,本身不具备攻击性,只会笨拙地蠕行移动.当其外膜在环境中自然破裂或被外力摧毁时,会释放出数颗至数十颗不等的肿瘤"
      )
    }
  }
  val 丰穰之瘤 = IceUnitType("richTumor") {
    speed = 0f
    accel = -3f
    range = 0f
    health = 30f
    hitSize = 4f
    drawCell = false
    targetAir = false
    useUnitCap = false
    targetable = false
    itemCapacity = 0
    targetGround = false
    createScorch = false
    outlineRadius = 1
    playerControllable = false
    deathSound = Sounds.plantBreak
    deathExplosionEffect = IceEffects.bloodNeoplasma
    constructor = IceRegister.getPutUnit<RichTumor>()
    bundle {
      desc(
        zh_CN, "丰穰之瘤", "无法移动的特殊组织体,不会被任何单位视为目标.落地后进入短暂的潜伏期,随后开始将下方地表同化为活性肿瘤地,为血肉网络提供持续的生长基础"
      )
    }
  }
  val 蚀虻 = IceUnitType("corrodfly-head") {
    rotateMoveFirst = true
    allowedInPayloads = false
    legStraightness = 0.3f
    stepShake = 0f
    legCount = 2
    legLength = 18f
    legGroupSize = 4
    lockLegBase = true
    legBaseUnder = true
    legContinuousMove = true
    legExtension = -2f
    legBaseOffset = 3f
    legMaxLength = 1.1f
    legMinLength = 0.2f
    legLengthScl = 0.96f
    legForwardScl = 1.1f
    rippleScale = 0.2f
    legMoveSpace = 1f

    hitSize = 8f
    rotateSpeed = 2.5f
    speed = 0.8f
    createScorch = false
    drawCell = false
    outlineRadius = 3
    outlineColor = IceColor.r2
    deathExplosionEffect = MultiEffect(IceEffects.bloodNeoplasma, 3)
    constructor = IceRegister.getPutUnit<CorrodflyHead>()
    bundle {
      desc(zh_CN, "蚀虻")
    }
  }
  val 蚀虻Middle = IceUnitType("corrodfly-middle") {
    hitSize = 5f
    drawCell = false
    outlineRadius = 3
    allowedInPayloads = false
    outlineColor = IceColor.r2
    hidden = true
    playerControllable = false
    createScorch = false
    deathSound = ISounds.chizovegeta
    aiController = Prov(::AIController)
    deathExplosionEffect = MultiEffect(IceEffects.bloodNeoplasma, 3)
    constructor = IceRegister.getPutUnit<CorrodflyMiddle>()
  }
  val 蚀虻End = IceUnitType("corrodfly-end") {
    legStraightness = 0.3f
    stepShake = 0f
    legCount = 2
    allowedInPayloads = false
    legLength = 18f
    legGroupSize = 4
    lockLegBase = true
    legBaseUnder = true
    legContinuousMove = true
    legExtension = -2f
    legBaseOffset = 3f
    legMaxLength = 1.1f
    legMinLength = 0.2f
    legLengthScl = 0.96f
    legForwardScl = 1.1f
    rippleScale = 0.2f
    legMoveSpace = 1f

    hitSize = 8f
    outlineRadius = 3
    outlineColor = IceColor.r2
    drawCell = false
    createScorch = false
    hidden = true
    constructor = IceRegister.getPutUnit<CorrodflyEnd>()
    faceTarget = false
    playerControllable = false
    deathSound = ISounds.chizovegeta
    aiController = Prov(::AIController)
    deathExplosionEffect = MultiEffect(IceEffects.bloodNeoplasma, 3)
    setWeapon("weapon") {
      x = 0f
      y = -4f
      shootX += 1
      recoil = 1f
      mirror = false
      rotate = true
      reload = 50f
      shootY += 2f
      shoot.shots = 2
      shoot.shotDelay = 15f
      shootSound = ISounds.flblSquirt
      bullet = MultiBasicBulletType("flesh").apply {
        width = 7f
        height = width
        shrinkInterp = Interp.one
        status = IStatus.流血
        statusDuration = 2 * 60f
        lightColor = IceColor.r3
        backColor = IceColor.r3
        frontColor = IceColor.r3
        lightOpacity = 0.2f
        shootEffect = Fx.none
        hitEffect = Effect(14f) { e ->
          Draw.color(IceColor.r3, IceColor.r1, e.fin())
          e.scaled(7f) { s ->
            Lines.stroke(0.5f + s.fout())
            Lines.circle(e.x, e.y, s.fin() * 5f)
          }
          Lines.stroke(0.5f + e.fout())
          Angles.randLenVectors(e.id.toLong(), 5, e.fin() * 15f) { x: Float, y: Float ->
            val ang = Mathf.angle(x, y)
            Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 3 + 1f)
          }
          Drawf.light(e.x, e.y, 20f, IceColor.r3, 0.6f * e.fout())
        }
        despawnEffect = hitEffect
        smokeEffect = Effect(20f) { e ->
          Draw.color(IceColor.r1, IceColor.r2, e.fin())
          Angles.randLenVectors(e.id.toLong(), 5, e.finpow() * 6f, e.rotation, 20f) { x: Float, y: Float ->
            Fill.circle(e.x + x, e.y + y, e.fout() * 1.5f)
          }
        }
        setRemoved { b ->
          val puddle = IcePuddle.create()
          puddle.team = b.team
          puddle.tile = b.tileOn()
          puddle.liquid = ILiquids.浓稠血浆
          puddle.amount = IceEffects.rand.random((height + width) / 2, height * width / 2)
          puddle.set(b.x, b.y)
          Puddles.register(puddle)
          puddle.add()
        }
      }
    }
  }
  val 虚宿 = IceUnitType("emptiness") {
    constructor = IceRegister.getPutUnit<Entity>()
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
    setEnginesMirror(object : UnitType.UnitEngine(-15f, -60f, 8f, -90f) {
      override fun draw(unit: Unit) = kotlin.Unit
    }, object : UnitType.UnitEngine(-40f, -50f, 8f, -90f) {
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
          ) { x: Float, y: Float, r: Float -> b.trail.update(x, y) }
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
            val spilloverEnergy: BulletType = object : BulletType() {
              init {
                collides = false
                absorbable = false
                splashDamage = 120f
                splashDamageRadius = 40f
                speed = 4.4f
                lifetime = 64f
                hitShake = 4f
                hitSize = 3f
                despawnHit = true
                hitEffect = mindustry.entities.effect.MultiEffect(SglFx.explodeImpWaveSmall, SglFx.diamondSpark)
                hitColor = IceColor.matrixNet

                trailColor = IceColor.matrixNet
                trailEffect = SglFx.movingCrystalFrag
                trailRotation = true
                trailInterval = 4f

                fragBullet = object : LightningBulletType() {
                  init {
                    lightningLength = 14
                    lightningLengthRand = 4
                    damage = 24f
                  }
                }
                fragBullets = 1
              }

              override fun update(b: Bullet) {
                super.update(b)

                b.vel.lerp(0f, 0f, 0.012f)

                if (b.timer(4, 3f)) {
                  Angles.randLenVectors(System.nanoTime(), 2, 2.2f) { x: Float, y: Float ->
                    SglParticleModels.floatParticle.create(
                      b.x, b.y, IceColor.matrixNet, x, y, 2.2f
                    ).strength = 0.3f
                  }
                }
              }

              override fun draw(b: Bullet) {
                Draw.color(hitColor)
                val fout = b.fout(Interp.pow3Out)
                Fill.circle(b.x, b.y, 5f * fout)
                Draw.color(Color.black)
                Fill.circle(b.x, b.y, 2.6f * fout)
              }
            }

            intervalBullet = spilloverEnergy.copy()
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
        return Units.bestTarget(unit.team, x, y, range, { u: Unit -> unit.checkTarget(air, ground) }, { t: Teamc? -> ground }, { u: Unit, x: Float, y: Float ->
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
    bundle {
      desc(zh_CN, "虚宿")
    }
  }
  val 无畏 = IceUnitType("fearless") {
    abilities.add(EnergyFieldAbility(0f, 60f, 0f).apply {
      y = -31.75f
      display = false
      maxTargets = 0
      healPercent = 0f
      color = Color.valueOf("FF5845")
    }, ShieldRegenFieldAbility(400f, 12000f, 60f, 240f))
    flying = true
    health = 64000f
    armor = 139f
    hitSize = 92f
    range = 480f
    speed = 1f
    rotateSpeed = 2f
    engineSize = 0f
    engineOffset = 44.25f
    faceTarget = false
    lowAltitude = true
    outlineColor = Color.valueOf("1F1F1F")
    setWeapon("weapon1") {
      x = 20.75f
      y = -3.25f
      reload = 180f
      shoot.apply {
        shots = 4
        shotDelay = 10f
      }
      shootY = 8f
      rotate = true
      rotateSpeed = 1f
      shootCone = 15f
      cooldownTime = 90f
      layerOffset = 0.01f
      minWarmup = 0.99f
      shootWarmupSpeed = 0.06f
      shootSound = ISounds.moonhidelaunched
      bullet = EmpsBulletType().apply {
        sprite = "ice-shining"
        damage = 125f
        lifetime = 40f
        speed = 12f
        spin = -4f
        width = 48f
        height = 48f
        shrinkY = 0f
        scaleLife = true
        absorbable = false
        frontColor = Color.valueOf("FF8663")
        backColor = Color.valueOf("FF5845")
        splashDamage = 125f
        splashDamageRadius = 120f
        shootEffect = ParticleEffect().apply {
          line = true
          particles = 12
          lifetime = 20f
          length = 45f
          cone = 30f
          lenFrom = 6f
          lenTo = 6f
          strokeFrom = 3f
          strokeTo = 0f
          lightColor = Color.valueOf("FF5845")
          colorFrom = Color.valueOf("FFDCD8")
          colorTo = Color.valueOf("FF5845")
        }
        hitPowerEffect = ParticleEffect().apply {
          line = true
          particles = 6
          lifetime = 22f
          length = 120f
          cone = -360f
          lenFrom = 6f
          lenTo = 6f
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
        radius = 120f
        timeIncrease = 1.5f
        unitDamageScl = 0.5f
        status = IStatus.电链
        statusDuration = 60f
        homingPower = 0.08f
        homingRange = 240f
        suppressionRange = 120f
        suppressionDuration = 600f
        suppressionEffectChance = 100f
        trailColor = Color.valueOf("FF5845")
        trailLength = 6
        trailWidth = 4f
        trailInterval = 12f
        trailChance = 1f
        trailRotation = true
        trailEffect = ParticleEffect().apply {
          line = true
          particles = 3
          lifetime = 25f
          length = 24f
          baseLength = 0f
          lenFrom = 12f
          lenTo = 0f
          cone = 15f
          offsetX = -15f
          lightColor = Color.valueOf("FF8663")
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }
        hitEffect = MultiEffect().apply {
          effects = arrayOf(WrapEffect().apply {
            effect = Fx.dynamicSpikes
            color = Color.valueOf("FF5845")
            rotation = 120f
          }, ParticleEffect().apply {
            particles = 1
            lifetime = 60f
            length = 0f
            sizeFrom = 12f
            sizeTo = 0f
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF8663")
          }, ParticleEffect().apply {
            particles = 1
            length = 0f
            lifetime = 15f
            sizeFrom = 120f
            sizeTo = 120f
            interp = Interp.circle
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF866300")
          }, WaveEffect().apply {
            lifetime = 60f
            sizeFrom = 120f
            sizeTo = 120f
            strokeFrom = 4f
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF8663")
          })
        }
        despawnEffect = Fx.none
        fragBullets = 8
        fragLifeMin = 0.5f
        fragVelocityMin = 0.5f
        fragBullet = BasicBulletType().apply {
          sprite = "ice-star"
          damage = 115f
          lifetime = 40f
          speed = 4f
          spin = 8f
          width = 15f
          height = 15f
          shrinkY = 0f
          impact = true
          knockback = -24f
          frontColor = Color.valueOf("FF8663")
          backColor = Color.valueOf("FF5845")
          weaveMag = 2f
          weaveScale = 7f
          trailColor = Color.valueOf("FF5845")
          trailLength = 9
          trailWidth = 2f
          status = IStatus.熔融
          statusDuration = 60f
          homingPower = 0.04f
          homingRange = 160f
          splashDamage = 85f
          splashDamageRadius = 40f
          hitEffect = ParticleEffect().apply {
            particles = 1
            sizeFrom = 6f
            sizeTo = 0f
            length = 0f
            spin = 3f
            interp = Interp.swing
            lifetime = 100f
            region = "ice-star"
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF8663")
          }
          despawnEffect = ParticleEffect().apply {
            particles = 1
            sizeFrom = 6f
            sizeTo = 0f
            length = 0f
            spin = 3f
            interp = Interp.swing
            lifetime = 100f
            region = "ice-star"
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF8663")
          }
        }
        parts.addAll(HaloPart().apply {
          tri = true
          radius = 54f
          triLength = 6f
          haloRadius = 20f
          haloRotateSpeed = 4f
          color = Color.valueOf("FF5845")
          layer = 110f
        }, HaloPart().apply {
          tri = true
          radius = 6f
          triLength = 24f
          haloRadius = 23f
          shapeRotation = 150f
          haloRotateSpeed = 4f
          haloRotation = 30f
          color = Color.valueOf("FF5845")
          layer = 110f
        }, HaloPart().apply {
          tri = true
          radius = 6f
          triLength = 36f
          haloRadius = 24f
          shapeRotation = -15f
          haloRotateSpeed = 4f
          haloRotation = -135f
          color = Color.valueOf("FF5845")
          layer = 110f
        })
      }
      parts.add(RegionPart().apply {
        suffix = "-barrel"
        mirror = true
        under = true
        moveY = 7.25f
        moves.add(DrawPart.PartMove().apply {
          progress = DrawPart.PartProgress.recoil
          y = -1f
        })
        children.add(
          RegionPart().apply {
            suffix = "-top"
            mirror = true
            under = true
            x = 0.25f
            moveY = 6f
            layerOffset = -0.0001f
          })
      })
    }
    setWeapon("weapon2") {
      x = 0f
      shoot.apply {
        shots = 3
        shotDelay = 30f
        firstShotDelay = 120f
      }
      shake = 8f
      recoil = 0f
      shootY = 0f
      reload = 1200f
      mirror = false
      shootCone = 360f
      cooldownTime = 1080f
      shootStatus = IStatus.庇护
      shootStatusDuration = 180f
      chargeSound = Sounds.shootLaser
      shootSound = Sounds.shootLaser
      bullet = mindustry.entities.bullet.EmpBulletType().apply {
        damage = 0f
        lifetime = 30f
        speed = 12f
        collides = false
        hittable = false
        absorbable = false
        reflectable = false
        pierceArmor = true
        shootEffect = Fx.none
        instantDisappear = true
        radius = 480f
        timeIncrease = 1f
        unitDamageScl = 1f
        powerDamageScl = 0f
        powerSclDecrease = 0f
        hitColor = Color.valueOf("FF5845")
        status = IStatus.电磁脉冲
        statusDuration = 300f
        splashDamage = 200f
        splashDamageRadius = 480f
        scaledSplashDamage = true
        hitPowerEffect = ParticleEffect().apply {
          line = true
          particles = 6
          lifetime = 22f
          length = 120f
          cone = 360f
          lenFrom = 6f
          lenTo = 6f
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
        hitEffect = MultiEffect().apply {
          lifetime = 120f
          effects = arrayOf(ParticleEffect().apply {
            particles = 12
            length = 40f
            lifetime = 36f
            cone = 360f
            sizeFrom = 5f
            sizeTo = 0f
            interp = Interp.circleOut
            colorFrom = Color.valueOf("FF8663")
            colorTo = Color.valueOf("FF5845")
          }, ParticleEffect().apply {
            particles = 6
            lifetime = 9f
            sizeFrom = 3f
            sizeTo = 0f
            length = 60f
            baseLength = 8f
            cone = 20f
            colorFrom = Color.valueOf("FF8663")
            colorTo = Color.valueOf("FF5845")
          }, WaveEffect().apply {
            lifetime = 10f
            sizeFrom = 8f
            sizeTo = 50f
            strokeFrom = 2f
            colorFrom = Color.valueOf("FF8663")
            colorTo = Color.valueOf("FF5845")
          })
        }
        despawnEffect = MultiEffect().apply {
          lifetime = 120f
          effects = arrayOf(ParticleEffect().apply {
            particles = 1
            length = 0f
            lifetime = 15f
            sizeFrom = 480f
            sizeTo = 480f
            colorFrom = Color.valueOf("FF584580")
            colorTo = Color.valueOf("FF866300")
          }, WaveEffect().apply {
            lifetime = 60f
            sizeFrom = 480f
            sizeTo = 480f
            strokeFrom = 4f
            colorFrom = Color.valueOf("FF8663")
            colorTo = Color.valueOf("FF5845")
          })
        }
      }
      parts.addAll(RegionPart().apply {
        suffix = "-arrow"
        mirror = true
        outline = false
        progress = DrawPart.PartProgress.smoothReload.absin(20f, 1f)
        x = -68f
        y = 68f
        moveX = 11f
        moveY = -11f
        rotation = -135f
        layer = 110f
        color = "FFD37F".toColor()
        colorTo = "F15454".toColor()
      }, RegionPart().apply {
        suffix = "-arrow"
        mirror = true
        outline = false
        progress = DrawPart.PartProgress.smoothReload.absin(20f, 1f)
        x = 68f
        y = -68f
        moveX = -11f
        moveY = 11f
        rotation = 45f
        layer = 110f
        color = "FFD37F".toColor()
        colorTo = "F15454".toColor()
      }, ShapePart().apply {
        progress = DrawPart.PartProgress.smoothReload
        circle = true
        hollow = true
        radius = 72f
        stroke = 3f
        layer = 110f
        color = "FFD37F".toColor()
        colorTo = "F15454".toColor()
      })
    }
    setWeapon("weapon3") {
      x = 15.5f
      y = 56f
      reload = 90f
      shoot.apply {
        shots = 3
        shotDelay = 15f
      }
      recoil = 2f
      rotate = true
      rotateSpeed = 3f
      shootCone = 15f
      cooldownTime = 60f
      shootSound = Sounds.shootLaser
      bullet = LaserBulletType(55f).apply {
        lifetime = 15f
        length = 320f
        width = 24f
        colors = arrayOf(
          Color.valueOf("DE4136"), Color.valueOf("FF5845"), Color.valueOf("FF8663")
        )
        status = IStatus.熔融
        statusDuration = 60f
      }
    }.copyAdd {
      x = 21.75f
      y = 40f
    }
    setWeapon("weapon4") {
      x = -15f
      y = -33.5f
      recoil = 3f
      shake = 3f
      shootY = 9f
      reload = 60f
      shootCone = 10f
      shoot = ShootAlternate().apply {
        shots = 3
        spread = 8f
        shotDelay = 5f
      }
      rotate = true
      rotateSpeed = 3f
      cooldownTime = 85f
      shootSound = Sounds.shootScathe
      ejectEffect = Fx.casing4
      bullet = BasicBulletType().apply {
        damage = 100f
        lifetime = 19f
        speed = 20f
        shrinkY = 0f
        width = 16f
        height = 16f
        weaveMag = 1f
        weaveScale = 4f
        trailLength = 12
        trailWidth = 3.2f
        status = IStatus.湍能
        statusDuration = 60f
        trailColor = Color.valueOf("F9C27B")
        hitColor = Color.valueOf("F9C27B")
        absorbable = false
        reflectable = false
        splashDamage = 610f
        splashDamageRadius = 40f
        shootEffect = ParticleEffect().apply {
          particles = 3
          lifetime = 25f
          sizeFrom = 3f
          sizeTo = 0f
          cone = 15f
          length = 33f
          colorFrom = Color.valueOf("F9C27B")
          colorTo = Color.valueOf("F9C27A")
        }
        smokeEffect = Fx.shootSmokeSquare
        trailChance = 0.25f
        trailEffect = ParticleEffect().apply {
          particles = 1
          lifetime = 25f
          sizeFrom = 3f
          sizeTo = 0f
          cone = 360f
          length = 23f
          sizeInterp = Interp.pow10In
          colorFrom = Color.valueOf("F9C27B")
          colorTo = Color.valueOf("F9C27A")
        }
        hitShake = 8f
        hitSound = Sounds.plantBreak
        despawnEffect = Fx.none
        hitEffect = MultiEffect().apply {
          effects = arrayOf(ParticleEffect().apply {
            region = "blank"
            particles = 3
            lifetime = 33f
            sizeFrom = 5f
            sizeTo = 0f
            cone = 360f
            offset = 45f
            length = 55f
            baseLength = 33f
            interp = Interp.pow5Out
            sizeInterp = Interp.pow10In
            colorFrom = Color.valueOf("F9C27B")
            colorTo = Color.valueOf("F9C27A")
          }, ParticleEffect().apply {
            lifetime = 33f
            particles = 9
            line = true
            strokeFrom = 2f
            lenFrom = 11f
            lenTo = 0f
            cone = 360f
            length = 80f
            colorFrom = Color.valueOf("F9C27B")
            colorTo = Color.valueOf("F9C27A")
          }, WaveEffect().apply {
            lifetime = 15f
            sizeTo = 80f
            strokeFrom = 4f
            colorFrom = Color.valueOf("F9C27A")
            colorTo = Color.valueOf("F9C27B")
          })
        }
      }
    }
    weapons.add(PointDefenseWeapon().apply {
      x = 0f
      y = -31.75f
      recoil = 0f
      reload = 6f
      color = Color.valueOf("FF5845")
      targetInterval = 1f
      targetSwitchInterval = 1f
      shootSound = Sounds.shootLaser
      bullet = BulletType().apply {
        damage = 125f
        maxRange = 320f
        shootEffect = Fx.sparkShoot
        hitEffect = Fx.pointHit
      }
    })
    setWeapon {
      x = 0f
      y = -44.25f
      shootY = 0f
      reload = 300f
      mirror = false
      useAmmo = false
      baseRotation = 180f
      shootSound = Sounds.none
      alwaysShooting = true
      alwaysContinuous = true
      bullet = ContinuousFlameBulletType().apply {
        colors = arrayOf(
          Color.valueOf("FF58458C"), Color.valueOf("FF5845B2"), Color.valueOf("FF5845CC"), Color.valueOf("FF8663"), Color.valueOf("FF8663CC")
        )
        damage = 20f
        lifetime = 30f
        length = 45f
        width = 3f
        drawFlare = false
        status = IStatus.熔融
        statusDuration = 150f
        hitEffect = ParticleEffect().apply {
          line = true
          particles = 7
          lifetime = 15f
          length = 65f
          cone = -360f
          strokeFrom = 2.5f
          strokeTo = 0f
          lenFrom = 8f
          lenTo = 0f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FF8663")
        }
      }
    }
    fallSpeed = 0.0033333334f
    fallEffect = ParticleEffect().apply {
      line = true
      particles = 2
      length = 0.01f
      lifetime = 30f
      colorTo = Color.valueOf("FF584550")
      strokeFrom = 2f
      strokeTo = 0f
      lenFrom = 14f
      lenTo = 14f
    }
    fallEngineEffect = MultiEffect().apply {
      effects = arrayOf(ParticleEffect().apply {
        particles = 5
        length = 6f
        baseLength = 6f
        lifetime = 50f
        interp = Interp.pow3Out
        sizeInterp = Interp.pow5In
        cone = -30f
        offset = 30f
        colorFrom = Color.valueOf("787878")
        colorTo = Color.valueOf("787878")
        sizeFrom = 5f
        sizeTo = 0f
      }, ParticleEffect().apply {
        particles = 3
        length = 10f
        baseLength = 10f
        lifetime = 45f
        interp = Interp.pow3Out
        sizeInterp = Interp.pow5In
        cone = -30f
        offset = 30f
        colorFrom = Color.valueOf("FF8663")
        colorTo = Color.valueOf("FF5845AA")
        sizeFrom = 3.5f
        sizeTo = 0f
      })
    }
    bundle {
      desc(zh_CN, "无畏", "无畏级战列巡航舰,帝国舰队的中坚力量\\n配备了两门火力凶猛的荷电粒子炮以及广域脉冲发生器,可以过载大范围内敌军的引擎和武器系统以及敌方工事的能源系统")
    }
  }
  val 星光 = MissileUnitType("starlight").apply {
    bundle {
      desc(zh_CN, "星光")
    }
    health = 130f
    hitSize = 4f
    speed = 7f
    lifetime = 185f
    rotateSpeed = 3.6f
    engineColor = Color.valueOf("FEB380")
    trailColor = Color.valueOf("FEB380")
    engineLayer = 110f
    engineSize = 2f
    trailLength = 12
    lowAltitude = false
    missileAccelTime = 45f
    weapons.add(IceWeapon().apply {
      mirror = false
      shootCone = 360f
      shootOnDeath = true
      shootSound = Sounds.none
      bullet = ExplosionBulletType(24f, 72f).apply {
        hitEffect = MultiEffect(
          WaveEffect().apply {
            lifetime = 15f
            sizeFrom = 0f
            sizeTo = 30f
            strokeFrom = 3f
            strokeTo = 0f
            colorFrom = Color.valueOf("FEB380")
            colorTo = Color.valueOf("FF8663")
          },
          ParticleEffect().apply {
            particles = 3
            lifetime = 20f
            line = true
            strokeFrom = 1.5f
            strokeTo = 1.5f
            lenFrom = 5f
            lenTo = 5f
            cone = 360f
            length = 50f
            interp = Interp.circleOut
            sizeInterp = Interp.pow5In
            colorFrom = Color.valueOf("FEB380")
            colorTo = Color.valueOf("FF8663")
          },
          ParticleEffect().apply {
            particles = 3
            lifetime = 25f
            line = true
            strokeFrom = 1.5f
            strokeTo = 1.5f
            lenFrom = 7f
            lenTo = 7f
            cone = 360f
            length = 30f
            interp = Interp.circleOut
            sizeInterp = Interp.pow5In
            colorFrom = Color.valueOf("FEB380")
            colorTo = Color.valueOf("FF8663")
          }
        )
      }
    })
    parts.add(FlarePart().apply {
      color1 = Color.valueOf("FEB380")
      stroke = 4.5f
      radius = 0f
      radiusTo = 30f
      progress = DrawPart.PartProgress.life.curve(Interp.pow3Out)
    })
  }
  val 扑火 = IceUnitType("putotFire") {
    circleTarget = true
    faceTarget = false
    targetAir = false
    flying = true
    health = 230f
    hitSize = 9f
    armor = 2f
    range = 40f
    accel = 0.08f
    drag = 0.04f
    speed = 3.6f
    rotateSpeed = 6f
    engineSize = 2f
    engineOffset = 4.5f
    trailLength = 4
    engineLayer = 110f
    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 0.1f
    })

    setWeapon {
      reload = 65f
      shootCone = 360f
      shoot = ShootPattern().apply {
        shots = 3
        shotDelay = 5f
      }
      ignoreRotation = true
      minShootVelocity = 0.04f
      shootSound = Sounds.none
      bullet = BombBulletType(35f, 30f).apply {
        lifetime = 30f
        width = 9f
        height = 15f
        status = StatusEffects.blasted
        shootEffect = Fx.none
        smokeEffect = Fx.none
        hitEffect = Fx.flakExplosion
        despawnEffect = Fx.flakExplosion
      }
    }
    bundle {
      desc(zh_CN, "扑火", "微型轰炸机,以极高的机动性持续骚扰敌军")
    }
  }
  val 趋火 = IceUnitType("tuihuo") {
    bundle {
      desc(zh_CN, "趋火", "轻型轰炸机,配备五联装投弹器以快速杀伤敌军")
    }
    immunities.add(StatusEffects.wet)
    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 0.2f
    })
    circleTarget = true
    faceTarget = false
    targetAir = false
    flying = true
    health = 675f
    hitSize = 13f
    armor = 5f
    range = 40f
    accel = 0.08f
    drag = 0.016f
    speed = 3f
    rotateSpeed = 5f
    engineSize = 2.5f
    engineOffset = 7f
    trailLength = 4
    engineLayer = 110f
    setWeapon {
      reload = 55f
      shootCone = 360f
      shoot = ShootPattern().apply {
        shots = 5
        shotDelay = 5f
      }
      ignoreRotation = true
      minShootVelocity = 0.04f
      shootSound = Sounds.none
      bullet = BombBulletType(35f, 30f).apply {
        lifetime = 30f
        width = 9f
        height = 15f
        status = StatusEffects.blasted
        shootEffect = Fx.none
        smokeEffect = Fx.none
        hitEffect = Fx.flakExplosion
        despawnEffect = Fx.flakExplosion
      }
    }
  }
  val 奔火 = IceUnitType("benFire") {
    circleTarget = true
    faceTarget = false
    targetAir = false
    flying = true
    health = 1150f
    hitSize = 21f
    armor = 7f
    range = 40f
    accel = 0.07f
    drag = 0.016f
    speed = 2.4f
    rotateSpeed = 4.5f
    engineSize = 3f
    engineOffset = 7.5f
    trailLength = 4
    engineLayer = 110f
    bundle {
      desc(zh_CN, "奔火", "中型轰炸机,以趋火为基础提升航弹装药量并加装护盾发生器以持续作战")
    }
    immunities.add(StatusEffects.wet)
    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 0.25f
    }, ShieldRegenFieldAbility(40f, 240f, 120f, 120f))
    setWeapon {
      reload = 70f
      shootCone = 360f
      shoot = ShootPattern().apply {
        shots = 5
        shotDelay = 10f
      }
      ignoreRotation = true
      minShootVelocity = 0.04f
      shootSound = Sounds.drillImpact
      bullet = BombBulletType(60f, 40f).apply {
        sprite = "large-bomb"
        spin = 3f
        width = 16f
        height = 16f
        shrinkX = 0.9f
        shrinkY = 0.9f
        speed = 0f
        lifetime = 60f
        absorbable = false
        backColor = Color.valueOf("#FF5845")
        frontColor = Color.valueOf("#FF8663")
        hitSound = Sounds.explosionReactor
        hitEffect = WrapEffect(Fx.dynamicSpikes, Color.valueOf("#FF8663")).apply {
          rotation = 48f
        }
        hitShake = 3f
        despawnEffect = Fx.massiveExplosion
      }
    }
  }
  val 逐火 = IceUnitType("zhuFire") {
    circleTarget = true
    lowAltitude = true
    flying = true
    health = 9700f
    hitSize = 42f
    armor = 14f
    range = 40f
    accel = 0.06f
    drag = 0.017f
    speed = 2f
    rotateSpeed = 3.6f
    engineSize = 4f
    engineOffset = 6f
    immunities.addAll(StatusEffects.wet, StatusEffects.burning, StatusEffects.sporeSlowed)
    engines.addAll(UnitType.UnitEngine().apply {
      x = 9.75f
      y = -7f
      radius = 3f
      rotation = -90f
    }, UnitType.UnitEngine().apply {
      x = -9.75f
      y = -7f
      radius = 3f
      rotation = -90f
    })
    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 0.4f
    }, ForceFieldAbility(80f, 2f, 1200f, 120f, 4, 0f))
    bundle {
      desc(zh_CN, "逐火", "中型攻击机,配备两门近防机炮,两门高爆机炮及两门离子激光,初级气动外壳足以应对一部分异常状态", "在战争烈度逐渐升级当下,[逐火]攻击机应运而生,更强的火力及装甲使其足以担任小队护航或集群突袭等多种用途")
    }
    setWeapon("weapon1") {
      x = 21.25f
      y = -3f
      shake = 1f
      recoil = 2f
      shootY = 2f
      reload = 35f
      shoot = ShootPattern().apply {
        shots = 3
        shotDelay = 3f
      }
      rotate = true
      rotateSpeed = 4f
      shootSound = Sounds.shoot
      bullet = BasicBulletType().apply {
        damage = 55f
        speed = 5f
        lifetime = 48f
        width = 6f
        height = 9f
        splashDamage = 65f
        splashDamageRadius = 64f
        shootEffect = Fx.shootBig
        ammoMultiplier = 4f
        status = StatusEffects.blasted
        statusDuration = 60f
        hitEffect = Fx.flakExplosion
      }
    }
    setWeapon("weapon2") {
      x = 11f
      y = 2.75f
      shake = 3f
      recoil = 3f
      shootY = 7f
      reload = 180f
      rotate = true
      rotateSpeed = 2f
      rotationLimit = 45f
      shootSound = Sounds.shootLaser
      bullet = LaserBulletType(135f).apply {
        status = IStatus.熔融
        length = 200f
        lifetime = 15f
        shootEffect = ParticleEffect().apply {
          line = true
          particles = 12
          lifetime = 20f
          length = 45f
          cone = 30f
          lenFrom = 6f
          lenTo = 6f
          strokeFrom = 3f
          strokeTo = 0f
          interp = Interp.fastSlow
          lightColor = Color.valueOf("#FF5845")
          colorFrom = Color.valueOf("#FF8663")
          colorTo = Color.valueOf("#FF5845")
        }
        colors = arrayOf(
          Color.valueOf("#D75B6E"),
          Color.valueOf("#E78F92"),
          Color.valueOf("#FFF0F0")
        )
        statusDuration = 180f
        ammoMultiplier = 1f
        hitEffect = ParticleEffect().apply {
          line = true
          particles = 10
          lifetime = 20f
          length = 75f
          cone = -360f
          lenFrom = 6f
          lenTo = 6f
          strokeFrom = 3f
          strokeTo = 0f
          lightColor = Color.valueOf("#FF5845")
          colorFrom = Color.valueOf("#FF8663")
          colorTo = Color.valueOf("#FF5845")
        }
      }
    }
    setWeapon("weapon3") {
      x = 15.25f
      y = 3.75f
      shake = 2f
      recoil = 3f
      reload = 55f
      shootY = 7.25f
      shoot = ShootPattern().apply {
        shots = 2
        shotDelay = 3f
      }
      rotate = true
      rotateSpeed = 2f
      rotationLimit = 45f
      shootSound = Sounds.shootPulsar
      layerOffset = -0.001f
      bullet = BasicBulletType().apply {
        damage = 85f
        speed = 5f
        lifetime = 48f
        width = 9f
        height = 12f
        splashDamage = 35f
        splashDamageRadius = 32f
        shootEffect = Fx.shootBig
        ammoMultiplier = 4f
        status = StatusEffects.blasted
        statusDuration = 60f
        hitEffect = Fx.flakExplosionBig
      }
    }
  }
  val 赴火 = IceUnitType("fuFire") {
    bundle {
      desc(zh_CN, "赴火", "大型多功能轰炸机,配备八联装投弹系统及两门高爆机炮,高级气动外壳保证了其飞行速度在大多数情况下不会降低")
    }
    circleTarget = true
    flying = true
    health = 27000f
    hitSize = 48f
    armor = 25f
    range = 40f
    drag = 0.02f
    speed = 1.6f
    rotateSpeed = 3f
    engineSize = 5f
    engineOffset = 12f
    trailLength = 16
    engineLayer = 110f
    immunities.addAll(
      StatusEffects.wet,
      StatusEffects.burning,
      StatusEffects.freezing,
      StatusEffects.sporeSlowed,
      StatusEffects.tarred,
      StatusEffects.muddy,
      StatusEffects.electrified,
      IStatus.辐射
    )
    abilities.addAll(ArmorPlateAbility().apply {
      healthMultiplier = 0.5f
    })
    engines.addAll(UnitType.UnitEngine().apply {
      x = 9f
      y = -10f
      radius = 4f
      rotation = -90f
    }, UnitType.UnitEngine().apply {
      x = -9f
      y = -10f
      radius = 4f
      rotation = -90f
    })
    setWeapon("weapon1") {
      x = 19.25f
      y = 9.25f
      recoil = 3f
      shake = 2f
      reload = 60f
      shootY = 7.25f
      shoot = ShootPattern().apply {
        shots = 4
        shotDelay = 3f
      }
      rotate = true
      rotateSpeed = 2f
      alternate = false
      rotationLimit = 45f
      shootSound = Sounds.shoot
      layerOffset = -0.001f
      bullet = BasicBulletType().apply {
        damage = 135f
        speed = 4f
        lifetime = 48f
        drag = -0.01f
        width = 12f
        height = 15f
        splashDamage = 65f
        splashDamageRadius = 64f
        shootEffect = Fx.shootBig
        ammoMultiplier = 4f
        status = StatusEffects.blasted
        statusDuration = 60f
        hitEffect = Fx.flakExplosionBig
      }
    }
    setWeapon {
      x = 14f
      y = 6f
      shoot = ShootPattern().apply {
        shots = 4
        shotDelay = 30f
      }
      reload = 160f
      alternate = false
      shootCone = 360f
      ignoreRotation = true
      minShootVelocity = 0.04f
      shootSound = Sounds.shoot
      bullet = BombBulletType(90f, 60f).apply {
        sprite = "large-bomb"
        lifetime = 90f
        speed = 0f
        spin = 6f
        width = 32f
        height = 32f
        shrinkX = 0.9f
        shrinkY = 0.9f
        absorbable = false
        backColor = Color.valueOf("#FF5845")
        frontColor = Color.valueOf("#FF8663")
        despawnEffect = MultiEffect(
          WrapEffect(Fx.dynamicSpikes, Color.valueOf("FF5845"), 80f),
          ParticleEffect().apply {
            particles = 24
            sizeFrom = 9f
            sizeTo = 0f
            length = 80f
            baseLength = 8f
            lifetime = 30f
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF8663")
            cone = 360f
          })

        hitShake = 4f
        hitSound = Sounds.explosionPlasmaSmall
        hitEffect = Fx.massiveExplosion
        lightning = 3
        lightningLength = 15
        lightningDamage = 75f
        lightningColor = Color.valueOf("FF5845")
        status = IStatus.熔融
        statusDuration = 180f
        fragBullets = 4
        fragLifeMin = 0.7f
        fragBullet = BombBulletType(60f, 40f).apply {
          sprite = "large-bomb"
          lifetime = 20f
          speed = 16f
          shrinkX = 0.9f
          shrinkY = 0.9f
          width = 8f
          height = 8f
          absorbable = false
          backColor = Color.valueOf("FF5845")
          frontColor = Color.valueOf("FF8663")
          hitSound = Sounds.explosionPlasmaSmall
          hitEffect = WrapEffect(Fx.dynamicSpikes, Color.valueOf("FF8663"), 48f)
          hitShake = 3f
          despawnEffect = Fx.massiveExplosion
        }
      }
    }
  }

  val 化火 = IceUnitType("huaFire") {
    bundle {
      desc(zh_CN, "化火", "大型轰炸机,配备八联装投弹系统及两门高爆机炮,特种装甲外壳使其足以应对绝大部分负面状况")
    }

    circleTarget = true
    faceTarget = false
    flying = true
    health = 112000f
    hitSize = 58f
    armor = 34f
    range = 40f
    accel = 0.08f
    drag = 0.02f
    speed = 1.2f
    rotateSpeed = 1.8f
    payloadCapacity = 2304f
    engineSize = 6f
    engineOffset = 13f
    trailLength = 16
    engineLayer = 110f

    engines.add(UnitType.UnitEngine().apply {
      x = 16f
      y = -22f
      radius = 5f
      rotation = -45f
    })
    engines.add(UnitType.UnitEngine().apply {
      x = -16f
      y = -22f
      radius = 5f
      rotation = -135f
    })
    immunities.addAll(
      StatusEffects.burning,
      StatusEffects.melting,
      StatusEffects.blasted,
      StatusEffects.wet,
      StatusEffects.freezing,
      StatusEffects.sporeSlowed,
      StatusEffects.slow,
      StatusEffects.tarred,
      StatusEffects.muddy,
      StatusEffects.sapped,
      StatusEffects.electrified,
      StatusEffects.unmoving,
      IStatus.熔融,
      IStatus.辐射,
      IStatus.衰变
    )
    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 0.8f
    })
    setWeapon {
      x = 31f
      y = -8f
      shoot = ShootBarrel().apply {
        shots = 3
        shotDelay = 5f
        barrels = floatArrayOf(0f, 0f, -20f, 0f, 0f, -30f, 0f, 0f, -40f)
      }
      shootY = 0f
      reload = 90f
      shootCone = 360f
      shootSound = Sounds.shootMissile
      bullet = BulletType().apply {
        spawnUnit = 星光
        speed = 0f
        shootEffect = ParticleEffect().apply {
          lifetime = 35f
          particles = 10
          length = 40f
          cone = 20f
          sizeFrom = 4f
          sizeTo = 0f
          baseRotation = 180f
          interp = Interp.circleOut
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
      }
    }
    setWeapon {
      x = 22f
      y = 12f
      shoot = ShootPattern().apply {
        shots = 4
        shotDelay = 30f
      }
      reload = 360f
      alternate = false
      shootCone = 360f
      ignoreRotation = true
      minShootVelocity = 0.04f
      shootSound = Sounds.shootBeamPlasma
      bullet = EmpBulletType().apply {
        sprite = "large-bomb"
        damage = 137f
        lifetime = 90f
        drag = 0.05f
        speed = 1f
        spin = 6f
        width = 32f
        height = 32f
        shrinkX = 0.9f
        shrinkY = 0.9f
        collides = false
        collidesAir = false
        absorbable = false
        collidesTiles = false
        keepVelocity = false
        backColor = Color.valueOf("FF5845")
        frontColor = Color.valueOf("FF8663")
        hitColor = Color.valueOf("FF5845")
        despawnEffect = MultiEffect(
          WrapEffect(Fx.dynamicSpikes, Color.valueOf("FF5845"), 120f),
          ParticleEffect().apply {
            particles = 24
            sizeFrom = 9f
            sizeTo = 0f
            length = 80f
            baseLength = 8f
            lifetime = 30f
            colorFrom = Color.valueOf("FF5845")
            colorTo = Color.valueOf("FF8663")
            cone = 360f
          }
        )
        radius = 120f
        unitDamageScl = 1.2f
        powerDamageScl = 1.5f
        hitPowerEffect = ParticleEffect().apply {
          particles = 7
          lifetime = 22f
          line = true
          lenFrom = 6f
          lenTo = 6f
          cone = 360f
          length = 80f
          colorFrom = Color.valueOf("FF8663")
          colorTo = Color.valueOf("FF5845")
        }
        status = IStatus.熔融
        statusDuration = 180f
        splashDamage = 915f
        splashDamageRadius = 120f
        scaledSplashDamage = true
        lightning = 3
        lightningLength = 11
        lightningLengthRand = 5
        lightningDamage = 75f
        lightningColor = Color.valueOf("FF5845")
        hitShake = 4f
        hitSound = Sounds.shootBeamPlasma
        hitEffect = Fx.massiveExplosion
        fragBullets = 1
        fragBullet = EmpBulletType().apply {
          sprite = "stardart"
          lifetime = 480f
          damage = 0f
          speed = 0f
          width = 8f
          height = 8f
          shrinkY = 0f
          collides = false
          collidesAir = false
          absorbable = false
          collidesTiles = false
          backColor = Color.valueOf("FF5845")
          frontColor = Color.valueOf("FF8663")
          hitColor = Color.valueOf("FF8663")
          despawnEffect = Fx.none
          status = IStatus.熔融
          statusDuration = 30f
          splashDamage = 537f
          splashDamageRadius = 80f
          scaledSplashDamage = true
          radius = 80f
          unitDamageScl = 1.2f
          powerDamageScl = 1.5f
          hitPowerEffect = ParticleEffect().apply {
            particles = 7
            lifetime = 22f
            line = true
            lenFrom = 6f
            lenTo = 6f
            cone = 360f
            length = 80f
            colorFrom = Color.valueOf("FF8663")
            colorTo = Color.valueOf("FF5845")
          }
          bulletInterval = 48f
          intervalBullet = SglEmpBulletType().apply {
            damage = 0f
            hittable = false
            collides = false
            collidesAir = false
            absorbable = false
            instantDisappear = true
            despawnShake = 1f
            hitColor = Color.valueOf("FF8663")
            status = IStatus.熔融
            statusDuration = 30f
            splashDamage = 73f
            splashDamageRadius = 48f
            scaledSplashDamage = true
            splashDamagePierce = true
            radius = 48f
            unitDamageScl = 1.2f
            powerDamageScl = 1.5f
            hitPowerEffect = ParticleEffect().apply {
              particles = 7
              lifetime = 22f
              line = true
              lenFrom = 6f
              lenTo = 6f
              cone = 360f
              length = 80f
              colorFrom = Color.valueOf("FF8663")
              colorTo = Color.valueOf("FF5845")
            }
            despawnEffect = Fx.none
            hitEffect = WaveEffect().apply {
              lifetime = 24f
              sizeFrom = 0f
              sizeTo = 48f
              strokeFrom = 4f
              strokeTo = 0f
              colorFrom = Color.valueOf("FF5845")
              colorTo = Color.valueOf("FF8663")
            }
          }
          fragBullets = 4
          fragBullet = BombBulletType(465f, 48f).apply {
            sprite = "large-bomb"
            lifetime = 20f
            speed = 16f
            shrinkX = 0.9f
            shrinkY = 0.9f
            drag = 0.2f
            width = 8f
            height = 8f
            collides = false
            collidesAir = false
            absorbable = false
            backColor = Color.valueOf("FF5845")
            frontColor = Color.valueOf("FF8663")
            incendAmount = 3
            status = IStatus.熔融
            statusDuration = 60f
            hitSound = Sounds.explosion
            hitEffect = WrapEffect(Fx.dynamicSpikes, Color.valueOf("FF8663"), 40f)
            hitShake = 3f
            despawnEffect = Fx.massiveExplosion
          }
        }
      }
    }
  }

  fun lightning(lifeTime: Float, time: Float, damage: Float, size: Float, color: Color?, generator: Func<Bullet?, LightningGenerator>): LightningBulletType {
    return object : LightningBulletType(0f, damage) {
      init {
        lifetime = lifeTime
        collides = false
        hittable = false
        absorbable = false
        reflectable = false

        hitColor = color
        hitEffect = Fx.hitLancer
        shootEffect = Fx.none
        despawnEffect = Fx.none
        smokeEffect = Fx.none

        status = StatusEffects.shocked
        statusDuration = 18f

        drawSize = 120f
      }

      override fun init(b: Bullet, container: LightningContainer) {
        container.time = time
        container.lifeTime = lifeTime
        container.maxWidth = size
        container.minWidth = size * 0.85f
        container.lerp = Interp.linear

        container.trigger = Cons2 { last: LightningVertex?, vert: LightningVertex? ->
          if (!b.isAdded) return@Cons2
          Tmp.v1.set(vert!!.x - last!!.x, vert.y - last.y)
          val resultLength = Damage.findPierceLength(b, pierceCap, Tmp.v1.len())

          Damage.collideLine(
            b, b.team, b.x + last.x, b.y + last.y, Tmp.v1.angle(), resultLength, false, false, pierceCap
          )
          b.fdata = resultLength
        }
        val gen = generator.get(b)
        gen.blockNow = Floatp2 { last: LightningVertex?, vertex: LightningVertex? ->
          val abs = Damage.findAbsorber(b.team, b.x + last!!.x, b.y + last.y, b.x + vertex!!.x, b.y + vertex.y) ?: return@Floatp2 -1f
          val ox = b.x + last.x
          val oy = b.y + last.y
          Mathf.len(abs.x - ox, abs.y - oy)
        }
        container.create(gen)
      }
    }
  }
}