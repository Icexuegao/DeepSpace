package ice.library.entities.bullet

import arc.Core
import arc.scene.ui.layout.Table
import ice.library.meta.IceEffects
import ice.library.meta.stat.IceStats
import mindustry.entities.Mover
import mindustry.game.Team
import mindustry.gen.Bullet
import mindustry.gen.Entityc
import mindustry.gen.Teamc
import mindustry.world.meta.StatUnit

class RandomDamageBulletType(
    var min: Int, var max: Int, speed: Float, apply: RandomDamageBulletType.() -> kotlin.Unit = {}
) : IceBasicBulletType(speed = speed) {
    init {
        apply(this)
    }

    override fun init() {
        super.init()
        damage = (min + max) / 2f
    }

    override fun setDamageStats(bt: Table, compact: Boolean, t: Any) {
        if (damage > 0 && (collides || splashDamage <= 0)) {
            if (continuousDamage() > 0) {
                bt.add(Core.bundle.format("bullet.damage", continuousDamage()) + StatUnit.perSecond.localized())
            } else {
                bt.add("[stat]$min ~ $max[lightgray] ${IceStats.伤害.localizedName}")
            }
        }
    }

    override fun create(
        owner: Entityc?,
        shooter: Entityc?,
        team: Team?,
        x: Float,
        y: Float,
        angle: Float,
        damage: Float,
        velocityScl: Float,
        lifetimeScl: Float,
        data: Any?,
        mover: Mover?,
        aimX: Float,
        aimY: Float,
        target: Teamc?
    ): Bullet {
        val create = super.create(owner, shooter, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover,
            aimX, aimY, target)
        create.damage(IceEffects.rand.random(min, max).toFloat())
        return create
    }
}