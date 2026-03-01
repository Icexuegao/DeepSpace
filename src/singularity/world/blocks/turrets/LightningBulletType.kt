package singularity.world.blocks.turrets

import arc.graphics.g2d.Draw
import arc.util.Time
import arc.util.pooling.Pools
import mindustry.entities.bullet.BulletType
import mindustry.gen.Bullet
import singularity.world.SglFx
import universecore.graphics.lightnings.LightningContainer

open class LightningBulletType : BulletType {
  constructor(time: Float, damage: Float) : super(time, damage)

  constructor() : super()

  override fun init(bullet: Bullet) {
    super.init(bullet)
    val cont = Pools.obtain(LightningContainer.PoolLightningContainer::class.java) { LightningContainer.PoolLightningContainer() }
    bullet.data = cont

    init(bullet, cont)
  }

  open fun init(b: Bullet, cont: LightningContainer) {
  }

  override fun update(b: Bullet) {
    super.update(b)

    if (b.data is LightningContainer) {
      update(b, b.data as LightningContainer)
    }
  }

  override fun draw(b: Bullet) {
    super.draw(b)

    if (b.data is LightningContainer) {
      draw(b, b.data as LightningContainer)
    }
  }

  open fun draw(b: Bullet, c: LightningContainer) {
    Draw.color(b.type.hitColor)
    c.draw(b.x, b.y)
  }

  open fun update(bullet: Bullet, container: LightningContainer) {
    container.update()
  }

  override fun removed(b: Bullet) {
    super.removed(b)

    if (b.data is LightningContainer) {
      SglFx.lightningCont.at(b.x, b.y, 0f, b.type.hitColor, b.data)
      Time.run(210f) { Pools.free(b.data) }
    }
  }
}
