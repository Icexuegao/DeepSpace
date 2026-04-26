package singularity.world.unit

import arc.Events
import arc.func.Cons
import arc.math.Mathf
import arc.struct.ObjectMap
import arc.util.Log
import arc.util.io.Reads
import arc.util.io.Writes
import arc.util.pooling.Pools
import arc.util.serialization.Jval
import ice.content.IStatus.电磁损毁
import mindustry.Vars
import mindustry.ctype.ContentType
import mindustry.game.EventType
import mindustry.gen.Groups
import mindustry.gen.Unit
import mindustry.io.SaveFileReader.CustomChunk
import mindustry.io.SaveVersion
import mindustry.mod.Mods.LoadedMod
import mindustry.type.UnitType
import mindustry.world.meta.StatUnit
import singularity.Sgl
import singularity.core.ModsInteropAPI
import singularity.core.ModsInteropAPI.ConfigModel
import singularity.world.meta.SglStat
import singularity.world.unit.EMPModel.EMPHealth
import java.io.DataInput
import java.io.DataOutput
import kotlin.math.min

class EMPHealthManager {
  private val healthMap = ObjectMap<Unit, EMPHealth>()
  private val unitDefaultHealthMap = ObjectMap<UnitType, EMPModel>()

  private var lastGetter: Unit? = null
  private var lastGetted: EMPHealth? = null

  fun init() {
    /*添加mod交互API模型，用于其他mod定义单位的EMP生命模型
     * 通常条目格式:
     * ...
     * "empHealthModels": {
     *   “$unitTypeName”:{  //选中单位的内部名称，mod名称前缀可选，默认选择本mod中的content，一般不建议跨mod配置单位数据
     *     "maxEmpHealth": #, //最大EMP生命值
     *     "empArmor": #,  //EMP伤害减免百分比
     *     "empRepair": #,  //EMP损伤自动恢复速度（/tick）
     *     "empContinuousDamage": #  //电磁损毁状态下的持续生命扣除速度（/tick）
     *   },
     *   “$unitTypeName”:{
     *     "maxEmpHealth": #,
     *     "empArmor": #,
     *     "empRepair": #,
     *     "empContinuousDamage": #
     *   },
     *   ...
     * }
     * ...
     * 若需要禁用某一单位的EMP损伤机制则作如下声明:
     * ...
     * "empHealthModels": {
     *   "$unitTypeName": {
     *     "disabled": true
     *   },
     *   ...
     * }
     * ...
     * */
    if (Sgl.config.interopAssignEmpModels) {
      Sgl.interopAPI.addModel(object :ConfigModel("empHealthModels") {
        override fun parse(mod: LoadedMod, declaring: Jval) {
          val declares = declaring.asObject()

          for(entry in declares) {
            val unit = ModsInteropAPI.selectContent<UnitType>(ContentType.unit, entry.key, mod, true)

            val model = EMPModel()
            model.disabled = entry.value.getBool("disabled", false)

            if (!model.disabled) {
              model.maxEmpHealth = entry.value.getFloat("maxEmpHealth", unit.health / Mathf.pow(unit.hitSize - unit.armor, 2f) * 200)
              model.empArmor = entry.value.getFloat("empArmor", Mathf.clamp(unit.armor / 100))
              model.empRepair = entry.value.getFloat("empRepair", unit.hitSize / 60)
              model.empContinuousDamage = entry.value.getFloat("empContinuousDamage", unit.hitSize / 30)

              unit.stats.add(SglStat.empHealth, model.maxEmpHealth)
              unit.stats.add(SglStat.empArmor, model.empArmor * 100, StatUnit.percent)
              unit.stats.add(SglStat.empRepair, model.empRepair * 60, StatUnit.perSecond)
            }

            unitDefaultHealthMap.put(unit, model)
          }
        }

        override fun disable(mod: LoadedMod) {
          for(unit in Vars.content.units()) {
            if (unit.minfo.mod === mod) {
              val model = EMPModel()
              model.disabled = true

              unitDefaultHealthMap.put(unit, model)
            }
          }
        }
      }, false)
    }

    Events.run(EventType.Trigger.update) {
      Groups.unit.each(Cons { u: Unit ->
        if (Vars.state.isGame) {
          if (!healthMap.containsKey(u)) healthMap.put(u, getInst(u))
        }
      })
      healthMap.each { unit: Unit?, health: EMPHealth ->
        if (Vars.state.isGame) health.update()
        if (unit != null && !unit.isAdded) {
          val h = healthMap.remove(unit)
          if (h != null) Pools.free(h)
        }
      }
    }

    SaveVersion.addCustomChunk("empHealth", object :CustomChunk {
      override fun shouldWrite() = true

      override fun write(stream: DataOutput?) {
        val write = Writes(stream)
        write.i(healthMap.size)
        for(entry in healthMap) {
          write.f(entry.key!!.x)
          write.f(entry.key!!.y)
          write.i(entry.key!!.type.id.toInt())
          write.f(entry.value!!.empHealth)
        }
      }

      override fun read(stream: DataInput?) {
        Reads(stream).use { read ->
          val len = read.i()
          healthMap.clear()
          for(i in 0..<len) {
            val x = read.f()
            val y = read.f()
            val id = read.i().toFloat()

            val health = read.f()

            var unit: Unit? = null
            for(u in Groups.unit) {
              if (u.type.id.toFloat() != id || !Mathf.equal(u.x, x) || !Mathf.equal(u.y, y)) continue
              unit = u
              break
            }

            if (unit == null) {
              Log.err("emp index unit not found in ($x, $y)")
              continue
            }

            val heal = getInst(unit)
            heal.empHealth = health
            healthMap.put(heal.unit, heal)
          }
        }
      }
    })

    for(unit in Vars.content.units()) {
      getModel(unit)
    }
  }

