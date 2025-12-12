package ice.world.content.blocks.liquid

import arc.Core
import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.struct.Seq
import arc.util.Eachable
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import ice.world.draw.DrawAnyLiquidTile
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.world.blocks.production.Pump
import mindustry.world.draw.*

class PumpChamber(name: String?) : Pump(name) {
    var arr: Array<TextureRegion?> = arrayOfNulls(4)
    var drawers: DrawBlock = DrawMulti(DrawRegion("-bottom"), object : DrawAnyLiquidTile() {
        init {
            padBottom = 2f
            padTop = 2f
            padLeft = 2f
            padRight = 2f
        }
    }, object : DrawDefault() {
        override fun draw(build: Building) {
            val building = build as IcetopmBuild
            val v = building.liquids[building.liquidDrop]
            val i = building.i
            if (v + 1 >= liquidCapacity) {
                if (building.man or (i == 3)) {
                    building.man = true
                    Draw.rect(arr[3], building.x, building.y, build.drawrot())
                } else {
                    extracted(build, i)
                }
            } else {
                extracted(build, i)
            }
        }

        private fun extracted(build: Building, i: Int) {
            Draw.rect(arr[i], build.x, build.y, build.drawrot())
        }
    })

    init {
        liquidCapacity = 30f
        squareSprite = false
        buildType = Prov { IcetopmBuild() }
        update = true
    }

    override fun load() {
        arr[0] = null
        arr[1] = TextureRegion(Core.atlas.find(name))
        arr[2] = TextureRegion(Core.atlas.find(name + 2))
        arr[3] = TextureRegion(Core.atlas.find(name + 3))
        drawers.load(this)
        super.load()
    }

    override fun drawPlanRegion(plan: BuildPlan, list: Eachable<BuildPlan>) {
        drawers.drawPlan(this, plan, list)
    }

    override fun getRegionsToOutline(out: Seq<TextureRegion>) {
        drawers.getRegionsToOutline(this, out)
    }

    override fun icons(): Array<TextureRegion> {
        return drawers.finalIcons(this)
    }

    inner class IcetopmBuild : PumpBuild() {
        var time: Float = 0f
        var i: Int = 1
        var fi: Boolean = false
        var man: Boolean = false

        override fun drawLight() {
            super.drawLight()
            drawers.drawLight(this)
        }


        override fun write(write: Writes) {
            super.write(write)
            write.f(time)
            write.i(i)
            write.bool(fi)
            write.bool(man)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            time = read.f()
            i = read.i()
            fi = read.bool()
            man = read.bool()
        }

        override fun draw() {
            drawers.draw(this)
        }

        override fun updateTile() {
            if (liquids[liquidDrop] <= liquidCapacity - 0.01) {
                man = false
            }
            if (time / 60 >= 0.5) {
                time = 0f
                if (i >= 3) {
                    fi = true
                } else if (i <= 1) {
                    fi = false
                }
                if (fi) {
                    i--
                } else {
                    i++
                }
            } else {
                time += Time.delta
            }
            super.updateTile()
        }
    }
}
