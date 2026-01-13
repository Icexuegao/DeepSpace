package singularity.world.blocks.distribute

import arc.func.Boolf
import arc.math.Mathf
import arc.struct.ObjectMap
import arc.struct.OrderedSet
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.ctype.Content
import mindustry.ctype.ContentType
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.world.Block
import mindustry.world.modules.ItemModule
import mindustry.world.modules.ItemModule.ItemConsumer
import mindustry.world.modules.LiquidModule
import mindustry.world.modules.LiquidModule.LiquidConsumer
import singularity.world.blocks.distribute.matrixGrid.RequestHandlers.*
import singularity.world.components.distnet.DistMatrixUnitBuildComp
import singularity.world.distribution.DistBufferType
import singularity.world.distribution.GridChildType
import kotlin.math.min

/**非content类，方块标记，不进入contents，用于创建矩阵网络IO接口点的标记类型 */
open class GenericIOPoint(name: String) : IOPoint(name) {
    init {
        size = 1
        hasLiquids = true
        hasItems = hasLiquids
        displayFlow = false

        outputsLiquid = true
        outputItems = outputsLiquid

        allowConfigInventory = false

        itemCapacity = 16
        liquidCapacity = 16f
    }

    public override fun setupRequestFact() {
        //items
        setFactory(GridChildType.output, ContentType.item, ReadItemRequestHandler())
        setFactory(GridChildType.input, ContentType.item, PutItemRequestHandler())
        setFactory(GridChildType.acceptor, ContentType.item, AcceptItemRequestHandler())
        //liquids
        setFactory(GridChildType.output, ContentType.liquid, ReadLiquidRequestHandler())
        setFactory(GridChildType.input, ContentType.liquid, PutLiquidRequestHandler())
        setFactory(GridChildType.acceptor, ContentType.liquid, AcceptLiquidRequestHandler())
    }

    override var requestFactories=ObjectMap<GridChildType, ObjectMap<ContentType, RequestHandler<*>>>()
    override var configTypes= OrderedSet<GridChildType>()

    override var supportContentType=  OrderedSet<ContentType>()

