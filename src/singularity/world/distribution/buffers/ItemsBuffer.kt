package singularity.world.distribution.buffers

import arc.Core
import arc.func.Boolf2
import arc.graphics.Color
import arc.graphics.g2d.TextureRegion
import arc.struct.IntMap
import arc.util.Nullable
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.content.Items
import mindustry.gen.Building
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.modules.ItemModule
import singularity.world.blocks.distribute.TargetConfigure
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.DistributeNetwork
import singularity.world.distribution.GridChildType
import kotlin.math.min

class ItemsBuffer : BaseBuffer<ItemStack, Item, ItemsBuffer.ItemPacket>() {
    private val tmp: ItemPacket = ItemPacket(Items.copper, 0)

    fun put(item: Item?, amount: Int) {
        tmp.obj!!.item = item
        tmp.obj!!.amount = amount
        put(tmp)
    }

    fun remove(item: Item?, amount: Int) {
        tmp.obj!!.item = item
        tmp.obj!!.amount = amount
        remove(tmp)
    }

    override fun remainingCapacity(): Int {
        return super.remainingCapacity().toInt()
    }

    override fun maxCapacity(): Int {
        return super.maxCapacity().toInt()
    }

    override fun bufferType(): DistBufferType<ItemsBuffer?>? {
        return DistBufferType.itemBuffer
    }

    fun remove(item: Item) {
        remove(item.id.toInt())
    }

    fun get(item: Item): Int {
        val p = get<ItemPacket?>(item.id.toInt())
        return if (p != null) p.amount() else 0
    }

    override fun deReadFlow(ct: Item?, amount: Number) {
        tmp.obj!!.item = ct
        tmp.obj!!.amount = amount.toInt()
        deReadFlow(tmp)
    }

    override fun dePutFlow(ct: Item?, amount: Number) {
        tmp.obj!!.item = ct
        tmp.obj!!.amount = amount.toInt()
        dePutFlow(tmp)
    }

    override fun usedCapacity(): Int? {
        return super.usedCapacity() as Int?
    }

    override fun bufferContAssign(network: DistributeNetwork) {
        itemRead@ for (packet in this) {
            val handler = network.core!!.building
            for (grid in network.grids) {
                for (entry in grid!!.get<Building?>(
                    GridChildType.container,
                    Boolf2 { e: Building?, c: TargetConfigure? -> e!!.acceptItem(handler, packet.get()) && c!!.get(GridChildType.container, packet.get()!!) })) {
                    if (packet.amount() <= 0) continue@itemRead
                    val amount = min(packet.amount(), entry.entity!!.acceptStack(packet.get(), packet.amount(), handler))
                    if (amount <= 0f) continue

                    packet.remove(amount)
                    packet.deRead(amount)
                    entry.entity.handleStack(packet.get(), amount, handler)
                }
            }
        }
    }

    override fun bufferContAssign(network: DistributeNetwork, ct: Item) {
        bufferContAssign(network, ct, get(ct))
    }

    override fun bufferContAssign(network: DistributeNetwork, ct: Item, amount: Number): Int {
        return bufferContAssign(network, ct, amount, false)
    }

    override fun bufferContAssign(network: DistributeNetwork, ct: Item, amount: Number, deFlow: Boolean): Int {
        var counter = amount.toInt()
        val core = network.core!!.building
        val packet = get<ItemPacket?>(ct.id.toInt())
        if (packet == null) return counter
        for (grid in network.grids) {
            for (entry in grid!!.get<Building?>(GridChildType.container, Boolf2 { e: Building?, c: TargetConfigure? ->
                c!!.get(GridChildType.container, ct)
                        && e!!.acceptItem(core, ct)
            })) {
                var move = min(packet.amount(), entry.entity!!.acceptStack(packet.get(), packet.amount(), core))
                move = min(move, counter)
                if (move <= 0) continue

                packet.remove(move)
                packet.deRead(move)
                counter -= move
                entry.entity!!.handleStack(packet.get(), move, core)
                if (deFlow) entry.entity.items.handleFlow(packet.get(), -move)
            }
        }

        return counter
    }

    override fun generateBindModule(): ItemModule {
        return BufferItemModule()
    }

    override fun localization(): String? {
        return Core.bundle.get("misc.item")
    }

    override fun displayColor(): Color? {
        return Pal.accent
    }

