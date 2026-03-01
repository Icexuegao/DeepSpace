package singularity.world.blocks.liquid

import arc.Core
import arc.func.Floatp
import arc.func.Prov
import arc.math.Mathf
import arc.math.WindowedMean
import arc.scene.ui.layout.Table
import arc.struct.Bits
import arc.util.Interval
import arc.util.Strings
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.content.Fx
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.type.Liquid
import mindustry.ui.Bar
import mindustry.world.Block
import mindustry.world.blocks.liquid.LiquidBlock
import mindustry.world.modules.LiquidModule
import universecore.components.blockcomp.ReplaceBuildComp
import java.util.*
import kotlin.math.max
import kotlin.math.min

open class MultLiquidBlock(name: String?) : LiquidBlock(name) {
    var conduitAmount: Int = 4
    var displayLiquids: Boolean = false

    init {
        displayFlow = false
    }

    override fun setBars() {
        super.setBars()
        removeBar("liquid")
    }
    companion object {
        private var groupName = arrayOfNulls<String>(0)

        fun groupName(i: Int): String {
            if (groupName.size <= i) {
                groupName = groupName.copyOf<String?>(i + 1)
                for (n in groupName.indices) {
                    if (groupName[n] == null) groupName[n] = "liquids#" + n
                }
            }

            return groupName[i]!!
        }
    }
    open inner class MultLiquidBuild : Building(), ReplaceBuildComp {
        var liquidsBuffer: Array<ClusterLiquidModule> = arrayOfNulls<ClusterLiquidModule>(conduitAmount) as Array<ClusterLiquidModule>
        var cacheLiquids: ClusterLiquidModule? = null

        override fun buildingRaw(): Building {
            return this
        }

        protected var current: Int = 0

        fun getModule(liquid: Liquid?): LiquidModule? {
            for (liquids in liquidsBuffer!!) {
                if (liquids.current() === liquid) return liquids
            }
            return null
        }

        open fun getModuleAccept(source: Building?, liquid: Liquid?): LiquidModule? {
            for (liquids in liquidsBuffer!!) {
                if (liquids.current() === liquid && liquids.currentAmount() < liquidCapacity) return liquids
            }
            return null
        }

        val empty: LiquidModule?
            get() {
                for (liquids in liquidsBuffer) {
                    if (liquids.currentAmount() < 0.01f) return liquids
                }
                return null
            }
        val isFull: Boolean
            get() = this.empty == null

        fun anyLiquid(): Boolean {
            for (liquids in liquidsBuffer!!) {
                if (liquids.currentAmount() > 0.01f) return true
            }
            return false
        }

        override fun create(block: Block?, team: Team?): Building {
            super.create(block, team)
            liquidsBuffer = arrayOfNulls<ClusterLiquidModule>(conduitAmount) as Array<ClusterLiquidModule>
            for (i in liquidsBuffer.indices) {
                liquidsBuffer[i] = ClusterLiquidModule()
            }
            liquids = if (liquidsBuffer.size == 0) ClusterLiquidModule() else liquidsBuffer[0]
            cacheLiquids = liquids as ClusterLiquidModule?

            return this
        }

        override fun displayBars(table: Table) {
            super.displayBars(table)
            if (!displayLiquids) return

            for (i in liquidsBuffer!!.indices) {
                val current = liquidsBuffer!![i]
                val fi = i

                table.add<Bar?>(Bar(Prov { if (current.smoothCurrent <= 0.001f) Core.bundle.get("bar.liquid") + " #" + fi else current.current().localizedName + "     " + (if (current.getFlowRate(current.current()) >= 0) Strings.autoFixed(current.getFlowRate(current.current()), 0) else "...") + Core.bundle.get("misc.perSecond") }, Prov { current.current().barColor() }, Floatp { current.smoothCurrent }))
                table.row()
            }
        }

        protected fun updateLiquidsFlow() {
            for (module in liquidsBuffer!!) {
                module.updateFlow()
            }
        }

        override fun updateTile() {
            super.updateTile()

            if (liquidsBuffer.size > 0) {
                liquids = liquidsBuffer[((current + 1) % liquidsBuffer.size).also { current = it }]
                cacheLiquids = liquids as ClusterLiquidModule?
            }

            if (displayLiquids && Vars.ui.hudfrag.blockfrag.hover() === this) {
                updateLiquidsFlow()
            }

            for (module in liquidsBuffer) {
                module.smoothCurrent = Mathf.lerpDelta(module.smoothCurrent, module.currentAmount(), 0.4f)
            }
        }

        open fun conduitAccept(source: MultLiquidBuild, index: Int, liquid: Liquid): Boolean {
            noSleep()
            val liquids: LiquidModule = liquidsBuffer[index]
            return source.interactable(team) && liquids.currentAmount() < 0.01f || liquids.current() === liquid && liquids.currentAmount() < liquidCapacity
        }

       open fun shouldClusterMove(source: MultLiquidBuild): Boolean {
            return source.liquidsBuffer.size == liquidsBuffer.size
        }

        fun handleLiquid(source: MultLiquidBuild?, index: Int, liquid: Liquid, amount: Float) {
            liquidsBuffer[index].add(liquid, amount)
        }

        fun moveLiquid(dest: MultLiquidBuild?, index: Int, liquid: Liquid): Float {
            return moveLiquid(dest, index, index, liquid)
        }

        fun moveLiquid(dest: MultLiquidBuild?, index: Int, otherIndex: Int, liquid: Liquid): Float {
            if (dest == null) return 0f

            if (index >= dest.liquidsBuffer.size || !dest.shouldClusterMove(this)) {
                return moveLiquid(dest, liquid)
            }
            val liquids: LiquidModule = liquidsBuffer[index]
            val oLiquids: LiquidModule = dest.liquidsBuffer[otherIndex]
            if (dest.interactable(team) && liquids.get(liquid) > 0f) {
                val ofract = oLiquids.get(liquid) / dest.block.liquidCapacity
                val fract = liquids.get(liquid) / block.liquidCapacity * block.liquidPressure
                var flow = min(Mathf.clamp((fract - ofract)) * (block.liquidCapacity), liquids.get(liquid))
                flow = min(flow, dest.block.liquidCapacity - oLiquids.get(liquid))

                if (flow > 0f && ofract <= fract && dest.conduitAccept(this, otherIndex, liquid)) {
                    dest.handleLiquid(this, otherIndex, liquid, flow)
                    liquids.remove(liquid, flow)
                    return flow
                } else if (oLiquids.currentAmount() / dest.block.liquidCapacity > 0.1f && fract > 0.1f) {
                    val fx = (x + dest.x) / 2f
                    val fy = (y + dest.y) / 2f
                    val other = oLiquids.current()
                    if ((other.flammability > 0.3f && liquid.temperature > 0.7f) || (liquid.flammability > 0.3f && other.temperature > 0.7f)) {
                        damage(1 * Time.delta)
                        dest.damage(1 * Time.delta)
                        if (Mathf.chance(0.1 * Time.delta)) {
                            Fx.fire.at(fx, fy)
                        }
                    } else if ((liquid.temperature > 0.7f && other.temperature < 0.55f) || (other.temperature > 0.7f && liquid.temperature < 0.55f)) {
                        liquids.remove(liquid, min(liquids.get(liquid), 0.7f * Time.delta))
                        if (Mathf.chance((0.2f * Time.delta).toDouble())) {
                            Fx.steam.at(fx, fy)
                        }
                    }
                }
            }
            return 0f
        }

        override fun handleLiquid(source: Building, liquid: Liquid, amount: Float) {
            var liquids = getModuleAccept(source, liquid)
            if (liquids != null || (this.empty.also { liquids = it }) != null) {
                liquids!!.add(liquid, amount)
            }
        }

        override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
            noSleep()
            return source.interactable(team) && (getModuleAccept(source, liquid) != null || !this.isFull)
        }

