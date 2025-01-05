package ice.type.unit

import arc.graphics.Color
import arc.math.geom.Vec2
import arc.util.Tmp
import mindustry.content.Fx
import mindustry.entities.Mover
import mindustry.entities.units.WeaponMount
import mindustry.gen.Bullet
import mindustry.gen.Groups
import mindustry.gen.Unit

class PointDefenseWeapon(name: String) : IceWeapon(name) {
    var damageWeapon = 10f
    var range = 10f
    var target: Bullet? = null
    var shootLength = 3f
    var color = Color.white

    override fun update(unit: Unit, mount: WeaponMount) {

        super.update(unit, mount)
    }

    override fun shoot(unit: Unit, mount: WeaponMount, shootX: Float, shootY: Float, rotation: Float) {
        target = Groups.bullet.intersect(x - range, y - range, range * 2, range * 2)
            .min({ b: Bullet -> b.team !== unit.team && b.type().hittable }) { b: Bullet -> b.dst2(unit) }
        if (target!=null) mount.target=target
        if (mount.reload >= 0f) {
            if (target != null) {
                val target1 = target!!
                if (target1.damage > damageWeapon) {
                    target1.damage -= damageWeapon
                } else {
                    target1.remove()
                }

                Tmp.v1.trns(mount.rotation, shootLength)

                Fx.pointHit.at(target1.x, target1.y, color)
                Fx.pointBeam.at(x + Tmp.v1.x, y + Tmp.v1.y, mount.rotation, color, Vec2().set(target))

            }
        }

    }

    override fun bullet(
        unit: Unit, mount: WeaponMount, xOffset: Float, yOffset: Float, angleOffset: Float, mover: Mover
    ) {
    }

}