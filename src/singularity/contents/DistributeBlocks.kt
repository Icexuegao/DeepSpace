package singularity.contents

import arc.func.*
import arc.struct.ObjectMap
import mindustry.content.Items
import mindustry.gen.Building
import mindustry.gen.Teamc
import mindustry.graphics.Layer
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.world.Block
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import mindustry.world.meta.Env
import singularity.type.SglCategory
import singularity.world.blocks.distribute.*
import singularity.world.blocks.distribute.matrixGrid.MatrixEdgeBlock
import singularity.world.blocks.distribute.matrixGrid.MatrixGridCore
import singularity.world.blocks.distribute.netcomponents.AutoRecyclerComp
import singularity.world.blocks.distribute.netcomponents.ComponentInterface
import singularity.world.blocks.distribute.netcomponents.CoreNeighbourComponent
import singularity.world.blocks.distribute.netcomponents.NetPluginComp
import singularity.world.distribution.DistBufferType
import singularity.world.draw.DrawDirSpliceBlock
import singularity.world.draw.DrawEdgeLinkBits

class DistributeBlocks : ContentList {
    override fun load() {
        transport_node = object : ItemNode("transport_node") {
            init {
                requirements(
                    Category.distribution, ItemStack.with(
                        Items.silicon, 8,
                        SglItems.aerogel, 8,
                        SglItems.aluminium, 10
                    )
                )

                range = 4
                arrowTimeScl = 6f
                transportTime = 3f
            }
        }

        phase_transport_node = object : ItemNode("phase_transport_node") {
            init {
                requirements(
                    Category.distribution, ItemStack.with(
                        Items.phaseFabric, 6,
                        SglItems.aerogel, 10,
                        SglItems.strengthening_alloy, 8,
                        SglItems.aluminium, 12
                    )
                )

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
        }

        iridium_transport_node = object : ItemNode("iridium_transport_node") {
            init {
                requirements(
                    Category.distribution, ItemStack.with(
                        Items.phaseFabric, 4,
                        SglItems.iridium, 4,
                        SglItems.crystal_FEX, 6,
                        SglItems.aerogel, 12,
                        SglItems.aluminium, 12
                    )
                )

                researchCostMultiplier = 2f
                itemCapacity = 20
                maxItemCapacity = 80
                range = 20
                siphon = true
                arrowPeriod = 1.1f
                arrowTimeScl = 2.25f
                hasPower = true
                pulse = true
                envEnabled = envEnabled or Env.space
                transportTime = 0.5f
                newConsume()
                consume!!.power(1f)
            }
        }

        matrix_core = object : DistNetCore("matrix_core") {
            init {
                requirements(
                    SglCategory.matrix, ItemStack.with(
                        SglItems.matrix_alloy, 200,
                        SglItems.strengthening_alloy, 240,
                        SglItems.crystal_FEX, 220,
                        SglItems.aerogel, 200,
                        SglItems.iridium, 90,
                        Items.silicon, 260,
                        Items.graphite, 220,
                        Items.phaseFabric, 180
                    )
                )

                size = 6

                matrixEnergyUse = 1f
            }
        }

        matrix_bridge = object : MatrixBridge("matrix_bridge") {
            init {
                requirements(
                    SglCategory.matrix, ItemStack.with(
                        SglItems.matrix_alloy, 20,
                        SglItems.strengthening_alloy, 18,
                        SglItems.crystal_FEX, 10,
                        SglItems.aerogel, 16,
                        Items.phaseFabric, 8
                    )
                )

                size = 2

                newConsume()
                consume!!.powerCond(0.8f, 0f, Boolf { e: MatrixBridgeBuild? -> !e!!.distributor!!.network.netStructValid() })

                matrixEnergyUse = 0.02f
            }
        }

        matrix_tower = object : MatrixBridge("matrix_tower") {
            init {
                requirements(
                    SglCategory.matrix, ItemStack.with(
                        SglItems.matrix_alloy, 40,
                        SglItems.strengthening_alloy, 24,
                        SglItems.crystal_FEX_power, 18,
                        SglItems.iridium, 6,
                        Items.phaseFabric, 12
                    )
                )

                crossLinking = true
                size = 3
                maxLinks = 4

                linkRange = 45

                newConsume()
                consume!!.powerCond(1.6f, 0f, Boolf { e: MatrixBridgeBuild? -> !e!!.distributor!!.network.netStructValid() })

                matrixEnergyUse = 0.05f
            }
        }

        matrix_controller = object : MatrixGridCore("matrix_controller") {
            init {
                requirements(
                    SglCategory.matrix, ItemStack.with(
                        SglItems.matrix_alloy, 120,
                        SglItems.strengthening_alloy, 100,
                        SglItems.crystal_FEX, 80,
                        SglItems.iridium, 45,
                        Items.phaseFabric, 60,
                        Items.silicon, 80
                    )
                )

                linkOffset = 8f
                size = 4

                matrixEnergyUse = 1.2f
            }
        }

        matrix_grid_node = object : MatrixEdgeBlock("matrix_grid_node") {
            init {
                requirements(
                    SglCategory.matrix, ItemStack.with(
                        SglItems.matrix_alloy, 40,
                        SglItems.strengthening_alloy, 25,
                        SglItems.iridium, 12,
                        Items.phaseFabric, 20
                    )
                )
                linkOffset = 4.5f
                size = 2
            }
        }

        io_point = object : GenericIOPoint("io_point") {
            init {
                requirements(
                    SglCategory.matrix, ItemStack.with(
                        SglItems.matrix_alloy, 6,
                        SglItems.strengthening_alloy, 10,
                        SglItems.aerogel, 4
                    )
                )
                size = 1
            }
        }

        matrix_energy_manager = object : DistEnergyManager("matrix_energy_manager") {
            init {
                requirements(
                    SglCategory.matrix, ItemStack.with(
                        SglItems.matrix_alloy, 100,
                        SglItems.crystal_FEX_power, 60,
                        SglItems.strengthening_alloy, 60,
                        SglItems.iridium, 40,
                        SglItems.aerogel, 75
                    )
                )
                size = 4
            }
        }

        matrix_energy_buffer = object : DistEnergyBuffer("matrix_energy_buffer") {
            init {
                requirements(
                    SglCategory.matrix, ItemStack.with(
                        SglItems.matrix_alloy, 70,
                        SglItems.crystal_FEX, 45,
                        SglItems.crystal_FEX_power, 35,
                        SglItems.iridium, 20,
                        Items.phaseFabric, 40
                    )
                )
                size = 3

                matrixEnergyCapacity = 16384f
            }
        }

        matrix_power_interface = object : DistPowerEntry("matrix_power_interface") {
            init {
                requirements(
                    SglCategory.matrix, ItemStack.with(
                        SglItems.matrix_alloy, 45,
                        Items.copper, 40,
                        Items.silicon, 35,
                        Items.plastanium, 30,
                        Items.graphite, 30
                    )
                )
                size = 2

                consPower = 1000f
                eneProd = 480f
            }
        }

        matrix_neutron_interface = object : DistNeutronEntry("matrix_neutron_interface") {
            init {
                requirements(
                    SglCategory.matrix, ItemStack.with(
                        SglItems.matrix_alloy, 35,
                        SglItems.strengthening_alloy, 30,
                        SglItems.crystal_FEX, 20,
                        SglItems.iridium, 10
                    )
                )
                size = 2
            }
        }

        matrix_component_interface = object : ComponentInterface("matrix_component_interface") {
            init {
                requirements(
                    SglCategory.matrix, ItemStack.with(
                        SglItems.matrix_alloy, 40,
                        SglItems.strengthening_alloy, 40,
                        SglItems.aerogel, 40
                    )
                )
                size = 2
                topologyUse = 0
                matrixEnergyUse = 0.2f

                draw = DrawMulti(
                    DrawDefault(),
                    object : DrawDirSpliceBlock<ComponentInterfaceBuild?>() {
                        init {
                            simpleSpliceRegion = true
                            spliceBits = Intf { e: ComponentInterfaceBuild? -> e!!.interSplice.toInt() }
                        }
                    },
                    object : DrawEdgeLinkBits<ComponentInterfaceBuild?>() {
                        init {
                            layer = Layer.blockOver
                            compLinked = Func { e: ComponentInterfaceBuild? -> e!!.connectSplice }
                        }
                    }
                )
            }
        }

        matrix_process_unit = object : CoreNeighbourComponent("matrix_process_unit") {
            init {
                requirements(
                    SglCategory.matrix, ItemStack.with(
                        SglItems.matrix_alloy, 45,
                        SglItems.crystal_FEX, 45,
                        SglItems.strengthening_alloy, 50,
                        SglItems.iridium, 35,
                        Items.phaseFabric, 40
                    )
                )
                size = 3

                computingPower = 8
                matrixEnergyUse = 0.6f
            }
        }

        matrix_topology_container = object : CoreNeighbourComponent("matrix_topology_container") {
            init {
                requirements(
                    SglCategory.matrix, ItemStack.with(
                        SglItems.matrix_alloy, 80,
                        SglItems.crystal_FEX, 50,
                        SglItems.strengthening_alloy, 80,
                        SglItems.iridium, 45,
                        Items.phaseFabric, 80,
                        Items.graphite, 75
                    )
                )
                size = 4

                topologyCapaity = 16
                matrixEnergyUse = 0.8f
            }
        }

        matrix_buffer = object : NetPluginComp("matrix_buffer") {
            init {
                requirements(
                    SglCategory.matrix, ItemStack.with(
                        SglItems.matrix_alloy, 60,
                        SglItems.crystal_FEX, 45,
                        SglItems.aerogel, 40,
                        SglItems.iridium, 28,
                        Items.phaseFabric, 45
                    )
                )
                size = 3
                bufferSize = ObjectMap.of<DistBufferType<*>?, Int?>(
                    DistBufferType.itemBuffer, 512,
                    DistBufferType.liquidBuffer, 512
                )
                matrixEnergyUse = 0.6f
            }
        }

        automatic_recycler_component = object : AutoRecyclerComp("automatic_recycler_component") {
            init {
                requirements(
                    SglCategory.matrix, ItemStack.with(
                        SglItems.matrix_alloy, 50,
                        SglItems.aerogel, 75,
                        SglItems.strengthening_alloy, 40,
                        SglItems.aluminium, 60
                    )
                )

                hasLiquids = true
                hasItems = hasLiquids

                setRecycle<Building?>(DistBufferType.itemBuffer, Cons { e: Building? -> e!!.items.clear() })
                setRecycle<Building?>(DistBufferType.liquidBuffer, Cons { e: Building? -> e!!.liquids.clear() })

                size = 3
                matrixEnergyUse = 0.4f

                buildType = Prov {
                    object : AutoRecyclerCompBuild() {
                        override fun acceptStack(item: Item?, amount: Int, source: Teamc?): Int {
                            return if (distributor!!.network.core === source) amount else 0
                        }

                        public override fun acceptItem(source: Building, item: Item?): Boolean {
                            return distributor!!.network.core === source
                        }

                        public override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
                            return distributor!!.network.core === source
                        }
                    }
                }
            }
        }
    }

