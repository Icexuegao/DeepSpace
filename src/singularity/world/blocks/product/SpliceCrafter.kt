package singularity.world.blocks.product

import arc.Core
import arc.func.*
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.struct.IntSet
import arc.struct.ObjectSet
import arc.struct.Seq
import arc.util.Strings
import arc.util.io.Reads
import arc.util.io.Writes
import ice.library.struct.AttachedProperty
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.gen.Tex
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.ui.Bar
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.modules.ItemModule
import mindustry.world.modules.ItemModule.ItemConsumer
import mindustry.world.modules.LiquidModule
import mindustry.world.modules.LiquidModule.LiquidConsumer
import singularity.world.modules.SglConsumeModule
import singularity.world.modules.SglLiquidModule
import universecore.components.blockcomp.*
import universecore.world.blocks.chains.ChainsContainer
import universecore.world.blocks.modules.BaseProductModule
import universecore.world.blocks.modules.ChainsModule
import universecore.world.consumers.ConsumeLiquidBase
import universecore.world.consumers.ConsumeType
import universecore.world.producers.ProduceLiquids
import universecore.world.producers.ProducePower
import universecore.world.producers.ProduceType
import kotlin.math.min

open class SpliceCrafter(name: String) : NormalCrafter(name), SpliceBlockComp {
    override var maxChainsWidth: Int = 10
    override var maxChainsHeight: Int = 10
    var structUpdated: Cons<SpliceCrafterBuild?>? = null
    override var interCorner: Boolean = false
    override var negativeSplice: Boolean = false
    var tempItemCapacity: Int = 0
    var tempLiquidCapacity: Float = 0f

    override fun init() {
        super.init()
        tempItemCapacity = itemCapacity
        tempLiquidCapacity = liquidCapacity

        for (consumer in consumers) {
            for (cons in consumer.all()) {
                val old: Floatf<ConsumerBuildComp>? = cons.consMultiplier as Floatf<ConsumerBuildComp>?
                cons.setMultiple(if (old == null) Floatf { e: SpliceCrafterBuild -> e!!.chains!!.container.all.size.toFloat() } else Floatf { e: SpliceCrafterBuild -> old!!.get(e) * e!!.chains!!.container.all.size })
            }
        }
        for (consumer in optionalCons) {
            for (cons in consumer.all()) {
                val old: Floatf<ConsumerBuildComp>? = cons.consMultiplier as Floatf<ConsumerBuildComp>?
                cons.setMultiple<SpliceCrafterBuild?>(if (old == null) Floatf { e: SpliceCrafterBuild? -> e!!.chains!!.container.all.size.toFloat() } else Floatf { e: SpliceCrafterBuild? -> old.get(e) * e!!.chains!!.container.all.size })
            }
        }
        for (producer in producers) {
            for (prod in producer.all()) {
                val old: Floatf<ProducerBuildComp>? = prod.prodMultiplier as Floatf<ProducerBuildComp>?
                prod.setMultiple(if (old == null) Floatf { e: SpliceCrafterBuild -> e.chains!!.container.all.size.toFloat() } else Floatf { e: SpliceCrafterBuild -> old.get(e) * e.chains!!.container.all.size })
            }
        }
        buildType = Prov(::SpliceCrafterBuild)
    }

    override fun chainable(other: ChainsBlockComp): Boolean {
        return this === other
    }

