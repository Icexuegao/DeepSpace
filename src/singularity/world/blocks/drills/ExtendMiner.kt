package singularity.world.blocks.drills

import arc.Core
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.struct.IntSet
import arc.struct.Seq
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.entities.Effect
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.Liquid
import mindustry.world.Tile
import mindustry.world.meta.StatValue
import mindustry.world.modules.ItemModule
import singularity.world.blocks.SglBlock
import singularity.world.blocks.drills.ExtendableDrill.ExtendableDrillBuild
import singularity.world.meta.SglStat
import singularity.world.modules.SglLiquidModule
import universecore.components.blockcomp.ChainsBlockComp
import universecore.components.blockcomp.ChainsBuildComp
import universecore.components.blockcomp.SpliceBlockComp
import universecore.components.blockcomp.SpliceBuildComp
import universecore.world.DirEdges
import universecore.world.blocks.modules.ChainsModule

//@Annotations.ImplEntries
open class ExtendMiner(name: String) : SglBlock(name), SpliceBlockComp {
    var master: ExtendableDrill? = null
    override var negativeSplice: Boolean = false
    override var interCorner = false
    var mining: Effect? = null
    var miningEffectChance: Float = 0.02f
    val ores: Seq<ItemStack> = Seq<ItemStack>()
    override val maxChainsWidth: Int
        get() = master!!.maxChainsWidth
    override var maxChainsHeight: Int
        get() = master!!.maxChainsHeight
        set(value) {}

    init {
        update = true
        hasItems = true
        outputItems = true
        buildType = Prov(::ExtendMinerBuild)
    }

    public override fun setStats() {
        super.setStats()
        stats.add(SglStat.componentBelongs, StatValue { t: Table? ->
            t!!.defaults().left()
            t.image(master!!.fullIcon).size(35f).padRight(8f)
            t.add(master!!.localizedName)
        })
        setChainsStats(stats)
    }

    override fun canPlaceOn(tile: Tile, team: Team?, rotate: Int): Boolean {
        if (isMultiblock()) {
            var re = false
            for (other in tile.getLinkedTilesAs(this, tempTiles)) {
                re = re or master!!.canMine(other)
            }
            return re
        } else {
            return master!!.canMine(tile)
        }
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        val tile = Vars.world.tile(x, y)
        if (tile == null) return

        master!!.getMines(tile, this, false, ores)

        if (ores.size > 0) {
            var line = 0
            for (stack in ores) {
                val width = if (stack.item.hardness <= master!!.bitHardness)  //可挖掘的矿物显示
                    drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", 60f / (master!!.drillTime + stack.item.hardness * master!!.hardMultiple) * stack.amount, 2), x, y - line, true) else  //不可挖掘的矿物显示
                    drawPlaceText(Core.bundle.get("bar.drilltierreq"), x, y - line, false)
                val dx = x * Vars.tilesize + offset - width / 2f - 4f
                val dy = y * Vars.tilesize + offset + size * Vars.tilesize / 2f + 5 - line * 8f
                Draw.mixcol(Color.darkGray, 1f)
                Draw.rect(stack.item.uiIcon, dx, dy - 1)
                Draw.reset()
                Draw.rect(stack.item.uiIcon, dx, dy)
                line++
            }
        }
    }

    public override fun init() {
        super.init()
        master!!.validChildType.add(this)
        hasItems = master!!.hasItems
        hasLiquids = master!!.hasLiquids
        itemCapacity = master!!.itemCapacity
        liquidCapacity = master!!.liquidCapacity
    }

    override fun chainable(other: ChainsBlockComp): Boolean {
        return other === this || other === master
    }

    // @Annotations.ImplEntries
    inner class ExtendMinerBuild : SglBuilding(), SpliceBuildComp {
        override var splice: Int = 0
            set(value) {
                field = value
                spliceDirBits = 0
                for (i in 0..3) {
                    if ((splice and (1 shl i * 2)) != 0) spliceDirBits = spliceDirBits or (1 shl i)
                }
            }
        var spliceDirBits: Int = 0
        override var loadingInvalidPos = IntSet()
        override var chains = ChainsModule(this)
        var masterDrill: ExtendableDrillBuild? = null
        var mines: Seq<ItemStack> = Seq<ItemStack>()
        var updateSplice: Boolean = false
        var warmup: Float = 0f

        override fun updateTile() {
            chains.container.update()
            if (masterDrill != null) {
                for (item in masterDrill!!.outputItems) {
                    dump(item.item)
                }
            }

            warmup = Mathf.lerpDelta(warmup, if (masterDrill == null) 0f else masterDrill!!.warmup, master!!.warmupSpeed)

            if (mining != null && Mathf.chanceDelta((miningEffectChance * warmup).toDouble())) {
                mining!!.at(x, y)
            }

            if (updateSplice) {
                updateSplice = false
                splice = getSplice
            }
        }

        public override fun init(tile: Tile?, team: Team?, shouldAdd: Boolean, rotation: Int): Building? {
            super.init(tile, team, shouldAdd, rotation)
            chains = ChainsModule(this)
            chains!!.newContainer()
            return this
        }

        //   @Annotations.EntryBlocked
        override fun onProximityUpdate() {
            super.onProximityUpdate()

            master!!.getMines(tile, block, mines)

            updateSplice = true
        }

        public override fun outputItems(): Seq<Item>? {
            return masterDrill!!.outputItems()
        }

        override fun onChainsUpdated() {
            masterDrill = null
            items = ItemModule()
            liquids = SglLiquidModule()
            for (comp in chains!!.container.all) {
                if (comp is ExtendableDrillBuild && comp.block === master) {
                    if (masterDrill != null) {
                        masterDrill = null
                        break
                    }
                    masterDrill = comp
                    items = masterDrill!!.items
                    liquids = masterDrill!!.liquids
                }
            }
        }

        public override fun acceptItem(source: Building, item: Item?): Boolean {
            return masterDrill != null && masterDrill!!.acceptItem(source, item)
        }

        public override fun acceptLiquid(source: Building, liquid: Liquid?): Boolean {
            return masterDrill != null && masterDrill!!.acceptLiquid(source, liquid)
        }

        override fun canChain(other: ChainsBuildComp): Boolean {
            if (!super.canChain(other)) return false
            var oth: Building? = null
            t@ for (i in 0..3) {
                if (oth != null) break
                for (point in DirEdges.get(size, i)) {
                    if (oth == null) {
                        oth = nearby(point.x, point.y)
                        if (oth !== other) {
                            oth = null
                            continue@t
                        }
                    } else if (oth !== nearby(point.x, point.y)) {
                        oth = null
                        break@t
                    }
                }
            }
            return oth != null
        }

        override fun onProximityAdded() {
            super.onProximityAdded()
            onChainsAdded()
        }

        override fun onProximityRemoved() {
            super.onProximityRemoved()
            onChainsRemoved()
        }

        override fun write(write: Writes) {
            super.write(write)
            write.f(warmup)
            writeChains(write)
        }

        public override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            warmup = read.f()
            readChains(read)
        }
    }
}