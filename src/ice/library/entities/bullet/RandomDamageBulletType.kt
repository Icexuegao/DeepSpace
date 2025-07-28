package ice.library.entities.bullet

import arc.func.Prov
import mindustry.Vars
import mindustry.entities.bullet.BasicBulletType
import mindustry.gen.Bullet

class RandomDamageBulletType(var randomDamage: Prov<Float>) : BasicBulletType() {
    override fun buildingDamage(b: Bullet): Float {
        val f = randomDamage.get() * b.damageMultiplier() * buildingDamageMultiplier
        Vars.ui.showLabel("$f", 2f, b.x, b.y)
        return f
    }
}