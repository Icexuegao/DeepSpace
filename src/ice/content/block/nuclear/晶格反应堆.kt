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

class 晶格反应堆 : NuclearReactor("lattice_reactor") {
  init {
    localization {
      zh_CN {
        name = "晶格反应堆"
        description = "特制的缓速反应堆,不使用压缩燃料,直接对燃料晶格结构排列化进行可控裂变,产能较低,但利用率极高\n需要冷却,反应堆温度超过限制温度时会造成堆芯熔毁,引发小范围[accent]爆炸[]"
      }
    }
    requirements(
      SglCategory.nuclear,
      IItems.强化合金,
      120,
      IItems.FEX水晶,
      90,
      IItems.充能FEX水晶,
      70,
      IItems.铀238,
      100,
      IItems.絮凝剂,
      60,
      IItems.暮光合金,
      80
    )
    size = 3
    itemCapacity = 25
    liquidCapacity = 20f
    energyCapacity = 1024f
    hasLiquids = true
    explosionDamageBase = 260
    explosionRadius = 12
    productHeat = 0.1f
    newReact(IItems.铀235, 1200f, 6f, false)
    newReact(IItems.钚239, 1020f, 7f, false)
    newReact(IItems.钍锭, 900f, 4.5f, false)

    addCoolant(0.25f)
    consume!!.liquid(ILiquids.急冻液, 0.2f)

    addTransfer(ItemStack(IItems.钚239, 1))
    consume!!.time(420f)
    consume!!.item(IItems.铀238, 1)

    addTransfer(ItemStack(IItems.氢聚变燃料, 1))
    consume!!.time(480f)
    consume!!.item(IItems.相位封装氢单元, 1)

    addTransfer(ItemStack(IItems.氦聚变燃料, 1))
    consume!!.time(540f)
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