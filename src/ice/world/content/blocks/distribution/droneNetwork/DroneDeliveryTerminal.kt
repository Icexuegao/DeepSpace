package ice.world.content.blocks.distribution.droneNetwork

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.struct.Seq
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import ice.ai.CarryTaskAI
import ice.content.IItems
import ice.content.IUnitTypes
import ice.world.content.blocks.abstractBlocks.RangeBlock
import ice.world.content.blocks.distribution.droneNetwork.DroneReceivingRnd.DroneReceivingRndBuild
import ice.world.meta.IceStats
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.entities.Units
import mindustry.gen.Building
import mindustry.gen.Groups
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.ui.Bar

/**
 * 无人机配送终端 - 用于生成和管理无人机，并将物品配送到接收站
 */
class DroneDeliveryTerminal(name: String) : RangeBlock(name) {
    // 无人机相关配置
    var unitBuildTime = 60f * 8f  // 生成单个无人机所需时间
    var unitSize = 3              // 最大无人机数量

    // 多边形装饰配置
    var polyStroke = 1.8f         // 多边形线条宽度
    var polyRadius = 8f           // 多边形半径
    var polySides = 6             // 多边形边数
    var polyRotateSpeed = 1f      // 多边形旋转速度
    var polyColor: Color = Pal.accent // 多边形颜色

    // 无人机类型
    val unit = IUnitTypes.和弦

    init {
        size = 3
        health = 300
        range = 20 * 8f
        update = true
        hasItems = true
        itemCapacity = 1000
        buildType = Prov(::DroneDeliveryTerminalBuild)
        consumeItems(ItemStack(Items.copper, 1))
        requirements(Category.distribution, ItemStack.with(IItems.铬铁矿, 10))
    }

    override fun setBars() {
        super.setBars()
        addBar("units") { build: DroneDeliveryTerminalBuild ->
            Bar(
                { "${IceStats.单位数量.localizedName} :${build.units.size}/$unitSize" },
                { Pal.power },
                { build.units.size / unitSize.toFloat() }
            )
        }
        addBar("productionProgress") { build: DroneDeliveryTerminalBuild ->
            Bar(
                IceStats.生产进度::localizedName,
                Pal::power,
                build::buildProgress
            )
        }
    }

    inner class DroneDeliveryTerminalBuild : RangeBlockBuild() {
        // 接收站和无人机集合
        val builds = Seq<DroneReceivingRndBuild>()
        val units = Seq<Unit>(Unit::class.java)

        // 状态变量
        var warmup = 0f
        var buildProgress = 0f
        var totalProgress = 0f
        var readyness = 0f

        // 读取保存数据时的临时存储
        private val readUnit = Seq<Int>()
        override fun draw() {
            super.draw()
            if (units.size == unitSize) {
                /**
                 * 绘制激活状态的多边形装饰
                 */
                Draw.z(Layer.bullet - 0.01f)
                Draw.color(polyColor)
                Lines.stroke(polyStroke * readyness)
                Lines.poly(x, y, polySides, polyRadius, Time.time * polyRotateSpeed)
                Draw.reset()
                Draw.z(Layer.block)
            } else {
                /**
                 * 绘制建造进度
                 */
                Draw.draw(Layer.blockOver) {
                    // TODO: 确保外观正常
                    Drawf.construct(this, IUnitTypes.和弦.fullIcon, 0f, buildProgress, warmup, totalProgress)
                }
            }
        }

        override fun drawSelect() {
            super.drawSelect()
            drawDronePaths()
        }

        /**
         * 绘制无人机路径
         */
        private fun drawDronePaths() {
            units.forEach { unit ->
                val ai = unit.controller() as CarryTaskAI
                ai.getTask()?.let { task ->
                    Drawf.dashLine(blockColor, unit.x, unit.y, task.target.x, task.target.y)
                }
            }
        }

        override fun acceptItem(source: Building, item: Item?): Boolean {
            return items[item] < getMaximumAccepted(item)
        }

        override fun updateTile() {
            updateNearbyBuildings()
            updateStateVariables()
            if (shouldBuildUnit()) {
                updateUnitProduction()
            }
            if (efficiency > 0) {
                assignDroneTasks()
            }
        }

        /**
         * 更新附近的接收站
         */
        private fun updateNearbyBuildings() {
            builds.clear()
            units.remove { it.dead() }
            Units.nearbyBuildings(x, y, range) {
                if (it is DroneReceivingRndBuild) {
                    builds.addUnique(it)
                    it.building = this
                }
            }
        }

        /**
         * 更新状态变量
         */
        private fun updateStateVariables() {
            readyness = Mathf.approachDelta(readyness, if (units.isEmpty) 0f else 1f, 1f / 60f)
            warmup = Mathf.approachDelta(warmup, efficiency, 1f / 60f)
        }

        /**
         * 判断是否应该建造新无人机
         */
        private fun shouldBuildUnit() = units.size < unitSize && efficiency > 0

        /**
         * 更新无人机生产进度
         */
        private fun updateUnitProduction() {
            buildProgress += edelta() / unitBuildTime
            totalProgress += edelta()
            if (buildProgress >= 1f) {
                createNewDrone()
            }
        }

        /**
         * 创建新无人机
         */
        private fun createNewDrone() {
            if (Vars.net.client()) return
            val newUnit = unit.create(team)
            units.add(newUnit)
            (newUnit.controller() as CarryTaskAI).source = this
            newUnit.set(x, y)
            newUnit.rotation = 90f
            newUnit.add()
            Fx.spawn.at(x, y)
            buildProgress = 0f
        }
        /**
         * 为空闲无人机分配任务
         */
        private fun assignDroneTasks() {
            units.forEach { unit ->
                val ai = unit.controller() as CarryTaskAI
                if (ai.hasTask()) return@forEach
                val target = builds.random() ?: return@forEach
                val sortItem = target.sortItem ?: return@forEach

                if (!canTransferToTarget(target, sortItem, unit)) return@forEach
                val availableAmount = items[sortItem]
                if (availableAmount <= 0) return@forEach
                val transferAmount = calculateTransferAmount(availableAmount, unit)
                ai.addTask(CarryTaskAI.ItemTask(this, target, ItemStack(sortItem, transferAmount)))
            }
        }

        /**
         * 检查是否可以向目标转移物品
         */
        private fun canTransferToTarget(target: DroneReceivingRndBuild, item: Item, unit: Unit): Boolean {
            return target.items.get(item) <= unit.itemCapacity()
        }

        /**
         * 计算转移物品数量
         */
        private fun calculateTransferAmount(availableAmount: Int, unit: Unit): Int {
            return if (availableAmount <= unit.itemCapacity()) availableAmount else unit.itemCapacity()
        }

        override fun afterReadAll() {
            readUnit.forEach { unitId ->
                Groups.unit.getByID(unitId)?.also { unit ->
                    units.addUnique(unit)
                    (unit.controller() as CarryTaskAI).source = this
                }
            }
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            val size = read.i()
            (1..size).forEach { _ ->
                val unitId = read.i()
                readUnit.add(unitId)
            }
        }

        override fun write(write: Writes) {
            super.write(write)
            write.i(units.size)
            units.forEach { unit ->
                write.i(unit.id)
            }
        }

        override fun remove() {
            units.forEach(Unit::kill)
            super.remove()
        }

    }
}