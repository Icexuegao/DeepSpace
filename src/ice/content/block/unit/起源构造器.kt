package ice.content.block.unit

import arc.func.Cons2
import arc.math.Mathf
import ice.content.IItems
import ice.content.ILiquids
import mindustry.type.Category
import mindustry.type.UnitType
import singularity.world.blocks.product.SglUnitFactory
import universecore.world.consumers.BaseConsumers

class 起源构造器:SglUnitFactory("cstr_1"){
  init {
    localization {
      zh_CN {
        localizedName = "起源构造器"
        description = "高度集成的机械建造工厂,可以直接进行中小型单位的建造任务"
        description =
          "比起繁杂冗长的重构工作,我们将所有工作都集成在了你看到的这坐机器当中,这可以节省出大量的空间与成本,为对抗敌人创造更多的优势"
      }
    }
    requirements(Category.units, IItems.单晶硅, 120, IItems.锌锭, 160, IItems.钍锭, 90, IItems.铝锭, 120, IItems.强化合金, 135)
    size = 5
    liquidCapacity = 240f
    energyCapacity = 256f
    basicPotentialEnergy = 256f

    consCustom = Cons2 { u: UnitType?, c: BaseConsumers? ->
      c!!.power(Mathf.round(u!!.health / u.hitSize) * 0.02f).showIcon = true
    }

    sizeLimit = 24f
    healthLimit = 7200f
    machineLevel = 4

    newBooster(1.5f)
    consume!!.liquid(ILiquids.急冻液, 2.4f)
    newBooster(1.8f)
    consume!!.liquid(ILiquids.FEX流体, 2f)
  }
}