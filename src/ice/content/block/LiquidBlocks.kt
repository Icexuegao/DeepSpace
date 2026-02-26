package ice.content.block

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles.randLenVectors
import ice.content.IItems
import ice.content.ILiquids
import ice.library.EventType.addContentInitEvent
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.distribution.itemNode.TransferNode
import ice.world.content.blocks.liquid.*
import ice.world.content.blocks.liquid.base.LiquidRouter
import ice.world.draw.DrawMulti
import mindustry.entities.Effect
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.blocks.production.Pump
import mindustry.world.draw.DrawBlurSpin
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawLiquidTile
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.Attribute
import singularity.world.blocks.liquid.LiquidUnloader

@Suppress("unused")
object LiquidBlocks : Load {
  val 泵腔 = PumpChamber("pumpChamber").apply {
    requirements(Category.liquid, ItemStack.with(IItems.肌腱, 40, IItems.碎骨, 10, IItems.无名肉块, 60))
    bundle {
      desc(zh_CN, "泵腔")
    }
  }
  val 动力泵 = Pump("kineticPump").apply {
    size = 1
    squareSprite = false
    requirements(Category.liquid, IItems.高碳钢, 40, IItems.锌锭, 10)
    bundle {
      desc(zh_CN, "动力泵")
    }
  }
  val 谐振泵 = Pump("resonancePump").apply {
    size = 2
    squareSprite = false
    requirements(Category.liquid, IItems.高碳钢, 40, IItems.锌锭, 10)
    bundle {
      desc(zh_CN, "谐振泵")
    }
  }
  val 心肌泵 = Pump("myocardialPump").apply {
    size = 4
    squareSprite = false
    pumpAmount = 0.625f
    liquidCapacity = 240f
    consumePower(8f)
    requirements(Category.liquid, IItems.石英玻璃, 120, IItems.铱板, 120, IItems.导能回路, 85, IItems.陶钢, 45, IItems.生物钢, 15)
    bundle {
      desc(zh_CN, "心肌泵", "终极液泵,生物科技的高级产物")
    }
  }
  val 异质析取器 = SolidPump("solutePump").apply {
    size = 3
    pumpAmount = 0.2f
    squareSprite = false
    liquidCapacity = 40f
    result = ILiquids.异溶质
    attribute = Attribute.water
    consumePower(2f)
    updateEffect = Effect(50f) { e ->
      Draw.color(Pal.lancerLaser)
      randLenVectors(
        e.id.toLong(), 4, 1 + 20f * e.fout(),
      ) { x: Float, y: Float ->
        Draw.color(result.color)
        Fill.circle(e.x + x, e.y + y, e.fout() * 3f)
      }
    }
    drawers = DrawMulti(DrawRegion("-bottom"), DrawLiquidTile(result, 2f), DrawDefault(), DrawBlurSpin("-rotator", 2f).apply { blurThresh = 2f })
    requirements(Category.liquid, IItems.高碳钢, 10, IItems.锌锭, 20)
    bundle {
      desc(zh_CN, "异质析取器", "从环境中提取${ILiquids.异溶质.localizedName}")
    }
  }

  val 谐振导管 = Conduit("resonanceConduit").apply {
    requirements(Category.liquid, IItems.高碳钢, 10, IItems.锌锭, 2)
    bundle {
      desc(zh_CN, "谐振导管")
    }
    addContentInitEvent {
      bridgeReplacement = 导管桥
      junctionReplacement = 基础液体交叉器
    }
  }
  val 流金导管 = Conduit("fluxGoldConduit").apply {
    liquidCapacity = 40f
    liquidPressure = 1.025f
    requirements(Category.liquid, IItems.金锭, 10, IItems.锌锭, 2)
    bundle {
      desc(zh_CN, "流金导管")
    }
    addContentInitEvent {
      bridgeReplacement = 导管桥
      junctionReplacement = 基础液体交叉器
    }
  }
  val 动脉导管 = Conduit("arteryConduit").apply {
    healAmount = 30f
    health = 600
    armor = 2f
    leaks = false
    liquidCapacity = 60f
    liquidPressure = 1.1f
    placeableLiquid = true
    requirements(Category.liquid, IItems.石英玻璃, 1, IItems.铱板, 2, IItems.陶钢, 1, IItems.生物钢, 1)
    bundle {
      desc(zh_CN, "动脉导管")
    }
    addContentInitEvent {
      bridgeReplacement = 动脉导管桥
      junctionReplacement = 基础液体交叉器
    }
  }

