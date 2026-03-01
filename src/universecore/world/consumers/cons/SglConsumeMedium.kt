package universecore.world.consumers.cons

import arc.Core
import arc.graphics.Color
import arc.math.Mathf
import arc.scene.ui.Image
import arc.scene.ui.layout.Stack
import arc.scene.ui.layout.Table
import arc.util.Scaling
import arc.util.Strings
import ice.library.util.toStringi
import mindustry.Vars
import mindustry.core.UI
import mindustry.gen.Building
import mindustry.ui.ReqImage
import mindustry.ui.Styles
import mindustry.world.meta.StatUnit
import mindustry.world.meta.Stats
import singularity.Singularity
import singularity.world.components.MediumBuildComp
import singularity.world.meta.SglStat
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsume
import universecore.world.consumers.ConsumeType

class SglConsumeMedium<T>(var request: Float) : BaseConsume<T>() where T : Building, T : MediumBuildComp, T : ConsumerBuildComp {
  override fun type() = ConsumeType.medium

  override fun hasIcons() = true

  override fun buildIcons(table: Table) {
    val region = Singularity.getModAtlas("medium")
    val stack = Stack()
    stack.add(Table { o ->
      o.left()
      o.add(Image(region)).size(32f).scaling(Scaling.fit)
    })
    val product = request * 60

    if (product > 0) {
      stack.add(Table { t ->
        t.left().bottom()
        t.add(if (product >= 1000) UI.formatAmount(product.toLong()) else product.toStringi(1) + "").style(Styles.outlineLabel)
        t.pack()
      })
    }
    table.add(stack)
  }

  override fun display(stats: Stats) {
    stats.add(SglStat.special) { table ->
      table.row()
      table.table { t ->
        t.defaults().left().fill().padLeft(6f)
        t.add(Core.bundle.get("misc.input") + ":").left()
        val display = request * 60
        t.table { icon ->
          icon.add(object : Stack() {
            init {
              add(Image(Singularity.getModAtlas("medium")))

              if (request != 0f) {
                val t = Table().left().bottom()
                t.add(if (display > 1000) UI.formatAmount((display as Number).toLong()) else Strings.autoFixed(display, 2) + "").style(Styles.outlineLabel)
                add(t)
              }
            }
          }).size(Vars.iconMed).padRight((3 + (if (request != 0f && Strings.autoFixed(display, 2).length > 2) 8 else 0)).toFloat())
          icon.add(StatUnit.perSecond.localized()).padLeft(2f).padRight(5f).color(Color.lightGray).style(Styles.outlineLabel)
          icon.add(Core.bundle.get("misc.medium"))
        }
      }.left().padLeft(5f)
    }
  }

  override fun build(entity: T, table: Table) {
    table.add(
      ReqImage(
        Singularity.getModAtlas("medium")
      ) { entity.mediumContains > request * parent!!.delta(entity) * multiple(entity) + 0.0001f }).padRight(8f)
  }

  override fun merge(other: BaseConsume<T>) {
    if (other is SglConsumeMedium<*>) {
      request += other.request

      return
    }
    throw IllegalArgumentException("only merge consume with same type")
  }

  override fun consume(entity: T) {}

  override fun update(entity: T) {
    entity.removeMedium(request * parent!!.delta(entity) * multiple(entity))
  }

  override fun efficiency(entity: T): Float {
    return Mathf.clamp(entity.mediumContains / (request * multiple(entity)))
  }

  override fun filter() = null
}