    open inner class SpliceCrafterBuild : NormalCrafterBuild(), SpliceBuildComp {
        var highlight: Boolean = false
        var b: SpliceCrafterBuild = this
        var handling: Boolean = false
        var updateModule: Boolean = true
        var firstInit: Boolean = true
        override var splice: Int = 0
        override var chains = ChainsModule(this)
        override var loadingInvalidPos = IntSet()
        var ChainsModule.consumer by AttachedProperty(SpliceConsumeModule(this))
        var ChainsModule.producer by AttachedProperty(SpliceProduceModule(this))
        var ChainsModule.curr by AttachedProperty(this)
        var ChainsModule.items by AttachedProperty(SpliceItemModule(itemCapacity, firstInit))
        var ChainsModule.liquids by AttachedProperty(SpliceLiquidModule(tempLiquidCapacity, firstInit))
        var ChainsContainer.consumer by AttachedProperty(SpliceConsumeModule(this))
        var ChainsContainer.producer by AttachedProperty(SpliceProduceModule(this))
        var ChainsContainer.curr by AttachedProperty(this)
        var ChainsContainer.items by AttachedProperty(SpliceItemModule(itemCapacity, firstInit))
        var ChainsContainer.liquids by AttachedProperty(SpliceLiquidModule(tempLiquidCapacity, firstInit))

        override fun items(): SpliceItemModule? {
            return items as SpliceItemModule?
        }

        override fun liquids(): SpliceLiquidModule? {
            return liquids as SpliceLiquidModule?
        }

        override fun create(block: Block?, team: Team?): NormalCrafterBuild? {
            super.create(block, team)
            chains = ChainsModule(this)

            if (hasItems) items = SpliceItemModule(itemCapacity, true)
            if (hasLiquids) liquids = SpliceLiquidModule(liquidCapacity, true)
            consumer = SpliceConsumeModule(this)
            producer = SpliceProduceModule(this)

            return this
        }

        public override fun init(tile: Tile?, team: Team?, shouldAdd: Boolean, rotation: Int): Building? {
            super.init(tile, team, shouldAdd, rotation)
            chains!!.newContainer()
            return this
        }

        override fun onProximityUpdate() {
            super.onProximityUpdate()
            updateModule = true
            updateRegionBit()
        }

        override fun write(write: Writes) {
            super.write(write)
            writeChains(write)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            readChains(read)
        }

        override fun onProximityRemoved() {
            super.onProximityRemoved()
            onChainsRemoved()
        }

        override fun onProximityAdded() {
            super.onProximityAdded()
            onChainsAdded()
        }

        override fun displayBars(bars: Table) {
            if (recipeCurrent != -1 && producer!!.current != null && block.hasPower && block.outputsPower && producer!!.current!!.get<ProducePower<*>>(ProduceType.power) != null) {
                val bar = (Func { entity: Building? ->
                    Bar(
                        Prov { Core.bundle.format("bar.poweroutput", Strings.fixed(entity!!.getPowerProduction() * 60 * entity.timeScale(), 1)) },
                        Prov { Pal.powerBar },
                        Floatp { powerProdEfficiency }
                    )
                })
                bars.add<Bar?>(bar.get(this)).growX()
                bars.row()
            }
            //显示流体存储量
            if (hasLiquids && displayLiquid) updateDisplayLiquid()
            if (!displayLiquids.isEmpty()) {
                bars.table(Tex.buttonTrans, Cons { t: Table? ->
                    t!!.defaults().growX().height(18f).pad(4f)
                    t.top().add(liquidsStr).padTop(0f)
                    t.row()
                    for (stack in displayLiquids) {
                        val bar = Func { entity: Building? ->
                            Bar(
                                Prov { stack.liquid.localizedName },
                                Prov { if (stack.liquid.barColor != null) stack.liquid.barColor else stack.liquid.color },
                                Floatp { min(entity!!.liquids.get(stack.liquid) / liquids()!!.allCapacity, 1f) }
                            )
                        }
                        t.add<Bar?>(bar.get(this)).growX()
                        t.row()
                    }
                }).height((26 * displayLiquids.size + 40).toFloat())
                bars.row()
            }

            if (recipeCurrent == -1 || consumer!!.current == null) return

            if (hasPower && consPower != null) {
                val buffered = consPower.buffered
                val capacity = consPower.capacity
                val bar = (Func { entity: Building? ->
                    Bar(
                        Prov { if (buffered) Core.bundle.format("bar.poweramount", if ((entity!!.power.status * capacity).isNaN()) "<ERROR>" else (entity.power.status * capacity).toInt()) else Core.bundle.get("bar.power") },
                        Prov { Pal.powerBar },
                        Floatp { if (Mathf.zero(consPower.requestedPower(entity)) && entity!!.power.graph.getPowerProduced() + entity.power.graph.getBatteryStored() > 0f) 1f else entity!!.power.status })
                })
                bars.add<Bar?>(bar.get(this)).growX()
                bars.row()
            }
            val cl = consumer!!.current!!.get<ConsumeLiquidBase<*>>(ConsumeType.liquid)
            if (cl != null) {
                bars.table(Tex.buttonEdge1, Cons { t: Table? -> t!!.left().add(Core.bundle.get("fragment.bars.consume")).pad(4f) }).pad(0f).height(38f).padTop(4f)
                bars.row()
                bars.table(Cons { t: Table? ->
                    t!!.defaults().grow().margin(0f)
                    t.table(Tex.pane2, Cons { liquid: Table? ->
                        liquid!!.defaults().growX().margin(0f).pad(4f).height(18f)
                        liquid.left().add(Core.bundle.get("misc.liquid")).color(Pal.gray)
                        liquid.row()
                        for (stack in cl.consLiquids!!) {
                            val bar = (Func { entity: Building? ->
                                Bar(
                                    Prov { stack.liquid.localizedName },
                                    Prov { if (stack.liquid.barColor != null) stack.liquid.barColor else stack.liquid.color },
                                    Floatp { min(entity!!.liquids.get(stack.liquid) / liquids()!!.allCapacity, 1f) }
                                )
                            })
                            liquid.add<Bar?>(bar.get(this))
                            liquid.row()
                        }
                    })
                }).height((46 + cl.consLiquids!!.size * 26).toFloat()).padBottom(0f).padTop(2f)
            }

            bars.row()

            if (recipeCurrent == -1 || producer!!.current == null) return
            val pl = producer!!.current!!.get<ProduceLiquids<*>>(ProduceType.liquid)
            if (pl != null) {
                bars.table(Tex.buttonEdge1, Cons { t: Table? -> t!!.left().add(Core.bundle.get("fragment.bars.product")).pad(4f) }).pad(0f).height(38f)
                bars.row()
                bars.table(Cons { t: Table? ->
                    t!!.defaults().grow().margin(0f)
                    t.table(Tex.pane2, Cons { liquid: Table? ->
                        liquid!!.defaults().growX().margin(0f).pad(4f).height(18f)
                        liquid.add(Core.bundle.get("misc.liquid")).color(Pal.gray)
                        liquid.row()
                        for (stack in pl.liquids) {
                            val bar = (Func { entity: Building? ->
                                Bar(
                                    Prov { stack.liquid.localizedName },
                                    Prov { if (stack.liquid.barColor != null) stack.liquid.barColor else stack.liquid.color },
                                    Floatp { min(entity!!.liquids.get(stack.liquid) / liquids()!!.allCapacity, 1f) }
                                )
                            })
                            liquid.add<Bar?>(bar.get(this))
                            liquid.row()
                        }
                    })
                }).height((46 + pl.liquids.size * 26).toFloat()).padTop(2f)
            }
        }

        override fun update() {
            if (hasItems) itemCapacity = items()!!.allCapacity
            if (hasLiquids) liquidCapacity = liquids()!!.allCapacity
            super.update()
            itemCapacity = tempItemCapacity
            liquidCapacity = tempLiquidCapacity
        }

        public override fun updateTile() {
            chains!!.container.update()
            if (updateModule) {
                if (hasItems) {
                    val tItems: SpliceItemModule = chains!!.items
                    if (!tItems.loaded) {
                        tItems.set(items as SpliceItemModule?)
                        tItems.loaded = true
                    }
                    if (items !== tItems) items = tItems
                }
                if (hasLiquids) {
                    val tLiquids: SpliceLiquidModule = chains!!.liquids
                    if (!tLiquids.loaded) {
                        tLiquids.set((liquids as singularity.world.blocks.product.SpliceCrafter.SpliceLiquidModule?)!!)
                        tLiquids.loaded = true
                    }
                    if (liquids !== tLiquids) liquids = tLiquids
                }
                val tCons: SpliceConsumeModule = chains!!.consumer
                if (!tCons.loaded) {
                    tCons.set(consumer as SpliceConsumeModule?)
                    tCons.loaded = true
                }
                if (consumer !== tCons) {
                    consumer = tCons
                }
                b = chains!!.curr
                val tProd: SpliceProduceModule = chains!!.producer
                if (!tProd.loaded) {
                    tProd.set(producer as SpliceProduceModule?)
                    tProd.loaded = true
                }
                if (producer !== tProd) producer = tProd

                splice = splice

                updateModule = false
            }

            super.updateTile()

            if (producer!!.entity !== this) producer!!.doDump(this)

            handling = false
        }

        override fun getLiquidDestination(from: Building?, liquid: Liquid?): Building? {
            liquidCapacity = liquids()!!.allCapacity
            handling = true
            return super.getLiquidDestination(from, liquid)
        }

        public override fun acceptItem(source: Building, item: Item?): Boolean {
            return source.interactable(this.team) && hasItems && !(source is ChainsBuildComp && chains!!.container.all.contains(source as ChainsBuildComp)) && consFilter!!.filter(b, ConsumeType.item, item, acceptAll(ConsumeType.item)) && items.get(item) < items()!!.allCapacity
        }

        public override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
            return source.interactable(this.team) && hasLiquids && !(source is ChainsBuildComp && chains!!.container.all.contains(source as ChainsBuildComp)) && consFilter!!.filter(b, ConsumeType.liquid, liquid, acceptAll(ConsumeType.liquid)) && liquids.get(liquid) <= liquids()!!.allCapacity - 0.0001f
        }

