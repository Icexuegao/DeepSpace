package ice.entities.bullet

import arc.Core
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Position
import arc.math.geom.Vec2
import arc.util.Tmp
import ice.entities.bullet.base.BulletType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.Damage
import mindustry.gen.Building
import mindustry.gen.Bullet
import mindustry.gen.Healthc
import mindustry.gen.Hitboxc
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import kotlin.math.max
import kotlin.math.min

class SapBulletType :BulletType() {
  var length: Float = 100f
  var lengthRand: Float = 0f
  var sapStrength: Float = 0.5f
  var color: Color = Color.white.cpy()
  var width: Float = 0.4f
  var sprite: String = "laser"

  var laserRegion: TextureRegion? = null
  var laserEndRegion: TextureRegion? = null

  override fun load() {
    super.load()

    laserRegion = Core.atlas.find(sprite)
    laserEndRegion = Core.atlas.find(sprite + "-end")
  }

  init {
    speed = 0f
    despawnEffect = Fx.none
    pierce = true
    collides = false
    hitSize = 0f
    hittable = false
    hitEffect = Fx.hitLiquid
    status = StatusEffects.sapped
    lightColor = Pal.sap
    lightOpacity = 0.6f
    statusDuration = 60f * 3f
    impact = true
  }

  override fun draw(b: Bullet) {
    if (b.data is Position) {
      Tmp.v1.set(b.data as Position?).lerp(b, b.fin())

      Draw.color(color)
      Drawf.laser(
        laserRegion, laserEndRegion,
        b.x, b.y, Tmp.v1.x, Tmp.v1.y, width * b.fout()
      )

      Draw.reset()

      Drawf.light(b.x, b.y, Tmp.v1.x, Tmp.v1.y, 15f * b.fout(), lightColor, lightOpacity)
    }
  }

  override fun drawLight(b: Bullet?) {
  }

  override fun calculateRange(): Float {
    return max(length, maxRange)
  }

  override fun init(b: Bullet) {
    super.init(b)

    val len = Mathf.random(length, length + lengthRand)

    val target = Damage.linecast(b, b.x, b.y, b.rotation(), len)
    b.data = target

    if (target != null) {
      val result = max(min(target.health(), damage), 0f)

      if (b.owner is Healthc) {
        (b.owner as Healthc).heal(result * sapStrength)
      }
    }

    if (target is Hitboxc) {
      target.collision(b, target.x(), target.y())
      b.collision(target, target.x(), target.y())
    } else if (target is Building) {
      if (target.collide(b)) {
        target.collision(b)
        hit(b, target.x, target.y)
      }
    } else {
      b.data = Vec2().trns(b.rotation(), len).add(b.x, b.y)
    }
  }
}