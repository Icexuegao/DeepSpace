package ice.content.unit.mech

import arc.graphics.Color
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import ice.world.content.unit.ability.ArmorPlateAbility
import mindustry.content.Fx
import mindustry.entities.abilities.RegenAbility
import mindustry.entities.bullet.ShrapnelBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.gen.MechUnit
import mindustry.gen.Sounds

class Enclosure: IceUnitType("unit_enclosure", MechUnit::class.java) {
  init {
    BaseBundle.Companion.bundle {
      desc(zh_CN,"围护","快速发射散射激光打击敌人,对近距离目标尤为有效")
    }
    health = 640f
    armor = 5f
    hitSize = 12f
    speed = 0.8f
    rotateSpeed = 4f
    lowAltitude = true
    canBoost = true
    boostMultiplier = 2f
    engineOffset = 5.5f
    engineSize = 3f
    mechLandShake = 2f
    riseSpeed = 0.05f
    mechFrontSway = 0.55f
    abilities.add(ArmorPlateAbility().apply {
      healthMultiplier = 0.2f
    }, RegenAbility().apply {
      percentAmount = 0.0125f
    })
    setWeapon("weapon1") {
      x = 7.75f
      y = 1f
      shootY+=2
      shake = 1f
      top = false
      shoot.apply {
        shots = 8
      }
      inaccuracy = 30f
      reload = 50f
      recoil = 2f
      ejectEffect = Fx.casing2
      shootSound = Sounds.shoot
      bullet = ShrapnelBulletType().apply {
        damage = 43f
        lifetime = 15f
        speed = 0f
        length = 60f
        width = 6f
        recoil = 0.25f
        fromColor = Color.valueOf("FFDCD8")
        toColor = Color.valueOf("FF5845")
        serrations = 1
        shootEffect = ParticleEffect().apply {
          particles = 2
          lifetime = 20f
          line = true
          strokeFrom = 3f
          strokeTo = 0f
          lenFrom = 8f
          lenTo = 0f
          cone = 25f
          length = 30f
          colorFrom = Color.valueOf("FF5845")
          colorTo = Color.valueOf("FFDCD8")
        }
        smokeEffect = Fx.none
      }
    }

  }
}