        public override fun draw() {
            if (hasItems) itemCapacity = items()!!.allCapacity
            if (hasLiquids) liquidCapacity = liquids()!!.allCapacity
            super.draw()
            itemCapacity = tempItemCapacity
            liquidCapacity = tempLiquidCapacity
        }

        public override fun drawStatus() {
            if (this.block.enableDrawStatus && consumers.size > 0 && chains!!.BUILD === this) {
                val multiplier = if (block.size > 1 || chains!!.container.all.size > 1) 1.0f else 0.64f
                val brcx = this.tile.drawx() + (this.block.size * 8).toFloat() / 2.0f - 8 * multiplier / 2
                val brcy = this.tile.drawy() - (this.block.size * 8).toFloat() / 2.0f + 8 * multiplier / 2
                Draw.z(71.0f)
                Draw.color(Pal.gray)
                Fill.square(brcx, brcy, 2.5f * multiplier, 45.0f)
                Draw.color(status()!!.color)
                Fill.square(brcx, brcy, 1.5f * multiplier, 45.0f)
                Draw.color()
            }
        }

        override fun containerCreated(old: ChainsContainer?) {
            chains!!.container.consumer = SpliceConsumeModule(this)
            chains!!.container.curr = this
            chains!!.container.producer = SpliceProduceModule(this)

            if (hasItems) chains!!.container.items = SpliceItemModule(itemCapacity, firstInit)
            if (hasLiquids) chains!!.container.liquids = SpliceLiquidModule(tempLiquidCapacity, firstInit)

            chains!!.container.BUILD = this
            if (firstInit) firstInit = false
        }

