package ice.library.type.baseContent.blocks.distribution.droneNetwork

import arc.func.Func
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
import ice.content.IceItems
import ice.library.type.baseContent.blocks.abstractBlocks.RangeBlock
import ice.library.type.baseContent.blocks.distribution.droneNetwork.DroneReceivingRnd.DroneReceivingRndBuild
import ice.library.type.components.BuildInterface
import ice.library.type.meta.stat.IceStats
import mindustry.content.Fx
import mindustry.content.Items
import mindustry.entities.Units
import mindustry.gen.Building
import mindustry.gen.Groups
import mindustry.gen.Unit
import mindustry.gen.UnitEntity
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.UnitType
import mindustry.ui.Bar

class DroneDeliveryTerminal(name: String) : RangeBlock(name) {
    val transfer = object : UnitType("icetransfer") {
        init {
            useUnitCap=false
            armor = 8f
            health = 600f
            speed = 3.5f
            rotateSpeed = 3f
            accel = 0.05f
            drag = 0.017f
            itemCapacity = 30
            lowAltitude = false
            constructor = Prov(UnitEntity::create)
            flying = true
            hitSize = 12f
            controller = Func { CarryTaskAI() }
            isEnemy = false
            allowedInPayloads = false
            logicControllable = false
            playerControllable = false
        }
    }
    var unitBuildTime = 60f * 8f
    var unitSize = 3
    var polyStroke = 1.8f
    var polyRadius = 8f
    var polySides = 6
    var polyRotateSpeed = 1f
    var polyColor: Color = Pal.accent

    init {
        size = 3
        health = 300
        range = 20 * 8f
        update = true
        hasItems = true
        itemCapacity = 1000
        buildType = Prov(::DroneDeliveryTerminalBuild)
        consumeItems(ItemStack(Items.copper, 1))
        requirements(Category.distribution, ItemStack.with(IceItems.铬铁矿, 10))
    }

    override fun setBars() {
        super.setBars()
        addBar("units") { e: DroneDeliveryTerminalBuild ->
            Bar(
                {
                    "${IceStats.单位数量.localizedName} :${e.units.size}/$unitSize"
                },
                { Pal.power },
                {
                    e.units.size / unitSize.toFloat()
                }
            )
        }
        addBar("productionProgress") { e: DroneDeliveryTerminalBuild ->
            Bar(
                {
                    IceStats.生产进度.localizedName
                },
                { Pal.power },
                {
                    e.buildProgress
                }
            )
        }
    }

    inner class DroneDeliveryTerminalBuild : RangeBlockBuild(),
        BuildInterface.BuildSeq, BuildInterface.UnitSeq {
        override val builds = Seq<DroneReceivingRndBuild>()
        override val units = Seq<Unit>(Unit::class.java)
        var warmup = 0f
        var buildProgress = 0f
        var totalProgress = 0f
        var readyness = 0f
        override fun draw() {
            super.draw()
            if (units.size == 3) {
                Draw.z(Layer.bullet - 0.01f)
                Draw.color(polyColor)
                Lines.stroke(polyStroke * readyness)
                Lines.poly(x, y, polySides, polyRadius, Time.time * polyRotateSpeed)
                Draw.reset()
                Draw.z(Layer.block)
            } else {
                Draw.draw(Layer.blockOver) {
                    //TODO make sure it looks proper
                    Drawf.construct(this, transfer.fullIcon, 0f, buildProgress, warmup, totalProgress)
                }
            }
        }

        override fun acceptItem(source: Building, item: Item?): Boolean {
            return items[item] < getMaximumAccepted(item)
        }

        override fun updateTile() {
            readUnit.forEach {
                Groups.unit.getByID(it)?.also { unit ->
                    units.addUnique(unit)
                    (unit.controller() as CarryTaskAI).source = this
                }
            }

            builds.clear()
            Units.nearbyBuildings(x, y, range) {
                if (it is DroneReceivingRndBuild) {
                    builds.addUnique(it)
                    it.building = this
                }
            }
            readyness = Mathf.approachDelta(readyness, if (units.isEmpty) 0f else 1f, 1f / 60f)
            for ((index, unit) in units.withIndex()) {
                if (unit.dead())units.remove(index)
            }
            units.forEach { unit ->
                val carrAI = unit.controller() as CarryTaskAI
                carrAI.task?.also { return@forEach }
                val target = builds.random() ?: return@forEach
                val sortItem = target.sortItem ?: return@forEach
                if (target.items.get(sortItem) > unit.itemCapacity()) return@forEach
                val size = items[sortItem]
                carrAI.addTask(CarryTaskAI.ItemTask(this, target, sortItem,
                    if (size <= unit.itemCapacity()) size else unit.itemCapacity()))
            }
            warmup = Mathf.approachDelta(warmup, efficiency, 1f / 60f)

            if (units.size < unitSize) {
                buildProgress += edelta() / unitBuildTime
                totalProgress += edelta()
                if (buildProgress >= 1f) {
                    val unit = transfer.create(team)
                    units.add(unit)
                    (unit.controller() as CarryTaskAI).source = this
                    unit.set(x, y)
                    unit.rotation = 90f
                    unit.add()
                    Fx.spawn.at(x, y)
                    buildProgress = 0f
                }

            }
        }

        val readUnit = Seq<Int>()
        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            val size = read.i()
            for (i in 0 until size) {
                val i1 = read.i()
                readUnit.add(i1)
            }
        }

        override fun write(write: Writes) {
            val us = units.toArray<Unit>(Unit::class.java)
            val size = units.size
            write.i(size)
            us.forEach {
                write.i(it.id)
            }
            super.write(write)
        }

        override fun drawSelect() {
            super.drawSelect()
            units.forEach {
                (it.controller() as CarryTaskAI).task?.also { itemTask ->
                    val target = itemTask.target
                    Drawf.dashLine(blockColor, it.x, it.y, target.x, target.y)
                }
            }
        }

        override fun remove() {
            units.forEach(Unit::kill)
            super.remove()
            dead(true)
        }

    }
}