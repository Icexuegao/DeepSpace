package ice.content.unit.mech

import arc.graphics.Color
import ice.entities.bullet.base.BasicBulletType
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.ArmorPlateAbility
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.abilities.RegenAbility
import mindustry.entities.abilities.StatusFieldAbility
import mindustry.entities.effect.WaveEffect
import mindustry.gen.MechUnit
import mindustry.gen.Sounds

class StrongShield : IceUnitType("unit_strongShield", MechUnit::class.java) {

  init {
    bundle {
      desc(zh_CN, "坚盾", "轻型地面突击单位.发射标准子弹攻击敌人,会超频在附近的友军.会缓慢恢复生命值,并在开火时减少所受伤害")
    }
    health = 240f
    armor = 2f
    hitSize = 8f
    speed = 0.6f
    rotateSpeed = 4f
    lowAltitude = true
    canBoost = true
    boostMultiplier = 2f
    engineOffset = 4.5f
    engineSize = 2f
    mechLandShake = 1f
    riseSpeed = 0.05f
    mechFrontSway = 0.55f
    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 0.1f
    }, RegenAbility().apply {
      percentAmount = 0.0125f
    })
    abilities.add(StatusFieldAbility(StatusEffects.overclock, 250f, 300f, 30f).apply {
      activeEffect = WaveEffect().apply {
        lifetime = 10f
        sizeFrom = 8f
        sizeTo = 40f
        strokeFrom = 2f
        strokeTo = 0f
        colorFrom = Color.valueOf("FFFFFF")
        colorTo = Color.valueOf("FFFFFF")
      }
    })
    setWeapon("weapon1") {
      x = 5.25f
      y = 0.75f
      top = false
      shoot.apply {
        shots = 2
        shotDelay = 5f
      }
      recoil = 1f
      shake = 0.5f
      reload = 25f
      ejectEffect = Fx.casing1
      shootSound = Sounds.shoot
      bullet = BasicBulletType(6f, 19f).apply {
        lifetime = 32f
      }
    }
  }
}
