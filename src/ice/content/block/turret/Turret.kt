package ice.content.block.turret

import arc.Core
import arc.func.Boolf
import arc.func.Cons
import arc.func.Func2
import arc.func.Func3
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Angles
import arc.math.Interp
import arc.math.Mathf
import arc.util.Strings
import arc.util.Time
import arc.util.Tmp
import arc.util.pooling.Pools
import ice.audio.ISounds
import ice.content.IItems
import ice.content.ILiquids
import ice.content.IStatus
import ice.content.block.turret.TurretBullets.addAmmoType
import ice.content.block.turret.TurretBullets.branch
import ice.content.block.turret.TurretBullets.crushedIce
import ice.content.block.turret.TurretBullets.freezingField
import ice.content.block.turret.TurretBullets.graphiteCloud
import ice.content.block.turret.TurretBullets.lightning
import ice.content.block.turret.TurretBullets.rand
import ice.content.block.turret.TurretBullets.破碎FEX结晶
import ice.entities.bullet.LaserBulletType
import ice.entities.bullet.RailBulletType
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import ice.graphics.IceColor
import ice.library.IFiles.appendModName
import ice.library.util.toColor
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeItems
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.consumeLiquids
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.part.IcePartProgress
import ice.world.meta.IceEffects
import mindustry.Vars
import mindustry.audio.SoundLoop
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.content.StatusEffects
import mindustry.entities.*
import mindustry.entities.bullet.ContinuousLaserBulletType
import mindustry.entities.bullet.LightningBulletType
import mindustry.entities.bullet.MissileBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.part.*
import mindustry.entities.pattern.*
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import mindustry.world.blocks.defense.turrets.ItemTurret
import mindustry.world.blocks.defense.turrets.PowerTurret
import mindustry.world.consumers.ConsumeCoolant
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawMulti
import mindustry.world.draw.DrawTurret
import mindustry.world.meta.StatUnit
import singularity.Sgl
import singularity.Singularity
import singularity.graphic.MathRenderer
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.ui.UIUtils
import singularity.util.MathTransform
import singularity.world.SglFx
import singularity.world.SglUnitSorts
import singularity.world.blocks.SglBlock.SglBuilding
import singularity.world.blocks.turrets.*
import singularity.world.blocks.turrets.SglTurret.SglTurretBuild
import singularity.world.draw.DrawSglTurret
import singularity.world.draw.part.CustomPart
import singularity.world.meta.SglStat
import singularity.world.particles.SglParticleModels
import universecore.world.lightnings.LightningContainer
import universecore.world.lightnings.LightningVertex
import universecore.world.lightnings.generator.CircleGenerator
import universecore.world.lightnings.generator.RandomGenerator
import universecore.world.lightnings.generator.VectorLightningGenerator
import kotlin.math.max
import kotlin.math.min

