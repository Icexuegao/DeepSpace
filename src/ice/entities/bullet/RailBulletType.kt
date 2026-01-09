package ice.entities.bullet

import arc.math.geom.Vec2
import arc.util.Tmp
import ice.entities.bullet.base.BulletType
import mindustry.content.Fx
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.gen.Building
import mindustry.gen.Bullet
import kotlin.math.max
import kotlin.math.min


class RailBulletType : BulletType() {
    var pierceEffect: Effect = Fx.hitBulletSmall
    var pointEffect: Effect = Fx.none
    var lineEffect: Effect = Fx.none
    var endEffect: Effect = Fx.none
    var length: Float = 100f
    var pointEffectSpace: Float = 20f

    init {
        speed = 0f
        pierceBuilding = true
        pierce = true
        reflectable = false
        hitEffect = Fx.none
        despawnEffect = Fx.none
        collides = false
        keepVelocity = false
        lifetime = 1f
        delayFrags = true
    }

    override fun calculateRange(): Float {
        return length
    }

    override fun handlePierce(b: Bullet, initialHealth: Float, x: Float, y: Float) {
        val sub = max(initialHealth * pierceDamageFactor, 0f)

        if (b.damage <= 0) {
            b.fdata = min(b.fdata, b.dst(x, y))
            return
        }

        if (b.damage > 0) {
            pierceEffect.at(x, y, b.rotation())
        }
        //subtract health from each consecutive pierce
        b.damage -= min(b.damage, sub)
        //bullet was stopped, decrease furthest distance
        if (b.damage <= 0f) {
            b.fdata = min(b.fdata, b.dst(x, y))
        }
    }

    override fun init(b: Bullet) {
        super.init(b)

        b.fdata = length
        Damage.collideLine(b, b.team, b.x, b.y, b.rotation(), length, false, false, pierceCap)
        val resultLen = b.fdata
        val nor = Tmp.v1.trns(b.rotation(), 1f).nor()
        if (pointEffect !== Fx.none) {
            var i = 0f
            while (i <= resultLen) {
                pointEffect.at(b.x + nor.x * i, b.y + nor.y * i, b.rotation(), trailColor)
                i += pointEffectSpace
            }
        }
        val any = b.collided.size > 0

        if (!any && endEffect !== Fx.none) {
            endEffect.at(b.x + nor.x * resultLen, b.y + nor.y * resultLen, b.rotation(), hitColor)
        }

        if (lineEffect !== Fx.none) {
            lineEffect.at(b.x, b.y, b.rotation(), hitColor, Vec2(b.x, b.y).mulAdd(nor, resultLen))
        }
    }

    override fun testCollision(bullet: Bullet, tile: Building): Boolean {
        return bullet.team !== tile.team
    }

    override fun hitTile(b: Bullet, build: Building?, x: Float, y: Float, initialHealth: Float, direct: Boolean) {
        handlePierce(b, initialHealth, x, y)
    }
}