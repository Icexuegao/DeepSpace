package singularity.world.distribution.buffers

import arc.Core
import arc.func.Boolf2
import arc.graphics.Color
import arc.graphics.g2d.TextureRegion
import arc.math.WindowedMean
import arc.struct.IntMap
import arc.struct.Seq
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.content.Liquids
import mindustry.gen.Building
import mindustry.type.Liquid
import mindustry.type.LiquidStack
import mindustry.world.modules.LiquidModule
import singularity.world.blocks.distribute.TargetConfigure
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.DistributeNetwork
import singularity.world.distribution.GridChildType
import singularity.world.distribution.MatrixGrid.BuildingEntry
import singularity.world.modules.SglLiquidModule
import universecore.util.handler.FieldHandler
import kotlin.math.min

class LiquidsBuffer : BaseBuffer<LiquidsBuffer.LiquidIntegerStack, Liquid, LiquidsBuffer.LiquidPacket>() {
    private val tmp: LiquidPacket = LiquidPacket(Liquids.water, 0f)

    fun put(liquid: Liquid, amount: Float): Float {
        val rem = tmp.obj!!.set(liquid, amount)
        put(tmp)

        return rem
    }

    fun remove(liquid: Liquid, amount: Float): Float {
        val rem = tmp.obj!!.set(liquid, amount)
        remove(tmp)

        return rem
    }

    override fun remainingCapacity(): Float {
        return super.remainingCapacity().toFloat()
    }

    override fun maxCapacity(): Float {
        return super.maxCapacity().toFloat()
    }

    override fun bufferType(): DistBufferType<LiquidsBuffer?>? {
        return DistBufferType.liquidBuffer
    }

    fun remove(liquid: Liquid) {
        remove(liquid.id.toInt())
    }

    fun get(liquid: Liquid): Float {
        val p = get<LiquidPacket?>(liquid.id.toInt())
        return if (p != null) p.obj!!.getAmount() else 0f
    }

    override fun deReadFlow(ct: Liquid, amount: Number) {
        tmp.obj!!.set(ct, amount.toFloat())
        deReadFlow(tmp)
    }

    override fun dePutFlow(ct: Liquid, amount: Number) {
        tmp.obj!!.set(ct, amount.toFloat())
        dePutFlow(tmp)
    }

    override fun bufferContAssign(network: DistributeNetwork) {
        liquidRead@ for (packet in this) {
            for (grid in network.grids) {
                val handler = network.core!!.building
                for (entry in grid!!.get<Building?>(
                    GridChildType.container,
                    Boolf2 { e: Building?, c: TargetConfigure? -> e!!.acceptLiquid(handler, packet.get()) && c!!.get(GridChildType.container, packet.get()) },
                    temp
                )) {
                    if (packet.amount() <= 0.001f) continue@liquidRead
                    var move = min(packet.amount(), entry.entity.block.liquidCapacity - entry.entity.liquids.get(packet.get()))

                    move -= move % LiquidIntegerStack.Companion.packMulti
                    if (move <= 0.001f) continue

                    packet.remove(move)
                    packet.deRead(move)
                    entry.entity.handleLiquid(handler, packet.get(), move)
                }
            }
        }
    }

    override fun bufferContAssign(network: DistributeNetwork, ct: Liquid) {
        bufferContAssign(network, ct, get(ct))
    }

    override fun bufferContAssign(network: DistributeNetwork, ct: Liquid, amount: Number): Float {
        return bufferContAssign(network, ct, amount, false)
    }

    override fun bufferContAssign(network: DistributeNetwork, ct: Liquid, amount: Number, deFlow: Boolean): Float {
        var am = amount.toFloat()
        val packet = get<LiquidPacket?>(ct.id.toInt())
        if (packet == null) return am
        val core = network.core!!.building
        for (grid in network.grids) {
            for (entry in grid!!.get<Building?>(GridChildType.container, Boolf2 { e: Building?, c: TargetConfigure? ->
                c!!.get(GridChildType.container, ct)
                        && e!!.acceptLiquid(core, ct)
            })) {
                val accept = if (!entry.entity.acceptLiquid(core, ct)) 0f else entry.entity.block.liquidCapacity - entry.entity.liquids.get(ct)
                var move = min(packet.amount(), accept)
                move = min(move, am)

                move -= move % LiquidIntegerStack.Companion.packMulti
                if (move <= 0.001f) continue

                packet.remove(move)
                packet.deRead(move)
                am -= move
                entry.entity.handleLiquid(core, packet.get(), move)
                if (deFlow) {
                    val cacheSums = FieldHandler.getValueDefault<FloatArray?>(LiquidModule::class.java, "cacheSums")
                    val flow = FieldHandler.getValueDefault<Array<WindowedMean?>?>(entry.entity.liquids, "flow")
                    if (flow != null) {
                        cacheSums[packet.id()] -= move
                    }
                }
            }
        }

        return am
    }

