package ice.content.block.turret

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Interp
import ice.audio.ISounds
import ice.content.IItems
import ice.content.IStatus
import ice.core.IFiles.appendModName
import ice.entities.bullet.base.BasicBulletType
import ice.entities.bullet.base.BulletType
import ice.entities.effect.MultiEffect
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.Effect
import mindustry.entities.Effect.EffectContainer
import mindustry.entities.bullet.PointBulletType
import mindustry.entities.effect.ExplosionEffect
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.pattern.ShootAlternate
import mindustry.gen.Sounds
import mindustry.graphics.Drawf
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.Liquid
import singularity.world.blocks.turrets.SglTurret

class 血雨 :SglTurret("turret_bloodyRain") {
  init {
    localization {
      zh_CN {
        localizedName = "血雨"
        description = " 改进型双联速射炮,向敌人发射大型穿甲弹,兼容各种弹药\n其恐怖的穿透力足以击穿建筑装甲"
      }
    }
    health = 5400
    size = 5
    armor = 4f
    range = 340f
    shake = 3f
    recoil = 4f
    recoilTime = 15f
    cooldownTime = 60f
    inaccuracy = 1.5f
    shootCone = 20f
    rotateSpeed = 4.5f
    shootSound = Sounds.shootBreach
    ammoUseEffect = Fx.casing4
    liquidCapacity = 30f
    shoot = ShootAlternate().apply {
      barrels = 2
      spread = 15f
    }

    requirements(
      Category.turret, IItems.铜锭, 1650, IItems.铬锭, 750, IItems.钍锭, 675, IItems.钴钢, 475, IItems.暮光合金, 325
    )
    setAmmo()
    newCoolant(1f, 0.4f, { l: Liquid? -> l!!.heatCapacity >= 0.4f && l.temperature <= 0.5f }, 0.25f, 20f)
  }

