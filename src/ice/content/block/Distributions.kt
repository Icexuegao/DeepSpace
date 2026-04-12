package ice.content.block

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import ice.content.IItems
import ice.graphics.IceColor
import ice.library.EventType.addContentInitEvent
import ice.library.world.Load
import ice.ui.bundle.bundle
import ice.ui.bundle.desc
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirementPairs
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import ice.world.content.blocks.distribution.*
import ice.world.content.blocks.distribution.conveyor.ArmoredConveyor
import ice.world.content.blocks.distribution.conveyor.Conveyor
import ice.world.content.blocks.distribution.conveyor.StackConveyor
import ice.world.content.blocks.distribution.digitalStorage.HubConduit
import ice.world.content.blocks.distribution.digitalStorage.LogisticsHub
import ice.world.content.blocks.distribution.digitalStorage.LogisticsInput
import ice.world.content.blocks.distribution.digitalStorage.LogisticsOutput
import ice.world.content.blocks.distribution.droneNetwork.DroneDeliveryTerminal
import ice.world.content.blocks.distribution.droneNetwork.DroneReceivingRnd
import ice.world.content.blocks.distribution.itemNode.TransferNode
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.Effect
import mindustry.entities.bullet.PointBulletType
import mindustry.entities.effect.ParticleEffect
import mindustry.entities.effect.WaveEffect
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.world.blocks.distribution.MassDriver
import mindustry.world.meta.BuildVisibility
import mindustry.world.meta.Env
import singularity.world.blocks.distribute.ItemNode

@Suppress("unused")
object Distributions : Load {
  val 基础传送带 = Conveyor("baseConveyor").apply {
    bundle {
      desc(zh_CN, "基础传送带", "运输设施,造价低廉")
    }
    size = 1
    speed = 6f
    health = 30
    addContentInitEvent {
      junctionReplacement = 基础交叉器
      bridgeReplacement = 基础传送带桥
    }
    requirements(Category.distribution, IItems.低碳钢, 1)
  }
  val 强化传送带 = Conveyor("reinforcedConveyor").apply {
    bundle {
      desc(zh_CN, "强化传送带", "运输设施,造价低廉,比基础传送带更快")
    }
    size = 1
    speed = 13f
    health = 45
    addContentInitEvent {
      junctionReplacement = 基础交叉器
      bridgeReplacement = 基础传送带桥
    }
    requirements(Category.distribution, IItems.高碳钢, 1, IItems.锌锭, 1)

  }

  val 特种传送带 = ArmoredConveyor("specialConveyor").apply {
    bundle {
      desc(zh_CN, "特种传送带", "运输设施,比强化传送带更快,不接收侧面输入")
    }
    size = 1
    speed = 15f
    health = 55
    addContentInitEvent {
      junctionReplacement = 基础交叉器
      bridgeReplacement = 装甲传送带桥
    }
    requirements(Category.distribution, IItems.钴锭, 1, IItems.铬锭, 1, IItems.复合陶瓷, 1)

  }

  val 血肉装甲传送带 = Conveyor("fleshArmorConveyor").apply {
    bundle {
      desc(zh_CN, "血肉装甲传送带", "运输设施,比特种传送带更快,会缓慢回复生命值")
    }
    health = 600
    armor = 8f
    speed = 42f
    healAmount = 30f
    placeableLiquid = true
    requirements(Category.distribution, IItems.生物钢, 1, IItems.铱板, 2)
    addContentInitEvent {
      bridgeReplacement = 增生传送带桥
      junctionReplacement = 交叉神经链路
    }

  }

