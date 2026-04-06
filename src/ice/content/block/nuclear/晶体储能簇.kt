package ice.content.block.nuclear

import arc.func.Floatf
import arc.func.Func
import arc.math.Mathf
import ice.content.IItems
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import mindustry.graphics.Layer
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import singularity.graphic.SglDrawConst
import singularity.type.SglCategory
import singularity.world.blocks.nuclear.EnergyContainer
import singularity.world.draw.DrawBottom
import singularity.world.draw.DrawRegionDynamic

class 晶体储能簇:EnergyContainer("crystal_container"){
  init {

      bundle {
        desc(zh_CN, "晶体储能簇", "晶体式中子能存储器,用于存储中子能")
      }
      squareSprite = false
      requirements(
        SglCategory.nuclear,
        IItems.FEX水晶,
        160,
        IItems.气凝胶,
        80,
        IItems.矩阵合金,
        80,
        IItems.强化合金,
        100,
        IItems.单晶硅,
        60,
        IItems.絮凝剂,
        55

      )
      size = 3
      energyCapacity = (2 shl 16).toFloat()
      energyPotential = 1024f
      maxEnergyPressure = 4096f

      drawers = DrawMulti(
        DrawBottom(), DrawDefault(), object : DrawRegionDynamic<EnergyContainerBuild>("_top") {
          init {
            layer = Layer.effect
            color = Func { _: EnergyContainerBuild? -> SglDrawConst.fexCrystal }
            alpha = Floatf { e: EnergyContainerBuild? -> Mathf.clamp(e!!.getEnergy() / e.energyCapacity()) }
          }
        })
    }
}