        override fun chainsAdded(old: ChainsContainer) {
            if (old === chains.container) return
            if (block.hasItems) chains.container.items.add(old.items)
            if (block.hasLiquids) chains.container.liquids.add(old.liquids)
            val statDisplay: SpliceCrafterBuild
            if ((chains.container.BUILD.also { statDisplay = it }) !== this) {
                if (statDisplay.y >= building.y && statDisplay.x <= building.x) chains.container.BUILD = this
            }

            updateModule = true
        }

        override fun chainsRemoved(children: Seq<ChainsBuildComp>) {
            val items: SpliceItemModule = chains!!.items
            val liquids: SpliceLiquidModule = chains!!.liquids
            val targetBlock: SpliceCrafter = getBlock(SpliceCrafter::class.java)
            val handled = ObjectSet<ChainsContainer>()
            var total = 0

            for (child in children) {
                if (handled.add(child.chains.container)) total += child.chains.container.all.size
            }

            for (otherContainer in handled) {
                val present = otherContainer.all.size.toFloat() / total
                val oItems: SpliceItemModule = otherContainer.items
                val oLiquids: SpliceLiquidModule = otherContainer.liquids

                if (targetBlock.hasItems) {
                    oItems.allCapacity = ((items.allCapacity - block.itemCapacity) * present).toInt()
                    oItems.clear()
                    val totalPre = items.total().toFloat() / items.allCapacity
                    items.each(ItemConsumer { item: Item?, amount: Int ->
                        val pre = amount.toFloat() / items.total()
                        oItems.set(item, ((items.total() - targetBlock.itemCapacity * totalPre) * present * pre).toInt())
                    })
                }

                if (targetBlock.hasLiquids) {
                    oLiquids.allCapacity = (liquids.allCapacity - targetBlock.tempLiquidCapacity) * present
                    oLiquids.clear()
                    val totalPre = liquids.total() / liquids.allCapacity
                    liquids.each(LiquidConsumer { liquid: Liquid?, amount: kotlin.Float ->
                        val pre = amount / liquids.total()
                        oLiquids.set(liquid, ((liquids.total() - targetBlock.liquidCapacity * totalPre) * present * pre))
                    })
                }
            }
        }

