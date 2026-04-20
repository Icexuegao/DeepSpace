package ice.content.block.nuclear

import arc.graphics.g2d.Draw
import ice.content.IItems
import ice.content.ILiquids

import mindustry.gen.Building
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import mindustry.world.draw.DrawPlasma
import singularity.graphic.SglDrawConst
import singularity.type.SglCategory
import singularity.world.blocks.nuclear.TokamakCore
import singularity.world.draw.DrawBottom

class 托卡马克点火装置:TokamakCore("tokamak_firer"){
  init  {
    quickRotate = false
    localization {
      zh_CN {
        name = "托卡马克点火装置"
        description = "托卡马克核聚变装置的核心组件,是添加材料与输出能量的端口,在一个核聚变装置中必须有且只有一个此设备。将此设备使用聚变约束导轨链接成一个闭环(这个闭环有且只能有4个拐角)构成完整的托卡马克聚变反应堆,而此反应堆的功率取决于整个结构的规模大小"
      }
    }
    requirements(
      SglCategory.nuclear,
      IItems.单晶硅,
      200,
      IItems.暮光合金,
      160,
      IItems.絮凝剂,
      220,
      IItems.强化合金,
      180,
      IItems.气凝胶,
      240,
      IItems.FEX水晶,
      160,
      IItems.充能FEX水晶,
      120,
      IItems.铱锭,
      100
    )
    size = 5

    itemCapacity = 60
    liquidCapacity = 65f
    energyCapacity = 65536f

    warmupSpeed = 0.0005f
    stopSpeed = 0.001f

    conductivePower = true

    drawers = DrawMulti(DrawBottom(), object : DrawPlasma() {
      init {
        suffix = "_plasma_"
        plasma1 = SglDrawConst.matrixNet
        plasma2 = Pal.reactorPurple
      }
    }, object : DrawDefault() {
      override fun draw(build: Building?) {
        Draw.z(Layer.blockOver)
        super.draw(build)
      }
    })

    setFuel(28f)
    consume!!.time(60f)
    consume!!.item(IItems.氢聚变燃料, 1)
    consume!!.liquid(ILiquids.相位态FEX流体, 0.1f)
    consume!!.power(32f)

    setFuel(30f)
    consume!!.time(60f)
    consume!!.item(IItems.氦聚变燃料, 1)
    consume!!.liquid(ILiquids.相位态FEX流体, 0.1f)
    consume!!.power(32f)
  }
}