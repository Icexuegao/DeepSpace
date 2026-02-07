package ice.content.block

import ice.content.block.TurretBlocks
import ice.library.world.Load

object IBlocks : Load {
  override fun load() {
    EnvironmentBlocks.load()
    DefenseBlocks.load()
    ProductBlocks.load()
    NuclearBlocks.load()
    Distribution.load()
    MatrixDistNet.load()
    PowerBlocks.load()
    LiquidBlocks.load()
    CrafterBlocks.load()
    EffectBlocks.load()
    TurretBlocks.load()
  }
}