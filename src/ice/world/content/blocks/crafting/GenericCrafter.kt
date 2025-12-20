package ice.world.content.blocks.crafting

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.struct.EnumSet
import arc.struct.Seq
import arc.util.Eachable
import arc.util.Strings
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import ice.graphics.IceColor
import ice.world.content.blocks.IceBlockComponents.calwavetimeremain
import ice.world.content.blocks.abstractBlocks.IceBlock
import ice.world.meta.IceStats
import mindustry.Vars
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.entities.Units
import mindustry.entities.units.BuildPlan
import mindustry.gen.Iconc
import mindustry.gen.Sounds
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.logic.LAccess
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.type.LiquidStack
import mindustry.type.StatusEffect
import mindustry.ui.Bar
import mindustry.world.Tile
import mindustry.world.blocks.liquid.Conduit.ConduitBuild
import mindustry.world.meta.BlockFlag
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import mindustry.world.meta.StatValues
import kotlin.math.max
import kotlin.math.min

open class GenericCrafter(name: String) : IceBlock(name) {
    var outputItems: Array<ItemStack>? = null
    var outputLiquids: Array<LiquidStack>? = null

    /** Liquid output directions, specified in the same order as outputLiquids. Use -1 to dump in every direction. Rotations are relative to block.  */
    var liquidOutputDirections: IntArray = intArrayOf(-1)

    /** if true, crafters with multiple liquid outputs will dump excess when there's still space for at least one liquid type  */
    var dumpExtraLiquid: Boolean = true
    var ignoreLiquidFullness: Boolean = false
    var craftTime = 80f
    var craftEffect: Effect = Fx.none
    var updateEffect: Effect = Fx.none
    var updateEffectChance = 0.04f
    var updateEffectSpread = 4f
    var warmupSpeed = 0.019f

    /**生产附加效果*/
    var statusEffect: StatusEffect? = null
    var statusTime = 0f
    var radius = 0f

    init {
        sync = true
        solid = true
        update = true
        hasItems = true
        ambientSound = Sounds.loopMachine
        ambientSoundVolume = 0.03f
        flags = EnumSet.of(BlockFlag.factory)
        drawArrow = false
        buildType = Prov(::GenericCrafterBuild)
    }

    override fun setStats() {
        stats.timePeriod = craftTime
        super.setStats()
        if ((hasItems && itemCapacity > 0) || outputItems != null) {
            stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds)
        }

