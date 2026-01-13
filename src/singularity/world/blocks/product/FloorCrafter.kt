package singularity.world.blocks.product

import arc.Core
import arc.func.Cons
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import mindustry.Vars
import mindustry.game.Team
import mindustry.world.Tile
import singularity.world.components.FloorCrafterBuildComp
import singularity.world.consumers.SglConsumeFloor
import singularity.world.consumers.SglConsumeType
import universecore.world.consumers.BaseConsumers
import universecore.world.producers.BaseProducers

open class FloorCrafter(name: String) : NormalCrafter(name) {
    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        var eff = 0f
        var c = 0
        var line = 0
        val t = Vars.world.tile(x, y)
        if (t != null) {
            for (consumer in consumers) {
                val cons = consumer.get<SglConsumeFloor<*>>(SglConsumeType.floor)
                if (cons != null) {
                    c++
                    eff = cons.getEff(FloorCrafterBuildComp.getFloors(t, this))
                }
            }

            if (c == 0) eff = 1f
            for (boost in boosts) {
                val cons = boost.key!!.get<SglConsumeFloor<*>>(SglConsumeType.floor)
                if (cons != null) {
                    c++
                    eff *= cons.getEff(FloorCrafterBuildComp.getFloors(t, this))
                }
            }

            for (product in optionalProducts) {
                if (!valid && !product.key!!.optionalAlwaysValid) continue
                val cons = product.key!!.get<SglConsumeFloor<*>>(SglConsumeType.floor)
                if (cons != null) {
                    val optEff = cons.getEff(FloorCrafterBuildComp.getFloors(t, this))
                    if (optEff <= 0) continue
                    val ta = buildIconsTable(product)
                    val width = drawPlaceText(
                        Core.bundle.format("bar.efficiency", (optEff * 100f).toInt()),
                        x, y + line, valid
                    )
                    val dx = x * Vars.tilesize + offset - width / 2f
                    val dy = y * Vars.tilesize + offset + size * Vars.tilesize / 2f + 5 - line * 8f
                    ta.setPosition(dx - ta.getWidth() / 8f, dy - ta.getHeight() / 16f)
                    ta.setTransform(true)
                    ta.setScale(1f / 8f)
                    ta.draw()
                    line++
                }
            }
        }

        drawPlaceText(
            if (c == 1 && valid) Core.bundle.format("bar.efficiency", (eff * 100f).toInt()) else if (valid) Core.bundle.get("infos.placeValid") else Core.bundle.get("infos.placeInvalid"),
            x, y + line, valid
        )
    }

    override fun canPlaceOn(tile: Tile, team: Team?, rotation: Int): Boolean {
        var eff = 0f
        var c = 0

        for (product in optionalProducts) {
            val cons = product.key!!.get<SglConsumeFloor<*>>(SglConsumeType.floor)
            if (cons != null) {
                if (product.key!!.optionalAlwaysValid && cons.getEff(FloorCrafterBuildComp.getFloors(tile, this)) > 0) return true
            }
        }

        for (consumer in consumers) {
            val cons = consumer.get<SglConsumeFloor<*>>(SglConsumeType.floor)
            if (cons != null) {
                c++
                eff = cons.getEff(FloorCrafterBuildComp.getFloors(tile, this))
            }
        }

        if (c == 0) eff = 1f
        for (boost in boosts) {
            val cons = boost.key!!.get<SglConsumeFloor<*>>(SglConsumeType.floor)
            if (cons != null) {
                c++
                eff *= cons.getEff(FloorCrafterBuildComp.getFloors(tile, this))
            }
        }

        return c > 0 && eff > 0
    }

    inner class FloorCrafterBuild : NormalCrafterBuild(), FloorCrafterBuildComp
    companion object {
        private val iconsTable = Table()

        private fun buildIconsTable(product: ObjectMap.Entry<BaseConsumers?, BaseProducers?>): Table {
            iconsTable.clear()
            var first = true
            for (produce in product.value!!.all()) {
                if (!produce.hasIcons()) continue

                if (!first) iconsTable.add("+").fillX().pad(4f)
                iconsTable.table(Cons { ca: Table? ->
                    ca!!.defaults().padLeft(3f).fill()
                    produce.buildIcons(ca)
                }).fill()

                first = false
            }
            iconsTable.pack()
            return iconsTable
        }
    }
}