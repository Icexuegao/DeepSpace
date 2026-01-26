package ice.content.block

import arc.func.Func
import arc.func.Intf
import arc.func.Prov
import arc.struct.ObjectMap
import ice.content.IItems
import ice.library.world.Load
import ice.ui.bundle.BaseBundle.Companion.bundle
import ice.world.content.blocks.abstractBlocks.IceBlock.Companion.requirements
import mindustry.content.Items
import mindustry.gen.Building
import mindustry.gen.Teamc
import mindustry.graphics.Layer
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import singularity.type.SglCategory
import singularity.world.blocks.distribute.*
import singularity.world.blocks.distribute.MatrixBridge.MatrixBridgeBuild
import singularity.world.blocks.distribute.matrixGrid.MatrixEdgeBlock
import singularity.world.blocks.distribute.matrixGrid.MatrixGridCore
import singularity.world.blocks.distribute.netcomponents.AutoRecyclerComp
import singularity.world.blocks.distribute.netcomponents.ComponentInterface
import singularity.world.blocks.distribute.netcomponents.CoreNeighbourComponent
import singularity.world.blocks.distribute.netcomponents.NetPluginComp
import singularity.world.distribution.DistBufferType
import singularity.world.draw.DrawDirSpliceBlock
import singularity.world.draw.DrawEdgeLinkBits

@Suppress("unused")
object MatrixDistNet : Load {

