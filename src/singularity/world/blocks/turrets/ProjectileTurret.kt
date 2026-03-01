package singularity.world.blocks.turrets

import arc.func.Cons
import arc.func.Cons2
import arc.func.Func
import arc.func.Prov
import arc.graphics.Color
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import mindustry.entities.bullet.BulletType
import mindustry.graphics.Pal
import mindustry.ui.Bar
import mindustry.world.meta.StatUnit
import mindustry.world.meta.Stats
import singularity.world.meta.SglStat
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.util.Empties
import universecore.world.consumers.BaseConsumers

open class ProjectileTurret(name: String) : SglTurret(name) {
  var coatings: ObjectMap<BaseConsumers, CoatingModel> = ObjectMap<BaseConsumers, CoatingModel>()
  private val realAmmos = ObjectMap<BulletType, ObjectMap<BaseConsumers, BulletType>>()
  var maxBufferCoatings: Int = 10

  init {
    buildType = Prov(::ProjectileTurretBuild)
  }

  override fun init() {
    super.init()
    for (type in ammoTypes) {
      for (coating in coatings) {
        realAmmos.get(type.value!!.bulletType) { ObjectMap() }!!.put(coating.key, coating.value!!.coatingFunc!!.get(type.value!!.bulletType))
      }
    }
  }

  override fun setStats() {
    super.setStats()
    stats.add(SglStat.maxCoatingBuffer, maxBufferCoatings.toFloat())
  }

  override fun setBars() {
    super.setBars()
    addBar<ProjectileTurretBuild?>("coatings") { e: ProjectileTurretBuild? ->
      Bar({ "< " + (if (e!!.coatCursor <= 0) "EMPTY" else coatings.get(e.currentAmmoCons[e.coatCursor - 1])!!.name) + " >" }, { if (e!!.coatCursor <= 0) Pal.bar else coatings.get(e.currentAmmoCons[e.coatCursor - 1])!!.color }, { e!!.coatCursor.toFloat() / maxBufferCoatings })
    }
  }

  @JvmOverloads
  fun newAmmoCoating(name: String, color: Color, ammoType: Func<BulletType, BulletType>, display: Cons<Table>, amount: Int = 1) {
    consume = object : BaseConsumers(true) {
      init {
        showTime = false
      }

      override fun time(time: Float): BaseConsumers? {
        showTime = false
        craftTime = time
        return this
      }
    }
    consume!!.optionalDef = Cons2 { e: ConsumerBuildComp?, c: BaseConsumers? -> }
    consume!!.display = Cons2 { s: Stats, c: BaseConsumers ->
      s.add(SglStat.bulletCoating) { t: Table? ->
        t!!.row()
        t.add("< $name >").color(Pal.accent).left().padLeft(15f)
        t.row()
        t.table { ta: Table? ->
          ta!!.defaults().left().padLeft(15f).padTop(4f)
          display.get(ta)
        }
      }
      s.add(SglStat.coatingTime, c!!.craftTime / 60, StatUnit.seconds)
    }
    optionalCons.add(consume)
    val cons: BaseConsumers? = consume
    consume!!.setConsTrigger { e: ProjectileTurretBuild ->
      for (i in 0..<amount) {
        e.applyShootType(cons)
      }
    }
    consume!!.consValidCondition { e: ProjectileTurretBuild -> e.coatCursor + amount <= maxBufferCoatings }
    val model = CoatingModel()
    model.name = name
    model.color = color
    model.coatingFunc = ammoType

    coatings.put(consume, model)
  }

  inner class ProjectileTurretBuild : SglTurretBuild() {
    var currentAmmoCons: Array<BaseConsumers?> = arrayOfNulls(maxBufferCoatings)
    var coatCursor: Int = 0

    fun applyShootType(type: BaseConsumers?) {
      currentAmmoCons[coatCursor++] = type
    }

    override fun doShoot(type: BulletType) {
      if (coatCursor <= 0) {
        super.doShoot(type)
      } else {
        coatCursor--
        val cons = currentAmmoCons[coatCursor]
        currentAmmoCons[coatCursor] = null
        val b = realAmmos.get(type, Empties.nilMapO())!!.get(cons)

        super.doShoot(b ?: type)
      }
    }

    override fun shouldConsumeOptions(): Boolean {
      return super.shouldConsumeOptions() || coatCursor < maxBufferCoatings
    }
  }

  class CoatingModel {
    var name: String? = null
    var color: Color? = null
    var coatingFunc: Func<BulletType, BulletType>? = null
  }
}