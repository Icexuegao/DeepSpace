package ice.world.content.blocks.liquid

import arc.Core
import arc.math.Mathf
import arc.util.Time
import ice.world.content.blocks.abstractBlocks.IceBlock
import ice.library.scene.ui.iTable
import mindustry.Vars
import mindustry.content.Fx
import mindustry.content.Liquids
import mindustry.entities.Effect
import mindustry.game.Team
import mindustry.graphics.Pal
import mindustry.type.Liquid
import mindustry.ui.Bar
import mindustry.world.Tile
import mindustry.world.blocks.environment.Floor
import mindustry.world.meta.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class SolidPump(name: String) : IceBlock(name) {
    var result: Liquid = Liquids.water
    var updateEffect: Effect = Fx.none
    var updateEffectChance: Float = 0.02f
    var baseEfficiency: Float = 1f
    var attribute: Attribute? = null
    var pumpAmount: Float = 0.2f
    var consumeTime = 60f * 5f

    init {
        solid = true
        update = true
        hasPower = true
        hasLiquids = true
        outputsLiquid = true
        group = BlockGroup.liquids
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        drawPotentialLinks(x, y)

        if (attribute != null) {
            val a = (sumAttribute(attribute, x, y)) / size / size + percentSolid(x, y) * baseEfficiency
            val args = (max(a, 0f) * 100f).roundToInt()
            drawPlaceText(Core.bundle.format("bar.efficiency", args), x, y, valid)
        }
    }

    override fun setBars() {
        super.setBars()
        addBar("efficiency") { entity: SolidPumpBuild ->
            Bar({
                Core.bundle.formatFloat("bar.pumpspeed", entity.lastPump * 60, 1)
            }, { Pal.ammo }, { entity.warmup * entity.efficiency })
        }
    }

    override fun setStats() {
        super.setStats()
        stats.add(Stat.output, result, 60f * pumpAmount, true)
        if (attribute != null) {
            stats.add(if (baseEfficiency > 0.0001f) Stat.affinities else Stat.tiles,
                blocks(attribute, floating, 1f, baseEfficiency <= 0.001f))

        }
    }

    fun blocks(
        attr: Attribute?, floating: Boolean, scale: Float, startZero: Boolean, checkFloors: Boolean = true
    ): StatValue {
        return StatValue { table ->
            table.iTable { c ->
                c.left()
                c.setRowsize(5)
                val blocks = Vars.content.blocks().select { block ->
                    (!checkFloors || block is Floor) && block.attributes.get(
                        attr) != 0f && !((block is Floor && block.isDeep) && !floating)
                }.with { s ->
                    s.sort {
                        it.attributes.get(attr)
                    }
                }

                if (blocks.any()) {
                    for (block in blocks) {
                        StatValues.blockEfficiency(block, block.attributes.get(attr) * scale, startZero).display(c)
                    }
                } else {
                    c.add("@none.inmap")
                }
            }
        }
    }

    override fun canPlaceOn(tile: Tile, team: Team?, rotation: Int): Boolean {
        val sum = tile.getLinkedTilesAs(this, tempTiles).sumf { t: Tile ->
            if (canPump(t)) baseEfficiency + (if (attribute != null) t.floor().attributes.get(
                attribute) else 0f) else 0f
        }
        return sum > 0.00001f
    }

    override fun outputsItems(): Boolean {
        return false
    }

    fun canPump(tile: Tile?): Boolean {
        return tile != null && !tile.floor().isLiquid
    }

    inner class SolidPumpBuild : IceBuild() {
        var warmup: Float = 0f
        var pumpTime: Float = 0f
        var boost: Float = 0f
        var validTiles: Float = 0f
        var lastPump: Float = 0f
        var liquidDrop: Liquid? = null
        var consTimer: Float = 0f
        var amount: Float = 0f
        override fun drawCracks() {}
        override fun pickedUp() {
            validTiles = 0f
            boost = validTiles
        }

        override fun shouldConsume(): Boolean {
            return liquids.get(result) < liquidCapacity - 0.01f
        }

        override fun updateTile() {
            liquidDrop = result
            val fraction = max(validTiles + boost + (if (attribute == null) 0f else attribute!!.env()), 0f)

            if (efficiency > 0 && typeLiquid() < liquidCapacity - 0.001f) {
                val maxPump = min(liquidCapacity - typeLiquid(), pumpAmount * delta() * fraction * efficiency)
                liquids.add(result, maxPump)
                lastPump = maxPump / Time.delta
                warmup = Mathf.lerpDelta(warmup, 1f, 0.02f)
                if (Mathf.chance((delta() * updateEffectChance).toDouble())) updateEffect.at(x, y)
            } else {
                warmup = Mathf.lerpDelta(warmup, 0f, 0.02f)
                lastPump = 0f
            }

            pumpTime += warmup * edelta()

            dumpLiquid(result)
        }

        override fun onProximityUpdate() {
            super.onProximityAdded()

            boost = sumAttribute(attribute, tile.x.toInt(), tile.y.toInt()) / size / size
            validTiles = 0f
            for (other in tile.getLinkedTiles(tempTiles)) {
                if (canPump(other)) {
                    validTiles += baseEfficiency / (size * size)
                }
            }
        }

        fun typeLiquid(): Float {
            return liquids.get(result)
        }

        override fun warmup(): Float {
            return warmup
        }

        override fun progress(): Float {
            return Mathf.clamp(consTimer / consumeTime)
        }

        override fun totalProgress(): Float {
            return pumpTime
        }
    }

}