package ice.entities.bullet

import arc.func.Cons
import ice.entities.bullet.base.BasicBulletType
import mindustry.Vars
import mindustry.content.Fx
import mindustry.entities.Damage
import mindustry.entities.Effect
import mindustry.entities.Units

import mindustry.gen.Building
import mindustry.gen.Bullet
import mindustry.gen.Unit

class EmpBulletType : BasicBulletType() {
  var radius: Float = 100f
  var timeIncrease: Float = 2.5f
  var timeDuration: Float = 60f * 10f
  var powerDamageScl: Float = 2f
  var powerSclDecrease: Float = 0.2f
  var hitPowerEffect: Effect = Fx.hitEmpSpark
  var chainEffect: Effect = Fx.chainEmp
  var applyEffect: Effect = Fx.heal
  var hitUnits: Boolean = true
  var unitDamageScl: Float = 0.7f

  override fun hit(b: Bullet, x: Float, y: Float) {
    super.hit(b, x, y)

    if (!b.absorbed) {
      Vars.indexer.allBuildings(x, y, radius, Cons { other: Building? ->
        var other = other
        if (other!!.team === b.team) {
          if (other.block.hasPower && other.block.canOverdrive && other.timeScale() < timeIncrease) {
            other.applyBoost(timeIncrease, timeDuration)
            chainEffect.at(x, y, 0f, hitColor, other)
            applyEffect.at(other, other.block.size * 7f)
          }

          if (other.block.hasPower && other.damaged()) {
            other.heal(healPercent / 100f * other.maxHealth() + healAmount)
            Fx.healBlockFull.at(other.x, other.y, other.block.size.toFloat(), hitColor, other.block)
            applyEffect.at(other, other.block.size * 7f)
          }
        } else if (other.power != null) {
          val absorber = Damage.findAbsorber(b.team, x, y, other.x, other.y)
          if (absorber != null) {
            other = absorber
          }

          if (other.power != null && other.power.graph.lastPowerProduced > 0f) {
            other.applySlowdown(powerSclDecrease, timeDuration)
            other.damage(damage * powerDamageScl)
            hitPowerEffect.at(other.x, other.y, b.angleTo(other), hitColor)
            chainEffect.at(x, y, 0f, hitColor, other)
          }
        }
      })

      if (hitUnits) {
        Units.nearbyEnemies(b.team, x, y, radius, Cons { other: Unit? ->
          if (other!!.team !== b.team && other.hittable()) {
            val absorber = Damage.findAbsorber(b.team, x, y, other.x, other.y)
            if (absorber != null) {
              return@Cons
            }

            hitPowerEffect.at(other.x, other.y, b.angleTo(other), hitColor)
            chainEffect.at(x, y, 0f, hitColor, other)
            other.damage(damage * unitDamageScl)
            other.apply(status, statusDuration)
          }
        })
      }
    }
  }
}