package ice.content.unit.flying.rain

import arc.graphics.Color
import ice.entities.bullet.base.BasicBulletType
import ice.entities.effect.MultiEffect
import ice.ui.bundle.BaseBundle
import ice.world.content.unit.IceUnitType
import mindustry.content.Fx
import mindustry.entities.bullet.LightningBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.entities.pattern.ShootHelix
import mindustry.gen.Sounds

class Storm : IceUnitType("unit_storm") {
  init {
    BaseBundle.bundle {
      desc(zh_CN, "暴雨", "轻型驱逐舰,配备了双联球状闪电发生器,杀伤力更强")
    }
    lowAltitude = true
    flying = true
    health = 1170f
    hitSize = 21f
    armor = 7f
    speed = 1.2f
    engineSize = 3.6f
    engineOffset = 12.5f

    setWeapon("空雷3炮") {
      x = 0f
      y = 0f
      recoil = 0f
      shootY = 6f
      reload = 115f
      mirror = false
      shoot = ShootHelix().apply {
        mag = 1f
        scl = 5f
      }
      shootSound = Sounds.shootBeamPlasma
      bullet = BasicBulletType().apply {
        damage = 125f
        splashDamage = 75f
        splashDamageRadius = 32f
        speed = 1.5f
        lifetime = 120f
        knockback = 1f
        shrinkY = 0f
        width = 8f
        height = 8f
        hitEffect = Fx.none
        despawnEffect = MultiEffect(ParticleEffect().apply {
          particles = 13
          sizeFrom = 4f
          sizeTo = 0f
          length = 42.5f
          baseLength = 8f
          lifetime = 25f
          colorFrom = Color.valueOf("F3E979")
          colorTo = Color.valueOf("FEEBB3")
          cone = 40f
        }, WaveEffect().apply {
          lifetime = 18f
          sizeFrom = 1f
          sizeTo = 45f
          strokeFrom = 3f
          strokeTo = 0f
          colorFrom = Color.valueOf("F3E979")
          colorTo = Color.valueOf("FEEBB3")
        })
        hitSound = Sounds.explosionPlasmaSmall
        sprite = "circle-bullet"
        trailLength = 11
        trailWidth = 3f
        trailColor = Color.valueOf("F3E979")
        frontColor = Color.valueOf("FEEBB3")
        backColor = Color.valueOf("F3E979")
        pierce = true
        absorbable = false
        bulletInterval = 2.5f
        intervalBullets = 1
        intervalBullet = LightningBulletType().apply {
          damage = 8f
          lightningColor = Color.valueOf("FEEBB3FA0")
          hitColor = Color.valueOf("FEEBB3A0")
          lightningLength = 5
          lightningLengthRand = 3
        }
      }
    }
  }
}