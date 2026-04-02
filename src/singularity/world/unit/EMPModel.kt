package singularity.world.unit

import arc.util.Time
import arc.util.pooling.Pool.Poolable
import arc.util.pooling.Pools
import ice.content.IStatus.电磁损毁
import mindustry.content.Fx
import mindustry.gen.Unit

open class EMPModel {
  var maxEmpHealth: Float = 0f
  var empArmor: Float = 0f
  var empRepair: Float = 0f
  var empContinuousDamage: Float = 0f

  var disabled: Boolean = false

  fun generate(unit: Unit): EMPHealth {
    val res = Pools.obtain(EMPHealth::class.java, ::EMPHealth)
    res.model = this
    res.empHealth = maxEmpHealth
    res.unit = unit
    res.bind = true

    return res
  }

  class EMPHealth : Poolable {
    /** 始终不应该为Null */
    var model: EMPModel? = null
    var empHealth: Float = 0f
    /** 始终不应该为Null */
    var unit: Unit? = null
    var bind: Boolean = false

    override fun reset() {
      model = null
      bind = false
      empHealth = 0f
    }

    fun update() {
      if (model!!.disabled) return
      if (!unit!!.hasEffect(电磁损毁)) {
        if (empHealth <= 0) {
          unit!!.shield = 0f
          Fx.unitShieldBreak.at(unit!!.x, unit!!.y, 0.0f, unit!!.team.color, unit)
          unit!!.apply(电磁损毁, 660f)
        } else if (empHealth < model!!.maxEmpHealth) {
          empHealth += model!!.empRepair * Time.delta
        }
      }
    }
  }
}