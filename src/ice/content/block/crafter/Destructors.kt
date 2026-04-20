package ice.content.block.crafter

import arc.Core
import ice.content.AtomSchematics
import ice.content.IItems
import ice.world.draw.DrawMulti
import ice.world.meta.IceStats
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.ui.Bar
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawPlasma
import singularity.graphic.SglDrawConst
import singularity.world.blocks.product.NormalCrafter
import singularity.world.draw.DrawBottom
import universecore.components.blockcomp.ConsumerBuildComp

class Destructors : NormalCrafter("destructor") {
  init {
    localization {
      zh_CN {
        name = "析构器"
        description = "加速碰撞破坏物质的原子核结构,以分析物质的微观构成形态并建立原子空间构成的蓝图"
      }
    }
    requirements(Category.crafting, IItems.简并态中子聚合物, 100, IItems.矩阵合金, 50, IItems.絮凝剂, 40, IItems.强化合金, 50, IItems.铱锭, 60)
    size = 5

    recipeIndfo = "析构物品"

    drawers = DrawMulti(
      DrawBottom(), object : DrawPlasma() {
        init {
          suffix = "_plasma_"
          plasma1 = SglDrawConst.matrixNet
          plasma2 = SglDrawConst.matrixNetDark
        }
      }, DrawDefault()
    )

    for (atomSchematic in AtomSchematics.AtomSchematic.all) {
      newConsume().apply {
        item(atomSchematic.item, 1)
        energy(8f)
        setConsTrigger {_: ConsumerBuildComp ->
          atomSchematic.destructing()
        }
        time(6f)
      }
      newProduce().apply {
        power(1f)
      }
    }
  }

  override fun setBars() {
    super.setBars()
    addBar("progress") {e: NormalCrafterBuild ->
      val schematic = if (e.consumeCurrent == -1) null else AtomSchematics.AtomSchematic.all[e.consumeCurrent]
      Bar({
        if (schematic != null) Core.bundle.formatString("解析进度: {0}/{1}", schematic.d, schematic.reqint)
        else IceStats.未选择.localized()
      }, {Pal.bar}, {schematic?.progession() ?: 0f})
    }
  }
}