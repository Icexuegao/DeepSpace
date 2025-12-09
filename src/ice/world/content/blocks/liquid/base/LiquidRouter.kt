package ice.world.content.blocks.liquid.base

import arc.func.Prov
import ice.world.draw.DrawBuild
import ice.world.draw.DrawRegionNull
import mindustry.gen.Building
import mindustry.type.Liquid
import mindustry.world.draw.DrawDefault

open class LiquidRouter(name: String) : LiquidBlock(name) {
    var liquidPadding: Float = 0f

    init {
        solid = true
        noUpdateDisabled = true
        canOverdrive = false
        floating = true
        buildType= Prov(::LiquidRouterBuild)
        setDrawMulti(DrawRegionNull("-bottom"), DrawBuild<LiquidRouterBuild> {
            if (liquids.currentAmount() > 0.001f) {
                mindustry.world.blocks.liquid.LiquidBlock.drawTiledFrames(size, x, y, liquidPadding, liquids.current(),
                    liquids.currentAmount() / liquidCapacity)
            }
        }, DrawDefault(), DrawRegionNull("-top"))
    }

    open inner class LiquidRouterBuild : LiquidBuild() {
        override fun updateTile() {
            dumpLiquid(liquids.current())
        }

        override fun acceptLiquid(source: Building?, liquid: Liquid?): Boolean {
            return (liquids.current() === liquid || liquids.currentAmount() < 0.2f)
        }
    }
}