    inner class ItemPacket(item: Item?, amount: Int) : Packet<ItemStack?, Item?>() {
        init {
            obj = ItemStack(item, amount)
            putCaching += amount
        }

        override fun id(): Int {
            return obj!!.item.id.toInt()
        }

        override fun get(): Item? {
            return obj!!.item
        }

        override fun color(): Color? {
            return obj!!.item.color
        }

        override fun localization(): String? {
            return obj!!.item.localizedName
        }

        override fun icon(): TextureRegion? {
            return obj!!.item.fullIcon
        }

        override fun occupation(): Int {
            return obj!!.amount * bufferType()!!.unit()
        }

        override fun amount(): Int {
            return obj!!.amount
        }

        override fun setZero() {
            readCaching += occupation()
            obj!!.amount = 0
        }

        override fun merge(other: Packet<ItemStack?, Item?>) {
            if (other.id() == id()) {
                obj!!.amount += other.obj!!.amount
                putCaching += other.occupation()
            }
        }

        fun put(amount: Int) {
            tmp.obj!!.item = obj!!.item
            tmp.obj!!.amount = amount
            this@ItemsBuffer.put(tmp)
        }

        fun remove(amount: Int) {
            tmp.obj!!.item = obj!!.item
            tmp.obj!!.amount = amount
            this@ItemsBuffer.remove(tmp)
        }

        override fun remove(other: Packet<ItemStack?, Item?>) {
            if (other.id() == id()) {
                obj!!.amount -= other.obj!!.amount
                readCaching += other.occupation()
            }
        }

        fun deRead(amount: Int) {
            tmp.obj!!.item = obj!!.item
            tmp.obj!!.amount = amount
            this@ItemsBuffer.deReadFlow(tmp)
        }

        fun dePut(amount: Int) {
            tmp.obj!!.item = obj!!.item
            tmp.obj!!.amount = amount
            this@ItemsBuffer.dePutFlow(tmp)
        }

        override fun copy(): Packet<ItemStack?, Item?> {
            return ItemPacket(obj!!.item, obj!!.amount)
        }
    }

    inner class BufferItemModule : ItemModule() {
        override fun add(items: ItemModule) {
            items.each(ItemConsumer { item: Item?, amount: Int -> this@ItemsBuffer.put(item, amount) })
        }

        override fun add(item: Item?, amount: Int) {
            this@ItemsBuffer.put(item, amount)
        }

        override fun get(id: Int): Int {
            return get(Vars.content.item(id))
        }

        override fun get(item: Item): Int {
            return this@ItemsBuffer.get(item)
        }

        override fun remove(item: Item?, amount: Int) {
            this@ItemsBuffer.remove(item, amount)
        }

        override fun set(item: Item?, amount: Int) {
            this@ItemsBuffer.set(ItemPacket(item, amount))
        }

        override fun set(other: ItemModule) {
            other.each(ItemConsumer { item: Item?, amount: Int -> this.set(item, amount) })
        }

        override fun total(): Int {
            return this@ItemsBuffer.usedCapacity()!!
        }

        override fun empty(): Boolean {
            return total() == 0
        }

        override fun any(): Boolean {
            return total() > 0
        }

        @Nullable
        override fun first(): Item? {
            for (i in items.indices) {
                if (get(i) > 0) {
                    return Vars.content.item(i)
                }
            }
            return null
        }

        @Nullable
        override fun take(): Item? {
            for (i in items.indices) {
                var index = (i + takeRotation)
                if (index >= items.size) index -= items.size
                if (get(index) > 0) {
                    val item = Vars.content.item(index)
                    remove(item, 1)
                    takeRotation = index + 1
                    return item
                }
            }
            return null
        }

        override fun each(cons: ItemConsumer) {
            for (packet in this@ItemsBuffer) {
                cons.accept(packet.get(), packet.amount())
            }
        }

        override fun sum(calc: ItemCalculator): Float {
            var sum = 0f
            for (packet in this@ItemsBuffer) {
                sum += calc.get(packet.get(), packet.amount())
            }
            return sum
        }

        override fun read(read: Reads, l: Boolean) {
            memory = IntMap<ItemPacket?>()
            used = 0
            val length = if (l) read.ub() else read.s().toInt()
            for (i in 0..<length) {
                val id = if (l) read.ub() else read.s().toInt()
                val amount = read.i()
                put(Vars.content.item(id), amount)
            }
        }

        override fun write(write: Writes) {
            write.s(memory.size)
            for (value in memory.values()) {
                write.s(value.id())
                write.i(value.amount())
            }
        }
    }
}