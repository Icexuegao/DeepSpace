package ice.content.block.crafter

import arc.func.Cons
import arc.func.Floatf
import arc.math.Mathf
import ice.content.IItems
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.draw.DrawMulti
import mindustry.graphics.Layer
import mindustry.type.Category
import mindustry.world.Block
import mindustry.world.draw.DrawDefault
import singularity.Singularity
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawRegionDynamic

class FEXCrystalCharger:NormalCrafter("FEX_crystal_charger"){
  init{
  bundle {
    desc(zh_CN, "FEX充能座", "对FEX结晶释放高能中子脉冲,合适的脉冲频率会令能量在晶格之内不断积累,叠加,使FEX晶体结构变得不稳定,并带来一些特别的效果")
  }
  requirements(Category.crafting, IItems.强化合金, 70, IItems.FEX水晶, 60, IItems.石英玻璃, 65, IItems.絮凝剂, 70, IItems.钴钢, 85)
  size = 3

  itemCapacity = 15
  basicPotentialEnergy = 128f

  newConsume()
  consume!!.time(90f)
  consume!!.item(IItems.FEX水晶, 1)
  consume!!.energy(2f)
  newProduce()
  produce!!.item(IItems.充能FEX水晶, 1)

  updateEffect = SglFx.neutronWeaveMicro
  updateEffectChance = 0.04f
  updateEffectColor = SglDrawConst.fexCrystal
  craftEffect = SglFx.crystalConstructed
  craftEffectColor = SglDrawConst.fexCrystal



  crafting = Cons { e: NormalCrafterBuild? ->
    if (Mathf.chanceDelta((0.03f * e!!.workEfficiency()).toDouble())) {
      SglFx.shrinkParticleSmall.at(e.x, e.y, SglDrawConst.fexCrystal)
    }
  }

  draw = DrawMulti(DrawDefault(), object : DrawRegionDynamic<NormalCrafterBuild?>() {
    init {
      alpha = Floatf { e: NormalCrafterBuild? -> if (e!!.items.has(IItems.FEX水晶) || e.progress() > 0.4f) 1f else 0f }
    }

    override fun load(block: Block?) {
      region = Singularity.getModAtlas("FEX_crystal")
    }
  }, object : DrawRegionDynamic<NormalCrafterBuild?>() {
    init {
      layer = Layer.effect
      alpha = Floatf { e: NormalCrafterBuild? -> if (e!!.items.has(IItems.FEX水晶) || e.progress() > 0.4f) e.progress() else 0f }
    }

    override fun load(block: Block?) {
      region = Singularity.getModAtlas("FEX_crystal_power")
    }
  })
}
}