package singularity.world.particles

import arc.math.geom.Rect
import mindustry.content.Fx
import mindustry.gen.Bullet
import mindustry.graphics.Pal
import singularity.world.blocks.turrets.HeatBulletType
import universecore.world.particles.MultiParticleModel
import universecore.world.particles.models.*

object SglParticleModels {
  val rect: Rect = Rect()
  val hitrect: Rect = Rect()
  var defHeatTrailHitter = object :HeatBulletType() {
    init {
      damage = 20f
      melDamageScl = 0.8f
      meltDownTime = 6f
      lifetime = 60f
      speed = 0f
      collidesAir = false
      collidesGround = true
      collides = false
      pierce = true
      hittable = false
      absorbable = false
      hitEffect = Fx.circleColorSpark
      hitColor = Pal.lighterOrange
      despawnEffect = Fx.none
    }

    override fun draw(b: Bullet) {}
  }
  var floatParticle = MultiParticleModel(SizeVelRelatedParticle(), RandDeflectParticle().apply {
    deflectAngle = 90f
    strength = 0.2f
  }, TrailFadeParticle().apply {
      trailFade = 0.04f
      fadeColor = Pal.lightishGray
      colorLerpSpeed = 0.03f
  }, ShapeParticle(), DrawDefaultTrailParticle())
  var heatBulletTrail = HeatBulletParticleModel()
}
