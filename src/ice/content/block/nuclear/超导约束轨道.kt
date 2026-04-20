package ice.content.block.nuclear

import ice.content.IItems

import singularity.type.SglCategory
import singularity.world.blocks.nuclear.TokamakOrbit

class 超导约束轨道:TokamakOrbit("magnetic_confinement_orbit"){
  init {
    localization {
      zh_CN {
        name = "超导电磁约束导轨"
        description = "通过电磁场约束等离子体流的聚变约束导轨,需要消耗大量电力驱动"
      }
    }
    requirements(
      SglCategory.nuclear,
      IItems.絮凝剂,
      60,
      IItems.暮光合金,
      80,
      IItems.单晶硅,
      100,
      IItems.强化合金,
      120,
      IItems.FEX水晶,
      80,
      IItems.气凝胶,
      100,
      IItems.铱锭,
      60

    )
    quickRotate = false
    size = 3
    squareSprite = false
    conductivePower = true

    newConsume()
    consume!!.power(3f)

    itemCapacity = 20
    liquidCapacity = 20f

    flueMulti = 1f
    efficiencyPow = 1.5f
  }
}