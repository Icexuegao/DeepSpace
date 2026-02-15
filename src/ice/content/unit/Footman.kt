package ice.content.unit

import ice.entities.bullet.base.BulletType
import ice.graphics.IceColor
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import ice.world.meta.IceEffects
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.content.UnitTypes
import mindustry.gen.Sounds

class Footman : IceUnitType("footman") {init {
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
}