  fun setAmmo() {
    addAmmoType(IItems.铬锭) {
      BasicBulletType().apply {
        damage = 85f
        lifetime = 33.6f
        speed = 10f
        width = 16f
        height = 20f
        pierceCap = 2
        pierceBuilding = true
        knockback = 0.5f
        reloadMultiplier = 1.9f
        ammoMultiplier = 5f
        shootEffect = Fx.shootBig
        smokeEffect = Fx.shootBigSmoke
      }
    }
    addAmmoType(IItems.钍锭) {
      BasicBulletType().apply {
        damage = 140f
        lifetime = 36f
        speed = 11f
        width = 18f
        height = 23f
        pierceCap = 4
        pierceBuilding = true
        knockback = 1.2f
        ammoMultiplier = 3f
        status = IStatus.衰变
        shootEffect = Fx.shootBig
        smokeEffect = Fx.shootBigSmoke
      }
    }
    addAmmoType(IItems.铱板) {
      BasicBulletType().apply {
        damage = 100f
        lifetime = 36f
        speed = 11f
        width = 18f
        height = 23f
        pierceCap = 2
        pierceBuilding = true
        knockback = 1.1f
        ammoMultiplier = 4f
        status = IStatus.破甲I
        splashDamage = 35f
        splashDamageRadius = 20f
        shootEffect = Fx.shootBig
        smokeEffect = Fx.shootBigSmoke
        hitEffect = Fx.flakExplosionBig
        despawnEffect = Fx.flakExplosionBig
      }
    }
    addAmmoType(IItems.生物钢) {
      BasicBulletType().apply {
        damage = 115f
        lifetime = 37f
        speed = 11f
        width = 16f
        height = 20f
        pierceCap = 2
        pierceBuilding = true
        knockback = 1.5f
        rangeChange = 67f
        ammoMultiplier = 5f
        status = IStatus.熔融
        statusDuration = 120f
        frontColor = Color.valueOf("FFDCD8")
        backColor = Color.valueOf("FF5845")
        shootEffect = Fx.shootBig
        smokeEffect = Fx.shootBigSmoke
        fragVelocityMin = 1f
        fragBullets = 1
        fragBullet = PointBulletType().apply {
          trailEffect = Fx.none
          damage = 0f
          speed = 24f
          lifetime = 5f
          hitEffect = Fx.none
          hitSound = ISounds.激射
          despawnEffect = WaveEffect().apply {
            lifetime = 10f
            sizeFrom = 0f
            sizeTo = 20f
            strokeFrom = 3f
            strokeTo = 0f
            colorFrom = Color.valueOf("D86E56")
            colorTo = Color.valueOf("D86E56")
          }
          fragBullets = 1
          fragAngle = 180f
          fragVelocityMin = 1f
          fragRandomSpread = 0f
          fragBullet = BulletType().apply {
            lifetime = 25f
            damage = 115f
            speed = 8f
            width = 8f
            height = 10f
            pierce = true
            pierceBuilding = true
            status = IStatus.熔融
            statusDuration = 60f
            homingPower = 0.12f
            homingRange = 40f
            splashDamage = 45f
            splashDamageRadius = 32f
            trailChance = -1f
            trailWidth = 1f
            trailLength = 400
            trailColor = Color.valueOf("D86E56")
            frontColor = Color.valueOf("D86E56")
            backColor = Color.valueOf("D86E56")
            hitEffect = MultiEffect().apply {
              lifetime = 120f
              effects = arrayOf(ParticleEffect().apply {
                particles = 6
                sizeFrom = 3f
                sizeTo = 0f
                length = 60f
                baseLength = 8f
                lifetime = 9f
                colorFrom = Color.valueOf("D75B6EFF")
                colorTo = Color.valueOf("D86E56")
                cone = 20f
              }, WaveEffect().apply {
                lifetime = 10f
                sizeFrom = 8f
                sizeTo = 50f
                strokeFrom = 2f
                strokeTo = 0f
                colorFrom = Color.valueOf("D75B6EFF")
                colorTo = Color.valueOf("D86E56")
              })
            }
          }
        }

      }
    }
    addAmmoType(IItems.暮光合金) {
      BasicBulletType().apply {
        damage = 50f
        lifetime = 30f
        speed = 12f
        width = 16f
        height = 20f
        knockback = 0.9f
        rangeChange = 20f
        ammoMultiplier = 3f
        status = StatusEffects.shocked
        statusDuration = 120f
        splashDamage = 135f
        splashDamageRadius = 40f
        val instBomb = Effect(15f, 100f) { e: EffectContainer? ->
          Draw.color(IItems.暮光合金.color)
          Lines.stroke(e!!.fout() * 4f)
          Lines.circle(e.x, e.y, 4f + e.finpow() * 20f)

          for(i in 0..3) {
            Drawf.tri(e.x, e.y, 6f, 80f * e.fout(), (i * 90 + 45).toFloat())
          }

          Draw.color()
          for(i in 0..3) {
            Drawf.tri(e.x, e.y, 3f, 30f * e.fout(), (i * 90 + 45).toFloat())
          }
          Drawf.light(e.x, e.y, 150f, IItems.暮光合金.color, 0.9f * e.fout())
        }
        hitEffect = instBomb
        hitSound = Sounds.explosionPlasmaSmall
        despawnEffect = instBomb
      }
    }
    addAmmoType(IItems.铈凝块) {
      BasicBulletType().apply {
        damage = 88f
        lifetime = 33.6f
        speed = 10f
        width = 16f
        height = 20f
        pierceCap = 2
        pierceBuilding = true
        knockback = 0.9f
        ammoMultiplier = 4f
        status = IStatus.蚀骨
        statusDuration = 150f
        splashDamage = 25f
        splashDamageRadius = 35f
        incendChance = 1f
        incendSpread = 16f
        incendAmount = 3
        backColor = Color.valueOf("FF8C00")
        frontColor = Color.valueOf("FFB90F")
        shootEffect = Fx.shootBig
        smokeEffect = Fx.shootBigSmoke
        hitEffect = Fx.flakExplosionBig
        despawnEffect = Fx.flakExplosionBig
      }
    }
    addAmmoType(IItems.低温化合物) {
      BasicBulletType().apply {
        damage = 206f
        lifetime = 43f
        speed = 8f
        width = 18f
        height = 23f
        backColor = Color.valueOf("87CEEB")
        frontColor = Color.valueOf("C0ECFF")
        ammoMultiplier = 2f
        reloadMultiplier = 0.6f
        shootEffect = ParticleEffect().apply {
          line = true
          particles = 12
          lifetime = 20f
          length = 45f
          cone = 30f
          lenFrom = 6f
          lenTo = 5f
          strokeFrom = 3f
          interp = Interp.fastSlow
          colorFrom = Color.valueOf("C0ECFF")
          colorTo = Color.valueOf("87CEEB")
        }
        smokeEffect = Fx.shootBigSmoke
        status = IStatus.封冻
        statusDuration = 150f
        hitEffect = ParticleEffect().apply {
          region = "crystal".appendModName()
          particles = 3
          lifetime = 36f
          length = 60f
          sizeFrom = 4f
          cone = 30f
          interp = Interp.pow5Out
          sizeInterp = Interp.pow2In
          colorFrom = Color.valueOf("87CEEB")
          colorTo = Color.valueOf("C0ECFF")
        }
        despawnEffect = ExplosionEffect().apply {
          sparkColor = Color.valueOf("87CEEB")
          smokeColor = Color.valueOf("6D90BC")
          waveColor = Color.valueOf("C0ECFF")
          waveStroke = 7f
          waveRad = 30f
          waveLife = 15f
          sparks = 5
          sparkRad = 27f
          sparkLen = 8f
          sparkStroke = 4f
        }

      }
    }
  }

  fun addAmmoType(item: Item, bulletType: () -> BasicBulletType) {
    val ammoTypes = bulletType.invoke()
    newAmmo(ammoTypes).setReloadAmount(ammoTypes.ammoMultiplier.toInt())
    consume?.apply {
      time(6f)
      item(item, 1)
    }
  }
}