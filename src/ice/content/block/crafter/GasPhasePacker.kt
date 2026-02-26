package ice.content.block.crafter

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Angles
import arc.math.Mathf
import ice.content.IItems
import ice.content.ILiquids
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.ui.bundle.BaseBundle.Companion.desc
import ice.ui.bundle.BaseBundle.Companion.zh_CN
import ice.world.draw.DrawLiquidRegion
import ice.world.draw.DrawMulti
import mindustry.content.Items
import mindustry.content.Liquids
import mindustry.gen.Building
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawLiquidTile
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom

class GasPhasePacker:NormalCrafter("gas_phase_packer"){
  init{
  bundle {
    desc(zh_CN, "气体相位封装机", "用于将一份流体用相位物封装成中子靶丸,以进一步转变为核聚变所使用的燃料")
  }
  requirements(
    Category.crafting, ItemStack.with(
      IItems.强化合金, 80, IItems.气凝胶, 80, IItems.絮凝剂, 60, IItems.单晶硅, 60, IItems.钴锭, 45
    )
  )
  size = 3

  hasLiquids = true
  liquidCapacity = 32f
  itemCapacity = 24

  warmupSpeed = 0.01f

  newConsume()
  consume!!.time(240f)
  consume!!.power(1.4f)
  consume!!.items(
    *ItemStack.with(
      Items.phaseFabric, 2, IItems.气凝胶, 2
    )
  )
  consume!!.liquid(ILiquids.氢气, 0.4f)
  newProduce()
  produce!!.item(IItems.相位封装氢单元, 1)

  newConsume()
  consume!!.time(240f)
  consume!!.power(1.4f)
  consume!!.items(
    *ItemStack.with(
      Items.phaseFabric, 2, IItems.气凝胶, 2
    )
  )
  consume!!.liquid(ILiquids.氦气, 0.4f)
  newProduce()
  produce!!.item(IItems.相位封装氦单元, 1)

  draw = DrawMulti(
    DrawBottom(), DrawLiquidTile(), object : DrawBlock() {
      var piston: TextureRegion? = null

      override fun draw(build: Building) {

        for (i in 0..3) {
          val len = Mathf.absin(build.totalProgress() + 90 * i, 4f, 4f)
          val angle = i * 360f / 4

          Draw.rect(piston, build.x + Angles.trnsx(angle + 225, len), build.y + Angles.trnsy(angle + 225, len), angle)
        }
      }

      override fun load(block: Block) {
        piston = Core.atlas.find(block.name + "_piston")
      }
    }, object : DrawLiquidRegion() {
      init {
        suffix = "_liquid"
      }
    }, DrawDefault()
  )
}
}