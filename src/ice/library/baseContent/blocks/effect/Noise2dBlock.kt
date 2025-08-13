package ice.library.baseContent.blocks.effect

import arc.func.Floatc
import arc.func.Floatp
import arc.func.Prov
import arc.graphics.g2d.Fill
import arc.scene.ui.Label
import arc.scene.ui.Slider
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.noise.Simplex
import ice.library.baseContent.blocks.abstractBlocks.IceBlock
import mindustry.Vars
import mindustry.world.Tile
import kotlin.math.abs

open class Noise2dBlock(name: String) : IceBlock(name) {
    init {
        buildType = Prov(::Noise2dBuild)
        size = 1
        configurable = true
    }

    inner class Noise2dBuild : IceBuild() {
        val tiles = Seq<Tile>()
        var scl: Float = 40f
        var threshold: Float = 0.5f
        var octaves: Float = 3f
        var falloff: Float = 0.5f
        var tilt: Float = 0f
        override fun draw() {
            super.draw()
            tiles.forEach {
                Fill.rect(it.drawx(), it.drawy(), 8f, 8f)
            }
        }

        lateinit var bct: Table
        override fun buildConfiguration(table: Table) {
            super.buildConfiguration(table)
            bct = table
            sliderOption("scale", { scl }, { f: Float ->
                scl = f
                nod()
            }, 1f, 500f)
            sliderOption("threshold", { threshold }, { f: Float ->
                threshold = f
                nod()
            }, 0f, 1f)
            sliderOption("octaves", { octaves }, { f: Float ->
                octaves = f
                nod()
            }, 1f, 10f)
            sliderOption("falloff", { falloff }, { f: Float ->
                falloff = f
                nod()
            }, 0f, 1f)
            sliderOption("tilt", { tilt }, { f: Float ->
                tilt = f
                nod()
            }, -4f, 4f)
        }

        fun sliderOption(name: String, fp: Floatp, fc: Floatc, min: Float, max: Float) {
            val table = Table()
            table.addChild(Label(fp.get().toString()))
            val slider = Slider(min, max, (abs(min) + max) / 10f, false)

            slider.update {
                slider.setValue(fp.get())
            }
            slider.moved(fc)
            table.add(slider)
            bct.add(table).row()
        }

        fun nod() {
            tiles.clear()
            Vars.world.tiles.each { x, y ->
                val noise = noise(x.toFloat(), y + x * tilt, scl, 1f, octaves, falloff)
                if (noise > threshold) tiles.addUnique(Vars.world.tiles[x, y])
            }
        }

        fun noise(x: Float, y: Float, scl: Float, mag: Float, octaves: Float, persistence: Float): Float {
            return Simplex.noise2d(1, octaves.toDouble(), persistence.toDouble(), (1f / scl).toDouble(), (x + 10).toDouble(), (y + 10).toDouble()) * mag
        }
    }
}