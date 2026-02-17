package ice.content.block.crafter

import arc.Core
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.draw.DrawMulti
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawPlasma
import singularity.graphic.SglDrawConst
import singularity.world.blocks.function.Destructor
import singularity.world.draw.DrawBottom

class Destructors: Destructor("destructor") {
  init {
    bundle {
      desc(zh_CN, "析构器", "加速碰撞破坏物质的原子核结构,以分析物质的微观构成形态并建立原子空间构成的蓝图")
    }
    requirements(Category.effect, ItemStack.with())
    size = 5

    placeablePlayer = false
    recipeIndfo = Core.bundle.get("infos.destructItems")

    draw = DrawMulti(
      DrawBottom(), object : DrawPlasma() {
        init {
          suffix = "_plasma_"
          plasma1 = SglDrawConst.matrixNet
          plasma2 = SglDrawConst.matrixNetDark
        }
      }, DrawDefault()
    )
  }
}