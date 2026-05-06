package ice.content.block.turret

import ice.content.IItems
import ice.content.ILiquids
import ice.content.IStatus
import ice.entities.bullet.LiquidBulletType
import ice.entities.bullet.base.BulletType
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.content.StatusEffects
import mindustry.entities.pattern.ShootMulti
import mindustry.entities.pattern.ShootPattern
import mindustry.entities.pattern.ShootSpread
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.Liquid
import singularity.world.blocks.turrets.SglTurret

class Quicksand :SglTurret("turret_quicksand") {
  init {
    localization {
      zh_CN {
        localizedName = "泉涌"
        description = "使用增压器高速喷射液体攻击敌人,使用水作为弹药时可以灭火"
      }
    }
    health = 2430
    size = 3
    recoil = 2f
    shootY = 4f
    range = 264f
    shoot = ShootMulti().apply {
      source = ShootPattern().apply {
        shots = 3
        shotDelay = 5f
      }
      dest = arrayOf(
        ShootSpread().apply {
          shots = 40
          spread = 0.5f
        })
    }
    velocityRnd = 0.1f
    inaccuracy = 15f
    shootCone = 45f
    canOverdrive = false
    loopSound = Sounds.none
    shootSound = Sounds.shootCleroi
    requirementPairs(
      Category.turret,

      IItems.铅锭 to 455,

      IItems.石英玻璃 to 115,

      IItems.铬锭 to 275,

      IItems.锌锭 to 45
    )

    setAmmo()
    limitRange()
  }

  fun setAmmo() {
    addAmmoType(Liquids.water) {
      LiquidBulletType().apply {
        damage = 0.4f
        lifetime = 60f
        speed = 5f
        layer = 98f
        orbSize = 4f
        puddleSize = 8f
        knockback = 2f
        liquid = Liquids.water
        status = StatusEffects.wet
        statusDuration = 240f
        ammoMultiplier = 0.4f
        shootEffect = Fx.shootLiquid
      }
    }
    addAmmoType(ILiquids.温热孢液) {
      LiquidBulletType().apply {
        damage = 5f
        lifetime = 60f
        speed = 5f
        orbSize = 4f
        puddleSize = 8f
        knockback = 1.5f
        liquid = Liquids.slag
        status = StatusEffects.melting
        statusDuration = 240f
        ammoMultiplier = 0.4f
        shootEffect = Fx.shootLiquid
      }
    }
    addAmmoType(ILiquids.急冻液) {
      LiquidBulletType().apply {
        damage = 0.4f
        lifetime = 60f
        speed = 5f
        orbSize = 4f
        puddleSize = 8f
        knockback = 1.5f
        liquid = ILiquids.急冻液
        status = IStatus.封冻
        statusDuration = 240f
        ammoMultiplier = 0.4f
        shootEffect = Fx.shootLiquid
      }
    }
    addAmmoType(ILiquids.废水) {
      LiquidBulletType().apply {
        damage = 6f
        lifetime = 60f
        speed = 5f
        layer = 98f
        orbSize = 4f
        puddleSize = 8f
        knockback = 2.2f
        liquid = ILiquids.废水
        status = IStatus.辐射
        statusDuration = 240f
        ammoMultiplier = 0.5f
        splashDamage = 9f
        splashDamageRadius = 7f
        shootEffect = Fx.shootLiquid
      }
    }
  }

  fun addAmmoType(liquid: Liquid, bulletType: () -> BulletType) {
    val ammoTypes = bulletType.invoke()
    newAmmo(ammoTypes).setReloadAmount(ammoTypes.ammoMultiplier.toInt())
    consume?.apply {
      liquids(liquid, 47f/60f)
      time(35f)
    }
  }
}