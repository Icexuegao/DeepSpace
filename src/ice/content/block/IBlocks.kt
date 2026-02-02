package ice.content.block

import ice.content.block.turret.Turret
import ice.library.world.Load

object IBlocks : Load {
  override fun load() {
    Environment.load()
    Defense.load()
    ProductBlocks.load()
    NuclearBlocks.load()
    Distribution.load()
    MatrixDistNet.load()
    PowerBlocks.load()
    LiquidBlocks.load()
    CrafterBlocks.load()
    EffectBlocks.load()
    Turret.load()
  }
}