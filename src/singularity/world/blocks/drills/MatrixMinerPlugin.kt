package singularity.world.blocks.drills

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.math.Rand
import arc.util.Time
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.game.Team
import mindustry.graphics.Layer
import mindustry.world.Edges
import mindustry.world.Tile
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import singularity.graphic.MathRenderer
import singularity.graphic.SglDrawConst
import singularity.world.blocks.SglBlock
import singularity.world.blocks.drills.MatrixMiner.MatrixMinerBuild
import singularity.world.meta.SglStat

abstract class MatrixMinerPlugin(name: String) : SglBlock(name) {
    var range: Int = 0
    var drillSize: Int = 0
    var pierceBuild: Boolean = false
    var energyMulti: Float = 1f
    var drillMoveMulti: Float = 1f
    var warmupSpeed: Float = 0.02f

    public override fun setStats() {
        super.setStats()
        if (range > 0) stats.add(Stat.range, range.toString() + "x" + range + StatUnit.blocks.localized())
        if (drillSize > 0) stats.add(SglStat.drillSize, drillSize.toFloat(), StatUnit.blocks)
        if (pierceBuild) stats.add(SglStat.pierceBuild, true)
        if (drillMoveMulti != 1f) stats.add(SglStat.drillMoveMulti, drillMoveMulti.toString() + "x")
        stats.add(SglStat.matrixEnergyUseMulti, energyMulti.toString() + "x")
    }

    override fun canPlaceOn(tile: Tile, team: Team?, rotation: Int): Boolean {
        for (edge in Edges.getEdges(size)) {
            val build = Vars.world.build(tile.x + edge.x, tile.y + edge.y)
            if (build is MatrixMinerBuild && build.team === team && (build.tile.x == tile.x || build.tile.y == tile.y)) {
                return true
            }
        }

        return false
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        super.drawPlace(x, y, rotation, valid)

        if (!valid) {
            drawPlaceText(Core.bundle.get("infos.requireMasterDevice"), x, y, false)
        }
    }

    abstract inner class MatrixMinerPluginBuild : SglBuilding() {
        open var owner: MatrixMinerBuild? = null
        var warmup: Float = 0f


        public override fun updateValid(): Boolean {
            return owner != null && owner!!.updateValid() && enabled
        }

        public override fun consEfficiency(): Float {
            return super.consEfficiency() * (if (owner == null) 0f else owner!!.consEfficiency())
        }

        override fun updateTile() {
            if (updateValid() && !owner!!.isChild(this)) owner = null

            warmup = Mathf.approachDelta(warmup, if (updateValid() && consumeValid()) owner!!.distributor!!.network.netEfficiency() else 0f, warmupSpeed)
            super.updateTile()

            if (updateValid()) updatePlugin(owner)
        }

        public override fun draw() {
            super.draw()
            Draw.z(Layer.effect)
            if (owner != null) {
                Lines.stroke(1.6f * warmup, SglDrawConst.matrixNet)
                Lines.line(x, y, owner!!.x, owner!!.y)

                Draw.draw(Draw.z(), Runnable {
                    rand.setSeed(id.toLong())
                    Draw.color(SglDrawConst.matrixNet)
                    MathRenderer.setDispersion(rand.random(0.08f, 0.12f) * warmup)
                    MathRenderer.setThreshold(0.4f, 0.6f)
                    for (i in 0..2) {
                        MathRenderer.drawSin(
                            x, y, 1f, owner!!.x, owner!!.y, rand.random(1.5f, 2.5f),
                            rand.random(360f, 720f),
                            Time.time * rand.random(2f, 4f) * (if (rand.random(1f) > 0.5f) 1 else -1)
                        )
                    }
                })
            }
        }

        public override fun write(write: Writes) {
            super.write(write)
            write.f(warmup)
        }

        public override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            warmup = read.f()
        }

        open fun boost(): Float {
            return 1f
        }

        open fun angleValid(angle: Float): Boolean {
            return false
        }

        fun range(): Int {
            return range
        }

        fun drillSize(): Int {
            return drillSize
        }

        fun drillMoveMulti(): Float {
            return drillMoveMulti
        }

        fun pierceBuild(): Boolean {
            return pierceBuild
        }

        fun energyMultiplier(): Float {
            return energyMulti
        }

        abstract fun updatePlugin(owner: MatrixMinerBuild?)
    }

    companion object {
        private val rand = Rand()
    }
}