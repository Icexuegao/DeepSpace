package ice.world.content.blocks.liquid

import arc.func.Prov
import ice.world.content.blocks.liquid.base.LiquidBlock
import mindustry.gen.Building
import mindustry.type.Liquid
import mindustry.world.draw.DrawDefault
import mindustry.world.meta.Stat

class LiquidJunction(name: String) : LiquidBlock(name) {
  init {
    floating = true
    drawers = DrawDefault()
    displayLiquid = false
    buildType = Prov(::LiquidJunctionBuild)
  }

  override fun setStats() {
    super.setStats()
    stats.remove(Stat.liquidCapacity)
  }

  inner class LiquidJunctionBuild : LiquidBuild() {

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