  fun setEmpModel(type: UnitType, maxHealth: Float, armor: Float, repair: Float, empContDam: Float) {
    type.immunities.remove(电磁损毁)
    unitDefaultHealthMap.put(type, object :EMPModel() {
      init {
        this.maxEmpHealth = maxHealth
        this.empArmor = armor
        this.empRepair = repair
        this.empContinuousDamage = empContDam
      }
    })
  }

  fun setEmpDisabled(type: UnitType?) {
    unitDefaultHealthMap.put(type, object :EMPModel() {
      init {
        disabled = true
      }
    })
  }

  fun getModel(type: UnitType): EMPModel {
    return unitDefaultHealthMap.get(type) {
      type.immunities.remove(电磁损毁)
      val res = EMPModel()
      res.maxEmpHealth = type.health / Mathf.pow(type.hitSize - type.armor, 2f) * 200
      res.empArmor = Mathf.clamp(type.armor / 100, 0f, 0.9f)
      res.empRepair = type.hitSize / 60
      res.empContinuousDamage = res.empRepair * 2

      type.stats.add(SglStat.empHealth, res.maxEmpHealth)
      type.stats.add(SglStat.empArmor, res.empArmor * 100, StatUnit.percent)
      type.stats.add(SglStat.empRepair, res.empRepair * 60, StatUnit.perSecond)
      res
    }
  }

  fun getInst(unit: Unit): EMPHealth {
    return getModel(unit.type).generate(unit)
  }

  fun zeroInst(unit: Unit): EMPHealth {
    ZERO.model = getModel(unit.type)
    ZERO.unit = unit
    ZERO.empHealth = 0f
    return ZERO
  }

  fun get(unit: Unit): EMPHealth {
    if (!unit.isAdded) return zeroInst(unit)
    if (lastGetted != null && unit === lastGetter && lastGetted!!.bind) return lastGetted!!

    return healthMap.get(unit.also { lastGetter = it }) { getInst(unit) }.also { lastGetted = it }!!
  }

  fun empDamaged(unit: Unit): Boolean {
    val health = get(unit)
    val model = health.model

    if (model!!.disabled) return false

    return health.empHealth < model.maxEmpHealth
  }

  fun getHealth(unit: Unit): Float {
    if (get(unit).model!!.disabled) return 1f
    return get(unit).empHealth
  }

  fun healthPresent(unit: Unit): Float {
    val h = get(unit)

    if (h.model!!.disabled) return 1f

    return Mathf.clamp(h.empHealth / h.model!!.maxEmpHealth)
  }

  fun empDamage(unit: Unit, damage: Float, realDam: Boolean): Float {
    val h = get(unit)

    if (h.model!!.disabled) return 0f

    val real = Mathf.maxZero(if (realDam) damage else damage - damage * h.model!!.empArmor)
    val orig = h.empHealth
    h.empHealth = Mathf.maxZero(h.empHealth - real)

    return orig - h.empHealth
  }

  fun heal(unit: Unit, heal: Float) {
    val h = get(unit)

    if (h.model!!.disabled) return

    h.empHealth = min(h.empHealth + heal, h.model!!.maxEmpHealth)
  }

  companion object {
    private val ZERO = EMPHealth()
  }
}