package ice.content.block.nuclear

import ice.content.IItems
import ice.content.ILiquids

import mindustry.type.ItemStack
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawLiquidRegion
import mindustry.world.draw.DrawMulti
import singularity.type.SglCategory
import singularity.world.blocks.nuclear.NuclearReactor
import singularity.world.draw.DrawReactorHeat

class 核反应堆 : NuclearReactor("nuclear_reactor") {init {
  localization {
    zh_CN {
      this.localizedName = "核反应堆"
      description = "标准的核裂变反应堆,使用压缩核燃料以高效率产出核能,燃料越紧凑效率越高,需要冷却,反应堆温度超过限制温度时会造成堆芯熔毁,引发剧烈的[accent]爆炸[]"
    }
  }
  requirements(
    SglCategory.nuclear,
    IItems.强化合金,
    200,
    IItems.FEX水晶,
    160,
    IItems.气凝胶,
    180,
    IItems.铀238,
    200,
    IItems.铅锭,
    180,
    IItems.絮凝剂,
    140
  )
  size = 4
  itemCapacity = 35
  liquidCapacity = 25f
  energyCapacity = 4096f

  hasLiquids = true

  ambientSoundVolume = 0.4f

  newReact(IItems.浓缩铀235核燃料, 450f, 8f, true)
  newReact(IItems.浓缩钚239核燃料, 420f, 9.5f, true)

  addCoolant(0.25f)
  consume!!.liquid(ILiquids.急冻液, 0.2f)

  addTransfer(ItemStack(IItems.钚239, 1))
  consume!!.time(180f)
  consume!!.item(IItems.铀238, 1)

  addTransfer(ItemStack(IItems.氢聚变燃料, 1))
  consume!!.time(210f)
  consume!!.item(IItems.相位封装氢单元, 1)

  addTransfer(ItemStack(IItems.氦聚变燃料, 1))
  consume!!.time(240f)
  consume!!.item(IItems.相位封装氦单元, 1)

  drawers = DrawMulti(
    DrawDefault(), object : DrawLiquidRegion(ILiquids.急冻液) {
      init {
        suffix = "_top"
      }
    }, DrawReactorHeat()
  )
}
}