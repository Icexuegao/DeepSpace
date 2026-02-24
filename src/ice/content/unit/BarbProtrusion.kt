package ice.content.unit

import arc.math.geom.Rect
import ice.entities.bullet.base.BasicBulletType
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.unit.IceUnitType
import ice.world.meta.IceEffects
import mindustry.graphics.Pal

class BarbProtrusion : IceUnitType("barbProtrusion") {
  init {
    bundle {
      desc(zh_CN, "突刺","轻型地面突击单位.发射炮弹攻击敌人")
    }
    armor = 8f
    speed = 0.7f
    health = 700f
    hitSize = 14f
    rotateSpeed = 3.3f
    squareShape = true
    omniMovement = false
    rotateMoveFirst = true
    treadRects = arrayOf(Rect(11f - (64 / 2), 5f - (64 / 2), 16f, 53f))
    setWeapon("weapon") {
      x = 0f
      shootY += 2f
      reload = 60f
      mirror = false
      rotate = true
      rotateSpeed = 3f
      bullet = BasicBulletType(4f, 80f).apply {
        height = 8f
        width = 4f
        drag = 0f
        trailColor = Pal.accent
        trailWidth = 1.7f
        trailLength = 4
        lifetime = 40f
        shootEffect = IceEffects.baseShootEffect(Pal.accent)
      }
    }
  }
}