  var 矩阵中枢 = DistNetCore("matrix_core").apply {
    bundle{
      desc(zh_CN,"矩阵中枢伺服器","一个矩阵网络的核心设备,矩阵网络必须有且只有一个中枢伺服器,矩阵桥连接此方块时只能单向连接")
    }
    requirements(
      SglCategory.matrix, IItems.矩阵合金, 200, IItems.强化合金, 240, IItems.FEX水晶, 220, IItems.气凝胶, 200, IItems.铱锭, 90, Items.silicon, 260, Items.graphite, 220, Items.phaseFabric, 180
    )
    size = 6
    matrixEnergyUse = 1f
  }
  var 矩阵桥 = MatrixBridge("matrix_bridge").apply {
    requirements(
      SglCategory.matrix, IItems.矩阵合金, 20, IItems.强化合金, 18, IItems.FEX水晶, 10, IItems.气凝胶, 16, Items.phaseFabric, 8

    )
    size = 2
    newConsume()
    consume!!.powerCond(0.8f, 0f) { e: MatrixBridgeBuild? -> !e!!.distributor.network.netStructValid() }
    matrixEnergyUse = 0.02f
    bundle{
      desc(zh_CN,"矩阵桥","矩阵建立的物质运输桥,可任意方向链接的物质运输桥,可运输物品和液体\n同时,这也是矩阵网络中用于连接设备的节点,只能呈树状结构建立矩阵网络")
    }
  }
  var 矩阵塔 = MatrixBridge("matrix_tower").apply {
    requirements(
      SglCategory.matrix, IItems.矩阵合金, 40, IItems.强化合金, 24, IItems.充能FEX水晶, 18, IItems.铱锭, 6, Items.phaseFabric, 12

    )
    crossLinking = true
    size = 3
    maxLinks = 4

    linkRange = 45

    newConsume()
    consume!!.powerCond(1.6f, 0f) { e: MatrixBridgeBuild -> !e.distributor.network.netStructValid() }

    matrixEnergyUse = 0.05f
    bundle{
      desc(zh_CN,"矩阵塔","大型的矩阵连接柱,比矩阵桥有更远的连接距离,但是只能向四个方向进行连接")
    }
  }
  var 网格控制器 = MatrixGridCore("matrix_controller").apply {

    requirements(
      SglCategory.matrix, ItemStack.with(
        IItems.矩阵合金, 120, IItems.强化合金, 100, IItems.FEX水晶, 80, IItems.铱锭, 45, Items.phaseFabric, 60, Items.silicon, 80
      )
    )

    linkOffset = 8f
    size = 4

    matrixEnergyUse = 1.2f
    bundle{
      desc(zh_CN,"网格控制器","矩阵网格的控制中枢,与矩阵网格框架建立矩阵网格,在网格中通过此设备配置网格内io点的的输入输出和存储设备,是一个重要的物流管理模型")
    }
  }
  var 网格框架 = MatrixEdgeBlock("matrix_grid_node").apply {
    bundle{
      desc(zh_CN,"网格框架","矩阵网格的构建设备,彼此连接,与一个网络控制器构成闭环后建立其矩阵网格")
    }
    requirements(
      SglCategory.matrix, ItemStack.with(
        IItems.矩阵合金, 40, IItems.强化合金, 25, IItems.铱锭, 12, Items.phaseFabric, 20
      )
    )
    linkOffset = 4.5f
    size = 2
  }
  var io端点 = GenericIOPoint("io_point").apply {
    bundle{
      desc(zh_CN,"通用IO端口","矩阵网格使用的IO设施,物品及液体的通用端口,通过网格控制器进行端口配置")
    }
    requirements(
      SglCategory.matrix, ItemStack.with(
        IItems.矩阵合金, 6, IItems.强化合金, 10, IItems.气凝胶, 4
      )
    )
    size = 1
  }
  var 能源管理器 = DistEnergyManager("matrix_energy_manager").apply {
    bundle{
      desc(zh_CN,"能源管理器","对能源接受和管理的设备,与相邻的能源设备形成一个能源管理模块,用于接受和缓存能量以供应网络使用")
    }
    requirements(
      SglCategory.matrix, ItemStack.with(
        IItems.矩阵合金, 100, IItems.充能FEX水晶, 60, IItems.强化合金, 60, IItems.铱锭, 40, IItems.气凝胶, 75
      )
    )
    size = 4
  }
  var 能源能源簇 = DistEnergyBuffer("matrix_energy_buffer").apply {

    requirements(
      SglCategory.matrix, ItemStack.with(
        IItems.矩阵合金, 70, IItems.FEX水晶, 45, IItems.充能FEX水晶, 35, IItems.铱锭, 20, Items.phaseFabric, 40
      )
    )
    size = 3
    bundle {
      desc(zh_CN,"能源能源簇","网络缓存能源能的设备,可以存储一定量的矩阵能源以避免停电等情况造成的致命问题")
    }
    matrixEnergyCapacity = 16384f
  }
  var 能量接口 = DistPowerEntry("matrix_power_interface").apply {

    requirements(
      SglCategory.matrix, ItemStack.with(
        IItems.矩阵合金, 45, Items.copper, 40, Items.silicon, 35, Items.plastanium, 30, Items.graphite, 30
      )
    )
    size = 2
bundle {
  desc(zh_CN,"能量接口","接收能量供应网络消耗而设备,需要邻近能源管理器放置")
}
    consPower = 1000f
    eneProd = 480f
  }
  var 中子接口 = DistNeutronEntry("matrix_neutron_interface").apply {

    requirements(
      SglCategory.matrix, ItemStack.with(
        IItems.矩阵合金, 35, IItems.强化合金, 30, IItems.FEX水晶, 20, IItems.铱锭, 10
      )
    )
    size = 2
    bundle {
      desc(zh_CN,"中子接口","网络接受中子能输入的设备,需要邻近能源管理器放置")
    }
  }
  var 矩阵组件接口 = ComponentInterface("matrix_component_interface").apply {
    requirements(
      SglCategory.matrix, ItemStack.with(
        IItems.矩阵合金, 40, IItems.强化合金, 40, IItems.气凝胶, 40
      )
    )
    size = 2
    topologyUse = 0
    matrixEnergyUse = 0.2f
    draw = DrawMulti(DrawDefault(), DrawDirSpliceBlock<ComponentInterface.ComponentInterfaceBuild>().apply {
      simpleSpliceRegion = true
      spliceBits = Intf { e: ComponentInterface.ComponentInterfaceBuild -> e.interSplice.toInt() }
    }, DrawEdgeLinkBits<ComponentInterface.ComponentInterfaceBuild>().apply {
      layer = Layer.blockOver
      compLinked = Func { e: ComponentInterface.ComponentInterfaceBuild -> e.connectSplice }
    })
    bundle {
      desc(zh_CN,"矩阵组件接口","用于将矩阵功能组件接入网络中,需要彼此连接形成连续的接口结构,且与组件设备需要的连接方式匹配才可接入网络")
    }
  }
  var 矩阵处理单元 = CoreNeighbourComponent("matrix_process_unit").apply {

    requirements(
      SglCategory.matrix, ItemStack.with(
        IItems.矩阵合金, 45, IItems.FEX水晶, 45, IItems.强化合金, 50, IItems.铱锭, 35, Items.phaseFabric, 40
      )
    )
    size = 3

    computingPower = 8
    matrixEnergyUse = 0.6f

    bundle {
      desc(zh_CN,"矩阵处理单元","需要紧贴矩阵中枢伺服器放置,提供网络核心的运算力,使网络每次刷新可以处理更多的请求任务")
    }
  }
  var 矩阵拓扑容器 = CoreNeighbourComponent("matrix_topology_container").apply {

    requirements(
      SglCategory.matrix, ItemStack.with(
        IItems.矩阵合金, 80, IItems.FEX水晶, 50, IItems.强化合金, 80, IItems.铱锭, 45, Items.phaseFabric, 80, Items.graphite, 75
      )
    )
    size = 4

    topologyCapaity = 16
    matrixEnergyUse = 0.8f
    bundle {
      desc(zh_CN,"矩阵拓扑容器","需要紧贴矩阵中枢伺服器放置,提供整个网络的拓扑容量,使网络可以安装更多的设备")
    }
  }
  var 通用物质缓存器 = NetPluginComp("matrix_buffer").apply {

    requirements(
      SglCategory.matrix, ItemStack.with(
        IItems.矩阵合金, 60, IItems.FEX水晶, 45, IItems.气凝胶, 40, IItems.铱锭, 28, Items.phaseFabric, 45
      )
    )
    size = 3
    bufferSize = ObjectMap.of(
      DistBufferType.itemBuffer, 512, DistBufferType.liquidBuffer, 512
    )
    matrixEnergyUse = 0.6f
    bundle {
      desc(zh_CN,"通用物质缓存器","网络组件,提供网络的物品和流体缓存区容量")
    }
  }
  var 自动回收组件 = AutoRecyclerComp("automatic_recycler_component").apply {

    requirements(
      SglCategory.matrix, ItemStack.with(
        IItems.矩阵合金, 50, IItems.气凝胶, 75, IItems.强化合金, 40, IItems.铝, 60
      )
    )

    hasLiquids = true
    hasItems = true
bundle {
  desc(zh_CN,"自动回收组件","可以配置缓存的溢出资源回收,在已安装该设备的网络中,若所有网络容器都已满,接收的在配置回收清单中的资源将被自动销毁,以避免网络堵死\n有两种配置模式：\n    [accent]黑名单模式[]: 销毁溢出的已选中资源\n    [accent]白名单模式[]: 配置选中的资源在溢出时被保留")
}
    setRecycle(DistBufferType.itemBuffer) { e: Building? -> e!!.items.clear() }
    setRecycle(DistBufferType.liquidBuffer) { e: Building? -> e!!.liquids.clear() }
    size = 3
    matrixEnergyUse = 0.4f
    buildType = Prov {
      object : AutoRecyclerComp.AutoRecyclerCompBuild() {
        override fun acceptStack(item: Item?, amount: Int, source: Teamc?): Int {
          return if (distributor.network.core === source) amount else 0
        }

        override fun acceptItem(source: Building, item: Item?): Boolean {
          return distributor.network.core === source
        }

        override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
          return distributor.network.core === source
        }
      }
    }
  }

}