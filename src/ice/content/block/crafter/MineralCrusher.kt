package ice.content.block.crafter

import ice.content.IItems
import ice.world.content.blocks.crafting.oreMultipleCrafter.OreFormula
import ice.world.content.blocks.crafting.oreMultipleCrafter.OreMultipleCrafter
import ice.world.draw.DrawLiquidRegion
import ice.world.draw.DrawMulti
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.type.LiquidStack
import mindustry.world.consumers.ConsumeLiquids
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion

class MineralCrusher : OreMultipleCrafter("mineralCrusher") {
  init {
    squareSprite = false
    hasLiquids = true
    drawers = DrawMulti(DrawRegion("-bottom"), DrawLiquidRegion(), DrawDefault(), DrawRegion("-runner", 6f, true).apply {
      x = 8.3f
      y = 8.3f
    }, DrawRegion("-runner", 6f, true).apply {
      x = -8.3f
      y = -8.3f
    }, DrawRegion("-runner", 6f, true).apply {
      x = 8.3f
      y = -8.3f
    }, DrawRegion("-runner", 6f, true).apply {
      x = -8.3f
      y = 8.3f
    })
    oreFormula.add(OreFormula().apply {
      crftTime = 60f
      addInput(IItems.方铅矿, 1)
      addInput(ConsumeLiquids(LiquidStack.with(Liquids.water, 15f)))
      addOutput(IItems.铅锭, 1, 5)
      addOutput(IItems.铜锭, 2, 60)
      addOutput(Items.beryllium, 3, 7)
    }, OreFormula().apply {
      crftTime = 30f
      addInput(IItems.黄铜矿, 1, IItems.生煤, 1)
      addOutput(IItems.铅锭, 1, 50)
      addOutput(IItems.钴锭, 1, 50)
    })
  }
}