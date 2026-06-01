package ice.content.block.turret

import arc.graphics.Color
import ice.content.IItems
import ice.content.IStatus
import ice.entities.bullet.ShrapnelBulletType
import mindustry.content.Fx
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.pattern.ShootSpread
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.Liquid
import singularity.world.blocks.turrets.SglTurret

class 极光 :SglTurret("turret_aurora") {
  init {
    localization {
      zh_CN {
        localizedName = "极光"
        description = "向附近的敌人发射九发近距离穿透性镭射激光束"
      }
    }

    health = 4320
    armor = 11f
    size = 4
    range = 168f
    recoil = 5f
    shake = 4f
    shootCone = 30f
    rotateSpeed = 6f
    recoilTime = 42f
    cooldownTime = 50f
    liquidCapacity = 30f
    shootSound = Sounds.shootScatter
    shoot = ShootSpread().apply {
      shots = 9
      spread = 2f
    }

    newCoolant(1f, 0.4f, { l: Liquid? -> l!!.heatCapacity >= 0.4f && l.temperature <= 0.5f }, 0.25f, 20f)

    requirements(
      Category.turret,
      IItems.铜锭, 1200,
      IItems.铬锭, 325,
      IItems.钍锭, 275,
      IItems.钴钢, 175,
      IItems.铪锭, 75
    )

    setAmmo()
    limitRange()
  }

  fun setAmmo() {
    addAmmoType(IItems.铬锭) {
      ShrapnelBulletType().apply {
        damage = 90f
        lifetime = 15f
        speed = 0f
        width = 17f
        length = 168f
        ammoMultiplier = 3f
        reloadMultiplier = 1.3f
      }
    }

    addAmmoType(IItems.钍锭) {
      ShrapnelBulletType().apply {
        damage = 135f
        lifetime = 15f
        speed = 0f
        width = 17f
        length = 168f
        ammoMultiplier = 4f
        fromColor = Color.valueOf("F9A3C7")
        toColor = Color.valueOf("CB8EBF")
        status = IStatus.衰变
        statusDuration = 60f
        shootEffect = Fx.thoriumShoot
        smokeEffect = Fx.none
      }
    }

    addAmmoType(IItems.铪锭) {
      ShrapnelBulletType().apply {
        damage = 180f
        lifetime = 15f
        speed = 0f
        width = 17f
        length = 168f
        fromColor = Color.valueOf("FCF387")
        toColor = Color.valueOf("E8D174")
        rangeChange = 24f
        ammoMultiplier = 3f

        shootEffect = ParticleEffect().apply {
          particles = 6
          lifetime = 20f
          line = true
          strokeFrom = 4f
          strokeTo = 0f
          lenFrom = 8f
          lenTo = 8f
          cone = 30f
          length = 35f
          lightColor = Color.valueOf("F3E979")
          colorFrom = Color.valueOf("FCF387")
          colorTo = Color.valueOf("E8D174")
        }

        smokeEffect = Fx.none
      }
    }
  }

  fun addAmmoType(item: Item, bulletType: () -> ShrapnelBulletType) {
    val ammoTypes = bulletType.invoke()
    newAmmo(ammoTypes).setReloadAmount(ammoTypes.ammoMultiplier.toInt())
    consume?.apply {
      time(35f)
      item(item, 1)
    }
  }
}