        outputItems?.let { items ->
            stats.add(Stat.output, StatValues.items(craftTime, *items))
        }
        outputLiquids?.let { liquids ->
            stats.add(Stat.output, StatValues.liquids(1f, *liquids))
        }
        statusEffect?.let {
            stats.add(IceStats.状态效果, it.localizedName)
        }
        if (statusTime > 0) stats.add(IceStats.状态持续时间, "$statusTime seconds")
        if (radius > 0) stats.add(IceStats.范围, "[" + radius / 8 + "] T")
    }

    override fun setBars() {
        super.setBars()
        //为液体输出设置液体bar
        outputLiquids?.let {
            //no need for dynamic liquid bar
            removeBar("liquid")
            //then display output buffer
            for (stack in outputLiquids) {
                addLiquidBar(stack.liquid)
            }
        }
        addBar("crafting") { build: GenericCrafterBuild ->
            Bar({
                Iconc.crafting + " " + Strings.fixed(build.progress * 100f, 0) + " %" + calwavetimeremain(
                    build.progress, build.getProgressIncrease(craftTime) * build.timeScale() * 60 / Time.delta)
            }, {
                val ammo = Pal.ammo.cpy()
                ammo.lerp(IceColor.b4, build.progress)
            }, { build.progress })
        }
    }

    fun outputItems(vararg items: Any) {
        outputItems = ItemStack.with(*items)
    }

    fun outputLiquids(vararg liquids: Any) {
        outputLiquids = LiquidStack.with(*liquids)
    }



    override fun rotatedOutput(fromX: Int, fromY: Int, destination: Tile): Boolean {
        if (destination.build !is ConduitBuild) return false
        val crafter = Vars.world.build(fromX, fromY)
        if (crafter == null) return false
        val relative = Mathf.mod(crafter.relativeTo(destination) - crafter.rotation, 4)
        for (dir in liquidOutputDirections) {
            if (dir == -1 || dir == relative) return false
        }
        return true
    }

    override fun init() {
        super.init()
        outputsLiquid = outputLiquids != null
        if (outputItems != null) hasItems = true
        if (outputLiquids != null) hasLiquids = true
    }

    override fun drawPlanRegion(plan: BuildPlan, list: Eachable<BuildPlan>) = drawers.drawPlan(this, plan, list)
    public override fun icons(): Array<TextureRegion> = drawers.finalIcons(this)
    override fun outputsItems() = outputItems != null
    override fun getRegionsToOutline(out: Seq<TextureRegion>) = drawers.getRegionsToOutline(this, out)
    override fun drawOverlay(x: Float, y: Float, rotation: Int) {
        outputLiquids?.let {
            for (i in it.indices) {
                if (liquidOutputDirections.size > i) {
                    val l = liquidOutputDirections[i]
                    if (l<0)continue
                    Draw.rect(
                        outputLiquids!![i].liquid.fullIcon,
                        x + Geometry.d4x(l + rotation) * (size * Vars.tilesize / 2f + 4),
                        y + Geometry.d4y(l + rotation) * (size * Vars.tilesize / 2f + 4),
                        8f, 8f
                    )
                }

            }
        }
    }

    open inner class GenericCrafterBuild() : IceBuild() {
        var progress = 0f
        var totalProgress = 0f
        var warmup = 0f
        override fun shouldConsume(): Boolean {
            if (outputItems != null) {
                for (output in outputItems) {
                    if (items.get(output.item) + output.amount > itemCapacity) {
                        return false
                    }
                }
            }
            if (outputLiquids != null && !ignoreLiquidFullness) {
                var allFull = true
                for (output in outputLiquids) {
                    if (liquids.get(output.liquid) >= liquidCapacity - 0.001f) {
                        if (!dumpExtraLiquid) {
                            return false
                        }
                    } else {
                        //if there's still space left, it's not full for all liquids
                        allFull = false
                    }
                }
                //if there is no space left for any liquid, it can't reproduce
                if (allFull) {
                    return false
                }
            }

            return enabled
        }

        override fun updateTile() {
            if (efficiency > 0) {
                progress += getProgressIncrease(craftTime)
                warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed)
                //continuously output based on efficiency
                if (outputLiquids != null) {
                    val inc = getProgressIncrease(1f)
                    for (output in outputLiquids) {
                        handleLiquid(this, output.liquid,
                            min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)))
                    }
                }

                if (wasVisible && Mathf.chanceDelta(updateEffectChance.toDouble())) {
                    updateEffect.at(x + Mathf.range(size * updateEffectSpread),
                        y + Mathf.range(size * updateEffectSpread))
                }
            } else {
                warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed)
            }
            //TODO may look bad, revert to edelta() if so
            totalProgress += warmup * Time.delta

            if (progress >= 1f) {
                craft()
            }

            dumpOutputs()
        }

        override fun getProgressIncrease(baseTime: Float): Float {
            if (ignoreLiquidFullness) {
                return super.getProgressIncrease(baseTime)
            }
            //limit progress increase by maximum amount of liquid it can produce
            var scaling = 1f
            var max = 1f
            outputLiquids?.let {
                max = 0f
                for (s in it) {
                    val value = (liquidCapacity - liquids.get(s.liquid)) / (s.amount * edelta())
                    scaling = min(scaling, value)
                    max = max(max, value)
                }
            }
            //when dumping excess take the maximum value instead of the minimum.
            return super.getProgressIncrease(baseTime) * (if (dumpExtraLiquid) min(max, 1f) else scaling)
        }

        open fun warmupTarget() = 1f
        override fun warmup() = warmup
        override fun totalProgress() = totalProgress
        fun craft() {
            consume()
            outputItems?.let {
                for (output in it) {
                    (0..<output.amount).forEach { _ ->
                        offload(output.item)
                    }
                }
            }

            if (wasVisible) {
                craftEffect.at(x, y)
            }
            progress %= 1f
            setStatusEffect()
        }

        override fun drawSelect() {
            super.drawSelect()
            if (radius > 0) Drawf.circles(x, y, radius, IceColor.s1)
        }

        fun setStatusEffect() {
            Units.nearby(
                team, x, y, radius
            ) { e: Unit -> e.apply(statusEffect, statusTime * 60) }
        }

        fun dumpOutputs() {
            outputItems?.let {
                if (timer(timerDump, dumpTime / timeScale)) {
                    for (output in it) {
                        dump(output.item)
                    }
                }
            }
            outputLiquids?.let {
                for (i in it.indices) {
                    val dir = if (liquidOutputDirections.size > i) liquidOutputDirections[i] else -1
                    dumpLiquid(it[i].liquid, 2f, dir)
                }
            }

        }

        override fun sense(sensor: LAccess?): Double {
            if (sensor == LAccess.progress) return progress().toDouble()
            //attempt to prevent wild total liquid fluctuation, at least for crafters
            if (sensor == LAccess.totalLiquids) {
                outputLiquids?.let {
                    return liquids.get(it[0].liquid).toDouble()
                }
            }
            return super.sense(sensor)
        }

        override fun progress() = Mathf.clamp(progress)
        override fun getMaximumAccepted(item: Item) = itemCapacity
        override fun shouldAmbientSound(): Boolean = efficiency > 0
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