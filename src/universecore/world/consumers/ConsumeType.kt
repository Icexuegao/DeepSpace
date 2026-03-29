package universecore.world.consumers

import arc.struct.Seq
import mindustry.ctype.ContentType
import universecore.world.consumers.cons.ConsumeEnergy
import universecore.world.consumers.cons.ConsumeFloor
import universecore.world.consumers.cons.ConsumeMedium
import universecore.world.consumers.cons.item.ConsumeItemBase
import universecore.world.consumers.cons.liquid.ConsumeLiquidBase
import universecore.world.consumers.cons.ConsumePayload
import universecore.world.consumers.cons.ConsumePower

open class ConsumeType<T : BaseConsume<*>>(private val type: Class<T>, cType: ContentType?) {
  companion object {
    private val allType: Seq<ConsumeType<*>> = Seq()

    fun all(): Array<ConsumeType<*>> = allType.toArray(ConsumeType::class.java)

    fun <Type : BaseConsume<*>> add(type: Class<Type>, cType: ContentType?): ConsumeType<out Type> {
      return ConsumeType(type, cType)
    }

    val power: ConsumeType<out ConsumePower<*>> = add(ConsumePower::class.java, null)
    val item: ConsumeType<out ConsumeItemBase<*>> = add(ConsumeItemBase::class.java, ContentType.item)
    val liquid: ConsumeType<out ConsumeLiquidBase<*>> = add(ConsumeLiquidBase::class.java, ContentType.liquid)
    val payload: ConsumeType<out ConsumePayload<*>> = add(ConsumePayload::class.java, null)
    val energy: ConsumeType<out ConsumeEnergy<*>> = add(ConsumeEnergy::class.java, null)
    val medium: ConsumeType<out ConsumeMedium<*>> = add(ConsumeMedium::class.java, null)
    val floor: ConsumeType<out ConsumeFloor<*>> = add(ConsumeFloor::class.java, null)
  }

  private val id: Int = allType.size
  private val contType: ContentType? = cType

  init {
    allType.add(this)
  }

  fun getType(): Class<T> = type

  fun cType(): ContentType? = contType

  fun id(): Int = id
}
