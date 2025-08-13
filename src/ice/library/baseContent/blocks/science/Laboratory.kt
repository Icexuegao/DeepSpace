package ice.library.baseContent.blocks.science

import arc.func.Prov
import arc.math.Mathf
import arc.util.Strings
import arc.util.Time
import ice.content.IItems
import ice.library.baseContent.blocks.IceBlockComponents.calwavetimeremain
import ice.library.baseContent.blocks.abstractBlocks.IceBlock
import ice.library.draw.drawer.IceDrawMulti
import ice.library.scene.tex.Colors
import mindustry.gen.Iconc
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.ui.Bar
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawGlowRegion
import mindustry.world.draw.DrawRegion

class Laboratory(name: String) : IceBlock(name) {

    init {
        size = 4
        solid = true
        health = 2500
        update = true
        hasPower = true
        canOverdrive = false
        itemCapacity = 20
        buildType = Prov(::LaboratoryBuild)
        requirements(Category.effect, ItemStack.with(IItems.石英玻璃, 50))
        drawers = IceDrawMulti(DrawRegion("-bottom"), DrawDefault(), DrawRegion("-light", 2f, true),
            DrawGlowRegion("-light").apply {
                color = Colors.b4
                rotate = true
                rotateSpeed = 2f
            })
    }

    override fun setBars() {
        super.setBars()
        addBar("crafting") { build: LaboratoryBuild ->
            Bar({
                Iconc.crafting + " " + Strings.fixed(build.progress * 100f, 0) + " %" + calwavetimeremain(
                    build.progress, build.getProgressIncrease(build.craftTime) * build.timeScale() * 60 / Time.delta)
            }, {
                val ammo = Pal.ammo.cpy()
                ammo.lerp(Colors.b4, build.progress)
            }, { build.progress })
        }
    }

    inner class LaboratoryBuild : IceBuild() {
        var progress: Float = 0f
        var totalProgress: Float = 0f
        var warmup: Float = 0f
        var craftTime = 360f
        override fun updateTile() {
            warmup = if (efficiency > 0) {
                progress += getProgressIncrease(craftTime)
                Mathf.approachDelta(warmup, 1f, 0.1f)
            } else {
                Mathf.approachDelta(warmup, 0f, 0.1f)
            }
            totalProgress += warmup * Time.delta

            if (progress >= 1f) {
                craft()
            }
        }

        fun craft() {
            progress %= 1f
        }

        override fun warmup(): Float {
            return warmup
        }

        override fun totalProgress(): Float {
            return totalProgress
        }
    }
}