  val 钴熠传送带 = StackConveyor("cobaltBrightConveyor").apply {
    bundle {
      desc(zh_CN, "钴熠传送带")
    }
    speed = 50f / 600f
    requirements(Category.distribution, IItems.高碳钢, 2, IItems.钴钢, 1, IItems.铬锭, 1)

    loadEffect = WaveEffect().apply {
      lifetime = 20f
      sides = 3
      sizeTo = 6f
      sizeFrom = 0f
      strokeTo = 0f
      strokeFrom = 3f
      colorFrom = IceColor.b4
      colorTo = IceColor.b4
    }
  }
  val 生物钢传送带 = StackConveyor("biologicalSteelConveyor").apply {
    bundle {
      desc(zh_CN, "生物钢传送带", "运输设施,打包物品进行运输,通电后加快运输速度")
    }
    healAmount = 10f
    health = 300
    armor = 8f
    speed = 0.1f
    baseEfficiency = 1f
    itemCapacity = 100
    consumePower(0.05f)
    hasPower = true
    consumesPower = true
    conductivePower = true
    placeableLiquid = true
    unloadEffect = ParticleEffect().apply {
      particles = 7
      lifetime = 15f
      length = 25f
      cone = -360f
      lenFrom = 5f
      lenTo = 0f
      colorFrom = Color.valueOf("BF3E47")
      colorTo = Color.valueOf("E78F92")
    }
    destroyBullet = PointBulletType().apply {
      damage = 0f
      splashDamage = 30f
      splashDamageRadius = 24f
      hitShake = 2f
      status = StatusEffects.melting
      statusDuration = 30f * 60f
      speed = 0f
      lifetime = 1f
      hitEffect = Fx.none
      despawnEffect = Fx.none
    }
    researchCostMultiplier = 40f
    requirements(Category.distribution, IItems.导能回路, 1, IItems.生物钢, 1)

  }
  val 梯度传送带 = StackConveyor("gradedConveyor").apply {
    bundle {
      desc(zh_CN, "梯度传送带", "运输设施,打包多种物品进行运输.如果末端未被阻挡,则包裹会被抛出")
    }
    speed = 60f / 600f
    drawLastItems = false
    differentItem = true
    loadEffect = Effect(30.0f) { e ->
      Draw.color(Color.valueOf("b8bde1"))
      Lines.stroke(0.5f * e.fout())
      val spread = 4f
      Fx.rand.setSeed(e.id.toLong())
      Draw.alpha(e.fout())
      for (i in 0..7) {
        val ang = e.rotation + Fx.rand.range(8f) + i
        Fx.v.trns(ang, Fx.rand.random(e.fin() * 10f))
        Lines.lineAngle(
          e.x + Fx.v.x + Fx.rand.range(spread),
          e.y + Fx.v.y + Fx.rand.range(spread),
          ang,
          e.fout() * Fx.rand.random(1f) + 1f
        )
      }
    }
    requirements(Category.distribution, IItems.铪锭, 20)
  }

  val 基础交叉器 = Junction("baseJunction").apply {
    bundle {
      desc(zh_CN, "基础交叉器", "两条交叉传送带的桥梁")
    }
    size = 1
    health = 100
    requirements(Category.distribution, IItems.低碳钢, 5, IItems.高碳钢, 5)
  }
  val 交叉神经链路 = Junction("junctionNeuralChain").apply {
    bundle {
      desc(zh_CN, "交叉神经链路", "两条交叉传送带的桥梁,比交叉器更快")
    }
    armor = 4f
    health = 250
    displayedSpeed = 30f
    placeableLiquid = true
    requirements(Category.distribution, IItems.生物钢, 1, IItems.铱板, 1)

  }

  val 转换分类器 = Sorter("transformSorter").apply {
    bundle {
      desc(zh_CN, "转换分类器", "如果物品与所选种类 相同/不同 ,则允许其通过.否则,物品将向两侧输出.可配置")
    }
    size = 1
    health = 100
    requirements(Category.distribution, IItems.高碳钢, 8, IItems.低碳钢, 6)

  }
  val 基础路由器 = Router("baseRouter").apply {
    bundle {
      desc(zh_CN, "基础路由器", "将物品平均分配至其他三个方向")
    }
    size = 1
    health = 70
    requirements(Category.distribution, IItems.低碳钢, 5)

  }
  val 转换溢流门 = TransformOverflowGate("transformOverflowGate").apply {
    bundle {
      desc(zh_CN, "转换溢流门", "当 前方/两侧 被阻塞时才会向 两侧/前方 输出,用于处理多余的物品.可配置")
    }
    size = 1
    health = 200
    requirements(Category.distribution, IItems.高碳钢, 8, IItems.低碳钢, 6)

  }