        override fun write(write: Writes) {
            super.write(write)
            write.i(liquidsBuffer.size)
            for (liquids in liquidsBuffer) {
                liquids.write(write)
            }
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            liquidsBuffer = (arrayOfNulls<ClusterLiquidModule>(read.i()) as Array<ClusterLiquidModule>?)!!
            for (i in liquidsBuffer!!.indices) {
                val module = ClusterLiquidModule()
                module.read(read)
                liquidsBuffer!![i] = module
            }
        }

        override fun onReplaced(old: ReplaceBuildComp?) {
        }


    }

    class ClusterLiquidModule : LiquidModule() {
        val flowTimer: Interval = Interval(2)
        var cacheFlow: Array<WindowedMean?>?=null
        var cacheSums: FloatArray?=null
        var displayFlow: FloatArray?=null
        var current: Liquid? = Vars.content.liquid(0)
        var flow: Array<WindowedMean?>? = null
        val cacheBits: Bits = Bits()
        val liquids: FloatArray = FloatArray(Vars.content.liquids().size)
        var smoothCurrent: Float = 0f

        override fun updateFlow() {
            if (flowTimer.get(1, 20f)) {
                if (flow == null) {
                    if (cacheFlow == null || cacheFlow!!.size != liquids.size) {
                        cacheFlow = arrayOfNulls<WindowedMean>(liquids.size)
                        for (i in liquids.indices) {
                            cacheFlow!![i] = WindowedMean(3)
                        }
                        cacheSums = FloatArray(liquids.size)
                        displayFlow = FloatArray(liquids.size)
                    } else {
                        for (i in liquids.indices) {
                            cacheFlow!![i]!!.reset()
                        }
                        Arrays.fill(cacheSums, 0f)
                        cacheBits.clear()
                    }

                    Arrays.fill(displayFlow, -1f)

                    flow = cacheFlow
                }
                val updateFlow = flowTimer.get(30f)

                for (i in liquids.indices) {
                    flow!![i]!!.add(cacheSums!![i])
                    if (cacheSums!![i] > 0) {
                        cacheBits.set(i)
                    }
                    cacheSums!![i] = 0f

                    if (updateFlow) {
                        displayFlow!![i] = if (flow!![i]!!.hasEnoughData()) flow!![i]!!.mean() / 20 else -1f
                    }
                }
            }
        }

        override fun stopFlow() {
            flow = null
        }

        override fun getFlowRate(liquid: Liquid): Float {
            return if (flow == null) -1f else displayFlow!![liquid.id.toInt()] * 60
        }

        override fun hasFlowLiquid(liquid: Liquid): Boolean {
            return flow != null && cacheBits.get(liquid.id.toInt())
        }

        override fun current(): Liquid {
            return current!!
        }

        override fun reset(liquid: Liquid, amount: Float) {
            Arrays.fill(liquids, 0f)
            liquids[liquid.id.toInt()] = amount
            current = liquid
        }

        override fun currentAmount(): Float {
            return liquids[current!!.id.toInt()]
        }

        override fun get(liquid: Liquid): Float {
            return liquids[liquid.id.toInt()]
        }

        override fun clear() {
            Arrays.fill(liquids, 0f)
        }

        override fun add(liquid: Liquid, amount: Float) {
            liquids[liquid.id.toInt()] += amount
            current = liquid

            if (flow != null) {
                cacheSums!![liquid.id.toInt()] += max(amount, 0f)
            }
        }

        override fun handleFlow(liquid: Liquid, amount: Float) {
            if (flow != null) {
                cacheSums!![liquid.id.toInt()] += max(amount, 0f)
            }
        }

        override fun remove(liquid: Liquid, amount: Float) {
            //cap to prevent negative removal
            add(liquid, max(-amount, -liquids[liquid.id.toInt()]))
        }

        override fun each(cons: LiquidConsumer) {
            for (i in liquids.indices) {
                if (liquids[i] > 0) {
                    cons.accept(Vars.content.liquid(i), liquids[i])
                }
            }
        }

        override fun sum(calc: LiquidCalculator): Float {
            var sum = 0f
            for (i in liquids.indices) {
                if (liquids[i] > 0) {
                    sum += calc.get(Vars.content.liquid(i), liquids[i])
                }
            }
            return sum
        }

        override fun write(write: Writes) {
            var amount = 0
            for (liquid in liquids) {
                if (liquid > 0) amount++
            }

            write.s(amount) //amount of liquids
            for (i in liquids.indices) {
                if (liquids[i] > 0) {
                    write.s(i) //liquid ID
                    write.f(liquids[i]) //liquid amount
                }
            }
        }

        override fun read(read: Reads, legacy: Boolean) {
            Arrays.fill(liquids, 0f)
            val count = if (legacy) read.ub() else read.s().toInt()

            for (j in 0..<count) {
                val liq = Vars.content.liquid(if (legacy) read.ub() else read.s().toInt())
                val amount = read.f()
                if (liq != null) {
                    val liquidid = liq.id.toInt()
                    liquids[liquidid] = amount
                    if (amount > 0) {
                        current = liq
                    }
                }
            }
        }
    }
}