package singularity.world.blocks.product

import arc.Core
import arc.func.Cons
import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Lines
import arc.graphics.g2d.TextureRegion
import arc.math.Angles
import arc.math.Mathf
import arc.math.geom.Geometry
import arc.math.geom.Vec2
import arc.scene.ui.ImageButton
import arc.scene.ui.layout.Table
import arc.util.Nullable
import arc.util.Time
import arc.util.Tmp
import arc.util.io.Reads
import arc.util.io.Writes
import mindustry.Vars
import mindustry.entities.Sized
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.gen.Icon
import mindustry.graphics.Drawf
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.type.UnitType
import mindustry.ui.Styles
import mindustry.world.Tile
import mindustry.world.blocks.payloads.Payload
import mindustry.world.blocks.payloads.UnitPayload
import mindustry.world.blocks.units.RepairTurret
import mindustry.world.draw.DrawDefault
import singularity.graphic.MathRenderer
import singularity.graphic.SglDraw
import singularity.graphic.SglDrawConst
import singularity.util.MathTransform
import singularity.util.func.Floatc3
import singularity.world.SglFx
import universecore.world.producers.ProducePayload
import universecore.world.producers.ProduceType
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

open class HoveringUnitFactory(name: String) : SglUnitFactory(name) {
    var outputRange: Float = 0f
    var defHoverRadius: Float = 0f
    var laserOffY: Float = 0f
    var hoverMoveMinRadius: Float = 0f
    var hoverMoveMaxRadius: Float = 0f
    var beamWidth: Float = 0.6f
    var pulseRadius: Float = 3f
    var pulseStroke: Float = 1f
    var hoverTextureSuffix: String = "_hover"
    var laser: TextureRegion? = null
    var laserEnd: TextureRegion? = null
    var laserTop: TextureRegion? = null
    var laserTopEnd: TextureRegion? = null
    var hover: TextureRegion? = null

    init {
        draw = DrawDefault()
        buildType= Prov(::HoveringUnitFactoryBuild)
    }

    override fun load() {
        super.load()
      //  hover = TextureRegion(Texture(Pixmaps.outline(Core.atlas.getPixmap(name + hoverTextureSuffix), Pal.darkOutline, 3)))
        hover= Core.atlas.white()
        laser = Core.atlas.find("laser-white")
        laserEnd = Core.atlas.find("laser-white-end")
        laserTop = Core.atlas.find("laser-top")
        laserTopEnd = Core.atlas.find("laser-top-end")
    }

    public override fun init() {
        super.init()
        configurable = configurable or (outputRange > size * Vars.tilesize)
        rotate = outputRange <= size * Vars.tilesize
    }

