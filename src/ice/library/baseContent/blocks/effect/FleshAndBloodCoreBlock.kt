package ice.library.baseContent.blocks.effect

import arc.func.Prov
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Angles
import arc.math.Mathf
import arc.math.geom.Rect
import arc.math.geom.Vec2
import arc.struct.Seq
import ice.library.IFiles
import mindustry.entities.Units
import mindustry.gen.Unit
import mindustry.graphics.Drawf
import mindustry.world.blocks.storage.CoreBlock

open class FleshAndBloodCoreBlock(name: String) : CoreBlock(name) {
    var eye: TextureRegion = IFiles.findPng("$name-eye")

    init {
        buildType = Prov(::FleshAndBloodCoreBlockBuild)
    }

    override fun icons(): Array<TextureRegion> {
        return arrayOf(region, eye)
    }

    inner class FleshAndBloodCoreBlockBuild : CoreBuild() {
        var movement: Vec2 = Vec2(1f, 0f)
        var radius2: Float = (8 * 50).toFloat()
        var unit: Unit? = null
        var units: Seq<Unit> = Seq()
        override fun update() {
            Units.nearby(Rect(x - radius2 / 2f, y - radius2 / 2f, radius2, radius2)) { u: Unit ->
                units.add(u)
            }
            unit = if (units.size == 0) null
            else units.first()
            units.clear()



            super.update()
            unit?:return
            val len = Vec2(x, y).sub(unit).len()
            if (len <radius2){
                val fl = len / radius2
                movement.setLength( Mathf.clamp(fl,0.3f,1f))
            }
        }

        override fun drawSelect() {
            Drawf.dashSquare(Color.red, x, y, radius2)
        }

        override fun draw() {
            super.draw()
            if (unit == null) {
                Draw.rect(eye, x, y)
            } else {
                //设置vec角度为 获得以(x,y)为起点，(aimX,aimY)到(x,y)矢量的角度
                movement.setAngle(Angles.angle(x, y, unit!!.x, unit!!.y))
                Draw.rect(eye, x + movement.x, y + movement.y)
            }
        }
    }
}