    override fun generateBindModule(): BufferLiquidModule {
        return BufferLiquidModule()
    }

    override fun localization(): String? {
        return Core.bundle.get("misc.liquid")
    }

    override fun displayColor(): Color? {
        return Liquids.water.color
    }

    class LiquidIntegerStack {
        var liquid: Liquid? = null
        var amount: Int = 0

        constructor()

        constructor(liquid: Liquid, amount: Float) {
            set(liquid, amount)
        }

        fun set(liquid: Liquid, amount: Float): Float {
            val rem: Float
            this.liquid = liquid
            this.amount = ((amount - ((amount % packMulti).also { rem = it })) / packMulti).toInt()

            return rem
        }

        fun getAmount(): Float {
            return amount * packMulti
        }

        fun toStack(): LiquidStack {
            return LiquidStack(liquid, getAmount())
        }

        fun copy(): LiquidIntegerStack {
            return LiquidsBuffer.LiquidIntegerStack(liquid!!, getAmount())
        }

        companion object {
            const val packMulti: Float = 0.25f
        }
    }

    inner class LiquidPacket : Packet<LiquidIntegerStack, Liquid> {
        constructor(liquid: Liquid, amount: Float) {
            obj = LiquidIntegerStack(liquid, amount)
            putCaching = (putCaching + obj!!.getAmount()).toInt()
        }

        constructor(stack: LiquidIntegerStack) {
            obj = stack.copy()
            putCaching = (putCaching + obj!!.getAmount()).toInt()
        }

        fun remove(amount: Float): Float {
            val rem = tmp.obj!!.set(obj!!.liquid!!, amount)
            this@LiquidsBuffer.remove(tmp)

            return rem
        }

        override fun id(): Int {
            return obj!!.liquid!!.id.toInt()
        }

        override fun get(): Liquid {
            return obj!!.liquid!!
        }

        override fun color(): Color? {
            return obj!!.liquid!!.color
        }

        override fun localization(): String? {
            return obj!!.liquid!!.localizedName
        }

        override fun icon(): TextureRegion? {
            return obj!!.liquid!!.fullIcon
        }

        override fun occupation(): Int {
            return obj!!.amount * bufferType()!!.unit()
        }

        override fun amount(): Float {
            return obj!!.getAmount()
        }

        override fun setZero() {
            readCaching += occupation()
            obj!!.amount = 0
        }

        override fun merge(other: Packet<LiquidIntegerStack?, Liquid?>) {
            if (other.id() == id()) {
                obj!!.amount += other.obj!!.amount
                putCaching += other.occupation()
            }
        }

        override fun remove(other: Packet<LiquidIntegerStack?, Liquid?>) {
            if (other.id() == id()) {
                obj!!.amount -= other.obj!!.amount
                readCaching += other.occupation()
            }
        }

        fun deRead(amount: Float) {
            tmp.obj!!.set(obj!!.liquid!!, amount)
            this@LiquidsBuffer.deReadFlow(tmp)
        }

        fun dePut(amount: Float) {
            tmp.obj!!.set(obj!!.liquid!!, amount)
            this@LiquidsBuffer.dePutFlow(tmp)
        }

        override fun copy(): Packet<LiquidIntegerStack, Liquid> {
            return LiquidPacket(obj)
        }
    }

    inner class BufferLiquidModule : SglLiquidModule() {
        var current: Liquid? = null

        override fun current(): Liquid? {
            return current
        }

        override fun currentAmount(): Float {
            return if (current == null) 0f else get(current!!)
        }

        override fun add(liquid: Liquid, amount: Float) {
            if (amount > 0) {
                this@LiquidsBuffer.put(liquid, amount)
            } else {
                this@LiquidsBuffer.remove(liquid, -amount)
            }

            current = liquid
        }

        override fun get(liquid: Liquid): Float {
            return this@LiquidsBuffer.get(liquid)
        }

        override fun total(): Float {
            return this@LiquidsBuffer.usedCapacity().toFloat()
        }

        override fun each(cons: LiquidConsumer) {
            for (packet in this@LiquidsBuffer) {
                cons.accept(packet.get(), packet.amount())
            }
        }

        override fun sum(calc: LiquidCalculator): Float {
            var sum = 0f
            for (packet in this@LiquidsBuffer) {
                sum += calc.get(packet.get(), packet.amount())
            }
            return sum
        }

        override fun read(read: Reads, l: Boolean) {
            memory = IntMap<LiquidPacket?>()
            used = 0
            val length = if (l) read.ub() else read.s().toInt()
            for (i in 0..<length) {
                val id = if (l) read.ub() else read.s().toInt()
                val amount = read.f()
                put(Vars.content.liquid(id), amount)
            }
        }

        override fun write(write: Writes) {
            write.s(memory.size)
            for (value in memory.values()) {
                write.s(value.id())
                write.f(value.amount())
            }
        }
    }

    companion object {
        private val temp = Seq<BuildingEntry<Building?>?>()
    }
}