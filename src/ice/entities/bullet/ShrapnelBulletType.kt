package ice.entities.bullet

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.math.Angles
import arc.math.Mathf
import arc.util.Tmp
import ice.entities.bullet.base.BulletType
import mindustry.content.Fx
import mindustry.entities.Damage
import mindustry.gen.Bullet
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import kotlin.math.max

class ShrapnelBulletType :BulletType() {
  var length: Float = 100f
  var width: Float = 20f
  var fromColor: Color = Color.white
  var toColor: Color = Pal.lancerLaser
  var hitLarge: Boolean = false

  var serrations: Int = 7
  var serrationLenScl: Float = 10f
  var serrationWidth: Float = 4f
  var serrationSpacing: Float = 8f
  var serrationSpaceOffset: Float = 80f
  var serrationFadeOffset: Float = 0.5f

  init {
    speed = 0f
    hitEffect = Fx.hitLancer
    smokeEffect = Fx.lightningShoot
    shootEffect = smokeEffect
    lifetime = 10f
    despawnEffect = Fx.none
    keepVelocity = false
    collides = false
    pierce = true
    hittable = false
    absorbable = false
    lightOpacity = 0.6f
  }

  override fun init(b: Bullet) {
    super.init(b)

    Damage.collideLaser(b, length, hitLarge, laserAbsorb, pierceCap)
  }

  override fun init() {
    super.init()

    drawSize = max(drawSize, length * 2f)
  }

  override fun calculateRange(): Float {
    return max(length, maxRange)
  }

  override fun draw(b: Bullet) {
    val realLength = b.fdata
    val rot = b.rotation()

    Draw.color(fromColor, toColor, b.fin())
    for(i in 0..<(serrations * realLength / length).toInt()) {
      Tmp.v1.trns(rot, i * serrationSpacing)
      val sl = Mathf.clamp(b.fout() - serrationFadeOffset) * (serrationSpaceOffset - i * serrationLenScl)
      Drawf.tri(b.x + Tmp.v1.x, b.y + Tmp.v1.y, serrationWidth, sl, b.rotation() + 90)
      Drawf.tri(b.x + Tmp.v1.x, b.y + Tmp.v1.y, serrationWidth, sl, b.rotation() - 90)
    }
    Drawf.tri(b.x, b.y, width * b.fout(), (realLength + 4f), b.rotation())
    Drawf.tri(b.x, b.y, width * b.fout(), 10f, b.rotation() + 180f)
    Draw.reset()

    Drawf.light(
      b.x,
      b.y,
      b.x + Angles.trnsx(rot, realLength),
      b.y + Angles.trnsy(rot, realLength),
      width * 2.5f * b.fout(),
      toColor,
      lightOpacity
    )
  }
}
