package ice.world.content.unit.ability

import arc.func.Cons
import arc.math.Angles
import arc.math.Mathf
import arc.scene.ui.layout.Table
import arc.util.Strings
import arc.util.Time
import ice.library.util.MathTransform
import ice.world.SglFx
import ice.world.meta.IceStats.反射率
import ice.world.meta.IceStats.立场强度
import ice.ui.bundle.BaseBundle
import mindustry.content.Fx
import mindustry.entities.Effect
import mindustry.entities.abilities.Ability
import mindustry.gen.Bullet
import mindustry.gen.Hitboxc
import mindustry.gen.Unit
import mindustry.graphics.Pal
import mindustry.ui.Bar
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit
import kotlin.math.min
import kotlin.math.roundToInt

abstract class MirrorShieldBase : Ability(), ICollideBlockerAbility, BaseBundle.Bundle {
    var breakEffect: Effect = SglFx.mirrorShieldBreak
    var reflectEffect: Effect = Fx.none
    var refrectEffect: Effect = Fx.absorb

    var shieldArmor: Float = 0f
    var maxShield: Float = 1200f
    var cooldown: Float = 900f
    var recoverSpeed: Float = 2f
    var minAlbedo: Float = 1f
    var maxAlbedo: Float = 1f
    var strength: Float = 200f
    var refractAngleRange: Float = 30f

    protected var alpha: Float = 0f
    protected var radScl: Float = 0f

    private var lastBreak = false

    abstract fun shouldReflect(unit: Unit, bullet: Bullet): Boolean
    abstract fun eachNearBullets(unit: Unit, cons: Cons<Bullet>)
    override fun localized() = localizedName
    override fun addStats(t: Table) {
        t.row()
        t.add("[lightgray]" + Stat.health.localized() + ": [white]" + maxShield.roundToInt())
        t.row()
        t.add("[lightgray]" + Stat.armor.localized() + ": [white]" + shieldArmor.roundToInt())
        t.row()
        t.add("[lightgray]" + 立场强度.localized() + ": [white]" + strength.roundToInt())
        t.row()
        t.add("[lightgray]" + 反射率.localized() + ": [white]" + (if (Mathf.equal(minAlbedo, maxAlbedo)) Mathf.round(
            minAlbedo * 100).toString() + "%" else Mathf.round(minAlbedo * 100).toString() + "% - " + Mathf.round(
            maxAlbedo * 100) + "%"))
        t.row()
        t.add("[lightgray]" + Stat.repairSpeed.localized() + ": [white]" + Strings.autoFixed(recoverSpeed * 60f,
            2) + StatUnit.perSecond.localized())
        t.row()
        t.add("[lightgray]" + Stat.cooldownTime.localized() + ": [white]" + Strings.autoFixed(
            cooldown / recoverSpeed / 60, 2) + " " + StatUnit.seconds.localized())
        t.row()
    }

    override fun displayBars(unit: Unit, bars: Table) {
        bars.add<Bar?>(Bar({ "${localizedName}: " + Mathf.round(Mathf.maxZero(unit.shield)) }, { Pal.accent },
            { unit.shield / maxShield })).row()
    }

    override fun blockedCollides(unit: Unit, other: Hitboxc): Boolean {
        if (other !is Bullet) return true

        val blocked =
            unit.shield > 0 && other.type.reflectable && !other.hasCollided(unit.id) && shouldReflect(unit, other)

        if (blocked) doCollide(unit, other)

        return blocked
    }

    override fun update(unit: Unit) {
        alpha = Mathf.lerpDelta(alpha, 0f, 0.06f)

        lastBreak = false
        if (unit.shield > 0) {
            radScl = Mathf.lerpDelta(radScl, 1f, 0.06f)

            eachNearBullets(unit) { bullet: Bullet? ->
                if (!bullet!!.type.reflectable || bullet.hasCollided(unit.id) || !shouldReflect(unit,
                        bullet)
                ) return@eachNearBullets
                doCollide(unit, bullet)
            }
        } else {
            radScl = 0f
        }

        if (!lastBreak && unit.shield < maxShield) unit.shield =
            min(maxShield, unit.shield + recoverSpeed * Time.delta * unit.reloadMultiplier)
    }

    fun doCollide(unit: Unit, bullet: Bullet) {
        if (bullet.damage < min(strength, unit.shield)) {
            doReflect(unit, bullet)
        } else {
            alpha = 1f
            bullet.collided.add(unit.id)
            damageShield(unit, bullet.damage())

            bullet.damage -= min(strength, unit.shield)
            val rot: Float
            bullet.rotation((bullet.rotation() + Mathf.range(refractAngleRange)).also { rot = it })
            refrectEffect.at(bullet.x, bullet.y, rot, bullet.type.hitColor)
        }
    }

    fun damageShield(unit: Unit, damage: Float) {
        if (unit.shield <= damage - shieldArmor) {
            unit.shield = -cooldown

            breakEffect.at(unit.x, unit.y, unit.rotation(), unit.team.color, this)
            lastBreak = true
        } else unit.shield -= Mathf.maxZero(damage - shieldArmor)
    }

    fun doReflect(unit: Unit, bullet: Bullet) {
        alpha = 1f

        val albedo = if (Mathf.equal(minAlbedo, maxAlbedo)) minAlbedo else Mathf.random(minAlbedo, maxAlbedo)

        damageShield(unit, bullet.damage() * albedo)

        val baseAngel = Angles.angle(bullet.x - unit.x, bullet.y - unit.y)
        val diffAngel = MathTransform.innerAngle(bullet.rotation(), baseAngel)

        val reflectAngel = baseAngel + 180 + diffAngel
        if (albedo >= 0.99f) {
            bullet.team = unit.team
            bullet.rotation(reflectAngel)
            bullet.collided.add(unit.id)
        } else {
            bullet.type.create(unit, unit.team, bullet.x, bullet.y, reflectAngel, bullet.damage * albedo, 1f,
                (if (bullet.type.splashDamage > bullet.damage) albedo else 1f) * (1 - bullet.time / bullet.lifetime),
                bullet.data, bullet.mover, unit.aimX, unit.aimY)

            val rot: Float
            bullet.rotation((bullet.rotation() + Mathf.range(refractAngleRange)).also { rot = it })
            bullet.damage *= 1 - albedo
            bullet.collided.add(unit.id)

            refrectEffect.at(bullet.x, bullet.y, rot, bullet.type.hitColor)

            reflectEffect.at(bullet.x, bullet.y, baseAngel, bullet.type.hitColor)
        }
    }
}
