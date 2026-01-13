package ice.world.content.blocks.effect

import arc.func.Floatc
import arc.func.Floatp
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.Label
import arc.scene.ui.Slider
import arc.scene.ui.layout.Table
import arc.struct.Seq
import arc.util.Time
import arc.util.Tmp
import arc.util.noise.Simplex
import ice.content.IItems
import ice.world.SglFx
import ice.world.content.blocks.crafting.GenericCrafter
import mindustry.Vars
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.world.Tile
import singularity.graphic.Distortion
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import kotlin.math.abs

open class Noise2dBlock(name: String) : GenericCrafter(name) {
    init {
        buildType = Prov(::Noise2dBuild)
        size = 1
        update = true
        health=10
        configurable = true
        buildType= Prov(::Noise2dBuild)
        craftTime=180f
        outputItems(IItems.钴锭,1)
        consumeItems(IItems.红冰,2)
    }

    inner class Noise2dBuild : GenericCrafterBuild() {
        val tiles = Seq<Tile>()
        var scl: Float = 40f
        var threshold: Float = 0.5f
        var octaves: Float = 3f
        var falloff: Float = 0.5f
        var tilt: Float = 0f


        val dist: Distortion = Distortion()
        val taskID: Int = SglDraw.nextTaskID()
        val timeId: Int = timers++

        fun workEfficiency(): Float{
            return  efficiency
        }

        val param: FloatArray = FloatArray(9)
        override fun draw() {
            super.draw()
          /*  tiles.forEach {
                Fill.rect(it.drawx(), it.drawy(), 8f, 8f)
            }*/


            val e = this
            Draw.z(Layer.effect)
            Draw.color(Pal.reactorPurple)
            Lines.stroke(0.4f * e.workEfficiency())
            Lines.square(e.x, e.y, 3 + Mathf.random(-0.15f, 0.15f))
            Lines.square(e.x, e.y, 4 + Mathf.random(-0.15f, 0.15f), 45f)

            Draw.z(Layer.flyingUnit + 0.5f)
            dist.setStrength(-32 * e.workEfficiency() * Vars.renderer.scale)
            SglDraw.drawDistortion(taskID, e, dist) { b ->
                Distortion.drawVoidDistortion(b.x, b.y, 24 + Mathf.absin(6f, 4f), 32 * b.workEfficiency())
            }

            SglDraw.drawBloomUponFlyUnit(e) { b ->
                Draw.color(Pal.reactorPurple)
                Lines.stroke(3 * b.workEfficiency())
                Lines.circle(b.x, b.y, 24 + Mathf.absin(6f, 4f))

                for (p in Geometry.d4) {
                    Tmp.v1.set(p.x.toFloat(), p.y.toFloat()).scl(28 + Mathf.absin(6f, 4f)).rotate(Time.time * 0.6f)
                    Draw.rect(
                        (SglDrawConst.matrixArrow as TextureRegionDrawable).getRegion(), b.x + Tmp.v1.x, b.y + Tmp.v1.y, 8 * b.workEfficiency(), 8 * b.workEfficiency(), Tmp.v1.angle() + 90
                    )

                    Tmp.v2.set(p.x.toFloat(), p.y.toFloat()).scl(24 + Mathf.absin(6f, 4f)).rotate(Time.time * 0.6f + 45)
                    Drawf.tri(b.x + Tmp.v2.x, b.y + Tmp.v2.y, 4 * b.workEfficiency(), 4f, Tmp.v2.angle())
                    Drawf.tri(b.x + Tmp.v2.x, b.y + Tmp.v2.y, 3 * b.workEfficiency(), 3f, Tmp.v2.angle() + 180)
                }
                Draw.reset()
            }

            if (e.timer(timeId, 15 / e.workEfficiency())) {
                SglFx.ploymerGravityField.at(e.x, e.y, 24 + Mathf.absin(6f, 4f), Pal.reactorPurple, e)
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
                slider.value = fp.get()
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