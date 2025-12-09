package ice.world.draw.part

import arc.graphics.g2d.Draw
import arc.math.Mathf
import arc.math.geom.Vec2
import arc.struct.Seq
import mindustry.entities.part.DrawPart

open class CustomPart(var draw: Drawer) : DrawPart() {
    var moves: Seq<PartMove> = Seq<PartMove>()

    var progress: PartProgress = PartProgress { p: PartParams? -> 1f }
    var layer: Float = -1f

    var x: Float = 0f
    var y: Float = 0f
    var drawRadius: Float = 0f
    var rotation: Float = 0f
    var moveX: Float = 0f
    var moveY: Float = 0f
    var drawRadiusTo: Float = 0f
    var moveRot: Float = 0f
    var mirror: Boolean = false

    private val vec = Vec2()
    private val vec2 = Vec2()

    override fun draw(params: PartParams) {
        val prog = Mathf.clamp(progress.get(params))
        val z = Draw.z()

        var dx = 0f
        var dy = 0f
        var dr = 0f
        for (move in moves) {
            dx += move.x * move.progress.get(params)
            dy += move.y * move.progress.get(params)
            dr += move.rot * move.progress.get(params)
        }

        val rot = rotation + moveRot * prog + dr
        vec.set(
            x + moveX * prog + dx, y + moveY * prog + dy
        ).rotate(params.rotation - 90)
        vec2.set(drawRadius + (drawRadiusTo - drawRadius) * prog, 0f).setAngle(rot).rotate(params.rotation)

        var drawX = vec.x + vec2.x
        var drawY = vec.y + vec2.y

        if (layer >= 0) Draw.z(layer)
        draw.draw(params.x + drawX, params.y + drawY, params.rotation + rot, prog)

        if (mirror) {
            vec.setAngle(2 * params.rotation - vec.angle())
            vec2.setAngle(-rot).rotate(params.rotation)
            drawX = vec.x + vec2.x
            drawY = vec.y + vec2.y
            draw.draw(params.x + drawX, params.y + drawY, params.rotation - rot, prog)
        }
        Draw.z(z)
    }

    fun interface Drawer {
        fun draw(x: Float, y: Float, rotation: Float, progress: Float)
    }
}
