package ice.world.content.blocks.effect

import arc.func.Prov
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.util.Time
import arc.util.Tmp
import ice.content.IItems
import mindustry.Vars
import mindustry.gen.Building
import mindustry.graphics.Pal
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Tile
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.Stat
import kotlin.math.abs
import kotlin.math.max

class OrientationProjector(name: String) : LinksBlock(name) {
    var speedBoost=2.5f
    init {
        solid = true
        update = true
        emitLight = true
        lightRadius = 50f
        canOverdrive = false
        configurable = true
        group = BlockGroup.projectors
        buildType = Prov(::OrientationProjectorBuildEnd)
        consumePower(200 / 60f)
        requirements(Category.effect, ItemStack.with(IItems.铪锭, 30))
    }

    override fun outputsItems(): Boolean {
        return false
    }

    override fun setStats() {
        super.setStats()
        stats.add(Stat.speedIncrease, "+" + (speedBoost * 100f - 100) + "%")
    }

    inner class OrientationProjectorBuildEnd : LinksBlockBuildEnd() {
        override fun updateTile() {
            if (efficiency > 0) {
                builds.forEach {
                    if (it.dead()) builds.remove(it)
                    it.applyBoost(speedBoost, 1f)
                }
            }
        }

        override fun addBuild(build: Building): Boolean {
            return super.addBuild(build) && build.block.canOverdrive
        }

        override fun draw() {
            super.draw()
            if (efficiency <= 0) return
            val f = 1f - (Time.time / 100f) % 1f
            Draw.alpha(0.8f)
            Lines.stroke((2f * f + 0.1f) * 1)
            val r = max(0.0, (Mathf.clamp(2f - f * 2f) * size * Vars.tilesize / 2f - f - 0.2f).toDouble()).toFloat()
            val w = Mathf.clamp(0.5f - f) * size * Vars.tilesize
            Lines.beginLine()
            for (i in 0..3) {
                Lines.linePoint(
                    x + Geometry.d4(i).x * r + Geometry.d4(i).y * w, y + Geometry.d4(i).y * r - Geometry.d4(i).x * w
                )
                if (f < 0.5f) Lines.linePoint(
                    x + Geometry.d4(i).x * r - Geometry.d4(i).y * w, y + Geometry.d4(i).y * r + Geometry.d4(i).x * w
                )
            }
            Lines.endLine(true)

            Draw.reset()
        }

        override fun drawInput(other: Tile) {
            Tmp.v2.trns(tile.angleTo(other), 2f)
            val tx = tile.drawx()
            val ty = tile.drawy()
            val ox = other.drawx()
            val oy = other.drawy()
            val alpha = (abs(0 - (Time.time * 2f) % 100f) / 100f)
            val x = Mathf.lerp(tx, ox, alpha)
            val y = Mathf.lerp(ty, oy, alpha)
            fun extracted() {
                Lines.square(this.x, this.y, 2f, 45f)
                Lines.square(ox, oy, 2f, 45f)
                Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y)
            }
            //draw "background"
            Draw.color(Pal.gray)
            Lines.stroke(2.5f)
            extracted()
            //绘制前景色
            Draw.color(blockColor)
            Lines.stroke(1f)

            extracted()

            Draw.mixcol(Draw.getColor(), 1f)
            Draw.color()
            Draw.rect(arrowRegion, x, y, Tmp.v1.set(other).sub(tile).angle())
            Draw.mixcol()
        }
    }
}