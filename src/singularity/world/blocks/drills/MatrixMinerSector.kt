package singularity.world.blocks.drills

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.math.Angles
import arc.math.Mathf
import arc.math.Rand
import arc.math.geom.Point2
import arc.math.geom.Vec2
import arc.struct.Seq
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import arc.util.pooling.Pool.Poolable
import arc.util.pooling.Pools
import mindustry.Vars
import mindustry.entities.Effect
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.graphics.Trail
import mindustry.type.Item
import mindustry.world.Edges
import mindustry.world.Tile
import mindustry.world.meta.StatUnit
import singularity.graphic.MathRenderer
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.world.SglFx
import singularity.world.blocks.drills.MatrixMiner.MatrixMinerBuild
import singularity.world.meta.SglStat
import kotlin.math.abs

open class MatrixMinerSector(name: String) : MatrixMinerPlugin(name) {
    var drillMoveSpeed: Float = 0.05f
    var drillEffect: Effect = SglFx.matrixDrill
    var baseDrillTime: Float = 30f

    override fun setStats() {
        super.setStats()

        stats.add(SglStat.drillAngle, 90f, StatUnit.degrees)
    }

    public override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        super.drawPlace(x, y, rotation, valid)

        if (valid) {
            drawArea(x, y)
        }
    }

    fun drawArea(x: Int, y: Int) {
        for (edge in Edges.getEdges(size)) {
            val t = Vars.world.tile(x + edge.x, y + edge.y)
            val build = t.build
            if (build is MatrixMinerBuild && build.team === Vars.player.team() && (build.tile.x.toInt() == x || build.tile.y.toInt() == y)) {
                val side: Int = build.relativeTo(x, y).toInt()
                val l = (range * 4).toFloat()
                Tmp.v1.set(l, 0f).setAngle((side * 90).toFloat())
                Tmp.v2.set(Tmp.v1).rotate90(1)
                Drawf.dashLine(Pal.accent, build.x, build.y, build.x + Tmp.v1.x + Tmp.v2.x, build.y + Tmp.v1.y + Tmp.v2.y)
                Drawf.dashLine(Pal.accent, build.x + Tmp.v1.x - Tmp.v2.x, build.y + Tmp.v1.y - Tmp.v2.y, build.x + Tmp.v1.x + Tmp.v2.x, build.y + Tmp.v1.y + Tmp.v2.y)
                Drawf.dashLine(Pal.accent, build.x + Tmp.v1.x - Tmp.v2.x, build.y + Tmp.v1.y - Tmp.v2.y, build.x, build.y)
            }
        }
    }

    inner class MatrixMinerSectorBuild : MatrixMinerPluginBuild() {
        val tmp: Vec2 = Vec2()
        var drillPos: Vec2? = Vec2()
        var nextPos: Vec2? = null
        var oreIndex: Int = -1
        var drillingPos: Int? = 0
        var drillProgress: Float = 0f
        var drillTrail: Trail = Trail(28)
        var side: Byte = -1
        var particles: Seq<OreParticle> = Seq<OreParticle>(512)

        override fun drawConfigure() {
            super.drawConfigure()

            Draw.color(Pal.accent, 0.1f + Mathf.absin(5f, 0.4f))
            val l = (owner!!.drillRange * 4).toFloat()
            Tmp.v1.set(l, 0f).setAngle((side * 90).toFloat())
            Tmp.v2.set(Tmp.v1).rotate90(1)
            Fill.tri(
                owner!!.x, owner!!.y,
                owner!!.x + Tmp.v1.x + Tmp.v2.x, owner!!.y + Tmp.v1.y + Tmp.v2.y,
                owner!!.x + Tmp.v1.x - Tmp.v2.x, owner!!.y + Tmp.v1.y - Tmp.v2.y
            )
        }

      /*  public override fun setOwner(miner: MatrixMinerBuild) {
            super.owner = miner
            side = miner.relativeTo(this)
        }*/

        public override fun init(tile: Tile?, team: Team?, shouldAdd: Boolean, rotation: Int): Building? {
            drillPos!!.set(tile!!.drawx(), tile.drawy())
            return super.init(tile, team, shouldAdd, rotation)
        }

        public override fun updateTile() {
            warmup = Mathf.approachDelta(warmup, if (updateValid() && consumeValid() && nextPos != null) owner!!.distributor!!.network.netEfficiency() else 0f, 0.04f)
            drillTrail.update(drillPos!!.x, drillPos!!.y)
            super.updateTile()
            if (owner == null) side = -1
            val itr = particles.iterator()
            while (itr.hasNext()) {
                val particle = itr.next()
                if (particle.removed) {
                    itr.remove()
                    Pools.free(particle)
                    continue
                }

                particle.update()
            }
        }

        public override fun updatePlugin(owner: MatrixMinerBuild?) {
            if (nextPos == null) {
                if (oreIndex < 0) oreIndex = Mathf.random(owner!!.orePosArr!!.size)
                var c = 0
                val len = owner!!.orePosArr!!.size

                while (c < len) {
                    c++
                    oreIndex = (oreIndex + 1) % len

                    drillingPos = owner.orePosArr!![oreIndex]
                    val tile = Vars.world.tile(drillingPos!!)

                    if (tile == null || (!owner.pierce && tile.build != null)
                        || abs(tile.x - owner.tileX()) > owner.drillRange / 2 || abs(tile.y - owner.tileY()) > owner.drillRange / 2 || !owner.angleValid(Angles.angle(tile.worldx() - owner.x, tile.worldy() - owner.y))
                    ) continue
                    val ore: Item?
                    if ((owner.ores.get(drillingPos).also { ore = it }) != null && owner.drillItem.contains(ore)) {
                        nextPos = tmp.set(tile.worldx(), tile.worldy())
                        break
                    }
                }
            } else {
                drillPos!!.lerpDelta(nextPos, drillMoveSpeed * warmup)

                if (Mathf.len(nextPos!!.x - drillPos!!.x, nextPos!!.y - drillPos!!.y) <= 6) {
                    drillProgress += 1 / baseDrillTime * consEfficiency() * warmup * owner!!.boost

                    while (drillProgress >= 1) {
                        drillProgress--

                        nextPos = null
                        val half = owner!!.drillSize / 2
                        val off = (owner.drillSize + 1) % 2
                        val ox = Point2.x(drillingPos!!) - half + off
                        val oy = Point2.y(drillingPos!!) - half + off

                        for (x in 0..<owner!!.drillSize) {
                            for (y in 0..<owner.drillSize) {
                                val t = Vars.world.tile(Point2.pack(ox + x, oy + y))
                                if (t == null || (!owner.pierce && t.build != null)) continue
                                val ore = owner.ores.get(t.pos())
                                if (ore == null || !owner.drillItem.contains(ore)) continue
                                val particle = Pools.obtain<OreParticle?>(OreParticle::class.java, Prov { OreParticle() })
                                particle.color = ore.color
                                particle.sides = Mathf.random(3, 5)
                                particle.alpha = 1f
                                particle.rotateSpeed = Mathf.random(0.6f, 5f)
                                particle.speed = Mathf.random(1f, 2.5f)
                                particle.position.set(t.worldx(), t.worldy()).add(Tmp.v1.rnd(Mathf.random(5f)))
                                particle.targetPos.set(owner.x, owner.y).add(Tmp.v1.rnd(Mathf.random(5f)))
                                particle.size = Mathf.random(1f, 3f)

                                particles.add(particle)

                                drillEffect.at(t.worldx(), t.worldy(), ore.color)

                                if (owner.itemBuffer.remainingCapacity() > 0) owner.offload(ore)
                            }
                        }
                    }
                }
            }
        }

        override fun draw() {
            super.draw()
            drawParticle()
            Draw.z(Layer.effect)
            Fill.circle(x, y, 5 * warmup)
            Lines.stroke(1.2f * warmup, SglDrawConst.matrixNet)
            SglDraw.dashCircle(x, y, 8f, 6, 180f, -Time.time)
            if (updateValid()) drawDrill()
        }

        fun drawParticle() {

            for (particle in particles) {
                particle.draw()
            }
        }

        fun drawDrill() {
             drillTrail.draw(SglDrawConst.matrixNet, 4.5f * warmup)

            Lines.stroke(2.6f * warmup, SglDrawConst.matrixNet)
            Lines.line(x, y, drillPos!!.x, drillPos!!.y)
            val half = owner!!.drillSize / 2
            val off = (owner!!.drillSize + 1) % 2
            val ox = Point2.x(drillingPos!!) - half + off
            val oy = Point2.y(drillingPos!!) - half + off

            for (x in 0..<owner!!.drillSize) {
                for (y in 0..<owner!!.drillSize) {
                    val t = Vars.world.tile(Point2.pack(ox + x, oy + y))
                    if (t == null || (!owner!!.pierce && t.build != null)) continue
                    val ore = owner!!.ores.get(t.pos())
                    if (ore == null || !owner!!.drillItem.contains(ore)) continue

                    Lines.stroke(1 * warmup * drillProgress, SglDrawConst.matrixNet)
                    Lines.line(drillPos!!.x, drillPos!!.y, ((ox + x) * Vars.tilesize).toFloat(), ((oy + y) * Vars.tilesize).toFloat())
                    Draw.color(ore.color)
                    Fill.circle(((ox + x) * Vars.tilesize).toFloat(), ((oy + y) * Vars.tilesize).toFloat(), 2 * drillProgress * warmup)
                }
            }


                Draw.draw(Draw.z(), Runnable {
                    Draw.color(SglDrawConst.matrixNet)
                    MathRenderer.setDispersion(0.23f * warmup)
                    MathRenderer.setThreshold(0.5f, 0.7f)

                    rand.setSeed(id.toLong())
                    for (i in 0..3) {
                        MathRenderer.drawSin(
                            x, y, rand.random(2f, 3.4f),
                            drillPos!!.x, drillPos!!.y,
                            1.6f,
                            rand.random(500f, 800f),
                            rand.random(2f, 3.6f) * Time.time
                        )
                    }
                })


            Draw.color(SglDrawConst.matrixNet)

            Fill.circle(drillPos!!.x, drillPos!!.y, 3 * warmup)
            SglDraw.drawDiamond(drillPos!!.x, drillPos!!.y, 6 + 14 * warmup, 3f * warmup, Time.time)
            SglDraw.drawDiamond(drillPos!!.x, drillPos!!.y, 8 + 16 * warmup, 4f * warmup, -Time.time * 1.2f)

            Lines.stroke(1.8f * warmup)
            SglDraw.drawCornerTri(
                drillPos!!.x, drillPos!!.y,
                24 * warmup,
                5 * warmup,
                Time.time * 1.5f,
                true
            )
        }

        public override fun angleValid(angle: Float): Boolean {
            return side >= 0 && abs(Angles.angleDist(angle, (side * 90).toFloat())) < 45
        }

        override fun write(write: Writes) {
            super.write(write)
            write.f(drillProgress)
            write.i(oreIndex)
            write.i(drillingPos!!)
            write.f(drillPos!!.x)
            write.f(drillPos!!.y)

            write.bool(nextPos != null)
            if (nextPos != null) {
                write.f(nextPos!!.x)
                write.f(nextPos!!.y)
            }
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            drillProgress = read.f()
            oreIndex = read.i()
            drillingPos = read.i()
            drillPos!!.set(read.f(), read.f())

            if (read.bool()) {
                nextPos = tmp.set(read.f(), read.f())
            }
        }
    }

    class OreParticle : Poolable {
        var position: Vec2 = Vec2()
        var targetPos: Vec2 = Vec2()
        var rotation: Float = 0f
        var alpha: Float = 0f
        var size: Float = 0f
        var speed: Float = 0f
        var rotateSpeed: Float = 0f
        var sides: Int = 0
        var color: Color? = null
        var removed: Boolean = false

        fun update() {
            val tar = targetPos

            position.approachDelta(tar, speed)
            rotation += rotateSpeed * Time.delta
            val distance = Mathf.len(tar.x - position.x, tar.y - position.y)
            alpha = Mathf.clamp(distance / 16)

            if (distance <= 2) {
                removed = true
            }
        }

        fun draw() {
            Lines.stroke(0.5f * alpha, color)
            Draw.z(Layer.effect)
            Lines.poly(position.x, position.y, sides, size, rotation)
            Draw.z(Layer.bullet - 1)
            Draw.alpha(0.55f * alpha)
            Fill.poly(position.x, position.y, sides, size, rotation)
            Draw.z(Layer.blockAdditive)
            Draw.reset()
        }

        override fun reset() {
            position.setZero()
            targetPos.setZero()
            rotation = 0f
            alpha = 0f
            size = 0f
            speed = 0f
            rotateSpeed = 0f
            sides = 0
            color = null
            removed = false
        }
    }

    companion object {
        private val rand = Rand()
    }
}