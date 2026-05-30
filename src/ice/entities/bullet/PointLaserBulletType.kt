package ice.entities.bullet

import arc.Core
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.util.Time
import mindustry.Vars
import mindustry.content.Fx
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.gen.Bullet
import mindustry.graphics.Drawf
import mindustry.graphics.Trail

open class PointLaserBulletType :ice.entities.bullet.base.BulletType() {
  var sprite: String = "point-laser"
  var laser: TextureRegion? = null
  var laserEnd: TextureRegion? = null

  var color: Color = Color.white

  var beamEffect: Effect = Fx.colorTrail
  var beamEffectInterval: Float = 3f
  var beamEffectSize: Float = 3.5f

  var oscScl: Float = 2f
  var oscMag: Float = 0.3f
  var damageInterval: Float = 5f

  var shake: Float = 0f

  init {
    removeAfterPierce = false
    speed = 0f
    despawnEffect = Fx.none
    lifetime = 20f
    impact = true
    keepVelocity = false
    collides = false
    pierce = true
    hittable = false
    absorbable = false
    optimalLifeFract = 0.5f
    smokeEffect = Fx.none
    shootEffect = smokeEffect

    //just make it massive, users of this bullet can adjust as necessary
    drawSize = 1000f
  }

  override fun continuousDamage(): Float {
    return damage / damageInterval * 60f
  }

  override fun estimateDPS(): Float {
    return damage * 100f / damageInterval * 3f
  }

  override fun load() {
    super.load()

    laser = Core.atlas.find(sprite)
    laserEnd = Core.atlas.find(sprite + "-end")
  }

  override fun draw(b: Bullet) {
    super.draw(b)

    Draw.color(color)
    Drawf.laser(laser, laserEnd, b.x, b.y, b.aimX, b.aimY, b.fslope() * (1f - oscMag + Mathf.absin(Time.time, oscScl, oscMag)))

    Draw.reset()
  }

  override fun update(b: Bullet) {
    updateTrail(b)
    updateTrailEffects(b)
    updateBulletInterval(b)

    if (b.timer.get(0, damageInterval)) {
      Damage.collidePoint(b, b.team, hitEffect, b.aimX, b.aimY)
    }

    if (b.timer.get(1, beamEffectInterval)) {
      beamEffect.at(b.aimX, b.aimY, beamEffectSize * b.fslope(), hitColor)
    }

    if (shake > 0) {
      Effect.shake(shake, shake, b)
    }
  }

  override fun updateTrailEffects(b: Bullet) {
    if (trailChance > 0) {
      if (Mathf.chanceDelta(trailChance.toDouble())) {
        trailEffect.at(b.aimX, b.aimY, if (trailRotation) b.angleTo(b.aimX, b.aimY) else (trailParam * b.fslope()), trailColor)
      }
    }

    if (trailInterval > 0f) {
      if (b.timer(0, trailInterval)) {
        trailEffect.at(b.aimX, b.aimY, if (trailRotation) b.angleTo(b.aimX, b.aimY) else (trailParam * b.fslope()), trailColor)
      }
    }
  }

  override fun updateTrail(b: Bullet) {
    if (!Vars.headless && trailLength > 0) {
      if (b.trail == null) {
        b.trail = Trail(trailLength)
      }
      b.trail.length = trailLength
      b.trail.update(b.aimX, b.aimY, b.fslope() * (1f - (if (trailSinMag > 0) Mathf.absin(Time.time, trailSinScl, trailSinMag) else 0f)))
    }
  }

  override fun updateBulletInterval(b: Bullet) {
    if (intervalBullet != null && b.time >= intervalDelay && b.timer.get(2, bulletInterval)) {
      val ang = b.rotation()
      for(i in 0..<intervalBullets) {
        intervalBullet.create(
          b,
          b.aimX,
          b.aimY,
          ang + Mathf.range(intervalRandomSpread) + intervalAngle + ((i - (intervalBullets - 1f) / 2f) * intervalSpread)
        )
      }
    }
  }
}
