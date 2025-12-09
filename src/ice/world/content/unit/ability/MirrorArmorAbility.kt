package ice.world.content.unit.ability

import arc.Core
import arc.func.Cons
import arc.graphics.Blending
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.gl.FrameBuffer
import arc.math.Mathf
import arc.util.Time
import arc.util.Tmp
import ice.graphics.SglDraw
import ice.graphics.SglDraw.DrawAcceptor
import ice.graphics.IceColor
import ice.shader.SglShaders
import ice.shader.SglShaders.MaskShader
import ice.ui.bundle.BaseBundle.Companion.bundle
import mindustry.gen.Bullet
import mindustry.gen.Groups
import mindustry.gen.Unit
import mindustry.graphics.Layer
import kotlin.math.max

class MirrorArmorAbility : MirrorShieldBase() {
    companion object {
        private val drawID = SglDraw.nextTaskID()
        private val maskID = SglDraw.nextTaskID()
        private val fu_kID = SglDraw.nextTaskID()

        private val drawBuffer = FrameBuffer()
        private val pingpongBuffer = FrameBuffer()
    }

    override var localizedName = "mirrorArmor"

    init {
        bundle {
            desc(zh_CN, "镜面装甲")
        }
    }

    override fun shouldReflect(unit: Unit, bullet: Bullet): Boolean {
        return bullet.dst(unit) < unit.hitSize + bullet.vel.len() * 2 * Time.delta
    }

    override fun eachNearBullets(unit: Unit, cons: Cons<Bullet>) {
        val nearRadius = unit.hitSize
        Groups.bullet.intersect(unit.x - nearRadius, unit.y - nearRadius, nearRadius * 2, nearRadius * 2, Cons { b ->
            if (unit.team !== b!!.team) cons.get(b)
        })
    }

    override fun draw(unit: Unit) {
        if (unit.shield <= 0) return

        val z = Draw.z()

        if (Core.settings.getBool("animatedshields")) {
            Draw.z(Layer.shields - 2f)
            SglDraw.drawToBuffer<Unit?, FrameBuffer?>(drawID, drawBuffer, unit, DrawAcceptor { b: FrameBuffer ->
                SglShaders.mirrorField.waveMix = Tmp.c1.set(IceColor.matrixNet)
                SglShaders.mirrorField.stroke = 1.2f
                SglShaders.mirrorField.sideLen = 5f
                SglShaders.mirrorField.waveScl = 0.03f
                SglShaders.mirrorField.gridStroke = 0.6f
                SglShaders.mirrorField.maxThreshold = 1f
                SglShaders.mirrorField.minThreshold = 0.7f
                SglShaders.mirrorField.offset.set(Time.time * 0.1f, Time.time * 0.1f)

                pingpongBuffer.resize(Core.graphics.width, Core.graphics.height)
                pingpongBuffer.begin(Color.clear)
                Blending.disabled.apply()
                b.blit(SglShaders.mirrorField)
                Blending.normal.apply()
                pingpongBuffer.end()
            }, DrawAcceptor { e: Unit ->
                Draw.mixcol(Tmp.c1.set(e.team.color).lerp(Color.white, alpha), 1f)
                Draw.scl(1.1f)
                Draw.rect(e.type.shadowRegion, e.x, e.y, e.rotation - 90)
                Draw.reset()
            })

            SglDraw.drawTask<Unit?, MaskShader?>(maskID, unit, SglShaders.alphaMask,
                DrawAcceptor { s: MaskShader? -> SglShaders.alphaMask.texture = pingpongBuffer.texture },
                DrawAcceptor { e ->
                    Draw.color(Color.white, max(alpha, Mathf.absin(6f, 0.6f)))
                    Draw.scl(1.15f)
                    Draw.rect(e.type.shadowRegion, e.x, e.y, e.rotation - 90)
                    Draw.reset()
                })

            //Yes, this code doesn't do anything, but it won't work properly without this code
            //fu*k off arc GL
            SglDraw.drawTask<Unit?>(fu_kID, unit, DrawAcceptor { u: Unit? -> }, DrawAcceptor { u: Unit? ->
                Draw.draw(Draw.z()) {
                    pingpongBuffer.resize(2, 2)
                    pingpongBuffer.begin(Color.clear)
                    pingpongBuffer.end()
                    pingpongBuffer.blit(Draw.getShader())
                }
            }, DrawAcceptor { u: Unit? -> })
        } else {
            Draw.z(SglDraw.mirrorField + 1)
            Draw.mixcol(Tmp.c1.set(unit.team.color).lerp(Color.white, alpha), 1f)
            Draw.alpha(0.3f * max(alpha, Mathf.absin(6f, 0.6f)))
            Draw.scl(1.1f)
            Draw.rect(unit.type.shadowRegion, unit.x, unit.y, unit.rotation - 90)
            Draw.reset()
        }

        Draw.z(z)
        Draw.reset()
    }


}