    inner class HoveringUnitFactoryBuild : SglUnitFactoryBuild() {
        val payloadReleasePos: Vec2 = Vec2()
        val hoveringStats: Array<HoveringStat> = arrayOfNulls<HoveringStat>(4) as Array<HoveringStat>

        @Nullable
        var currentOutputTarget: Building? = null
        private var configOutputting = false

        public override fun init(tile: Tile?, team: Team?, shouldAdd: Boolean, rotation: Int): Building? {
            super.init(tile, team, shouldAdd, rotation)
            for (i in hoveringStats.indices) {
                val p = Geometry.d4(i)

                hoveringStats[i] = HoveringStat()
                val stat = hoveringStats[i]
                stat.idOff = i

                stat.defx = x + p.x * defHoverRadius
                stat.defy = y + p.y * defHoverRadius

                stat.pos.set(stat.defx, stat.defy)

                stat.angelVec.set(p.x.toFloat(), p.y.toFloat()).scl(-1f)
            }

            return this
        }

        override fun buildConfiguration(table: Table) {
            super.buildConfiguration(table)
            table.button(Icon.upload, Styles.clearTogglei, 40f, Runnable { configOutputting = !configOutputting })
                .update(Cons { u: ImageButton? -> u!!.setChecked(configOutputting) }).size(56f)
        }

        override fun onConfigureTapped(x: Float, y: Float): Boolean {
            if (!configOutputting) return false
            val dst = dst(x, y)
            if (dst > outputRange) {
                configOutputting = false
                return false
            } else if (dst < size * Vars.tilesize / 2f) {
                payloadReleasePos.setZero()
                return true
            } else {
                val building = Vars.world.buildWorld(x, y)

                if (building != null && (building.block.acceptsPayload || building.block.outputsPayload) && building.interactable(team)) {
                    payloadReleasePos.set(building.x - this.x, building.y - this.y)
                } else payloadReleasePos.set(x - this.x, y - this.y)
                return true
            }
        }

        override fun drawConfigure() {
            super.drawConfigure()
            if (!configOutputting) return

            if (outputRange > size * Vars.tilesize) Drawf.circles(x, y, outputRange, Pal.accent)

            if (abs(payloadReleasePos.x) > size * Vars.tilesize / 2f || abs(payloadReleasePos.y) > size * Vars.tilesize / 2f) {
                val dx = x + payloadReleasePos.x
                val dy = y + payloadReleasePos.y
                val building = Vars.world.buildWorld(dx, dy)

                Drawf.line(Pal.accent, x, y, dx, dy)
                Drawf.square(dx, dy, 14f, 45f)
                val lerp = (Time.time % 120f) / 120f
                Lines.stroke(3 * (1 - lerp), Pal.accent)
                Lines.square(dx, dy, 14 + 48 * lerp, 45f)

                if (building != null && (building.block.acceptsPayload || building.block.outputsPayload)) {
                    Draw.rect(Icon.download.getRegion(), dx, dy)
                    Drawf.square(building.x, building.y, building.block.size * Vars.tilesize * 1f + Mathf.absin(4f, 8f), 45f, Pal.accent)
                } else if (currentTask != null && (building == null || !building.interactable(team) || currentTask!!.buildUnit!!.flying || currentTask!!.buildUnit!!.canBoost || !building.block.solid)) {
                    Drawf.square(dx, dy, 8f, MathTransform.gradientRotateDeg(Time.time, 45f), Pal.accent)
                } else if (currentTask != null) {
                    Draw.color(Pal.accent, Color.crimson, Mathf.absin(4f, 1f))
                    Draw.rect(Icon.warning.getRegion(), dx, dy)
                }
            }

            Draw.reset()
        }

        override fun outputtingOffset(): Vec2 {
            return payloadReleasePos
        }

        public override fun craftTrigger() {
            super.craftTrigger()
            Tmp.v1.set(3f, 0f).setAngle(Mathf.randomSeed(id.toLong(), 360f) - Time.time)
            if (!payloads.isEmpty) payload!!.set(x + Tmp.v1.x, y + Tmp.v1.y, 90f)
        }

        public override fun drawConstructingPayload() {
            val z = Draw.z()

            Draw.z(Layer.flyingUnit - 1)
            var p: ProducePayload<*>? =null
            if (producer!!.current != null && (producer!!.current!!.get<ProducePayload<*>>(ProduceType.payload).also { p = it }) != null) {
                Tmp.v1.set(3f, 0f).setAngle(Mathf.randomSeed(id.toLong(), 360f) - Time.time)
                val dx = x + Tmp.v1.x
                val dy = y + Tmp.v1.y
                Draw.draw(Draw.z()) {
                    Drawf.construct(dx, dy, p!!.payloads[0].item.fullIcon, 0f, progress(), warmup(), totalProgress() % (20 * Mathf.PI2)) }

                Draw.z(min(Layer.darkness, z - 1f))
                val x = dx + UnitType.shadowTX
                val y = dy + UnitType.shadowTY

                Draw.color(Pal.shadow, Pal.shadow.a * progress())
                Draw.rect(p!!.payloads[0].item.fullIcon, x, y)
                Draw.color()
            }

            Draw.z(z)
        }

        override fun released(payload: Payload?) {
            SglFx.spreadDiamond.at(payload!!.x(), payload.y(), payload.size(), team.color)
        }

        override fun drawPayload() {
            if (outputting != null) {
                val dst = dst(outputting)
                val lerp = Mathf.clamp(dst / (size * Vars.tilesize))
                val prog = dst / payloadReleasePos.len()
                val tar = currentOutputTarget

                Draw.color(team.color)
                Draw.alpha(0.6f * lerp)

                Lines.stroke(1.6f * lerp)
                Lines.circle(outputting!!.x(), outputting!!.y(), outputting!!.size())

                Draw.draw(Draw.z(), Runnable {
                    MathRenderer.setDispersion((0.18f + Mathf.absin(Time.time / 3f, 6f, 0.4f)) * lerp * Mathf.clamp((1 - prog) / 0.5f))
                    MathRenderer.setThreshold(0.4f, 0.8f)
                    MathRenderer.drawSin(x, y, 6f, outputting!!.x(), outputting!!.y(), 5f, 120f, -2.5f * Time.time)
                    MathRenderer.drawSin(x, y, 6f, outputting!!.x(), outputting!!.y(), 5f, 150f, -3.2f * Time.time)
                })
                val z = Draw.z()
                Draw.z(min(Layer.darkness, z - 1f))
                val sh = if (outputting is UnitPayload && (outputting as UnitPayload) .unit.type.flying && tar == null) 1f else 1 - lastOutputProgress
                val x = outputting!!.x() + UnitType.shadowTX * sh
                val y = outputting!!.y() + UnitType.shadowTY * sh

                Draw.color(Pal.shadow)
                Draw.rect(outputting!!.icon(), x, y, outputting!!.rotation() - 90)
                Draw.color()
                Draw.z(z)
            }

            super.drawPayload()
        }

        public override fun draw() {
            super.draw()

            drawPayload()
            drawConstructingPayload()

            for (stat in hoveringStats) {
                stat.draw()
            }
        }

        override fun updateTile() {
            super.updateTile()
            val dx = x + payloadReleasePos.x
            val dy = y + payloadReleasePos.y
            currentOutputTarget = Vars.world.buildWorld(dx, dy)
            //对齐方块输出坐标
            if (currentOutputTarget != null && currentOutputTarget!!.interactable(team)
                && (currentOutputTarget!!.block.acceptsPayload || currentOutputTarget!!.block.outputsPayload)
            ) {
                payloadReleasePos.set(currentOutputTarget!!.x - x, currentOutputTarget!!.y - y)
            } else currentOutputTarget = null

            for (stat in hoveringStats) {
                stat.update()
            }
        }

        public override fun write(write: Writes) {
            super.write(write)

            write.f(payloadReleasePos.x)
            write.f(payloadReleasePos.y)
        }

        public override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)

