package universecore.world.particles.models

import arc.graphics.Color
import arc.math.Mathf
import universecore.world.particles.Particle
import universecore.world.particles.Particle.Cloud
import universecore.world.particles.ParticleModel

/** 拖尾淡化粒子模型
 *
 * 控制粒子拖尾节点的尺寸淡化和颜色渐变,
 * 支持线性和插值两种淡化模式 */
open class TrailFadeParticle :ParticleModel() {
  var trailFade: Float = 0.075f
  var fadeColor: Color? = null
  var colorLerpSpeed: Float = 0.03f
  var linear: Boolean = false

  override fun updateTrail(particle: Particle, c: Cloud) {
    c.size = if (linear) Mathf.approachDelta(c.size, 0f, trailFade) else Mathf.lerpDelta(c.size, 0f, trailFade)
    fadeColor?.let { c.color.lerp(it, colorLerpSpeed) }
  }
}
