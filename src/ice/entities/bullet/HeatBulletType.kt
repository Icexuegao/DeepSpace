package ice.entities.bullet

import arc.func.Cons
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Lines
import arc.math.Mathf
import arc.util.Time
import ice.entities.bullet.base.BulletType
import mindustry.content.Fx
import mindustry.content.StatusEffects
import mindustry.entities.Units
import mindustry.entities.units.StatusEntry
import mindustry.gen.Bullet
import mindustry.gen.Hitboxc
import mindustry.gen.Unit
import mindustry.graphics.Pal
import mindustry.type.StatusEffect
import singularity.graphic.SglDraw
import kotlin.math.min

open class HeatBulletType : BulletType() {
    var melDamageScl: Float = 0.4f
    var maxExDamage: Float = -1f
    var meltDownTime: Float = 10f

    var meltdown: StatusEffect = object : StatusEffect("meltdown") {
        init {
            damage = 2.2f
            effect = Fx.melting

            init {
                opposite(StatusEffects.freezing, StatusEffects.wet)
                affinity(StatusEffects.tarred) { unit, result, time: Float ->
                    unit.damagePierce(8f)
                    Fx.burning.at(unit.x + Mathf.range(unit.bounds() / 2f), unit.y + Mathf.range(unit.bounds() / 2f))
                    result.set(meltdown, 180 + result.time)
                }
            }
        }

        override fun update(unit: Unit, entry: StatusEntry) {
            super.update(unit, entry)
            if (unit.shield > 0) {
                unit.shieldAlpha = 1f
                unit.shield -= Time.delta * entry.time / 6
            }
        }

        override fun draw(unit: Unit, time: Float) {
            super.draw(unit, time)

            SglDraw.drawBloomUponFlyUnit<Unit>(unit, SglDraw.DrawAcceptor { u: Unit ->
                val rate = Mathf.clamp(90 / (time / 30))
                Lines.stroke(2.2f * rate, Pal.lighterOrange)
                Draw.alpha(rate * 0.7f)
                Lines.circle(u.x, u.y, u.hitSize / 2 + rate * u.hitSize / 2)

                Fx.rand.setSeed(unit.id.toLong())

                (0..7).forEach { i ->
                    SglDraw.drawTransform(u.x, u.y, u.hitSize / 2 + rate * u.hitSize / 2, 0f, Time.time + Fx.rand.random(360f)) { x: Float, y: Float, r: Float ->
                        val len = Fx.rand.random(u.hitSize / 4, u.hitSize / 1.5f)
                        SglDraw.drawDiamond(x, y, len, len * 0.135f, r)
                    }
                }
                Draw.reset()
            })
        }
    }

    override fun hitEntity(b: Bullet?, entity: Hitboxc?, health: Float) {
        super.hitEntity(b, entity, health)
        if (entity is Unit) {
            val mel = entity.getDuration(meltdown)

            entity.damage(min(mel * melDamageScl, if (maxExDamage < 0) damage else maxExDamage))

            entity.apply(meltdown, mel + meltDownTime)
        }
    }

    override fun createSplashDamage(b: Bullet, x: Float, y: Float) {
        super.createSplashDamage(b, x, y)
        Units.nearbyEnemies(b.team, x, y, splashDamageRadius, Cons { u: Unit? ->
            val mel = u!!.getDuration(meltdown)
            u.damage(min(mel * melDamageScl, if (maxExDamage < 0) splashDamage else maxExDamage))
            u.apply(meltdown, mel + meltDownTime)
        })
    }
}


