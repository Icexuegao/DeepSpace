package ice.content.block

import ice.content.IItems
import ice.content.IUnitTypes
import ice.library.EventType.addContentInitEvent
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.effect.*
import ice.world.content.blocks.science.Laboratory
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.blocks.power.LightBlock
import mindustry.world.blocks.storage.StorageBlock
import mindustry.world.meta.Env

@Suppress("unused")
object EffectBlocks : Load {
  val 盒子 = StorageBlock("box").apply {
    size = 1
    health = 180
    itemCapacity = 60
    requirements(Category.effect, IItems.高碳钢, 30, IItems.低碳钢, 10, IItems.铜锭, 15)
    bundle {
      desc(zh_CN, "盒子", "微量存储各种类型的物品,可以用装卸器卸载物品", "经典回归之这个小盒就是你永远的家")
    }
  }
  val 压缩存储器 = StorageBlock("compressorMemory").apply {
    size = 2
    armor = 4f
    itemCapacity = 1200
    requirements(Category.effect, IItems.钴锭, 250, IItems.铱板, 150, IItems.钴钢, 100)
    bundle {
      desc(zh_CN, "压缩存储器", "坚固耐用,新型空间压缩技术使其具有更大的空间")
    }
  }
  val 仓库 = StorageBlock("warehouse").apply {
    size = 3
    health = 1280
    itemCapacity = 5560
    requirements(Category.effect, IItems.高碳钢, 330, IItems.低碳钢, 120, IItems.铜锭, 65)
    bundle {
      desc(zh_CN, "仓库", "存储各种类型的物品,可以用装卸器卸载物品")
    }
  }

  val 虔信方垒 = CoreBlock("pietasCornerCore").apply {
    size = 3
    health = 1000
    squareSprite = false
    unitType = IUnitTypes.加百列
    powerProduct = 200 / 60f
    isFirstTier = true
    itemCapacity = 4000
    unitCapModifier = 8
    alwaysUnlocked = true
    buildCostMultiplier = 2f
    requirements(Category.effect, IItems.高碳钢, 1000, IItems.低碳钢, 1200, IItems.锌锭, 400, IItems.铜锭, 200)
    bundle {
      desc(zh_CN, "虔信方垒")
    }
  }
  val 传颂核心 = CoreBlock("eulogyCore").apply {
    size = 4
    health = 5000
    unitType = IUnitTypes.路西法
    squareSprite = false
    itemCapacity = 10000
    unitCapModifier = 10
    buildCostMultiplier = 2f
    requirements(Category.effect, IItems.高碳钢, 3500, IItems.钴锭, 3000, IItems.铬锭, 2000, IItems.金锭, 1000)
    bundle {
      desc(zh_CN, "传颂核心")
    }
  }
  val 血肉枢纽: Block = FleshAndBloodCoreBlock("fleshAndBloodhinge").apply {
    health = -1
    size = 4
    itemCapacity = 6000
    squareSprite = false
    requirements(Category.effect, IItems.无名肉块, 2300, IItems.碎骨, 2000)
    bundle {
      desc(zh_CN, "血肉枢纽")
    }
  }

  val 真菌塔: Block = FungusCore("fungusTower").apply {
    size = 2
    squareSprite=false
    category = Category.effect
    bundle {
      desc(zh_CN, "真菌塔")
    }
  }
  val 定向超速器: Block = OrientationProjector("orientationProjector").apply {
    size = 2
    buildSize = 5
    range = 8 * 20f
    bundle {
      desc(zh_CN, "定向超速器")
    }
  }
  val 遗弃资源箱: Block = ResBox("resBox").apply {
    bundle {
      desc(zh_CN, "遗弃资源箱")
    }
    squareSprite=false
  }
  val 遗弃匣: Block = LostBox("lostBox").apply {
    size = 2
    envEnabled = Env.any
    category = Category.effect
    bundle {
      desc(zh_CN, "遗弃匣")
    }
  }
  val 传输矿仓: Block = ItemExtractor("conveyOreWar").apply {
    size = 2
    buildSize = 8
    range = 10 * 8f
    addContentInitEvent {
      allowLink.add(ProductBlocks.纤汲钻井)
    }
    requirements(Category.effect, ItemStack.with(IItems.低碳钢, 30))
    bundle {
      desc(zh_CN, "传输矿仓")
    }
  }

  val 基础实验室: Block = Laboratory("laboratory").apply {
    consumePower(100f / 60)
    bundle {
      desc(zh_CN, "基础实验室")
    }
    itemCapacity=100
    alwaysUnlocked = true
    requirements(Category.effect, IItems.高碳钢, 50, IItems.低碳钢, 50, IItems.铜锭, 50)
  }
  val 小型照明器: Block = LightBlock("illuminatorSmall").apply {
    size = 1
    armor = 1f
    radius = 90f
    brightness = 1.6f
    consumePower(0.2f)
    requirements(Category.effect, IItems.铜锭, 10f, IItems.高碳钢, 10f)
    bundle {
      desc(zh_CN, "照明器", "高效的照明设备,功耗低照明范围广")
    }
  }
  val 大型照明器: Block = LightBlock("illuminatorLarge").apply {
    size = 2
    armor = 4f
    radius = 270f
    brightness = 1.6f
    squareSprite=false
    consumePower(0.5f)
    requirements(Category.effect, IItems.铜锭, 30f, IItems.高碳钢, 20f, IItems.黄铜锭, 10f)
    bundle {
      desc(zh_CN, "大型照明器", "神说要有光,于是便有了光")
    }
  }
}