            payloadReleasePos.set(
                read.f(),
                read.f()
            )
        }

        var mark1: Sized = object : Sized {
            override fun hitSize(): Float {
                return if (currentTask != null) currentTask!!.buildUnit!!.hitSize else 0f
            }

            override fun getX(): Float {
                return x + Angles.trnsx(Mathf.randomSeed(id.toLong(), 360f) - Time.time, 3f, 0f)
            }

            override fun getY(): Float {
                return y + Angles.trnsy(Mathf.randomSeed(id.toLong(), 360f) - Time.time, 3f, 0f)
            }
        }
        inner class HoveringStat {
            var defx: Float = 0f
            var defy: Float = 0f
            var idOff: Int = 0
            val pos: Vec2 = Vec2()
            val targetPos: Vec2 = Vec2()
            val angelVec: Vec2 = Vec2()
            val halfMark: Vec2 = Vec2()
            val last: Vec2 = Vec2()
            val offset: Vec2 = Vec2()
            var lerp: Float = 0f
            var off: Float = 0f

            fun update() {
                if (Mathf.chanceDelta((0.004f * lerp).toDouble()) || targetPos.isZero()) {
                    targetPos.set(Mathf.random(hoverMoveMinRadius, hoverMoveMaxRadius), 0f).setAngle(Mathf.random(360f))
                }

                pos.lerpDelta(Mathf.lerp(defx, x + targetPos.x, warmup()), Mathf.lerp(defy, y + targetPos.y, warmup()), 0.02f)
                lerp = Mathf.approachDelta(lerp, 1 - Mathf.clamp(Mathf.len(x + targetPos.x - pos.x, y + targetPos.y - pos.y) / 24), 0.03f)

                off += Time.delta * lerp * 2
                Tmp.v1.set(3f, 0f).setAngle(off).scl(lerp)
                pos.add(Tmp.v1)

                Tmp.v1.set(pos.x - x, pos.y - y).scl(-1f).nor()
                angelVec.lerpDelta(Tmp.v1, 0.03f)

                if (halfMark.isZero()) {
                    //初始化半长控制点的位置
                    halfMark.set(x + (pos.x - x) / 2f, y + (pos.y - y) / 2f)
                } else halfMark.lerpDelta(x + (pos.x - x) / 2f, y + (pos.y - y) / 2f, 0.04f)

                if (Mathf.chanceDelta((0.07f * lerp * warmup()).toDouble())) {
                    SglFx.constructSpark.at(last.x, last.y, SglDrawConst.matrixNetDark)
                }

                if (Mathf.chanceDelta((0.08f * lerp * warmup()).toDouble())) {
                    SglFx.moveDiamondParticle.at(pos.x, pos.y, Tmp.v1.angle(), SglDrawConst.matrixNetDark, Mathf.len(pos.x - x, pos.y - y))
                }
            }

            fun draw() {
                val z = Draw.z()

                Draw.color(team.color, 0.4f + Mathf.absin(5f, 0.2f))
                Lines.stroke((1 + Mathf.absin(6f, 0.5f)) * warmup())
                val x1 = (defx + halfMark.x) / 2
                val y1 = (defy + halfMark.y) / 2
                val x2 = (pos.x + halfMark.x) / 2
                val y2 = (pos.y + halfMark.y) / 2

                Tmp.v1.set(4 + Mathf.absin(4f, 3f), 0f).setAngle(off)

                Lines.curve(defx, defy, x1 + Tmp.v1.x, y1 + Tmp.v1.y, x2 - Tmp.v1.x, y2 - Tmp.v1.y, pos.x, pos.y, max(18, (Mathf.dst(x, y, pos.x, pos.y) / 6).toInt()))

                Draw.alpha(1f)
                Draw.z(Layer.effect)
                Fill.circle(defx, defy, 1.6f * warmup())

                Draw.color()

                Draw.z(Layer.flyingUnit)
                val rot = angelVec.angle()
                Draw.rect(hover, pos.x, pos.y, rot - 90)

                SglDraw.drawTransform(pos.x, pos.y, laserOffY, 0f, angelVec.angle(), Floatc3 { x: Float, y: Float, r: Float ->
                    RepairTurret.drawBeam(
                        x, y, r, 4f, id + idOff, if (currentTask != null) mark1 else null, team, warmup(),
                        pulseStroke, pulseRadius, beamWidth, last, offset, team.color, Color.white,
                        laser, laserEnd, laserTop, laserTopEnd
                    )
                })
                Draw.z(z)
            }
        }

    }

}