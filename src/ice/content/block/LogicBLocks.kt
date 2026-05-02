package ice.content.block

import ice.content.IItems
import ice.content.ILiquids
import ice.library.world.Load
import ice.ui.bundle.localization
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.type.Category
import mindustry.world.blocks.logic.LogicBlock
import mindustry.world.blocks.logic.MemoryBlock
import mindustry.world.blocks.logic.MessageBlock
import mindustry.world.blocks.logic.SwitchBlock

@Suppress("unused")
object LogicBLocks :Load {
  val 信息板 = object :MessageBlock("message") {
    init {
      localization {
        zh_CN {
          localizedName = "信息板"
          description = "保存文字信息,用于队友间进行交流"
        }
      }
      health = 40
      requirements(Category.logic, IItems.低碳钢, 5, IItems.高碳钢, 5, IItems.铜锭, 3)
    }
  }
  val 开关 = object :SwitchBlock("switch") {
    init {
      localization {
        zh_CN {
          localizedName = "开关"
          description = "可切换的开关,开关状态可以用逻辑处理器读取和控制"
        }
      }
      health = 40
      requirements(Category.logic, IItems.低碳钢, 5, IItems.高碳钢, 5, IItems.铅锭, 3)
    }
  }

  val 微型处理器 = object :LogicBlock("micro-processor") {
    init {
      localization {
        zh_CN {
          localizedName = "微型处理器"
          description = "循环运行一系列逻辑指令,可用于控制单位和建筑物"
        }
      }
      requirements(Category.logic, IItems.低碳钢, 50, IItems.高碳钢, 50, IItems.铜锭, 20, IItems.单晶硅, 20)

      health = 40
      instructionsPerTick = 3
      range = (8 * 15).toFloat()
      size = 1
    }
  }
  val 逻辑处理器 = object :LogicBlock("logic-processor") {
    init {
      localization {
        zh_CN {
          localizedName = "逻辑处理器"
          description = "循环运行一系列逻辑指令,可用于控制单位和建筑物,比微型处理器更快"
        }
      }
      requirements(Category.logic, IItems.铝锭, 50, IItems.石墨烯, 50, IItems.金锭, 30, IItems.钴钢, 110, IItems.导能回路, 50)

      health = 200
      instructionsPerTick = 11
      range = (8 * 32).toFloat()
      size = 2
    }
  }
  val 超核处理器 = object :LogicBlock("hyper-processor") {
    init {
      localization {
        zh_CN {
          localizedName = "超核处理器"
          description = "循环运行一系列逻辑指令,可用于控制单位和建筑物,比逻辑处理器更快"
        }
      }
      requirements(
        Category.logic,
        IItems.强化合金,
        150,
        IItems.暮光合金,
        150,
        IItems.电子元件,
        100,
        IItems.气凝胶,
        150,
        IItems.铱锭,
        50,
        IItems.铪锭,
        120
      )

      consumeLiquid(ILiquids.急冻液, 5f / 60f)
      hasLiquids = true

      health = 900
      instructionsPerTick = 75
      range = (8 * 90).toFloat()
      size = 3
    }
  }

  val 内存元 = object :MemoryBlock("memory-cell") {
    init {
      localization {
        zh_CN {
          localizedName = "内存元"
          description = "存储处理器的信息"
        }
      }
      requirements(Category.logic, IItems.高碳钢, 50, IItems.铜锭, 30, IItems.单晶硅, 30)
      health = 40
      memoryCapacity = 64
    }
  }
  val 内存库 = object :MemoryBlock("memory-bank") {
    init {
      localization {
        zh_CN {
          localizedName = "内存库"
          description = "存储处理器的信息,容量更大"
        }
      }
      health = 200
      requirements(Category.logic, IItems.钴钢, 90, IItems.陶钢, 30, IItems.导能回路, 40, IItems.铪锭, 30)
      memoryCapacity = 512
      size = 2
    }
  }
}