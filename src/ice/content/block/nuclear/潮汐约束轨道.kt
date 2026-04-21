package ice.content.block.nuclear

import ice.content.IItems

import singularity.type.SglCategory
import singularity.world.blocks.nuclear.TokamakOrbit

class 潮汐约束轨道:TokamakOrbit("tidal_confinement_orbit"){
  init {
    localization {
      zh_CN {
        this.localizedName = "潮汐约束导轨"
        description = "利用引力场强制约束等离子流的聚变导轨,体积巨大,但具有非常高的功率倍数"
      }
    }
    quickRotate = false
    requirements(
      SglCategory.nuclear,
      IItems.絮凝剂,
      100,
      IItems.暮光合金,
      120,
      IItems.简并态中子聚合物,
      60,
      IItems.强化合金,
      140,
      IItems.FEX水晶,
      100,
      IItems.充能FEX水晶,
      80,
      IItems.气凝胶,
      160,
      IItems.铱锭,
      120

    )
    size = 5
    squareSprite = false
    itemCapacity = 40
    liquidCapacity = 45f

    flueMulti = 2f
    efficiencyPow = 2f
  }
}