package ice.world.content.blocks.production

import arc.Core
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.struct.EnumSet
import arc.struct.ObjectFloatMap
import arc.struct.Seq
import arc.util.Strings
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import ice.Ice
import ice.graphics.IceColor
import ice.world.content.blocks.abstractBlocks.IceBlock
import ice.world.draw.DrawCracks
import ice.world.draw.DrawMulti
import ice.world.draw.DrawRegionColor
import ice.world.draw.DrawRim
import ice.world.meta.IceStatValues
import ice.world.meta.IceStats
import mindustry.Vars
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.gen.Sounds
import mindustry.type.Item
import mindustry.type.Liquid
import mindustry.ui.Bar
import mindustry.world.Block
import mindustry.world.Tile
import mindustry.world.blocks.environment.Floor
import mindustry.world.consumers.Consume
import mindustry.world.consumers.ConsumeLiquidBase
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.BlockFlag
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import mindustry.world.meta.StatValues

class IceDrill(name: String) : IceBlock(name) {
    var tier = 3
    var drillTime: Float = 100f
    var hardnessDrillMultiplier = 50f
    var drillMultipliers: ObjectFloatMap<Item> = ObjectFloatMap()
    var updateEffect: Effect = Fx.pulverizeSmall
    var updateEffectChance: Float = 0.02f
    var drillEffect: Effect = Fx.mine
    var drillEffectRnd: Float = -1f
    var blockedItems = Seq<Item>()
    var liquidBoostIntensity = 1.6f
    var warmupSpeed: Float = 0.015f
    var rotator = DrawRegion("-rotator", 3f, true)

    init {
        size = 2
        solid = true
        update = true
        hasItems = true
        itemCapacity = 10
        squareSprite = false
        ambientSound = Sounds.drill
        ambientSoundVolume = 0.018f
        flags = EnumSet.of(BlockFlag.drill)
        buildType = Prov(::IceDrillBuild)
        drawers = DrawMulti(DrawDefault(), DrawCracks(), DrawRim(), rotator, DrawRegionColor<IceDrillBuild>("-item") {
            it.dominantColor()
        })
    }

    override fun init() {
        super.init()
        if (drillEffectRnd < 0) drillEffectRnd = size.toFloat()
    }

    override fun setStats() {
        super.setStats()
        stats.add(IceStats.钻探等级, IceStatValues.funString { "$tier" })
        stats.add(Stat.drillSpeed, 60f / drillTime * size * size, StatUnit.itemsSecond)
        stats.add(
            Stat.drillTier, IceStatValues.drillables(
                drillTime, hardnessDrillMultiplier, (size * size).toFloat(), drillMultipliers
            ) { b: Block ->
                b.minfo.mod == Ice.mod && b is Floor && !b.wallOre && b.itemDrop != null && b.itemDrop.hardness <= tier && (!blockedItems.contains(
                    b.itemDrop
                ))
            })
        if (liquidBoostIntensity != 1f) {
            val bool = findConsumer<Consume> { f: Consume -> f is ConsumeLiquidBase && f.booster }
            if (bool is ConsumeLiquidBase) {
                stats.remove(Stat.booster)
                stats.add(
                    Stat.booster, StatValues.speedBoosters(
                        "{0}" + StatUnit.timesSpeed.localized(), bool.amount, liquidBoostIntensity * liquidBoostIntensity, false
                    ) { liquid: Liquid? -> bool.consumes(liquid) })
            }
        }
    }

    override fun canPlaceOn(tile: Tile, team: Team?, rotation: Int): Boolean {
        if (isMultiblock) {
            for (other in tile.getLinkedTilesAs(this, tempTiles)) {
                if (canMine(other)) {
                    return true
                }
            }
            return false
        } else {
            return canMine(tile)
        }
    }

    fun canMine(tile: Tile?): Boolean {
        if (tile == null || tile.block().isStatic) return false
        val drops = tile.drop()
        return drops != null && drops.hardness <= tier
    }

    override fun setBars() {
        super.setBars()
        addBar("drillspeed") { build: IceDrillBuild ->
            val time = build.getDrillTime(build.dominantItem)
            Bar({
                Core.bundle.format("bar.drillspeed", Strings.fixed(60 / time * build.warmup() * build.timeScale(), 2))
            }, { build.dominantColor() }, { build.warmup() })
        }
    }

    inner class IceDrillBuild : IceBuild() {
        var warmup = 0f
        var progress = 0f
        val tiles = Seq<Tile>()
        var dominantItem: Item? = null
        var totalProgress = 0f
        override fun init(tile: Tile, team: Team, shouldAdd: Boolean, rotation: Int): Building {
            return super.init(tile, team, shouldAdd, rotation).apply {
                tile.getLinkedTilesAs(block, tiles)
                dominantItem = tiles.select(::canMine).random()?.drop()
            }
        }

        fun getDrillTime(item: Item?): Float {
            if (item == null) return 0f
            return (drillTime + hardnessDrillMultiplier * item.hardness) / drillMultipliers.get(
                item, 1f
            ) / tiles.select { it.drop() == dominantItem }.size
        }

        var lastColor = IceColor.b4
        fun dominantColor(): Color {
            return dominantItem?.color ?: lastColor
        }

        override fun drawSelect() {
            val dx = x - size * Vars.tilesize / 2f
            val dy = y + size * Vars.tilesize / 2f
            val s = Vars.iconSmall / 4f
            Draw.mixcol(Color.darkGray, 1f)
            dominantItem?.let {
                Draw.rect(it.fullIcon, dx, dy - 1, s, s)
                Draw.reset()
                Draw.rect(it.fullIcon, dx, dy, s, s)
            }
        }

        override fun updateTile() {
            if (dominantItem == null) return
            dump()
            totalProgress += warmup * Time.delta
            if (items.total() < itemCapacity && efficiency > 0) {
                progress += getProgressIncrease(getDrillTime(dominantItem))
                warmup = Mathf.approachDelta(warmup, 1f, warmupSpeed)
                if (Mathf.chanceDelta((updateEffectChance * warmup).toDouble())) updateEffect.at(
                    x + Mathf.range(size * 2f), y + Mathf.range(size * 2f)
                )
            } else {
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed)
            }

            if (progress >= 1) {
                dumpMine()
            }
        }

        fun dumpMine() {
            items.add(dominantItem, 1)
            drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem!!.color)
            progress %= 1f

            dominantItem = tiles.select(::canMine).random().drop()
            lastColor = dominantItem!!.color
        }

        override fun warmup() = warmup

        override fun progress() = progress

        override fun totalProgress() = totalProgress

        override fun write(write: Writes) {
            super.write(write)
            write.f(progress)
            write.f(warmup)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            progress = read.f()
            warmup = read.f()
        }
    }
}