@Suppress("unused", "DuplicatedCode")
object Turret : Load {
  val 碎冰 = ItemTurret("trashIce").apply {
    bundle {
      desc(zh_CN, "碎冰")
    }
    size = 1
    health = 250
    recoil = 0.5f
    shootY = 3f
    reload = 45f
    range = 160f
    shootCone = 30f
    squareSprite=false
    shoot = ShootSummon().apply {
      x = 0f
      y = 0f
      spread = 5f
      shots = 5
      shotDelay = 3f
    }
    shootSound = ISounds.laser1
    shootEffect = Effect(8.0f) { e: Effect.EffectContainer ->
      Draw.color(IceColor.b4, Color.white, e.fin())
      val w = 1.0f + 5.0f * e.fout()
      Drawf.tri(e.x, e.y, w, 15.0f * e.fout(), e.rotation)
      Drawf.tri(e.x, e.y, w, 3.0f * e.fout(), e.rotation + 180.0f)
    }
    ammo(IItems.硫钴矿, BasicBulletType(5f, 9f).apply {
      width = 2f
      height = 9f
      lifetime = 30f
      ammoMultiplier = 2f
      despawnEffect = IceEffects.基础子弹击中特效
      hitEffect = despawnEffect
      trailColor = IceColor.b4
      backColor = IceColor.b4
      hitColor = IceColor.b4
      frontColor = IceColor.b4
    })
    requirements(Category.turret, IItems.铬铁矿, 10, IItems.低碳钢, 20)
    drawer = DrawTurret().apply {
      parts.add(RegionPart("-barrel").apply {
        progress = DrawPart.PartProgress.recoil
        under = true
        heatColor = IceColor.b4
        heatProgress = DrawPart.PartProgress.recoil
        moveY = -1.5f
      })
    }
  }
  val 神矢 = PowerTurret("divineArrow").apply {
    bundle {
      desc(zh_CN, "神矢")
    }
    size = 2
    health = 1000
    requirements(Category.turret, ItemStack.with(IItems.铬铁矿, 10, IItems.低碳钢, 20))
    reload = 30f
    recoils = 2
    squareSprite=false
    drawer = DrawTurret().apply {
      for (i in 0..1) {
        parts.add(object : RegionPart("-" + (if (i == 0) "l" else "r")) {
          init {
            progress = PartProgress.recoil
            recoilIndex = i
            under = true
            moveY = -1.5f
          }
        })
      }
      parts.add(ShapePart().apply {
        hollow = true
        radius = 4f
        layer = 110f
        sides = 4
        y = -4f
        color = IceColor.b4
        rotateSpeed = 2f
        progress = DrawPart.PartProgress.recoil
      })
      parts.add(ShapePart().apply {
        hollow = true
        radius = 0f
        radiusTo = 4f
        layer = 110f
        sides = 4
        stroke = 0.5f
        rotateSpeed = 2f
        y = -4f
        color = IceColor.b4
        progress = IcePartProgress { p: DrawPart.PartParams ->
          DrawPart.PartProgress.warmup.get(p) * ((Time.time / 15) % 1)
        }
      })
    }
    shoot = object : ShootAlternate() {
      var scl: Float = 2f
      var mag: Float = 1.5f
      var offset: Float = Mathf.PI * 1.25f
      override fun shoot(totalShots: Int, handler: BulletHandler, barrelIncrementer: Runnable?) {
        for (i in 0..<shots) {
          for (sign in Mathf.signs) {
            val index = ((totalShots + i + barrelOffset) % barrels) - (barrels - 1) / 2f
            handler.shoot(index * spread * -Mathf.sign(mirror), 0f, 0f, firstShotDelay + shotDelay * i) { b ->
              b.moveRelative(0f, Mathf.sin(b.time + offset, scl, mag * sign))
            }
          }
          barrelIncrementer?.run()
        }
      }
    }.apply {
      barrelOffset = 8
      spread = 5f
      shots = 2
      shotDelay = 15f
    }
    shootSound = Sounds.shootMissile
    shootY = 6f
    shootEffect = IceEffects.squareAngle(range = 30f, color1 = IceColor.b4, color2 = Color.white)
    shootType = MissileBulletType(6f, 30f).apply {
      splashDamageRadius = 30f
      splashDamage = 30f * 1.5f
      lifetime = 45f
      trailLength = 20
      trailWidth = 1.5f
      trailColor = IceColor.b4
      backColor = IceColor.b4
      hitColor = IceColor.b4
      frontColor = IceColor.b4
      despawnEffect = IceEffects.blastExplosion(IceColor.b4)
      hitEffect = despawnEffect
    }
    range = shootType.speed * shootType.lifetime
  }
  val 绪终 = ItemTurret("thinkEnd").apply {
    bundle {
      desc(zh_CN, "绪终")
    }
    size = 5
    shoot.apply {
      firstShotDelay = 120f
      recoils = 1
      reload = 120f
      shootWarmupSpeed = 0.05f
    }
    ammo(IItems.暮光合金, BasicBulletType(4f, 4f))
    requirements(Category.turret, ItemStack.with(IItems.铜锭, 10, IItems.单晶硅, 5))
    drawer = DrawTurret().apply {
      parts.add(RegionPart("4-l").apply {
        moveY = -4f
        moveX = -8f
        moveRot = 60f
        heatColor = Color.valueOf("c3baff").a(0.5f)
        heatProgress = DrawPart.PartProgress.warmup
      })
      parts.add(RegionPart("4-r").apply {
        moveY = -4f
        moveX = 8f
        moveRot = -60f
        heatColor = Color.valueOf("c3baff").a(0.5f)
        heatProgress = DrawPart.PartProgress.warmup
      })
      parts.add(RegionPart("1").apply {
        moveY = 2f
        progress = DrawPart.PartProgress.warmup.curve(Interp.pow2)
        heatColor = Color.valueOf("c3baff").a(0.5f)
        heatProgress = DrawPart.PartProgress.warmup
      })
      parts.add(RegionPart("2-l").apply {
        moveY = -2f
        moveRot = 25f
        heatColor = Color.valueOf("c3baff").a(0.5f)
        heatProgress = DrawPart.PartProgress.warmup.curve(Interp.pow5In)
      })
      parts.add(RegionPart("2-r").apply {
        moveY = -2f
        moveRot = -25f
        heatColor = Color.valueOf("c3baff").a(0.5f)
        heatProgress = DrawPart.PartProgress.warmup.curve(Interp.pow5In)
      })

      parts.add(RegionPart("3").apply {
        heatColor = Color.valueOf("c3baff").a(0.5f)
        heatProgress = DrawPart.PartProgress.warmup
      })
      bundle {
        desc(zh_CN, "绪终")
      }
    }
  }
  val 冲穿 = ItemTurret("breakThrough").apply {
    health = 5600
    size = 5
    range = 720f
    reload = 300f
    shake = 5f
    recoil = 4f
    maxAmmo = 60
    recoilTime = 300f
    ammoPerShot = 15
    cooldownTime = 300f
    shootSound = Sounds.shootToxopidShotgun
    unitSort = UnitSorts.strongest
    shootCone = 2f
    rotateSpeed = 1.4f
    minWarmup = 0.96f
    shootWarmupSpeed = 0.08f
    warmupMaintainTime = 300f
    shoot = ShootSpread().apply {
      shots = 5
      spread = 1f
    }
    liquidCapacity = 30f
    consumePower(25f)
    consume(ConsumeCoolant(1.5f))
    coolantMultiplier = 0.333f
    bundle {
      desc(zh_CN, "冲穿", "以临界速度发射五道远程穿透磁轨炮摧毁敌人,比裂颅更强")
    }
    addAmmoType(IItems.钍锭) {
      RailBulletType().apply {
        damage = 720f
        knockback = 10f
        lifetime = 30f
        length = 720f
        ammoMultiplier = 1f
        pierce = true
        pierceDamageFactor = 0.4f
        status = StatusEffects.unmoving
        statusDuration = 900f
        shootEffect = Fx.instShoot
        pointEffect = Fx.railTrail
        hitEffect = Fx.railHit
      }
    }
    addAmmoType(IItems.金锭) {
      RailBulletType().apply {
        damage = 1200f
        knockback = 20f
        lifetime = 30f
        length = 720f
        ammoMultiplier = 1f
        pierce = true
        pierceDamageFactor = 0.2f
        status = StatusEffects.melting
        statusDuration = 900f
        shootEffect = Fx.instShoot
        pointEffect = Fx.railTrail
        hitEffect = Fx.railHit
      }
    }
    addAmmoType(IItems.铱板) {
      RailBulletType().apply {
        damage = 960f
        knockback = 15f
        lifetime = 30f
        length = 720f
        ammoMultiplier = 1f
        pierce = true
        pierceDamageFactor = 0.3f
        status = StatusEffects.burning
        statusDuration = 900f
        shootEffect = Fx.instShoot
        pointEffect = Fx.railTrail
        hitEffect = Fx.railHit
        pierceEffect = ParticleEffect().apply {
          line = true
          particles = 60
          offset = 0f
          lifetime = 15f
          length = 40f
          cone = -7.5f
          lenFrom = 6f
          lenTo = 0f
          colorFrom = "D86E56".toColor()
          colorTo = "FFFFFF".toColor()
        }
      }
    }
    requirements(Category.turret, IItems.铜锭, 1800, IItems.钴锭, 840, IItems.铱板, 630, IItems.导能回路, 435, IItems.陶钢, 225)
    drawer = DrawTurret().apply {
      parts.addAll(RegionPart("-barrel").apply {
        moveY = -1.5f
        moves.add(DrawPart.PartMove(DrawPart.PartProgress.recoil, 0f, -3f, 0f))
        heatColor = "F03B0E".toColor()
      }, RegionPart("-top").apply {
        heatProgress = DrawPart.PartProgress.warmup
        mirror = true
        under = true
        moveX = 4f
        moveY = 8.25f
        heatColor = "F03B0E".toColor()
      }, RegionPart("-side").apply {
        heatProgress = DrawPart.PartProgress.warmup
        mirror = true
        under = true
        moveX = 4f
        moveY = 0f
        heatColor = "F03B0E".toColor()
      }, HaloPart().apply {
        tri = true
        y = -16f
        shapes = 2
        radius = 0f
        radiusTo = 3f
        triLength = 16f
        haloRadius = 15f
        haloRotation = 90f
        color = "FF5845".toColor()
        layer = 110f
      }, HaloPart().apply {
        tri = true
        y = -16f
        shapeRotation = 180f
        shapes = 2
        radius = 0f
        radiusTo = 3f
        triLength = 4f
        haloRadius = 15f
        haloRotation = 90f
        color = "FF5845".toColor()
        layer = 110f
      }, ShapePart().apply {
        y = -16f
        circle = true
        hollow = true
        radius = 4f
        stroke = 0f
        strokeTo = 1f
        color = "FF5845".toColor()
        layer = 110f
      }, ShapePart().apply {
        y = -16f
        circle = true
        hollow = true
        radius = 8f
        stroke = 0f
        strokeTo = 1f
        color = "FF5845".toColor()
        layer = 110f
      }, HaloPart().apply {
        hollow = false
        mirror = false
        tri = true
        x = 0f
        y = -16f
        shapeRotation = 0f
        moveX = 0f
        moveY = 0f
        shapeMoveRot = 0f
        shapes = 4
        sides = 5
        radius = 0f
        radiusTo = 3f
        stroke = 1f
        strokeTo = -1f
        triLength = 6f
        triLengthTo = -1f
        haloRadius = 12f
        haloRadiusTo = -1f
        haloRotateSpeed = 1f
        haloRotation = 0f
        rotateSpeed = 0f
        color = "FF5845".toColor()
        layer = 110f
      }, HaloPart().apply {
        tri = true
        y = -16f
        shapeRotation = 180f
        shapes = 4
        radius = 0f
        radiusTo = 3f
        triLength = 2f
        haloRadius = 12f
        haloRotateSpeed = 1f
        color = "FF5845".toColor()
        layer = 110f
      }, HaloPart().apply {
        tri = true
        y = -16f
        shapes = 4
        radius = 3f
        triLength = 0f
        triLengthTo = 2f
        haloRadius = 8f
        haloRotateSpeed = -1f
        color = "FF5845".toColor()
        layer = 110f
      })
    }
  }
  val 光谱 = PowerTurret("spectral").apply {
    health = 1380
    size = 3
    recoil = 2f
    shootY = 4f
    range = 256f
    reload = 120f
    shootCone = 3f
    rotateSpeed = 5f
    recoilTime = 210f
    cooldownTime = 210f
    minWarmup = 0.9f
    shootWarmupSpeed = 0.08f
    squareSprite=false
    shoot = ShootSpread().apply {
      shots = 3
      shotDelay = 15f
    }
    shake = 2f
    consumePower(14f)
    consume(ConsumeCoolant(0.3f))
    liquidCapacity = 40f
    coolantMultiplier = 3f
    shootSound = Sounds.shootLaser
    shootType = LaserBulletType(135f).apply {
      length = 256f
      shootEffect = ParticleEffect().apply {
        line = true
        particles = 12
        lifetime = 20f
        length = 45f
        cone = 30f
        lenFrom = 6f
        lenTo = 6f
        strokeFrom = 3f
        interp = Interp.fastSlow
        colorFrom = "FFDCD8".toColor()
        colorTo = "FF5845".toColor()
      }
      colors = arrayOf("D75B6E".toColor(), "E78F92".toColor(), "FFF0F0".toColor())
      ammoMultiplier = 1f
      status = StatusEffects.melting
      statusDuration = 30f
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
        lightColor = "FF5845".toColor()
        colorFrom = "FFDCD8".toColor()
        colorTo = "FF5845".toColor()
      }
    }
    drawer = DrawTurret().apply {
      parts.add(RegionPart("-side").apply {
        mirror = true
        moveX = 1f
        children.add(RegionPart("-top").apply {
          mirror = true
          moveX = 0.25f
          moveY = 1.75f
        })
      })
    }
    requirements(Category.turret, IItems.铜锭, 120, IItems.铬锭, 140, IItems.钍锭, 60, IItems.单晶硅, 120)
    bundle {
      desc(zh_CN, "光谱", "中型能量炮塔,可以快速向敌人发射高热激光")
    }
  }
  val 撕裂 = PowerTurret("tear").apply {
    squareSprite=false
    health = 19200
    size = 8
    range = 768f
    reload = 60f
    cooldownTime = 45f
    shake = 4f
    shootY = 0f
    recoil = 8f
    recoilTime = 45f
    shootCone = 5f
    rotateSpeed = 0.8f
    minWarmup = 0.97f
    shootWarmupSpeed = 0.08f
    warmupMaintainTime = 300f
    consumePower(272f)
    consumeItems(IItems.肃正协议, 4)
    consumeLiquids(ILiquids.急冻液, 4f)
    itemCapacity = 4
    liquidCapacity = 120f
    canOverdrive = false
    shoot = ShootMulti(ShootHelix().apply {
      scl = 3f
      mag = 0.75f
    }, ShootHelix().apply {
      mag = 3f
      scl = 0.75f
    })
    shootSound = ISounds.聚爆
    drawer = DrawTurret().apply {
      parts.addAll(RegionPart("-side").apply {
        heatProgress = DrawPart.PartProgress.warmup
        under = true
        mirror = true
        moveX = 1f
        moveY = -1f
        heatColor = "F03B0E".toColor()
      }, RegionPart("-part").apply {
        heatProgress = DrawPart.PartProgress.warmup
        drawRegion = false
        heatColor = "F03B0E".toColor()
      })
    }
    bundle {
      desc(zh_CN, "撕裂", "一座强大的电磁轨道炮,超长轨道,超大力度,可以快速地进行精准射击")
    }
    requirements(Category.turret, IItems.铜锭, 9600, IItems.铬锭, 6400, IItems.铱板, 3600, IItems.导能回路, 2400, IItems.陶钢, 1920, IItems.生物钢, 1200)
    shootType = BasicBulletType(16f, 840f, "gauss-bullet").apply {
      lifetime = 48f
      shrinkY = 0f
      height = 32f
      width = 26f
      ammoMultiplier = 1f
      frontColor = "FF8663".toColor()
      backColor = "FF5845".toColor()
      hittable = false
      pierceCap = 2
      status = StatusEffects.melting
      statusDuration = 180f
      splashDamage = 240f
      splashDamageRadius = 80f
      buildingDamageMultiplier = 0.5f
      trailColor = "FF5845".toColor()
      trailLength = 24
      trailWidth = 3f
      trailSinScl = 0.75f
      trailSinMag = 1.5f
      hitShake = 3f
      despawnShake = 4f
      knockback = 5f
      lightning = 4
      lightningLength = 12
      lightningDamage = 255f
      lightningColor = "FF5845".toColor()
      hitEffect = MultiEffect(WaveEffect().apply {
        lifetime = 20f
        sizeFrom = 0f
        sizeTo = 65f
        strokeFrom = 4f
        strokeTo = 0f
        lightColor = "FF5845".toColor()
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      }, ParticleEffect().apply {
        line = true
        particles = 11
        lifetime = 30f
        length = 85f
        baseLength = 20f
        cone = -360f
        lenFrom = 7f
        lenTo = 0f
        interp = Interp.exp10In
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      })

      despawnEffect = MultiEffect(ParticleEffect().apply {
        particles = 1
        sizeFrom = 45f
        sizeTo = 0f
        length = 0f
        interp = Interp.bounceOut
        lifetime = 60f
        region = "star".appendModName()
        lightColor = "FF5845".toColor()
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
        layer = 110f
      }, WaveEffect().apply {
        lifetime = 20f
        sizeFrom = 0f
        sizeTo = 65f
        strokeFrom = 4f
        strokeTo = 0f
        interp = Interp.elasticOut
        lightColor = "FF5845".toColor()
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      }, ParticleEffect().apply {
        line = true
        particles = 11
        lifetime = 30f
        length = 85f
        baseLength = 20f
        cone = -360f
        lenFrom = 7f
        lenTo = 0f
        interp = Interp.exp10In
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      })

      shootEffect = MultiEffect(ParticleEffect().apply {
        particles = 4
        sizeFrom = 6f
        sizeTo = 0f
        length = 70f
        lifetime = 30f
        interp = Interp.sineOut
        sizeInterp = Interp.sineIn
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
        cone = 15f
      }, WaveEffect().apply {
        lifetime = 30f
        sides = 0
        sizeFrom = 0f
        sizeTo = 40f
        strokeFrom = 4f
        strokeTo = 0f
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      })
    }
  }
  val 隧穿 = ItemTurret("tunnelOpening").apply {
    squareSprite=false
    health = 3450
    size = 5
    recoil = 3f
    shootY = 10f
    shake = 3.5f
    range = 264f
    reload = 378f
    shootCone = 15f
    maxAmmo = 40
    recoilTime = 192f
    cooldownTime = 270f
    rotateSpeed = 2f
    ammoPerShot = 10
    liquidCapacity = 40f
    coolantMultiplier = 0.4f
    minWarmup = 0.96f
    shootWarmupSpeed = 0.08f
    warmupMaintainTime = 300f
    shootSound = Sounds.shootScathe
    unitSort = UnitSorts.farthest
    shootEffect = Fx.bigShockwave
    ammoUseEffect = Fx.casing3Double
    consumePower(17f)
    consume(ConsumeCoolant(1.5f))
    drawer = DrawTurret().apply {
      parts.addAll(RegionPart("-shot").apply {
        progress = DrawPart.PartProgress.recoil
        moveY = -4.5f
        children.add(RegionPart("-shot-glow").apply {
          heatProgress = DrawPart.PartProgress.warmup
          drawRegion = false
          heatColor = "F03B0E".toColor()
        })
      }, RegionPart("-glow").apply {
        heatProgress = DrawPart.PartProgress.warmup
        drawRegion = false
        heatColor = "F03B0E".toColor()
      }, HaloPart().apply {
        tri = true
        y = -20f
        radius = 27f
        triLength = 0f
        triLengthTo = 3f
        haloRadius = 10f
        haloRotateSpeed = 0.5f
        color = "FF5845".toColor()
        layer = 110f
      }, HaloPart().apply {
        tri = true
        y = -20f
        radius = 3f
        triLength = 0f
        triLengthTo = 12f
        haloRadius = 11.5f
        shapeRotation = 150f
        haloRotateSpeed = 0.5f
        haloRotation = 30f
        color = "FF5845".toColor()
        layer = 110f
      }, HaloPart().apply {
        tri = true
        y = -20f
        radius = 3f
        triLength = 0f
        triLengthTo = 18f
        haloRadius = 12f
        shapeRotation = -15f
        haloRotateSpeed = 0.5f
        haloRotation = -135f
        color = "FF5845".toColor()
        layer = 110f
      })
    }
    requirements(Category.turret, IItems.铜锭, 1120, IItems.钴锭, 470, IItems.钍锭, 390, IItems.铬锭, 280, IItems.铱板, 225, IItems.爆炸化合物, 65)
    bundle {
      desc(zh_CN, "隧穿", "向指定方位发射三道强劲的定向爆破束,并在到达极限距离后原路返回")
    }
    shoot = ShootSpread().apply {
      shots = 3
      spread = 15f
    }
    addAmmoType(IItems.铬锭) {
      BasicBulletType().apply {
        damage = 135f
        lifetime = 120f
        speed = 30f
        drag = 0.1f
        width = 14f
        height = 25f
        pierce = true
        collides = false
        pierceBuilding = true
        bulletInterval = 1f

        intervalBullet = BulletType().apply {
          damage = 0f
          hitShake = 2f
          despawnShake = 1f
          splashDamage = 45f
          splashDamageRadius = 20f
          scaledSplashDamage = true
          instantDisappear = true
          hitEffect = Fx.flakExplosion
          despawnEffect = Fx.flakExplosion
        }

        fragBullets = 1
        fragAngle = 180f
        fragVelocityMin = 1f
        fragRandomSpread = 0f

        fragBullet = BasicBulletType().apply {
          damage = 135f
          lifetime = 120f
          speed = 0.1f
          drag = -0.041f
          width = 14f
          height = 25f
          pierce = true
          collides = false
          pierceBuilding = true
          bulletInterval = 1f

          intervalBullet = BulletType().apply {
            damage = 0f
            hitShake = 2f
            despawnShake = 1f
            splashDamage = 45f
            splashDamageRadius = 20f
            scaledSplashDamage = true
            instantDisappear = true
            hitEffect = Fx.flakExplosion
            despawnEffect = Fx.flakExplosion
          }

          despawnSound = Sounds.shootPulsar

          despawnEffect = ParticleEffect().apply {
            particles = 15
            line = true
            strokeFrom = 3f
            strokeTo = 0f
            lenFrom = 10f
            lenTo = 0f
            length = 50f
            lifetime = 10f
            colorFrom = "FFE176".toColor()
            colorTo = "FFFFFF".toColor()
            cone = 60f
          }
        }
      }
    }
    addAmmoType(IItems.钴锭) {
      BasicBulletType().apply {
        damage = 185f
        lifetime = 120f
        speed = 30f
        drag = 0.1f
        width = 14f
        height = 25f
        ammoMultiplier = 1f
        reloadMultiplier = 1.4f
        pierce = true
        collides = false
        pierceBuilding = true
        bulletInterval = 1f

        intervalBullet = BulletType().apply {
          damage = 0f
          hitShake = 2f
          despawnShake = 1f
          splashDamage = 62f
          splashDamageRadius = 20f
          scaledSplashDamage = true
          instantDisappear = true
          hitEffect = Fx.flakExplosion
          despawnEffect = Fx.flakExplosion
        }

        fragBullets = 1
        fragAngle = 180f
        fragVelocityMin = 1f
        fragRandomSpread = 0f

        fragBullet = BasicBulletType().apply {
          damage = 185f
          lifetime = 120f
          speed = 0.1f
          drag = -0.041f
          width = 14f
          height = 25f
          pierce = true
          collides = false
          pierceBuilding = true
          bulletInterval = 1f

          intervalBullet = BulletType().apply {
            damage = 0f
            hitShake = 2f
            despawnShake = 1f
            splashDamage = 62f
            splashDamageRadius = 20f
            scaledSplashDamage = true
            instantDisappear = true
            hitEffect = Fx.flakExplosion
            despawnEffect = Fx.flakExplosion
          }

          despawnSound = Sounds.loopPulse

          despawnEffect = ParticleEffect().apply {
            particles = 15
            line = true
            strokeFrom = 3f
            strokeTo = 0f
            lenFrom = 10f
            lenTo = 0f
            length = 50f
            lifetime = 10f
            colorFrom = "FFE176".toColor()
            colorTo = "FFFFFF".toColor()
            cone = 60f
          }
        }
      }
    }
    addAmmoType(IItems.钍锭) {
      BasicBulletType().apply {
        damage = 235f
        lifetime = 120f
        speed = 30f
        drag = 0.085f
        width = 16f
        height = 25f
        pierce = true
        collides = false
        pierceBuilding = true
        rangeChange = 56f
        bulletInterval = 1f

        intervalBullet = BulletType().apply {
          damage = 0f
          hitShake = 2f
          despawnShake = 1f
          status = IStatus.衰变
          splashDamage = 78f
          splashDamageRadius = 20f
          scaledSplashDamage = true
          instantDisappear = true
          hitEffect = Fx.flakExplosion
          despawnEffect = Fx.flakExplosion
        }

        fragBullets = 1
        fragAngle = 180f
        fragVelocityMin = 1f
        fragRandomSpread = 0f

        fragBullet = BasicBulletType().apply {
          damage = 235f
          lifetime = 120f
          speed = 0.1f
          drag = -0.043f
          width = 16f
          height = 25f
          pierce = true
          collides = false
          pierceBuilding = true
          bulletInterval = 1f

          intervalBullet = BulletType().apply {
            damage = 0f
            hitShake = 2f
            despawnShake = 1f
            status = IStatus.衰变
            splashDamage = 78f
            splashDamageRadius = 20f
            scaledSplashDamage = true
            instantDisappear = true
            hitEffect = Fx.flakExplosion
            despawnEffect = Fx.flakExplosion
          }

          despawnSound = Sounds.shootPulsar

          despawnEffect = ParticleEffect().apply {
            particles = 15
            line = true
            strokeFrom = 4f
            strokeTo = 0f
            lenFrom = 10f
            lenTo = 0f
            length = 70f
            baseLength = 0f
            lifetime = 10f
            colorFrom = "FFE176".toColor()
            colorTo = "FFFFFF".toColor()
            cone = 60f
          }
        }
      }
    }
    addAmmoType(IItems.金锭) {
      BasicBulletType().apply {
        damage = 480f
        lifetime = 120f
        speed = 30f
        drag = 0.067f
        width = 10f
        height = 25f
        pierce = true
        collides = false
        pierceBuilding = true
        rangeChange = 144f
        ammoMultiplier = 1f
        bulletInterval = 1f

        intervalBullet = BulletType().apply {
          damage = 0f
          status = StatusEffects.shocked
          splashDamage = 160f
          splashDamageRadius = 20f
          scaledSplashDamage = true
          lightning = 3
          lightningLength = 4
          lightningDamage = 16f
          instantDisappear = true
          hitEffect = Fx.flakExplosion
          despawnEffect = Fx.flakExplosion
        }

        fragBullets = 1
        fragAngle = 180f
        fragVelocityMin = 1f
        fragRandomSpread = 0f

        fragBullet = BasicBulletType().apply {
          damage = 480f
          lifetime = 120f
          speed = 0.1f
          drag = -0.045f
          width = 10f
          height = 25f
          pierce = true
          collides = false
          pierceBuilding = true
          bulletInterval = 1f

          intervalBullet = BulletType().apply {
            damage = 0f
            status = StatusEffects.shocked
            splashDamage = 160f
            splashDamageRadius = 20f
            scaledSplashDamage = true
            lightning = 3
            lightningLength = 4
            lightningDamage = 16f
            instantDisappear = true
            hitEffect = Fx.flakExplosion
            despawnEffect = Fx.flakExplosion
          }

          despawnSound = Sounds.shootPulsar

          despawnEffect = ParticleEffect().apply {
            particles = 15
            line = true
            strokeFrom = 5f
            strokeTo = 0f
            lenFrom = 16f
            lenTo = 0f
            length = 100f
            lifetime = 10f
            colorFrom = "F3E979".toColor()
            colorTo = "FFFFFF".toColor()
            cone = 60f
          }
        }
      }
    }
    addAmmoType(IItems.铱板) {
      BasicBulletType().apply {
        damage = 420f
        lifetime = 120f
        speed = 30f
        drag = 0.075f
        width = 10f
        height = 25f
        pierce = true
        collides = false
        pierceBuilding = true
        rangeChange = 96f
        ammoMultiplier = 3f
        bulletInterval = 1f

        intervalBullet = BulletType().apply {
          damage = 0f
          hitShake = 2f
          despawnShake = 1f
          status = IStatus.衰变
          statusDuration = 60f
          splashDamage = 140f
          splashDamageRadius = 20f
          scaledSplashDamage = true
          instantDisappear = true
          hitEffect = Fx.flakExplosion
          despawnEffect = Fx.flakExplosion
        }

        fragBullets = 1
        fragAngle = 180f
        fragVelocityMin = 1f
        fragRandomSpread = 0f

        fragBullet = BasicBulletType().apply {
          damage = 420f
          lifetime = 120f
          speed = 0.1f
          drag = -0.044f
          width = 10f
          height = 25f
          pierce = true
          collides = false
          pierceBuilding = true
          bulletInterval = 1f

          intervalBullet = BulletType().apply {
            damage = 0f
            hitShake = 2f
            despawnShake = 1f
            status = IStatus.衰变
            statusDuration = 60f
            splashDamage = 140f
            splashDamageRadius = 20f
            scaledSplashDamage = true
            instantDisappear = true
            hitEffect = Fx.flakExplosion
            despawnEffect = Fx.flakExplosion
          }

          despawnSound = Sounds.shootPulsar

          despawnEffect = ParticleEffect().apply {
            particles = 15
            line = true
            strokeFrom = 5f
            strokeTo = 0f
            lenFrom = 16f
            lenTo = 0f
            length = 100f
            lifetime = 10f
            colorFrom = "FFE176".toColor()
            colorTo = "FFFFFF".toColor()
            cone = 60f
          }
        }
      }
    }
  }
  val 腥风 = PowerTurret("bloodyWind").apply {
    squareSprite=false
    health = 6400
    size = 6
    armor = 8f
    range = 672f
    reload = 1.5f
    shake = 3f
    recoil = 4f
    recoils = 4
    shootY = 20f
    recoilTime = 6f
    inaccuracy = 3f
    shootCone = 60f
    rotateSpeed = 4f
    targetInterval = 1f
    cooldownTime = 20f
    shootSound = ISounds.速射
    liquidCapacity = 30f
    coolantMultiplier = 0.75f
    consumePower(53f)
    consume(ConsumeCoolant(3f))
    shoot = ShootBarrel().apply {
      barrels = floatArrayOf(-5.5f, 0f, 0f, 5.5f, 0f, 0f, -16f, 0f, 0f, 16f, 0f, 0f)
    }
    requirements(Category.turret, IItems.铬锭, 2200, IItems.石英玻璃, 570, IItems.铱板, 1200, IItems.导能回路, 625, IItems.钴钢, 825)

    drawer = DrawTurret().apply {
      parts.addAll(RegionPart("-l").apply {
        under = true
        recoilIndex = 0
        heatColor = "F03B0E".toColor()
        heatProgress = DrawPart.PartProgress.recoil
        moves.add(DrawPart.PartMove().apply {
          progress = DrawPart.PartProgress.recoil
          y = -4f
        })
      }, RegionPart("-r").apply {
        under = true
        recoilIndex = 1
        heatColor = "F03B0E".toColor()
        heatProgress = DrawPart.PartProgress.recoil
        moves.add(DrawPart.PartMove().apply {
          progress = DrawPart.PartProgress.recoil
          y = -4f
        })
      }, RegionPart("-ll").apply {
        under = true
        recoilIndex = 2
        heatColor = "F03B0E".toColor()
        heatProgress = DrawPart.PartProgress.recoil
        moves.add(DrawPart.PartMove().apply {
          progress = DrawPart.PartProgress.recoil
          y = -4f
        })
      }, RegionPart("-rr").apply {
        under = true
        recoilIndex = 3
        heatColor = "F03B0E".toColor()
        heatProgress = DrawPart.PartProgress.recoil
        moves.add(DrawPart.PartMove().apply {
          progress = DrawPart.PartProgress.recoil
          y = -4f
        })
      }, HoverPart().apply {
        color = "FF5845".toColor()
        phase = 120f
        circles = 3
        stroke = 1f
        layer = 100f
      })
    }
    bundle {
      desc(zh_CN, "腥风", "改进型四联速射粒子炮,向敌人发射高热的粒子束\n为了更强的电热转换回路拆除了部分气冷系统,使用液体时冷却效果更佳")
    }
    shootType = BasicBulletType().apply {
      damage = 121f
      lifetime = 33.6f
      speed = 20f
      width = 5f
      height = 75f
      pierce = true
      knockback = 1f
      status = IStatus.熔融
      statusDuration = 120f
      ammoMultiplier = 1f
      trailLength = 2
      trailWidth = 1.2f
      trailColor = "FF5845".toColor()
      frontColor = "FF8663".toColor()
      backColor = "FF5845".toColor()
      hitColor = "FF8663".toColor()

      shootEffect = WaveEffect().apply {
        lifetime = 6f
        sizeFrom = 1f
        sizeTo = 10f
        strokeFrom = 1.3f
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      }

      smokeEffect = Fx.shootBigSmoke

      hitEffect = MultiEffect(WaveEffect().apply {
        lifetime = 10f
        sizeTo = 6f
        strokeFrom = 4f
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      }, ParticleEffect().apply {
        line = true
        particles = 8
        lifetime = 10f
        length = 50f
        strokeFrom = 3f
        lenFrom = 10f
        lenTo = 4f
        cone = 60f
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      })

      despawnEffect = WaveEffect().apply {
        lifetime = 10f
        sizeTo = 6f
        strokeFrom = 4f
        colorFrom = "FF5845".toColor()
        colorTo = "FF8663".toColor()
      }
    }
  }

  var 闪光 = SglTurret("flash").apply {
    bundle {
      desc(zh_CN, "闪光", "发射带电巨浪子弹贯穿敌人,释放的大量闪电能够对集群造成十分可观的打击")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 35, Items.surgeAlloy, 40, Items.plastanium, 45
      )
    )
    size = 2

    itemCapacity = 20
    liquidCapacity = 30f
    range = 240f

    shootSound = Sounds.shootSmite
    //copy from smite
    newAmmo(object : BasicBulletType(6f, 72f) {
      init {
        sprite = "large-orb"
        width = 17f
        height = 21f
        hitSize = 8f

        recoilTime = 120f

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
        pierceCap = 3
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

        intervalBullet = object : LightningBulletType() {
          init {
            damage = 18f
            collidesAir = false
            ammoMultiplier = 1f
            lightningColor = Pal.accent
            lightningLength = 5
            lightningLengthRand = 10
            buildingDamageMultiplier = 0.25f
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

        bulletInterval = 3f
      }
    }).setReloadAmount(3)
    consume?.apply {
      item(Items.surgeAlloy, 1)
      power(2.4f)
      time(45f)
    }
    newCoolant(1f, 0.4f, { l: Liquid? -> l!!.heatCapacity >= 0.4f && l.temperature <= 0.5f }, 0.25f, 20f)
  }
  var 伦琴 = ProjectileTurret("roentgen").apply {
    bundle {
      desc(zh_CN, "伦琴", "发射极具穿透力的高能激光束,杀伤力极强")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 120, IItems.气凝胶, 100, IItems.铝, 60, IItems.FEX水晶, 40, Items.silicon, 75, Items.surgeAlloy, 45
      )
    )
    size = 4
    range = 240f
    shootY = 12f
    cooldownTime = 60f

    moveWhileCharging = false
    shoot.firstShotDelay = 40f
    shootSound = Sounds.shootLaser

    newAmmo(object : LightLaserBulletType() {
      init {
        length = 240f
        damage = 225f
        empDamage = 180f
        lightColor = Pal.reactorPurple
        chargeEffect = MultiEffect(SglFx.colorLaserChargeBegin, SglFx.colorLaserCharge, Fx.lightningCharge)
        status = StatusEffects.electrified
        statusDuration = 12f
        hitColor = Pal.reactorPurple
        shootEffect = MultiEffect(
          SglFx.crossLightMini, Fx.circleColorSpark
        )

        colors = arrayOf<Color?>(
          Pal.reactorPurple.cpy().mul(1f, 1f, 1f, 0.4f), Pal.reactorPurple, Color.white
        )

        generator.maxSpread = 6f
      }
    })
    consume!!.time(30f)
    consume!!.power(12.4f)

    newAmmoCoating(Core.bundle.get("coating.crystal_fex"), SglDrawConst.fexCrystal, { b: mindustry.entities.bullet.BulletType? ->
      val res = b!!.copy() as LightLaserBulletType
      res.damage *= 1.25f
      res.colors = arrayOf<Color?>(
        SglDrawConst.fexCrystal.cpy().mul(1f, 1f, 1f, 0.4f), SglDrawConst.fexCrystal, Color.white
      )
      res.lightColor = SglDrawConst.fexCrystal
      res.empDamage *= 0.8f
      res.status = IStatus.结晶化
      res.statusDuration = 15f
      res
    }, { t ->
      t!!.add(SglStat.exDamageMultiplier.localized() + 125 + "%")
      t.row()
      t.add(Core.bundle.get("bullet.empDamageMulti") + 80 + "%")
      t.row()
      t.add(IStatus.结晶化.localizedName + "[lightgray] ~ [stat]0.25[lightgray] " + Core.bundle.get("unit.seconds"))
    })
    consume!!.time(60f)
    consume!!.liquid(ILiquids.FEX流体, 0.1f)

    newCoolant(1.5f, 20f)
    consume!!.liquid(ILiquids.相位态FEX流体, 0.1f)

    draw = object : DrawSglTurret() {}
  }
  var 遮幕 = SglTurret("curtain").apply {
    bundle {
      desc(zh_CN, "遮幕", "发射石墨炸弹,会制造一篇石墨云,一种朴素但有效的对空防御武器")
    }
    requirements(
      Category.turret, ItemStack.with(
        Items.titanium, 20, Items.graphite, 20, Items.lead, 12
      )
    )
    itemCapacity = 20
    range = 144f
    targetGround = false

    newAmmo(object : BasicBulletType(1.6f, 30f, "missile") {
      init {
        frontColor = Items.graphite.color.cpy().lerp(Color.white, 0.7f)
        backColor = Items.graphite.color
        width = 7f
        height = 12f
        lifetime = 90f
        ammoMultiplier = 1f
        hitShake = 0.35f
        scaleLife = true
        splashDamageRadius = 32f
        splashDamage = 12f
        collidesGround = false
        collidesTiles = false
        hitEffect = Fx.explosion
        trailEffect = Fx.smoke
        trailChance = 0.12f
        trailColor = Items.graphite.color

        hitSound = Sounds.explosion

        fragOnHit = true
        fragBullets = 1
        fragVelocityMin = 0f
        fragVelocityMax = 0f
        fragBullet = graphiteCloud(360f, 36f, true, ground = false, empDamage = 0.2f)
      }
    }, true) { bt, ammo: mindustry.entities.bullet.BulletType? ->
      bt!!.add(Core.bundle.format("bullet.damage", ammo!!.damage))
      bt.row()
      bt.add(Core.bundle.format("bullet.splashdamage", ammo.splashDamage.toInt(), Strings.fixed(ammo.splashDamageRadius / Vars.tilesize, 1)))
      bt.row()
      bt.add(Core.bundle.get("infos.curtainAmmo"))
      bt.row()
      bt.add(IStatus.电子干扰.emoji() + "[stat]" + IStatus.电子干扰.localizedName + "[lightgray] ~ [stat]2[lightgray] " + Core.bundle.get("unit.seconds"))
    }
    consume!!.item(Items.graphite, 1)
    consume!!.time(90f)
  }
  var 迷雾 = SglTurret("mist").apply {
    bundle {
      desc(zh_CN, "迷雾", "一门重型对地复合石墨大炮,发射4颗填充了松散石墨的炮弹,爆炸后会产生一片会带有电磁脉冲的石墨云")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 100, IItems.气凝胶, 120, Items.titanium, 100, Items.graphite, 80, Items.lead, 85
      )
    )
    size = 3

    itemCapacity = 36
    range = 300f
    minRange = 40f
    inaccuracy = 8f
    targetAir = false
    velocityRnd = 0.2f
    shake = 2.5f
    recoil = 6f
    recoilTime = 120f
    cooldownTime = 120f

    shootY = 0f
    shootEffect = MultiEffect(
      SglFx.crossLightMini, object : WaveEffect() {
        init {
          colorFrom = SglDrawConst.frost
          colorTo = Color.lightGray
          lifetime = 12f
          sizeTo = 40f
          strokeFrom = 6f
          strokeTo = 0.3f
        }
      })

    scaledHealth = 180f

    shootSound = Sounds.shootPulsar

    shoot.shots = 4
    newAmmo(object : EmpArtilleryBulletType(3f, 20f) {
      init {
        empDamage = 40f
        empRange = 20f

        knockback = 1f
        lifetime = 80f
        height = 12f
        width = height
        collidesTiles = false
        splashDamageRadius = 20f
        splashDamage = 35f

        damage = 0f

        frontColor = Items.graphite.color.cpy().lerp(Color.white, 0.7f)
        backColor = Items.graphite.color

        fragOnHit = true
        fragBullets = 1
        fragVelocityMin = 0f
        fragVelocityMax = 0f
        fragBullet = graphiteCloud(360f, 40f, air = true, ground = true, empDamage = 0.35f)
      }
    }) { t, b: mindustry.entities.bullet.BulletType? ->
      t!!.add(Core.bundle.get("infos.graphiteEmpAmmo"))
      t.row()
      t.table { table ->
        table!!.add(Core.bundle.format("bullet.empDamage", Strings.autoFixed(0.35f * 60, 1) + "/" + StatUnit.seconds.localized(), ""))
        table.row()
        table.add(IStatus.电子干扰.emoji() + "[stat]" + IStatus.电子干扰.localizedName + "[lightgray] ~ [stat]6[lightgray] " + Core.bundle.get("unit.seconds"))
      }.padLeft(15f)
    }
    consume!!.item(Items.graphite, 6)
    consume!!.time(120f)
  }
  var 阴霾 = SglTurret("haze").apply {
    bundle {
      desc(zh_CN, "阴霾", "大型石墨导弹发射器,发射一枚电磁脉冲核弹,包裹的巨量石墨会产生一片巨大的石墨云传导电磁脉冲,造成严重的电子损伤")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 180, IItems.气凝胶, 180, IItems.矩阵合金, 120, IItems.铀238, 100, Items.surgeAlloy, 140, Items.graphite, 200
      )
    )
    size = 5

    accurateDelay = false
    accurateSpeed = false
    itemCapacity = 36
    range = 580f
    minRange = 100f
    shake = 7.5f
    recoil = 2f
    recoilTime = 150f
    cooldownTime = 150f

    shootSound = Sounds.shootMissile

    rotateSpeed = 1.25f

    shootY = 4f

    warmupSpeed = 0.015f
    fireWarmupThreshold = 0.94f
    linearWarmup = false

    scaledHealth = 200f
    val type: Func3<Float, Float, Float, EmpBulletType> = Func3 { dam: Float?, empD: Float?, r: Float? ->
      object : EmpBulletType() {
        init {
          lifetime = 180f
          splashDamage = dam!!
          splashDamageRadius = r!!

          damage = 0f
          empDamage = empD!!
          empRange = r

          hitSize = 5f

          hitShake = 16f
          despawnHit = true

          hitEffect = MultiEffect(
            Fx.shockwave, Fx.bigShockwave, SglFx.explodeImpWaveLarge, SglFx.spreadLightning
          )

          homingPower = 0.02f
          homingRange = 240f

          shootEffect = Fx.shootBig
          smokeEffect = Fx.shootSmokeMissile
          trailColor = Pal.redLight
          trailEffect = SglFx.shootSmokeMissileSmall
          trailInterval = 1f
          trailRotation = true
          hitColor = Items.graphite.color

          trailWidth = 3f
          trailLength = 28

          hitSound = Sounds.explosion
          hitSoundVolume = 1.2f

          speed = 0.1f

          fragOnHit = true
          fragBullets = 1
          fragVelocityMin = 0f
          fragVelocityMax = 0f
          fragBullet = object : BulletType(0f, 0f) {
            init {
              lifetime = 450f
              collides = false
              pierce = true
              hittable = false
              absorbable = false
              hitEffect = Fx.none
              shootEffect = Fx.none
              despawnEffect = Fx.none
              smokeEffect = Fx.none
              drawSize = r * 1.2f
            }

            val branch: RandomGenerator = RandomGenerator()
            val generator: RandomGenerator = RandomGenerator().apply {
              maxLength = 100f
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

            override fun init(b: Bullet) {
              super.init(b)
              val c = Pools.obtain(LightningContainer.PoolLightningContainer::class.java) { LightningContainer.PoolLightningContainer() }
              b.data = c
              c.maxWidth = 6f
              c.lerp = Interp.linear
              c.minWidth = 4f
              c.lifeTime = 60f
              c.time = 30f
            }

            override fun update(b: Bullet) {
              super.update(b)
              Units.nearbyEnemies(b.team, b.x, b.y, r, Cons { u: Unit? -> Sgl.empHealth.empDamage(u, 0.8f, false) })
              if (b.timer(0, 6f)) {
                Damage.status(b.team, b.x, b.y, r, IStatus.电子干扰, min(450 - b.time, 120f), true, true)
              }
              val data = b.data
              if (data is LightningContainer) {
                if (b.timer(2, 15 / Mathf.clamp((b.fout() - 0.15f) * 4))) {
                  generator.setOffset(Mathf.random(-45f, 45f), Mathf.random(-45f, 45f))
                  generator.originAngle = Mathf.random(0f, 360f)
                  data.create(generator)
                }
                data.update()
              }
            }

            override fun draw(e: Bullet) {
              Draw.z(Layer.bullet - 5)
              Draw.color(Pal.stoneGray)
              Draw.alpha(0.6f)
              rand.setSeed(e.id.toLong())
              Angles.randLenVectors(e.id.toLong(), 8 + 70, r * 1.2f) { x: Float, y: Float ->
                val size = rand.random(14, 20).toFloat()
                val i = e.fin(Interp.pow3Out)
                Fill.circle(e.x + x * i, e.y + y * i, size * e.fout(Interp.pow5Out))
              }

              Draw.color(Items.graphite.color)
              Draw.z(Layer.effect)
              val data = e.data
              if (data is LightningContainer) {
                data.draw(e.x, e.y)
              }
            }

            override fun removed(b: Bullet) {
              val data = b.data
              if (data is LightningContainer) {
                Pools.free(data)
              }
              super.removed(b)
            }
          }
        }

        var regionOutline: TextureRegion? = null

        override fun init(b: Bullet) {
          super.init(b)
          b.data = SoundLoop(Sounds.loopMissileTrail, 0.65f)
        }

        override fun update(b: Bullet) {
          super.update(b)
          Tmp.v1.set(b.vel).setLength(28f)
          b.vel.approachDelta(Tmp.v1, 0.06f * Mathf.clamp((b.fin() - 0.10f) * 5f))
          val data = b.data
          if (data is SoundLoop) {
            data.update(b.x, b.y, true)
          }
        }

        override fun removed(b: Bullet) {
          super.removed(b)
          val data = b.data
          if (data is SoundLoop) {
            data.stop()
          }
        }

        override fun draw(b: Bullet) {
          drawTrail(b)
          Draw.z(Layer.effect + 1)
          Draw.rect(regionOutline, b.x, b.y, b.rotation() - 90)

          SglDraw.drawTransform(b.x, b.y, 0f, 4 * b.fin(), b.rotation() - 90) { x: Float, y: Float, r: Float ->
            Draw.rect(regionOutline, x, y, 4f, 10.5f, r)
          }
          SglDraw.drawTransform(b.x, b.y, 0f, -4f, b.rotation() - 90) { x: Float, y: Float, r: Float ->
            Draw.color(hitColor, 0.75f)
            Fill.circle(x, y, 2.5f)
            Draw.color(Color.white)
            Fill.circle(x, y, 1.5f)
          }
        }

        override fun load() {
          super.load()
          val r = Singularity.getModAtlas("haze_missile")
          /////val p = Core.atlas.getPixmap(r)
          regionOutline = Singularity.getModAtlas("haze_missile") //TextureRegion(Texture(Pixmaps.outline(p, Pal.darkOutline, 3)))
        }
      }
    }

    newAmmo(type.get(480f, 500f, 120f)) { t, b: mindustry.entities.bullet.BulletType? ->
      t!!.add(Core.bundle.get("infos.graphiteEmpAmmo"))
      t.row()
      t.table { table ->
        table!!.add(Core.bundle.format("bullet.empDamage", Strings.autoFixed(0.8f * 60, 1) + "/" + StatUnit.seconds.localized(), ""))
        table.row()
        table.add(IStatus.电子干扰.emoji() + "[stat]" + IStatus.电子干扰.localizedName + "[lightgray] ~ [stat]7.5[lightgray] " + Core.bundle.get("unit.seconds"))
      }.padLeft(15f)
    }
    consume!!.items(
      *ItemStack.with(
        Items.graphite, 12, IItems.浓缩铀235核燃料, 1
      )
    )
    consume!!.time(480f)

    newAmmo(type.get(600f, 550f, 145f)) { t, _ ->
      t!!.add(Core.bundle.get("infos.graphiteEmpAmmo"))
      t.row()
      t.table { table ->
        table!!.add(Core.bundle.format("bullet.empDamage", Strings.autoFixed(0.5f * 60, 1) + "/" + StatUnit.seconds.localized(), ""))
        table.row()
        table.add(IStatus.电子干扰.emoji() + "[stat]" + IStatus.电子干扰.localizedName + "[lightgray] ~ [stat]7.5[lightgray] " + Core.bundle.get("unit.seconds"))
      }.padLeft(15f)
    }
    consume!!.items(
      *ItemStack.with(
        Items.graphite, 12, IItems.浓缩钚239核燃料, 1
      )
    )
    consume!!.time(510f)

    draw = DrawSglTurret(object : RegionPart("_missile") {
      init {
        progress = PartProgress.warmup.mul(PartProgress.reload.inv())
        x = 0f
        y = -4f
        moveY = 8f
      }
    }, object : RegionPart("_side") {
      init {
        progress = PartProgress.warmup
        mirror = true
        moveX = 4f
        moveY = 2f
        moveRot = -35f

        under = true
        layerOffset = -0.3f
        turretHeatLayer = Layer.turret - 0.2f

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, -10f))
      }
    }, object : RegionPart("_spine") {
      init {
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup
        mirror = true
        outline = false

        heatColor = Items.graphite.color
        heatLayerOffset = 0f

        xScl = 1.5f
        yScl = 1.5f

        x = 3.3f
        y = 7.3f
        moveX = 10f
        moveY = 5f
        moveRot = -30f

        under = true
        layerOffset = -0.3f
        turretHeatLayer = Layer.turret - 0.2f

        moves.add(PartMove(PartProgress.recoil.delay(0.8f), -1.33f, 0f, 16f))
      }
    }, object : RegionPart("_spine") {
      init {
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup
        mirror = true
        outline = false

        heatColor = Items.graphite.color
        heatLayerOffset = 0f

        xScl = 1.5f
        yScl = 1.5f

        x = 3.3f
        y = 7.3f
        moveX = 12.3f
        moveY = -2.6f
        moveRot = -45f

        under = true
        layerOffset = -0.3f
        turretHeatLayer = Layer.turret - 0.2f

        moves.add(PartMove(PartProgress.recoil.delay(0.4f), -1.33f, 0f, 24f))
      }
    }, object : RegionPart("_spine") {
      init {
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup
        mirror = true
        outline = false

        heatColor = Items.graphite.color
        heatLayerOffset = 0f

        xScl = 1.5f
        yScl = 1.5f

        x = 3.3f
        y = 7.3f
        moveX = 13f
        moveY = -9.2f
        moveRot = -60f

        under = true
        layerOffset = -0.3f
        turretHeatLayer = Layer.turret - 0.2f

        moves.add(PartMove(PartProgress.recoil, -1.33f, 0f, 30f))
      }
    }, object : RegionPart("_blade") {
      init {
        progress = PartProgress.warmup
        mirror = true
        moveX = 2.5f

        heatProgress = PartProgress.warmup
        heatColor = Items.graphite.color

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, 0f))
      }
    }, object : RegionPart("_body") {
      init {
        mirror = false
        heatProgress = PartProgress.warmup
        heatColor = Items.graphite.color
      }
    })
  }
  var 惊蛰 = SglTurret("thunder").apply {
    bundle {
      desc(zh_CN, "惊蛰", "大功率电离轰击武器,它会用耀眼的闪电将敌人化为灰烬", "这座庞然大物凭借其如同雷鸣般的声响和能够与雷电平齐的杀伤力")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 180, IItems.气凝胶, 150, Items.surgeAlloy, 120, IItems.矩阵合金, 100, IItems.FEX水晶, 100, IItems.充能FEX水晶, 80, IItems.铱锭, 80
      )
    )
    size = 5
    scaledHealth = 320f
    range = 400f
    val shootRan: Float = range
    warmupSpeed = 0.016f
    linearWarmup = false
    fireWarmupThreshold = 0.8f
    rotateSpeed = 1.6f
    cooldownTime = 90f
    recoil = 3.4f

    energyCapacity = 4096f
    basicPotentialEnergy = 2048f

    shootY = 22f

    shake = 4f
    shootSound = Sounds.shootCollaris

    newAmmo(object : BulletType() {
      init {
        speed = 0f
        lifetime = 60f
        collides = false
        hittable = false
        absorbable = false
        splashDamage = 1460f
        splashDamageRadius = 46f
        damage = 0f
        drawSize = shootRan

        hitColor = Pal.reactorPurple
        shootEffect = MultiEffect(SglFx.impactBubble, SglFx.shootRecoilWave, object : WaveEffect() {
          init {
            colorTo = Pal.reactorPurple
            colorFrom = colorTo
            lifetime = 12f
            sizeTo = 40f
            strokeFrom = 6f
            strokeTo = 0.3f
          }
        })

        hitEffect = Fx.none
        despawnEffect = Fx.none
        smokeEffect = Fx.none
        val g: RandomGenerator = RandomGenerator().apply {
          maxLength = 100f
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

        fragBullet = lightning(82f, 25f, 42f, 4.8f, Pal.reactorPurple) { b: Bullet? ->
          val u = Units.closest(b!!.team, b.x, b.y, 80f) { e -> true }
          g.originAngle = if (u == null) b.rotation() else b.angleTo(u)
          g
        }
        fragSpread = 25f
        fragOnHit = false
      }

      val generator: VectorLightningGenerator = VectorLightningGenerator().apply {
        maxSpread = 14f
        minInterval = 8f
        maxInterval = 20f

        branchChance = 0.1f
        minBranchStrength = 0.5f
        maxBranchStrength = 0.8f
        branchMaker = Func2 { vert, strength ->
          branch.maxLength = (60 * strength)
          branch.originAngle = vert.angle + Mathf.random(-90, 90)
          branch
        }
      }

      override fun init(b: Bullet) {
        super.init(b)
        val container = Pools.obtain(LightningContainer.PoolLightningContainer::class.java) { LightningContainer.PoolLightningContainer() }
        container.lifeTime = lifetime
        container.minWidth = 5f
        container.maxWidth = 8f
        container.time = 6f
        container.lerp = Interp.linear
        b.data = container

        Tmp.v1.set(b.aimX - b.originX, b.aimY - b.originY)
        val scl = Mathf.clamp(Tmp.v1.len() / shootRan)
        Tmp.v1.setLength(shootRan).scl(scl)
        val shX: Float
        val shY: Float
        val absorber = Damage.findAbsorber(b.team, b.originX, b.originY, b.originX + Tmp.v1.x, b.originY + Tmp.v1.y)
        if (absorber != null) {
          shX = absorber.x
          shY = absorber.y
        } else {
          shX = b.x + Tmp.v1.x
          shY = b.y + Tmp.v1.y
        }

        generator.vector.set(
          shX - b.originX, shY - b.originY
        )
        val amount = Mathf.random(5, 7)
        for (i in 0..<amount) {
          container.create(generator)
        }

        Time.run(6f) {
          SglFx.lightningBoltWave.at(shX, shY, Pal.reactorPurple)
          createFrags(b, shX, shY)
          Effect.shake(6f, 6f, shX, shY)
          Sounds.explosion.at(shX, shY, hitSoundPitch, hitSoundVolume)
          Damage.damage(b.team, shX, shY, splashDamageRadius, splashDamage)
        }
      }

      override fun update(b: Bullet) {
        super.update(b)
        (b.data as LightningContainer).update()
      }

      override fun draw(b: Bullet) {
        val container: LightningContainer = b.data as LightningContainer
        Draw.z(Layer.bullet)
        Draw.color(Pal.reactorPurple)
        container.draw(b.x, b.y)
      }

      override fun createSplashDamage(b: Bullet, x: Float, y: Float) {}

      override fun despawned(b: Bullet?) {}

      override fun removed(b: Bullet) {
        super.removed(b)
        val data = b.data
        if (data is LightningContainer.PoolLightningContainer) {
          Pools.free(data)
        }
      }
    })
    consume!!.item(IItems.充能FEX水晶, 2)
    consume!!.energy(2.2f)
    consume!!.time(180f)
    val generator = CircleGenerator().apply {
      radius = 8f
      maxSpread = 2.5f
      minInterval = 2f
      maxInterval = 2.5f
    }

    initialed = Cons { e: SglBuilding ->
      e.CONTAINER = object : LightningContainer() {
        init {
          lifeTime = 45f
          maxWidth = 2f
          lerp = Interp.linear
          time = 0f
        }
      }
    }
    val timeId = timers++
    updating = Cons { e: SglBuilding ->
      e.CONTAINER?.update()
      val turret = e as SglTurretBuild
      if (turret.warmup > 0 && e.timer(timeId, 25 / turret.warmup)) {
        e.CONTAINER?.create(generator)
      }
      if (Mathf.chanceDelta((0.03f * turret.warmup).toDouble())) {
        Tmp.v1.set(0f, -16f).rotate(turret.drawrot())
        SglFx.randomLightning.at(e.x + Tmp.v1.x, e.y + Tmp.v1.y, Pal.reactorPurple)
      }
    }

    newCoolant(1.45f, 20f)
    consume!!.liquid(ILiquids.相位态FEX流体, 0.25f)

    draw = DrawMulti(DrawSglTurret(object : RegionPart("_center") {
      init {
        moveY = 8f
        progress = PartProgress.warmup
        heatColor = Pal.reactorPurple
        heatProgress = PartProgress.warmup.delay(0.25f)

        moves.add(PartMove(PartProgress.recoil, 0f, -4f, 0f))
      }
    }, object : RegionPart("_body") {
      init {
        heatColor = Pal.reactorPurple
        heatProgress = PartProgress.warmup.delay(0.25f)
      }
    }, object : RegionPart("_side") {
      init {
        mirror = true
        moveX = 5f
        moveY = -5f
        progress = PartProgress.warmup
        heatColor = Pal.reactorPurple
        heatProgress = PartProgress.warmup.delay(0.25f)
      }
    }, object : ShapePart() {
      init {
        color = Pal.reactorPurple
        circle = true
        hollow = true
        stroke = 0f
        strokeTo = 2f
        y = -16f
        radius = 0f
        radiusTo = 10f
        progress = PartProgress.warmup
        layer = Layer.effect
      }
    }, object : ShapePart() {
      init {
        circle = true
        y = -16f
        radius = 0f
        radiusTo = 3.5f
        color = Pal.reactorPurple
        layer = Layer.effect
        progress = PartProgress.warmup
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.reactorPurple
        layer = Layer.effect
        y = -16f
        haloRotation = 90f
        shapes = 2
        triLength = 0f
        triLengthTo = 30f
        haloRadius = 10f
        tri = true
        radius = 4f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.reactorPurple
        layer = Layer.effect
        y = -16f
        haloRotation = 90f
        shapes = 2
        triLength = 0f
        triLengthTo = 6f
        haloRadius = 10f
        tri = true
        radius = 4f
        shapeRotation = 180f
      }
    }, object : ShapePart() {
      init {
        circle = true
        y = 22f
        radius = 0f
        radiusTo = 5f
        color = Pal.reactorPurple
        layer = Layer.effect
        progress = PartProgress.warmup
      }
    }, object : ShapePart() {
      init {
        color = Pal.reactorPurple
        circle = true
        hollow = true
        stroke = 0f
        strokeTo = 1.5f
        y = 22f
        radius = 0f
        radiusTo = 8f
        progress = PartProgress.warmup
        layer = Layer.effect
      }
    }), object : DrawBlock() {
      override fun draw(build: Building) {
        DrawBlock.rand.setSeed(build.id.toLong())
        val turret = build as SglTurretBuild
        Draw.z(Layer.effect)
        Draw.color(Pal.reactorPurple)
        Tmp.v1.set(1f, 0f).setAngle(turret.rotationu)
        val sclX = Tmp.v1.x
        val sclY = Tmp.v1.y
        turret.CONTAINER?.draw(turret.x + sclX * 22, turret.y + sclY * 22)
        val step = 45 / 16f
        if (turret.warmup < 0.001f) return
        for (i in 0..15) {
          val x = turret.x + (step * i) * sclX * turret.warmup + 14 * sclX
          val y = turret.y + (step * i) * sclY * turret.warmup + 14 * sclY
          SglDraw.drawRectAsCylindrical(
            x, y, DrawBlock.rand.random(2, 18) * turret.warmup, DrawBlock.rand.random(1.5f, 10f), (10 + i * 0.75f + DrawBlock.rand.random(8)) * turret.warmup, (Time.time * DrawBlock.rand.random(0.8f, 2f) + DrawBlock.rand.random(360)) * (if (DrawBlock.rand.random(1f) < 0.5) -1 else 1), turret.drawrot(), Pal.reactorPurple, Pal.reactorPurple2, Layer.bullet - 0.5f, Layer.effect
          )
        }
      }
    })
  }
  var 白露 = ProjectileTurret("dew").apply {
    bundle {
      desc(zh_CN, "白露", "连续高速发射一连串穿甲弹,向敌人倾泻如同暴雨般的火力")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 150, IItems.铝, 110, IItems.气凝胶, 120, IItems.矩阵合金, 160, Items.thorium, 100, Items.silicon, 85, IItems.铀238, 85
      )
    )
    size = 5
    scaledHealth = 360f
    rotateSpeed = 2.5f
    range = 350f
    shootY = 17.4f
    warmupSpeed = 0.035f
    linearWarmup = false
    recoil = 0f
    fireWarmupThreshold = 0.75f
    shootCone = 15f
    shake = 2.2f

    shootSound = Sounds.shootCollaris

    shoot = object : ShootPattern() {
      override fun shoot(totalShots: Int, handler: BulletHandler) {
        val off = totalShots % 2 - 0.5f

        for (i in 0..2) {
          handler.shoot(off * 16, 0f, 0f, firstShotDelay + 3 * i)
        }
      }
    }

    newAmmo(object : BulletType() {
      init {
        damage = 80f
        speed = 8f
        lifetime = 45f
        hitSize = 4.3f
        hitColor = SglDrawConst.matrixNet
        hitEffect = Fx.colorSpark
        despawnEffect = Fx.circleColorSpark
        trailEffect = SglFx.polyParticle
        trailRotation = true
        trailChance = 0.04f
        trailColor = SglDrawConst.matrixNet
        shootEffect = MultiEffect(Fx.shootBig, Fx.colorSparkBig)
        hittable = true
        pierceBuilding = true
        pierceCap = 4
      }

      override fun update(b: Bullet) {
        super.update(b)
        b.damage = b.type.damage + b.type.damage * b.fin() * 0.3f
      }

      override fun draw(b: Bullet) {
        SglDraw.drawDiamond(b.x, b.y, 18f, 6f, b.rotation(), SglDrawConst.matrixNet)
        Draw.color(SglDrawConst.matrixNet)
        for (i in Mathf.signs) {
          Drawf.tri(b.x, b.y, 6f * b.fin(), 20f * b.fin(), b.rotation() + 156f * i)
        }
      }
    })
    consume!!.item(Items.thorium, 1)
    consume!!.time(10f)

    newAmmoCoating(Core.bundle.get("coating.depletedUranium"), Pal.accent, { b: mindustry.entities.bullet.BulletType ->
      object : WarpedBulletType<mindustry.entities.bullet.BulletType>(b) {
        init {
          damage = b.damage * 1.15f
          pierceArmor = true
          pierceCap = 5
        }

        override fun hitEntity(b: Bullet, entity: Hitboxc?, health: Float) {
          if (entity is Unit) {
            if (entity.shield > 0) {
              val damageShield = min(max(entity.shield, 0f), damage * 0.85f)
              entity.shield -= damageShield
              Fx.colorSparkBig.at(b.x, b.y, b.rotation(), Pal.bulletYellowBack)
            }
          }
          super.hitEntity(b, entity, health)
        }

        override fun draw(b: Bullet) {
          SglDraw.drawDiamond(b.x, b.y, 24f, 6f, b.rotation(), Pal.accent)
          Draw.color(SglDrawConst.matrixNet)
          for (i in Mathf.signs) {
            Drawf.tri(b.x, b.y, 6f * b.fin(), 30f * b.fin(), b.rotation() + 162f * i)
          }
        }
      }
    }, { t ->
      t!!.add(SglStat.exDamageMultiplier.localized() + 115 + "%")
      t.row()
      t.add(SglStat.exShieldDamage.localized() + 85 + "%")
      t.row()
      t.add(SglStat.exPierce.localized() + ": 1")
      t.row()
      t.add("@bullet.armorpierce")
    })
    consume!!.time(10f)
    consume!!.item(IItems.铀238, 1)

    newAmmoCoating(Core.bundle.get("coating.crystal_fex"), SglDrawConst.fexCrystal, { b: mindustry.entities.bullet.BulletType? ->
      object : WarpedBulletType<mindustry.entities.bullet.BulletType>(b) {
        init {
          damage = b!!.damage * 1.25f
          hitColor = SglDrawConst.fexCrystal
          trailEffect = SglFx.movingCrystalFrag
          trailInterval = 6f
          trailColor = SglDrawConst.fexCrystal

          status = IStatus.结晶化
          statusDuration = 15f
        }

        override fun draw(b: Bullet) {
          SglDraw.drawDiamond(b.x, b.y, 24f, 6f, b.rotation(), hitColor)
          Draw.color(SglDrawConst.matrixNet)
          for (i in Mathf.signs) {
            Drawf.tri(b.x, b.y, 6f * b.fin(), 30f * b.fin(), b.rotation() + 162f * i)
          }
        }
      }
    }, { t ->
      t!!.add(SglStat.exDamageMultiplier.localized() + 125 + "%")
      t.row()
      t.add(IStatus.结晶化.localizedName + "[lightgray] ~ [stat]0.25[lightgray] " + Core.bundle.get("unit.seconds"))
    }, 2)
    consume!!.time(20f)
    consume!!.item(IItems.FEX水晶, 1)

    draw = DrawSglTurret(object : RegionPart("_blade") {
      init {
        mirror = true
        moveX = 4f
        progress = PartProgress.warmup
        heatColor = SglDrawConst.dew
        heatProgress = PartProgress.heat

        moves.add(PartMove(PartProgress.recoil, 0f, -2.6f, 0f))
      }
    }, object : RegionPart("_side") {
      init {
        mirror = true
        moveX = 8f
        moveRot = -25f
        progress = PartProgress.warmup
        heatColor = SglDrawConst.dew
        heatProgress = PartProgress.warmup.delay(0.25f)

        moves.add(PartMove(PartProgress.recoil, 1f, -1f, -5f))
      }
    }, object : RegionPart("_body") {
      init {
        heatColor = SglDrawConst.dew
        heatProgress = PartProgress.warmup.delay(0.25f)
      }
    }, object : ShapePart() {
      init {
        layer = Layer.effect
        color = SglDrawConst.matrixNet
        x = 0f
        y = -16f
        circle = true
        hollow = true
        stroke = 0f
        strokeTo = 1.8f
        radius = 0f
        radiusTo = 8f
        progress = PartProgress.warmup
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        y = -16f
        shapes = 1
        triLength = 16f
        triLengthTo = 46f
        haloRadius = 0f
        tri = true
        radius = 0f
        radiusTo = 4f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        y = -16f
        shapes = 1
        triLength = 8f
        triLengthTo = 20f
        haloRotation = 180f
        haloRadius = 0f
        tri = true
        radius = 0f
        radiusTo = 4f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        y = -16f
        shapes = 2
        haloRotation = 90f
        triLength = 6f
        triLengthTo = 24f
        haloRadius = 0f
        tri = true
        radius = 0f
        radiusTo = 2.5f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.recoil.delay(0.3f)
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        mirror = true
        x = 2f
        y = -6f
        haloRotation = -135f
        shapes = 1
        triLength = 14f
        triLengthTo = 21f
        haloRadius = 10f
        tri = true
        radius = 0f
        radiusTo = 6f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.recoil.delay(0.3f)
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        mirror = true
        x = 2f
        y = -6f
        haloRotation = -135f
        shapes = 1
        triLength = 0f
        triLengthTo = 6f
        haloRadius = 10f
        tri = true
        radius = 0f
        radiusTo = 6f
        shapeRotation = 180f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.recoil.delay(0.3f)
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        mirror = true
        x = 22f
        y = -6f
        haloRotation = -135f
        shapes = 1
        triLength = 8f
        triLengthTo = 16f
        haloRadius = 0f
        tri = true
        radius = 0f
        radiusTo = 4.5f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.recoil.delay(0.3f)
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        mirror = true
        x = 22f
        y = -6f
        haloRotation = -135f
        shapes = 1
        triLength = 0f
        triLengthTo = 4f
        haloRadius = 0f
        tri = true
        radius = 0f
        radiusTo = 4.5f
        shapeRotation = 180f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.recoil.delay(0.3f)
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        mirror = true
        x = 12f
        y = -4f
        haloRotation = -160f
        shapes = 1
        triLength = 12f
        triLengthTo = 20f
        haloRadius = 0f
        tri = true
        radius = 0f
        radiusTo = 5f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.recoil.delay(0.3f)
        color = SglDrawConst.matrixNet
        layer = Layer.effect
        mirror = true
        x = 12f
        y = -4f
        haloRotation = -160f
        shapes = 1
        triLength = 0f
        triLengthTo = 5f
        haloRadius = 0f
        tri = true
        radius = 0f
        radiusTo = 5f
        shapeRotation = 180f
      }
    })

    newCoolant(1f, 0.25f, { l: Liquid? -> l!!.heatCapacity > 0.7f && l.temperature < 0.35f }, 0.4f, 20f)
  }
  var 春分 = SglTurret("spring").apply {
    bundle {
      desc(zh_CN, "春分", "这座炮塔能够引导能量够修复我方单位和建筑,同时它会侵入敌方的机械结构中,阻止其行动", "成熟的能量引导技术赋予了这座巨物十分突出的能力")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 120, IItems.铝, 140, Items.phaseFabric, 80, IItems.矩阵合金, 100, IItems.绿藻素, 120, IItems.充能FEX水晶, 85, IItems.铱锭, 60
      )
    )
    size = 5
    scaledHealth = 450f
    recoil = 1.8f
    rotateSpeed = 3f
    warmupSpeed = 0.022f
    linearWarmup = false
    fireWarmupThreshold = 0.6f
    range = 400f
    targetGround = true
    targetHealUnit = true
    targetAir = true
    targetHealing = true
    shootY = 12f
    shootEffect = Fx.none

    energyCapacity = 4096f
    basicPotentialEnergy = 1024f

    shootSound = Sounds.shootMalign

    shoot = object : ShootPattern() {
      override fun shoot(totalShots: Int, handler: BulletHandler) {
        for (i in intArrayOf(-1, 1)) {
          for (a in 1..2) {
            handler.shoot(0f, 0f, 4.57f * i, 0f) { b: Bullet? ->
              val len = b!!.time * 5f
              b.moveRelative(0f, i * (4 * a - 0.01f * a * len) * Mathf.sin(0.04f * len + 4))
            }
          }
        }
      }
    }
    shoot.shots = 2

    newAmmo(object : BulletType() {
      init {
        damage = 42f
        lifetime = 80f
        speed = 5f
        drawSize = 24f
        pierceCap = 4
        pierceBuilding = true
        collidesTeam = true
        smokeEffect = Fx.none
        hitColor = Pal.heal
        hitEffect = Fx.circleColorSpark
        healEffect = SglFx.impactBubble
        shootEffect = Fx.none
        healAmount = 24f
        healPercent = 0.1f
        hitSize = 8f
        trailColor = Pal.heal
        trailRotation = true
        trailEffect = Fx.disperseTrail
        trailInterval = 3f
        trailWidth = hitSize
        trailLength = 24

        hitSound = Sounds.drillImpact
      }

      override fun draw(b: Bullet) {
        super.draw(b)
        Draw.color(Pal.heal)
        Fill.circle(b.x, b.y, b.hitSize)
      }

      override fun update(b: Bullet) {
        super.update(b)
        Units.nearby(b.team, b.x, b.y, b.hitSize, Cons { u: Unit? ->
          if (u!!.damaged() && !b.hasCollided(u.id)) {
            b.collided.add(u.id)

            u.heal(u.maxHealth * (b.type.healPercent / 100) + b.type.healAmount)
            u.apply(IStatus.临春, 30f)
            b.type.healEffect.at(b.x, b.y, b.rotation(), b.type.healColor)
          }
        })
        Damage.status(b.team, b.x, b.y, b.hitSize, IStatus.暮春, 12f, true, true)
      }
    }) { s, b: mindustry.entities.bullet.BulletType? ->
      s!!.add(
        (Core.bundle.get("misc.toTeam") + " " + IStatus.临春.emoji() + "[stat]" + IStatus.临春.localizedName + "[lightgray] ~ [stat]0.5[lightgray] " + Core.bundle.get("unit.seconds") + "[]" + Sgl.NL + Core.bundle.get("misc.toEnemy") + " " + IStatus.暮春.emoji() + "[stat]" + IStatus.暮春.localizedName + "[lightgray] ~ [stat]0.2[lightgray] " + Core.bundle.get("unit.seconds") + "[]")
      )
    }
    consume!!.energy(2.6f)
    consume!!.time(60f)

    draw = DrawSglTurret(object : RegionPart("_side") {
      init {
        mirror = true
        moveX = 2f
        heatColor = Pal.heal
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup.delay(0.25f)

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, -6f))
      }
    }, object : RegionPart("_blade") {
      init {
        mirror = true
        moveY = 4f
        moveRot = -30f
        heatColor = Pal.heal
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup.delay(0.25f)

        moves.add(PartMove(PartProgress.recoil, -2f, -2f, 0f))
      }
    }, object : RegionPart("_body") {
      init {
        heatColor = Pal.heal
        heatProgress = PartProgress.warmup.delay(0.25f)
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 10f
        y = 16f
        drawRadius = 0f
        drawRadiusTo = 4f
        rotation = -30f

        moveY = -10f
        moveX = 2f
        moveRot = -35f
        progress = PartProgress.warmup
        layer = Layer.effect
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          Draw.color(Pal.heal)
          Drawf.tri(x, y, 4 * p, 6 + 10 * p, r)
          Drawf.tri(x, y, 4 * p, 4 * p, r + 180)
        }

        moves.add(PartMove(PartProgress.recoil, 0f, -1f, -3f))
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 10f
        y = 12f
        drawRadius = 0f
        drawRadiusTo = 4f
        rotation = -30f

        moveY = -10f
        moveX = 2f
        moveRot = -65f
        progress = PartProgress.warmup
        layer = Layer.effect
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          Draw.color(Pal.heal)
          Drawf.tri(x, y, 6 * p, 8 + 12 * p, r)
          Drawf.tri(x, y, 6 * p, 6 * p, r + 180)
        }

        moves.add(PartMove(PartProgress.recoil, 0f, -1f, -5f))
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 8f
        y = 16f
        drawRadius = 0f
        drawRadiusTo = 5f
        rotation = -30f

        moveY = -20f
        moveX = 4f
        moveRot = -90f
        progress = PartProgress.warmup
        layer = Layer.effect
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          Draw.color(Pal.heal)
          Drawf.tri(x, y, 8 * p, 8 + 16 * p, r)
          Drawf.tri(x, y, 8 * p, 8 * p, r + 180)
        }

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, -6f))
      }
    })
  }
  var 吹雪 = LaserTurret("fubuki").apply {
    bundle {
      desc(zh_CN, "吹雪", "向前喷发凛冽的冰霜风暴,凛冽的风雪足以将敌人冻结成冰雕")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 100, IItems.铝, 140, IItems.充能FEX水晶, 60, IItems.气凝胶, 80, IItems.铱锭, 30, Items.phaseFabric, 60
      )
    )
    size = 4
    scaledHealth = 400f
    rotateSpeed = 2.4f
    warmupSpeed = 0.01f
    fireWarmupThreshold = 0f
    linearWarmup = false
    range = 300f
    targetGround = true
    targetAir = true

    energyCapacity = 1024f
    basicPotentialEnergy = 256f

    shootY = 12f

    needCooldown = false
    shootingConsume = true

    shootSound = Sounds.none



    newAmmo(object : BulletType() {
      val ice: mindustry.entities.bullet.BulletType = crushedIce.copy()
      val shootBullets = arrayOf(ice, object : BulletType() {
        init {
          damage = 26f
          speed = 8f
          lifetime = 37.5f
          hitColor = Color.white
          despawnEffect = SglFx.cloudGradient

          trailWidth = 1.5f
          trailColor = Color.white
          trailLength = 18

          trailEffect = SglFx.iceParticle
          trailRotation = true
          trailChance = 0.07f

          knockback = 2f
        }

        override fun draw(b: Bullet) {
          super.draw(b)
          Draw.color(hitColor, 0f)

          Draw.z(Layer.flyingUnit + 1)
          SglDraw.gradientCircle(b.x, b.y, 14f, 0.6f)
          SglDraw.drawBloomUponFlyUnit(b) { e: Bullet ->
            Draw.color(hitColor)
            SglDraw.drawDiamond(e.x, e.y, 14f, 6 + Mathf.absin(1f, 2f), e.rotation())
          }
        }

        override fun hitEntity(b: Bullet?, entity: Hitboxc?, health: Float) {
          super.hitEntity(b, entity, health)
          if (entity is Statusc) {
            entity.apply(IStatus.霜冻, entity.getDuration(IStatus.霜冻) + 10f)
          }
        }
      }, object : BulletType() {
        init {
          damage = 36f
          speed = 6f
          lifetime = 50f
          hitColor = SglDrawConst.frost
          despawnEffect = SglFx.cloudGradient

          trailWidth = 2f
          trailColor = Color.white
          trailLength = 22

          trailEffect = SglFx.particleSpread
          trailRotation = true
          trailChance = 0.06f

          knockback = 4f
        }

        override fun draw(b: Bullet) {
          super.draw(b)
          Draw.color(hitColor, 0f)
          Draw.z(Layer.flyingUnit + 1)
          SglDraw.gradientCircle(b.x, b.y, 14f, 0.6f)

          SglDraw.drawBloomUponFlyUnit(b) { e: Bullet ->
            Draw.color(Color.white)
            Fill.circle(e.x, e.y, 2f)
            Lines.stroke(1f, hitColor)
            Lines.circle(e.x, e.y, 4f)
            val step = 360f / 6
            for (i in 0..5) {
              SglDraw.drawTransform(e.x, e.y, 6f, 0f, step * i + Time.time * 2) { x: Float, y: Float, r: Float ->
                Drawf.tri(x, y, 2.5f, 2.5f, r)
                Drawf.tri(x, y, 2.5f, 6f, r + 180)
              }
            }
            Draw.reset()
          }
        }

        override fun hitEntity(b: Bullet?, entity: Hitboxc?, health: Float) {
          super.hitEntity(b, entity, health)
          if (entity is Statusc) {
            entity.apply(IStatus.霜冻, entity.getDuration(IStatus.霜冻) + 12f)
          }
        }
      })

      init {
        ice.speed = 10f
        ice.lifetime = 30f
        ice.trailWidth = 1f
        ice.trailLength = 18
        ice.trailColor = SglDrawConst.frost
        ice.knockback = 1f

        speed = 0f
        lifetime = 10f
        rangeOverride = 300f
        despawnEffect = Fx.none
        hittable = false
        collides = false
        absorbable = false
      }

      val trans: Color? = Color.white.cpy().a(0f)

      override fun continuousDamage(): Float {
        var res = 0f
        for (i in shootBullets.indices) {
          res += shootBullets[i].damage * (1f / (i + 1))
        }
        return res * 4
      }

      override fun update(b: Bullet) {
        super.update(b)
        val owner = b.owner
        if (owner is SglTurretBuild && owner.isAdded) {
          b.keepAlive = owner.warmup > 0.01f

          owner.warmup = Mathf.lerpDelta(owner.warmup, (if (owner.wasShooting() && owner.shootValid()) 1 else 0).toFloat(), warmupSpeed)
          owner.reloadCounter = 0f

          if (b.timer(5, if (owner.warmup <= 0.01) Float.MAX_VALUE else 3 / owner.warmup)) {
            for (i in shootBullets.indices) {
              val bu = shootBullets[i]

              if (Mathf.chance((1f / (i + 1)).toDouble())) {
                bu.create(b, b.x, b.y, b.rotation() + Mathf.range(12 * owner.warmup))
              }
            }
          }
        } else b.remove()
      }

      override fun draw(b: Bullet) {
        super.draw(b)
        val owner = b.owner
        if (owner is SglTurretBuild) {
          Draw.color(SglDrawConst.frost)
          Fill.circle(b.x, b.y, 3 * owner.warmup)
          Lines.stroke(0.7f * owner.warmup)
          SglDraw.dashCircle(b.x, b.y, 4f, Time.time * 1.5f)

          Draw.draw(Draw.z()) {
            rand.setSeed(owner.id.toLong())
            MathRenderer.setDispersion(0.2f * owner.warmup)
            MathRenderer.setThreshold(0.3f, 0.6f)
            MathRenderer.drawOval(
              b.x, b.y, 8 * owner.warmup, 3 * owner.warmup, Time.time * rand.random(1.5f, 3f)
            )
            MathRenderer.drawOval(
              b.x, b.y, 9 * owner.warmup, 4f * owner.warmup, -Time.time * rand.random(1.5f, 3f)
            )
          }

          Tmp.v1.set(range, 0f).setAngle(owner.rotationu).scl(owner.warmup)
          Tmp.v2.set(Tmp.v1).rotate(owner.warmup * 15)
          Tmp.v1.rotate(-owner.warmup * 15)

          Draw.z(Layer.flyingUnit)
          SglDraw.gradientLine(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, SglDrawConst.frost, trans, 0)
          SglDraw.gradientLine(b.x, b.y, b.x + Tmp.v2.x, b.y + Tmp.v2.y, SglDrawConst.frost, trans, 0)
        }
      }
    })
    consume!!.time(1f)
    consume!!.showTime = false
    consume!!.energy(3.2f)
    consume!!.liquid(Liquids.cryofluid, 0.2f)

    draw = DrawSglTurret(object : RegionPart("_blade") {
      init {
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup

        heatColor = SglDrawConst.frost

        moveX = 2f
        moveY = -6f

        mirror = true
      }
    }, object : RegionPart("_body") {
      init {
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup
        heatColor = SglDrawConst.frost

        moveY = -4f
      }
    })
  }
  var 霜降 = LaserTurret("frost").apply {

    bundle {
      desc(zh_CN, "霜降", "从激光冷却发展而来的巨型冷冻光束炮,用巨大的冰冻光束给敌人沉痛而寒冷的重击")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 160, IItems.铝, 110, Items.phaseFabric, 100, IItems.矩阵合金, 120, IItems.充能FEX水晶, 100, IItems.铱锭, 100
      )
    )
    size = 5
    scaledHealth = 420f
    recoil = 2.8f
    rotateSpeed = 2f
    warmupSpeed = 0.02f
    fireWarmupThreshold = 0.9f
    linearWarmup = false
    range = 360f
    targetGround = true
    targetAir = true
    shootEffect = SglFx.railShootRecoil

    energyCapacity = 4096f
    basicPotentialEnergy = 1024f

    shootSound = Sounds.shootLaser


    updating = Cons { e: SglBuilding ->
      val t = e as SglTurretBuild
      if (Mathf.chanceDelta((0.08f * e.warmup()).toDouble())) SglFx.iceParticle.at(
        t.x + Angles.trnsx(t.rotationu, -12f), t.y + Angles.trnsy(t.rotationu, -12f), t.rotationu + 90 * Mathf.randomSign(), SglDrawConst.frost
      )
      if (Mathf.chanceDelta((0.05f * e.warmup()).toDouble())) SglFx.iceParticle.at(
        t.x + Angles.trnsx(t.rotationu, 22f), t.y + Angles.trnsy(t.rotationu, 22f), t.rotationu + 15 * Mathf.randomSign(), SglDrawConst.frost
      )
    }

    newAmmo(object : ContinuousLaserBulletType() {
      init {
        pierceCap = 5
        damage = 115f
        lifetime = 240f
        damageInterval = 6f
        fadeTime = 30f
        length = 360f
        width = 8f
        hitColor = SglDrawConst.frost
        fragBullet = crushedIce
        fragBullets = 2
        fragSpread = 10f
        fragOnHit = true
        despawnHit = false
        fragRandomSpread = 60f
        incendAmount = 0
        incendChance = 0f
        drawSize = 500f
        pointyScaling = 0.7f
        oscMag = 0.8f
        oscScl = 1.2f
        frontLength = 220f
        lightColor = SglDrawConst.frost
        colors = arrayOf<Color?>(
          Color.valueOf("6CA5FF").a(0.6f), Color.valueOf("6CA5FF").a(0.85f), Color.valueOf("ACE7FF"), Color.valueOf("DBFAFF")
        )
      }

      override fun update(b: Bullet) {
        super.update(b)
        val owner = b.owner
        if (owner is SglTurretBuild) {
          owner.heat = 1f
          owner.curRecoil = owner.heat
          owner.warmup = owner.curRecoil
        }
      }

      override fun hitEntity(b: Bullet, entity: Hitboxc?, health: Float) {
        if (entity is Healthc) {
          entity.damage(b.damage)
        }

        if (entity is Unit) {
          entity.apply(IStatus.霜冻, entity.getDuration(IStatus.霜冻) + 10)
        }
      }
    })
    consume!!.liquid(Liquids.cryofluid, 0.4f)
    consume!!.energy(2.4f)
    consume!!.time(210f)

    draw = DrawSglTurret(object : RegionPart("_side") {
      init {
        mirror = true
        moveX = 8f
        moveRot = -22f
        heatColor = SglDrawConst.frost
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup.delay(0.5f)

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, -8f))
      }
    }, object : RegionPart("_blade") {
      init {
        mirror = true
        moveY = 2f
        moveX = 4f
        moveRot = -24f
        heatColor = SglDrawConst.frost
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup.delay(0.5f)

        moves.add(PartMove(PartProgress.recoil, 0f, -1f, -6f))
      }
    }, object : CustomPart() {
      init {
        y = 4f
        progress = PartProgress.warmup
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          SglDraw.gradientTri(x, y, 40 + 260 * p, 60 * p, r, SglDrawConst.frost, 0f)
          SglDraw.gradientTri(x, y, 40 * p, 60 * p, r + 180, SglDrawConst.frost, 0f)
        }
      }
    }, object : RegionPart("_body") {
      init {
        heatColor = SglDrawConst.frost
        heatProgress = PartProgress.warmup.delay(0.5f)
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 16f
        y = 16f
        rotation = -12f

        layer = Layer.effect
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          SglDraw.gradientTri(x, y, 8 + 32 * p, 6 * p, r, SglDrawConst.frost, 0f)
          SglDraw.drawDiamond(x, y, 8 + 16 * p, 6 * p, r, SglDrawConst.frost)
        }
        progress = PartProgress.warmup.delay(0.5f)

        moves.add(PartMove(PartProgress.recoil, 0f, -1f, -8f))
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 30f
        y = 4f
        rotation = -45f

        layer = Layer.effect
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          SglDraw.gradientTri(x, y, 12 + 36 * p, 6 * p, r, SglDrawConst.frost, 0f)
          SglDraw.drawDiamond(x, y, 12 + 18 * p, 6 * p, r, SglDrawConst.frost)
        }
        progress = PartProgress.warmup.delay(0.5f)

        moves.add(PartMove(PartProgress.recoil, 2f, -1.5f, -9f))
      }
    }, object : HaloPart() {
      init {
        color = SglDrawConst.frost
        tri = true
        y = -12f
        radius = 0f
        radiusTo = 8f
        triLength = 8f
        triLengthTo = 18f
        haloRadius = 0f
        shapes = 2
        layer = Layer.effect
        progress = PartProgress.warmup
      }
    }, object : ShapePart() {
      init {
        circle = true
        color = Color.white
        y = 24f
        radius = 0f
        radiusTo = 6f
        layer = Layer.effect
        progress = PartProgress.warmup
      }
    }, object : ShapePart() {
      init {
        circle = true
        color = SglDrawConst.frost
        y = 24f
        radius = 0f
        radiusTo = 6f
        hollow = true
        stroke = 0f
        strokeTo = 2.5f
        layer = Layer.effect
        progress = PartProgress.warmup
      }
    }, object : CustomPart() {
      init {
        y = -12f
        layer = Layer.effect
        progress = PartProgress.warmup
        rotation = 90f

        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          SglDraw.drawDiamond(x, y, 20 + 76 * p, 32 * p, r, SglDrawConst.frost, 0f)
        }
      }
    })
  }
  var 冬至 = SglTurret("winter").apply {
    bundle {
      desc(zh_CN, "冬至", "它用力场,将周围的物质分子的移动牢牢的限制,在极寒领域展开的瞬间,有如时间也被冻结一般,一切都停了下来,并破碎成无数微小的碎片")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 210, IItems.简并态中子聚合物, 80, Items.phaseFabric, 180, IItems.铱锭, 100, IItems.气凝胶, 200, IItems.铝, 220, IItems.矩阵合金, 160, IItems.充能FEX水晶, 180
      )
    )
    size = 6
    scaledHealth = 410f
    recoil = 3.6f
    rotateSpeed = 1.75f
    warmupSpeed = 0.015f
    shake = 6f
    fireWarmupThreshold = 0.925f
    linearWarmup = false
    range = 560f
    targetGround = true
    targetAir = true
    shootEffect = MultiEffect(
      SglFx.winterShooting, SglFx.shootRecoilWave, object : WaveEffect() {
        init {
          colorTo = Pal.reactorPurple
          colorFrom = colorTo
          lifetime = 12f
          sizeTo = 40f
          strokeFrom = 6f
          strokeTo = 0.3f
        }
      })
    moveWhileCharging = true
    shootY = 4f

    unitSort = SglUnitSorts.denser

    energyCapacity = 4096f
    basicPotentialEnergy = 4096f

    shoot.firstShotDelay = 120f
    chargeSound = Sounds.chargeLancer
    chargeSoundPitch = 0.9f

    shootSound = Sounds.explosionReactor
    shootSoundPitch = 0.6f
    shootSoundVolume = 2f

    soundPitchRange = 0.05f

    newAmmo(object : BulletType() {
      init {
        lifetime = 20f
        speed = 28f
        collides = false
        absorbable = false
        scaleLife = true
        drawSize = 80f
        fragBullet = object : BulletType() {
          init {
            lifetime = 120f
            speed = 0.6f
            collides = false
            hittable = true
            absorbable = false
            despawnHit = true
            splashDamage = 2180f
            splashDamageRadius = 84f
            hitShake = 12f

            trailEffect = SglFx.particleSpread
            trailInterval = 10f
            trailColor = SglDrawConst.winter

            hitEffect = SglFx.iceExplode
            hitColor = SglDrawConst.winter

            hitSound = Sounds.explosionAfflict
            hitSoundPitch = 0.6f
            hitSoundVolume = 2.5f

            fragBullet = freezingField
            fragOnHit = false
            fragBullets = 1
            fragVelocityMin = 0f
            fragVelocityMax = 0f
          }

          override fun draw(b: Bullet) {
            super.draw(b)
            Draw.color(SglDrawConst.winter)

            SglDraw.drawBloomUponFlyUnit(b) { e: Bullet ->
              val rot = e.fin(Interp.pow2Out) * 3600
              SglDraw.drawCrystal(
                e.x, e.y, 30f, 14f, 8f, 0f, 0f, 0.8f, Layer.effect, Layer.bullet, rot, e.rotation(), SglDrawConst.frost, SglDrawConst.winter
              )

              Draw.alpha(1f)
              Fill.circle(e.x, e.y, 18 * e.fin(Interp.pow3In))
              Draw.reset()
            }
          }

          override fun update(b: Bullet) {
            super.update(b)
            Vars.control.sound.loop(Sounds.loopPulse, b, 2f)
          }
        }
        fragBullets = 1
        fragSpread = 0f
        fragRandomSpread = 0f
        fragAngle = 0f
        fragOnHit = false
        hitColor = SglDrawConst.winter

        hitEffect = Fx.none
        despawnEffect = Fx.none
        smokeEffect = Fx.none

        trailEffect = MultiEffect(
          SglFx.glowParticle, SglFx.railShootRecoil
        )
        trailRotation = true
        trailChance = 1f

        trailLength = 75
        trailWidth = 7f
        trailColor = SglDrawConst.winter

        chargeEffect = SglFx.shrinkIceParticleSmall
      }

      override fun draw(b: Bullet) {
        super.draw(b)
        Draw.z(Layer.bullet)
        Draw.color(SglDrawConst.winter)
        val rot = b.fin() * 3600

        SglDraw.drawCrystal(
          b.x, b.y, 30f, 14f, 8f, 0f, 0f, 0.8f, Layer.effect, Layer.bullet, rot, b.rotation(), SglDrawConst.frost, SglDrawConst.winter
        )
      }
    }, true) { bt, ammo: mindustry.entities.bullet.BulletType? ->
      bt!!.add(Core.bundle.format("bullet.splashdamage", ammo!!.fragBullet.splashDamage.toInt(), Strings.fixed(ammo.fragBullet.splashDamageRadius / Vars.tilesize, 1)))
      bt.row()
      bt.add(Core.bundle.get("infos.winterAmmo"))
    }
    consume!!.time(720f)
    consume!!.energy(1.1f)
    consume!!.liquids(
      *LiquidStack.with(
        ILiquids.相位态FEX流体, 0.2f, Liquids.cryofluid, 0.2f
      )
    )

    updating = Cons { e: SglBuilding? ->
      val t = e as SglTurretBuild?
      if (Mathf.chanceDelta((0.06f * t!!.warmup).toDouble())) {
        Tmp.v1.set(36f, 0f).setAngle(t.rotationu + 90 * Mathf.randomSign()).rotate(Mathf.random(-30, 30).toFloat())
        SglFx.iceParticle.at(e.x + Tmp.v1.x, e.y + Tmp.v1.y, Tmp.v1.angle(), SglDrawConst.frost)
      }
    }

    draw = DrawSglTurret(object : CustomPart() {
      init {
        progress = PartProgress.warmup
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          Draw.color(SglDrawConst.winter)
          SglDraw.gradientTri(x, y, 70 + 120 * p, 92 * p, r, 0f)
          SglDraw.gradientTri(x, y, 40 + 68 * p, 92 * p, r + 180, 0f)
          Draw.color()
        }
      }
    }, object : RegionPart("_blade") {
      init {
        mirror = true
        heatColor = SglDrawConst.winter
        heatProgress = PartProgress.warmup.delay(0.3f)
        moveX = 5f
        moveY = 4f
        moveRot = -15f
        progress = PartProgress.warmup

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, 0f))
      }
    }, object : RegionPart("_side") {
      init {
        mirror = true
        heatColor = SglDrawConst.winter
        heatProgress = PartProgress.warmup.delay(0.3f)
        moveX = 8f
        moveRot = -30f
        progress = PartProgress.warmup

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, -5f))
      }
    }, object : RegionPart("_bot") {
      init {
        mirror = true
        heatColor = SglDrawConst.winter
        heatProgress = PartProgress.warmup.delay(0.3f)
        moveX = 6f
        moveY = 2f
        moveRot = -25f
        progress = PartProgress.warmup

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, 0f))
      }
    }, object : RegionPart("_body") {
      init {
        heatColor = SglDrawConst.winter
        heatProgress = PartProgress.warmup.delay(0.3f)
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 20f
        drawRadius = 0f
        drawRadiusTo = 20f
        rotation = -30f
        layer = Layer.effect
        progress = PartProgress.warmup
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          SglDraw.drawCrystal(
            x, y, 8 + 8 * p, 6 * p, 4 * p, 0f, 0f, 0.4f * p, Layer.effect, Layer.bullet - 1, Time.time * 1.24f, r, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter
          )
        }
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 20f
        drawRadius = 0f
        drawRadiusTo = 28f
        rotation = -65f
        layer = Layer.effect
        progress = PartProgress.warmup.delay(0.15f)
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          SglDraw.drawCrystal(
            x, y, 16 + 21 * p, 12 * p, 8 * p, 0f, 0f, 0.7f * p, Layer.effect, Layer.bullet - 1, Time.time * 1.24f + 45, r, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter
          )
        }
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 20f
        drawRadius = 0f
        drawRadiusTo = 24f
        rotation = -105f
        layer = Layer.effect
        progress = PartProgress.warmup.delay(0.3f)
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          SglDraw.drawCrystal(
            x, y, 12 + 14 * p, 10 * p, 6 * p, 0f, 0f, 0.6f * p, Layer.effect, Layer.bullet - 1, Time.time * 1.24f + 90, r, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter
          )
        }
      }
    }, object : CustomPart() {
      init {
        mirror = true
        x = 20f
        drawRadius = 0f
        drawRadiusTo = 20f
        rotation = -135f
        layer = Layer.effect
        progress = PartProgress.warmup.delay(0.45f)
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          SglDraw.drawCrystal(
            x, y, 9 + 12 * p, 8 * p, 5 * p, 0f, 0f, 0.65f * p, Layer.effect, Layer.bullet - 1, Time.time * 1.24f + 135, r, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter
          )
        }
      }
    }, object : CustomPart() {
      init {
        progress = PartProgress.charge
        y = 4f
        layer = Layer.effect
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          Draw.color(SglDrawConst.winter)
          Drawf.tri(x, y, 10 * p, 12 * p, r)
          Drawf.tri(x, y, 10 * p, 8 * p, r + 180)
          Draw.color(SglDrawConst.frost)
          SglDraw.gradientCircle(x, y, 4 + 12 * p, -7 * p, 0f)
        }
      }
    }, object : CustomPart() {
      init {
        progress = PartProgress.warmup
        y = -18f
        layer = Layer.effect
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          Draw.color(SglDrawConst.frost)
          Lines.stroke(1.8f * p)
          Lines.circle(x, y, 3.5f)
          Draw.alpha(0.7f)

          for (i in 0..5) {
            SglDraw.drawTransform(x, y, 14 * p, 0f, r + Time.time * 1.5f + i * 60) { dx: Float, dy: Float, dr: Float ->
              Drawf.tri(dx, dy, 4 * p, 4f, dr)
              Drawf.tri(dx, dy, 4 * p, 14f, dr + 180f)
            }
          }

          Draw.color(SglDrawConst.winter)
          val pl = Mathf.clamp((p - 0.3f) / 0.7f)
          for (i in 0..3) {
            SglDraw.drawTransform(x, y, 16 * pl, 0f, r - Time.time + i * 90) { dx: Float, dy: Float, dr: Float ->
              SglDraw.drawCrystal(
                dx, dy, 12f, 8 * pl, 8 * pl, 0f, 0f, 0.5f * pl, Layer.effect, Layer.bullet - 1, Time.time, dr, Tmp.c1.set(SglDrawConst.frost).a(0.65f), SglDrawConst.winter
              )
            }
          }
        }
      }
    })
  }
  var 虚妄 = SglTurret("mirage").apply {
    bundle {
      desc(zh_CN, "虚妄", "高能FEX结晶弹射器,将大块结晶态FEX发射向目标,不同的结晶状态会产生截然不同的效果,在互相作用下可以造成相当大的杀伤效果")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 260, IItems.矩阵合金, 120, IItems.气凝胶, 200, IItems.铀238, 160, IItems.铱锭, 80, IItems.FEX水晶, 120
      )
    )
    size = 5

    scaledHealth = 380f
    recoil = 2.8f
    recoilTime = 120f
    rotateSpeed = 2f
    warmupSpeed = 0.023f
    shake = 3.6f
    fireWarmupThreshold = 0.92f
    linearWarmup = false
    range = 480f

    targetAir = true
    targetGround = true

    shootEffect = MultiEffect(
      SglFx.shootRail, SglFx.shootRecoilWave
    )
    smokeEffect = Fx.shootSmokeSmite

    shootSound = Sounds.blockExplode1Alt
    shootSoundVolume = 1.4f

    newAmmo(object : MultiTrailBulletType() {
      init {
        damage = 380f
        speed = 8f
        lifetime = 60f

        pierceCap = 4
        pierceBuilding = true

        hitSize = 6f

        knockback = 1.7f

        status = IStatus.结晶化
        statusDuration = 150f

        hittable = false
        despawnHit = true

        hitEffect = MultiEffect(
          Fx.shockwave, SglFx.diamondSpark
        )

        fragOnHit = false
        fragOnAbsorb = true
        fragBullets = 8
        fragBullet = 破碎FEX结晶.copy()
        fragBullet.homingRange = 160f
        fragBullet.homingPower = 0.1f

        trailColor = SglDrawConst.fexCrystal
        trailWidth = 4f
        trailLength = 18
        trailEffect = Fx.colorSparkBig
        trailChance = 0.24f
        trailRotation = true

        hitColor = SglDrawConst.fexCrystal
        val gen: VectorLightningGenerator = VectorLightningGenerator().apply {
          branchChance = 0.18f
          minBranchStrength = 0.8f
          maxBranchStrength = 1f

          minInterval = 5f
          maxInterval = 15f

          branchMaker = Func2 { vert, strength ->
            branch.maxLength = (40 * strength)
            branch.originAngle = vert.angle + Mathf.random(-90, 90)
            branch
          }
        }

        intervalBullet = lightning(30f, 45f, 4f, SglDrawConst.fexCrystal, true) { b: Bullet? ->
          val e = Units.bestEnemy(b!!.team, b.x, b.y, 80f, { u: Unit -> true }, UnitSorts.farthest)
          if (e == null) {
            gen.vector.rnd(Mathf.random(40f, 80f))
          } else gen.vector.set(e.x - b.x, e.y - b.y).add(Mathf.random(-3f, 3f), Mathf.random(-3f, 3f))
          gen
        }
        bulletInterval = 1f
      }

      override fun draw(b: Bullet) {
        super.draw(b)
        Draw.z(Layer.bullet)
        Draw.color(SglDrawConst.fexCrystal)
        val rot = b.fin() * 1800

        SglDraw.drawCrystal(
          b.x, b.y, 30f, 14f, 8f, 0f, 0f, 0.8f, Layer.effect, Layer.bullet, rot, b.rotation(), Tmp.c1.set(SglDrawConst.fexCrystal).a(0.6f), SglDrawConst.fexCrystal
        )
      }
    }) { t, b: mindustry.entities.bullet.BulletType? ->
      t!!.add(Core.bundle.format("infos.generateLightning", 60 / b!!.bulletInterval, 45))
    }
    consume!!.item(IItems.FEX水晶, 1)
    consume!!.time(60f)

    newAmmo(object : MultiTrailBulletType() {
      init {
        damage = 520f
        speed = 6f
        lifetime = 80f

        pierceCap = 4
        pierceBuilding = true

        hitSize = 8f

        knockback = 1.7f

        subTrails = 3

        absorbable = false
        hittable = false
        despawnHit = true

        hitEffect = MultiEffect(
          Fx.shockwave, Fx.bigShockwave, SglFx.crossLight, SglFx.spreadSparkLarge, SglFx.diamondSparkLarge
        )

        fragBullets = 1
        fragBullet = object : singularity.world.blocks.turrets.LightningBulletType() {
          init {
            damage = 42f
            lifetime = 105f
            speed = 6f

            hitColor = SglDrawConst.fexCrystal

            collides = false
            pierceCap = 42
            hittable = false
            absorbable = false

            despawnEffect = MultiEffect(
              Fx.shockwave, SglFx.diamondSpark
            )

            trailColor = SglDrawConst.fexCrystal
            trailEffect = SglFx.movingCrystalFrag
            trailInterval = 4f
          }

          val gen: VectorLightningGenerator = VectorLightningGenerator().apply {
            minInterval = 6f
            maxInterval = 16f
          }
          val s: singularity.world.blocks.turrets.LightningBulletType = this

          override fun continuousDamage(): Float {
            return damage * 20
          }

          override fun init(b: Bullet?, cont: LightningContainer) {
            super.init(b, cont)
            cont.lifeTime = 16f
            cont.minWidth = 2.5f
            cont.maxWidth = 4.5f
            cont.lerp = Interp.pow2Out
            cont.time = 0f
          }

          override fun update(b: Bullet, container: LightningContainer) {
            super.update(b, container)

            b.vel.x = Mathf.lerpDelta(b.vel.x, 0f, 0.05f)
            b.vel.y = Mathf.lerpDelta(b.vel.y, 0f, 0.05f)

            if (b.timer(4, 3f)) {
              var tar: Hitboxc? = null
              var dst = 0f
              for (unit in Groups.unit.intersect(b.x - 180, b.y - 180, 360f, 360f)) {
                if (unit.team === b.team || !unit.hasEffect(IStatus.结晶化)) continue
                val d = unit.dst(b)
                if (d > 180) continue

                if (tar == null || d > dst) {
                  tar = unit
                  dst = d
                }
              }

              if (tar == null) {
                dst = 0f
                for (bullet in Groups.bullet.intersect(b.x - 180, b.y - 180, 360f, 360f)) {
                  if (bullet.team !== b.team || bullet.type !== s) continue
                  val d = bullet.dst(b)
                  if (d > 180) continue

                  if (tar == null || d > dst) {
                    tar = bullet
                    dst = d
                  }
                }
              }

              if (tar == null) return

              gen.vector.set(tar.x() - b.x, tar.y() - b.y)

              container.create(gen)

              Damage.collideLine(b, b.team, b.x, b.y, gen.vector.angle(), gen.vector.len(), false, false)
            }
          }

          override fun draw(b: Bullet, c: LightningContainer) {
            super.draw(b, c)
            val rot = b.fin(Interp.pow2Out) * 1800
            SglDraw.drawCrystal(
              b.x, b.y, 30f, 14f, 9f, 0f, 0f, 0.6f, Layer.effect, Layer.bullet, rot, b.rotation(), Tmp.c1.set(SglDrawConst.fexCrystal).a(0.6f), SglDrawConst.fexCrystal
            )

            Lines.stroke(0.6f * b.fout(), SglDrawConst.fexCrystal)
            SglDraw.dashCircle(b.x, b.y, 180f, 6, 180f, Time.time * 1.6f)
          }

          override fun despawned(b: Bullet) {
            super.despawned(b)

            Damage.damage(b.team, b.x, b.y, 60f, 180f)
          }
        }

        trailColor = SglDrawConst.fexCrystal
        trailWidth = 5f
        trailLength = 22
        trailEffect = Fx.colorSparkBig
        trailChance = 0.24f
        trailRotation = true

        hitColor = SglDrawConst.fexCrystal
        val gen: VectorLightningGenerator = VectorLightningGenerator().apply {
          branchChance = 0.17f
          minBranchStrength = 0.8f
          maxBranchStrength = 1f
          minInterval = 5f
          maxInterval = 15f
          branchMaker = Func2 { vert, strength ->
            branch.maxLength = (40 * strength)
            branch.originAngle = vert.angle + Mathf.random(-90, 90)
            branch
          }
        }
        intervalBullet = lightning(30f, 60f, 4f, SglDrawConst.fexCrystal, true) { b: Bullet? ->
          val e = Units.bestEnemy(b!!.team, b.x, b.y, 80f, Boolf { u: Unit? -> true }, UnitSorts.farthest)
          if (e == null) {
            gen.vector.rnd(Mathf.random(40f, 80f))
          } else gen.vector.set(e.x - b.x, e.y - b.y).add(Mathf.random(-3f, 3f), Mathf.random(-3f, 3f))
          gen
        }
        bulletInterval = 1.5f
      }

      override fun draw(b: Bullet) {
        super.draw(b)

        Draw.z(Layer.bullet)
        Draw.color(SglDrawConst.fexCrystal)
        val rot = b.fin() * 1800

        SglDraw.drawCrystal(
          b.x, b.y, 30f, 14f, 8f, 0f, 0f, 0.8f, Layer.effect, Layer.bullet, rot, b.rotation(), Tmp.c1.set(SglDrawConst.fexCrystal).a(0.6f), SglDrawConst.fexCrystal
        )
      }

      override fun hitEntity(b: Bullet, entity: Hitboxc?, health: Float) {
        super.hitEntity(b, entity, health)

        if (b.vel.len() > 0.3f) {
          b.time -= b.vel.len()
        }
        b.vel.scl(0.6f)

        if (entity is Unit && entity.hasEffect(IStatus.结晶化)) {
          for (i in 0..4) {
            val len = Mathf.random(1f, 7f)
            val a = b.rotation() + Mathf.range(fragRandomSpread / 2) + fragAngle + ((i - 2) * fragSpread)
            破碎FEX结晶.create(
              b, entity.x + Angles.trnsx(a, len), entity.y + Angles.trnsy(a, len), a, Mathf.random(fragVelocityMin, fragVelocityMax), Mathf.random(fragLifeMin, fragLifeMax)
            )
          }
        }
      }
    }, true) { table, b: mindustry.entities.bullet.BulletType? ->
      table!!.add(Core.bundle.format("bullet.damage", b!!.damage))
      table.row()
      table.add(Core.bundle.format("bullet.pierce", b.pierceCap))
      table.row()
      table.add(Core.bundle.format("bullet.frags", b.fragBullets))
      table.row()
      table.table { t ->
        t!!.add(
          Core.bundle.format(
            "infos.mirageLightningDamage", Strings.autoFixed(180f / Vars.tilesize, 1), (b.fragBullet.damage * 20).toString() + StatUnit.perSecond.localized(), IStatus.结晶化.emoji() + IStatus.结晶化.localizedName
          )
        )
      }.left().padLeft(15f)
      table.row()
      table.add(Core.bundle.format("infos.generateLightning", 60 / b.bulletInterval, 60))
    }
    consume!!.item(IItems.充能FEX水晶, 2)
    consume!!.time(120f)

    draw = DrawSglTurret(object : RegionPart("_shooter") {
      init {
        mirror = false
        heatProgress = PartProgress.warmup
        heatColor = SglDrawConst.fexCrystal

        progress = PartProgress.recoil

        moveY = -4f
      }
    }, object : RegionPart("_side") {
      init {
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup

        heatColor = SglDrawConst.fexCrystal
        mirror = true

        moveX = 8f
        moveRot = -35f

        moves.add(PartMove(PartProgress.recoil, 0f, 0f, -10f))
      }
    }, object : RegionPart("_blade") {
      init {
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup

        heatColor = SglDrawConst.fexCrystal
        mirror = true

        moveX = 2f
        moveY = -4f
        moveRot = 15f

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, 5f))
      }
    }, object : RegionPart("_body") {
      init {
        heatProgress = PartProgress.warmup
        heatColor = SglDrawConst.fexCrystal

        mirror = false
      }
    })
  }
  var 阳炎 = SglTurret("soflame").apply {
    bundle {
      desc(zh_CN, "阳炎", "将能量聚集到“太阳分子”上,直到能量足够高时发射出去,极热的物质云会留下灼热的轨迹,并在碰撞时爆炸,将目标化为灰烬")
    }
    requirements(
      Category.turret, ItemStack.with(
        IItems.强化合金, 150, IItems.铝, 180, IItems.FEX水晶, 140, IItems.充能FEX水晶, 120, IItems.气凝胶, 180, IItems.铱锭, 60, Items.surgeAlloy, 120, Items.phaseFabric, 100
      )
    )
    size = 5
    recoil = 4f
    recoilTime = 120f
    rotateSpeed = 1.5f
    shootCone = 3f
    warmupSpeed = 0.018f
    fireWarmupThreshold = 0.9f
    linearWarmup = false
    range = 360f
    shootY = 8f
    shake = 8f

    energyCapacity = 4096f
    basicPotentialEnergy = 2048f

    shootEffect = SglFx.shootRail
    shootSound = Sounds.shootSmite
    smokeEffect = Fx.shootSmokeSmite

    unitSort = SglUnitSorts.denser
    val subBullet: mindustry.entities.bullet.BulletType = object : HeatBulletType() {
      init {
        speed = 4f
        lifetime = 90f

        damage = 0f
        splashDamage = 90f
        splashDamageRadius = 8f

        meltDownTime = 30f
        melDamageScl = 0.5f
        maxExDamage = 150f

        trailColor = Pal.lighterOrange
        hitColor = Pal.lighterOrange
        trailEffect = SglFx.glowParticle
        trailChance = 0.1f
        trailRotation = true

        hitEffect = MultiEffect(
          object : WaveEffect() {
            init {
              colorFrom = Pal.lighterOrange
              colorTo = Color.white
              lifetime = 12f
              sizeTo = 28f
              strokeFrom = 6f
              strokeTo = 0.3f
            }
          }, Fx.circleColorSpark
        )
        despawnEffect = Fx.absorb
        despawnHit = true

        trailWidth = 2f
        trailLength = 24
      }

      override fun draw(b: Bullet) {
        super.draw(b)
        Draw.color(hitColor)
        Fill.circle(b.x, b.y, 3f)
      }
    }
    newAmmo(object : HeatBulletType() {
      init {
        damage = 260f
        splashDamage = 540f
        splashDamageRadius = 32f
        hitSize = 5f
        speed = 4f
        lifetime = 90f

        hitShake = 14f

        hitColor = Pal.lighterOrange
        trailColor = Pal.lighterOrange

        hitSound = Sounds.explosion
        hitSoundVolume = 4f

        trailEffect = SglFx.trailParticle
        trailChance = 0.1f

        hitEffect = MultiEffect(
          object : WaveEffect() {
            init {
              colorTo = Pal.lighterOrange
              colorFrom = colorTo
              lifetime = 12f
              sizeTo = 50f
              strokeFrom = 7f
              strokeTo = 0.3f
            }
          }, SglFx.explodeImpWaveLarge, SglFx.impactBubble
        )

        meltDownTime = 90f
        melDamageScl = 0.3f
      }

      override fun init(b: Bullet) {
        super.init(b)
        val p = SglParticleModels.heatBulletTrail.create(b.x, b.y, Pal.lighterOrange, 0f, 0f, 5f)
        p.owner = b
        p.bullet = SglParticleModels.defHeatTrailHitter.create(b, b.x, b.y, b.rotation())

        Tmp.v1.set(1f, 0f).setAngle(b.rotation())
        for (i in 0..3) {
          val off = Mathf.random(0f, Mathf.PI2)
          val scl = Mathf.random(3f, 6f)
          val x = b.x
          val y = b.y
          Time.run((i * 5).toFloat()) {
            for (sign in Mathf.signs) {
              subBullet.create(b, x, y, b.rotation()).mover = Mover { e: Bullet? -> e!!.moveRelative(0f, Mathf.sin(e.time + off, scl, ((1 + i) * sign).toFloat())) }
            }
          }
        }
      }
    }) { t, b: mindustry.entities.bullet.BulletType? ->
      t!!.table { child ->
        child!!.left().add(Core.bundle.format("infos.shots", 6)).color(Color.lightGray).left()
        UIUtils.buildAmmo(child, subBullet)
      }.padLeft(15f)
    }
    consume!!.time(180f)
    consume!!.energy(5f)

    draw = object : DrawSglTurret(object : RegionPart("_blade") {
      init {
        progress = PartProgress.warmup
        heatProgress = PartProgress.warmup
        mirror = true
        moveX = 4f
        heatColor = Pal.lightishOrange

        moves.add(PartMove(PartProgress.recoil, 0f, -2f, 0f))
      }
    }, object : RegionPart("_body") {
      init {
        mirror = false
        heatProgress = PartProgress.warmup
        heatColor = Pal.lightishOrange
      }
    }, object : ShapePart() {
      init {
        progress = PartProgress.warmup
        y = shootY
        circle = true
        radius = 0f
        radiusTo = 4f
        layer = Layer.effect
      }
    }, object : CustomPart() {
      init {
        progress = PartProgress.warmup
        y = shootY
        layer = Layer.effect
        draw = Drawer { x: Float, y: Float, r: Float, p: Float ->
          Lines.stroke(0.8f * p, Pal.lighterOrange)
          SglDraw.dashCircle(x, y, 6 * p, Time.time * 1.7f)
        }
      }
    }, object : ShapePart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        circle = true
        y = -18f
        radius = 0f
        radiusTo = 4f
      }
    }, object : ShapePart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        circle = true
        hollow = true
        y = -18f
        stroke = 0f
        strokeTo = 2f
        radius = 0f
        radiusTo = 10f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        tri = true
        y = -18f
        haloRadius = 10f
        haloRotateSpeed = 1f
        shapes = 4
        radius = 4f
        triLength = 0f
        triLengthTo = 8f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        tri = true
        y = -18f
        haloRadius = 10f
        haloRotateSpeed = 1f
        shapes = 4
        radius = 4f
        triLength = 0f
        triLengthTo = 4f
        shapeRotation = 180f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        y = -18f
        tri = true
        shapes = 2
        haloRadius = 10f
        haloRotation = 90f
        radius = 5f
        triLength = 0f
        triLengthTo = 30f
        shapeRotation = 0f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup
        color = Pal.lighterOrange
        layer = Layer.effect
        y = -18f
        tri = true
        shapes = 2
        haloRadius = 10f
        haloRotation = 90f
        radius = 5f
        triLength = 0f
        triLengthTo = 5f
        shapeRotation = 180f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup.delay(0.2f)
        color = Pal.lighterOrange
        layer = Layer.effect
        y = 0f
        tri = true
        shapes = 2
        haloRadius = 18f
        haloRotation = 90f
        radius = 4f
        triLength = 0f
        triLengthTo = 20f
        shapeRotation = 0f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup.delay(0.2f)
        color = Pal.lighterOrange
        layer = Layer.effect
        y = 0f
        tri = true
        shapes = 2
        haloRadius = 18f
        haloRotation = 90f
        radius = 4f
        triLength = 0f
        triLengthTo = 4f
        shapeRotation = 180f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup.delay(0.4f)
        color = Pal.lighterOrange
        layer = Layer.effect
        y = 8f
        tri = true
        shapes = 2
        haloRadius = 15f
        haloRotation = 90f
        radius = 4f
        triLength = 0f
        triLengthTo = 16f
        shapeRotation = 0f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup.delay(0.4f)
        color = Pal.lighterOrange
        layer = Layer.effect
        y = 8f
        tri = true
        shapes = 2
        haloRadius = 15f
        haloRotation = 90f
        radius = 4f
        triLength = 0f
        triLengthTo = 4f
        shapeRotation = 180f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup.delay(0.6f)
        color = Pal.lighterOrange
        layer = Layer.effect
        y = 16f
        tri = true
        shapes = 2
        haloRadius = 12f
        haloRotation = 90f
        radius = 4f
        triLength = 0f
        triLengthTo = 12f
        shapeRotation = 0f
      }
    }, object : HaloPart() {
      init {
        progress = PartProgress.warmup.delay(0.6f)
        color = Pal.lighterOrange
        layer = Layer.effect
        y = 16f
        tri = true
        shapes = 2
        haloRadius = 12f
        haloRotation = 90f
        radius = 4f
        triLength = 0f
        triLengthTo = 4f
        shapeRotation = 180f
      }
    }) {
      val param: FloatArray = FloatArray(9)

      override fun draw(build: Building) {
        super.draw(build)

        Draw.z(Layer.effect)
        rand.setSeed(build.id.toLong())
        SglDraw.drawTransform(build.x, build.y, shootX, shootY, build.drawrot()) { ox: Float, oy: Float, _: Float ->
          for (i in 0..2) {
            val bool = rand.random(1f) > 0.5f
            for (d in 0..2) {
              param[d * 3] = rand.random(4f) / (d + 1) * (if (bool != (d % 2 == 0)) -1 else 1)
              param[d * 3 + 1] = rand.random(360f)
              param[d * 3 + 2] = rand.random(6f) / ((d + 1) * (d + 1))
            }
            val v = MathTransform.fourierSeries(Time.time, *param)

            v.add(ox, oy)
            Draw.color(Pal.lighterOrange)
            Fill.circle(v.x, v.y, 1.3f * build.warmup())
          }
        }
      }
    }
  }
  var 夏至 = object : SglTurret("summer") {
    init {
      bundle {
        desc(zh_CN, "夏至", "高速释放巨量的受控热能团,以太阳风暴摧毁一切敌人,它开火伴随着猛烈的热浪,将被击中的一切化为铁水灰烬")
      }
      requirements(
        Category.turret, ItemStack.with(
          IItems.强化合金, 210, IItems.简并态中子聚合物, 80, Items.phaseFabric, 180, IItems.铱锭, 120, IItems.气凝胶, 240, IItems.矩阵合金, 140, IItems.充能FEX水晶, 150, IItems.FEX水晶, 100
        )
      )
      size = 6
      accurateDelay = false
      accurateSpeed = false
      scaledHealth = 410f
      recoil = 2f
      recoilTime = 120f
      rotateSpeed = 2f
      shootCone = 45f
      warmupSpeed = 0.025f
      fireWarmupThreshold = 0.85f
      linearWarmup = false
      range = 500f
      targetGround = true
      targetAir = true
      shootY = 8f
      shake = 2f

      energyCapacity = 4096f
      basicPotentialEnergy = 4096f

      unitSort = UnitSorts.strongest

      shootSound = Sounds.shootReign
      shootSoundPitch = 2f

      shoot = object : ShootPattern() {
        override fun shoot(totalShots: Int, handler: BulletHandler) {
          var i = 0
          while (i < shots) {
            for (sign in Mathf.signs) {
              Tmp.v1.set(sign.toFloat(), 1f).setLength(Mathf.random(2.5f)).scl(Mathf.randomSign().toFloat())
              handler.shoot(12 * sign + Tmp.v1.x, Tmp.v1.y, (-45 * sign + Mathf.random(-20, 20)).toFloat(), i / 2f * shotDelay) { b: Bullet? ->
                if (b!!.owner is SglTurretBuild && (b.owner as SglTurretBuild).wasShooting()) {
                  b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo((b.owner as SglTurretBuild).targetPos), b.type.homingPower * Time.delta * 50f))
                }
              }
            }
            i += 2
          }
        }
      }
      shoot.shots = 12
      shoot.shotDelay = 5f

      newAmmo(object : HeatBulletType() {
        init {
          speed = 4.5f
          lifetime = 180f
          damage = 85f
          hitSize = 2f
          homingPower = 0.06f
          trailEffect = SglFx.glowParticle
          trailRotation = true
          trailChance = 0.12f
          trailColor = Pal.lightishOrange.cpy().a(0.7f)
          hitColor = Pal.lightishOrange
          shootEffect = Fx.shootSmallColor
          hitEffect = MultiEffect(
            Fx.absorb, Fx.circleColorSpark
          )
          smokeEffect = Fx.none
          despawnEffect = Fx.none
          despawnHit = false
          trailWidth = 2f
          trailLength = 26

          hitSound = Sounds.mechStep
          hitSoundPitch = 2f
          hitSoundVolume = 1.6f

          meltDownTime = 12f
          melDamageScl = 0.3f
          maxExDamage = 120f
        }

        override fun draw(b: Bullet) {
          super.draw(b)
          Draw.z(Layer.bullet)
          Draw.color(Pal.lighterOrange)
          val fout = b.fout(Interp.pow4Out)
          val z = Draw.z()
          Draw.z(z - 0.0001f)
          b.trail.draw(trailColor, trailWidth * fout)
          Draw.z(z)

          SglDraw.drawLightEdge(b.x, b.y, 35 * fout + Mathf.absin(0.5f, 3.5f), 2f, 14 * fout + Mathf.absin(0.4f, 2.5f), 2f, 30f, Pal.lightishOrange)
          SglDraw.drawDiamond(b.x, b.y, 16 * fout + Mathf.absin(0.6f, 2f), 2f, 90f, Pal.lightishOrange)
          Fill.circle(b.x, b.y, 2.2f * fout)
        }

        override fun drawTrail(b: Bullet?) {}

        override fun removed(b: Bullet?) {}
      })
      consume!!.energy(5f)
      consume!!.time(60f)

      draw = DrawSglTurret(object : RegionPart("_side") {
        init {
          mirror = true
          moveX = 4f
          progress = PartProgress.warmup
          heatColor = Pal.lightishOrange
          heatProgress = PartProgress.warmup.delay(0.25f)
        }
      }, object : RegionPart("_bot") {
        init {
          mirror = true
          moveY = -4f
          moveX = 2f
          progress = PartProgress.warmup
          heatColor = Pal.lightishOrange
          heatProgress = PartProgress.warmup.delay(0.25f)
        }
      }, object : RegionPart("_body") {
        init {
          progress = PartProgress.recoil
          heatProgress = PartProgress.warmup.delay(0.25f)
          heatColor = Pal.lightishOrange
          moveY = -4f
        }
      }, object : RegionPart("_blade") {
        init {
          mirror = true
          moveX = 2f
          moveY = 8f
          moveRot = -45f
          progress = PartProgress.warmup
          heatColor = Pal.lightishOrange
          heatProgress = PartProgress.warmup.delay(0.25f)
        }
      }, object : ShapePart() {
        init {
          color = Pal.lighterOrange
          circle = true
          hollow = true
          stroke = 0f
          strokeTo = 2f
          y = -18f
          radius = 0f
          radiusTo = 12f
          progress = PartProgress.warmup
          layer = Layer.effect
        }
      }, object : ShapePart() {
        init {
          circle = true
          y = -18f
          radius = 0f
          radiusTo = 3.5f
          color = Pal.lighterOrange
          layer = Layer.effect
          progress = PartProgress.warmup
        }
      }, object : HaloPart() {
        init {
          progress = PartProgress.warmup
          color = Pal.lighterOrange
          layer = Layer.effect
          y = -18f
          haloRotation = 90f
          shapes = 2
          triLength = 0f
          triLengthTo = 32f
          haloRadius = 0f
          haloRadiusTo = 12f
          tri = true
          radius = 2f
          radiusTo = 5f
        }
      }, object : HaloPart() {
        init {
          progress = PartProgress.warmup
          color = Pal.lighterOrange
          layer = Layer.effect
          y = -18f
          haloRotation = 90f
          shapes = 2
          triLength = 0f
          triLengthTo = 8f
          haloRadius = 0f
          haloRadiusTo = 12f
          tri = true
          radius = 2f
          radiusTo = 5f
          shapeRotation = 180f
        }
      }, object : HaloPart() {
        init {
          progress = PartProgress.warmup
          color = Pal.lighterOrange
          layer = Layer.effect
          y = -18f
          haloRotation = 0f
          haloRotateSpeed = 1f
          shapes = 2
          triLength = 0f
          triLengthTo = 10f
          haloRadius = 16f
          tri = true
          radius = 6f
        }
      }, object : HaloPart() {
        init {
          progress = PartProgress.warmup
          color = Pal.lighterOrange
          layer = Layer.effect
          y = -18f
          haloRotation = 0f
          haloRotateSpeed = 1f
          shapes = 2
          triLength = 0f
          triLengthTo = 6f
          haloRadius = 16f
          tri = true
          radius = 6f
          shapeRotation = 180f
        }
      }, object : HaloPart() {
        init {
          progress = PartProgress.warmup
          color = Pal.lighterOrange
          layer = Layer.effect
          y = -18f
          haloRotation = 0f
          haloRotateSpeed = -1f
          shapes = 4
          triLength = 0f
          triLengthTo = 4f
          haloRadius = 12f
          tri = true
          radius = 5f
        }
      }, object : HaloPart() {
        init {
          progress = PartProgress.warmup
          color = Pal.lighterOrange
          layer = Layer.effect
          y = -18f
          haloRotation = 0f
          haloRotateSpeed = -1f
          shapes = 4
          triLength = 0f
          triLengthTo = 6f
          haloRadius = 12f
          tri = true
          radius = 5f
          shapeRotation = 180f
        }
      })
    }
  }
}