  val 基础传送带桥 = TransferNode("baseBridge").apply {
    bundle {
      desc(zh_CN, "基础传送带桥", "跨越地形货建筑传输物品")
    }
    allowDiagonal = false
    directionAny = false
    health = 30
    range = 5
    hasLiquids = false
    fadeIn = true
    bridgeWidth = 8f
    hasPower = false
    arrowSpacing = 6f
    transportTime = 60f/17f
    requirements(Category.distribution, IItems.高碳钢, 6, IItems.锌锭, 4)
  }
  val 装甲传送带桥 = TransferNode("armorBridge").apply {
    bundle {
      desc(zh_CN, "装甲传送带桥", "跨越任何地形货建筑传输物品,比普通桥更快,更远")
    }
    allowDiagonal = false
    directionAny = false
    health = 40
    armor = 4f
    range = 10
    hasLiquids = false
    fadeIn = false
    bridgeWidth = 8f
    hasPower = false
    arrowSpacing = 6f
    transportTime = 1.6f
    placeableLiquid = true
    selectionColumns = 6
    requirements(Category.distribution, IItems.陶钢, 4, IItems.铱板, 4)

  }
  val 增生传送带桥 = TransferNode("growthBridge").apply {
    directionAny = false
    hasLiquids = false
    squareSprite = false
    allowDiagonal = false
    healAmount = 60f
    armor = 4f
    range = 18
    transportTime = 1f
    placeableLiquid = true
    consumePower(0.5f)
    requirements(Category.distribution, IItems.导能回路, 10, IItems.生物钢, 5)
    bundle {
      desc(zh_CN, "增生传送带桥", "跨越任何地形货建筑传输物品,比装甲传送带桥更快,更远.会缓慢回复生命")
    }
  }
  val 传输节点 = TransferNode("transferNode").apply {
    bundle {
      desc(zh_CN, "传输节点","能同时运输液体和物品,拥有较远的范围")
    }
    hasPower=false
    fadeIn = true
    size = 1
    health = 100
    itemCapacity=20
    transportTime = 60f/22f
    requirements(Category.distribution, IItems.锌锭,4, IItems.铜锭, 4, IItems.钴锭, 8, IItems.石英玻璃, 10)
  }
  val 运输节点 = ItemNode("transport_node").apply {
    bundle {
      desc(zh_CN, "运输节点", "高级的传送带桥,可以对节点的任意侧面配置指定物品的输入与输出")
    }
    requirements(Category.distribution, IItems.电子元件, 8, IItems.气凝胶, 8, IItems.铝锭, 10)
    range = 4
    arrowTimeScl = 6f
    transportTime = 3f
  }
  val 相位运输节点 = ItemNode("phase_transport_node").apply {
    bundle {
      desc(zh_CN, "相位运输节点", "高级的传送带桥,具有更快的运输速度和更远的连接距离")
    }
    requirements(Category.distribution, IItems.絮凝剂, 6, IItems.气凝胶, 10, IItems.强化合金, 8, IItems.铝锭, 12)
    researchCostMultiplier = 1.5f
    itemCapacity = 15
    maxItemCapacity = 60
    range = 12
    arrowPeriod = 0.9f
    arrowTimeScl = 2.75f
    hasPower = true
    pulse = true
    envEnabled = envEnabled or Env.space
    transportTime = 1f
    newConsume()
    consume!!.power(0.4f)
  }
  val 铱制高效运输节点 = ItemNode("iridium_transport_node").apply {
    bundle {
      desc(
        zh_CN,
        "高效运输节点",
        "高级的传送带桥,具有更快的运输速度和更远的连接距离,节点还具备卸载器的功能,可以直接从指定方向的方块中抽取被选中的物品"
      )
    }
    requirements(
      Category.distribution, IItems.絮凝剂, 4, IItems.铱锭, 4, IItems.FEX水晶, 6, IItems.气凝胶, 12, IItems.铝锭, 12
    )
    researchCostMultiplier = 2f
    itemCapacity = 60
    maxItemCapacity = 180
    range = 20
    siphon = true
    arrowPeriod = 1.1f
    arrowTimeScl = 2.25f
    hasPower = true
    pulse = true
    envEnabled = envEnabled or Env.space
    transportTime = 0.5f
    newConsume().apply {
      power(1f)
    }
  }

  val 基础卸载器 = Unloader("baseUninstalle").apply {
    bundle {
      desc(zh_CN, "基础卸载器", "从容器中卸载物品")
    }
    speed = 60f / 10f
    health = 50
    requirementPairs(Category.distribution, IItems.高碳钢 to 30, IItems.低碳钢 to 10, IItems.铜锭 to 15)
  }
  val 极速卸载器 = Unloader("speedUninstalle").apply {
    bundle {
      desc(zh_CN, "极速卸载器", "从容器中高速卸载物品")
    }
    speed = 60f / 30f
    health = 80
    requirementPairs(Category.distribution, IItems.铬锭 to 30, IItems.铱板 to 25, IItems.导能回路 to 15)
  }
  val 量子卸载器 = Unloader("electronicUninstaller").apply {
    bundle {
      desc(zh_CN, "量子卸载器", "从容器中超高速卸载物品")
    }
    squareSprite=false
    speed = 60f / 60f
    health = 200
    squareSprite = false
    requirementPairs(Category.distribution, IItems.电子元件 to 25, IItems.钴锭 to 25, IItems.导能回路 to 5)

    newConsume().apply {
      power(20f/60f)
    }
  }

