package ice.world.content.blocks.liquid

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import ice.world.content.blocks.liquid.base.LiquidBlock
import mindustry.gen.Building
import mindustry.type.Liquid
import mindustry.world.meta.Stat

class LiquidJunction(name: String) : LiquidBlock(name) {
  init {
    floating = true
    buildType = Prov(::LiquidJunctionBuild)
  }

  override fun setStats() {
    super.setStats()
    stats.remove(Stat.liquidCapacity)
  }

  override fun setBars() {
    super.setBars()
    removeBar("liquid")
  }

  override fun icons(): Array<TextureRegion> {
    return arrayOf(region)
  }

  inner class LiquidJunctionBuild : LiquidBuild() {
    override fun draw() {
      Draw.rect(region, x, y)
    }

    override fun getLiquidDestination(source: Building, liquid: Liquid?): Building? {
      if (!enabled) return this
      val dir = (source.relativeTo(tile.x.toInt(), tile.y.toInt()) + 4) % 4
      val next = nearby(dir)
      if (next == null || (!next.acceptLiquid(this, liquid) && next.block !is LiquidJunction)) {
        return this
      }
      return next.getLiquidDestination(this, liquid)
    }
  }
}