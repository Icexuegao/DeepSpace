package ice.world.content.unit.entity

import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Angles
import arc.util.Time
import ice.library.IFiles
import ice.world.content.unit.entity.base.Entity

class ArdenThorn : Entity() {
    companion object {
        val regions: TextureRegion by lazy {
            Core.atlas.find(IFiles.getNormName("ardenThorn-propeller"))
        }
    }

    override fun drawBodyRegion(rotation: Float) {
        super.drawBodyRegion(rotation)
        drawRotorWing(13f, 10f, rotation)
    }

    override fun drawShadowRegion(x: Float, y: Float, rotation: Float) {
        super.drawShadowRegion(x, y, rotation)
        val rot = rotation
        val trnsx1 = Angles.trnsx(rot, 13f, 10f)
        val trnsy1 = Angles.trnsy(rot, 13f, 10f)
        val trnsx2 = Angles.trnsx(rot, -13f, 10f)
        val trnsy2 = Angles.trnsy(rot, -13f, 10f)
        val speed = Time.time * 5f * 6
        val ux = x + trnsx1
        val uy = y + trnsy1
        val nx = x + trnsx2
        val ny = y + trnsy2
        Draw.rect(regions, ux, uy, speed)
        Draw.rect(regions, nx, ny, -speed)
    }

    fun drawRotorWing(rx: Float, ry: Float, rotation: Float) {
        val rot = rotation
        val trnsx1 = Angles.trnsx(rot, rx, ry)
        val trnsy1 = Angles.trnsy(rot, rx, ry)
        val trnsx2 = Angles.trnsx(rot, -rx, ry)
        val trnsy2 = Angles.trnsy(rot, -rx, ry)
        val speed = Time.time * 5f * 6
        val ux = x + trnsx1
        val uy = y + trnsy1
        val nx = x + trnsx2
        val ny = y + trnsy2
        Draw.rect(regions, ux, uy, speed)
        Draw.rect(regions, nx, ny, -speed)
    }
}