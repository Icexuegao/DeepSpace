package singularity.world.unit.types

import arc.math.geom.Vec2
import ice.entities.bullet.base.BulletType
import mindustry.gen.Bullet
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.SglFx

class EdgeFragBullet : BulletType() {
  init {
    damage = 80f
    splashDamage = 40f
    splashDamageRadius = 24f
    speed = 4f
    hitSize = 3f
    lifetime = 120f
    despawnHit = true
    hitEffect = SglFx.diamondSpark
    hitColor = SglDrawConst.matrixNet

    collidesTiles = false

    homingRange = 160f
    homingPower = 0.075f

    trailColor = SglDrawConst.matrixNet
    trailLength = 25
    trailWidth = 3f
  }

  override fun draw(b: Bullet) {
    super.draw(b)
    SglDraw.drawDiamond(b.x, b.y, 10f, 4f, b.rotation())
  }

  override fun update(b: Bullet) {
    super.update(b)
    b.vel.lerpDelta(Vec2.ZERO, 0.04f)
  }
}