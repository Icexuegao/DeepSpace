package ice.library.entities.bullet

import arc.Core
import arc.Events
import arc.input.KeyCode
import arc.math.Interp
import arc.math.Mathf
import arc.scene.Element
import arc.scene.ui.layout.Collapser
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.util.Strings
import arc.util.Tmp
import ice.library.IFiles
import ice.library.content.unit.entity.base.Entity
import ice.library.meta.stat.IceStatValues.ammo
import ice.library.meta.stat.IceStatValues.ammoStat
import ice.library.meta.stat.IceStatValues.sep
import ice.library.meta.stat.IceStatValues.withTooltip
import ice.library.util.accessField
import mindustry.Vars
import mindustry.content.StatusEffects
import mindustry.ctype.UnlockableContent
import mindustry.entities.bullet.BasicBulletType
import mindustry.entities.bullet.BulletType
import mindustry.game.EventType.UnitBulletDestroyEvent
import mindustry.game.EventType.UnitDamageEvent
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.ui.Styles
import mindustry.world.blocks.defense.turrets.Turret
import mindustry.world.meta.StatUnit
import kotlin.math.max
import kotlin.math.min

open class IceBasicBulletType(
    speed: Float = 3f,
    damage: Float = 20f,
    sprite: String = "bullet",
    apply: IceBasicBulletType.() -> kotlin.Unit = {}
) : BasicBulletType() {
    companion object {
        var bulletDamageEvent: UnitDamageEvent by BulletType::class.accessField("bulletDamageEvent")
    }

    internal var drawFun: (Bullet) -> kotlin.Unit = {}
    internal var updateFun: (Bullet) -> kotlin.Unit = {}
    internal var removedFun: (Bullet) -> kotlin.Unit = {}

    init {
        shrinkInterp = Interp.one
        this.speed = speed
        this.damage = damage
        this.sprite = sprite
        apply(this)
    }

    open fun addUpdate(update: (Bullet) -> kotlin.Unit) {
        this.updateFun = update
    }

    open fun addRemoved(removed: (Bullet) -> kotlin.Unit) {
        removedFun = removed
    }

    override fun removed(b: Bullet) {
        super.removed(b)
        removedFun(b)
    }

    override fun load() {
        super.load()
        backRegion = IFiles.findIcePng("$sprite-back")
        frontRegion = IFiles.findIcePng(sprite)
    }

    override fun update(b: Bullet) {
        super.update(b)
        updateFun(b)
    }

    open fun addDraw(draw: (Bullet) -> kotlin.Unit) {
        this.drawFun = draw
    }

    override fun draw(b: Bullet) {
        super.draw(b)
        drawFun(b)
    }

    open fun setDamageStats(bt: Table, compact: Boolean, t: Any) {
        if (damage > 0 && (collides || splashDamage <= 0)) {
            if (continuousDamage() > 0) {
                bt.add(Core.bundle.format("bullet.damage",
                    continuousDamage()) + StatUnit.perSecond.localized())
            } else {
                bt.add(Core.bundle.format("bullet.damage", damage))
            }
        }
    }

    fun setStats(bt: Table, compact: Boolean, t: Any) {
        setDamageStats(bt, compact, t)

        if (buildingDamageMultiplier != 1f) {
            sep(bt, Core.bundle.format("bullet.buildingdamage",
                ammoStat((buildingDamageMultiplier * 100 - 100).toInt().toFloat())))
        }

        if (rangeChange != 0f && !compact) {
            sep(bt,
                Core.bundle.format("bullet.range", ammoStat(rangeChange / Vars.tilesize)))
        }

        if (shieldDamageMultiplier != 1f) {
            sep(bt, Core.bundle.format("bullet.shielddamage",
                ammoStat((shieldDamageMultiplier * 100 - 100).toInt().toFloat())))
        }

        if (splashDamage > 0) {
            sep(bt, Core.bundle.format("bullet.splashdamage", splashDamage.toInt(),
                Strings.fixed(splashDamageRadius / Vars.tilesize, 1)))
        }

        if (statLiquidConsumed <= 0f && !compact && !Mathf.equal(ammoMultiplier,
                1f) && displayAmmoMultiplier && (t !is Turret || t.displayAmmoMultiplier)
        ) {
            sep(bt, Core.bundle.format("bullet.multiplier", ammoMultiplier.toInt()))
        }

        if (!compact && !Mathf.equal(reloadMultiplier, 1f)) {
            val `val` = (reloadMultiplier * 100 - 100).toInt()
            sep(bt, Core.bundle.format("bullet.reload", ammoStat(`val`.toFloat())))
        }

        if (knockback > 0) {
            sep(bt, Core.bundle.format("bullet.knockback", Strings.autoFixed(knockback, 2)))
        }

        if (healPercent > 0f) {
            sep(bt,
                Core.bundle.format("bullet.healpercent", Strings.autoFixed(healPercent, 2)))
        }

        if (healAmount > 0f) {
            sep(bt,
                Core.bundle.format("bullet.healamount", Strings.autoFixed(healAmount, 2)))
        }

        if (pierce || pierceCap != -1) {
            sep(bt,
                if (pierceCap == -1) "@bullet.infinitepierce" else Core.bundle.format("bullet.pierce",
                    pierceCap))
        }

        if (incendAmount > 0) {
            sep(bt, "@bullet.incendiary")
        }

        if (homingPower > 0.01f) {
            sep(bt, "@bullet.homing")
        }

        if (lightning > 0) {
            sep(bt, Core.bundle.format("bullet.lightning", lightning,
                if (lightningDamage < 0) damage else lightningDamage))
        }

        if (pierceArmor) {
            sep(bt, "@bullet.armorpierce")
        }

        if (maxDamageFraction > 0) {
            sep(bt,
                Core.bundle.format("bullet.maxdamagefraction", (maxDamageFraction * 100).toInt()))
        }

        if (suppressionRange > 0) {
            sep(bt, Core.bundle.format("bullet.suppression",
                Strings.autoFixed(suppressionDuration / 60f, 2),
                Strings.fixed(suppressionRange / Vars.tilesize, 1)))
        }

        if (status !== StatusEffects.none) {
            sep(bt,
                (if (status.hasEmoji()) status.emoji() else "") + "[stat]" + status.localizedName + (if (status.reactive) "" else ("[lightgray] ~ [stat]" +
                        Strings.autoFixed(statusDuration / 60f, 1) + "[lightgray] " + Core.bundle.get(
                    "unit.seconds")))).with { c: Element -> withTooltip(c, status) }
        }

        if (!targetMissiles) {
            sep(bt, "@bullet.notargetsmissiles")
        }

        if (!targetBlocks) {
            sep(bt, "@bullet.notargetsbuildings")
        }

        if (intervalBullet != null) {
            bt.row()
            val ic = Table()
            ammo<UnlockableContent>(ObjectMap.of(t, intervalBullet), true).display(ic)
            val coll = Collapser(ic, true)
            coll.setDuration(0.1f)

            bt.table { it: Table ->
                it.left().defaults().left()
                it.add(Core.bundle.format("bullet.interval",
                    Strings.autoFixed(intervalBullets / bulletInterval * 60, 2)))
                it.button(Icon.downOpen, Styles.emptyi) { coll.toggle(false) }
                    .update { i -> i.style.imageUp = (if (!coll.isCollapsed) Icon.upOpen else Icon.downOpen) }
                    .size(8f).padLeft(16f).expandX()
            }
            bt.row()
            bt.add(coll)
        }
        if (fragBullet != null) {
            bt.row()
            val fc = Table()
            ammo(ObjectMap.of(KeyCode.t, fragBullet), true).display(fc)
            val coll = Collapser(fc, true)
            coll.setDuration(0.1f)

            bt.table { ft ->
                ft.left().defaults().left()
                ft.add(Core.bundle.format("bullet.frags", fragBullets))
                ft.button(Icon.downOpen, Styles.emptyi) { coll.toggle(false) }
                    .update { i -> i.style.imageUp = (if (!coll.isCollapsed) Icon.upOpen else Icon.downOpen) }
                    .size(8f).padLeft(16f).expandX()
            }
            bt.row()
            bt.add(coll)
        }
    }

    override fun hitEntity(b: Bullet, entity: Hitboxc, health: Float) {
        if (entity is Entity) {
            entity.hitEntity(b, health)
            return
        }
        val wasDead = entity is Unit && entity.dead
        var health = health

        if (entity is Healthc) {
            var damage = b.damage
            val shield = if (entity is Shieldc) max(entity.shield(), 0f) else 0f
            if (maxDamageFraction > 0) {
                val cap = entity.maxHealth() * maxDamageFraction + shield
                damage = min(damage, cap)
                //将生命值上限为 handlePierce 以正确处理它
                health = min(health, cap)
            } else {
                health += shield
            }
            val owner = b.owner
            if (lifesteal > 0f && owner is Healthc) {
                val result = max(min(entity.health(), damage), 0f)
                owner.heal(result * lifesteal)
            }
            if (pierceArmor) {
                entity.damagePierce(damage)
            } else {
                entity.damage(damage)
            }
        }

        if (entity is Unit) {
            Tmp.v3.set(entity).sub(b).nor().scl(knockback * 80f)
            if (impact) Tmp.v3.setAngle(b.rotation() + (if (knockback < 0) 180f else 0f))
            entity.impulse(Tmp.v3)
            entity.apply(status, statusDuration)
            Events.fire(bulletDamageEvent.set(entity, b))
        }

        if (!wasDead && entity is Unit && entity.dead) {
            Events.fire(UnitBulletDestroyEvent(entity, b))
        }

        handlePierce(b, health, entity.x(), entity.y())
    }
}