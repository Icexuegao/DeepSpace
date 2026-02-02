package universecore.world.producers

import arc.Events
import arc.func.Boolf2
import arc.func.Func2
import arc.scene.ui.layout.Table
import arc.struct.ObjectMap
import mindustry.ctype.UnlockableContent
import mindustry.game.EventType.UnitCreateEvent
import mindustry.gen.Building
import mindustry.type.PayloadStack
import mindustry.type.UnitType
import mindustry.world.Block
import mindustry.world.blocks.payloads.BuildPayload
import mindustry.world.blocks.payloads.Payload
import mindustry.world.blocks.payloads.UnitPayload
import mindustry.world.meta.Stat
import mindustry.world.meta.StatValues
import mindustry.world.meta.Stats
import universecore.components.blockcomp.ProducerBuildComp
import universecore.world.consumers.ConsumePayload

class ProducePayload<T>(var payloads: Array<PayloadStack>, var valid: Boolf2<T, UnlockableContent>) : BaseProduce<T>() where T : Building, T : ProducerBuildComp {
  var displayLim: Int = 4
  var payloadMaker: Func2<T, UnlockableContent, Payload> = Func2 { ent: T, type: UnlockableContent -> this.makePayloadDef(ent, type) }

  companion object {
    private val TMP = ObjectMap<UnlockableContent, PayloadStack>()
  }

  private fun makePayloadDef(ent: T, type: UnlockableContent?): Payload {
    if (type is UnitType) {
      val unit = type.create(ent.team)
      Events.fire(UnitCreateEvent(unit, ent))
      return UnitPayload(unit)
    } else if (type is Block) {
      return BuildPayload(type, ent.team)
    }
    throw IllegalArgumentException("default payload maker can only make 'Building' and 'Unit', if you want to make other things, please use custom payload maker to field 'payloadMaker'")
  }

  override fun type(): ProduceType<*> {
    return ProduceType.payload
  }

  override fun buildIcons(table: Table) {
    ConsumePayload.buildPayloadIcons(table, payloads, displayLim)
  }

  override fun merge(other: BaseProduce<T>) {
    if (other is ProducePayload<*>) {
      TMP.clear()
      for (stack in payloads) {
        TMP.put(stack.item, stack)
      }

      for (stack in (other as ProducePayload<T>).payloads) {
        TMP.get(stack.item) { PayloadStack(stack.item, 0) }!!.amount += stack.amount
      }

      payloads = TMP.values().toSeq().sort(Comparator { a: PayloadStack?, b: PayloadStack? -> a!!.item.id - b!!.item.id }).toArray(PayloadStack::class.java)
    } else throw IllegalArgumentException("only merge consume with same type")
  }


  override fun produce(entity: T) {
    for (stack in payloads) {
      for (i in 0..<stack.amount) {
        val payload = payloadMaker.get(entity, stack.item)
        payload.set(entity.x, entity.y, entity.rotdeg())
        if (entity.acceptPayload(entity, payload)) entity.handlePayload(entity, payload)
      }
    }
  }

  override fun valid(entity: T): Boolean {
    for (stack in payloads) {
      if (!valid.get(entity, stack.item)) return false
    }
    return true
  }

  override fun update(entity: T) {}

  override fun display(stats: Stats) {
    for (stack in payloads) {
      stats.add(Stat.output) { t: Table? ->
        t!!.add(StatValues.stack(stack))
        t.add(stack.item.localizedName).padLeft(4f).padRight(4f)
      }
    }
  }
}