package ice.content.block.turret

import ice.content.IItems
import ice.content.ILiquids
import ice.content.IStatus
import ice.ui.bundle.BaseBundle
import ice.ui.bundle.desc
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirementPairs
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.content.StatusEffects
import mindustry.entities.bullet.LiquidBulletType
import mindustry.entities.pattern.ShootMulti
import mindustry.entities.pattern.ShootPattern
import mindustry.entities.pattern.ShootSpread
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.blocks.defense.turrets.LiquidTurret

class Quicksand :LiquidTurret("turret_quicksand") {
  init {
    desc(BaseBundle.zh_CN, "泉涌", "使用增压器高速喷射液体攻击敌人,使用水作为弹药时可以灭火")
    health = 2430
    size = 3
    recoil = 2f
    shootY = 4f
    reload = 35f
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

    ammo(Liquids.water, LiquidBulletType().apply {
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
      shootEffect = Fx.hitLiquid
    },ILiquids.温热孢液, LiquidBulletType().apply {
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
    },ILiquids.急冻液, LiquidBulletType().apply {
      damage = 0.4f
      lifetime = 60f
      speed = 5f
      orbSize = 4f
      puddleSize = 8f
      knockback = 1.5f
      liquid = Liquids.cryofluid
      status = StatusEffects.freezing
      statusDuration = 240f
      ammoMultiplier = 0.4f
      shootEffect = Fx.shootLiquid
    },ILiquids.废水, LiquidBulletType().apply {
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
    })

  }
}