  val 导管桥 = TransferNode("bridgeConduit").apply {
    range = 6
    hasItems = false
    hasPower = false
    liquidCapacity = 10f
    requirements(Category.liquid, IItems.锌锭, 5, IItems.石英玻璃, 10)
    bundle {
      desc(zh_CN, "导管桥", "在以自我为中心且边长为${2 * range + 1}的正方形范围内,向任意方向传输液体")
    }
  }
  val 装甲导管桥 = TransferNode("bridgeConduitArmored").apply {
    directionAny = false
    armor = 4f
    allowDiagonal = false
    range = 10
    fadeIn = false
    hasItems = false
    bridgeWidth = 8f
    hasPower = false
    arrowSpacing = 6f
    liquidCapacity = 24f
    placeableLiquid = true
    selectionColumns = 6
    requirements(Category.liquid, IItems.石英玻璃, 8, IItems.陶钢, 3, IItems.铱板, 5)
    bundle {
      desc(zh_CN, "装甲导管桥")
    }
  }
  val 长距导管桥 = TransferNode("bridgeConduitLarge").apply {
    range = 10
    hasItems = false
    liquidCapacity = 10f
    consumePower(30f / 60f)
    requirements(Category.liquid, IItems.铜锭, 8, IItems.锌锭, 10, IItems.石英玻璃, 20)

    bundle {
      desc(zh_CN, "长距导管桥", "消耗电力,在以自我为中心且边长为${2 * range + 1}的正方形范围内,向任意方向传输液体")
    }
  }
  val 动脉导管桥 = TransferNode("bridgeConduitArtery").apply {
    healAmount = 60f
    allowDiagonal = false
    hasItems = false
    directionAny = false
    armor = 4f
    range = 18
    liquidCapacity = 32f
    placeableLiquid = true
    consumePower(0.5f)
    requirements(Category.liquid, IItems.石英玻璃, 20, IItems.导能回路, 10, IItems.生物钢, 5)
    bundle {
      desc(zh_CN, "动脉导管桥")
    }
  }
  val 基础液体路由器 = LiquidRouter("baseLiquidRouter").apply {
    size = 1
    health = 100
    requirements(Category.liquid, IItems.铜锭, 4, IItems.石英玻璃, 2)
    bundle {
      desc(zh_CN, "基础液体路由器", "接受一个方向的液体输入,并平均输出到其他3个方向,可以储存一定量的液体")
    }
  }
  val 装甲液体路由器 = LiquidRouter("armoredLiquidRouter").apply {
    armor = 4f
    liquidCapacity = 60f
    liquidPressure = 1.1f
    solid = false
    underBullets = true
    placeableLiquid = true
    requirements(Category.liquid, IItems.石英玻璃, 2, IItems.陶钢, 1, IItems.铱板, 3)
    bundle {
      desc(zh_CN, "装甲液体路由器", "向各个方向快速运输液体")
    }
  }
  val 基础液体交叉器 = LiquidJunction("baseLiquidJunction").apply {
    size = 1
    health = 80
    requirements(Category.liquid, IItems.黄铜锭, 5, IItems.石英玻璃, 5)

    bundle {
      desc(zh_CN, "基础液体交叉器")
    }
  }
  val 流体容器 = LiquidRouter("liquidContainer").apply {
    size = 2
    solid = true
    health = 500
    squareSprite = false
    liquidPadding = 6f / 4f
    liquidCapacity = 800f
    requirements(Category.liquid, IItems.铜锭, 20, IItems.石英玻璃, 15)
    bundle {
      desc(zh_CN, "流体容器")
    }
  }
  val 流体仓库 = LiquidRouter("liquidStorage").apply {
    size = 3
    solid = true
    health = 1000
    squareSprite = false
    liquidPadding = 6f / 4f
    liquidCapacity = 2000f
    requirements(Category.liquid, IItems.铜锭, 50, IItems.石英玻璃, 30)
    bundle {
      desc(zh_CN, "流体仓库")
    }
  }
  val 装甲储液罐 = LiquidRouter("armorLiquidStorage").apply {
    healAmount = 120f
    health = 3200
    armor = 8f
    size = 4
    liquidPadding = 4f
    liquidCapacity = 6400f
    placeableLiquid = true
    requirements(Category.liquid, IItems.铱板, 85, IItems.陶钢, 55, IItems.石英玻璃, 35)
    bundle {
      desc(zh_CN, "装甲储液罐", "双层复合装甲,内置的修复夹层可快速修复罐体,使其更安全地储存大量液体")
    }
  }
  val 流体枢纽 = MultipleLiquidBlock("fluidJunction").apply {
    size = 3
    liquidCapacity = 1000f
    health = size * size * 100
    requirements(Category.liquid, IItems.铜锭, 50, IItems.铬锭, 30, IItems.单晶硅, 20, IItems.石英玻璃, 50)
    bundle {
      desc(zh_CN, "流体枢纽", "能将多种流体独立存储于同一单元,有效解决了复杂流水线中的空间占用问题,是高级化生产的必备设施")
    }
  }
  val 流体抽离器 = LiquidClassifier("liquidClassifier").apply {
    size = 1
    liquidCapacity = 0f
    requirements(Category.liquid, IItems.铜锭, 20, IItems.黄铜锭, 10, IItems.铬锭, 10, IItems.石英玻璃, 10)

    bundle {
      desc(zh_CN, "流体抽离器", "流体枢纽的流体卸载装置,将流体卸载于相邻的可输入建筑,本身并不存储流体")
    }
  }
  val 流体装卸器 = LiquidUnloader("liquid_unloader").apply {
    bundle {
      desc(zh_CN, "流体装卸器", "从方块中卸载流体，就像装卸器提取物品一样")
    }
    requirements(Category.liquid, IItems.单晶硅, 20, IItems.铝锭, 25, IItems.铬锭, 15)
    size = 1
  }
}