  val 质量驱动器 = MassDriver("massDrives").apply {
    bundle {
      desc(
        zh_CN,
        "质量驱动器",
        "远距离传输物品,收集若干物品后将其发射到另一个质量驱动器中"
      )
    }
    size = 2
    reload =120f
    range = 40*8f
    consumePower(3.75f)
    dumpTime = 1
    knockback = 4f
    translation = 2f
    bulletSpeed = 6f
    rotateSpeed = 2f
    itemCapacity = 50
    bulletLifetime = 160f
    shootSound = Sounds.shootCollaris
    shootEffect = Fx.shootBig2
    smokeEffect = Fx.shootSmokeTitan
    receiveEffect = Fx.hitSquaresColor
    requirements(Category.distribution, IItems.铬锭, 135, IItems.铱板, 25, IItems.导能回路, 55, IItems.钴锭, 35)
  }

  val 重型质量驱动器 = MassDriver("heavyDutyMassDrives").apply {
    bundle {
      desc(
        zh_CN,
        "重型质量驱动器",
        "超远距离传输物品,收集若干物品后将其发射到另一个重型质量驱动器中,容量巨大但转速和发射速度缓慢"
      )
    }
    size = 5
    reload = 600f
    range = 1280f
    consumePower(23.75f)
    squareSprite = false
    dumpTime = 1
    knockback = 4f
    translation = 2f
    bulletSpeed = 12f
    rotateSpeed = 2f
    itemCapacity = 2400
    minDistribute = 600
    bulletLifetime = 160f
    shootSound = Sounds.shootCollaris
    shootEffect = Fx.shootBig2
    smokeEffect = Fx.shootSmokeTitan
    receiveEffect = Fx.hitSquaresColor
    requirements(Category.distribution, IItems.钴锭, 335, IItems.铱板, 285, IItems.导能回路, 225, IItems.钴钢, 175)
  }

  val 物流枢纽核心 = LogisticsHub("logisticsHub").apply {
    bundle {
      desc(zh_CN, "物流枢纽核心","简易的物品管理模块,通过管道统一分发物品")
    }
    requirements(
      Category.distribution, IItems.电子元件, 50, IItems.导能回路, 100, IItems.钴钢, 200, IItems.强化合金, 50
    )
  }
  val 枢纽管道 = HubConduit("hubConduit").apply {
    bundle {
      desc(zh_CN, "枢纽管道","物流枢纽通过此管道分发物品,必须临近放置")
    }
    requirements(Category.distribution, IItems.锌锭, 1, IItems.导能回路, 1)
  }
  val 物流输入器 = LogisticsInput("logisticsInput").apply {
    bundle {
      desc(zh_CN, "物流输入器","通过枢纽管道向当前核心输入物品")
    }
    requirements(Category.distribution, IItems.铜锭, 20, IItems.导能回路, 5)
  }
  val 物流输出器 = LogisticsOutput("logisticsOutput").apply {
    bundle {
      desc(zh_CN, "物流输出器","通过枢纽管道从当前核心抽出物品")
    }
    requirements(Category.distribution, IItems.锌锭, 1, IItems.电子元件, 1)
  }
  val 无人机供货端 = DroneDeliveryTerminal("droneTeliveryTerminal")
  val 无人机需求端 = DroneReceivingRnd("droneReceivingRnd").apply {
    requirements(Category.distribution, IItems.铜锭, 20, IItems.单晶硅, 10)
    bundle {
      desc(zh_CN, "无人机需求端", "用于从供货端接受物品")
    }
  }
  val 随机源 = Randomer("randomSource").apply {
    bundle {
      desc(zh_CN, "随机源", "随机输出所有资源")
    }
    buildVisibility = BuildVisibility.sandboxOnly
  }
  val dirSource = DirSource("dirSource").apply {
    bundle { desc(zh_CN, "定向源", "定向输出所有资源") }
  }
}