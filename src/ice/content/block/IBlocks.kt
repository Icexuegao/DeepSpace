package ice.content.block

object IBlocks {
  fun load() {
    EnvironmentBlocks.load()
    DefenseBlocks.load()
    ProductBlocks.load()
    NuclearBlocks.load()
    Distributions.load()
    MatrixDistNet.load()
    PowerBlocks.load()
    LiquidBlocks.load()
    CrafterBlocks.load()
    EffectBlocks.load()
    UnitBlocks.load()
    TurretBlocks.load()
  }
}