package ice.content.block.crafter

import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.draw.DrawMulti
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.world.blocks.liquid.LiquidBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawLiquidTile
import singularity.world.blocks.product.MediumCrafter
import singularity.world.draw.DrawBottom

class QualityGenerator : MediumCrafter("quality_generator") {
  init {
    bundle {
      desc(zh_CN, "质量生成器", "消耗中子能生成介质", "将能量无序逆向转换的设备,将大量中子能向物质质量进行转换,将产出除夸克类介质外的纯净无序介质")
    }
    requirements(Category.crafting, IItems.铱锭, 50, IItems.充能FEX水晶, 80, IItems.絮凝剂, 50, IItems.暮光合金, 20)
    size = 4


    energyCapacity = 16384f
    mediumCapacity = 32f

    newConsume()
    consume!!.energy(32f)
    newProduce()
    produce!!.medium(0.6f)
    drawers = DrawMulti(DrawBottom(), object : DrawLiquidTile(ILiquids.孢子云) {
      override fun draw(build: Building) {
        val drawn = drawLiquid ?: build.liquids.current()
        LiquidBlock.drawTiledFrames(build.block.size, build.x, build.y, padLeft, padRight, padTop, padBottom, drawn, (build as MediumCrafterBuild).mediumContains / mediumCapacity * alpha)
      }
    }, DrawDefault())
  }
}