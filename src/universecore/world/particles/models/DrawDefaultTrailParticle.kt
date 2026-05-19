package universecore.world.particles.models

import universecore.world.particles.Particle
import universecore.world.particles.ParticleModel

/** 默认拖尾绘制模型
 *
 * 遍历粒子的所有云节点，根据节点在拖尾中的位置计算透明度渐变，
 * 从粒子头部到尾部逐渐淡化 */
open class DrawDefaultTrailParticle :ParticleModel() {
  override fun drawTrail(particle: Particle) {
    var n = 0f
    for(ps in particle) {
      ps.draw(1 - n / particle.cloudCount(), 1 - (n + 1) / particle.cloudCount())
      n++
    }
  }
}