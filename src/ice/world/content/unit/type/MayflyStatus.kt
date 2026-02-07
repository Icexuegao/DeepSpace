package ice.world.content.unit.type

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.math.Angles
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.util.Time
import arc.util.Tmp
import ice.graphics.IceColor
import mindustry.Vars
import mindustry.gen.Unit
import mindustry.graphics.Layer
import mindustry.graphics.Pal
import mindustry.graphics.Trail
import mindustry.type.UnitType
import singularity.graphic.MathRenderer
import singularity.graphic.SglDraw
import singularity.util.MathTransform
import singularity.world.SglFx
import kotlin.math.min

class MayflyStatus {
    var x: Float = 0f
    var y: Float = 0f
    val vel: Vec2 = Vec2()
    val rot: Vec2 = Vec2()
    val tmp1: Vec2 = Vec2()
    val tmp2: Vec2 = Vec2()
    val trail1: Trail = Trail(20)
    val trail2: Trail = Trail(20)
    val trail: Trail = Trail(28)
    val off: Float = Mathf.random(0f, 360f)

    val farg1: FloatArray = FloatArray(9)
    val farg2: FloatArray = FloatArray(9)

    init {
        for (d in 0..2) {
            farg1[d * 3] = Mathf.random(0.5f, 3f) / (d + 1) * Mathf.randomSign()
            farg1[d * 3 + 1] = Mathf.random(0f, 360f)
            farg1[d * 3 + 2] = Mathf.random(8f, 16f) / ((d + 1) * (d + 1))
        }
        for (d in 0..2) {
            farg2[d * 3] = Mathf.random(0.5f, 3f) / (d + 1) * Mathf.randomSign()
            farg2[d * 3 + 1] = Mathf.random(0f, 360f)
            farg2[d * 3 + 2] = Mathf.random(8f, 16f) / ((d + 1) * (d + 1))
        }
    }

    fun update(unit: Unit, mount: WeaponMount) {
        val movX = Angles.trnsx(mount.rotation, 0f, 14f) * mount.warmup
        val movY = Angles.trnsy(mount.rotation, 0f, 14f) * mount.warmup

        val targetX = unit.x + Angles.trnsx(unit.rotation() - 90, mount.weapon.x + movX, mount.weapon.y + movY)
        val targetY = unit.y + Angles.trnsy(unit.rotation() - 90, mount.weapon.x + movX, mount.weapon.y + movY)

        if (Mathf.chanceDelta((0.03f * mount.warmup).toDouble())) {
            val dx = unit.x + Angles.trnsx(unit.rotation, -28f, 0f) - x
            val dy = unit.y + Angles.trnsy(unit.rotation, -28f, 0f) - y

            val dst = Mathf.dst(dx, dy)
            val ang = Mathf.angle(dx, dy)

            Tmp.v1.rnd(3f)
            SglFx.moveParticle.at(x + Tmp.v1.x, y + Tmp.v1.y, ang, IceColor.matrixNet, dst)
        }

        val dx = targetX - x
        val dy = targetY - y

        val dst = Mathf.len(dx, dy)
        Tmp.v1.set(1f, 0f).setAngle(unit.rotation() + mount.rotation)

        rot.lerpDelta(Tmp.v1, 0.05f)
        val speed = 2 * (dst / 24)

        val vec2 = Tmp.v1.set(dx, dy).setLength(speed)
        vel.lerpDelta(vec2.add(Tmp.v2.set(0.12f, 0f).setAngle(Time.time * (if (mount.weapon.x > 0) 1 else -1) + mount.phase)), 0.075f)

        x += vel.x * Time.delta
        y += vel.y * Time.delta

        tmp1.set(MathTransform.fourierSeries(Time.time, *farg1)).scl(mount.warmup)
        tmp2.set(MathTransform.fourierSeries(Time.time, *farg2)).scl(mount.warmup)

        trail.update(x, y)
        trail1.update(x + tmp1.x, y + tmp1.y)
        trail2.update(x + tmp2.x, y + tmp2.y)
    }

    fun draw(unit: Unit, mount: WeaponMount) {
        unit.type.applyColor(unit)
        val angle = rot.angle() - 90
        Draw.rect(mount.weapon.region, x, y, angle)

        SglDraw.drawBloomUnderFlyUnit {
            trail.draw(IceColor.matrixNet, 4f)
            Draw.color(Color.black)
            Fill.circle(x, y, 4f)
            Draw.reset()
        }

        val z = Draw.z()
        Draw.z(Layer.effect - 1)

        Draw.color(IceColor.matrixNet)

        Draw.draw(Draw.z()) {
            val dx = Angles.trnsx(unit.rotation, -28f, 0f)
            val dy = Angles.trnsy(unit.rotation, -28f, 0f)
            MathRenderer.setDispersion((0.2f + Mathf.absin(Time.time / 3f + off, 6f, 0.4f)) * mount.warmup)
            MathRenderer.setThreshold(0.3f, 0.8f)
            MathRenderer.drawSin(x, y, 3f, unit.x + dx, unit.y + dy, 5f, 120f, -3 * Time.time + off)
        }

        Draw.z(Draw.z() + 0.01f)

        trail1.draw(IceColor.matrixNet, 3 * mount.warmup)
        trail2.draw(IceColor.matrixNet, 3 * mount.warmup)

        Draw.color(IceColor.matrixNet)
        Fill.circle(x + tmp1.x, y + tmp1.y, 4f)
        Fill.circle(x + tmp2.x, y + tmp2.y, 4f)

        SglDraw.drawDiamond(x, y, 24f, 10f, angle)

        SglDraw.drawTransform(x, y, 0f, 12f, angle) { x: Float, y: Float, r: Float -> SglDraw.gapTri(x, y, 12 * mount.warmup, 22 + 24 * mount.warmup, 8f, r + 90) }
        SglDraw.drawTransform(x, y, 0f, 10f, angle - 180) { x: Float, y: Float, r: Float -> SglDraw.gapTri(x, y, 9 * mount.warmup, 12 + 8 * mount.warmup, 6f, r + 90) }

        Fill.circle(x, y, 6f)
        Draw.color(Color.black)
        Fill.circle(x, y, 4f)
        Draw.reset()

        Draw.z(min(Layer.darkness, z - 1f))
        val e = Mathf.clamp(unit.elevation, unit.type.shadowElevation, 1f) * unit.type.shadowElevationScl * (1f - unit.drownTime)
        val x = this.x + UnitType.shadowTX * e
        val y = this.y + UnitType.shadowTY * e
        val floor = Vars.world.floorWorld(x, y)

        val dest = if (floor.canShadow) 1f else 0f
        unit.shadowAlpha = if (unit.shadowAlpha < 0) dest else Mathf.approachDelta(unit.shadowAlpha, dest, 0.11f)
        Draw.color(Pal.shadow, Pal.shadow.a * unit.shadowAlpha)

        Draw.z(Draw.z() + 0.02f)
        Draw.rect(mount.weapon.region, this.x + UnitType.shadowTX * e, this.y + UnitType.shadowTY * e, angle)
        Draw.color()
        Draw.z(z)
    }
}