    companion object {
        /**运输节点 */
        var transport_node: Block? = null

        /**相位运输节点 */
        var phase_transport_node: Block? = null

        /**铱制高效运输节点 */
        var iridium_transport_node: Block? = null

        /**矩阵中枢 */
        var matrix_core: Block? = null

        /**矩阵桥 */
        var matrix_bridge: Block? = null

        /**矩阵塔 */
        var matrix_tower: Block? = null

        /**网格控制器 */
        var matrix_controller: Block? = null

        /**网格框架 */
        var matrix_grid_node: Block? = null

        /**io端点 */
        var io_point: Block? = null

        /**能源管理器 */
        var matrix_energy_manager: Block? = null

        /**能量接口 */
        var matrix_power_interface: Block? = null

        /**中子接口 */
        var matrix_neutron_interface: Block? = null

        /**矩阵储能簇 */
        var matrix_energy_buffer: Block? = null

        /**矩阵组件接口 */
        var matrix_component_interface: Block? = null

        /**矩阵处理单元 */
        var matrix_process_unit: Block? = null

        /**矩阵拓扑容器 */
        var matrix_topology_container: Block? = null

        /**通用物质缓存器 */
        var matrix_buffer: Block? = null

        /**自动回收组件 */
        var automatic_recycler_component: Block? = null
    }
}