        var ChainsContainer.BUILD by AttachedProperty(this)
        var ChainsModule.BUILD by AttachedProperty(this)

        override fun chainsFlowed(old: ChainsContainer?) {
            val statDisplay: SpliceCrafterBuild
            if ((chains!!.container.BUILD.also { statDisplay = it }) !== this) {
                if (statDisplay.y >= y && statDisplay.x <= building.x) chains!!.container.BUILD = this
            }
            updateModule = true
        }

        override fun onChainsUpdated() {
            if (structUpdated != null) structUpdated!!.get(this)
        }
    }

    class SpliceItemModule(var allCapacity: Int, firstLoad: Boolean) : ItemModule() {
        protected var added: ObjectSet<ItemModule?> = ObjectSet<ItemModule?>()
        var loaded: Boolean
        var lastFrameId: Long = 0

        init {
            loaded = !firstLoad
        }

        fun set(otherModule: SpliceItemModule?) {
            super.set(otherModule)
        }

        fun add(otherModule: SpliceItemModule) {
            if (added.add(otherModule)) {
                super.add(otherModule)
                allCapacity += otherModule.allCapacity
            }
        }

        override fun updateFlow() {
            if (lastFrameId == Core.graphics.getFrameId()) return
            lastFrameId = Core.graphics.getFrameId()

            super.updateFlow()
            added.clear()
        }
    }

    class SpliceLiquidModule(var allCapacity: kotlin.Float, firstLoad: Boolean) : SglLiquidModule() {
        protected var added: ObjectSet<LiquidModule?> = ObjectSet<LiquidModule?>()
        var loaded: Boolean
        var lastFrameId: Long = 0

        init {
            loaded = !firstLoad
        }

        fun set(otherModule: SpliceLiquidModule) {
            otherModule.each(LiquidConsumer { liquid: Liquid?, amount: kotlin.Float -> this.set(liquid, amount) })
        }

        fun add(otherModule: SpliceLiquidModule) {
            if (added.add(otherModule)) {
                otherModule.each(LiquidConsumer { liquid: Liquid?, amount: kotlin.Float -> this.add(liquid, amount) })
                allCapacity += otherModule.allCapacity
            }
        }

        override fun set(liquid: Liquid?, amount: kotlin.Float) {
            val delta = get(liquid) - amount
            add(liquid, -delta)
        }

        override fun updateFlow() {
            if (lastFrameId == Core.graphics.getFrameId()) return
            lastFrameId = Core.graphics.getFrameId()

            super.updateFlow()
            added.clear()
        }
    }

    class SpliceConsumeModule(entity: ConsumerBuildComp?) : SglConsumeModule(entity) {
        var loaded: Boolean = false
        var lastFrameId: Long = 0

        fun set(module: SpliceConsumeModule?) {}

        public override fun update() {
            if (lastFrameId == Core.graphics.getFrameId()) return
            lastFrameId = Core.graphics.getFrameId()

            super.update()
        }
    }

    class SpliceProduceModule(entity: ProducerBuildComp) : BaseProductModule(entity) {
        var loaded: Boolean = false
        var lastFrameId: Long = 0

        fun set(module: SpliceProduceModule?) {}

        public override fun update() {
            if (lastFrameId == Core.graphics.getFrameId()) return
            lastFrameId = Core.graphics.getFrameId()

            super.update()
        }
    }
}