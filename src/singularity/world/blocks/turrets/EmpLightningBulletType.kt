package singularity.world.blocks.turrets

import arc.graphics.g2d.Draw
import arc.util.Time
import arc.util.pooling.Pools
import mindustry.gen.Bullet
import mindustry.graphics.Layer
import singularity.world.SglFx
import universecore.graphics.lightnings.LightningContainer
import universecore.graphics.lightnings.LightningContainer.PoolLightningContainer

open class EmpLightningBulletType : EmpBulletType {
  constructor(time: Float, damage: Float) : super(time, damage)

  constructor() : super()

  override fun init(bullet: Bullet) {
    super.init(bullet)
    val cont = Pools.obtain(PoolLightningContainer::class.java, ::PoolLightningContainer)
    bullet.data = cont

    init(bullet, cont)
  }

  open fun init(b: Bullet, cont: LightningContainer) {
  }

  override fun update(b: Bullet) {
    super.update(b)

    val data = b.data
    if (data is LightningContainer) {
      update(b, data)
    }
  }

  override fun draw(b: Bullet) {
    super.draw(b)

    val data = b.data
    if (data is LightningContainer) {
      draw(b, data)
    }
  }

  fun draw(b: Bullet, c: LightningContainer) {
    Draw.color(hitColor)
    Draw.z(Layer.bullet)
    c.draw(b.x, b.y)
  }

  fun update(bullet: Bullet, container: LightningContainer) {
    container.update()
  }

  override fun removed(b: Bullet) {
    super.removed(b)
    val data = b.data
    if (data is LightningContainer) {
      SglFx.lightningCont.at(b.x, b.y, 0f, hitColor, data)
      Time.run(210f) { Pools.free(data) }
    }
  }
}