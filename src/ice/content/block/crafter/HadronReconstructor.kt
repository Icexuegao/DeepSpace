package ice.content.block.crafter

import arc.graphics.g2d.Draw
import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.draw.DrawMulti
import mindustry.gen.Building
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawPlasma
import singularity.world.SglFx
import singularity.world.blocks.product.AtomSchematicCrafter
import singularity.world.draw.DrawBottom
import universecore.world.producers.ProduceType

class HadronReconstructor : AtomSchematicCrafter("hadron_reconstructor") {
  init {
    bundle {
      desc(zh_CN, "强子重构仪", "消耗介质生成原材料,可配置", "微缩的定向大量强子对撞机,使得创造物质从理论成为现实")
    }
    requirementPairs(
      Category.crafting,
      IItems.简并态中子聚合物 to 80,

      IItems.铱锭 to 120,

      IItems.充能FEX水晶 to 120,

      IItems.矩阵合金 to 90,

      IItems.气凝胶 to 120,

      IItems.暮光合金 to 90
    )
    size = 4
    itemCapacity = 24


    craftEffect = SglFx.hadronReconstruct

    drawers = DrawMulti(
      DrawBottom(), object : DrawPlasma() {
        init {
          suffix = "_plasma_"
          plasma1 = Pal.reactorPurple
          plasma2 = Pal.reactorPurple2
        }
      }, object : DrawBlock() {
        override fun draw(build: Building?) {
          val e = build as NormalCrafterBuild
          Draw.alpha(e.progress())
          if (e.producer!!.current != null) Draw.rect(e.producer!!.current!!.get(ProduceType.item)!!.items[0].item.uiIcon, e.x, e.y, 4f, 4f)
          Draw.color()
        }
      }, DrawDefault()
    )
  }
}