    inner class GenericIOPPointBuild : IOPointBuild() {
        var outItems: ItemModule? = null
        var outLiquid: LiquidModule? = null
        protected var siphoning: Boolean = false

        public override fun create(block: Block?, team: Team?): Building? {
            super.create(block, team)
            outItems = ItemModule()
            outLiquid = LiquidModule()
            return this
        }

        fun output(item: Item?, amount: Int): Int {
            val add = min(amount, itemCapacity - outItems!!.get(item))
            outItems!!.add(item, add)
            return add
        }

        fun output(liquid: Liquid?, amount: Float): Float {
            val add = min(amount, liquidCapacity - outLiquid!!.get(liquid))
            outLiquid!!.add(liquid, add)
            return add
        }

        override fun valid(unit: DistMatrixUnitBuildComp?, type: GridChildType?, content: Content?): Boolean {
            if (config == null) return false

            if (content is Item) {
                return config!!.get(GridChildType.output, content) && acceptItemOut(unit!!.building, content)
            } else if (content is Liquid) {
                return config!!.get(GridChildType.output, content) && acceptLiquidOut(unit!!.building, content)
            }

            return false
        }

        public override fun resourcesDump() {
            if (config == null) return
            for (item in config!!.get(GridChildType.output, ContentType.item)) {
                dump(item as Item?)
            }
            for (liquid in config!!.get(GridChildType.output, ContentType.liquid)) {
                dumpLiquid(liquid as Liquid?)
            }
        }

        override fun dump(toDump: Item?): Boolean {
            if (config == null || !block.hasItems || outItems!!.total() == 0 || (toDump != null && !outItems!!.has(toDump))) return false
            if (proximity.size == 0) return false
            var other: Building?

            if (toDump == null) {
                for (ii in 0..<Vars.content.items().size) {
                    val item = Vars.content.item(ii)

                    other = getNext(
                        "dumpItem",
                        Boolf { e: Building? ->
                            e!!.interactable(team)
                                    && outItems!!.has(item)
                                    && e.acceptItem(this, item)
                                    && canDump(e, item)
                                    && config!!.directValid(GridChildType.output, item, getDirectBit(e))
                        })
                    if (other != null) {
                        other.handleItem(this, item)
                        outItems!!.remove(item, 1)
                        incrementDump(proximity.size)
                        return true
                    }
                }
            } else {
                other = getNext(
                    "dumpItem",
                    Boolf { e: Building? ->
                        e!!.interactable(team)
                                && outItems!!.has(toDump)
                                && e.acceptItem(this, toDump)
                                && canDump(e, toDump)
                                && config!!.directValid(GridChildType.output, toDump, getDirectBit(e))
                    })
                if (other != null) {
                    other.handleItem(this, toDump)
                    outItems!!.remove(toDump, 1)
                    incrementDump(proximity.size)
                    return true
                }
            }
            return false
        }

        override fun dumpLiquid(liquid: Liquid, scaling: Float) {
            val dump = this.cdump

            if (config == null || outLiquid!!.get(liquid) <= 0.0001f) return
            if (!Vars.net.client() && Vars.state.isCampaign() && team === Vars.state.rules.defaultTeam) liquid.unlock()

            for (i in 0..<proximity.size) {
                incrementDump(proximity.size)
                var other = proximity.get((i + dump) % proximity.size)
                other = other!!.getLiquidDestination(this, liquid)

                if (other != null && other.interactable(team) && other.block.hasLiquids && canDumpLiquid(other, liquid) && other.liquids != null && config!!.directValid(GridChildType.output, liquid, getDirectBit(other))) {
                    val ofract = other.liquids.get(liquid) / other.block.liquidCapacity
                    val fract = outLiquid!!.get(liquid) / block.liquidCapacity

                    if (ofract < fract) outputLiquid(other, (fract - ofract) * block.liquidCapacity / scaling, liquid)
                }
            }
        }

        fun outputLiquid(next: Building, amount: Float, liquid: Liquid?): Float {
            val flow = min(next.block.liquidCapacity - next.liquids.get(liquid), amount)
            if (next.acceptLiquid(this, liquid)) {
                next.handleLiquid(this, liquid, flow)
                outLiquid!!.remove(liquid, flow)
            }

            return flow
        }

        public override fun resourcesSiphon() {
            siphoning = true
            if (config == null) return
            for (item in config!!.get(GridChildType.input, ContentType.item)) {
                siphonItem(item as Item?)
            }
            for (liquid in config!!.get(GridChildType.input, ContentType.liquid)) {
                siphonLiquid(liquid as Liquid?)
            }
            siphoning = false
        }

        public override fun transBack() {
            if (config == null) return
            val parentBuild = parentMat!!.building
            val itsB = parentMat!!.getBuffer(DistBufferType.itemBuffer)
            val lisB = parentMat!!.getBuffer(DistBufferType.liquidBuffer)

            items.each(ItemConsumer { item: Item?, amount: Int ->
                val move = if (parentBuild.acceptItem(this, item)) min(itsB!!.remainingCapacity(), amount) else 0
                if (move > 0) {
                    items.remove(item, move)
                    itsB!!.put(item, move)
                    itsB.dePutFlow(item, move)
                }
            })

            liquids.each(LiquidConsumer { liquid: Liquid, amount: Float ->
                val move = if (parentBuild.acceptLiquid(this, liquid)) min(lisB!!.remainingCapacity(), amount) else 0f
                if (move > 0) {
                    liquids.remove(liquid, move)
                    lisB!!.put(liquid, move)
                    lisB.dePutFlow(liquid, move)
                }
            })

            outItems!!.each(ItemConsumer { item: Item?, amount: Int ->
                if (config!!.get(GridChildType.output, ContentType.item).contains(item)) return@ItemConsumer
                val move = if (parentBuild.acceptItem(this, item)) min(itsB!!.remainingCapacity(), amount) else 0
                if (move > 0) {
                    outItems!!.remove(item, move)
                    itsB!!.put(item, move)
                    itsB.dePutFlow(item, move)
                }
            })

            outLiquid!!.each(LiquidConsumer { liquid: Liquid, amount: Float ->
                if (config!!.get(GridChildType.output, ContentType.liquid).contains(liquid)) return@LiquidConsumer
                val move = if (parentBuild.acceptLiquid(this, liquid)) min(lisB!!.remainingCapacity(), amount) else 0f
                if (move > 0) {
                    outLiquid!!.remove(liquid, move)
                    lisB!!.put(liquid, move)
                    lisB.dePutFlow(liquid, move)
                }
            })
        }

        override fun moveLiquid(next: Building?, liquid: Liquid?): Float {
            var next = next
            if (next != null) {
                next = next.getLiquidDestination(this, liquid)
                if (next.interactable(this.team) && next.block.hasLiquids && this.liquids.get(liquid) > 0) {
                    val ofract = next.liquids.get(liquid) / next.block.liquidCapacity
                    val fract = this.liquids.get(liquid) / this.block.liquidCapacity * this.block.liquidPressure
                    var flow = min(Mathf.clamp(fract - ofract) * this.block.liquidCapacity, this.liquids.get(liquid))
                    flow = min(flow, next.block.liquidCapacity - next.liquids.get(liquid))
                    if (flow > 0.0f && ofract <= fract && next.acceptLiquid(this, liquid)) {
                        next.handleLiquid(this, liquid, flow)
                        outLiquid!!.remove(liquid, flow)
                        return flow
                    }
                }
            }
            return 0f
        }

        fun siphonItem(item: Item?) {
            if (config == null) return
            val other: Building?
            other = getNext(
                "siphonItem",
                Boolf { e: Building? ->
                    e!!.block.hasItems
                            && e.items.has(item)
                            && config!!.directValid(GridChildType.input, item, getDirectBit(e))
                })
            if (other == null || !interactable(other.team) || !acceptItem(other, item)) return
            other.removeStack(item, 1)
            handleItem(other, item)
        }

        fun siphonLiquid(liquid: Liquid?) {
            if (config == null) return
            val other: Building?
            other = getNext(
                "siphonLiquid",
                Boolf { e: Building? ->
                    e!!.block.hasLiquids
                            && e.liquids.get(liquid) > 0 && config!!.directValid(GridChildType.input, liquid, getDirectBit(e))
                })
            if (other == null || !acceptLiquid(other, liquid)) return
            other.moveLiquid(this, liquid)
        }

        public override fun acceptItem(source: Building, item: Item?): Boolean {
            if (siphoning) return super.acceptItem(source, item)
            return config != null && config!!.directValid(GridChildType.acceptor, item, getDirectBit(source))
                    && config!!.get(GridChildType.acceptor, ContentType.item).contains(item)
                    && super.acceptItem(source, item)
        }

        fun acceptItemOut(source: Building, item: Item?): Boolean {
            return interactable(source.team) && outItems!!.get(item) < itemCapacity
        }

        public override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
            if (siphoning) return super.acceptLiquid(source, liquid)
            return config != null && config!!.directValid(GridChildType.acceptor, liquid, getDirectBit(source))
                    && config!!.get(GridChildType.acceptor, ContentType.liquid).contains(liquid)
                    && super.acceptLiquid(source, liquid)
        }

        fun acceptLiquidOut(source: Building, liquid: Liquid?): Boolean {
            return interactable(source.team) && outLiquid!!.get(liquid) < liquidCapacity
        }

        override fun write(write: Writes) {
            super.write(write)
            outItems!!.write(write)
            outLiquid!!.write(write)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            outItems!!.read(read)
            outLiquid!!.read(read)

            if (revision < 3) {
                if (read.i() > 0) {
                    config = TargetConfigure()
                    config!!.read(read)
                }
            }
        }
    }
}