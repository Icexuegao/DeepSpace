package universecore.world.consumers.cons

import arc.scene.ui.Image
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import arc.struct.Seq
import arc.util.Scaling
import mindustry.ctype.Content
import mindustry.ctype.UnlockableContent
import mindustry.gen.Building
import mindustry.type.PayloadStack
import mindustry.ui.ReqImage
import mindustry.ui.Styles
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValues
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ConsumerBuildComp
import universecore.world.consumers.BaseConsume
import universecore.world.consumers.ConsumeType

class ConsumePayload<T>(var payloads: Array<PayloadStack>) : BaseConsume<T>() where T : Building, T : ConsumerBuildComp {
  companion object {
    private val TMP = ObjectMap<UnlockableContent, PayloadStack>()

    fun buildPayloadIcons(table: Table, payloads: Array<PayloadStack>, displayLim: Int) {
      var count = 0
      for (stack in payloads) {
        count++
        if (displayLim in 0..count) {
          table.add("...")
          break
        }
        table.stack(
          Table { o: Table ->
            o.left()
            o.add(Image(stack.item.fullIcon)).size(32f).scaling(Scaling.fit)
          }, Table { t: Table ->
            t.left().bottom()
            t.add(stack.amount.toString() + "").style(Styles.outlineLabel)
            t.pack()
          }
        )
      }
    }
  }

  var displayLim: Int = 4

  override fun type() = ConsumeType.payload

  override fun buildIcons(table: Table) {
    buildPayloadIcons(table, payloads, displayLim)
  }

  override fun merge(other: BaseConsume<T>) {
    if (other is ConsumePayload<*>) {
      TMP.clear()
      for (stack in payloads) {
        TMP.put(stack.item, stack)
      }

      for (stack in (other as ConsumePayload<T>).payloads) {
        TMP.get(stack.item) { PayloadStack(stack.item, 0) }.amount += stack.amount
      }

      payloads = TMP.values().toSeq().sort(Comparator { a: PayloadStack, b: PayloadStack -> a.item.id - b.item.id }).toArray(PayloadStack::class.java)
    } else throw IllegalArgumentException("only merge consume with same type")
  }

  override fun efficiency(entity: T): Float {
    for (stack in payloads) {
      if (!entity.payloads.contains(stack.item, stack.amount)) {
        return 0f
      }
    }
    return 1f
  }

  override fun consume(entity: T) {
    for (stack in payloads) {
      entity.payloads.remove(stack.item, stack.amount)
    }
  }

  override fun update(entity: T) {}

  override fun display(stats: Stats) {
    for (stack in payloads) {
      stats.add(Stat.input) { t ->
        t.add(StatValues.stack(stack))
        t.add(stack.item.localizedName).padLeft(4f).padRight(4f)
      }
    }
  }

  override fun build(entity: T, table: Table) {
    val inv = entity.payloads

    table.table { c ->
      var i = 0
      for (stack in payloads) {
        c.add(
          ReqImage(
            StatValues.stack(stack)
          ) { inv.contains(stack.item, stack.amount) }).padRight(8f)
        if (++i % 4 == 0) c.row()
      }
    }.left()
  }

  override fun filter(): Seq<Content>? {
    return Seq.with(*payloads).map { s: PayloadStack -> s.item }
  }
}