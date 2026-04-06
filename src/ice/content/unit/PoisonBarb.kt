package ice.content.unit

import ice.entities.bullet.RailBulletType
import ice.library.util.toColor
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.UnitSpawnAbility
import mindustry.content.Fx
import mindustry.gen.Sounds
import mindustry.graphics.Pal
import mindustry.world.meta.BlockFlag

class PoisonBarb : IceUnitType("poisonBarb") {
  init {
    bundle {
      desc(zh_CN, "毒刺", "轻型空中突击单位.发射穿透性激光攻击敌人,每隔一段时间会克隆自身","帝国科技的终极产物,几乎可以无限制地自我增殖")
    }
    lowAltitude = true
    flying = true
    health = 1270f
    hitSize = 16f
    armor = 4f
    speed = 4f
    drag = 0.04f
    accel = 0.08f
    rotateSpeed = 7.5f
    engineOffset = 11f
    engineSize = 2.5f
    trailLength = 8
    outlineColor = "1F1F1F".toColor()
    targetFlags = arrayOf(BlockFlag.reactor, BlockFlag.generator, BlockFlag.factory)
    abilities.add(UnitSpawnAbility(this, 1800f).apply {
      color = Pal.remove
      alpha = 0.4f
    })
    setWeapon("weapon") {
      x = 0f
      shootY = 8f
      reload = 180f
      mirror = false
      shootCone = 5f
      cooldownTime = 120f
      shootSound = Sounds.shootRetusa
      bullet = RailBulletType().apply {
        damage = 325f
        length = 160f
        recoil = 1f
        pointEffectSpace = 36f
        pointEffect = Fx.railTrail
        shootEffect = Fx.railShoot
        hitEffect = Fx.railHit
        pierceDamageFactor = 0f
        buildingDamageMultiplier = 0.2f
      }
    }
  }
}